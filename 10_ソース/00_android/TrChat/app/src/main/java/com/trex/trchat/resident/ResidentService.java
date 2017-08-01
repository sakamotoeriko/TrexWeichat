package com.trex.trchat.resident;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


import com.trex.trchat.lib.trexsdk.TrChatCoreSdk;
import com.trex.trchat.lib.trexsdk.callbacks.TrChatTransDataEvent;
import com.trex.trchat.videocall.VideoCallController;
import com.trex.trchat.videocall.VideoCallProtocol;
import com.trex.trchat.videocall.model.ConnectSession;
import com.trex.trchat.videocall.model.RoomInfo;


public class ResidentService extends Service implements TrChatTransDataEvent {

    public static final String TAG = ResidentService.class.getSimpleName();

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
        VideoCallController videoCall = VideoCallController.getInstance(getApplication());

        ConnectSession session = new ConnectSession(dwUserid, -1, -1, "");
        int check = VideoCallProtocol.check(lpBuf);
        Log.d(TAG, "OnAnyChatTransBuffer check:" + check);
        switch (check) {
            case VideoCallProtocol.VIDEOCALL_RECIEVED_REQUEST: {
                RoomInfo roomInfo = VideoCallProtocol.getRoomInfoByRequestData(lpBuf);
                session.setRoomInfo(roomInfo);
                videoCall.recieveVideoCall(session);
            }
            break;
            case VideoCallProtocol.VIDEOCALL_RECIEVED_REPLAY: {
                RoomInfo roomInfo = new RoomInfo(-1, "");
                int status = VideoCallProtocol.getReplayStatus(lpBuf, roomInfo);
                session.setStatus(status);
                session.setRoomInfo(roomInfo);
                if (status == ConnectSession.CONNECTSESSION_STATUS_REJECTED_RECALL) {
                    //need to call again
                    videoCall.requestVideoCallRecall(session);
                } else {
                    //rejected or accepted
                    videoCall.revieveVideoCallReplay(session);
                }
            }
            break;
            case VideoCallProtocol.VIDEOCALL_RECIEVED_ENDCALL:
                //TODO
                break;
            case VideoCallProtocol.INVALID:
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
