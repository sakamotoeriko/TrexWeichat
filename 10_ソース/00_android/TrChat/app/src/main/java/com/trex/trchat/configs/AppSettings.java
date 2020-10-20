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
        return false;
    }

    public String getAvatarServerUrlStr(){
        return "http://157.7.165.211:5110/avatar/";
    }
}
