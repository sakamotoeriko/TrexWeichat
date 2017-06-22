package com.trex.trchat.trexprofile;

import com.trex.trchat.trexbusiness.Session;

public class ProfileChecker {
    public static final int INVALID = 0xff;
    public static final int VIDEOCALL_RECIEVED_REQUEST = 0x01;
    public static final int VIDEOCALL_RECIEVED_REPLAY = 0x02;

    public static int check(byte[] recieve, Session session){
        if (CallProfile.isRequest(recieve,session)){
            return VIDEOCALL_RECIEVED_REQUEST;
        }else if (CallProfile.isReplay(recieve,session)){
            return VIDEOCALL_RECIEVED_REPLAY;
        }

        return INVALID;
    }
}
