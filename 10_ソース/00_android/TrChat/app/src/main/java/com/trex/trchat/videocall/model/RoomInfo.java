package com.trex.trchat.videocall.model;

import java.io.Serializable;

public class RoomInfo  implements Serializable {

    private static final long serialVersionUID = -7084736311741961976L;
    private int mId;
    private String mPwd;

    public RoomInfo(int mId, String mPwd) {
        this.mId = mId;
        this.mPwd = mPwd;
    }

    public void copyInstance(RoomInfo roomInfo) {
        if (roomInfo == null) return;
        this.mId = roomInfo.getId();
        this.mPwd = roomInfo.getPwd();
    }

    public int getId() {
        return mId;
    }

    public String getPwd() {
        return mPwd;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoomInfo session = (RoomInfo) o;

        return mId == session.getId();
    }

    @Override
    public int hashCode() {
        int result = mId;
        result = 31 * result + mPwd.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RoomInfo{" +
                "mId=" + mId +
                ", mPwd='" + mPwd + '\'' +
                '}';
    }
}
