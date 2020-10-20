package com.bairuitech.bussinesscenter;

import android.os.Bundle;

/**
 * 
 * @author liaobin
 * @version 1.0
 * @date 2013-12-30
 *
 */
public class SessionItem  {

	public int sourceUserId;		// 通话发起者userid
	public int targetUserId;		// 通话目标userid
	public int roomId;				// 通话房间Id
	public String roomPwd;			// 通话房间pwd
	public int sessionStatus;		// 通话状态
	public int sessionType;			// 通话类型
	public SessionItem(int sessionType,int sUserId,int tUserId)
	{
		this.sessionType=sessionType;
		this.sourceUserId=sUserId;
		this.targetUserId=tUserId;
	}
	public int getSourceUserId() {
		return sourceUserId;
	}
	public void setSourceUserId(int sourceUserId) {
		this.sourceUserId = sourceUserId;
	}
	public int getTargetUserId() {
		return targetUserId;
	}
	public void setTargetUserId(int targetUserId) {
		this.targetUserId = targetUserId;
	}
	public int getRoomId() {
		return roomId;
	}
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	public String getRoomPwd(){
		return roomPwd;
	}
	public void setRoomPwd(String pwd){
		this.roomPwd = pwd;
	}
	public int getSessionStatus() {
		return sessionStatus;
	}
	public void setSessionStatus(int sessionStatus) {
		this.sessionStatus = sessionStatus;
	}
	public int getSessionType() {
		return sessionType;
	}
	public void setSessionType(int sessionType) {
		this.sessionType = sessionType;
	}
	
	/***
	 * 获取通话中另外一方的UserId
	 * @param selfUserId 自己的用户Id
	 * @return 通话中另外一方的UserId
	 */
	public int getPeerUserItem(int selfUserId)
	{
		if(sourceUserId==selfUserId)
		{
			return targetUserId;
		}
		else if(targetUserId==selfUserId)
		{
			return sourceUserId;
		}
		return 0;
		
	}
	

	private static final String BUNDLE_SOURCE_USRID = "BUNDLE_SOURCE_USRID";
	private static final String BUNDLE_TARGET_USRID = "BUNDLE_TARGET_USRID";
	private static final String BUNDLE_ROOMID = "BUNDLE_ROOMID";
	private static final String BUNDLE_ROOMPWD = "BUNDLE_ROOMPWD";
	private static final String BUNDLE_SESSION_STATUS = "BUNDLE_SESSION_STATUS";
	private static final String BUNDLE_SESSION_TYPE = "BUNDLE_SESSION_TYPE";

	public Bundle getBundle(){
		Bundle b = new Bundle();

		b.putInt(BUNDLE_SOURCE_USRID,sourceUserId);
		b.putInt(BUNDLE_TARGET_USRID,targetUserId);
		b.putInt(BUNDLE_ROOMID,roomId);
		b.putString(BUNDLE_ROOMPWD,roomPwd);
		b.putInt(BUNDLE_SESSION_STATUS,sessionStatus);
		b.putInt(BUNDLE_SESSION_TYPE,sessionType);

		return b;
	}

	public static SessionItem getSessionFromBundle(Bundle bundle){
		if (bundle == null){
			return null;
		}
		int sUsrid = bundle.getInt(BUNDLE_SOURCE_USRID);
		int tUsrid = bundle.getInt(BUNDLE_TARGET_USRID);
		int roomid = bundle.getInt(BUNDLE_ROOMID);
		String roompwd = bundle.getString(BUNDLE_ROOMPWD);
		int sessionStatus = bundle.getInt(BUNDLE_SESSION_STATUS);
		int sessionType = bundle.getInt(BUNDLE_SESSION_TYPE);

		SessionItem si = new SessionItem(sessionType,sUsrid,tUsrid);
		si.setRoomId(roomid);
		si.setRoomPwd(roompwd);
		si.setSessionStatus(sessionStatus);
		return si;
	}
}
