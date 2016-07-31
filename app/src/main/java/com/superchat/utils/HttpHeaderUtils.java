package com.superchat.utils;

import java.io.UnsupportedEncodingException;

import android.util.Base64;

public class HttpHeaderUtils {
	private static String ENCRYPTION_KEY = "Sleep is essential to human body as during the time of sleeping.";

	private HttpHeaderUtils() {
	}

	public static String createRTAppKeyHeader(int userId, String password) {
		return createRTAppKeyHeader(userId, password, true);
	}

	public static String createRTAppKeyHeader(int userId, String password, boolean newProtocol) {
//		System.out.println("--------------userId : "+userId);
//		userId =1574403;
//		password = "a" ;
		if(password == null || userId <= 0){
			return "";
		}
		StringBuffer builder = new StringBuffer();
		if (newProtocol) {
			builder.append("'");
		}
		builder.append(userId);
		builder.append(":");
		if (newProtocol) {
			DataEncryption encrptor = null;
			try {
				encrptor = new DataEncryption(ENCRYPTION_KEY.getBytes("utf-8"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			byte[] isoPassword = null;
			try {
				isoPassword = password.getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			byte[] encryptedPassword = encrptor.rockeTalkEncrypt(isoPassword);
			String base64Password = MyBase64.encode(encryptedPassword);
			builder.append(base64Password);
		} else {
			builder.append(password);
		}
		if (newProtocol) {
			builder.append("'");
		}
		
//		System.out.println("-----------------------app key--------------------"+builder.toString());
		return builder.toString().trim();
	}
	
	public static String encriptPass(String password) {
	boolean newProtocol = true ;
//		System.out.println("--------------userId : "+userId);
//		userId =1574403;
//		password = "a" ;
		
		StringBuffer builder = new StringBuffer();
		if (newProtocol) {
			
		}
		
		if (newProtocol) {
			DataEncryption encrptor = null;
			try {
				encrptor = new DataEncryption(ENCRYPTION_KEY.getBytes("utf-8"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			byte[] isoPassword = null;
			try {
				isoPassword = password.getBytes("utf-8");
				byte[] encryptedPassword = encrptor.rockeTalkEncrypt(isoPassword);
				String base64Password = MyBase64.encode(encryptedPassword);
				builder.append(base64Password);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			builder.append(password);
		}
		if (newProtocol) {
			
		}
		
//		System.out.println("-----------------------encrepted pass-------------------"+builder.toString());
		return builder.toString().trim();
	}
	public static String encryptData(String data) {
		String base64EncryptedData = null;
		DataEncryption encrptor = null;
		try {
			encrptor = new DataEncryption(ENCRYPTION_KEY.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		byte[] byteData = null;
		try {
			byteData = data.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] encryptedPassword = encrptor.rockeTalkEncrypt(byteData);
//		base64EncryptedData = Base64.encode(encryptedPassword, 0);//MyBase64.encode(encryptedPassword);
		return new String(Base64.encode(encryptedPassword, 0));
	}
	//decrypt Data
	public static String decryptData(String data) {
		String base64EncryptedData = null;
		DataEncryption decryptor = null;
		byte[] encryptedPassword = null;
		try {
			decryptor = new DataEncryption(ENCRYPTION_KEY.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		byte[] byteData = null;
		try {
			byteData = data.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try{
			encryptedPassword = decryptor.rockeTalkDecrypt(byteData);
//			base64EncryptedData = MyBase64.encode(encryptedPassword);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return new String(Base64.decode(encryptedPassword, 0));
	}
}
/*package com.kainat.app.android.util;

import java.io.UnsupportedEncodingException;

public class HttpHeaderUtils {
	private static String ENCRYPTION_KEY = "The quick brown fox jumps over the lazy dog";

	private HttpHeaderUtils() {
	}

	public static String createRTAppKeyHeader(int userId, String password) {
		return createRTAppKeyHeader(userId, password, true);
	}

	public static String createRTAppKeyHeader(int userId, String password, boolean newProtocol) {
		if(password == null || userId <= 0){
			return "";
		}
		StringBuilder builder = new StringBuilder();
		if (newProtocol) {
			builder.append("'");
		}
		builder.append(userId);
		builder.append(":");
		if (newProtocol) {
			DataEncryption encrptor = null;
			try {
				encrptor = new DataEncryption(ENCRYPTION_KEY.getBytes("ISO8859_1"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			byte[] isoPassword = null;
			try {
				isoPassword = password.getBytes("ISO8859_1");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			byte[] encryptedPassword = encrptor.RockeTalkEncrypt(isoPassword);
			String base64Password = MyBase64.encode(encryptedPassword);
			builder.append(base64Password);
		} else {
			builder.append(password);
		}
		if (newProtocol) {
			builder.append("'");
		}
		return builder.toString();
	}
}*/