package com.trex.trchat.videocall;

import com.trex.trchat.common.Utils;
import com.trex.trchat.videocall.model.ConnectSession;
import com.trex.trchat.videocall.model.RoomInfo;

public class VideoCallProtocol {

    /*
    request:
    ---------|--------------|------------|------------|---------------|
    pre:2byte|function:1byte|Action:1byte|roomid:2byte|password:n byte|
    ---------|--------------|------------|------------|---------------|
     */

    /*
    replay:
    ---------|--------------|------------|--------------| |------------|---------------|
    pre:2byte|function:1byte|Action:1byte|isReject:1byte| |roomid:2byte|password:n byte|
    ---------|--------------|------------|--------------| |------------|---------------|
     */

    /*
    endcall:
    ---------|--------------|------------| |------------|
    pre:2byte|function:1byte|Action:1byte| |roomid:2byte|
    ---------|--------------|------------| |------------|
     */
    public static final int CALLPROFILE_BYTES_LENTH = 4;
    public static final byte[] CALLPROFILE_PRE = {(byte) 0xff, (byte) 0xfe};// pre byte
    public static final byte CALLPROFILE_FUNC_VIDEOCALL = (byte) 0x01;
    public static final byte CALLPROFILE_VIDEOCALL_ACTION_REQUEST = (byte) 0x01;
    public static final byte CALLPROFILE_VIDEOCALL_ACTION_REPLAY = (byte) 0x02;
    public static final byte CALLPROFILE_VIDEOCALL_ACTION_ENDCALL = (byte) 0x03;


    public static byte[] createVideoCallRequest(RoomInfo roomInfo) {
        byte[] rid = Utils.int2bytes(roomInfo.getId());
        byte[] pwd = Utils.str2bytes(roomInfo.getPwd());
        byte[] request = new byte[CALLPROFILE_BYTES_LENTH + rid.length + pwd.length];
        request[0] = CALLPROFILE_PRE[0];
        request[1] = CALLPROFILE_PRE[1];
        request[2] = CALLPROFILE_FUNC_VIDEOCALL;
        request[3] = CALLPROFILE_VIDEOCALL_ACTION_REQUEST;
        request[4] = rid[0];
        request[5] = rid[1];
        if (pwd != null && pwd.length > 0) {
            System.arraycopy(pwd, 0, request, 6, pwd.length);
        }
        return request;
    }

    public static byte[] createVideoCallReplay(boolean reject) {
        byte[] replay = new byte[CALLPROFILE_BYTES_LENTH + 1];
        replay[0] = CALLPROFILE_PRE[0];
        replay[1] = CALLPROFILE_PRE[1];
        replay[2] = CALLPROFILE_FUNC_VIDEOCALL;
        replay[3] = CALLPROFILE_VIDEOCALL_ACTION_REPLAY;
        replay[4] = reject ? (byte) 0x01 : (byte) 0x00;
        return replay;
    }

    // answer 0:accept 1:reject 2:recall
    public static byte[] createVideoCallReplay(int answer, RoomInfo roomInfo) {
        byte[] rid = Utils.int2bytes(roomInfo.getId());
        byte[] pwd = Utils.str2bytes(roomInfo.getPwd());
        byte[] replay = new byte[CALLPROFILE_BYTES_LENTH + 1 + rid.length + pwd.length];
        replay[0] = CALLPROFILE_PRE[0];
        replay[1] = CALLPROFILE_PRE[1];
        replay[2] = CALLPROFILE_FUNC_VIDEOCALL;
        replay[3] = CALLPROFILE_VIDEOCALL_ACTION_REPLAY;
        replay[4] = (byte) (answer & 0x0f);
        replay[5] = rid[0];
        replay[6] = rid[1];
        if (pwd != null && pwd.length > 0) {
            System.arraycopy(pwd, 0, replay, 7, pwd.length);
        }
        return replay;
    }

    public static byte[] createVideoCallEndCall(RoomInfo roomInfo) {
        byte[] endcall = new byte[CALLPROFILE_BYTES_LENTH + 2];
        endcall[0] = CALLPROFILE_PRE[0];
        endcall[1] = CALLPROFILE_PRE[1];
        endcall[2] = CALLPROFILE_FUNC_VIDEOCALL;
        endcall[3] = CALLPROFILE_VIDEOCALL_ACTION_ENDCALL;
        byte[] rid = Utils.int2bytes(roomInfo.getId());
        endcall[4] = rid[0];
        endcall[5] = rid[1];
        return endcall;
    }

    public static boolean valid(byte[] recieve) {
        if (recieve == null)
            return false;
        if (recieve.length <= CALLPROFILE_BYTES_LENTH)
            return false;
        if (recieve[0] != CALLPROFILE_PRE[0] || recieve[1] != CALLPROFILE_PRE[1])
            return false;
        if (recieve[2] != CALLPROFILE_FUNC_VIDEOCALL)
            return false;

        return true;
    }

    public static boolean isRequest(byte[] recieve) {
        if (!valid(recieve))
            return false;

        if (recieve[3] != CALLPROFILE_VIDEOCALL_ACTION_REQUEST)
            return false;

        return true;
    }

    public static boolean isReplay(byte[] recieve) {
        if (!valid(recieve))
            return false;

        if (recieve[3] != CALLPROFILE_VIDEOCALL_ACTION_REPLAY)
            return false;
        return true;
    }

    public static int getReplayStatus(byte[] recieve, RoomInfo roomInfo) {
        if (!isReplay(recieve)) {
            return -1;
        }

        int replayStatus;
        if (recieve[4] == (byte) 0x00) {
            replayStatus = ConnectSession.CONNECTSESSION_STATUS_ACCEPTED;
        } else if (recieve[4] == (byte) 0x01) {
            replayStatus = ConnectSession.CONNECTSESSION_STATUS_REJECTED;
        } else {
            replayStatus = ConnectSession.CONNECTSESSION_STATUS_REJECTED_RECALL;
        }
        roomInfo.copyInstance(getRoomInfoByReplayData(recieve));
        return replayStatus;
    }

    public static RoomInfo getRoomInfoByEndcallData(byte[] data) {
        if (!VideoCallProtocol.isEndCall(data)) {
            return null;
        }

        byte[] rid = new byte[2];
        rid[0] = data[4];
        rid[1] = data[5];
        int roomid = Utils.bytes2int(rid);
        return new RoomInfo(roomid, "");
    }

    public static boolean isEndCall(byte[] recieve) {
        if (!valid(recieve))
            return false;

        if (recieve[3] != CALLPROFILE_VIDEOCALL_ACTION_ENDCALL)
            return false;

        return true;
    }

    public static RoomInfo getRoomInfoByRequestData(byte[] data) {
        if (!VideoCallProtocol.isRequest(data)) {
            return null;
        }
        byte[] rid = new byte[2];
        rid[0] = data[4];
        rid[1] = data[5];
        int roomid = Utils.bytes2int(rid);
        String pwd = "";
        int pwdlenth = data.length - VideoCallProtocol.CALLPROFILE_BYTES_LENTH - rid.length;
        if (pwdlenth > 0) {
            byte[] pwdbytes = new byte[pwdlenth];
            System.arraycopy(data, VideoCallProtocol.CALLPROFILE_BYTES_LENTH + rid.length, pwdbytes, 0, pwdlenth);
            pwd = Utils.bytes2str(pwdbytes);
        }

        return new RoomInfo(roomid, pwd);
    }

    public static RoomInfo getRoomInfoByReplayData(byte[] data) {
        if (!VideoCallProtocol.isReplay(data)) {
            return null;
        }
        byte[] rid = new byte[2];
        rid[0] = data[5];
        rid[1] = data[6];
        int roomid = Utils.bytes2int(rid);

        String pwd = "";
        int pwdlenth = data.length - VideoCallProtocol.CALLPROFILE_BYTES_LENTH - rid.length - 1;
        if (pwdlenth > 0) {
            byte[] pwdbytes = new byte[pwdlenth];
            System.arraycopy(data, VideoCallProtocol.CALLPROFILE_BYTES_LENTH + rid.length + 1, pwdbytes, 0, pwdlenth);
            pwd = Utils.bytes2str(pwdbytes);
        }


        return new RoomInfo(roomid, pwd);
    }

    //CHECKER
    public static final int INVALID = 0xff;
    public static final int VIDEOCALL_RECIEVED_REQUEST = 0x01;
    public static final int VIDEOCALL_RECIEVED_REPLAY = 0x02;
    public static final int VIDEOCALL_RECIEVED_ENDCALL = 0x03;

    public static int check(byte[] recieve) {
        if (VideoCallProtocol.isRequest(recieve)) {
            return VIDEOCALL_RECIEVED_REQUEST;
        } else if (VideoCallProtocol.isReplay(recieve)) {
            return VIDEOCALL_RECIEVED_REPLAY;
        } else if (VideoCallProtocol.isEndCall(recieve)) {
            return VIDEOCALL_RECIEVED_ENDCALL;
        }

        return INVALID;
    }
}
