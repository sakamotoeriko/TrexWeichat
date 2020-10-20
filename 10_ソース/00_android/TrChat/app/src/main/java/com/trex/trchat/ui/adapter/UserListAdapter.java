package com.trex.trchat.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trex.trchat.R;
import com.trex.trchat.videocall.model.UserInfo;

import java.util.List;

public class UserListAdapter extends BaseAdapter {

    public static final String TAG = UserListAdapter.class.getSimpleName();
    private Context mContext;
    private List<UserInfo> mUsers;

    public UserListAdapter(Context mContext, List<UserInfo> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @Override
    public int getCount() {
        Log.d(TAG, "getCount :" + mUsers.size());
        return mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView position:" + position);
        ViewHolder vh;
        if (convertView == null || convertView.getTag() == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item_user, null);
//            vh.id = (TextView) convertView.findViewById(R.id.userid);/
            vh.name = (TextView) convertView.findViewById(R.id.username);
            vh.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            vh.online = (TextView) convertView.findViewById(R.id.online);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        UserInfo user = mUsers.get(position);
//        vh.id.setText("" + user.getUserid());
        vh.name.setText("" + user.getNickname());
        Bitmap bitmap = user.getAvatar();
        if (bitmap != null) {
            vh.avatar.setImageBitmap(bitmap);
        }
        if (user.isOnline()) {
            vh.online.setTextColor(Color.GREEN);
            vh.online.setText("online");
        } else {
            vh.online.setTextColor(Color.RED);
            vh.online.setText("offline");
        }
        return convertView;
    }

    private class ViewHolder {
        //        TextView id;
        TextView name;
        TextView online;
        ImageView avatar;
    }
}
