package com.trexchat.model;

import com.trexchat.FrindsList;

import java.util.ArrayList;
import java.util.List;

public class Frinds {
    public static class User{
        public String getmIpAddr() {
            return mIpAddr;
        }

        public String getmPort() {
            return mPort;
        }

        public String getmDisplayName() {
            return mDisplayName;
        }

        String mIpAddr;
        String mPort;
        String mDisplayName;
        int id;
        public int getId() {
            return id;
        }



        public void setId(int id) {
            this.id = id;
        }

        public User() {
        }

        public User(String mIpAddr, String mPort, String mDisplayName) {
            this.mIpAddr = mIpAddr;
            this.mPort = mPort;
            this.mDisplayName = mDisplayName;
        }
    }

    private static List<User> mFrinds ;

    public static List<User> loadFrinds(){

        if (mFrinds == null) {
            mFrinds = new ArrayList<>();
            User user = new User();
            user.mIpAddr = FrindsList.SERVER_ADDR;
            user.mPort = FrindsList.SERVER_PORT;
            user.mDisplayName = "trex_a";
            mFrinds.add(user);
        }
        return mFrinds;
    }

}
