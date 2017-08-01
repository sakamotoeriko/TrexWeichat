package com.trex.trchat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;

import com.trex.trchat.R;
import com.trex.trchat.lib.trexsdk.TrChatCoreSdk;
import com.trex.trchat.lib.trexsdk.callbacks.TrChatBaseEvent;
import com.trex.trchat.trexbusiness.TrChatController;
import com.trex.trchat.ui.video.VideoSession;
import com.trex.trchat.videocall.VideoCallController;
import com.trex.trchat.videocall.model.ConnectSession;
import com.trex.trchat.videocall.model.RoomInfo;
import com.trex.trchat.videocall.model.UserInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoActivity extends Activity implements TrChatBaseEvent, VideoCallController.VideoCallListener {
    public static final String TAG = VideoActivity.class.getSimpleName();

    public static final String INTENT_EXTRA_SESSION = "INTENT_EXTRA_SESSION";

    private SurfaceView mSurfaceMain;
    private SurfaceView mSurfaceUsr1;
    private SurfaceView mSurfaceUsr2;
    private SurfaceView mSurfaceUsr3;


    List<VideoSession> mVideoSessions = Collections.synchronizedList(new ArrayList<VideoSession>());

    private TrChatCoreSdk mTrChatCoreSdk;
    private VideoCallController mVideoCallController;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initTrChatSdk();

        Intent intent = getIntent();
        mVideoCallController = TrChatController.getInstance(getApplication()).getVideoCallController();
        mVideoCallController.setListener(this);
        ConnectSession session;
        if (intent.hasExtra(INTENT_EXTRA_SESSION)) {
            session = (ConnectSession) intent.getSerializableExtra(INTENT_EXTRA_SESSION);
            Log.d(TAG,"ConnectSession:"+session.toString());
            mTrChatCoreSdk.enterRoom(session.getRoomInfo().getId(), session.getRoomInfo().getPwd());
        } else {
            finish();
            return;
        }

        initView();
        initVideoSession();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        for (VideoSession session : mVideoSessions) {
            session.stop();
        }
        RoomInfo info = mVideoCallController.getRoomInfo();
        if (info != null)
            mTrChatCoreSdk.leaveRoom(info.getId());
        TrChatController.getInstance(getApplication()).getVideoCallController().clearSession();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.endcall:
                //TODO

                for (VideoSession session : mVideoSessions) {
                    session.stop();
                }
                mVideoSessions.clear();
                finish();
                break;
        }
    }

    private void initTrChatSdk() {
        mTrChatCoreSdk = TrChatCoreSdk.getInstance();
        mTrChatCoreSdk.setBaseEvent(this);
        mTrChatCoreSdk.mSensorHelper.InitSensor(getApplicationContext());
        // 初始化Camera上下文句柄
        mTrChatCoreSdk.mCameraHelper.SetContext(getApplicationContext());
    }

    private void initView() {

        mSurfaceMain = (SurfaceView) findViewById(R.id.surface_main);
        mSurfaceUsr1 = (SurfaceView) findViewById(R.id.surface_user1);
        mSurfaceUsr2 = (SurfaceView) findViewById(R.id.surface_user2);
        mSurfaceUsr3 = (SurfaceView) findViewById(R.id.surface_user3);

        mSurfaceMain.setVisibility(View.VISIBLE);
        mSurfaceUsr1.setVisibility(View.VISIBLE);
        mSurfaceUsr2.setVisibility(View.VISIBLE);
        mSurfaceUsr3.setVisibility(View.VISIBLE);

        mSurfaceMain.setTag(R.string.index, "mSurfaceMain");
        mSurfaceUsr1.setTag(R.string.index, "mSurfaceUsr1");
        mSurfaceUsr2.setTag(R.string.index, "mSurfaceUsr2");
        mSurfaceUsr3.setTag(R.string.index, "mSurfaceUsr3");

    }

    private void initVideoSession() {
        ArrayList<ConnectSession> sessions = mVideoCallController.getSessionList();
        if (sessions.isEmpty()) {
            Log.e(TAG, "initVideoSession linkedSession==null");
            finish();
            return;
        }

        VideoSession selfsession = new VideoSession(mSurfaceUsr1, -1, false);
        mVideoSessions.add(selfsession);

        SurfaceView[] sv = {mSurfaceMain, mSurfaceUsr2, mSurfaceUsr3};


        for (int i = 0; i < sessions.size(); i++) {
            int tagId = sessions.get(i).getTargetUserId();
            Log.d(TAG, "initVideoSession i:" + i + " tagId:" + tagId);
            if (i > 2)
                return;
            VideoSession ts = new VideoSession(sv[i], tagId, i == 0);
            mVideoSessions.add(ts);
        }

        for (final VideoSession s : mVideoSessions) {
            s.init();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    s.start();
                }
            }, 2000);
        }
    }

    @Override
    public void OnTrChatConnectMessage(boolean bSuccess) {

    }

    @Override
    public void OnTrChatLoginMessage(int dwUserId, int dwErrorCode) {

    }

    @Override
    public void OnTrChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
        Log.d(TAG, "OnTrChatEnterRoomMessage roomid:" + dwRoomId + " errorCode:" + dwErrorCode);

        if (dwErrorCode == 0) {
            mVideoSessions.get(0).openCameraSpeaker();
            mVideoSessions.get(0).isOpened = false;
            int[] usrids = mTrChatCoreSdk.getRoomOnlineUsers(dwRoomId);
            if (usrids != null && usrids.length != 1) {
                for (int uid : usrids) {
                    if (uid == -1 || uid == TrChatController.getInstance(getApplication()).getSelfUserId()) {
                        continue;
                    }
                    userJoin(uid);
                }
            }
        }
    }

    @Override
    public void OnTrChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
        Log.d(TAG, "OnTrChatOnlineUserMessage dwUserNum:" + dwUserNum + " dwRoomId:" + dwRoomId);

        for (VideoSession s : mVideoSessions) {
            if (s.isSurfaceViewVisible()) {
                s.openCameraSpeaker();
                s.isOpened = false;
            }
        }
    }

    @Override
    public void OnTrChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
        Log.d(TAG, "OnTrChatUserAtRoomMessage dwUserId:" + dwUserId + " bEnter:" + bEnter);
        // TODO
        if (dwUserId == TrChatController.getInstance(getApplication()).getSelfUserId()) {
            Log.w(TAG, "self userid:" + dwUserId);
            return;
        }
        if (bEnter) {
            userJoin(dwUserId);
        } else {
            userLeave(dwUserId);
        }
    }

    @Override
    public void OnTrChatLinkCloseMessage(int dwErrorCode) {
        Log.d(TAG, "OnTrChatLinkCloseMessage dwErrorCode:" + dwErrorCode);
        for (VideoSession s : mVideoSessions) {
            if (s.isSurfaceViewVisible()) {
                s.closeCameraSpeaker();
                s.isOpened = false;
            }
        }
        //TODO
        if (dwErrorCode == 0) {

        } else {

        }
    }


    private synchronized void userJoin(int userid) {
        for (VideoSession s : mVideoSessions) {
            if (s.isUser(userid)) {
                if (!s.isSurfaceViewVisible()) {
                    s.setSurfaceViewVisibility(View.VISIBLE);
                }
                s.openCameraSpeaker();
                s.isOpened = false;

                return;
            }
        }

        Log.d(TAG, "userJoin");
        int size = mVideoSessions.size();
        SurfaceView surfaceView;

        switch (size) {
            case 2:
                surfaceView = mSurfaceUsr2;
                break;
            case 3:
                surfaceView = mSurfaceUsr3;
                break;
            default:
                Log.e(TAG, "userJoin session size = " + size);
                return;
        }

        surfaceView.setVisibility(View.VISIBLE);
        final VideoSession vs = new VideoSession(surfaceView, userid, false);
        vs.init();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                vs.start();
                vs.openCameraSpeaker();
                for (VideoSession s : mVideoSessions) {
                    s.isOpened = false;
                }
            }
        });

        mVideoSessions.add(vs);
    }

    private synchronized void userLeave(int userid) {

        List<VideoSession> removelist = new ArrayList<>();
        for (VideoSession s : mVideoSessions) {
            if (!s.isUser(userid)) continue;

            removelist.add(s);
//            mVideoSessions.remove(s);
            s.setSurfaceViewVisibility(View.INVISIBLE);
            if (!s.isSurfaceView(mSurfaceMain)) {
                s.stop();
//                s.setSurfaceViewVisibility(View.GONE);
            } else {
                //TODO
                finish();
            }
        }
        mVideoSessions.removeAll(removelist);
        if (mVideoSessions.size() == 1) {
            mVideoSessions.get(0).stop();
            finish();
            return;
        }
    }

    @Override
    public void onRequested(ConnectSession session) {

    }

    @Override
    public void onRecieved(ConnectSession session) {

    }

    @Override
    public void onReplay(ConnectSession session) {

    }
}
