package com.bairuitech.server;

import java.util.ArrayList;
import java.util.List;

public class UserData {
	private static final UserInfo user1 = new UserInfo("trex1","12345","trex1");
	private static final UserInfo user2 = new UserInfo("trex2","12345","trex2");
	private static final UserInfo user3 = new UserInfo("trex3","12345","trex3");
	private static final UserInfo user4 = new UserInfo("trex4","12345","trex4");
	private static final UserInfo user5 = new UserInfo("trex5","12345","trex5");
	
	private static List<UserInfo> userlist = new ArrayList<UserInfo>();
	static {
		userlist.add(user1);
		userlist.add(user2);
		userlist.add(user3);
		userlist.add(user4);
		userlist.add(user5);
	}
	public static UserInfo verify(String name,String pwd) {
		for(UserInfo user :userlist) {
			if(!user.getName().equals(name)) continue;
				if(user.getPwd().equals(pwd)) {
					return user;
			}
		}
		return null;
	}
	
	public static List<UserInfo> getUserData(){
		return userlist;
	}
}
