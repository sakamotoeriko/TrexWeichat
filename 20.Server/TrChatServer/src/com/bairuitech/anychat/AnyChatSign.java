package com.bairuitech.anychat;		// 不能修改包的名称

public class AnyChatSign
{
	// 对应用接入信息使用私钥进行签名
	public static native int RsaSign(int userid, String struserid, String appid, String privatekey, AnyChatOutParam outParam);
	// 对应用接入信息签名使用公钥进行验证
	public static native int RsaVerify(int userid, String struserid, String appid, String sigstr, int timestamp, String publickey);

	
	
	
    static {
    	System.loadLibrary("anychatsign");
    }
    
}


