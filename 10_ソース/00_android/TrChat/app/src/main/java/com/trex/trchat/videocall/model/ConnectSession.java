package com.trex.trchat.videocall.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class ConnectSession implements Serializable {
    public static final String TAG = ConnectSession.class.getSimpleName();
    private static final long serialVersionUID = 3662628414831988679L;
    public static final int CONNECTSESSION_STATUS_IDLE = 0x00;
    public static final int CONNECTSESSION_STATUS_CALLING = 0x01;
    public static final int CONNECTSESSION_STATUS_REJECTED = 0x02;
    public static final int CONNECTSESSION_STATUS_REJECTED_RECALL = 0x03;
    public static final int CONNECTSESSION_STATUS_ACCEPTED = 0x04;
    public static final int CONNECTSESSION_STATUS_TIMEOUT = 0x05;
    public static final int CONNECTSESSION_STATUS_ENDCALL = 0x06;
    public static final int CONNECTSESSION_STATUS_CHATTING = 0x07;


    int targetUserId;
    int selfUserId;
    RoomInfo roomInfo;
    int status;

    public ConnectSession(int targetUserId, int selfUserId, int roomId, String roomPwd) {
        this.targetUserId = targetUserId;
        this.selfUserId = selfUserId;
        roomInfo = new RoomInfo(roomId, roomPwd);
        setStatus(CONNECTSESSION_STATUS_IDLE);
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setRoomInfo(RoomInfo info){
        this.roomInfo.copyInstance(info);
    }
    public RoomInfo getRoomInfo() {
        return roomInfo;
    }

    public int getTargetUserId() {
        return targetUserId;
    }

    public boolean canStartNewSession(ArrayList<ConnectSession> sessions) {
        if (sessions == null) {
            Log.e(TAG, "canStartNewSession session list is null");//illegal call (not possable)
            return false;
        }

        Log.d(TAG, "canStartNewSession newSession:" + this.toString());
        if (sessions.isEmpty()) {
            Log.d(TAG, "canStartNewSession session list is empty (return true)");
            return true;
        }
        if (!sessions.get(0).getRoomInfo().equals(this.roomInfo)) {
            return false;
        }

        for (ConnectSession s : sessions) {
            if (s.getStatus() == CONNECTSESSION_STATUS_CALLING) {
                Log.d(TAG, "canStartNewSession calling session exist :"+s.toString() +" return false");
                return false;
            }
        }
        Log.d(TAG, "canStartNewSession (return true)");
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectSession session = (ConnectSession) o;

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
                ", roomId=" + roomInfo.getId() +
                ", roomPwd='" + roomInfo.getPwd() + '\'' +
                ", status=" + status +
                '}';
    }
}
