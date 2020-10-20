package com.trexchat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.trexchat.adapter.FrindsListAdapter;
import com.trexchat.config.ConfigEntity;
import com.trexchat.config.ConfigService;
import com.trexchat.model.Frinds;

import trexgroup.com.trexchat.R;

public class FrindsList extends Activity implements AnyChatBaseEvent {

    public static final String SERVER_ADDR = "demo.anychat.cn";
    public static final String SERVER_PORT = "8906";
    public static final int ROOMID = 9527;
    public static final String ROOMPWD = "9527trex0115";

    public static final String TAG = FrindsList.class.getSimpleName();
    private ListView lvFrinds;

    public AnyChatCoreSDK anyChatSDK;

    private final int SHOWLOGINSTATEFLAG = 1; // 显示的按钮是登陆状态的标识
    private final int SHOWWAITINGSTATEFLAG = 2; // 显示的按钮是等待状态的标识
    private final int SHOWLOGOUTSTATEFLAG = 3; // 显示的按钮是登出状态的标识
    private final int LOCALVIDEOAUTOROTATION = 1; // 本地视频自动旋转控制

    private Frinds.User mSelfInfo = new Frinds.User(SERVER_ADDR, SERVER_PORT, "TrexB");

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frinds_list);
        initFrindsList();

        InitSDK();
        ApplyVideoConfig();

        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("VideoActivity");
        // 注册广播
        registerBoradcastReceiver();
        login();
    }

    void initFrindsList() {
        lvFrinds = (ListView) findViewById(R.id.frinds_listview);
        FrindsListAdapter adapter = new FrindsListAdapter(this);
        lvFrinds.setAdapter(adapter);
        lvFrinds.setEnabled(false);
        lvFrinds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProgressDialog pd = showProgress();

//                anyChatSDK.EnterRoom(ROOMID, ROOMPWD);
                int[] userIds = anyChatSDK.GetOnlineUser();
                for (int uid:userIds){
                    String name = anyChatSDK.GetUserName(uid);
                    Log.d(TAG,"user:"+name);
                    if (name.equals(((FrindsListAdapter)lvFrinds.getAdapter()).getName(position))){
                        pd.dismiss();
                        String strUserID = String.valueOf(uid);
                        Intent intent = new Intent();
                        intent.putExtra("UserID", strUserID);
                        intent.setClass(FrindsList.this, VideoActivity.class);
                        startActivity(intent);
                        return;
                    }
                }
            }
        });
    }

    private ProgressDialog showProgress() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.show();
        return pd;
    }

    private void InitSDK() {
        if (anyChatSDK == null) {
            anyChatSDK = AnyChatCoreSDK.getInstance(this);
            anyChatSDK.SetBaseEvent(this);
            anyChatSDK.InitSDK(android.os.Build.VERSION.SDK_INT, 0);
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION,
                    LOCALVIDEOAUTOROTATION);
        }
    }

    private void login() {
        //普通登录
        if (checkInputData()) {

            int port = Integer.parseInt(mSelfInfo.getmPort());
            /**
             *AnyChat可以连接自主部署的服务器、也可以连接AnyChat视频云平台；
             *连接自主部署服务器的地址为自设的服务器IP地址或域名、端口；
             *连接AnyChat视频云平台的服务器地址为：cloud.anychat.cn；端口为：8906
             */
            anyChatSDK.Connect(mSelfInfo.getmIpAddr(), port);

            /***
             * AnyChat支持多种用户身份验证方式，包括更安全的签名登录，
             * 详情请参考：http://bbs.anychat.cn/forum.php?mod=viewthread&tid=2211&highlight=%C7%A9%C3%FB
             */
            anyChatSDK.Login(mSelfInfo.getmDisplayName(), "");
        }
    }

    private boolean checkInputData() {
        String ip = mSelfInfo.getmIpAddr();
        String port = mSelfInfo.getmPort();
        String name = mSelfInfo.getmDisplayName();
//        String roomID = mEditRoomID.getText().toString().trim();
        if (ValueUtils.isStrEmpty(ip)) {
            showToast("请输入IP");
            return false;
        } else if (ValueUtils.isStrEmpty(port)) {
            showToast("请输入端口号");
            return false;
        } else if (ValueUtils.isStrEmpty(name)) {
            showToast("请输入姓名");
            return false;
        } else {
            return true;
        }
    }

    // 根据配置文件配置视频参数
    private void ApplyVideoConfig() {
        ConfigEntity configEntity = ConfigService.LoadConfig(this);
        if (configEntity.mConfigMode == 1) // 自定义视频参数配置
        {
            // 设置本地视频编码的码率（如果码率为0，则表示使用质量优先模式）
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_BITRATECTRL,
                    configEntity.mVideoBitrate);
//			if (configEntity.mVideoBitrate == 0) {
            // 设置本地视频编码的质量
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_QUALITYCTRL,
                    configEntity.mVideoQuality);
//			}
            // 设置本地视频编码的帧率
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_FPSCTRL,
                    configEntity.mVideoFps);
            // 设置本地视频编码的关键帧间隔
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_GOPCTRL,
                    configEntity.mVideoFps * 4);
            // 设置本地视频采集分辨率
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL,
                    configEntity.mResolutionWidth);
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL,
                    configEntity.mResolutionHeight);
            // 设置视频编码预设参数（值越大，编码质量越高，占用CPU资源也会越高）
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_PRESETCTRL,
                    configEntity.mVideoPreset);
        }
        // 让视频参数生效
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_APPLYPARAM,
                configEntity.mConfigMode);
        // P2P设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_NETWORK_P2PPOLITIC,
                configEntity.mEnableP2P);
        // 本地视频Overlay模式设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_OVERLAY,
                configEntity.mVideoOverlay);
        // 回音消除设置
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_AUDIO_ECHOCTRL,
                configEntity.mEnableAEC);
        // 平台硬件编码设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_CORESDK_USEHWCODEC,
                configEntity.mUseHWCodec);
        // 视频旋转模式设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_ROTATECTRL,
                configEntity.mVideoRotateMode);
        // 本地视频采集偏色修正设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_FIXCOLORDEVIA,
                configEntity.mFixColorDeviation);
        // 视频GPU渲染设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_VIDEOSHOW_GPUDIRECTRENDER,
                configEntity.mVideoShowGPURender);
        // 本地视频自动旋转设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION,
                configEntity.mVideoAutoRotation);
    }

    //AnyChatBaseEvent
    @Override
    public void OnAnyChatConnectMessage(boolean bSuccess) {
        Log.d(TAG, "OnAnyChatConnectMessage :" + bSuccess);
        showToast("Connect:" + bSuccess);
    }

    //AnyChatBaseEvent
    @Override
    public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
        Log.d(TAG, "OnAnyChatLoginMessage dwUserId:" + dwUserId + " dwErrorCode:" + dwErrorCode);
        showToast("Login:" + dwUserId + " err:" + dwErrorCode);
        if (dwErrorCode == 0) {
            anyChatSDK.EnterRoom(ROOMID, ROOMPWD);
        }
    }

    //AnyChatBaseEvent
    @Override
    public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
        Log.d(TAG, "OnAnyChatEnterRoomMessage dwRoomId:" + dwRoomId + " dwErrorCode:" + dwErrorCode);
        showToast("EnterRoom dwRoomId:" + dwRoomId + " dwErrorCode:" + dwErrorCode);
    }

    //AnyChatBaseEvent
    @Override
    public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
        Log.d(TAG, "OnAnyChatOnlineUserMessage dwUserNum:" + dwUserNum + " dwRoomId:" + dwRoomId);
        showToast("OnLine dwRoomId:" + dwRoomId + " dwUserNum:" + dwUserNum);
        lvFrinds.setEnabled(true);
    }

    //AnyChatBaseEvent
    @Override
    public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {

        Log.d(TAG, "OnAnyChatUserAtRoomMessage dwUserId:" + dwUserId + " bEnter:" + bEnter);
        showToast("OnAnyChatUserAtRoomMessage dwUserId:" + dwUserId + " bEnter:" + bEnter);
    }

    //AnyChatBaseEvent
    @Override
    public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
        Log.d(TAG, "OnAnyChatLinkCloseMessage dwErrorCode:" + dwErrorCode);
        showToast("OnAnyChatLinkCloseMessage dwErrorCode:" + dwErrorCode);

    }

    // 广播
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("VideoActivity")) {
                Toast.makeText(FrindsList.this, "网络已断开！", Toast.LENGTH_SHORT)
                        .show();
//                setBtnVisible(SHOWLOGINSTATEFLAG);
//                mRoleList.setAdapter(null);
//                mBottomConnMsg.setText("No content to the server");
                anyChatSDK.LeaveRoom(-1);
                anyChatSDK.Logout();
            }
        }
    };

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("VideoActivity");
        // 注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
