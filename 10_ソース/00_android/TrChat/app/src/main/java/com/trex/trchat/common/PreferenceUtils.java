package com.trex.trchat.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PreferenceUtils {
    private static final String KEY_USERNAME = "KEY_USERNAME";
    private static final String KEY_USERPWD = "KEY_USERPWD";
    public static final String TAG = PreferenceUtils.class.getSimpleName();

    public static String[] getLoginInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        String n = sp.getString(KEY_USERNAME, "");
        String p = sp.getString(KEY_USERPWD, "");
        String[] ret = {n, p};
        return ret;
    }

    public static void setLoginInfo(Context context, String name, String pwd) {
        Log.d(TAG, "setLoginInfo name:" + name + " pwd:" + pwd);
        if (context == null) {
            Log.e(TAG, "setLoginInfo context==null");
            return;
        }
        if (name == null || name.equals("")) {
            Log.e(TAG, "setLoginInfo name==null");
            return;
        }
        if (pwd == null || pwd.equals("")) {
            Log.e(TAG, "setLoginInfo pwd==null");
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_USERNAME, name);
        editor.putString(KEY_USERPWD,pwd);

        editor.apply();
    }
}
