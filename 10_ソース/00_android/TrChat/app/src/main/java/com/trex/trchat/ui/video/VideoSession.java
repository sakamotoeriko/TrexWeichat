package com.trex.trchat.ui.video;

import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.trex.trchat.lib.trexsdk.TrChatCoreSdk;
import com.trex.trchat.lib.trexsdk.common.TrChatSdkDefine;

import java.util.Timer;
import java.util.TimerTask;

public class VideoSession {
    public static final String TAG = VideoSession.class.getSimpleName();

    private SurfaceView surfaceView;
    private int userid;
    private int index;
    public boolean isOpened;
    private Timer mTimerCheckVideo;

    public VideoSession(int userid, SurfaceView surfaceView) {
        this.userid = userid;
        this.surfaceView = surfaceView;
        isOpened = false;
    }

    public void init() {
        TrChatCoreSdk trChatCoreSdk = TrChatCoreSdk.getInstance();
        if (isLocal()) {
            index = -1;
            surfaceView.getHolder().setType(
                    SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            surfaceView.setZOrderOnTop(true);
            if (trChatCoreSdk.getSDKOptionInt(TrChatSdkDefine.define.BRAC_SO_LOCALVIDEO_CAPDRIVER) == TrChatSdkDefine.define.VIDEOCAP_DRIVER_JAVA) {
                surfaceView.getHolder().addCallback(TrChatCoreSdk.mCameraHelper);
            }
            // 默认打开前置摄像头
            TrChatCoreSdk.mCameraHelper.SelectVideoCapture(AnyChatCoreSDK.mCameraHelper.CAMERA_FACING_FRONT);
        } else {
            if (trChatCoreSdk.getSDKOptionInt(TrChatSdkDefine.define.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == TrChatSdkDefine.define.VIDEOSHOW_DRIVER_JAVA) {
                index = trChatCoreSdk.bindVideo(surfaceView.getHolder());
                trChatCoreSdk.setVideoUser(index, userid);
            }
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
    }

    public void stop() {
        closeCamera();
        closeSpeaker();
        mTimerCheckVideo.cancel();
        isOpened = false;
    }

    public void openCamera() {
        TrChatCoreSdk.getInstance().userCameraControl(userid, 1);
    }

    public void openSpeaker() {
        TrChatCoreSdk.getInstance().userSpeakControl(userid, 1);
    }

    public void closeCamera() {
        TrChatCoreSdk.getInstance().userCameraControl(userid, 0);
    }

    public void closeSpeaker() {
        TrChatCoreSdk.getInstance().userSpeakControl(userid, 0);
    }

    public boolean isSurfaceViewVisible() {
        return (surfaceView.getVisibility() == View.VISIBLE);
    }

    private void checkVideoStatus() {
        Log.d(TAG, "checkVideoStatus");

        if (isOpened || surfaceView.getVisibility() != View.VISIBLE)
            return;

        TrChatCoreSdk trChatCoreSdk = TrChatCoreSdk.getInstance();
        if (isLocal()) {
            if (trChatCoreSdk.getCameraState(-1) == 2 && trChatCoreSdk.getUserVideoWidth(-1) != 0) {
                SurfaceHolder holder = surfaceView.getHolder();
                if (trChatCoreSdk.getSDKOptionInt(TrChatSdkDefine.define.BRAC_SO_VIDEOSHOW_DRIVERCTRL) != TrChatSdkDefine.define.VIDEOSHOW_DRIVER_JAVA) {
                    holder.setFormat(PixelFormat.RGB_565);
                    holder.setFixedSize(trChatCoreSdk.getUserVideoWidth(-1), trChatCoreSdk.getUserVideoHeight(-1));
                }
                Surface s = holder.getSurface();
                trChatCoreSdk.setVideoPos(-1, s, 0, 0, 0, 0);
                isOpened = true;
            }
        } else {
            if (trChatCoreSdk.getCameraState(userid) == 2 && trChatCoreSdk.getUserVideoWidth(userid) != 0) {
                SurfaceHolder holder = surfaceView.getHolder();
                // 如果是采用内核视频显示（非Java驱动），则需要设置Surface的参数
                if (trChatCoreSdk.getSDKOptionInt(TrChatSdkDefine.define.BRAC_SO_VIDEOSHOW_DRIVERCTRL) != TrChatSdkDefine.define.VIDEOSHOW_DRIVER_JAVA) {
                    holder.setFormat(PixelFormat.RGB_565);
                    holder.setFixedSize(trChatCoreSdk.getUserVideoWidth(-1), trChatCoreSdk.getUserVideoHeight(-1));
                }
                Surface s = holder.getSurface();
                if (trChatCoreSdk.getSDKOptionInt(TrChatSdkDefine.define.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == TrChatSdkDefine.define.VIDEOSHOW_DRIVER_JAVA) {
                    trChatCoreSdk.setVideoUser(index, userid);
                } else {
                    trChatCoreSdk.setVideoPos(userid, s, 0, 0, 0, 0);
                }
                isOpened = true;
            }
        }
    }

    private boolean isLocal() {
        return (userid == -1);
    }
}
