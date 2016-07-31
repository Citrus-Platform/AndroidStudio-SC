package com.chat.sdk;

public interface ChatCountListener {
	/**
	 * 
	 * @param senderName message sender name
	 * @param message text message.
	 */
	public void notifyChatRecieve(String senderName, String message);
	public void notifyChatHome(String senderName, String message);
}
