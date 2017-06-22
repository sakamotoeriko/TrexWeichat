package com.trex.trchat.trexbusiness;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LinkedSession implements Serializable {
    private static final long serialVersionUID = 3548489651551027582L;

    private UserItem self;
    private List<UserItem> targets = new ArrayList<>();

    private int roomid;
    private String password;

    public LinkedSession(UserItem self) {
        this.self = self;
    }

    public int getRoomid() {
        return roomid;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<UserItem> addTartget(UserItem item){
        if (!targets.contains(item)){
            targets.add(item);
        }
        return targets;
    }

    public List<UserItem> removeTarget(UserItem item){
        targets.remove(item);
        return targets;
    }

    public UserItem getSelf() {
        return self;
    }

    public List<UserItem> getTargets() {
        return targets;
    }

}
