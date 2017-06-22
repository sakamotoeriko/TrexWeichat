package com.trex.trchat.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.trex.trchat.R;
import com.trex.trchat.lib.trexsdk.TrChatCoreSdk;
import com.trex.trchat.lib.trexsdk.callbacks.TrChatBaseEvent;
import com.trex.trchat.lib.trexsdk.common.TrChatSdkDefine;
import com.trex.trchat.trexbusiness.LinkedSession;
import com.trex.trchat.trexbusiness.Session;
import com.trex.trchat.trexbusiness.TrChatBusiness;
import com.trex.trchat.trexbusiness.UserItem;
import com.trex.trchat.trexbusiness.VideoCallBusiness;
import com.trex.trchat.trexbusiness.callbacks.VideoCallBusinessEvent;
import com.trex.trchat.ui.video.VideoSession;

import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends Activity implements VideoCallBusinessEvent, TrChatBaseEvent {
    public static final String TAG = VideoActivity.class.getSimpleName();

    public static final String INTENT_EXTRA_SESSION = "INTENT_EXTRA_SESSION";

    private SurfaceView mSurfaceMain;
    private SurfaceView mSurfaceUsr1;
    private SurfaceView mSurfaceUsr2;
    private SurfaceView mSurfaceUsr3;


    List<VideoSession> mVideoSessions = new ArrayList<>();

    private TrChatCoreSdk mTrChatCoreSdk;

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
        VideoCallBusiness.getInstance().setVideoCallBusinessEvent(this);
        Session session;
        if (intent.hasExtra(INTENT_EXTRA_SESSION)) {
            session = (Session) intent.getSerializableExtra(INTENT_EXTRA_SESSION);
            mTrChatCoreSdk.enterRoom(session.getRoomId(), session.getRoomPwd());
        } else {
            finish();
            return;
        }

        initView();
        initVideoSession();
    }

    @Override
    protected void onStop() {
        for (VideoSession session : mVideoSessions) {
            session.stop();
        }
        mTrChatCoreSdk.leaveRoom(VideoCallBusiness.getInstance().getRoomid());
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initTrChatSdk() {
        mTrChatCoreSdk = TrChatCoreSdk.getInstance();
        mTrChatCoreSdk.setBaseEvent(this);
        mTrChatCoreSdk.mSensorHelper.InitSensor(getApplicationContext());
        // 初始化Camera上下文句柄
        TrChatCoreSdk.mCameraHelper.SetContext(getApplicationContext());
    }

    private void initView() {

        mSurfaceMain = (SurfaceView) findViewById(R.id.surface_main);
        mSurfaceUsr1 = (SurfaceView) findViewById(R.id.surface_user1);
        mSurfaceUsr2 = (SurfaceView) findViewById(R.id.surface_user2);
        mSurfaceUsr3 = (SurfaceView) findViewById(R.id.surface_user3);

        mSurfaceMain.setVisibility(View.VISIBLE);
        mSurfaceUsr1.setVisibility(View.VISIBLE);
        mSurfaceUsr2.setVisibility(View.GONE);
        mSurfaceUsr3.setVisibility(View.GONE);

    }

    private void initVideoSession() {
        LinkedSession linkedSession = TrChatBusiness.getInstance().getLinkedSession();
        if (linkedSession == null) {
            Log.e(TAG, "initVideoSession linkedSession==null");
            finish();
            return;
        }

        VideoSession selfsession = new VideoSession(-1, mSurfaceUsr1);
        mVideoSessions.add(selfsession);

        SurfaceView[] sv = {mSurfaceMain, mSurfaceUsr2, mSurfaceUsr3};

        List<UserItem> targets = linkedSession.getTargets();
        for (int i = 0; i < targets.size(); i++) {
            UserItem item = targets.get(i);
            Log.d(TAG, "initVideoSession i:" + i + "item:" + item.toString());
            if (i > 2)
                return;
            VideoSession ts = new VideoSession(item.getUserid(), sv[0]);
            mVideoSessions.add(ts);
        }

        for (VideoSession s : mVideoSessions) {
            s.init();
            s.start();
        }
    }

    @Override
    public void onReplay(Session session) {
        int sts = session.getStatus();
        Log.d(TAG, "onReplay status:" + sts);
        if (sts == Session.SESSION_STATUS_ACCEPTED) {

        } else if (sts == Session.SESSION_STATUS_REJECTED) {
            //TODO
        } else if (sts == Session.SESSION_STATUS_REJECTED_RECALL) {
            //TODO
        }
    }

    @Override
    public void onNewSession(Session session) {

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
            mVideoSessions.get(0).openCamera();
            mVideoSessions.get(0).openSpeaker();
            mVideoSessions.get(0).isOpened = false;
        }
    }

    @Override
    public void OnTrChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
        Log.d(TAG, "OnTrChatOnlineUserMessage dwUserNum:" + dwUserNum + " dwRoomId:" + dwRoomId);

        for (VideoSession s : mVideoSessions) {
            if (s.isSurfaceViewVisible()) {
                s.openCamera();
                s.openSpeaker();
                s.isOpened = false;
            }
        }
    }

    @Override
    public void OnTrChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
        Log.d(TAG, "OnTrChatUserAtRoomMessage dwUserId:" + dwUserId + " bEnter:" + bEnter);
        // TODO
//        for (VideoSession s : mVideoSessions) {
//            if (s.isSurfaceViewVisible()) {
//                s.openCamera();
//                s.openSpeaker();
//                s.isOpened = false;
//            }
//        }
    }

    @Override
    public void OnTrChatLinkCloseMessage(int dwErrorCode) {
        Log.d(TAG, "OnTrChatLinkCloseMessage dwErrorCode:" + dwErrorCode);
        for (VideoSession s : mVideoSessions) {
            if (s.isSurfaceViewVisible()) {
                s.closeCamera();
                s.closeSpeaker();
                s.isOpened = false;
            }
        }
        //TODO
        if (dwErrorCode == 0) {

        } else {

        }
    }
}
