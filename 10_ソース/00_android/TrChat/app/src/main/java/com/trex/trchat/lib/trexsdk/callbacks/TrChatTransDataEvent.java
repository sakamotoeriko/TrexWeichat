package com.trex.trchat.lib.trexsdk.callbacks;

// 数据传输通知接口
public interface TrChatTransDataEvent {

    // 文件传输回调函数定义
    void OnAnyChatTransFile(int dwUserid, String FileName, String TempFilePath, int dwFileLength, int wParam, int lParam, int dwTaskId);

    // 透明通道数据回调函数定义
    void OnAnyChatTransBuffer(int dwUserid, byte[] lpBuf, int dwLen);

    // 扩展透明通道数据回调函数定义
    void OnAnyChatTransBufferEx(int dwUserid, byte[] lpBuf, int dwLen, int wparam, int lparam, int taskid);

    // SDK Filter 通信数据回调函数定义
    void OnAnyChatSDKFilterData(byte[] lpBuf, int dwLen);
}
