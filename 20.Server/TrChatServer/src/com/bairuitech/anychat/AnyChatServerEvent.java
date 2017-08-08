package com.bairuitech.anychat;

import com.bairuitech.anychat.AnyChatVerifyUserOutParam;

// 数据传输通知接口
public interface AnyChatServerEvent {
	
	// 服务器应用程序消息回调函数定义
	public void OnAnyChatServerAppMessageExCallBack(int dwNotifyMessage, int wParam, int lParam);
	// SDK定时器回调函数定义
	public void OnAnyChatTimerEventCallBack();
	
	// 用户身份验证回调函数定义
	public int OnAnyChatVerifyUserCallBack(String szUserName, String szPassword, AnyChatVerifyUserOutParam outParam);
	// 用户登录成功回调函数定义
	public void OnAnyChatUserLoginActionCallBack(int dwUserId, String szUserName, int dwLevel, String szIpAddr);
	// 用户注销回调函数定义
	public void OnAnyChatUserLogoutActionExCallBack(int dwUserId, int dwErrorCode);
	
	// 用户申请进入房间回调函数定义
	public int OnAnyChatPrepareEnterRoomCallBack(int dwUserId, int dwRoomId, String szRoomName, String szPassword);
	// 用户进入房间回调函数定义
	public void OnAnyChatUserEnterRoomActionCallBack(int dwUserId, int dwRoomId);
	// 用户离开房间回调函数定义
	public void OnAnyChatUserLeaveRoomActionCallBack(int dwUserId, int dwRoomId);
	
	// 文件传输回调函数定义
	public void OnAnyChatTransFile(int dwUserId, String szFileName, String szTempFilePath, int dwFileLength, int wParam, int lParam, int dwTaskId);
	// 透明通道数据回调函数定义
	public void OnAnyChatTransBuffer(int dwUserId, byte[] lpBuf, int dwLen);
	// 扩展透明通道数据回调函数定义
	public void OnAnyChatTransBufferEx(int dwUserId, byte[] lpBuf, int dwLen, int wParam, int lParam, int dwTaskId);
	// SDK Filter 通信数据回调函数定义
	public void OnAnyChatSDKFilterData(int dwUserId, byte[] lpBuf, int dwLen);
	
	// 收到用户文字聊天通信数据回调函数定义
	public void OnAnyChatRecvUserTextMsgCallBack(int dwRoomId, int dwSrcUserId, int dwTarUserId, int bSecret, String szTextMessage, int dwLen);
	// 服务器录像回调函数定义（扩展）
	public void OnAnyChatServerRecordExCallBack(int dwUserId, String szRecordFileName, int dwElapse, int dwFlags, int dwParam, String lpUserStr, int dwRecordServerId);
	
	// 视频通话消息通知回调函数定义
	public int OnAnyChatVideoCallEventCallBack(int dwEventType, int dwSrcUserId, int dwTarUserId, int dwErrorCode, int dwFlags, int dwParam, String lpUserStr);
	
	// 用户信息控制回调函数定义
	public int OnAnyChatUserInfoCtrlCallBack(int dwSendUserId, int dwUserId, int dwCtrlCode, int wParam, int lParam, String lpStrValue);
	
	// 业务对象事件回调函数定义
	public int OnAnyChatObjectEventCallBack(int dwObjectType, int dwObjectId, int dwEventType, int dwParam1, int dwParam2, int dwParam3, int dwParam4, String lpStrParam);
}
