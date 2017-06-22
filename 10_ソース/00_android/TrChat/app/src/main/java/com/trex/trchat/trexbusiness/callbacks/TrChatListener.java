package com.trex.trchat.trexbusiness.callbacks;

import com.trex.trchat.trexbusiness.Session;

public interface TrChatListener {
    void onRequested(Session session);
    void onRecieved(Session session);
    void onReplay(Session session);
}

