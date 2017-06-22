package com.trex.trchat.trexbusiness;

import java.io.Serializable;

public class Session implements Serializable {
    private static final long serialVersionUID = 3662628414831988679L;
    public static final int SESSION_STATUS_IDLE = 0x00;
    public static final int SESSION_STATUS_CALLING = 0x01;
    public static final int SESSION_STATUS_REJECTED = 0x02;
    public static final int SESSION_STATUS_REJECTED_RECALL = 0x03;
    public static final int SESSION_STATUS_ACCEPTED = 0x04;
    public static final int SESSION_STATUS_CHATTING = 0x05;
    int targetUserId;
    int selfUserId;
    int roomId;
    String roomPwd;

    int status;

    public Session(int targetUserId, int selfUserId, int roomId, String roomPwd) {
        this.targetUserId = targetUserId;
        this.selfUserId = selfUserId;
        this.roomId = roomId;
        this.roomPwd = roomPwd;
        setStatus(SESSION_STATUS_IDLE);
    }

    public int getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(int targetUserId) {
        this.targetUserId = targetUserId;
    }

    public int getSelfUserId() {
        return selfUserId;
    }

    public void setSelfUserId(int selfUserId) {
        this.selfUserId = selfUserId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomPwd() {
        return roomPwd;
    }

    public void setRoomPwd(String roomPwd) {
        this.roomPwd = roomPwd;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Session session = (Session) o;

        if (targetUserId != session.targetUserId) return false;
        return selfUserId == session.selfUserId;

    }

    @Override
    public int hashCode() {
        int result = targetUserId;
        result = 31 * result + selfUserId;
        return result;
    }


    @Override
    public String toString() {
        return "Session{" +
                "targetUserId=" + targetUserId +
                ", selfUserId=" + selfUserId +
                ", roomId=" + roomId +
                ", roomPwd='" + roomPwd + '\'' +
                ", status=" + status +
                '}';
    }
}
