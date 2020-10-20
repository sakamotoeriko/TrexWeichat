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

public class MemberListAdapter extends BaseAdapter {

    public static final String TAG = MemberListAdapter.class.getSimpleName();
    private Context mContext;
    private List<UserInfo> mUsers;

    public MemberListAdapter(Context mContext, List<UserInfo> mUsers) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item_member, null);
//            vh.id = (TextView) convertView.findViewById(R.id.userid);/

            vh.img = (ImageView) convertView.findViewById(R.id.memberimg);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        UserInfo user = mUsers.get(position);
//        vh.id.setText("" + user.getUserid());

        Bitmap bitmap = user.getAvatar();
        if (bitmap != null) {
            vh.img.setImageBitmap(bitmap);
        } else {
            vh.img.setImageResource(R.drawable.noavatar);
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView img;
    }
}
