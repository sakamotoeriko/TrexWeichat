package com.trex.trchat.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.trex.trchat.R;
import com.trex.trchat.lib.trexsdk.TrChatCoreSdk;
import com.trex.trchat.lib.trexsdk.callbacks.TrChatBaseEvent;
import com.trex.trchat.lib.trexsdk.callbacks.TrChatUserInfoEvent;
import com.trex.trchat.trexbusiness.Session;
import com.trex.trchat.trexbusiness.UserItem;
import com.trex.trchat.trexbusiness.VideoCallBusiness;
import com.trex.trchat.ui.adapter.FrindsListAdapter;


public class HomeActivity extends Activity implements TrChatBaseEvent, TrChatUserInfoEvent {

    TrChatCoreSdk mTrChatCoreSdk;
    ListView mFrindListView;
    FrindsListAdapter mFrindsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mTrChatCoreSdk = TrChatCoreSdk.getInstance();
        mTrChatCoreSdk.setBaseEvent(this);
        mTrChatCoreSdk.setUserInfoEvent(this);
        mFrindListView = (ListView) findViewById(R.id.frindlist);
        mFrindsListAdapter = new FrindsListAdapter(this, VideoCallBusiness.getInstance().updateFrindsList());
        mFrindListView.setAdapter(mFrindsListAdapter);

        mFrindListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserItem user = (UserItem) mFrindsListAdapter.getItem(position);
                VideoCallBusiness vb = VideoCallBusiness.getInstance();
                Session session = new Session(user.getUserid(), vb.getmSelf().getUserid(), vb.createIdelRoomId(), vb.getPassword());
                vb.requestVideoCall(session);
            }
        });
    }

    @Override
    public void OnTrChatConnectMessage(boolean bSuccess) {

    }

    @Override
    public void OnTrChatLoginMessage(int dwUserId, int dwErrorCode) {

    }

    @Override
    public void OnTrChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {

    }

    @Override
    public void OnTrChatOnlineUserMessage(int dwUserNum, int dwRoomId) {

    }

    @Override
    public void OnTrChatUserAtRoomMessage(int dwUserId, boolean bEnter) {

    }

    @Override
    public void OnTrChatLinkCloseMessage(int dwErrorCode) {

    }

    @Override
    public void OnAnyChatUserInfoUpdate(int dwUserId, int dwType) {
        if (dwUserId == 0 && dwType == 0) {
            VideoCallBusiness.getInstance().updateFrindsList();
            if (mFrindsListAdapter != null)
                mFrindsListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void OnAnyChatFriendStatus(int dwUserId, int dwStatus) {

    }
}
