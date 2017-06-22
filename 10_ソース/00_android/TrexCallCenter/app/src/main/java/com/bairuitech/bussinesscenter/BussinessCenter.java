package com.bairuitech.bussinesscenter;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.bairuitech.callcenter.R;
import com.bairuitech.callcenter.VideoActivity;
import com.bairuitech.util.BaseConst;
import com.bairuitech.util.BaseMethod;
import com.bairuitech.util.ScreenInfo;

public class BussinessCenter{

	public static final String TAG = BussinessCenter.class.getSimpleName();
	public static AnyChatCoreSDK anychat;
	private static BussinessCenter mBussinessCenter;
	private MediaPlayer mMediaPlaer;
	public static SessionItem sessionItem;
	public static SessionItem sessionItemExtention;//
	public static ScreenInfo mScreenInfo;
	public static Activity mContext;
	public static ArrayList<UserItem> mOnlineFriendItems;
	public static ArrayList<Integer> mOnlineFriendIds;

	public static int selfUserId;
	public static boolean bBack = false;// 程序是否在后台
	public static String selfUserName;

	private BussinessCenter() {
		initParams();
	}

	public static BussinessCenter getBussinessCenter() {
		if (mBussinessCenter == null)
			mBussinessCenter = new BussinessCenter();
		return mBussinessCenter;
	}

	private void initParams() {
		anychat = AnyChatCoreSDK.getInstance(mContext);
		mOnlineFriendItems = new ArrayList<UserItem>();
		mOnlineFriendIds = new ArrayList<Integer>();
	}

	/***
	 * 播放接收到呼叫音乐提示
	 * @param context	上下文    
	 */
	private void playCallReceivedMusic(Context context) {
		mMediaPlaer = MediaPlayer.create(context, R.raw.call);
		mMediaPlaer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mMediaPlaer.start();
			}
		});
		mMediaPlaer.start();
	}

	/***
	 * 停止播放
	 */
	public void stopSessionMusic() {
		if (mMediaPlaer == null)
			return;
		try {
			mMediaPlaer.pause();
			mMediaPlaer.stop();
			mMediaPlaer.release();
			mMediaPlaer = null;
		} catch (Exception e) {
			Log.i("media-stop", "er");
		}
	}

	/***
	 * @param userId 用户id
	 * @param status 用户在线状态，1是上线，0是下线  
	 */
	public void onUserOnlineStatusNotify(int userId, int status) {
		// TODO Auto-generated method stub
		String strMsg = "";
		UserItem userItem = getUserItemByUserId(userId);
		if (userItem == null)
			return;
		if (status == UserItem.USERSTATUS_OFFLINE) {
			if (mOnlineFriendIds.indexOf(userId) >= 0) {
				mOnlineFriendItems.remove(userItem);
				mOnlineFriendIds.remove((Object) userId);
			}
			strMsg = userItem.getUserName() + "下线";
		} else {
			strMsg = userItem.getUserName() + "上线";
		}
		if (mContext != null)
			BaseMethod.showToast(strMsg, mContext);
	}

	public void realse() {
		anychat = null;
		mOnlineFriendItems = null;
		mMediaPlaer = null;
		mScreenInfo = null;
		mContext = null;
		mBussinessCenter = null;
	}

	public void realseData() {
		mOnlineFriendItems.clear();
		mOnlineFriendIds.clear();
	}

	/***
	 * 发送呼叫事件
	 * @param dwEventType	视频呼叫事件类型       
	 * @param dwUserId		目标userid       
	 * @param dwErrorCode	出错代码       
	 * @param dwFlags		功能标志        
	 * @param dwParam		自定义参数，传给对方        
	 * @param szUserStr		自定义参数，传给对方         
	 */
	public static void VideoCallControl(int dwEventType, int dwUserId, int dwErrorCode, int dwFlags, int dwParam, String szUserStr) {
		anychat.VideoCallControl(dwEventType, dwUserId, dwErrorCode, dwFlags, dwParam, szUserStr);
	}

	public void onVideoCallRequest(int dwUserId, int dwFlags,
			int dwParam, String szUserStr) {
		// TODO Auto-generated method stub
		playCallReceivedMusic(mContext);
		// 如果程序在后台，通知有呼叫请求
		if (bBack) {
			UserItem item = getUserItemByUserId(dwUserId);
			Bundle bundle = new Bundle();
			if (item != null) {
				bundle.putString("USERNAME", item.getUserName()	+ mContext.getString(R.string.sessioning_reqite));
			} else {
				bundle.putString("USERNAME", "some one call you");
			}
			bundle.putInt("USERID", dwUserId);
			BaseMethod.sendBroadCast(mContext, BaseConst.ACTION_BACK_EQUESTSESSION, bundle);
		}
	}

	public void onVideoCallReply(int dwUserId, int dwErrorCode,
			int dwFlags, int dwParam, String szUserStr) {
		// TODO Auto-generated method stub
		String strMessage = null;
		switch (dwErrorCode) {
		case AnyChatDefine.BRAC_ERRORCODE_SESSION_BUSY:
			strMessage = mContext.getString(R.string.str_returncode_bussiness);
			break;
		case AnyChatDefine.BRAC_ERRORCODE_SESSION_REFUSE:
			strMessage = mContext.getString(R.string.str_returncode_requestrefuse);
			break;
		case AnyChatDefine.BRAC_ERRORCODE_SESSION_OFFLINE:
			strMessage = mContext.getString(R.string.str_returncode_offline);
			break;
		case AnyChatDefine.BRAC_ERRORCODE_SESSION_QUIT:
			strMessage = mContext.getString(R.string.str_returncode_requestcancel);
			break;
		case AnyChatDefine.BRAC_ERRORCODE_SESSION_TIMEOUT:
			strMessage = mContext.getString(R.string.str_returncode_timeout);
			break;
		case AnyChatDefine.BRAC_ERRORCODE_SESSION_DISCONNECT:
			strMessage = mContext.getString(R.string.str_returncode_disconnect);
			break;
		case AnyChatDefine.BRAC_ERRORCODE_SUCCESS:
			break;
		default:
			break;
		}
		if (strMessage != null) {
			BaseMethod.showToast(strMessage, mContext);
			// 如果程序在后台，通知通话结束
			if (bBack) {
				Bundle bundle = new Bundle();
				bundle.putInt("USERID", dwUserId);
				BaseMethod.sendBroadCast(mContext,
						BaseConst.ACTION_BACK_CANCELSESSION, null);
			}
			stopSessionMusic();
		}
	}

	public void onVideoCallStart(int dwUserId, int dwFlags, int dwParam,
			String szUserStr) {
		// TODO Auto-generated method stub
		stopSessionMusic();
		sessionItem = new SessionItem(dwFlags, selfUserId, dwUserId);
		sessionItem.setRoomId(dwParam);
		sessionItem.setRoomPwd(szUserStr);
		Intent intent = new Intent();
		intent.setClass(mContext, VideoActivity.class);
		mContext.startActivity(intent);
	}

	public void onVideoCallEnd(int dwUserId, int dwFlags, int dwParam,
			String szUserStr) {
		// TODO Auto-generated method stub
		if (dwUserId == sessionItem.targetUserId){
			sessionItem = null;
		}else if (dwUserId == sessionItemExtention.targetUserId){
			sessionItemExtention = null;
		}

	}

	public void onSecondVideoCallStart(int dwUserId, int dwFlags, int dwParam,
									   String szUserStr){

		sessionItemExtention = new SessionItem(dwFlags, selfUserId, dwUserId);
		sessionItemExtention.setRoomId(dwParam);
		sessionItemExtention.setRoomPwd(szUserStr);

		Intent broadcastIntent = new Intent();
		broadcastIntent.setPackage(VideoActivity.class.getPackage().getName());
		broadcastIntent.setAction(VideoActivity.BROADCAST_NEWSESSION);

		broadcastIntent.putExtra("",sessionItemExtention.getBundle());
		mContext.startActivity(broadcastIntent);
	}

	/***
	 * 通过用户id获取用户对象
	 * 
	 * @param userId
	 *            用户id
	 * @return
	 */
	public UserItem getUserItemByUserId(int userId) {

		int size = mOnlineFriendItems.size();
		for (int i = 0; i < size; i++) {
			UserItem userItem = mOnlineFriendItems.get(i);
			if (userItem != null && userItem.getUserId() == userId) {
				return userItem;
			}
		}
		return null;
	}

	public UserItem getUserItemByIndex(int index) {
		try {
			return mOnlineFriendItems.get(index);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	/***
	 * 获取好友数据
	 */
	public void getOnlineFriendDatas() {
		mOnlineFriendItems.clear();
		mOnlineFriendIds.clear();
		// 获取本地ip
		String ip = anychat.QueryUserStateString(-1,
				AnyChatDefine.BRAC_USERSTATE_LOCALIP);
		UserItem userItem = new UserItem(selfUserId, selfUserName, ip);
		// 获取用户好友userid列表
		int[] friendUserIds = anychat.GetUserFriends();
		int friendUserId = 0;
		mOnlineFriendItems.add(userItem);
		mOnlineFriendIds.add(selfUserId);
		if (friendUserIds == null)
			return;
		for (int i = 0; i < friendUserIds.length; i++) {
			friendUserId = friendUserIds[i];
			int onlineStatus = anychat.GetFriendStatus(friendUserId);
			if (onlineStatus == UserItem.USERSTATUS_OFFLINE) {
				continue;
			}
			userItem = new UserItem();
			userItem.setUserId(friendUserId);
			// 获取好友昵称
			String nickName = anychat.GetUserInfo(friendUserId,
					UserItem.USERINFO_NAME);
			if (nickName != null)
				userItem.setUserName(nickName);
			// 获取好友IP地址
			String strIp = anychat.GetUserInfo(friendUserId,
					UserItem.USERINFO_IP);
			if (strIp != null)
				userItem.setIp(strIp);
			mOnlineFriendItems.add(userItem);
			mOnlineFriendIds.add(friendUserId);
		}
	}

	public static int getRoomId(){
		for (int i = 0 ;i < Integer.MAX_VALUE; i++){
			int[] uids = anychat.GetRoomOnlineUsers(i);
			Log.d(TAG,"getRoomId room:"+i+" uids:"+ Arrays.toString(uids));
			if (uids==null || uids.length==0){
				return i;
			}
		}
		return -1;
	}

	public static String getPassword(){


		return "123456789";
	}


	public static final byte TRANSBUF_PRE1 = (byte) 0xff;
	public static final byte TRANSBUF_PRE2 = (byte) 0x00;
	public static final byte TRANSBUF_VIDEOCALL = (byte) 0xAE;
	public static final byte TRANSBUF_VIDEOCALL_REQUEST = (byte) 0x01;
	public static final byte TRANSBUF_VIDEOCALL_REPLAY = (byte) 0x02;

	public void requestAnotherCall(int userId,int roomId,String pwd){
		byte[] pwdbuf = pwd.getBytes(Charset.defaultCharset());
		byte roomidbuf = (byte)roomId;
		byte[] prebuf = {TRANSBUF_PRE1,TRANSBUF_PRE2,TRANSBUF_VIDEOCALL,TRANSBUF_VIDEOCALL_REQUEST};
		byte[] buf = new byte[pwdbuf.length+1+prebuf.length];

		System.arraycopy(prebuf,0,buf,0,prebuf.length);
		buf[4] = roomidbuf;
		System.arraycopy(pwdbuf,0,buf,5,pwdbuf.length);

		anychat.TransBuffer(userId,buf,buf.length);
	}

	public void replayAnotherCall(int userId,boolean reject){

		byte[] prebuf = {TRANSBUF_PRE1,TRANSBUF_PRE2,TRANSBUF_VIDEOCALL,TRANSBUF_VIDEOCALL_REPLAY};
		byte[] buf = new byte[prebuf.length+1];

		System.arraycopy(prebuf,0,buf,0,prebuf.length);
		buf[buf.length-1] = reject?(byte)0x01:(byte)0x00;
		anychat.TransBuffer(userId,buf,buf.length);
	}

	public void onTransBuffer(int dwUserid, byte[] lpBuf, int dwLen){
		Log.d(TAG, "onTransBuffer dwUserid:" + dwUserid + " dwLen:" + dwLen + " lpBug:" + Arrays.toString(lpBuf));

		if (lpBuf[0] == TRANSBUF_PRE1 &&
				lpBuf[1] == TRANSBUF_PRE2 &&
				lpBuf[2] == TRANSBUF_VIDEOCALL ){

			if (lpBuf[3] == TRANSBUF_VIDEOCALL_REPLAY){
				onReplayFromAnotherCall(dwUserid,lpBuf);
			}else if (lpBuf[3] == TRANSBUF_VIDEOCALL_REQUEST){
				onRequestFromAnotherCall(dwUserid,lpBuf);
			}
		}

	}
	public void onRequestFromAnotherCall(int userId,byte[] buf){
		// TODO Check param
		byte[] prebuf = new byte[4];
		byte roomid  = buf[4];
		byte[] pwdbuf = new byte[buf.length -5];

		System.arraycopy(buf,0,prebuf,0,prebuf.length);
		System.arraycopy(buf,5,pwdbuf,0,pwdbuf.length);

		if (prebuf[0] == TRANSBUF_PRE1 &&
				prebuf[1] == TRANSBUF_PRE2 &&
				prebuf[2] == TRANSBUF_VIDEOCALL &&
				prebuf[3] == TRANSBUF_VIDEOCALL_REQUEST){
			String pwd = pwdbuf.toString();
			Log.d(TAG, "onRequestFromAnotherCall roomid:" + roomid + "pwd:" + pwd);
			replayAnotherCall(userId,false);//TODO temp replay the call
			sessionItem = new SessionItem(0, selfUserId, userId);
			sessionItem.setRoomId(roomid);
			sessionItem.setRoomPwd(pwd);
			Intent intent = new Intent();
			intent.setClass(mContext, VideoActivity.class);
			mContext.startActivity(intent);
		}
	}

	public void onReplayFromAnotherCall(int userId,byte[] buf){
		// TODO Check param
		byte reject  = buf[4];

		if (buf[0] == TRANSBUF_PRE1 &&
				buf[1] == TRANSBUF_PRE2 &&
				buf[2] == TRANSBUF_VIDEOCALL &&
				buf[3] == TRANSBUF_VIDEOCALL_REPLAY) {

			if (reject==(byte)0x00) {
				//replay
				sessionItemExtention = new SessionItem(0, selfUserId, userId);
				sessionItemExtention.setRoomId(sessionItem.roomId);
				sessionItemExtention.setRoomPwd(sessionItem.roomPwd);

				Intent broadcastIntent = new Intent();
				broadcastIntent.setPackage(VideoActivity.class.getPackage().getName());
				broadcastIntent.setAction(VideoActivity.BROADCAST_NEWSESSION);

				broadcastIntent.putExtra("", sessionItemExtention.getBundle());
				mContext.sendBroadcast(broadcastIntent);
			}
		}
	}
}
