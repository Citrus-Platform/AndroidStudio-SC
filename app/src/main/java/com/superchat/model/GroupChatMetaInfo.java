package com.superchat.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import static com.superchat.utils.Constants.KEY_GROUP_BROADCAST;
import static com.superchat.utils.Constants.KEY_GROUP_NORMAL;

public class GroupChatMetaInfo {


	boolean isBroadCastActive = false;

	public boolean isBroadCastActive() {
		return isBroadCastActive;
	}

	public void setBroadCastActive(String broadcast) {
		if(broadcast != null){
			if(broadcast.trim().equalsIgnoreCase(KEY_GROUP_NORMAL)){
				isBroadCastActive = false;
			} else if(broadcast.trim().equalsIgnoreCase(KEY_GROUP_BROADCAST)){
				isBroadCastActive = true;
			}
		}
	}
}