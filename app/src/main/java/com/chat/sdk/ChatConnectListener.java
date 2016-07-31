package com.chat.sdk;

public interface ChatConnectListener {
	public void chatClientConnected(ChatService service);
	public void chatClientDisconnected();
	
	public void notifyTypingRecieve(String user);
	public void refreshOnlineGroupUser();
	public void refreshSubjectOfGroup();
	/**
	 * This method notifying when new message inserted inside the database.   
	 */
	public void notifyChatRecieve(String sender, String message);
}
