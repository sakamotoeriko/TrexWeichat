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

    public void clearSession(){
        sessions.clear();
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
            }
        };
        mHandler.postDelayed(requestTimeoutRun, 10000);
        if (mListener != null)
            mListener.onRequested(session);
    }

    public synchronized void requestVideoCallRecall(final ConnectSession session) {
        Log.d(TAG, "requestVideoCallRecall session:" + session.toString());
        if (requestTimeoutRun != null)
            mHandler.removeCallbacks(requestTimeoutRun);

        requestVideoCall(session);
    }
    private Runnable requestTimeoutRun;


    public synchronized void revieveVideoCallReplay(ConnectSession session) {
        if (requestTimeoutRun != null)
            mHandler.removeCallbacks(requestTimeoutRun);

        if (mListener != null) {
            mListener.onReplay(session);
        }
    }

    public synchronized void recieveVideoCall(ConnectSession session) {
        Log.d(TAG, "recieveVideoCall session:" + session.toString());
        if (AppSettings.getInstance().isAutoReplay()) {
            //auto replay
            if (session.canStartNewSession(sessions)) {
                byte[] replay = VideoCallProtocol.createVideoCallReplay(false);
                mTrChatCoreSdk.transBuff(session.getTargetUserId(), replay, replay.length);
                sessions.add(session);
                if (mListener != null)
                    mListener.onRecieved(session);

            } else {
                if (sessions.isEmpty() || sessions.get(0) == null) {
                    //illegal path
                    Log.e(TAG, "recieveVideoCall illegal path");
                    return;
                }
                byte[] replay = VideoCallProtocol.createVideoCallReplay(true, sessions.get(0).getRoomInfo());
                //not in the same room,replay owned roomid and wating to another request if target can join
                mTrChatCoreSdk.transBuff(session.getTargetUserId(), replay, replay.length);
            }
        } else {
            //show replay dialog
            //TODO
        }
    }


}
