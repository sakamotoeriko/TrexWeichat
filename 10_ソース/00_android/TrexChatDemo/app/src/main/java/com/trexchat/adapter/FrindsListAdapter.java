package com.trexchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.trexchat.model.Frinds;

import java.util.List;

import trexgroup.com.trexchat.R;

public class FrindsListAdapter extends BaseAdapter{
    private Context mContext;
    private List<Frinds.User> mFrinds;
    public FrindsListAdapter(Context context) {
        this.mContext = context;
        mFrinds = Frinds.loadFrinds();
    }

    public boolean hasFrind(String name){
        for (Frinds.User u:mFrinds){
            if (u.getmDisplayName().equals(name)){
                return true;
            }
        }
        return false;
    }
    public String getName(int position){
        return mFrinds.get(position).getmDisplayName();
    }
    @Override
    public int getCount() {
        return mFrinds.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null || convertView.getTag() == null){
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_frinds,null);
            vh.create(convertView);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        vh.setValue(mFrinds.get(position));
        return convertView;
    }

    class ViewHolder {
        TextView tvDispName;
        TextView tvIp;
        TextView tvPort;
        void create(View v){
            tvDispName = (TextView) v.findViewById(R.id.frinds_listitem_displayname);
            tvIp = (TextView) v.findViewById(R.id.frinds_listitem_ip);
            tvPort = (TextView) v.findViewById(R.id.frinds_listitem_port);
            v.setTag(this);
        }

        void setValue(Frinds.User user){
            tvDispName.setText(user.getmDisplayName());
            tvIp.setText(user.getmIpAddr());
            tvPort.setText(user.getmPort());
        }
    }
}
