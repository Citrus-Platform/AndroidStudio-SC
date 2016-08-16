package com.superchat.model;

import com.google.gson.annotations.SerializedName;

public class MessageDataModel {

	public MessageDataModel() {
		super();
	}
	//======= [Android Values] =========

//	1.	recipientCount
//	2.	fromUserName
//	3.	fromGroupUserName
//	4.	messageID
//	5.	textMessage
//	6.	toUserName
//	7.	audioMessageLength
//	8.	caption
//	9.	mediaURL
//	10. mediaLocalPath
//	11. thumbData
//	12. lastUpdateField ==>
//	13. readUserCount  - how many users have read the message
//	14. dataChanged -
//	15. locationMessage -
//	16. broadcastMessageID -
//	17. foreignMessageID
//	18. contactName
//	19. unreadCount
//	20. seen
//	21. mediaStatus
//	22.	readTime
//	23.	messageType
//	24. messageTypeID - image, audio, pdf etc.

	//======= [IOS Values] =========
	//	Some values are must, that is not in Android - dateTime, chatType, roomID

//	25. roomID - group name, broadcast name, shared ID name, Bulletin Name
//	26. recipentCount - Member count of group when message is sent
//	27. userID
//	28. chatType - 2 - group meaage, 4 - broadcast
//	29. dateTime - when this message was sent.
//	30. status - wait = 0, sent - 1, delivered - 2, read - 3, error - 5
//	31. readTime - when message was read - P2P and Group(when all users have read the message)
//	32. location - comma seperated lat-lon
//	33. originalFileName - original file name
//	34. deliveryTime - when this message was delivered.


//=====================================================
	//String values
	@SerializedName(value = "fromUserName")
	public String fromUserName;


	@SerializedName(value = "roomID")
	public String roomID;
	
	@SerializedName(value = "fromGroupUserName")
	public String fromGroupUserName = null;

	@SerializedName(value = "chatType")
	public String chatType = null;
	
	@SerializedName(value = "messageID")
	public String messageID = null;

	@SerializedName(value = "textMessage")
	public String textMessage = null;

	@SerializedName(value = "userID")
	public String userID = null;
	
	@SerializedName(value = "date")
	public String date = null;
	
	@SerializedName(value = "toUserName")
	public String toUserName = null;
	
	@SerializedName(value = "dateTime")
	public String dateTime = null;

	@SerializedName(value = "audioMessageLength")
	public String audioMessageLength = null;

	@SerializedName(value = "caption")
	public String caption = null;
	
	@SerializedName(value = "mediaURL")
	public String mediaURL = null;
	
	@SerializedName(value = "status")
	public String status = null;

	@SerializedName(value = "deliveryTime")
	public String deliveryTime = null;
	
	@SerializedName(value = "mediaLocalPath")
	public String mediaLocalPath = null;
	
	@SerializedName(value = "thumbData")
	public String thumbData = null;
	
	@SerializedName(value = "locationMessage")
	public String locationMessage = null;
	
	@SerializedName(value = "broadcastMessageID")
	public String broadcastMessageID = null;
	
	@SerializedName(value = "foreignMessageID")
	public String foreignMessageID = null;
	
	@SerializedName(value = "contactName")
	public String contactName = null;
	
	//int values
	@SerializedName(value = "unreadCount")
	public int unreadCount;
	
	@SerializedName(value = "seen")
	public int seen;
	
	@SerializedName(value = "mediaStatus")
	public int mediaStatus;
	
	@SerializedName(value = "messageType")
	public int messageType;
	
	@SerializedName(value = "messageTypeID")
	public int messageTypeID;
	
	@SerializedName(value = "dataChanged")
	public int dataChanged;
	
	@SerializedName(value = "recipientCount")
	public int recipientCount;
	
	@SerializedName(value = "readUserCount")
	public int readUserCount;
	
	//long values
	@SerializedName(value = "readTime")
	public long readTime;
	
	@SerializedName(value = "lastUpdateField")
	public long lastUpdateField;

}
