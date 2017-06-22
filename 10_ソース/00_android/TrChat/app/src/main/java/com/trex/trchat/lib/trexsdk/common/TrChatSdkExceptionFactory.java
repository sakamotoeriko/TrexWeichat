package com.trex.trchat.lib.trexsdk.common;


public class TrChatSdkExceptionFactory {
    public static final int TRCHAT_SDK_EXCEPTION_NOEXCEPTION =  0x00;
    public static final int TRCHAT_SDK_EXCEPTION_NOCONTEXT =  0x01;
    public static final int TRCHAT_SDK_EXCEPTION_COREINSTANCE_NOTFIND =  0x02;

    public static RuntimeException getException(int reason){
        switch (reason){
            case TRCHAT_SDK_EXCEPTION_NOCONTEXT:
                return new RuntimeException("Context hasnt been set.");
            case TRCHAT_SDK_EXCEPTION_COREINSTANCE_NOTFIND:
                return new RuntimeException("Core instance is not found.");
        }
        return new RuntimeException("TrChat sdk error");
    }
}
