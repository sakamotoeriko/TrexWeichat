package com.trex.trchat.videocall;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.trex.trchat.configs.AppSettings;
import com.trex.trchat.lib.trexsdk.TrChatCoreSdk;
import com.trex.trchat.videocall.model.ConnectSession;
import com.trex.trchat.videocall.model.RoomInfo;
import com.trex.trchat.videocall.model.UserInfo;

import java.util.ArrayList;

public class VideoCallController {
    public static final String TAG = VideoCallController.class.getSimpleName();
    private static VideoCallController instance = new VideoCallController();

    public static VideoCallController getInstance(Context context) {
        if (instance.mContext == null)
            instance.setContext(context);
        return instance;
    }


    public interface VideoCallListener {
        void onRequested(ConnectSession session);

        void onRecieved(ConnectSession session);

        void onReplay(ConnectSession session);

        void onEndCall(ConnectSession session);
    }

    private Context mContext;
    private TrChatCoreSdk mTrChatCoreSdk;
    private VideoCallListener mListener;
    private UserInfo mSelf;
    private ArrayList<ConnectSession> sessions = new ArrayList<>();
    private Handler mHandler;

    private VideoCallController() {
        mTrChatCoreSdk = TrChatCoreSdk.getInstance();
        mHandler = new Handler();
    }

    private void setContext(Context context) {
        this.mContext = context;
    }

    public void setListener(VideoCallListener listener) {
        this.mListener = listener;
    }

    public ArrayList<ConnectSession> getSessionList() {
        return sessions;
    }

    public RoomInfo getRoomInfo() {
        if (sessions == null || sessions.isEmpty()) {
            return null;
        }
        return sessions.get(0).getRoomInfo();
    }

    public void clearSession() {
        sessions.clear();
    }

    public synchronized void receiveVideoCallEndCall(final ConnectSession session) {
        Log.d(TAG, "receiveVideoCallEndCall session:" + session.toString());

        sessions.remove(session);
        if (mListener != null)
            mListener.onEndCall(session);
    }

    public synchronized void requestVideoCallEndCall(final ConnectSession session) {
        Log.d(TAG, "requestVideoCallEndCall session:" + session.toString());

        byte[] request = VideoCallProtocol.createVideoCallEndCall(session.getRoomInfo());
        mTrChatCoreSdk.transBuff(session.getTargetUserId(), request, request.length);
        sessions.remove(session);
    }

    public synchronized void requestVideoCall(final ConnectSession session) {
        Log.d(TAG, "requestVideoCall session:" + session.toString());

        if (!session.canStartNewSession(sessions)) {
            return;
        }

        byte[] request = VideoCallProtocol.createVideoCallRequest(session.getRoomInfo());
        mTrChatCoreSdk.transBuff(session.getTargetUserId(), request, request.length);
        session.setStatus(ConnectSession.CONNECTSESSION_STATUS_CALLING);
        sessions.add(session);
        requestTimeoutRun = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "requestVideoCall timeout session:" + session.toString());
                sessions.remove(session);
                session.setStatus(ConnectSession.CONNECTSESSION_STATUS_TIMEOUT);
                if (mListener != null) {
                    mListener.onReplay(session);
                }
            }
        };
        mHandler.postDelayed(requestTimeoutRun, 30000);
        if (mListener != null)
            mListener.onRequested(session);
    }

    public synchronized void requestVideoCallRecall(final ConnectSession session) {
        Log.d(TAG, "requestVideoCallRecall session:" + session.toString());
        if (requestTimeoutRun != null)
            mHandler.removeCallbacks(requestTimeoutRun);
        sessions.remove(session);
        requestVideoCall(session);
    }

    private Runnable requestTimeoutRun;


    public synchronized void revieveVideoCallReplay(ConnectSession session) {
        if (requestTimeoutRun != null)
            mHandler.removeCallbacks(requestTimeoutRun);

        if (session.getStatus() != ConnectSession.CONNECTSESSION_STATUS_ACCEPTED) {
            sessions.remove(session);
            for (ConnectSession s : sessions) {
                Log.d(TAG, "revieveVideoCallReplay s:" + s.toString());
            }
        }
        for (ConnectSession s : sessions) {
            if (s.equals(session)) {
                s.setStatus(ConnectSession.CONNECTSESSION_STATUS_CHATTING);
            }
        }
        if (mListener != null) {
            mListener.onReplay(session);
        }
    }

    public synchronized void replayVideoCall(ConnectSession session, boolean reject) {
        byte[] replay = VideoCallProtocol.createVideoCallReplay(reject ? 1 : 0,
                session.getRoomInfo());
        mTrChatCoreSdk.transBuff(session.getTargetUserId(), replay, replay.length);
        if (!reject) sessions.add(session);
    }

    public synchronized void receiveVideoCall(ConnectSession session) {
        Log.d(TAG, "receiveVideoCall session:" + session.toString());

        if (session.canStartNewSession(sessions)) {
            // idle or in the same room
            if (mListener != null)
                mListener.onRecieved(session);

        } else {
            if (sessions.isEmpty() || sessions.get(0) == null) {
                //illegal path
                Log.e(TAG, "receiveVideoCall illegal path");
                return;
            }
            //recall
            byte[] replay = VideoCallProtocol.createVideoCallReplay(2, sessions.get(0).getRoomInfo());
            //not in the same room,replay owned roomid and wating to another request if target can join
            mTrChatCoreSdk.transBuff(session.getTargetUserId(), replay, replay.length);
        }
    }


}
