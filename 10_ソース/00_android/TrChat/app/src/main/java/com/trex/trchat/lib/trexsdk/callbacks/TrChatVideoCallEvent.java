package com.trex.trchat.lib.trexsdk.callbacks;

public interface TrChatVideoCallEvent {
    void OnAnyChatVideoCallEvent(int dwEventType, int dwUserId, int dwErrorCode, int dwFlags, int dwParam, String userStr);
}
