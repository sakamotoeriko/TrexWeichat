package com.trex.trchat.lib.trexsdk.common;

import android.content.Context;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.trex.trchat.lib.trexsdk.callbacks.TrChatBaseEvent;
import com.trex.trchat.lib.trexsdk.callbacks.TrChatStateChgEvent;
import com.trex.trchat.lib.trexsdk.callbacks.TrChatTransDataEvent;
import com.trex.trchat.lib.trexsdk.callbacks.TrChatUserInfoEvent;
import com.trex.trchat.lib.trexsdk.callbacks.TrChatVideoCallEvent;

public abstract class TrChatCoreSdkBase {
    public abstract void setContext(Context context);

    public abstract void initSdk() throws Exception;

    public abstract void setBaseEvent(TrChatBaseEvent e);

    public abstract void setTransDataEvent(TrChatTransDataEvent e);

    public abstract void setUserInfoEvent(TrChatUserInfoEvent e);

    public abstract void setVideoCallEvent(TrChatVideoCallEvent e);

    public abstract void setStateChgEvent(TrChatStateChgEvent e);

    public abstract void removeEvent(Object e);

    public abstract int connect(String ip, int port);

    public abstract int login(String username, String password);

    public abstract int release();

    public abstract int enterRoom(int roomid, String password);

    public abstract int leaveRoom(int roomid);

    public abstract int[] getRoomOnlineUsers(int roomid);
    
    public abstract int transBuff(int userid, byte[] buf, int len);

    public abstract int userCameraControl(int userid, int open);

    public abstract int userSpeakControl(int userid, int open);

    public abstract int getUserSpeakVolume(int userid);

    public abstract int getCameraState(int userid);

    public abstract int getUserVideoWidth(int userid);

    public abstract int getUserVideoHeight(int userid);

    public abstract int bindVideo(SurfaceHolder holder);

    public abstract void setVideoUser(int index, int userid);

    public abstract int getSDKOptionInt(int optname);

    public abstract int setVideoPos(int userid, Surface s, int lef, int top, int right, int bottom);


    //    获取用户好友ID列表
    public abstract int[] getUserFriends();
    // 获取好友在线状态
    public abstract int getFriendStatus(int dwFriendUserId);
    // 获取用户分组ID列表
    public abstract int[] getUserGroups();
    // 获取分组下面的好友列表
    public abstract int[] getGroupFriends(int dwGroupId);
    // 获取用户信息
    public abstract String getUserInfo(int dwUserId, int dwInfoId);
    // 获取用户分组名称
    public abstract String getGroupName(int dwGroupId);
}
