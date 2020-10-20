package com.trex.trchat.lib.trexsdk.callbacks;

public interface TrChatRecordEvent {
    // 视频录制完成事件
     void OnAnyChatRecordEvent(int dwUserId, int dwErrorCode, String lpFileName, int dwElapse, int dwFlags, int dwParam, String lpUserStr);
    // 图像抓拍完成事件
     void OnAnyChatSnapShotEvent(int dwUserId, int dwErrorCode, String lpFileName, int dwFlags, int dwParam, String lpUserStr);
}
