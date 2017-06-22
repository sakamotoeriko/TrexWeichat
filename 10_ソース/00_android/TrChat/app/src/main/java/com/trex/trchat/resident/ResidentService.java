package com.trex.trchat.resident;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


import com.trex.trchat.lib.trexsdk.TrChatCoreSdk;
import com.trex.trchat.lib.trexsdk.callbacks.TrChatTransDataEvent;
import com.trex.trchat.trexbusiness.Session;
import com.trex.trchat.trexbusiness.VideoCallBusiness;
import com.trex.trchat.trexprofile.ProfileChecker;

public class ResidentService extends Service implements TrChatTransDataEvent {
    public ResidentService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private TrChatCoreSdk mTrChatCoreSdk;

    @Override
    public void onCreate() {
        super.onCreate();
        mTrChatCoreSdk = TrChatCoreSdk.getInstance();
//        mTrChatCoreSdk.initSdk();
        mTrChatCoreSdk.setTransDataEvent(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void OnAnyChatTransFile(int dwUserid, String FileName, String TempFilePath, int dwFileLength, int wParam, int lParam, int dwTaskId) {

    }

    @Override
    public void OnAnyChatTransBuffer(int dwUserid, byte[] lpBuf, int dwLen) {

        VideoCallBusiness videoCall = VideoCallBusiness.getInstance();
        videoCall.setContext(getApplicationContext());
        Session session = new Session(dwUserid, -1, -1, "");
        switch (ProfileChecker.check(lpBuf, session)) {
            case ProfileChecker.VIDEOCALL_RECIEVED_REQUEST: {
                videoCall.recieveVideoCall(session);
            }
            break;
            case ProfileChecker.VIDEOCALL_RECIEVED_REPLAY: {
//                if (session.getStatus() == Session.SESSION_STATUS_REJECTED_RECALL){
//                    videoCall.requestVideoCall(session);
//                }else {
                    videoCall.revieveVideoCallReplay(session);
//                }

            }
            break;
            case ProfileChecker.INVALID:
                //error path
                break;
            default:
                //error path
                break;
        }
    }

    @Override
    public void OnAnyChatTransBufferEx(int dwUserid, byte[] lpBuf, int dwLen, int wparam, int lparam, int taskid) {

    }

    @Override
    public void OnAnyChatSDKFilterData(byte[] lpBuf, int dwLen) {

    }
}
