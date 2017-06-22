package com.trex.trchat.lib.trexsdk;

import android.content.Context;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.trex.trchat.lib.trexsdk.callbacks.TrChatBaseEvent;
import com.trex.trchat.lib.trexsdk.callbacks.TrChatStateChgEvent;
import com.trex.trchat.lib.trexsdk.callbacks.TrChatTransDataEvent;
import com.trex.trchat.lib.trexsdk.callbacks.TrChatUserInfoEvent;
import com.trex.trchat.lib.trexsdk.callbacks.TrChatVideoCallEvent;
import com.trex.trchat.lib.trexsdk.common.TrChatCoreSdkBase;
import com.trex.trchat.lib.trexsdk.common.TrChatSdkExceptionFactory;
import com.bairuitech.anychat.*;

public class TrChatCoreSdk extends TrChatCoreSdkBase {

    public static final String TAG = TrChatCoreSdk.class.getSimpleName();

    static class SingletonHolder {
        static TrChatCoreSdk instance = new TrChatCoreSdk();
    }

    public static TrChatCoreSdk getInstance() {
        return SingletonHolder.instance;
    }

    public TrChatCoreSdk() {

    }

    private Context mContext = null;
    private AnyChatCoreSDK mAnyChat = null;
    public static AnyChatCameraHelper mCameraHelper = AnyChatCoreSDK.mCameraHelper;
    public static AnyChatSensorHelper mSensorHelper = AnyChatCoreSDK.mSensorHelper;
    private TrChatBaseEvent mBaseEvent = null;
    private TrChatTransDataEvent mTransDataEvent = null;
    private TrChatStateChgEvent mStateChgEvent = null;
    private TrChatUserInfoEvent mUserInfoEvent = null;
    private TrChatVideoCallEvent mVideoCallEvent = null;


    @Override
    public void setContext(Context context) {
        mContext = context;
        //invalid all instance
        mAnyChat = null;
    }

    @Override
    public void initSdk() {
        if (mContext == null) {
            throw TrChatSdkExceptionFactory.getException(TrChatSdkExceptionFactory.TRCHAT_SDK_EXCEPTION_NOCONTEXT);
        }

        if (mAnyChat == null) {
            mAnyChat = AnyChatCoreSDK.getInstance(mContext);
            mAnyChat.SetBaseEvent(mAnyChatBaseEvent);
            mAnyChat.SetTransDataEvent(mAnyChatTransDataEvent);
            mAnyChat.SetStateChgEvent(mAnyChatStateChgEvent);
            mAnyChat.SetUserInfoEvent(mAnyChatUserInfoEvent);
            mAnyChat.SetVideoCallEvent(mAnyChatVideoCallEvent);
            mAnyChat.InitSDK(android.os.Build.VERSION.SDK_INT, 0);
        }
    }

    @Override
    public void setBaseEvent(TrChatBaseEvent e) {
        valid();
        mBaseEvent = e;
    }

    @Override
    public void setTransDataEvent(TrChatTransDataEvent e) {
        valid();
        mTransDataEvent = e;
    }

    @Override
    public void setUserInfoEvent(TrChatUserInfoEvent e) {
        valid();
        mUserInfoEvent = e;
    }

    @Override
    public void setVideoCallEvent(TrChatVideoCallEvent e) {
        valid();
        mVideoCallEvent = e;
    }

    @Override
    public void setStateChgEvent(TrChatStateChgEvent e) {
        valid();
        mStateChgEvent = e;
    }

    @Override
    public void removeEvent(Object e) {
        if (mBaseEvent == e)
            mBaseEvent = null;
        if (mTransDataEvent == e)
            mTransDataEvent = null;
        if (mUserInfoEvent == e)
            mUserInfoEvent = null;
        if (mVideoCallEvent == e)
            mVideoCallEvent = null;
        if (mStateChgEvent == e)
            mStateChgEvent = null;
    }

    @Override
    public int connect(String ip, int port) {
        valid();
        return mAnyChat.Connect(ip, port);
    }

    @Override
    public int login(String username, String password) {
        valid();
        return mAnyChat.Login(username, password);
    }

    @Override
    public int release() {
        valid();
        mBaseEvent = null;
        mTransDataEvent = null;
        mUserInfoEvent = null;
        mVideoCallEvent = null;
        mStateChgEvent = null;
        mAnyChat.removeEvent(mAnyChatBaseEvent);
        mAnyChat.removeEvent(mAnyChatTransDataEvent);
        mAnyChat.removeEvent(mAnyChatUserInfoEvent);
        mAnyChat.removeEvent(mAnyChatVideoCallEvent);
        mAnyChat.removeEvent(mAnyChatStateChgEvent);
        return mAnyChat.Release();
    }

    @Override
    public int enterRoom(int roomid, String password) {
        valid();
        return mAnyChat.EnterRoom(roomid, password);
    }

    @Override
    public int leaveRoom(int roomid) {
        valid();
        return mAnyChat.LeaveRoom(roomid);
    }

    @Override
    public int[] getRoomOnlineUsers(int roomid) {
        valid();
        return mAnyChat.GetRoomOnlineUsers(roomid);
    }

    @Override
    public int transBuff(int userid, byte[] buf, int len) {
        valid();
        return mAnyChat.TransBuffer(userid, buf, len);
    }

    @Override
    public int userCameraControl(int userid, int open) {
        valid();
        return mAnyChat.UserCameraControl(userid, open);
    }

    @Override
    public int userSpeakControl(int userid, int open) {
        valid();
        return mAnyChat.UserSpeakControl(userid, open);
    }

    @Override
    public int getUserSpeakVolume(int userid) {
        valid();
        return mAnyChat.GetUserSpeakVolume(userid);
    }

    @Override
    public int getCameraState(int userid) {
        valid();
        return mAnyChat.GetCameraState(userid);
    }

    @Override
    public int getUserVideoWidth(int userid) {
        valid();
        return mAnyChat.GetUserVideoWidth(userid);
    }

    @Override
    public int getUserVideoHeight(int userid) {
        valid();
        return mAnyChat.GetUserVideoHeight(userid);
    }

    @Override
    public int bindVideo(SurfaceHolder holder) {
        valid();
        return mAnyChat.mVideoHelper.bindVideo(holder);
    }

    @Override
    public void setVideoUser(int index, int userid) {
        valid();
        mAnyChat.mVideoHelper.SetVideoUser(index, userid);
    }

    @Override
    public int getSDKOptionInt(int optname) {
        return AnyChatCoreSDK.GetSDKOptionInt(optname);
    }

    @Override
    public int setVideoPos(int userid, Surface s, int lef, int top, int right, int bottom) {
        valid();
        return mAnyChat.SetVideoPos(userid, s, lef, top, right, bottom);
    }

    @Override
    public int[] getUserFriends() {
        valid();
        return mAnyChat.GetUserFriends();
    }

    @Override
    public int getFriendStatus(int dwFriendUserId) {
        valid();
        return mAnyChat.GetFriendStatus(dwFriendUserId);
    }

    @Override
    public int[] getUserGroups() {
        valid();
        return mAnyChat.GetUserGroups();
    }

    @Override
    public int[] getGroupFriends(int dwGroupId) {
        valid();
        return mAnyChat.GetGroupFriends(dwGroupId);
    }

    @Override
    public String getUserInfo(int dwUserId, int dwInfoId) {
        valid();
        return mAnyChat.GetUserInfo(dwUserId, dwInfoId);
    }

    @Override
    public String getGroupName(int dwGroupId) {
        valid();
        return mAnyChat.GetGroupName(dwGroupId);
    }

    private int valid() throws RuntimeException {
        if (mAnyChat == null) {
            throw TrChatSdkExceptionFactory.getException(TrChatSdkExceptionFactory.TRCHAT_SDK_EXCEPTION_COREINSTANCE_NOTFIND);
        }
        return TrChatSdkExceptionFactory.TRCHAT_SDK_EXCEPTION_NOEXCEPTION;
    }

    private AnyChatBaseEvent mAnyChatBaseEvent = new AnyChatBaseEvent() {
        @Override
        public void OnAnyChatConnectMessage(boolean bSuccess) {
            Log.i(TAG, "OnAnyChatConnectMessage bSuccess:" + bSuccess + " hasObserver:" + (mBaseEvent != null));
            if (mBaseEvent != null)
                mBaseEvent.OnTrChatConnectMessage(bSuccess);
        }

        @Override
        public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
            Log.i(TAG, "OnAnyChatLoginMessage dwUserId:" + dwUserId + " dwErrorCode:" + dwErrorCode + " hasObserver:" + (mBaseEvent != null));
            if (mBaseEvent != null)
                mBaseEvent.OnTrChatLoginMessage(dwUserId, dwErrorCode);
        }

        @Override
        public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
            Log.i(TAG, "OnAnyChatEnterRoomMessage dwRoomId:" + dwRoomId + " dwErrorCode:" + dwErrorCode + " hasObserver:" + (mBaseEvent != null));
            if (mBaseEvent != null)
                mBaseEvent.OnTrChatEnterRoomMessage(dwRoomId, dwErrorCode);
        }

        @Override
        public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
            Log.i(TAG, "OnAnyChatOnlineUserMessage dwUserNum:" + dwUserNum + " dwRoomId:" + dwRoomId + " hasObserver:" + (mBaseEvent != null));
            if (mBaseEvent != null)
                mBaseEvent.OnTrChatOnlineUserMessage(dwUserNum, dwRoomId);
        }

        @Override
        public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
            Log.i(TAG, "OnAnyChatUserAtRoomMessage dwUserId:" + dwUserId + " bEnter:" + bEnter + " hasObserver:" + (mBaseEvent != null));
            if (mBaseEvent != null)
                mBaseEvent.OnTrChatUserAtRoomMessage(dwUserId, bEnter);
        }

        @Override
        public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
            Log.i(TAG, "OnAnyChatLinkCloseMessage dwErrorCode:" + dwErrorCode + " hasObserver:" + (mBaseEvent != null));
            if (mBaseEvent != null)
                mBaseEvent.OnTrChatLinkCloseMessage(dwErrorCode);
        }
    };

    private AnyChatTransDataEvent mAnyChatTransDataEvent = new AnyChatTransDataEvent() {
        @Override
        public void OnAnyChatTransFile(int dwUserid, String FileName, String TempFilePath, int dwFileLength, int wParam, int lParam, int dwTaskId) {
            if (mTransDataEvent != null)
                mTransDataEvent.OnAnyChatTransFile(dwUserid, FileName, TempFilePath, dwFileLength, wParam, lParam, dwTaskId);
        }

        @Override
        public void OnAnyChatTransBuffer(int dwUserid, byte[] lpBuf, int dwLen) {
            if (mTransDataEvent != null)
                mTransDataEvent.OnAnyChatTransBuffer(dwUserid, lpBuf, dwLen);
        }

        @Override
        public void OnAnyChatTransBufferEx(int dwUserid, byte[] lpBuf, int dwLen, int wparam, int lparam, int taskid) {
            if (mTransDataEvent != null)
                mTransDataEvent.OnAnyChatTransBufferEx(dwUserid, lpBuf, dwLen, wparam, lparam, taskid);
        }

        @Override
        public void OnAnyChatSDKFilterData(byte[] lpBuf, int dwLen) {
            if (mTransDataEvent != null)
                mTransDataEvent.OnAnyChatSDKFilterData(lpBuf, dwLen);
        }
    };

    private AnyChatStateChgEvent mAnyChatStateChgEvent = new AnyChatStateChgEvent() {

        @Override
        public void OnAnyChatMicStateChgMessage(int dwUserId, boolean bOpenMic) {
            if (mStateChgEvent != null)
                mStateChgEvent.OnAnyChatMicStateChgMessage(dwUserId, bOpenMic);
        }

        @Override
        public void OnAnyChatCameraStateChgMessage(int dwUserId, int dwState) {
            if (mStateChgEvent != null)
                mStateChgEvent.OnAnyChatCameraStateChgMessage(dwUserId, dwState);
        }

        @Override
        public void OnAnyChatChatModeChgMessage(int dwUserId, boolean bPublicChat) {
            if (mStateChgEvent != null)
                mStateChgEvent.OnAnyChatChatModeChgMessage(dwUserId, bPublicChat);
        }

        @Override
        public void OnAnyChatActiveStateChgMessage(int dwUserId, int dwState) {
            if (mStateChgEvent != null)
                mStateChgEvent.OnAnyChatActiveStateChgMessage(dwUserId, dwState);
        }

        @Override
        public void OnAnyChatP2PConnectStateMessage(int dwUserId, int dwState) {
            if (mStateChgEvent != null)
                mStateChgEvent.OnAnyChatP2PConnectStateMessage(dwUserId, dwState);
        }
    };

    private AnyChatUserInfoEvent mAnyChatUserInfoEvent = new AnyChatUserInfoEvent() {
        @Override
        public void OnAnyChatUserInfoUpdate(int dwUserId, int dwType) {
            if (mUserInfoEvent != null)
                mUserInfoEvent.OnAnyChatUserInfoUpdate(dwUserId, dwType);
        }

        @Override
        public void OnAnyChatFriendStatus(int dwUserId, int dwStatus) {
            if (mUserInfoEvent != null)
                mUserInfoEvent.OnAnyChatFriendStatus(dwUserId, dwStatus);
        }
    };

    private AnyChatVideoCallEvent mAnyChatVideoCallEvent = new AnyChatVideoCallEvent() {
        @Override
        public void OnAnyChatVideoCallEvent(int dwEventType, int dwUserId, int dwErrorCode, int dwFlags, int dwParam, String userStr) {
            if (mVideoCallEvent != null)
                mVideoCallEvent.OnAnyChatVideoCallEvent(dwEventType, dwUserId, dwErrorCode, dwFlags, dwParam, userStr);
        }
    };
}
