package com.trex.trchat.ui.video;

import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.trex.trchat.R;
import com.trex.trchat.lib.trexsdk.TrChatCoreSdk;
import com.trex.trchat.lib.trexsdk.common.TrChatSdkDefine;

import java.util.Timer;
import java.util.TimerTask;

public class VideoSession {
    public static final String TAG = VideoSession.class.getSimpleName();

    private SurfaceView surfaceView;
    private int userid;
    private int index;
    private boolean isMainView;
    public boolean isOpened;

    private Timer mTimerCheckVideo;
    private TrChatCoreSdk mTrChatCoreSdk;

    public VideoSession(SurfaceView surfaceView, int userid, boolean isMain) {
        this.surfaceView = surfaceView;
        this.userid = userid;
        isOpened = false;
        isMainView = isMain;
        mTrChatCoreSdk = TrChatCoreSdk.getInstance();
        Log.d(TAG,"new userid:"+userid);
    }

    public void init() {
        if (isLocal()) {
            index = -1;
            surfaceView.setZOrderOnTop(true);
            if (mTrChatCoreSdk.getSDKOptionInt(TrChatSdkDefine.define.BRAC_SO_LOCALVIDEO_CAPDRIVER) == TrChatSdkDefine.define.VIDEOCAP_DRIVER_JAVA) {
                surfaceView.getHolder().addCallback(mTrChatCoreSdk.mCameraHelper);
            }
            // 默认打开前置摄像头
            mTrChatCoreSdk.mCameraHelper.SelectVideoCapture(AnyChatCoreSDK.mCameraHelper.CAMERA_FACING_FRONT);
            Log.d(TAG, "init open local camera");
        } else {
            if (!isMainView) {
                Log.d(TAG, "not the main view");
                surfaceView.setZOrderOnTop(true);
            }
            surfaceView.setTag(userid);
            index = mTrChatCoreSdk.bindVideo(surfaceView.getHolder());
            mTrChatCoreSdk.setVideoUser(index, userid);

            Log.d(TAG, "init target userid:" + userid + " index:" + index + "surface:" + (String) surfaceView.getTag(R.string.index));
        }
    }

    public void start() {
        if (mTimerCheckVideo == null) mTimerCheckVideo = new Timer();
        mTimerCheckVideo.schedule(new TimerTask() {
            @Override
            public void run() {
                checkVideoStatus();
            }
        }, 1000, 100);
        openCameraSpeaker();
        Log.d(TAG,"start userid:"+userid);
    }

    public void stop() {
        closeCameraSpeaker();
        if (mTimerCheckVideo != null)
            mTimerCheckVideo.cancel();
        isOpened = false;
        Log.d(TAG,"stop userid:"+userid);
    }

    public void openCameraSpeaker() {
        openCamera();
        openSpeaker();
    }

    public void closeCameraSpeaker() {
        closeCamera();
        closeSpeaker();
    }

    public void setSurfaceViewVisibility(int visibility) {
        surfaceView.setVisibility(visibility);
    }

    public boolean isSurfaceViewVisible() {
        return (surfaceView.getVisibility() == View.VISIBLE);
    }

    public boolean isUser(int userid) {
        return this.userid == userid;
    }

    public boolean isSurfaceView(SurfaceView sv) {
        return this.surfaceView.equals(sv);
    }

    private void checkVideoStatus() {
        if (isOpened) {
            Log.w("[TAGLOOP]", "checkVideoStatus isOpened:" + isOpened);
            return;
        }
        if (surfaceView.getVisibility() != View.VISIBLE) {
            Log.w(TAG, "checkVideoStatus not visible");
            return;
        }

        boolean isLocal = isLocal();
        int cameraState = mTrChatCoreSdk.getCameraState(userid);
        int videoState = mTrChatCoreSdk.getUserVideoWidth(userid);
        final SurfaceHolder holder = surfaceView.getHolder();
//        Log.d("[TAGLOOP]", "checkVideoStatus userid:" + userid + " cameraState:" + cameraState + " videoState:" + videoState);

        if (cameraState != 2 && videoState != 0) {
            isOpened = true;
            holder.setFormat(PixelFormat.RGB_565);

            surfaceView.post(new Runnable() {
                @Override
                public void run() {
                    holder.setFixedSize(mTrChatCoreSdk.getUserVideoWidth(userid), mTrChatCoreSdk.getUserVideoHeight(userid));
                }
            });

//            Surface s = holder.getSurface();
//            mTrChatCoreSdk.setVideoPos(userid, s, 0, 0, 0, 0);
            if (isLocal) {
                Surface s = holder.getSurface();
                mTrChatCoreSdk.setVideoPos(-1, s, 0, 0, 0, 0);
            } else {
                mTrChatCoreSdk.setVideoUser(index, userid);
            }
            Log.d(TAG,"open checkVideoStatus userid:"+userid);
        }
    }

    private void openCamera() {
        int ret = mTrChatCoreSdk.userCameraControl(userid, 1);
        Log.d(TAG, "openCamera:" + userid + " ret =" + ret);
    }

    private void openSpeaker() {
//        mTrChatCoreSdk.userSpeakControl(userid, 1);
    }

    private void closeCamera() {
        mTrChatCoreSdk.userCameraControl(userid, 0);
        Log.d(TAG, "closeCamera:" + userid);
    }

    private void closeSpeaker() {
        mTrChatCoreSdk.userSpeakControl(userid, 0);
    }


    private boolean isLocal() {
        return (userid == -1);
    }
}
