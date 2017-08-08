package com.trex.trchat.videocall.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private static final long serialVersionUID = 5170875856740191396L;
    public static final int USERSTATUS_OFFLINE = 0;
    public static final int USERSTATUS_ONLINE = 1;
    public static final int USERINFO_NAME = 1;
    public static final int USERINFO_NICKNAME = 2;

    private int userid;
    private String username;//for login
    private String nickname;//for display
    private String ip;
    private boolean isOnline;
    private Bitmap avatar;


    public UserInfo(int userid, String username, String ip) {
        this.userid = userid;
        this.username = username;
        this.ip = ip;
        avatar = null;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserInfo that = (UserInfo) o;

        return userid == that.userid;

    }

    @Override
    public int hashCode() {
        return userid;
    }


    @Override
    public String toString() {
        return "UserItem{" +
                "userid=" + userid +
                ", username='" + username + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }


}
