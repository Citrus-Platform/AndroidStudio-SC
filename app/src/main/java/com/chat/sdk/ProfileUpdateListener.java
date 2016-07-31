package com.chat.sdk;

public interface ProfileUpdateListener {
	/**
	 * 
	 * @param senderName message sender name
	 * @param message text message.
	 */
	public void notifyProfileUpdate(String userName);
	public void notifyProfileUpdate(String userName, String status);
	public void notifyProfileUpdate(String userName, String status, String userDisplayName);

}
