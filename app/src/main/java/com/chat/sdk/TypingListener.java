package com.chat.sdk;

public interface TypingListener {
	public void notifyTypingRecieve(String userName);
	public void notifyRecordStatusRecieve(String userName);
	public void notifyListeningStatusRecieve(String userName);
	public void refreshOnlineGroupUser();
	public void refreshSubjectOfGroup();
}
