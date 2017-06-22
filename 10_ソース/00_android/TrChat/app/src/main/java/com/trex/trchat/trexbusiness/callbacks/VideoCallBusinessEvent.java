package com.trex.trchat.trexbusiness.callbacks;

import com.trex.trchat.trexbusiness.Session;

public interface VideoCallBusinessEvent {
    void onReplay(Session session);
    void onNewSession(Session session);
}
