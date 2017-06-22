package com.trex.trchat.trexbusiness;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.trex.trchat.configs.AppSettings;
import com.trex.trchat.lib.trexsdk.TrChatCoreSdk;
import com.trex.trchat.trexbusiness.callbacks.TrChatListener;
import com.trex.trchat.trexprofile.CallProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrChatBusiness {

    public static final String TAG = TrChatBusiness.class.getSimpleName();

    static class SingletonHolder {
        static TrChatBusiness instance = new TrChatBusiness();
    }

    public static TrChatBusiness getInstance() {
        return SingletonHolder.instance;
    }


    private Context mContext;//application context
    private TrChatCoreSdk mTrChatCoreSdk;
    private LinkedSession mLinkedSession = null;
    private TrChatListener mTrChatListener = null;
    private UserItem mSelf;

    public TrChatBusiness() {
        mTrChatCoreSdk = TrChatCoreSdk.getInstance();
    }

    public void setContext(Application application) {
        this.mContext = application;
    }

    public void setListener(TrChatListener listener) {
        this.mTrChatListener = listener;
    }

    public boolean validSession(Session session) {
        if (mLinkedSession == null) {
            return true;
        }

        if (session.getRoomId() != mLinkedSession.getRoomid()) {
            return false;
        }

        return true;
    }

    public LinkedSession getLinkedSession() {
        return mLinkedSession;
    }
    private void addSession(Session session) {
        if (mLinkedSession == null) {
            mLinkedSession = new LinkedSession(new UserItem(session.getSelfUserId(), "", ""));
            mLinkedSession.setRoomid(session.getRoomId());
            mLinkedSession.setPassword(session.getRoomPwd());
        }
        mLinkedSession.addTartget(new UserItem(session.getTargetUserId(), "", ""));
    }

    public synchronized void requestVideoCall(Session session) {
        Log.d(TAG, "requestVideoCall session:" + session.toString());
        if (!validSession(session)) {
            Log.e(TAG, "requestVideoCall session illegal");
            return;
        }

        byte[] request = CallProfile.createVideoCallRequest(session.getRoomId(), session.getRoomPwd());
        mTrChatCoreSdk.transBuff(session.getTargetUserId(), request, request.length);
        session.setStatus(Session.SESSION_STATUS_CALLING);
        addSession(session);
        if (mTrChatListener != null)
            mTrChatListener.onRequested(session);
    }

    public synchronized void recieveVideoCall(Session session) {
        Log.d(TAG, "recieveVideoCall session:" + session.toString());
        if (AppSettings.getInstance().isAutoReplay()) {
            //auto replay
            if (mLinkedSession == null || mLinkedSession.getRoomid() == session.getRoomId()) {
                byte[] replay = CallProfile.createVideoCallReplay(false);
                mTrChatCoreSdk.transBuff(session.getTargetUserId(), replay, replay.length);
                addSession(session);
                if (mTrChatListener != null)
                    mTrChatListener.onRecieved(session);
            } else {
                byte[] replay = CallProfile.createVideoCallReplay(true, mLinkedSession.getRoomid(), mLinkedSession.getPassword());
                //not in the same room,replay owned roomid and wating to another request if target can join
                mTrChatCoreSdk.transBuff(session.getTargetUserId(), replay, replay.length);
            }
        } else {
            //show replay dialog
            //TODO
        }
    }

    public synchronized void revieveVideoCallReplay(Session session) {
        if (mTrChatListener != null) {
            mTrChatListener.onReplay(session);
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

//        Log.d(TAG, "updateFrindsList ids:" + Arrays.toString(friendUserIds));

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

            String name = mTrChatCoreSdk.getUserInfo(id,
                    UserItem.USERINFO_NAME);

            String ip = mTrChatCoreSdk.getUserInfo(id,
                    UserItem.USERINFO_IP);
            UserItem user = new UserItem(id, name, ip);
            mFrindsList.add(user);
        }
        Log.d(TAG, "updateFrindsList " + Arrays.toString(mFrindsList.toArray()));
        return mFrindsList;
    }
}
