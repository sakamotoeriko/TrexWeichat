package com.trex.trchat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.trex.trchat.R;
import com.trex.trchat.lib.trexsdk.TrChatCoreSdk;
import com.trex.trchat.lib.trexsdk.callbacks.TrChatBaseEvent;
import com.trex.trchat.lib.trexsdk.callbacks.TrChatUserInfoEvent;
import com.trex.trchat.lib.trexsdk.config.ConfigEntity;
import com.trex.trchat.lib.trexsdk.config.ConfigService;
import com.trex.trchat.trexbusiness.TrChatController;
import com.trex.trchat.ui.adapter.FrindsListAdapter;
import com.trex.trchat.videocall.VideoCallController;
import com.trex.trchat.videocall.model.ConnectSession;
import com.trex.trchat.videocall.model.UserInfo;

public class HomeActivity extends Activity implements TrChatBaseEvent, TrChatUserInfoEvent, VideoCallController.VideoCallListener {

    public static final String TAG = HomeActivity.class.getSimpleName();
    TrChatCoreSdk mTrChatCoreSdk;
    ListView mFrindListView;
    FrindsListAdapter mFrindsListAdapter;
    TrChatController mTrChatController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mTrChatCoreSdk = TrChatCoreSdk.getInstance();
        mTrChatCoreSdk.setBaseEvent(this);
        mTrChatCoreSdk.setUserInfoEvent(this);
        ApplyVideoConfig();
        mTrChatController = TrChatController.getInstance(getApplication());
        mTrChatController.getVideoCallController().setListener(this);

        mFrindListView = (ListView) findViewById(R.id.frindlist);
        mFrindsListAdapter = new FrindsListAdapter(this, mTrChatController.updateFrindsList());
        mFrindListView.setAdapter(mFrindsListAdapter);


        mFrindListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserInfo user = (UserInfo) mFrindsListAdapter.getItem(position);

                ConnectSession session = new ConnectSession(user.getUserid(), mTrChatController.getSelfUserId(), mTrChatController.getIdleRoomId(), mTrChatController.getPassword());
                mTrChatController.getVideoCallController().requestVideoCall(session);
            }
        });
    }


    // 根据配置文件配置视频参数
    private void ApplyVideoConfig() {
        ConfigEntity configEntity = ConfigService.LoadConfig(this);
        if (configEntity.configMode == 1)        // 自定义视频参数配置
        {
            // 设置本地视频编码的码率（如果码率为0，则表示使用质量优先模式）
            AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_BITRATECTRL, configEntity.videoBitrate);
            if (configEntity.videoBitrate == 0) {
                // 设置本地视频编码的质量
                AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_QUALITYCTRL, configEntity.videoQuality);
            }
            // 设置本地视频编码的帧率
            AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_FPSCTRL, configEntity.videoFps);
            // 设置本地视频编码的关键帧间隔
            AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_GOPCTRL, configEntity.videoFps * 4);
            // 设置本地视频采集分辨率
            AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL, configEntity.resolution_width);
            AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL, configEntity.resolution_height);
            // 设置视频编码预设参数（值越大，编码质量越高，占用CPU资源也会越高）
            AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_PRESETCTRL, configEntity.videoPreset);
        }
        // 让视频参数生效
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_APPLYPARAM, configEntity.configMode);
        // P2P设置
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_NETWORK_P2PPOLITIC, configEntity.enableP2P);
        // 本地视频Overlay模式设置
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_OVERLAY, configEntity.videoOverlay);
        // 回音消除设置
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_AUDIO_ECHOCTRL, configEntity.enableAEC);
        // 平台硬件编码设置
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_CORESDK_USEHWCODEC, configEntity.useHWCodec);
        // 视频旋转模式设置
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_ROTATECTRL, configEntity.videorotatemode);
        // 本地视频采集偏色修正设置
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_FIXCOLORDEVIA, configEntity.fixcolordeviation);
        // 视频GPU渲染设置
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_GPUDIRECTRENDER, configEntity.videoShowGPURender);
        // 本地视频自动旋转设置
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION, configEntity.videoAutoRotation);
    }

    @Override
    protected void onResume() {
        mTrChatController.getVideoCallController().setListener(this);
        super.onResume();
    }

    @Override
    public void OnTrChatConnectMessage(boolean bSuccess) {

    }

    @Override
    public void OnTrChatLoginMessage(int dwUserId, int dwErrorCode) {

    }

    @Override
    public void OnTrChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {

    }

    @Override
    public void OnTrChatOnlineUserMessage(int dwUserNum, int dwRoomId) {

    }

    @Override
    public void OnTrChatUserAtRoomMessage(int dwUserId, boolean bEnter) {

    }

    @Override
    public void OnTrChatLinkCloseMessage(int dwErrorCode) {

    }

    @Override
    public void OnAnyChatUserInfoUpdate(int dwUserId, int dwType) {
        if (dwUserId == 0 && dwType == 0) {
            mTrChatController.updateFrindsList();
            if (mFrindsListAdapter != null)
                mFrindsListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void OnAnyChatFriendStatus(int dwUserId, int dwStatus) {

    }


    private void gotoVideoActivity(ConnectSession session) {
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra(VideoActivity.INTENT_EXTRA_SESSION, session);
        Log.d(TAG,"ConnectSession:"+session.toString());
        startActivity(intent);
    }

    @Override
    public void onRequested(ConnectSession session) {
        Log.d(TAG, "onRequested session:" + session.toString());
        gotoVideoActivity(session);
    }

    @Override
    public void onRecieved(ConnectSession session) {
        Log.d(TAG, "onRecieved session:" + session.toString());
        gotoVideoActivity(session);
    }

    @Override
    public void onReplay(ConnectSession session) {
        Log.d(TAG, "onReplay session:" + session.toString());

    }
}
