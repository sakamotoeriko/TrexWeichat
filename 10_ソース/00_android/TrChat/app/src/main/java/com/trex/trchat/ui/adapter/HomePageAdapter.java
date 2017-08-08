package com.trex.trchat.ui.adapter;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.trex.trchat.ui.home.ChatFragment;
import com.trex.trchat.ui.home.HomeFragment;
import com.trex.trchat.ui.home.SettingsFragment;

import java.util.ArrayList;
import java.util.List;

public class HomePageAdapter extends FragmentPagerAdapter {
    public static final String TAG = HomePageAdapter.class.getSimpleName();
    List<Fragment> mFragments;

    public HomePageAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new ArrayList<>();
        mFragments.add(HomeFragment.newInstance());
        mFragments.add(ChatFragment.newInstance());
        mFragments.add(SettingsFragment.newInstance());
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public void startUpdate(ViewGroup container) {
        Log.d(TAG, "startUpdate");
        super.startUpdate(container);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d(TAG, "instantiateItem pos:" + position);
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.d(TAG, "destroyItem pos:" + position);
        super.destroyItem(container, position, object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Log.d(TAG, "setPrimaryItem pos:" + position);
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        Log.d(TAG, "finishUpdate");
        super.finishUpdate(container);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        Log.d(TAG, "isViewFromObject");
        return super.isViewFromObject(view, object);
    }

    @Override
    public Parcelable saveState() {
        Log.d(TAG, "saveState");
        return super.saveState();
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        Log.d(TAG, "restoreState");
        super.restoreState(state, loader);
    }

    @Override
    public long getItemId(int position) {
        Log.d(TAG, "getItemId");
        return super.getItemId(position);
    }
}
