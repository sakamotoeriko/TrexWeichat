package com.trex.trchat.trexbusiness;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.widget.Adapter;
import android.widget.BaseAdapter;

import com.trex.trchat.R;
import com.trex.trchat.configs.AppSettings;
import com.trex.trchat.lib.trexsdk.TrChatCoreSdk;
import com.trex.trchat.videocall.VideoCallController;
import com.trex.trchat.videocall.model.UserInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrChatController {
    public static final String TAG = TrChatController.class.getSimpleName();
    private static TrChatController instance = new TrChatController();
    private MediaPlayer mMediaPlaer;
    Handler mHandler;

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
        mHandler = new Handler();
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

    public List<UserInfo> getFrindsList() {
        return mFrindsList;
    }

    public List<UserInfo> updateFrindsList(BaseAdapter adapter) {
//        mFrindsList.clear();

        int[] friendUserIds = this.mTrChatCoreSdk.getUserFriends();
        if (friendUserIds == null || friendUserIds.length <= 0) {
            return mFrindsList;
        }

        Log.d(TAG, "updateFrindsList  friendUserIds" + Arrays.toString(friendUserIds));
        for (int i = 0; i < friendUserIds.length; i++) {
            int id = friendUserIds[i];

            String name = mTrChatCoreSdk.getUserInfo(id,
                    UserInfo.USERINFO_NAME);

            String nickname = mTrChatCoreSdk.getUserInfo(id,
                    UserInfo.USERINFO_NICKNAME);
            int onlineStatus = mTrChatCoreSdk.getFriendStatus(id);

            boolean isNew = true;
            for (UserInfo user : mFrindsList) {
                if (user.getUserid() == id) {
                    isNew = false;
                    user.setIp("");
                    user.setUsername(name);
                    user.setNickname(nickname);
                    user.setOnline(onlineStatus == UserInfo.USERSTATUS_ONLINE);
                    updateAvatar(adapter, user);
                    if (user.getAvatar() == null) {
                        user.setAvatar(((BitmapDrawable) mContext.getDrawable(R.drawable.noavatar)).getBitmap());
                    }
                    break;
                }
            }
            if (isNew && id != mSelf.getUserid()) {
                UserInfo user = new UserInfo(id, name, "");
                user.setNickname(nickname);
                user.setOnline(onlineStatus == UserInfo.USERSTATUS_ONLINE);
                user.setAvatar(((BitmapDrawable) mContext.getDrawable(R.drawable.noavatar)).getBitmap());
                updateAvatar(adapter, user);
                mFrindsList.add(user);
            }

        }
        Log.d(TAG, "updateFrindsList  mFrindsList" + Arrays.toString(mFrindsList.toArray()));

        adapterNotifyDataChange(adapter);
        return mFrindsList;
    }

    public void updateAvatar(final BaseAdapter adapter, final UserInfo user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap avatar = getBitmapFromURL(String.valueOf(user.getUserid()));
                if (avatar == null) {
                    Log.d(TAG, "updateFrindsList failed");
                    user.setAvatar(((BitmapDrawable) mContext.getDrawable(R.drawable.noavatar)).getBitmap());
                } else {
                    Log.d(TAG, "updateFrindsList success");
                    user.setAvatar(avatar);
                }
                Log.d(TAG, "updateFrindsList notifyDataSetChanged");
                adapterNotifyDataChange(adapter);
            }
        }).start();
    }

    @WorkerThread
    public Bitmap getBitmapFromURL(String userid) {
        Log.d(TAG, "getBitmapFromURL userid:" + userid);
        if (userid == null || userid.equals("")) {
            return null;
        }
        try {
            String serverUrl = AppSettings.getInstance().getAvatarServerUrlStr();
            java.net.URL url = new java.net.URL(serverUrl + userid + "/");
            Log.d(TAG, "getBitmapFromURL url:" + url.toString());

            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.d(TAG, "getBitmapFromURL bitmap:" + myBitmap.getByteCount());
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void adapterNotifyDataChange(final BaseAdapter adapter) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }


    public void playCallReceivedMusic(Context context) {
        mMediaPlaer = MediaPlayer.create(context, R.raw.call);
        mMediaPlaer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mMediaPlaer.start();
            }
        });
        mMediaPlaer.start();
    }
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

}
