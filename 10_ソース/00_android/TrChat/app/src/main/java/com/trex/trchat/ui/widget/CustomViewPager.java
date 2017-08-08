package com.trex.trchat.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.trex.trchat.R;


public class CustomViewPager extends ViewPager {
    public boolean scrollable = true;


    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CustomViewPager, 0, 0);
        scrollable = a.getBoolean(R.styleable.CustomViewPager_scrollable, true);
        Log.d("viewpager",""+scrollable);
    }



    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (scrollable)
            return super.onTouchEvent(ev);
        else
            return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (scrollable)
            return super.onInterceptTouchEvent(ev);
        else
            return false;

    }
}
