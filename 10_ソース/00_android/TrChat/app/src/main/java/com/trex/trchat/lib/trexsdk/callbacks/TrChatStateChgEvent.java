package com.trex.trchat.lib.trexsdk.callbacks;

public interface TrChatStateChgEvent {
    // 用户的音频设备状态变化消息，dwUserId表示用户ID号，bOpenMic表示该用户是否已打开音频采集设备
    void OnAnyChatMicStateChgMessage(int dwUserId, boolean bOpenMic);

    // 用户摄像头状态改变消息，dwUserId表示用户ID号，dwState表示摄像头的当前状态
    void OnAnyChatCameraStateChgMessage(int dwUserId, int dwState);

    // 用户聊天模式改变消息，dwUserId表示用户ID号，bPublicChat表示用户的当前聊天模式
    void OnAnyChatChatModeChgMessage(int dwUserId, boolean bPublicChat);

    // 用户活动状态发生变化消息，dwUserId表示用户ID号，dwState表示用户的当前活动状态
    void OnAnyChatActiveStateChgMessage(int dwUserId, int dwState);

    // 本地用户与其它用户的P2P网络连接状态发生变化，dwUserId表示其它用户ID号，dwState表示本地用户与其它用户的当前P2P网络连接状态
    void OnAnyChatP2PConnectStateMessage(int dwUserId, int dwState);
    // 用户视频分辨率改变消息
    //	public void OnAnyChatVideoSizeChgMessage(int dwUserId, int dwWidth, int dwHeight);
}
