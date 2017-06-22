package com.trex.trchat.trexprofile;

import com.trex.trchat.common.Utils;
import com.trex.trchat.trexbusiness.Session;

public class CallProfile {

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
    public static final int CALLPROFILE_BYTES_LENTH = 16;
    public static final byte[] CALLPROFILE_PRE = {(byte) 0xff, (byte) 0xfe};// pre byte
    public static final byte CALLPROFILE_VIDEOCALL = (byte) 0x01;
    public static final byte CALLPROFILE_VIDEOCALL_REQUEST = (byte) 0x01;
    public static final byte CALLPROFILE_VIDEOCALL_REPLAY = (byte) 0x02;


    public static byte[] createVideoCallRequest(int roomid, String password) {
        byte[] rid = Utils.int2bytes(roomid);
        byte[] pwd = Utils.str2bytes(password);
        byte[] request = new byte[CALLPROFILE_BYTES_LENTH + rid.length + pwd.length];
        request[0] = CALLPROFILE_PRE[0];
        request[1] = CALLPROFILE_PRE[1];
        request[2] = CALLPROFILE_VIDEOCALL;
        request[3] = CALLPROFILE_VIDEOCALL_REQUEST;
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
        replay[2] = CALLPROFILE_VIDEOCALL;
        replay[3] = CALLPROFILE_VIDEOCALL_REPLAY;
        replay[4] = reject ? (byte) 0x01 : (byte) 0x00;
        return replay;
    }

    public static byte[] createVideoCallReplay(boolean reject, int roomid, String password) {
        byte[] rid = Utils.int2bytes(roomid);
        byte[] pwd = Utils.str2bytes(password);
        byte[] replay = new byte[CALLPROFILE_BYTES_LENTH + 1 + rid.length + pwd.length];
        replay[0] = CALLPROFILE_PRE[0];
        replay[1] = CALLPROFILE_PRE[1];
        replay[2] = CALLPROFILE_VIDEOCALL;
        replay[3] = CALLPROFILE_VIDEOCALL_REPLAY;
        replay[4] = reject ? (byte) 0x01 : (byte) 0x00;
        replay[5] = rid[0];
        replay[6] = rid[1];
        if (pwd != null && pwd.length > 0) {
            System.arraycopy(pwd, 0, replay, 7, pwd.length);
        }
        return replay;
    }

    public static boolean valid(byte[] recieve) {
        if (recieve == null)
            return false;
        if (recieve.length <= CALLPROFILE_BYTES_LENTH)
            return false;
        if (recieve[0] != CALLPROFILE_PRE[0] || recieve[1] != CALLPROFILE_PRE[1])
            return false;
        if (recieve[2] != CALLPROFILE_VIDEOCALL)
            return false;

        return true;
    }

    public static boolean isRequest(byte[] recieve, Session session) {
        if (!valid(recieve))
            return false;

        if (recieve[3] != CALLPROFILE_VIDEOCALL_REQUEST)
            return false;

        byte[] rid = new byte[2];
        rid[0] = recieve[4];
        rid[1] = recieve[5];
        session.setRoomId(Utils.bytes2int(rid));

        int pwdlenth = recieve.length - CALLPROFILE_BYTES_LENTH - rid.length;
        if (pwdlenth > 0) {
            byte[] pwd = new byte[pwdlenth];
            System.arraycopy(recieve, CALLPROFILE_BYTES_LENTH + rid.length, pwd, 0, pwdlenth);
            session.setRoomPwd(Utils.bytes2str(pwd));
        } else {
            session.setRoomPwd("");
        }


        return true;
    }


    public static boolean isReplay(byte[] recieve, Session session) {
        if (!valid(recieve))
            return false;

        if (recieve[3] != CALLPROFILE_VIDEOCALL_REPLAY)
            return false;

        int status = (recieve[4] == (byte) 0x00) ? Session.SESSION_STATUS_ACCEPTED : Session.SESSION_STATUS_REJECTED;

        if (recieve[4] == (byte) 0x00) {
            session.setStatus(Session.SESSION_STATUS_ACCEPTED);
        } else {
            session.setStatus(Session.SESSION_STATUS_REJECTED);
            int pwdlenth = recieve.length - (CALLPROFILE_BYTES_LENTH + 1 + 2);
            if (pwdlenth >= 0) {
                byte[] rid = new byte[2];
                rid[0] = recieve[5];
                rid[1] = recieve[6];
                session.setRoomId(Utils.bytes2int(rid));
                if (pwdlenth == 0) {
                    session.setRoomPwd("");
                } else {
                    byte[] pwd = new byte[pwdlenth];
                    System.arraycopy(recieve, 7, pwd, 0, pwdlenth);
                    session.setRoomPwd(Utils.bytes2str(pwd));
                }
                session.setStatus(Session.SESSION_STATUS_REJECTED_RECALL);
            }
        }
        if (session.getStatus() == Session.SESSION_STATUS_CALLING) {
            session.setStatus(status);
        } else {
            //error path
            //TODO
        }

        return true;
    }
}
