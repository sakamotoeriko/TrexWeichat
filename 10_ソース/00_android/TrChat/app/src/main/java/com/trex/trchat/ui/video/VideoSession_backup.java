package com.trex.trchat.ui.video;

import android.graphics.PixelFormat;
import android.util.Log;
import android.view.MotionEvent;
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

public class VideoSession_backup {
    public static final String TAG = VideoSession_backup.class.getSimpleName();

    private SurfaceView surfaceView;
    private int userid;
    private int index;
    public boolean isOpened;
    private Timer mTimerCheckVideo;

    public VideoSession_backup(int userid, SurfaceView surfaceView) {
        this.userid = userid;
        this.surfaceView = surfaceView;
        isOpened = false;

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    VideoSession_backup.this.openCamera();
                    VideoSession_backup.this.openSpeaker();
                }
                return true;
            }
        });
    }

    public void init() {
        TrChatCoreSdk trChatCoreSdk = TrChatCoreSdk.getInstance();
        if (isLocal()) {
            index = -1;
            surfaceView.getHolder().setType(
                    SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            surfaceView.setZOrderOnTop(true);
            if (trChatCoreSdk.getSDKOptionInt(TrChatSdkDefine.define.BRAC_SO_LOCALVIDEO_CAPDRIVER) == TrChatSdkDefine.define.VIDEOCAP_DRIVER_JAVA) {
                surfaceView.getHolder().addCallback(trChatCoreSdk.mCameraHelper);
            }
            // 默认打开前置摄像头
            trChatCoreSdk.mCameraHelper.SelectVideoCapture(AnyChatCoreSDK.mCameraHelper.CAMERA_FACING_FRONT);
            Log.d(TAG, "init open local camera");
        } else {
            surfaceView.setTag(userid);
            if (!surfaceView.getTag(R.string.index).equals("mSurfaceMain")){
                Log.d(TAG,"surface:!!!!!! :"+userid);
                surfaceView.getHolder().setType(
                        SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                surfaceView.setZOrderOnTop(true);
            }
            if (trChatCoreSdk.getSDKOptionInt(TrChatSdkDefine.define.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == TrChatSdkDefine.define.VIDEOSHOW_DRIVER_JAVA) {
                index = trChatCoreSdk.bindVideo(surfaceView.getHolder());
                trChatCoreSdk.setVideoUser(index, userid);
                Log.d(TAG, "init target userid:" + userid + " index:" + index + "surface:"+(String)surfaceView.getTag(R.string.index));
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
        if (mTimerCheckVideo != null)
            mTimerCheckVideo.cancel();
        isOpened = false;
    }

    public void openCamera() {
        int ret = TrChatCoreSdk.getInstance().userCameraControl(userid, 1);
        Log.d(TAG,"openCamera:"+userid + " ret ="+ret);
    }

    public void openSpeaker() {
//        TrChatCoreSdk.getInstance().userSpeakControl(userid, 1);
    }

    public void closeCamera() {
        TrChatCoreSdk.getInstance().userCameraControl(userid, 0);
        Log.d(TAG,"closeCamera:"+userid);
    }

    public void closeSpeaker() {
        TrChatCoreSdk.getInstance().userSpeakControl(userid, 0);
    }

    public void setSurfaceViewVisibility(int visibility) {
        surfaceView.setVisibility(visibility);
    }

    public boolean isSurfaceViewVisible() {
        return (surfaceView.getVisibility() == View.VISIBLE);
    }

    private void checkVideoStatus() {
        if (isOpened || surfaceView.getVisibility() != View.VISIBLE)
            return;

        Log.d(TAG+"loop", "checkVideoStatus");
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
                Log.d(TAG, "checkVideoStatus local open");
            }
        } else {
            if (trChatCoreSdk.getCameraState(userid) == 2 && trChatCoreSdk.getUserVideoWidth(userid) != 0) {
                SurfaceHolder holder = surfaceView.getHolder();
                // 如果是采用内核视频显示（非Java驱动），则需要设置Surface的参数
                if (trChatCoreSdk.getSDKOptionInt(TrChatSdkDefine.define.BRAC_SO_VIDEOSHOW_DRIVERCTRL) != TrChatSdkDefine.define.VIDEOSHOW_DRIVER_JAVA) {
                    holder.setFormat(PixelFormat.RGB_565);
                    holder.setFixedSize(trChatCoreSdk.getUserVideoWidth(userid), trChatCoreSdk.getUserVideoHeight(userid));
                }
                Surface s = holder.getSurface();
                if (trChatCoreSdk.getSDKOptionInt(TrChatSdkDefine.define.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == TrChatSdkDefine.define.VIDEOSHOW_DRIVER_JAVA) {
                    trChatCoreSdk.setVideoUser(index, userid);
                } else {
                    trChatCoreSdk.setVideoPos(userid, s, 0, 0, 0, 0);
                }
                isOpened = true;
                Log.d(TAG, "checkVideoStatus userid:" + userid + " index:" + index + " open");
            }
        }
    }

    public boolean isUser(int userid) {
        return this.userid == userid;
    }

    public boolean isSurfaceView(SurfaceView sv) {
        return this.surfaceView.equals(sv);
    }

    private boolean isLocal() {
        return (userid == -1);
    }
}
