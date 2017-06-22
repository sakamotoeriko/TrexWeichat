package com.trex.trchat.trexbusiness;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.trex.trchat.configs.AppSettings;
import com.trex.trchat.lib.trexsdk.TrChatCoreSdk;
import com.trex.trchat.trexbusiness.callbacks.VideoCallBusinessEvent;
import com.trex.trchat.trexprofile.CallProfile;
import com.trex.trchat.ui.VideoActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VideoCallBusiness {
    public static final String TAG = VideoCallBusiness.class.getSimpleName();
    private ArrayList<Session> mSessionArrayList = new ArrayList<>();
    private Context mContext;//application context
    private TrChatCoreSdk mTrChatCoreSdk;
    private VideoCallBusinessEvent mVideoCallBusinessEvent;


    private UserItem mSelf;
    private int roomid = -1;

    static class SingletonHolder {
        static VideoCallBusiness instance = new VideoCallBusiness();
    }

    public static VideoCallBusiness getInstance() {
        return SingletonHolder.instance;
    }

    public VideoCallBusiness() {
        mTrChatCoreSdk = TrChatCoreSdk.getInstance();
    }

    public void setContext(Context context) {
        if (this.mContext == null) {
            this.mContext = context;
        }
    }

    public void setVideoCallBusinessEvent(VideoCallBusinessEvent event) {
        this.mVideoCallBusinessEvent = event;
    }

    public UserItem getmSelf() {
        return mSelf;
    }

    public void setmSelf(UserItem mSelf) {
        this.mSelf = mSelf;
    }

    public void setmSelf(int id, String name) {
        this.mSelf = new UserItem(id,name,"");
    }

    public int getRoomid(){
        return roomid;
    }
    private void valid() {
        if (mContext == null)
            new RuntimeException("Context has not been set");
    }

    private synchronized ArrayList<Session> addSession(Session session) {
        if (session != null) {
            mSessionArrayList.add(session);
            Log.d(TAG, "addSession size:" + mSessionArrayList.size());
            if (mSessionArrayList.size() == 1) {
                //need to Show VideoActivity
                roomid = session.getRoomId();
                Intent intent = new Intent(mContext, VideoActivity.class);
                intent.putExtra(VideoActivity.INTENT_EXTRA_SESSION, session);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } else {
                //need to add a VideoWindow to VideoActivity
                if (mVideoCallBusinessEvent != null) {
                    mVideoCallBusinessEvent.onNewSession(session);
                }
            }
        }
        return mSessionArrayList;
    }

    private ArrayList<Session> removeSession(Session session) {
        if (session != null && mSessionArrayList.contains(session)) {
            mSessionArrayList.remove(session);
        }
        return mSessionArrayList;
    }


    public synchronized void requestVideoCall(Session session) {
        Log.d(TAG,"requestVideoCall session:"+ session.toString());
        byte[] request = CallProfile.createVideoCallRequest(session.getRoomId(), session.getRoomPwd());
        mTrChatCoreSdk.transBuff(session.getTargetUserId(), request, request.length);
        session.setStatus(Session.SESSION_STATUS_CALLING);
        addSession(session);
    }

    public synchronized void recieveVideoCall(Session session) {
        Log.d(TAG,"recieveVideoCall session:"+ session.toString());
        if (AppSettings.getInstance().isAutoReplay()) {
            //auto replay
            valid();

            if (mSessionArrayList.size() == 0 || session.getRoomId() == mSessionArrayList.get(0).getRoomId()) {
                byte[] replay = CallProfile.createVideoCallReplay(false);
                mTrChatCoreSdk.transBuff(session.getTargetUserId(), replay, replay.length);
                addSession(session);
            } else {
                Session chattingSession = mSessionArrayList.get(0);
                byte[] replay = CallProfile.createVideoCallReplay(true, chattingSession.getRoomId(), chattingSession.getRoomPwd());
                mTrChatCoreSdk.transBuff(session.getTargetUserId(), replay, replay.length);
                //not in the same room,replay owned roomid and wating to another request if target can join
            }


        } else {
            //show replay dialog
            //TODO
        }
    }

    public synchronized void revieveVideoCallReplay(Session session) {
        if (mVideoCallBusinessEvent != null) {
            mVideoCallBusinessEvent.onReplay(session);
        }
    }

    public int createIdelRoomId() {
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


    private List<UserItem> mFrindsList = new ArrayList<>();

    public List<UserItem> updateFrindsList() {
        mFrindsList.clear();

        int[] friendUserIds = this.mTrChatCoreSdk.getUserFriends();
        if (friendUserIds == null || friendUserIds.length <= 0) {
            return mFrindsList;
        }

        Log.d(TAG,"updateFrindsList ids:"+ Arrays.toString(friendUserIds));

        for (int i = 0; i < friendUserIds.length; i++) {
            int id = friendUserIds[i];
            if (id == mSelf.getUserid()) {
                continue;
            }
            int onlineStatus = mTrChatCoreSdk.getFriendStatus(id);
            if (onlineStatus == UserItem.USERSTATUS_OFFLINE) {
                //offline
                continue;
            }

            String nickName = mTrChatCoreSdk.getUserInfo(id,
                    UserItem.USERINFO_NAME);

            String ip = mTrChatCoreSdk.getUserInfo(id,
                    UserItem.USERINFO_IP);
            UserItem user = new UserItem(id,nickName,ip);


            mFrindsList.add(user);
        }
        return mFrindsList;
    }
}
