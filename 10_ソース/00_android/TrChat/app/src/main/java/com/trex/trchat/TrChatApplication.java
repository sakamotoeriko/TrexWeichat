package com.trex.trchat;

import android.app.Application;
import android.content.Intent;

import com.trex.trchat.resident.ResidentService;

public class TrChatApplication extends Application {
    @Override
    public void onCreate() {
        startService(new Intent(this,ResidentService.class));
        super.onCreate();
    }
}
