package com.trex.trchat.trexbusiness;

import java.io.Serializable;

public class UserItem implements Serializable {
    private static final long serialVersionUID = 5170875856740191396L;
    public static final int USERSTATUS_OFFLINE = 0;
    public static final int USERSTATUS_ONLINE = 1;
    public static final int USERINFO_NAME = 1;
    public static final int USERINFO_IP = 2;

    private int userid;
    private String username;
    private String ip;

    public UserItem(int userid, String username, String ip) {
        this.userid = userid;
        this.username = username;
        this.ip = ip;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
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

        UserItem that = (UserItem) o;

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
