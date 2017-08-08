package com.bairuitech.server;

import java.util.ArrayList;
import java.util.List;

public class UserData {
	private static final UserInfo user1 = new UserInfo("trex1", "12345", "trchat");
	private static final UserInfo user2 = new UserInfo("trex2", "12345", "user1");
	private static final UserInfo user3 = new UserInfo("trex3", "12345", "user2");
	private static final UserInfo user4 = new UserInfo("trex4", "12345", "user3");
	private static final UserInfo user5 = new UserInfo("trex5", "12345", "user4");

	private static List<UserInfo> userlist = new ArrayList<UserInfo>();
	static {
		userlist.add(user1);
		userlist.add(user2);
		userlist.add(user3);
		userlist.add(user4);
		userlist.add(user5);
	}

	public static UserInfo verify(String name, String pwd) {
		//		System.out.print("verify name:"+name+" pwd:"+pwd+"¥n");
		for (UserInfo user : userlist) {
			//			System.out.print("verify checkname:"+user.getName()+" pwd:"+user.getPwd()+"\r\n");
			if (!user.getName().equals(name))
				continue;

			if (user.getPwd().equals(pwd)) {
				System.out.print("verify true\r\n");
				return user;
			}
		}
		System.out.print("verify false\r\n");
		return null;
	}

	public static List<UserInfo> getUserData() {
		return userlist;
	}
	public static List<UserInfo> getFrinds(int userid){
		return userlist;
	}
	public static void updateOnlineStatus(int userid,boolean status) {
		for(UserInfo user:userlist) {
			if(userid!=user.getId()) continue;
			user.setIsOnline(status);
		}
		
	}
}
