package com.trex.trchat.trexbusiness;

import android.content.Context;
import android.util.Log;

import com.trex.trchat.lib.trexsdk.TrChatCoreSdk;
import com.trex.trchat.videocall.VideoCallController;
import com.trex.trchat.videocall.model.UserInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrChatController {
    public static final String TAG = TrChatController.class.getSimpleName();
    private static TrChatController instance = new TrChatController();

    public static TrChatController getInstance(Context context) {
        if (instance.mContext == null)
            instance.setContext(context);
        return instance;
    }

    private TrChatController() {
        mTrChatCoreSdk = TrChatCoreSdk.getInstance();
    }

    private void setContext(Context context) {
        this.mContext = context;
        mVideoCallControler = VideoCallController.getInstance(context);
    }


    private Context mContext;//application context
    private TrChatCoreSdk mTrChatCoreSdk;
    private VideoCallController mVideoCallControler;
    private List<UserInfo> mFrindsList = new ArrayList<>();
    private UserInfo mSelf;

    public void setSelf(UserInfo self) {
        this.mSelf = self;
    }

    public VideoCallController getVideoCallController() {
        if (mVideoCallControler == null)
            mVideoCallControler = VideoCallController.getInstance(mContext);
        return mVideoCallControler;
    }

    public int getSelfUserId() {
        if (mSelf == null) {
            Log.e(TAG, "getSelfUserId self has not been set! illegal path");
        }
        return mSelf.getUserid();
    }

    public int getIdleRoomId() {
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            int[] uids = mTrChatCoreSdk.getRoomOnlineUsers(i);
            if (uids == null || uids.length == 0) {
                return i;
            }
        }
        return -1;
    }

    public static String getPassword() {
        //TODO
        return "123456789";
    }

    public List<UserInfo> updateFrindsList() {
        mFrindsList.clear();

        int[] friendUserIds = this.mTrChatCoreSdk.getUserFriends();
        if (friendUserIds == null || friendUserIds.length <= 0) {
            return mFrindsList;
        }

        for (int i = 0; i < friendUserIds.length; i++) {
            int id = friendUserIds[i];
            if (id == mSelf.getUserid()) {
                continue;
            }
            int onlineStatus = mTrChatCoreSdk.getFriendStatus(id);
            if (onlineStatus == UserInfo.USERSTATUS_OFFLINE) {
                //offline
                continue;
            }

            String name = mTrChatCoreSdk.getUserInfo(id,
                    UserInfo.USERINFO_NAME);

            String ip = mTrChatCoreSdk.getUserInfo(id,
                    UserInfo.USERINFO_IP);
            UserInfo user = new UserInfo(id, name, ip);
            mFrindsList.add(user);
        }
        mFrindsList.add(0, mSelf);

        Log.d(TAG, "updateFrindsList " + Arrays.toString(mFrindsList.toArray()));
        return mFrindsList;
    }
}
