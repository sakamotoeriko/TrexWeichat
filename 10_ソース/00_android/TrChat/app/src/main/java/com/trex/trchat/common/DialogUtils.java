package com.trex.trchat.common;

import android.content.Context;
import android.widget.Toast;

public class DialogUtils {
    public static void showToast(Context context,String text){
        Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
    }
}
