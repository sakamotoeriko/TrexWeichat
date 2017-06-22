package com.trex.trchat.lib.trexsdk.callbacks;

public interface TrChatPrivateChatEvent {
    // 用户私聊请求消息，dwUserId表示发起者的用户ID号，dwRequestId表示私聊请求编号，标识该请求
    void OnAnyChatPrivateRequestMessage(int dwUserId, int dwRequestId);

    // 用户私聊请求回复消息，dwUserId表示回复者的用户ID号，dwErrorCode为出错代码
    void OnAnyChatPrivateEchoMessage(int dwUserId, int dwErrorCode);

    // 用户退出私聊消息，dwUserId表示退出者的用户ID号，dwErrorCode为出错代码
    void OnAnyChatPrivateExitMessage(int dwUserId, int dwErrorCode);
}
