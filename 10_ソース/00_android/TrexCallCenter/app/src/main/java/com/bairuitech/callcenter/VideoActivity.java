package com.bairuitech.callcenter;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bairuitech.anychat.*;
import com.bairuitech.bussinesscenter.BussinessCenter;
import com.bairuitech.bussinesscenter.SessionItem;
import com.bairuitech.callcenter.R;
import com.bairuitech.util.*;

public class VideoActivity extends Activity implements AnyChatBaseEvent,
		OnClickListener, OnTouchListener, AnyChatVideoCallEvent, AnyChatUserInfoEvent {
	public static final String TAG = VideoActivity.class.getSimpleName();
	public static final String BROADCAST_NEWSESSION = "com.bairuitech.callcenter.BROADCAST_NEWSESSION";
	public static final String BROADCAST_NEWSESSION_EXTRA = "com.bairuitech.callcenter.BROADCAST_NEWSESSION.EXTRA";
	public static final String BROADCAST_SESSION_END = "com.bairuitech.callcenter.BROADCAST_SESSION_END";
	private SurfaceView mSurfaceSelf;
	private SurfaceView mSurfaceRemote;
	private ProgressBar mProgressSelf;
	private ProgressBar mProgressRemote;
	private ImageView mImgSwitch;
	private TextView mTxtTime;
	private Button mBtnEndSession;
	private Button mBtnCall3rd;
	private Dialog dialog;

	private SurfaceView mSurfaceRemote2;
	private ProgressBar mProgressRemote2;
	private View layoutremote;
	private View layoutremote2;


	private AnyChatCoreSDK anychat;
	private Handler mHandler;
	private Timer mTimerCheckAv;
	private Timer mTimerShowVideoTime;
	private TimerTask mTimerTask;
	private ConfigEntity configEntity;

	boolean bSelfVideoOpened = false;
	boolean bOtherVideoOpened = false;
	boolean b3rdVideoOpened = false;
	boolean bVideoViewLoaded = false;
	public static final int MSG_CHECKAV = 1;
	public static final int MSG_TIMEUPDATE = 2;
	public static final int PROGRESSBAR_HEIGHT = 5;

	int dwTargetUserId;
	int videoIndex = 0;
	int videocallSeconds = 0;

	int dwTargetUserId2 =0;
	int videoIndex2 = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initSdk();
		dwTargetUserId = BussinessCenter.sessionItem
				.getPeerUserItem(BussinessCenter.selfUserId);
		initView();

		Log.d(TAG,"onCreate roomid:"+BussinessCenter.sessionItem.roomId+" pwd:"+BussinessCenter.sessionItem.roomPwd);
		anychat.EnterRoom(BussinessCenter.sessionItem.roomId, BussinessCenter.sessionItem.roomPwd);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MSG_CHECKAV:
					Log.d(TAG,"MSG_CHECKAV");
					CheckVideoStatus();
					updateVolume();
					break;
				case MSG_TIMEUPDATE:
					mTxtTime.setText(BaseMethod
							.getTimeShowString(videocallSeconds++));
					break;
				}

			}
		};
		initTimerCheckAv();
		initTimerShowTime();
		registerSessionChangedReciever();
	}

	private BroadcastReceiver sessionChangedReciever ;
	private void registerSessionChangedReciever(){
		sessionChangedReciever = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals(BROADCAST_NEWSESSION)){

					dwTargetUserId2 = BussinessCenter.sessionItemExtention.targetUserId;
					initViewFor2Remote();
				}else if (action.equals(BROADCAST_SESSION_END)){

				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(BROADCAST_NEWSESSION);
		filter.addAction(BROADCAST_SESSION_END);
		this.registerReceiver(sessionChangedReciever,filter);
	}
	private void unregisterSessionChangedReciever(){
		if (sessionChangedReciever !=null){
			this.unregisterReceiver(sessionChangedReciever);
		}
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		// 如果是采用Java视频显示，则需要绑定用户
		if (AnyChatCoreSDK
				.GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
			videoIndex = anychat.mVideoHelper.bindVideo(mSurfaceRemote
					.getHolder());
			anychat.mVideoHelper.SetVideoUser(videoIndex, dwTargetUserId);
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		BussinessCenter.mContext = this;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		anychat.UserCameraControl(-1, 0);
		anychat.UserSpeakControl(-1, 0);
		anychat.UserSpeakControl(dwTargetUserId, 0);
		anychat.UserCameraControl(dwTargetUserId, 0);
		mTimerCheckAv.cancel();
		mTimerShowVideoTime.cancel();
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
		anychat.LeaveRoom(-1);
		anychat.removeEvent(this);

		unregisterSessionChangedReciever();
	}

	private void initSdk() {
		if (anychat == null)
			anychat = AnyChatCoreSDK.getInstance(VideoActivity.this);
		anychat.SetBaseEvent(this);
		anychat.SetVideoCallEvent(this);
		anychat.SetUserInfoEvent(this);
		anychat.mSensorHelper.InitSensor(getApplicationContext());
		// 初始化Camera上下文句柄
		AnyChatCoreSDK.mCameraHelper.SetContext(getApplicationContext());

	}

	private void initTimerShowTime() {
		if (mTimerShowVideoTime == null)
			mTimerShowVideoTime = new Timer();
		mTimerTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessage(MSG_TIMEUPDATE);
			}
		};
		mTimerShowVideoTime.schedule(mTimerTask, 100, 1000);
	}

	private void initTimerCheckAv() {
		if (mTimerCheckAv == null)
			mTimerCheckAv = new Timer();
		mTimerTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessage(MSG_CHECKAV);
			}
		};
		mTimerCheckAv.schedule(mTimerTask, 1000, 100);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dialog = DialogFactory.getDialog(DialogFactory.DIALOGID_ENDCALL,
					dwTargetUserId, this);
			dialog.show();
		}

		return super.onKeyDown(keyCode, event);
	}

	private void initViewFor2Remote(){
		layoutremote2 = findViewById(R.id.remote_layout2);

		mSurfaceRemote2 = (SurfaceView) findViewById(R.id.surface_remote2);
		mProgressRemote2 = (ProgressBar) findViewById(R.id.progress_remote2);

		mSurfaceRemote2.setTag(dwTargetUserId2);
		// 如果是采用Java视频显示，则设置Surface的CallBack
		if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
			videoIndex2 = anychat.mVideoHelper.bindVideo(mSurfaceRemote2.getHolder());
			anychat.mVideoHelper.SetVideoUser(videoIndex2, dwTargetUserId2);
			Log.d(TAG,"initViewFor2Remote videoIndex2:"+videoIndex2+" dwTargetUserId2:"+dwTargetUserId2);
			Log.i("ANYCHAT", "VIDEOSHOW---" + "JAVA");
		}
		layoutremote2.setVisibility(View.VISIBLE);
		Log.d(TAG,"initViewFor2Remote end");

	}
	private void initView() {
		this.setContentView(R.layout.video_activity);
		mSurfaceSelf = (SurfaceView) findViewById(R.id.surface_local);
		mSurfaceRemote = (SurfaceView) findViewById(R.id.surface_remote);
		mProgressSelf = (ProgressBar) findViewById(R.id.progress_local);
		mProgressRemote = (ProgressBar) findViewById(R.id.progress_remote);
		mImgSwitch = (ImageView) findViewById(R.id.img_switch);
		mTxtTime = (TextView) findViewById(R.id.txt_time);
		mBtnEndSession = (Button) findViewById(R.id.btn_endsession);
		mBtnEndSession.setOnClickListener(this);
		mBtnCall3rd = (Button) findViewById(R.id.call3rd);
		mBtnCall3rd.setOnClickListener(this);
		mImgSwitch.setOnClickListener(this);
		mSurfaceRemote.setTag(dwTargetUserId);
		configEntity = ConfigService.LoadConfig(this);
		if (configEntity.videoOverlay != 0) {
			mSurfaceSelf.getHolder().setType(
					SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		mSurfaceSelf.setZOrderOnTop(true);
		// 如果是采用Java视频采集，则设置Surface的CallBack
		if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
			mSurfaceSelf.getHolder().addCallback(AnyChatCoreSDK.mCameraHelper);
			Log.i("ANYCHAT", "VIDEOCAPTRUE---" + "JAVA");
		}

		// 如果是采用Java视频显示，则设置Surface的CallBack
		if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
			videoIndex = anychat.mVideoHelper.bindVideo(mSurfaceRemote.getHolder());
			anychat.mVideoHelper.SetVideoUser(videoIndex, dwTargetUserId);
			Log.i("ANYCHAT", "VIDEOSHOW---" + "JAVA");
		}

		final View layoutLocal = (View) findViewById(R.id.frame_local_area);
		// 得到xml布局中视频区域的大小
		layoutLocal.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						// TODO Auto-generated method stub
						if (!bVideoViewLoaded) {
							bVideoViewLoaded = true;
						}
					}
				});
		// 判断是否显示本地摄像头切换图标
		if (AnyChatCoreSDK
				.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
			if (AnyChatCoreSDK.mCameraHelper.GetCameraNumber() > 1) {
				mImgSwitch.setVisibility(View.VISIBLE);
				// 默认打开前置摄像头
				AnyChatCoreSDK.mCameraHelper.SelectVideoCapture(AnyChatCoreSDK.mCameraHelper.CAMERA_FACING_FRONT);
			}
		} else {
			String[] strVideoCaptures = anychat.EnumVideoCapture();
			if (strVideoCaptures != null && strVideoCaptures.length > 1) {
				mImgSwitch.setVisibility(View.VISIBLE);
				// 默认打开前置摄像头
				for (int i = 0; i < strVideoCaptures.length; i++) {
					String strDevices = strVideoCaptures[i];
					if (strDevices.indexOf("Front") >= 0) {
						anychat.SelectVideoCapture(strDevices);
						break;
					}
				}
			}
		}
		// 根据屏幕方向改变本地surfaceview的宽高比
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			adjustLocalVideo(true);
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			adjustLocalVideo(false);
		}

	}

	/***
	 * 调整本地视频区域宽度为界面宽度大小的1/4。竖屏时，本地预览的surfaceview的宽高比例为分辨率高宽比例;横屏时，
	 * 本地预览的surfaceview的宽高比例为分辨率宽高比例
	 * 
	 * @paramwidth
	 *            宽
	 * 
	 */
	public void adjustLocalVideo(boolean bLandScape) {
		float width;
		float height = 0;
		DisplayMetrics dMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
		width = (float) dMetrics.widthPixels / 4;
		LinearLayout layoutLocal = (LinearLayout) this
				.findViewById(R.id.frame_local_area);
		FrameLayout.LayoutParams layoutParams = (android.widget.FrameLayout.LayoutParams) layoutLocal
				.getLayoutParams();
		if (bLandScape) {

			if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL) != 0)
				height = width * AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL)
						/ AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL)
						+ PROGRESSBAR_HEIGHT;
			else
				height = (float) 3 / 4 * width + PROGRESSBAR_HEIGHT;
		} else {

			if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL) != 0)
				height = width * AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL)
						/ AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL)
						+ PROGRESSBAR_HEIGHT;
			else
				height = (float) 4 / 3 * width + PROGRESSBAR_HEIGHT;
		}
		layoutParams.width = (int) width;
		layoutParams.height = (int) height;
		layoutLocal.setLayoutParams(layoutParams);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			adjustLocalVideo(true);
			AnyChatCoreSDK.mCameraHelper.setCameraDisplayOrientation();
		} else {
			adjustLocalVideo(false);
			AnyChatCoreSDK.mCameraHelper.setCameraDisplayOrientation();
		}

	}

	// 判断视频是否已打开
	private void CheckVideoStatus() {
		if (!bOtherVideoOpened) {
			if (anychat.GetCameraState(dwTargetUserId) == 2
					&& anychat.GetUserVideoWidth(dwTargetUserId) != 0) {
				SurfaceHolder holder = mSurfaceRemote.getHolder();
				// 如果是采用内核视频显示（非Java驱动），则需要设置Surface的参数
				if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) != AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
					holder.setFormat(PixelFormat.RGB_565);
					holder.setFixedSize(anychat.GetUserVideoWidth(-1), anychat.GetUserVideoHeight(-1));
				}
				Surface s = holder.getSurface();
				if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
					anychat.mVideoHelper.SetVideoUser(videoIndex, dwTargetUserId);
				} else
					anychat.SetVideoPos(dwTargetUserId, s, 0, 0, 0, 0);
				bOtherVideoOpened = true;
			}
		}
		if (!b3rdVideoOpened){
			Log.d(TAG,"MSG_CHECKAV dwTargetUserId2:"+dwTargetUserId2);
			if (anychat.GetCameraState(dwTargetUserId2) == 2
					&& anychat.GetUserVideoWidth(dwTargetUserId2) != 0) {
				SurfaceHolder holder = mSurfaceRemote2.getHolder();
				// 如果是采用内核视频显示（非Java驱动），则需要设置Surface的参数
				if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) != AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
					holder.setFormat(PixelFormat.RGB_565);
					holder.setFixedSize(anychat.GetUserVideoWidth(-1), anychat.GetUserVideoHeight(-1));
				}
				Surface s = holder.getSurface();
				if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
					anychat.mVideoHelper.SetVideoUser(videoIndex2, dwTargetUserId2);
					Log.d(TAG,"videoIndex2:"+videoIndex2+" dwTargetUserId2:"+dwTargetUserId2);
				} else
					anychat.SetVideoPos(dwTargetUserId2, s, 0, 0, 0, 0);
				b3rdVideoOpened = true;
			}
		}
		if (!bSelfVideoOpened) {
			if (anychat.GetCameraState(-1) == 2 && anychat.GetUserVideoWidth(-1) != 0) {
				SurfaceHolder holder = mSurfaceSelf.getHolder();
				if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) != AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
					holder.setFormat(PixelFormat.RGB_565);
					holder.setFixedSize(anychat.GetUserVideoWidth(-1), anychat.GetUserVideoHeight(-1));
				}
				Surface s = holder.getSurface();
				anychat.SetVideoPos(-1, s, 0, 0, 0, 0);
				bSelfVideoOpened = true;
			}
		}
	}

	private void updateVolume() {
		mProgressSelf.setProgress(anychat.GetUserSpeakVolume(-1));
		mProgressRemote.setProgress(anychat.GetUserSpeakVolume(dwTargetUserId));
		if (mProgressRemote2!=null){
			mProgressRemote2.setProgress(anychat.GetUserSpeakVolume(dwTargetUserId2));
		}
	}

	@Override
	public void OnAnyChatConnectMessage(boolean bSuccess) {
		// TODO Auto-generated method stub
		if (dialog != null
				&& dialog.isShowing()
				&& DialogFactory.getCurrentDialogId() == DialogFactory.DIALOGID_RESUME) {
			dialog.dismiss();
		}
		Log.d(TAG,"OnAnyChatConnectMessage");
	}

	@Override
	public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
		// TODO Auto-generated method stub
		if (dwErrorCode == 0) {
			BussinessCenter.selfUserId = dwUserId;
			BussinessCenter.selfUserName = anychat.GetUserName(dwUserId);
		}
		Log.d(TAG,"OnAnyChatLoginMessage");
	}

	@Override
	public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
		// TODO Auto-generated method stub
		if (dwErrorCode == 0) {
			anychat.UserCameraControl(-1, 1);
			anychat.UserSpeakControl(-1, 1);

			bSelfVideoOpened = false;
		}
		Log.d(TAG, "OnAnyChatEnterRoomMessage roomid:" + dwRoomId + " errcode:" + dwErrorCode);
	}

	@Override
	public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
		// TODO Auto-generated method stub
		anychat.UserCameraControl(dwTargetUserId, 1);
		anychat.UserSpeakControl(dwTargetUserId, 1);
		if (dwTargetUserId2!=0) {
			anychat.UserCameraControl(dwTargetUserId2, 1);
			anychat.UserSpeakControl(dwTargetUserId2, 1);
		}
		bOtherVideoOpened = false;
		b3rdVideoOpened = false;

		Log.d(TAG, "OnAnyChatOnlineUserMessage dwUserNum:" + dwUserNum + " dwRoomId:" + dwRoomId);
	}

	@Override
	public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
		// TODO Auto-generated method stub
		anychat.UserCameraControl(dwTargetUserId, 1);
		anychat.UserSpeakControl(dwTargetUserId, 1);
		if (dwTargetUserId2!=0) {
			anychat.UserCameraControl(dwTargetUserId2, 1);
			anychat.UserSpeakControl(dwTargetUserId2, 1);
		}
		bOtherVideoOpened = false;
		b3rdVideoOpened = false;
		Log.d(TAG, "OnAnyChatUserAtRoomMessage dwUserId:" + dwUserId + " bEnter:" + bEnter);
	}

	@Override
	public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
		// TODO Auto-generated method stub
		anychat.UserCameraControl(-1, 0);
		anychat.UserSpeakControl(-1, 0);
		anychat.UserSpeakControl(dwTargetUserId, 0);
		anychat.UserCameraControl(dwTargetUserId, 0);
		if (dwTargetUserId2!=0) {
			anychat.UserCameraControl(dwTargetUserId2, 0);
			anychat.UserSpeakControl(dwTargetUserId2, 0);
		}
		if (dwErrorCode == 0) {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			BaseMethod.showToast(this.getString(R.string.session_end), this);
			dialog = DialogFactory.getDialog(DialogFactory.DIALOG_NETCLOSE,
					DialogFactory.DIALOG_NETCLOSE, this);
			dialog.show();
		} else {
			BaseMethod.showToast(this.getString(R.string.str_serverlink_close),
					this);
			Intent intent = new Intent();
			intent.putExtra("INTENT", BaseConst.AGAIGN_LOGIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(this, LoginActivity.class);
			this.startActivity(intent);
			this.finish();
		}
		Log.i("ANYCHAT", "OnAnyChatLinkCloseMessage:" + dwErrorCode);
		Log.d(TAG, "OnAnyChatLinkCloseMessage dwErrorCode:" + dwErrorCode);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mBtnEndSession) {
			dialog = DialogFactory.getDialog(DialogFactory.DIALOGID_ENDCALL,
					dwTargetUserId, this);
			dialog.show();
		}
		if (v == mBtnCall3rd){
			String userid = ((TextView)findViewById(R.id.userid)).getText().toString();
			try {
				int u = Integer.parseInt(userid);
				BussinessCenter.sessionItemExtention = new SessionItem(0, u,
						BussinessCenter.selfUserId);
				BussinessCenter.getBussinessCenter().requestAnotherCall(u,BussinessCenter.sessionItem.roomId,BussinessCenter.sessionItem.roomPwd);
//				BussinessCenter.VideoCallControl(AnyChatDefine.BRAC_VIDEOCALL_EVENT_REQUEST, u, 0,	0, BussinessCenter.sessionItem.getRoomId(), BussinessCenter.sessionItem.getRoomPwd());
			}catch (Exception e){
				e.printStackTrace();
			}
			return;
		}
		if (v == mImgSwitch) {
			// 如果是采用Java视频采集，则在Java层进行摄像头切换
			if (AnyChatCoreSDK
					.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
				AnyChatCoreSDK.mCameraHelper.SwitchCamera();
				return;
			}
			String strVideoCaptures[] = anychat.EnumVideoCapture();
			String temp = anychat.GetCurVideoCapture();
			for (int i = 0; i < strVideoCaptures.length; i++) {
				if (!temp.equals(strVideoCaptures[i])) {
					anychat.UserCameraControl(-1, 0);
					anychat.SelectVideoCapture(strVideoCaptures[i]);
					anychat.UserCameraControl(-1, 1);
					bSelfVideoOpened = false;
					break;
				}
			}
		}
	}

	@Override
	public void OnAnyChatVideoCallEvent(int dwEventType, int dwUserId,
			int dwErrorCode, int dwFlags, int dwParam, String userStr) {
		// TODO Auto-generated method stub
		switch (dwEventType) {

			case AnyChatDefine.BRAC_VIDEOCALL_EVENT_REQUEST:
				BussinessCenter.getBussinessCenter().onVideoCallRequest(
						dwUserId, dwFlags, dwParam, userStr);
				if (dialog != null && dialog.isShowing())
					dialog.dismiss();
				dialog = DialogFactory.getDialog(DialogFactory.DIALOGID_REQUEST,
						dwUserId,VideoActivity.this);
				dialog.show();
				break;
			case AnyChatDefine.BRAC_VIDEOCALL_EVENT_REPLY:
				BussinessCenter.getBussinessCenter().onVideoCallReply(
						dwUserId, dwErrorCode, dwFlags, dwParam, userStr);
				if (dwErrorCode == AnyChatDefine.BRAC_ERRORCODE_SUCCESS) {
					dialog = DialogFactory.getDialog(
							DialogFactory.DIALOGID_CALLING, dwUserId,
							VideoActivity.this);
					dialog.show();

				} else {
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
				}
				break;
			case AnyChatDefine.BRAC_VIDEOCALL_EVENT_START:
				if (dialog != null && dialog.isShowing())
					dialog.dismiss();

				BussinessCenter.getBussinessCenter().onSecondVideoCallStart(
						dwUserId, dwFlags, dwParam, userStr);
				break;
			default:
				break;
		}

//		this.finish();
	}

	@Override
	public void OnAnyChatUserInfoUpdate(int dwUserId, int dwType) {
		// TODO Auto-generated method stub
		if (dwUserId == 0 && dwType == 0) {
			BussinessCenter.getBussinessCenter().getOnlineFriendDatas();
		}
	}

	@Override
	public void OnAnyChatFriendStatus(int dwUserId, int dwStatus) {
		// TODO Auto-generated method stub
		BussinessCenter.getBussinessCenter().onUserOnlineStatusNotify(dwUserId, dwStatus);
	}
	

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	

}
