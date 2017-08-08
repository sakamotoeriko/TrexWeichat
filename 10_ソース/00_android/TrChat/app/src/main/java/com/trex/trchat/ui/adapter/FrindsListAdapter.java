package com.trex.trchat.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.trex.trchat.R;
import com.trex.trchat.videocall.model.UserInfo;

import java.util.List;

public class FrindsListAdapter extends BaseAdapter {

    private Context mContext;
    private List<UserInfo> mFrinds;

    public FrindsListAdapter(Context mContext, List<UserInfo> mFrinds) {
        this.mContext = mContext;
        this.mFrinds = mFrinds;
    }

    @Override
    public int getCount() {
        return mFrinds.size();
    }

    @Override
    public Object getItem(int position) {
        return mFrinds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null || convertView.getTag() == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item_frinds, null);
            vh.id = (TextView) convertView.findViewById(R.id.frindid);
            vh.name = (TextView) convertView.findViewById(R.id.frindname);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        UserInfo user = mFrinds.get(position);
        vh.id.setText("" + user.getUserid());
        vh.name.setText("" + user.getUsername());
        if (position == 0) {
            vh.id.setTextColor(Color.RED);
            vh.name.setTextColor(Color.RED);
        } else if (user.isOnline()) {
            vh.id.setTextColor(Color.GREEN);
            vh.name.setTextColor(Color.GREEN);
        } else {
            vh.id.setTextColor(Color.GRAY);
            vh.name.setTextColor(Color.GRAY);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView id;
        TextView name;
    }
}
