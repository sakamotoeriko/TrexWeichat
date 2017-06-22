package com.trex.trchat.configs;

public class AppSettings {
    static class SingletonHolder {
        static AppSettings instance = new AppSettings();
    }

    public static AppSettings getInstance() {
        return SingletonHolder.instance;
    }

    public AppSettings() {
    }

    public boolean isAutoReplay(){
        return true;
    }
}
