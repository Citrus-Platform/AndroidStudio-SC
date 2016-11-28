package com.superchat.model;

import static com.superchat.utils.Constants.KEY_GROUP_BROADCAST;
import static com.superchat.utils.Constants.KEY_GROUP_NORMAL;

public class GroupChatXmppCaption {

	GroupChatXmppCaptionData caption;

	public GroupChatXmppCaptionData getCaption() {
		return caption;
	}

	public void setCaption(GroupChatXmppCaptionData caption) {
		this.caption = caption;
	}
}