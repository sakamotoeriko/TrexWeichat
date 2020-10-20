package com.trex.trchat.resident;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ResidentReceiver extends BroadcastReceiver {
    public ResidentReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context,ResidentService.class);
        context.startService(startServiceIntent);
    }
}
