package com.bairuitech.server;

public class UserInfo {

	private static int IDSEED = 100;
	private String nickname;
	private String pwd;
	private int id;
	private String name;
	private boolean isOnline;
	private String ipaddr;
	
	public UserInfo(String name, String pwd, String nickname) {
		super();
		this.id = IDSEED++;
		this.nickname = nickname;
		this.pwd = pwd;
		this.name = name;
		this.isOnline = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean getIsOnline() {
		return this.isOnline;
	}

	public void setIsOnline(boolean online) {
		this.isOnline = online;
	}

	public String getIpaddr() {
		return ipaddr;
	}

	public void setIpaddr(String ipaddr) {
		this.ipaddr = ipaddr;
	}


}
