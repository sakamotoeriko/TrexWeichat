package com.trex.trchat.ui.home;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.trex.trchat.R;
import com.trex.trchat.configs.AppSettings;
import com.trex.trchat.lib.trexsdk.TrChatCoreSdk;
import com.trex.trchat.lib.trexsdk.callbacks.TrChatBaseEvent;
import com.trex.trchat.lib.trexsdk.callbacks.TrChatUserInfoEvent;
import com.trex.trchat.lib.trexsdk.config.ConfigEntity;
import com.trex.trchat.lib.trexsdk.config.ConfigService;
import com.trex.trchat.trexbusiness.TrChatController;
import com.trex.trchat.ui.VideoActivity;
import com.trex.trchat.ui.adapter.HomePageAdapter;
import com.trex.trchat.ui.widget.CustomViewPager;
import com.trex.trchat.videocall.VideoCallController;
import com.trex.trchat.videocall.model.ConnectSession;
import com.trex.trchat.videocall.model.UserInfo;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomePageActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener, VideoCallController.VideoCallListener, TrChatBaseEvent, TrChatUserInfoEvent {

    public static final String TAG = HomePageActivity.class.getSimpleName();
    HomePageAdapter mAdapter;
    CustomViewPager mViewPager;
    TrChatCoreSdk mTrChatCoreSdk;
    TrChatController mTrChatController;
    Handler mHandler = new Handler();
    Timer mTimer;

    View mReceivecallView;
    ImageView mReceivecallAvatar;
    TextView mReceivecallName;
    Button mReceivecallReject;
    Button mReceivecallAccept;

    View mRequestcallView;
    TextView mRequestcallTime;
    Button mRequestcallSpeaker;
    Button mRequestcallMic;
    Button mRequestcallCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        initViews();
        initTrChatSdk();
    }

    @Override
    protected void onResume() {
        mTrChatController.getVideoCallController().setListener(this);
        super.onResume();
    }

    private void initViews() {
        mViewPager = (CustomViewPager) findViewById(R.id.viewpager);
        mAdapter = new HomePageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener(this);

        ((RadioGroup) findViewById(R.id.rg_tab_bar)).setOnCheckedChangeListener(this);


        //Recieve Call View
        mReceivecallView = findViewById(R.id.recievecall_frame);
        mReceivecallAvatar = (ImageView) findViewById(R.id.recievecall_avatar);
        mReceivecallName = (TextView) findViewById(R.id.recievecall_name);
        mReceivecallReject = (Button) findViewById(R.id.reject);
        mReceivecallAccept = (Button) findViewById(R.id.accept);
        mReceivecallView.setVisibility(View.GONE);

        //Request Call View
        mRequestcallView = findViewById(R.id.requestcall_frame);
        mRequestcallSpeaker = (Button) findViewById(R.id.requestcall_speaker);
        mRequestcallMic = (Button) findViewById(R.id.requestcall_mic);
        mRequestcallCancel = (Button) findViewById(R.id.requestcall_cancel);
        mRequestcallTime = (TextView) findViewById(R.id.requestcall_time);
        mRequestcallView.setVisibility(View.GONE);
    }

    private void initTrChatSdk() {
        mTrChatCoreSdk = TrChatCoreSdk.getInstance();
        mTrChatCoreSdk.setBaseEvent(this);
        mTrChatCoreSdk.setUserInfoEvent(this);
        mTrChatController = TrChatController.getInstance(getApplication());
        mTrChatController.getVideoCallController().setListener(this);
        ApplyVideoConfig();
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
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Log.d(TAG, "onCheckedChanged checkedId:" + checkedId);
        switch (checkedId) {
            case R.id.tab_home:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tab_chat:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.tab_settings:
                mViewPager.setCurrentItem(2);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.d(TAG, "onPageScrolled position:" + position);
    }

    @Override
    public void onPageSelected(int position) {
        Log.d(TAG, "onPageSelected position:" + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.d(TAG, "onPageScrollStateChanged state:" + state);
    }

    private void gotoVideoActivity(ConnectSession session) {
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra(VideoActivity.INTENT_EXTRA_SESSION, session);
        Log.d(TAG, "ConnectSession:" + session.toString());
        startActivity(intent);
    }

    private Calendar mRequestTimerCounter;

    private void updateRequestTimer() {
        if (mRequestTimerCounter == null) return;
        mRequestTimerCounter.add(Calendar.SECOND, 1);
        final String tm = String.format("%02d:%02d", mRequestTimerCounter.get(Calendar.MINUTE), mRequestTimerCounter.get(Calendar.SECOND));

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mRequestcallTime.setText(tm);
            }
        });
    }

    @Override
    public void onRequested(final ConnectSession session) {
        Log.d(TAG, "onRequested session:" + session.toString());

        mRequestTimerCounter = Calendar.getInstance();
        mRequestTimerCounter.set(Calendar.MINUTE, 0);
        mRequestTimerCounter.set(Calendar.SECOND, 0);

        if (mTimer == null)
            mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateRequestTimer();
            }
        }, 0, 1000);

        mRequestcallView.setVisibility(View.VISIBLE);
        mRequestcallCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrChatController.getVideoCallController().requestVideoCallEndCall(session);
                mRequestcallView.setVisibility(View.GONE);
            }
        });
    }

    private void showReceivecallView(){
        mReceivecallView.setVisibility(View.VISIBLE);
        mTrChatController.playCallReceivedMusic(getApplication());
    }
    private void dismissReceivecallView(){
        mReceivecallView.setVisibility(View.GONE);
        mTrChatController.stopSessionMusic();
    }
    @Override
    public void onEndCall(ConnectSession session) {
        dismissReceivecallView();
    }

    @Override
    public void onRecieved(final ConnectSession session) {
        boolean isAuto = AppSettings.getInstance().isAutoReplay();
        Log.d(TAG, "onRecieved session:" + session.toString() + " isAuto:" + isAuto);
        if (isAuto) {
            //auto replay
            gotoVideoActivity(session);
        } else {
            //show replay dialog
            //TODO
            int targId = session.getTargetUserId();
            List<UserInfo> friends = mTrChatController.getFrindsList();
            Bitmap avatar = null;
            String name = "";
            for (UserInfo user : friends) {
                if (user.getUserid() == targId) {
                    avatar = user.getAvatar();
                    name = user.getNickname();
                }
            }
            mReceivecallAvatar.setImageBitmap(avatar);
            mReceivecallName.setText(name);
            showReceivecallView();
            mReceivecallAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTrChatController.getVideoCallController().replayVideoCall(session, false);
                    dismissReceivecallView();
                    gotoVideoActivity(session);
                }
            });
            mReceivecallReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTrChatController.getVideoCallController().replayVideoCall(session, true);
                    dismissReceivecallView();
                }
            });

        }
    }

    @Override
    public void onReplay(ConnectSession session) {
        Log.d(TAG, "onReplay session:" + session.toString());
        mRequestcallView.setVisibility(View.GONE);
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        int status = session.getStatus();
        switch (status) {
            case ConnectSession.CONNECTSESSION_STATUS_ACCEPTED:
                //accepted
                gotoVideoActivity(session);
                break;
            case ConnectSession.CONNECTSESSION_STATUS_TIMEOUT:
                Toast.makeText(this, "对方无人接听", Toast.LENGTH_LONG);
                break;
            case ConnectSession.CONNECTSESSION_STATUS_REJECTED:
                Toast.makeText(this, "对方忙", Toast.LENGTH_LONG);
                break;

            default:
                break;
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
        Log.d(TAG, "OnAnyChatUserInfoUpdate dwUserId:" + dwUserId + " dwType:" + dwType);
        if (mAdapter == null) return;
        ChatFragment chatFragment = (ChatFragment) mAdapter.getItem(1);
        chatFragment.updateAdapter();
    }

    @Override
    public void OnAnyChatFriendStatus(int dwUserId, int dwStatus) {
        Log.d(TAG, "OnAnyChatFriendStatus dwUserId:" + dwUserId + " dwStatus:" + dwStatus);
        if (mAdapter == null) return;
        ChatFragment chatFragment = (ChatFragment) mAdapter.getItem(1);
        chatFragment.updateAdapter();
    }
}
