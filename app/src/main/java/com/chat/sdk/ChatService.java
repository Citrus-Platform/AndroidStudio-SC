package com.chat.sdk;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chat.sdk.db.ChatDBConstants;
import com.chat.sdk.db.ChatDBWrapper;
import com.chatsdk.org.jivesoftware.smack.Chat;
import com.chatsdk.org.jivesoftware.smack.ChatManagerListener;
import com.chatsdk.org.jivesoftware.smack.ConnectionConfiguration;
import com.chatsdk.org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import com.chatsdk.org.jivesoftware.smack.ConnectionListener;
import com.chatsdk.org.jivesoftware.smack.MessageListener;
import com.chatsdk.org.jivesoftware.smack.PacketListener;
import com.chatsdk.org.jivesoftware.smack.PrivacyList;
import com.chatsdk.org.jivesoftware.smack.PrivacyListManager;
import com.chatsdk.org.jivesoftware.smack.RosterListener;
import com.chatsdk.org.jivesoftware.smack.SmackAndroid;
import com.chatsdk.org.jivesoftware.smack.XMPPConnection;
import com.chatsdk.org.jivesoftware.smack.XMPPException;
import com.chatsdk.org.jivesoftware.smack.filter.MessageTypeFilter;
import com.chatsdk.org.jivesoftware.smack.filter.PacketExtensionFilter;
import com.chatsdk.org.jivesoftware.smack.filter.PacketFilter;
import com.chatsdk.org.jivesoftware.smack.packet.Message;
import com.chatsdk.org.jivesoftware.smack.packet.Message.MediaBody;
import com.chatsdk.org.jivesoftware.smack.packet.Message.SeenState;
import com.chatsdk.org.jivesoftware.smack.packet.Message.Type;
import com.chatsdk.org.jivesoftware.smack.packet.Message.XMPPMessageType;
import com.chatsdk.org.jivesoftware.smack.packet.Packet;
import com.chatsdk.org.jivesoftware.smack.packet.Presence;
import com.chatsdk.org.jivesoftware.smack.packet.PrivacyItem;
import com.chatsdk.org.jivesoftware.smack.util.StringUtils;
import com.chatsdk.org.jivesoftware.smackx.ChatState;
import com.chatsdk.org.jivesoftware.smackx.Form;
import com.chatsdk.org.jivesoftware.smackx.FormField;
import com.chatsdk.org.jivesoftware.smackx.GroupChatInvitation;
import com.chatsdk.org.jivesoftware.smackx.muc.MultiUserChat;
import com.chatsdk.org.jivesoftware.smackx.packet.MUCInitialPresence;
import com.chatsdk.org.jivesoftware.smackx.packet.MUCInitialPresence.History;
import com.chatsdk.org.jivesoftware.smackx.packet.MUCUser;
import com.chatsdk.org.jivesoftware.smackx.packet.MUCUser.Invite;
import com.chatsdk.org.jivesoftware.smackx.packet.MessageEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DBWrapper;
import com.superchat.data.db.DatabaseConstants;
import com.superchat.model.LoginResponseModel.UserResponseDetail;
import com.superchat.model.ProfileUpdateModel;
import com.superchat.ui.ChatHome;
import com.superchat.ui.ChatListScreen;
import com.superchat.ui.HomeScreen;
import com.superchat.ui.HomeScreen.GetSharedIDListFromServer;
import com.superchat.ui.RegistrationOptions;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;
import android.widget.Toast;
import me.leolin.shortcutbadger.ShortcutBadger;

public class ChatService extends Service{
	private final String TAG = "ChatService";
	public static XMPPConnection connection;
//	Roster roster;
	private static NotificationManager notificationManager;
	private Builder messageNotification;
	public ChatCountListener chatCountListener;
	private static ChatCountListener chatListener;
	private static TypingListener typingNotifier;
	public ProfileUpdateListener profileUpdateNotifier;
	// Presence presence;
	SharedPrefManager prefManager;
	static String userMe = "";
	String displayUserName = "";
	Calendar calender;
	boolean onForeground;
	String currentUser = "";
	public static Context context;
	static String notificationPackage = "";
	ChatDBWrapper chatDBWrapper;
	static String notificationActivity = ".ui.ChatListScreen";
	public static final String MEDIA_TYPE =  ".amr";//".m4a";
	public static boolean xmppConectionStatus = false; 
	private static String notificationAllMessage="";
	static boolean isFirstMessage = true;
	static String previousUser = "";
	RosterListener rosterListener = new RosterListener(){

		@Override
		public void entriesAdded(Collection<String> addresses) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void entriesUpdated(Collection<String> addresses) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void entriesDeleted(Collection<String> addresses) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void presenceChanged(Presence presence) {
			
			 Log.alltime(TAG, "presenceChanged: "+presence.getFrom());
//			 for(RosterEntry entry: roster.getEntries()) {
//
//	                if(roster.getPresence(entry.getUser()).equals(Presence.Type.available)) {
////	                    Contact person = new Contact();
////	                    person.setJid(entry.getUser());
////	                    person.setName(entry.getName());
////
////	                    contacts.add(person);
//	                }
//	            }
			
		}};
	ConnectionListener connectionListener = new ConnectionListener(){

//		  @Override  
//		    public void connected(final XMPPConnection connection){  
//		        if(!connection.isAuthenticated())  
//		            login(connection, loginUser, passwordUser);  
//		    }  
		@Override
		public void connectionClosed() {
			// TODO Auto-generated method stub
//			System.out.println("connectionClosed");
		}

		@Override
		public void connectionClosedOnError(Exception e) {
			// TODO Auto-generated method stub
//			System.out.println("connectionClosedOnError : "+e.toString());
			// Judge for the account has been logged
			boolean error = e.getMessage().equals("stream:error (conflict)");
			if (error) {
				// Close the connection and logout as another user has logged in
				String mobileNumber = prefManager.getUserPhone();
				if(mobileNumber!=null && !mobileNumber.equals("")){
//					prefManager.saveUserLogedOut(false);
//					prefManager.setMobileVerified(mobileNumber, false);
					
//					prefManager.setMobileRegistered(mobileNumber, false);
					//Do not clean here - check at the login time,
					//if number is same then don't delete - else delete.
					prefManager.clearSharedPref();
					ChatDBWrapper.getInstance().clearMessageDB();
					DBWrapper.getInstance().clearAllDB();
					
				}
				try{
					Intent intent1 = new Intent(context, ChatService.class);
					if(intent1!=null)
						((SuperChatApplication)context).stopService(intent1);
				}catch(Exception ex){
					
				}
                Intent intent = new Intent(context, RegistrationOptions.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("CONFLICT_LOGOUT", true);
        		startActivity(intent);
			}
		}

		@Override
		public void reconnectingIn(int seconds) {
			// TODO Auto-generated method stub
//			System.out.println("reconnectingIn in :- "+seconds);
		}

		@Override
		public void reconnectionSuccessful() {
			// TODO Auto-generated method stub
//			System.out.println("reconnectionSuccessful");
		}

		@Override
		public void reconnectionFailed(Exception e) {
			// TODO Auto-generated method stub
//			System.out.println("reconnectionSuccessful");
		}
		
	};
	
	public static ConnectionStatusListener connectionStatusListener;
	public static ConnectionStatusListener getConnectionStatusListener() {
		return connectionStatusListener;
	}
	public static void setConnectionStatusListener(
			ConnectionStatusListener connectionStatusListener) {
		ChatService.connectionStatusListener = connectionStatusListener;
	}
	// PacketFilter invitationFilter = new PacketExtensionFilter("x",
	// "jabber:x:conference");
	private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static final int STRIDE = 64;   // must be >= WIDTH
	private static int[] createColors() {
		
        int[] colors = new int[STRIDE * HEIGHT];
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int r = x * 255 / (WIDTH - 1);
                int g = y * 255 / (HEIGHT - 1);
                int b = 255 - Math.min(r, g);
                int a = Math.max(r, g);
                colors[y * STRIDE + x] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }
        return colors;
    }
	PacketFilter invitationFilter = new PacketExtensionFilter("x",
			"http://jabber.org/protocol/muc#user");
	private final IBinder mBinder = new MyBinder();

	public void setChatVisibilty(boolean onForeground){
		this.onForeground = onForeground;
	}
	public void setChatPerson(String currentUser){
		this.currentUser = currentUser;
	}
	PacketListener invitationListener = new PacketListener() {
		public void processPacket(Packet packet) {
			Log.i(TAG, "Got invitation packet " + packet.toXML() + " -- " + packet.getPacketID());
			MUCUser mucUser = (MUCUser) packet.getExtension("x", "http://jabber.org/protocol/muc#user");
			// mucUser.getInvite().getReason()
			String reason = "";
			if (mucUser != null) {
				String groupUUID = null;
				if(packet.toXML().startsWith("<presence")){
					Presence presence = (Presence)packet;
					if(presence.getError() != null && presence.getError().getCode() == 404){
						Log.i(TAG, "Got Error code : " + presence.getError().getCode());
						if(presence.getError().getMessage() != null)
							Toast.makeText(context, "Error in joining : 404 -  " + presence.getError().getMessage(), 1000).show();
						else
							Toast.makeText(context, "Error in joining group : 404 -  " + presence.getFrom(), 1000).show();
						return;
					}
					String senderPerson = "";
					String fromName = presence.getFrom();
					
					if (fromName != null && fromName.contains("@")) {
						groupUUID = fromName.substring(0, fromName.indexOf('@'));
						if(fromName.contains("/")){
							senderPerson =  fromName.substring(fromName.indexOf("/")+1);
						}
						// fromName = AtmeChatClient.getChatDBWrapper().getChatName(user);
					}
					if(groupUUID!=null){
						boolean isGroup = prefManager.isGroupChat(groupUUID);
						if(isGroup){
							String affiliation = mucUser.getItem().getAffiliation();
							String role = mucUser.getItem().getRole();
							if(affiliation!=null){
								//No Need to add this code here, as its already being added at the time of login and also when get profile is done.
//								if(affiliation.equals("owner"))
//									prefManager.saveUserGroupInfo(groupUUID, senderPerson, SharedPrefManager.GROUP_OWNER_INFO, true);
								prefManager.saveGroupInfo(groupUUID,SharedPrefManager.GROUP_ACTIVE_INFO,true);
//								else if(affiliation.equals("none"))
//									prefManager.saveUserGroupInfo(groupUUID, senderPerson, SharedPrefManager.GROUP_OWNER_INFO, false);
//								prefManager.saveUserGroupInfo(groupUUID,senderPerson,SharedPrefManager.GROUP_ACTIVE_INFO,true);
//								try {
//									roster.createEntry(senderPerson, prefManager.getUserServerName(senderPerson), new String[]{groupUUID});
//								} catch (XMPPException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
							}
							if(role!=null){
								if(role.equals("moderator"))
									prefManager.saveUserGroupInfo(groupUUID, senderPerson, SharedPrefManager.GROUP_ADMIN_INFO, true);
								else if(role.equals("participant"))
									prefManager.saveUserGroupInfo(groupUUID, senderPerson, SharedPrefManager.GROUP_ADMIN_INFO, false);
								prefManager.saveUserGroupInfo(groupUUID,senderPerson,SharedPrefManager.GROUP_ACTIVE_INFO,true);
								prefManager.saveGroupInfo(groupUUID,SharedPrefManager.GROUP_ACTIVE_INFO,true);
//								try {
//									roster.createEntry(senderPerson, prefManager.getUserServerName(senderPerson), new String[]{groupUUID});
//								} catch (XMPPException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
							}
						}
					}
					Log.i(TAG, "Got Affiliation and Status: " + mucUser.getStatus().getCode());
					Log.d(TAG, "Got Affiliation and Role: " + presence.getFrom());
					Log.d(TAG, "Got Affiliation and Role: " + mucUser.getItem().getAffiliation()+" , "+mucUser.getItem().getRole());
				}
				Invite invite = mucUser.getInvite();
				if (invite != null) {

					reason = invite.getReason();
					if (reason != null && reason.contains(">>>")){
						if(groupUUID!=null){
							SharedPrefManager.getInstance().saveUserStatusMessage(groupUUID, reason.substring(reason.indexOf(">>>")));
							}
						reason = reason.substring(0, reason.indexOf(">>>"));
					}
				}

			}
			// Check if the MUCUser extension includes an invitation
			GroupChatInvitation roomAddressObj = (GroupChatInvitation) packet.getExtension("x", "jabber:x:conference");
			String roomAddress = roomAddressObj.getRoomAddress();
			Log.d(TAG, "Got roomAddress packet " + roomAddress);
			if (roomAddress != null && !roomAddress.equals("")) {
				sendGroupPresence(roomAddress,0);
				String fromName = StringUtils.parseBareAddress(roomAddress);
				// Log.d(TAG, "Got text [" + message.getBody()
				// + "] from [" + fromName + "]");
				String groupUUID = fromName;
				String senderPerson = "";
				if (fromName != null && fromName.contains("@")) {
					groupUUID = fromName.substring(0, fromName.indexOf('@'));
					if(fromName.contains("\\")){
						senderPerson =  fromName.substring(fromName.indexOf("\\")+1);
					}
					// fromName = AtmeChatClient.getChatDBWrapper().getChatName(user);
				}
				if (reason.equals(""))
					reason = groupUUID;
				Log.d(TAG, "Got Affiliation and Role: " + reason);
				// atMEMulti
				if(reason!=null && reason.equals("atMEMulti"))
					reason = "Multi Chat Room";
				
				SharedPrefManager.getInstance().saveGroupName(groupUUID, reason);
//				String userDisplayName = AtmeChatClient.getChatDBWrapper().getChatName(
//						senderPerson);
				if(senderPerson!=null && !senderPerson.equals("")){
					prefManager.saveUsersOfGroup(groupUUID, senderPerson);
//					try {
//						roster.createEntry(senderPerson, prefManager.getUserServerName(senderPerson), new String[]{groupUUID});
//					} catch (XMPPException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				}
				 sendGroupPresence(groupUUID,0);
				joinMultiUserChat(groupUUID);
			}
			// if (mucUser.getInvite() != null &&
			// ((Message) packet).getType() != Message.Type.error) {
			// Log.d(TAG,
			// "Got invitation packet " + mucUser.getInvite().getFrom() + " -- "
			// + mucUser.getInvite().getReason());
			// String groupName = mucUser.getInvite().getFrom();
			// SharedPrefManager.getInstance().saveGroupName(groupName);
			// sendMessage(groupName, userMe+" has joined "+" "+groupName+".");
			// }
			if(prefManager!=null) //new1
				prefManager.saveLastOnline(System.currentTimeMillis()); //new1
		}
	};
	PacketListener groupPacketListener = new PacketListener() {
		@Override
		public void processPacket(Packet packet) {
//			System.out.println("Got group packet " + packet.toXML() + " -- " + packet.getPacketID());

			if (!packet.toXML().contains("<message"))
				return;
			try {
				final Message message = (Message) packet;
				int xMPPMessageType = message.getXMPPMessageType().ordinal();
				
				Log.d(TAG,
						"Message Seen State processPacket: "
								+ message.getMessageSeenState()+" ,, "+xMPPMessageType);
				// Log.d(TAG, "message body " + message.getBody());
				String fromName = null;
				if (message != null) {
					String from = message.getFrom();
					fromName = StringUtils.parseBareAddress(from);
					String senderName = "";
					if (from != null && from.contains("/")) {
						senderName = from.substring(from.indexOf("/") + 1);
					}
					// Log.d(TAG, "Got text [" + message.getBody()
					// + "] from [" + fromName + "]");
					
					String user = fromName;
					if (fromName != null && fromName.contains("@")) {
						user = fromName.substring(0, fromName.indexOf('@'));
						fromName = DBWrapper.getInstance().getChatName(
								senderName);
					}
					
					if (xMPPMessageType == XMPPMessageType.atMeXmppMessageTypeGroupName.ordinal()) {
						Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeGroupName: "+ xMPPMessageType);
						String inviter = senderName;
						
						String captionTag  = message.getMediaTagMessage();
						String description = null;
						String fileId = null;
						if(captionTag != null){
							if(captionTag.contains("&quot;"))
								captionTag = captionTag.replace("&quot;", "\"");
							try {
								JSONObject jsonobj = new JSONObject(captionTag);
								if(jsonobj.has("description") && jsonobj.getString("description").toString().trim().length() > 0) {
									description = jsonobj.getString("description").toString();
								}
								if(jsonobj.has("fileId") && jsonobj.getString("fileId").toString().trim().length() > 0) {
									fileId = jsonobj.getString("fileId").toString();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						
						if(userMe.equals(inviter))
							inviter = "You";
						else
							inviter = DBWrapper.getInstance().getChatName(senderName);
						
						 if(inviter!=null && inviter.contains("#786#"))
							 inviter = inviter.substring(0, inviter.indexOf("#786#"));
						 
						String newGroupName = message.getBody();
						if(senderName.equals(inviter)){
							if(inviter.contains("_"))
								inviter = "+"+inviter.substring(0, inviter.indexOf("_"));
//							inviter = inviter.replaceFirst("m", "+");
							}
						 String message_ID = message.getPacketID();
						 if(message_ID!=null && !message_ID.equals("")){
							boolean isDuplicate = chatDBWrapper.isDuplicateMessage(user, message_ID);
							if(!isDuplicate){
								boolean isChannel = SharedPrefManager.getInstance().isPublicGroup(user);
								if(inviter.equals("You")){
									String tmpText = " updated group info.";
									if(isChannel)
										tmpText = " updated open group info.";
									saveInfoMessage(SharedPrefManager.getInstance().getGroupDisplayName(user), user, inviter+tmpText,message_ID);
								}else{
									String tmpText = " updated group info.";
									if(isChannel)
										tmpText = " updated open group info.";
									saveInfoMessage(SharedPrefManager.getInstance().getGroupDisplayName(user), user, inviter+tmpText,message_ID);
								}
							 }
							}
						 prefManager.saveGroupName(user,newGroupName);
						//Save group Name
						prefManager.saveGroupDisplayName(user,newGroupName);
						//Save Group Description
						if(description != null)
							prefManager.saveUserStatusMessage(user, description);
						//Save group picture
						if(fileId != null)
							prefManager.saveUserFileId(user, fileId);
						if(typingNotifier!=null)
							typingNotifier.refreshSubjectOfGroup();
						if (profileUpdateNotifier != null)
							profileUpdateNotifier.notifyProfileUpdate(user, description, newGroupName);
						return;
					} else if (xMPPMessageType == XMPPMessageType.atMeXmppMessageTypeMemberList.ordinal()) {
						String message_ID = message.getPacketID();
						String captionTag  = message.getMediaTagMessage();
						String description = null;
						String memberCounts = null;
						String fileId = null;
						if(captionTag != null){
							if(captionTag.contains("&quot;"))
								captionTag = captionTag.replace("&quot;", "\"");
							try {
								JSONObject jsonobj = new JSONObject(captionTag);
								if(jsonobj.has("description") && jsonobj.getString("description").toString().trim().length() > 0) {
									description = jsonobj.getString("description").toString();
								}
								if(jsonobj.has("fileId") && jsonobj.getString("fileId").toString().trim().length() > 0) {
									fileId = jsonobj.getString("fileId").toString();
								}
								if(jsonobj.has("memberCount") && jsonobj.getString("memberCount").toString().trim().length() > 0) {
									memberCounts = jsonobj.getString("memberCount").toString();
									if(memberCounts!=null && !memberCounts.equals("")){
										int membersSize = Integer.parseInt(memberCounts);
//										String storedCount = prefManager.getGroupMemberCount(user);
										prefManager.saveGroupMemberCount(user, String.valueOf(membersSize));
//										if(storedCount!=null && !storedCount.equals("")){
//											membersSize = membersSize + Integer.parseInt(storedCount);
//											prefManager.saveGroupMemberCount(user, String.valueOf(membersSize));
//										}else{
//											prefManager.saveGroupMemberCount(user, String.valueOf(membersSize));
//										}
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						
						boolean isDuplicate = chatDBWrapper.isDuplicateMessage(user, message_ID);
						if(isDuplicate){
							if(prefManager!=null) //new1
								prefManager.saveLastOnline(System.currentTimeMillis()); //new1
							return;
						}
						Log.d(TAG,
								"XMPPMessageType atMeXmppMessageTypeMemberList: "
										+ xMPPMessageType+""+message.getBody());
						String list = message.getBody();
						if(captionTag!=null){
								try {
									JSONObject jsonobj = new JSONObject(captionTag);
									if(jsonobj.has("Members") && jsonobj.getString("Members").toString().trim().length() > 0) {
										list = jsonobj.getString("Members").toString();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						if(list!=null && !list.equals("")){
							
							
							for(String gp:list.split(",")){
								String tmpUser = gp;
								if(SharedPrefManager.getInstance().isUserInvited(tmpUser)){
									continue;
								}
								boolean isNewAdded =  prefManager.saveUsersOfGroup(user, gp);
								if(isNewAdded){
									 prefManager.saveUserGroupInfo(user,gp,SharedPrefManager.GROUP_ACTIVE_INFO,true);
									 prefManager.saveGroupInfo(user,SharedPrefManager.GROUP_ACTIVE_INFO,true);
									 if(!userMe.equals(gp))
										 gp = DBWrapper.getInstance().getChatName(gp);
									 else
										 gp = "You";
									String inviter = senderName;
									if(userMe.equals(inviter))
										inviter = "You";
									else
										inviter = DBWrapper.getInstance().getChatName(senderName);
									if(inviter!=null && inviter.contains("#786#")){
										 inviter = inviter.substring(0, inviter.indexOf("#786#"));
										 }
									if(gp!=null && gp.contains("#786#"))
										gp = gp.substring(0, gp.indexOf("#786#"));
									
									if(!gp.equalsIgnoreCase(inviter) && !gp.equals("")){
										Log.d(TAG,gp+" group persons added "+ SharedPrefManager.getInstance().getGroupDisplayName(user));
										if(tmpUser.equals(gp)){
											if(tmpUser.contains("_"))
												tmpUser = "+"+gp.substring(0, gp.indexOf("_"));
//											tmpUser = gp.replaceFirst("m", "+");
										}else
											tmpUser = gp;
										if(senderName.equals(inviter)){
											if(inviter.contains("_"))
												inviter = "+"+inviter.substring(0, inviter.indexOf("_"));
//											inviter = inviter.replaceFirst("m", "+");
											}
										saveInfoMessage(SharedPrefManager.getInstance().getGroupDisplayName(user), user, inviter+" added \""+tmpUser+"\".",message_ID);
									}
								}
							}
							
						}
						
						//Save Group Description
						if(description != null)
							prefManager.saveUserStatusMessage(user, description);
						//Save group picture
						if(fileId != null)
							prefManager.saveUserFileId(user, fileId);
						if(typingNotifier!=null)
							typingNotifier.refreshOnlineGroupUser();
						return;
					} else if (xMPPMessageType == XMPPMessageType.atMeXmppMessageTypeJoinGroup
							.ordinal()) {
						Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeJoinGroup: "
								+ xMPPMessageType);
						return;
					} else if (xMPPMessageType == XMPPMessageType.atMeXmppMessageTypeLeftGroup.ordinal()) {
						Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeLeftGroup: " + xMPPMessageType);
						String list = message.getBody();
						String captionTag = message.getMediaTagMessage();
						String removed_user = null;
						try {
							if(captionTag.contains("&quot;"))
								captionTag = captionTag.replace("&quot;", "\"");
							JSONObject jsonobj = new JSONObject(captionTag);
							if(jsonobj.has("userName") && jsonobj.getString("userName").toString().trim().length() > 0) {
								removed_user = jsonobj.getString("userName").toString();
							}
							if(removed_user != null && jsonobj.has("displayName") && jsonobj.getString("displayName").toString().trim().length() > 0) {
//								displayname = jsonobj.getString("displayname").toString();
								prefManager.saveUserServerName(removed_user, jsonobj.getString("displayName").toString());
							}
							if(removed_user != null && jsonobj.has("fileId") && jsonobj.getString("fileId").toString().trim().length() > 0) {
//								fileId = jsonobj.getString("fileId").toString();
								prefManager.saveUserFileId(removed_user, jsonobj.getString("fileId").toString());
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						String persons = "";
						boolean isGroupDeactivated = false;
						int membersSize = 1;//list.split(",").length;
						String storedCount = prefManager.getGroupMemberCount(user);
						if(!userMe.equals(senderName))
						if(storedCount!=null && !storedCount.equals("")){
							membersSize = Integer.parseInt(storedCount);
							if(membersSize>0)
								membersSize = membersSize - 1;
							prefManager.saveGroupMemberCount(user, String.valueOf(membersSize));
						}
						if(list!=null && !list.equals("")){
							
							for(String gp:list.split(",")){
								String tmpUser = gp;
								 
								if(prefManager.isOwner(user, tmpUser)){
									isGroupDeactivated = true;
									prefManager.saveUserGroupInfo(user,userMe,SharedPrefManager.GROUP_ACTIVE_INFO,false);
									prefManager.saveGroupInfo(user,SharedPrefManager.GROUP_ACTIVE_INFO,false);
								}
								 prefManager.saveUserGroupInfo(user,gp,SharedPrefManager.GROUP_ACTIVE_INFO,false);
								 if(!userMe.equals(gp))
									 persons += DBWrapper.getInstance().getChatName(gp);
								 else
									 persons += "You";
								 if(persons!=null && persons.contains("#786#"))
									 persons = persons.substring(0, persons.indexOf("#786#"));
								 if(tmpUser.equals(persons)){
									 tmpUser = prefManager.getUserServerName(tmpUser);
									 if(tmpUser.contains("_"))
										 tmpUser = "+"+persons.substring(0, persons.indexOf("_"));
//										tmpUser = persons.replaceFirst("m", "+");
									}else
										tmpUser = persons;
								 if(isGroupDeactivated){
									 String message_ID = message.getPacketID();
									 boolean isChannel = SharedPrefManager.getInstance().isPublicGroup(user);
									 if(message_ID!=null && !message_ID.equals("")){
										boolean isDuplicate = chatDBWrapper.isDuplicateMessage(user, message_ID);
										String tmpText = "Group has been deactivated.";
										if(isChannel)
											tmpText = "Open group has been deactivated.";
										if(!isDuplicate)
											saveInfoMessage(SharedPrefManager.getInstance().getGroupDisplayName(user), user, tmpText,message_ID);
									 }else{
										 String tmpText = "Group has been deactivated.";
											if(isChannel)
												tmpText = "Open group has been deactivated.";
										 saveInfoMessage(SharedPrefManager.getInstance().getGroupDisplayName(user), user, tmpText);
									 }
									 }else if(gp.equals(senderName)){
									
									
									boolean isAdmin = prefManager.isAdmin(user, gp);
									if(isAdmin){
										prefManager.saveUserGroupInfo(user,gp,SharedPrefManager.GROUP_ADMIN_INFO,false);
//										prefManager.saveUserGroupInfo(user,gp,SharedPrefManager.GROUP_OWNER_INFO,false);
									}
									prefManager.removeUsersFromGroup(user,gp);
									
									if(isAdmin && userMe.equals(gp)){
										ArrayList<String> userList = prefManager.getGroupUsersList(user);
										 boolean isChannel = SharedPrefManager.getInstance().isPublicGroup(user);
										 String tmpText = " left the group.";
											if(isChannel)
												tmpText = " left the open group.";
										if(userList!=null && !userList.isEmpty()){
											String newAdmin = userList.get(0);
											sendInfoMessage(user,newAdmin,Message.XMPPMessageType.atMeXmppMessageTypeGroupAdminUpdate);
//											prefManager.saveUserGroupInfo(user,newAdmin,SharedPrefManager.GROUP_ADMIN_INFO,true);
//											prefManager.saveUserGroupInfo(user,newAdmin,SharedPrefManager.GROUP_OWNER_INFO,true);
										}else
											saveInfoMessage(SharedPrefManager.getInstance().getGroupDisplayName(user), user, tmpUser+tmpText);
									}else{
										 boolean isChannel = SharedPrefManager.getInstance().isPublicGroup(user);
										 String tmpText = " left the group.";
											if(isChannel)
												tmpText = " left the open group.";
										saveInfoMessage(SharedPrefManager.getInstance().getGroupDisplayName(user), user, tmpUser+tmpText);
										}
								}else if(userMe.equals(senderName)){	
									String message_ID = message.getPacketID();
									boolean isDuplicate = chatDBWrapper.isDuplicateMessage(user, message_ID);
									if(isDuplicate){
										if(prefManager!=null) //new1
											prefManager.saveLastOnline(System.currentTimeMillis()); //new1
										return;
									}
									prefManager.removeUsersFromGroup(user,gp);
									String name = SharedPrefManager.getInstance().getUserServerName(gp);
									if(tmpUser != null && (name.equalsIgnoreCase("null") || name.equalsIgnoreCase(gp)))
										name = "Superchatter";
									saveInfoMessage(SharedPrefManager.getInstance().getGroupDisplayName(user), user, "You removed \""+name+"\".",message_ID);
								}else{
									String message_ID = message.getPacketID();
									boolean isDuplicate = chatDBWrapper.isDuplicateMessage(user, message_ID);
									if(isDuplicate){
										if(prefManager!=null) //new1
											prefManager.saveLastOnline(System.currentTimeMillis()); //new1
										return;
									}
									prefManager.removeUsersFromGroup(user,gp);
									if(fromName!=null && fromName.contains("#786#"))
										fromName = fromName.substring(0, fromName.indexOf("#786#"));
									if(tmpUser.equalsIgnoreCase(user))
										tmpUser = SharedPrefManager.getInstance().getUserServerName(tmpUser);
									saveInfoMessage(SharedPrefManager.getInstance().getGroupDisplayName(user), user, fromName+" removed \""+tmpUser+"\".",message_ID);
								}
							}
//							if(persons.contains(","))
//								persons = persons.substring(0, persons.length()-1);
						}
						//fromName senderName
						
						if(typingNotifier!=null)
							typingNotifier.refreshOnlineGroupUser();
						return;
					} else if (xMPPMessageType == XMPPMessageType.atMeXmppMessageTypeGroupAdminUpdate
							.ordinal()) {
						Log.d(TAG,
								"XMPPMessageType atMeXmppMessageTypeGroupAdminUpdate: "
										+ xMPPMessageType);
						String list = message.getBody();
						if(list!=null && !list.equals("")){  //vikash has left the group and promoted prakash as admin
							String persons = "";
							if(!userMe.equals(list))
								 persons += DBWrapper.getInstance().getChatName(list);
							 else
								 persons += "You";
							
							if(persons!=null && persons.contains("#786#"))
								 persons = persons.substring(0, persons.indexOf("#786#"));
							if(fromName!=null && fromName.contains("#786#"))
								fromName = fromName.substring(0, fromName.indexOf("#786#"));
							String tmpUser = persons;
							 if(tmpUser.equals(list)){
								 if(tmpUser.contains("_"))
									 tmpUser = "+"+persons.substring(0, persons.indexOf("_"));
//									tmpUser = persons.replaceFirst("m", "+");
								}else
									tmpUser = persons;
							 String tmpFromName = fromName;
							 if(senderName.equals(fromName) && tmpFromName.contains("_")){
								 tmpFromName = "+"+tmpFromName.substring(0, tmpFromName.indexOf("_"));
//								 tmpFromName = tmpFromName.replaceFirst("m", "+");
							 }
							 boolean isChannel = SharedPrefManager.getInstance().isPublicGroup(user);
							
							if(userMe.equals(senderName)){
								 String tmpText = "You left the group and promoted  \"";
									if(isChannel)
										tmpText = "You left the open group and promoted  \"";
								saveInfoMessage(SharedPrefManager.getInstance().getGroupDisplayName(user), user, tmpText+tmpUser+"\" as admin.");
						}else{
							 String tmpText = " left the group and promoted  \"";
								if(isChannel)
									tmpText = " left the open group and promoted  \"";
								saveInfoMessage(SharedPrefManager.getInstance().getGroupDisplayName(user), user, tmpFromName+tmpText+tmpUser+"\" as admin.");
						}
							prefManager.saveUserGroupInfo(user,list,SharedPrefManager.GROUP_ADMIN_INFO,true);
							prefManager.saveUserGroupInfo(user,list,SharedPrefManager.GROUP_OWNER_INFO,true);
						}
						return;
					}else if (xMPPMessageType == XMPPMessageType.atMeXmppMessageTypeGroupWelcomeMessage.ordinal()) {
						if(!userMe.equals(senderName)){
							if(message.getPacketID()!=null){
								boolean isDuplicate = chatDBWrapper.isDuplicateMessage(user, message.getPacketID());
								if(isDuplicate)
									return;
							}
							String welcomeMessage = "You are welcome in "+SharedPrefManager.getInstance().getGroupDisplayName(user);
							String message_ID = message.getPacketID();
							if (message.getBody() != null
									&& !message.getBody().equals("")) {
								welcomeMessage = message.getBody();
							}
							saveInfoMessage(SharedPrefManager.getInstance().getGroupDisplayName(user), user, welcomeMessage,message_ID);
							
						}
						return;
					}
					if (senderName.equals(userMe)
							|| !prefManager.isGroupChat(
									user) || !prefManager.isGroupMemberActive(user, userMe)) {
						Log.d(TAG, "Self messaging is not allowed.");
						return;
					}
					// if (message.getMessageSeenState() ==
					// Message.SeenState.recieved
					// || message.getMessageSeenState() ==
					// Message.SeenState.seen) {
					// AtmeChatClient.getChatDBWrapper().updateSeenStatus(user,
					// convertStringToWhereAs(message.getPacketID()),
					// message.getMessageSeenState());
					// if (chatCountListener != null)
					// chatCountListener.notifyChatRecieve();
					// if (chatListener != null)
					// chatListener.notifyChatRecieve();
					// return;
					// }
					if (message.getBody() != null
							&& !message.getBody().equals("")) {
						prefManager.saveChatCounter(prefManager
								.getChatCounter() + 1);
						int messageCount = prefManager.getChatCounter();
//	                    ShortcutBadger.setBadge(getApplicationContext(), badgeCount);
	                ShortcutBadger.with(SuperChatApplication.context).count(messageCount);
						prefManager.saveChatCountOfUser(user,
								prefManager.getChatCountOfUser(user) + 1);
						if(prefManager.getUserFileId(senderName) == null && message.getPicId() != null)
							prefManager.saveUserFileId(senderName, message.getPicId());
						message.setMessageSeenState(Message.SeenState.recieved);
						if(fromName.equals(senderName)){
							if(senderName.contains("_"))
							fromName = "+"+senderName.substring(0, senderName.indexOf("_"));//senderName.replaceFirst("m", "+");
						}
						String me = prefManager.getUserName();
						if(!me.equals(senderName)){
							boolean isDuplicate = chatDBWrapper.isDuplicateMessage(user, message.getPacketID());
							if(isDuplicate){
								if(prefManager!=null) //new1
									prefManager.saveLastOnline(System.currentTimeMillis()); //new1
								return;
							}
						}
						showNotificationForMessage(senderName, user, fromName, message.getBody(), message);
						if (chatCountListener != null)
							chatCountListener.notifyChatRecieve(user,message.getBody());
						if (((ChatListScreen.onForeground && !ChatListScreen.currentUser.equals(user)) || !ChatListScreen.onForeground)) {
							List<String> strList = new ArrayList<String>();
							strList.add(message.getPacketID());
							sendGroupOrBroadcastAck(senderName, strList, Message.SeenState.recieved,user);
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.d(TAG,
						"Exception occured during processPacket."
								+ e.toString());
			}
			if(prefManager!=null) //new1
				prefManager.saveLastOnline(System.currentTimeMillis()); //new1
		}
	};
	
	PacketListener typingListener = new PacketListener() {
		public void processPacket(Packet packet) {
			//For group
			//<message to="919910068484_readmark@52.88.175.48/Smack" from="mahesh_test_group_readmark@conference.52.88.175.48/918884786865_readmark" 
			//status="1" XMPPMessageType="0"status_message_type="0" type="groupchat"><composing xmlns="http://jabber.org/protocol/chatstates" /></message>
			
			//For Individual
			//<message to="919910068484_readmark@52.88.175.48" from="918884786865_readmark@52.88.175.48/fc4c4d30" 
			//status="1" XMPPMessageType="0"status_message_type="0" type="chat"><composing xmlns="http://jabber.org/protocol/chatstates" /></message>
			
			Log.d(TAG, "typingListener: " + packet.toXML());

			Message message = (Message) packet;
			String msgFrom = message.getFrom();
//			String groupSender = msgFrom.substring(msgFrom.lastIndexOf('/') + 1);
			Type type = message.getType();
			
//			boolean isGroup = SharedPrefManager.getInstance().isGroupChat(groupSender);//msgFrom.substring(msgFrom.lastIndexOf('/') + 1));
			
			//Message.Type.groupchat
			// Log.d(TAG, "who is typing now : " + msgFrom);
			if (msgFrom != null && msgFrom.contains("@")  && type == Message.Type.chat) {
				
					msgFrom = msgFrom.substring(0, msgFrom.indexOf('@'));
					Log.i(TAG, "typingListener: For P2P : msgFrom : " + msgFrom);
					startUserTyping(msgFrom);
			}
			if(prefManager!=null) //new1
				prefManager.saveLastOnline(System.currentTimeMillis()); //new1
		}
		
	};
	PacketListener typingGroupListener = new PacketListener() {
		public void processPacket(Packet packet) {
			//For group
			//<message to="919910068484_readmark@52.88.175.48/Smack" from="mahesh_test_group_readmark@conference.52.88.175.48/918884786865_readmark" 
			//status="1" XMPPMessageType="0"status_message_type="0" type="groupchat"><composing xmlns="http://jabber.org/protocol/chatstates" /></message>
			
			//For Individual
			//<message to="919910068484_readmark@52.88.175.48" from="918884786865_readmark@52.88.175.48/fc4c4d30" 
			//status="1" XMPPMessageType="0"status_message_type="0" type="chat"><composing xmlns="http://jabber.org/protocol/chatstates" /></message>
			
			Log.d(TAG, "typingGroupListener: " + packet.toXML());

			Message message = (Message) packet;
			String msgFrom = message.getFrom();
			String groupSender = msgFrom.substring(msgFrom.lastIndexOf('/') + 1);
			Type type = message.getType();
			//Message.Type.groupchat
			// Log.d(TAG, "who is typing now : " + msgFrom);
			if (msgFrom != null && msgFrom.contains("@") && type == Message.Type.groupchat) {
//				if(isGroup){//isGroup != null && isGroup.length() > 12 && isGroup.indexOf('_') != -1){
					msgFrom = msgFrom.substring(0, msgFrom.indexOf('@'));
//					msgFrom = msgFrom + "@" + isGroup;
					Log.i(TAG, "typingListener: For Group : msgFrom : " + msgFrom+" , "+groupSender);
					if(!groupSender.equals(SharedPrefManager.getInstance().getUserName()))
						startUserTypingForGroup(msgFrom, groupSender);
//				}
				
			}
			if(prefManager!=null) //new1
				prefManager.saveLastOnline(System.currentTimeMillis()); //new1
		}
		
	};
	Timer timer = null;
	TimerTask timerTask;
	public void startUserTyping(final String user) {
		if(timer == null){
		 timer = new Timer();
		 timerTask = new TimerTask() {

			@Override
			public void run() {
				SharedPrefManager.getInstance().saveUserTypingStatus(user, false);
				if (chatListener != null)
					chatListener.notifyChatHome(user,null);
				if (typingNotifier != null)
					typingNotifier.notifyTypingRecieve(user);
				cancel();
				timer = null;
				timerTask = null;
			}
		};
		SharedPrefManager.getInstance().saveUserTypingStatus(user, true);

		if (chatListener != null)
			chatListener.notifyChatHome(user,null);
		if (typingNotifier != null)
			typingNotifier.notifyTypingRecieve(user);
		timer.schedule(timerTask, 3000);
	}
	}
	public void startUserTypingForGroup(final String group, final String user) {
		if(timer == null){
			timer = new Timer();
			timerTask = new TimerTask() {
				
				@Override
				public void run() {
					SharedPrefManager.getInstance().saveUserTypingStatusForGroup(group, null);
					if (chatListener != null)
						chatListener.notifyChatHome(user,null);
					if (typingNotifier != null)
						typingNotifier.notifyTypingRecieve(user);
					cancel();
					timer = null;
					timerTask = null;
				}
			};
			SharedPrefManager.getInstance().saveUserTypingStatusForGroup(group, user);
			
			if (chatListener != null)
				chatListener.notifyChatHome(user,null);
			if (typingNotifier != null)
				typingNotifier.notifyTypingRecieve(user);
			timer.schedule(timerTask, 3000);
		}
	}

	PacketListener recordStatusListener = new PacketListener() {
		public void processPacket(Packet packet) {
			Log.d(TAG, "recordStatusListener: " + packet.toXML());

			Message message = (Message) packet;
			String msgFrom = message.getFrom();
			String isGroup = msgFrom.substring(msgFrom.lastIndexOf('/') + 1);
			Type type = message.getType();
			// Log.d(TAG, "who is typing now : " + msgFrom);
			if (msgFrom != null && msgFrom.contains("@")) {
				if(type == Message.Type.groupchat){//isGroup != null && isGroup.length() > 12 && isGroup.indexOf('_') != -1){
					msgFrom = msgFrom.substring(0, msgFrom.indexOf('@'));
//					msgFrom = msgFrom + "@" + isGroup;
					Log.i(TAG, "typingListener: For Group : msgFrom : " + msgFrom);
					if(!isGroup.equals(SharedPrefManager.getInstance().getUserName()))
						startUserRecordStatusForGroup(msgFrom, isGroup);
				}
//				else{
//					msgFrom = msgFrom.substring(0, msgFrom.indexOf('@'));
//					startUserRecordStatus(msgFrom);
//				}
			}
			if(prefManager!=null) //new1
				prefManager.saveLastOnline(System.currentTimeMillis()); //new1
		}
		
	};
	Timer timerRecordStatus = null;
	TimerTask timerTaskRecordStatus;
	public void startUserRecordStatus(final String user) {
		if(timerRecordStatus == null){
		 timerRecordStatus = new Timer();
		 timerTaskRecordStatus = new TimerTask() {

			@Override
			public void run() {
				SharedPrefManager.getInstance().saveUserRecordingStatus(user, false);
				if (chatListener != null)
					chatListener.notifyChatHome(user,null);
				if (typingNotifier != null)
					typingNotifier.notifyRecordStatusRecieve(user);
				cancel();
				timerRecordStatus = null;
				timerTaskRecordStatus = null;
			}
		};
		SharedPrefManager.getInstance().saveUserRecordingStatus(user, true);

		if (chatListener != null)
			chatListener.notifyChatHome(user,null);
		if (typingNotifier != null)
			typingNotifier.notifyRecordStatusRecieve(user);
		timerRecordStatus.schedule(timerTaskRecordStatus, 3000);
	}
	}
	public void startUserRecordStatusForGroup(final String group, final String user) {
		if(timerRecordStatus == null){
			timerRecordStatus = new Timer();
			timerTaskRecordStatus = new TimerTask() {
				
				@Override
				public void run() {
					SharedPrefManager.getInstance().saveUserRecordingStatusForGroup(group, null);
					if (chatListener != null)
						chatListener.notifyChatRecieve(user,null);
					if (typingNotifier != null)
						typingNotifier.notifyRecordStatusRecieve(user);
					cancel();
					timerRecordStatus = null;
					timerTaskRecordStatus = null;
				}
			};
			SharedPrefManager.getInstance().saveUserRecordingStatusForGroup(group, user);
			
			if (chatListener != null)
				chatListener.notifyChatRecieve(user,null);
			if (typingNotifier != null)
				typingNotifier.notifyRecordStatusRecieve(user);
			timerRecordStatus.schedule(timerTaskRecordStatus, 3000);
		}
	}
	
	PacketListener listeningStatusListener = new PacketListener() {
		public void processPacket(Packet packet) {
			Log.d(TAG, "listeningStatusListener: " + packet.toXML());

			Message message = (Message) packet;
			String msgFrom = message.getFrom();
			String isGroup = msgFrom.substring(msgFrom.lastIndexOf('/') + 1);
			Type type = message.getType();
			// Log.d(TAG, "who is typing now : " + msgFrom);
			if (msgFrom != null && msgFrom.contains("@")) {
				if(type == Message.Type.groupchat){//if(isGroup != null && isGroup.length() > 12 && isGroup.indexOf('_') != -1){
					msgFrom = msgFrom.substring(0, msgFrom.indexOf('@'));
//					msgFrom = msgFrom + "@" + isGroup;
					Log.i(TAG, "typingListener: For Group : msgFrom : " + msgFrom);
					if(!isGroup.equals(SharedPrefManager.getInstance().getUserName()))
						startUserListeningStatusForGroup(msgFrom, isGroup);
				}
//				else{
//					msgFrom = msgFrom.substring(0, msgFrom.indexOf('@'));
//					startUserListeningStatus(msgFrom);
//				}
			}
			if(prefManager!=null) //new1
				prefManager.saveLastOnline(System.currentTimeMillis()); //new1
		}
		
	};
	Timer timerListeningStatus = null;
	TimerTask timerTaskListeningStatus;
	public void startUserListeningStatus(final String user) {
		if(timerListeningStatus == null){
		 timerListeningStatus = new Timer();
		 timerTaskListeningStatus = new TimerTask() {

			@Override
			public void run() {
				SharedPrefManager.getInstance().saveUserListeningStatus(user, false);
				if (chatListener != null)
					chatListener.notifyChatHome(user,null);
				if (typingNotifier != null)
					typingNotifier.notifyListeningStatusRecieve(user);
				cancel();
				timerListeningStatus = null;
				timerTaskListeningStatus = null;
			}
		};
		SharedPrefManager.getInstance().saveUserListeningStatus(user, true);

		if (chatListener != null)
			chatListener.notifyChatHome(user,null);
		if (typingNotifier != null)
			typingNotifier.notifyListeningStatusRecieve(user);
		timerListeningStatus.schedule(timerTaskListeningStatus, 3000);
	}
	}
	public void startUserListeningStatusForGroup(final String group, final String user) {
		if(timerListeningStatus == null){
			timerListeningStatus = new Timer();
			timerTaskListeningStatus = new TimerTask() {
				
				@Override
				public void run() {
					SharedPrefManager.getInstance().saveUserListeningStatusForGroup(group, "");
					if (chatListener != null)
						chatListener.notifyChatHome(user,null);
					if (typingNotifier != null)
						typingNotifier.notifyListeningStatusRecieve(user);
					cancel();
					timerListeningStatus = null;
					timerTaskListeningStatus = null;
				}
			};
			SharedPrefManager.getInstance().saveUserListeningStatusForGroup(group, user);
			
			if (chatListener != null)
				chatListener.notifyChatHome(user,null);
			if (typingNotifier != null)
				typingNotifier.notifyListeningStatusRecieve(user);
			timerListeningStatus.schedule(timerTaskListeningStatus, 3000);
		}
	}
	ChatManagerListener chatManagerListener = new ChatManagerListener() {

		@Override
		public void chatCreated(final Chat arg0, final boolean arg1) {
			// TODO Auto-generated method stub

			arg0.addMessageListener(new MessageListener() {

				@Override
				public void processMessage(Chat arg0, Message arg1) {
					// TODO Auto-generated method stub
					Log.d("Chat State", "typing called..");
					if (ChatState.composing.equals(arg1)) {
						Log.d("Chat State", arg0.getParticipant()
								+ " is typing..");
					} else if (ChatState.gone.equals(arg1)) {
						Log.d("Chat State", arg0.getParticipant()
								+ " has left the conversation.");
					} else {
						Log.d("Chat State",
								arg0.getParticipant() + ": " + arg1.getFrom());
					}
				}

			});
		}
	};

	PacketListener packetListener = new PacketListener() {

		@Override
		public void processPacket(Packet packet) {

			if (!packet.toXML().contains("<message"))
				return;
			try {
				Log.i(TAG, "Got packet " + packet.toXML() + " -- "+ packet.getPacketID());
				// Collection<PacketExtension> col = packet.getExtensions();
				// PacketExtension extens = (PacketExtension)col.toArray()[0];
				//
				// Log.d(TAG, "PacketExtension size: " +col.size()
				// +" , "+extens.getElementName()+" -- "+extens.toXML());

				final Message message = (Message) packet;
				Log.d(TAG, "Message Subject: "+ message.getSubject());
				Log.d(TAG, "Message Seen State processPacket: "+ message.getMessageSeenState());
				
				// Log.d(TAG, "message body " + message.getBody());
				String fromName = null;
				if (message != null) {
					int xMPPMessageType = message.getXMPPMessageType().ordinal();
					if (xMPPMessageType == XMPPMessageType.atMeXmppMessageTypeActivateUser.ordinal()){
						String captionTag  = message.getMediaTagMessage();
						prefManager.saveUserExistence(captionTag, true);
						return;
					}else if (xMPPMessageType == XMPPMessageType.atMeXmppMessageTypeMakeSGSubAdmin.ordinal()){
                        prefManager.setAsDomainSubAdmin(true);
                    }else if (xMPPMessageType == XMPPMessageType.atMeXmppMessageTypeRemoveSGSubAdmin.ordinal()){
                        prefManager.setAsDomainSubAdmin(false);
                    }else if(xMPPMessageType == XMPPMessageType.atMeXmppMessageTypeDeactivateUser.ordinal()){
						Log.d(TAG, "atMeXmppMessageTypeDeactivateUser: User deactivated.");
						String captionTag  = message.getMediaTagMessage();
						prefManager.saveUserExistence(captionTag, false);
//						prefManager.saveMyExistence(false);
						if (captionTag.equals(userMe)) {
							// Close the connection and logout as another user has logged in
							String mobileNumber = prefManager.getUserPhone();
							if(mobileNumber!=null && !mobileNumber.equals("")){
//								prefManager.saveUserLogedOut(false);
//								prefManager.setMobileVerified(mobileNumber, false);
//								prefManager.setMobileRegistered(mobileNumber, false);
								prefManager.clearSharedPref();
								ChatDBWrapper.getInstance().clearMessageDB();
								DBWrapper.getInstance().clearAllDB();
								
							}
			                Intent intent = new Intent(context, RegistrationOptions.class);
			                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
			                intent.putExtra("CONFLICT_LOGOUT", true);
			        		startActivity(intent);
			        		try{
								 Intent intent1 = new Intent(context, ChatService.class);
								 if(intent1!=null)
								 ((SuperChatApplication)context).stopService(intent1);
							}catch(Exception ex){
								
							}
						}
						return;
					}else if (xMPPMessageType == XMPPMessageType.atMeXmppMessageTypeUserProfileUpdate.ordinal()){
						Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeUserProfileUpdate: "+ xMPPMessageType);
						String captionTag  = message.getMediaTagMessage();
						String displayname = null;
						String userName = null;
						String statusMessage = null;
						String fileId = null;
						String flatNumber = null;
						String buildingNumber = null;
						String address = null;
						String residenceType = null;
						 
						int dnc = -1;
						int dnm = -1;
						if(captionTag != null){
							if(captionTag.contains("&quot;"))
								captionTag = captionTag.replace("&quot;", "\"");
							try {
								JSONObject jsonobj = new JSONObject(captionTag);
								if(jsonobj.has("displayname") && jsonobj.getString("displayname").toString().trim().length() > 0) {
									displayname = jsonobj.getString("displayname").toString();
								}
								if(jsonobj.has("userName") && jsonobj.getString("userName").toString().trim().length() > 0) {
									userName = jsonobj.getString("userName").toString();
								}
								if(jsonobj.has("statusMessage") && jsonobj.getString("statusMessage").toString().trim().length() > 0) {
									statusMessage = jsonobj.getString("statusMessage").toString();
								}
								if(jsonobj.has("fileId") && jsonobj.getString("fileId").toString().trim().length() > 0) {
									fileId = jsonobj.getString("fileId").toString();
								}
								if(jsonobj.has("fileId") && jsonobj.getString("fileId").toString().trim().length() > 0) {
									fileId = jsonobj.getString("fileId").toString();
								}
								if(jsonobj.has("flatNumber") && jsonobj.getString("flatNumber").toString().trim().length() > 0) {
									flatNumber = jsonobj.getString("flatNumber").toString();
								}
								if(jsonobj.has("buildingNumber") && jsonobj.getString("buildingNumber").toString().trim().length() > 0) {
									buildingNumber = jsonobj.getString("buildingNumber").toString();
								}
								if(jsonobj.has("residenceType") && jsonobj.getString("residenceType").toString().trim().length() > 0) {
									residenceType = jsonobj.getString("residenceType").toString();
								}
								if(jsonobj.has("address") && jsonobj.getString("address").toString().trim().length() > 0) {
									address = jsonobj.getString("address").toString();
								}
								if(jsonobj.has("privacyStatusMap") && jsonobj.getString("privacyStatusMap").toString().trim().length() > 0){
									String privacyMap = jsonobj.getString("privacyStatusMap");
									if(privacyMap!=null && !privacyMap.equals("")){
										if(privacyMap.contains("&quot;"))
											privacyMap = privacyMap.replace("&quot;", "\"");
										JSONObject jsonobj1 = new JSONObject(privacyMap);
										if(jsonobj1.has("DNC")) {
											dnc = jsonobj1.getInt("DNC");
										}
										if(jsonobj1.has("DNM")) {
											dnm = jsonobj1.getInt("DNM");
										}
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						//Save User DisplayName
						if(userName!=null && !userName.equals("")){
							if(dnc!=-1){
								if(dnc==1)
									prefManager.saveStatusDNC(userName, true);
								else if(dnc==0)
									prefManager.saveStatusDNC(userName, false);
							}
							if(dnm!=-1){
								if(dnm==1)
									prefManager.saveStatusDNM(userName, true);
								else if(dnm==0)
									prefManager.saveStatusDNM(userName, false);
							}
						}
						if(displayname != null){
							if(userName.equals(prefManager.getUserName()))
								prefManager.saveDisplayName(displayname);
							else{
								prefManager.saveUserServerName(userName, displayname);
								DBWrapper.getInstance().updateUserDisplayName(displayname, userName);
							}
						}
						//Save User statusMessage
						if(statusMessage != null)
							prefManager.saveUserStatusMessage(userName, statusMessage);
						//Save group picture
						if(fileId != null)
							prefManager.saveUserFileId(userName, fileId);
						//Save flatNumber, buildingNumber, residenceType, address
						if(flatNumber != null || buildingNumber != null || residenceType != null || address != null){
							DBWrapper wrapper = DBWrapper.getInstance();
							String number = wrapper.getContactNumber(userName);
							UserResponseDetail userDetail = (new GsonBuilder().create()).fromJson(captionTag, UserResponseDetail.class);
							if(number != null && !number.equals("")){
								wrapper.updateUserDetails(number, userDetail);
								}
						}
//						if (profileUpdateNotifier != null){
//							if(profileUpdateNotifier instanceof ContactsScreen || profileUpdateNotifier instanceof EsiaChatContactsScreen
//									|| profileUpdateNotifier instanceof GroupProfileScreen)
//								profileUpdateNotifier.notifyProfileUpdate(userName, statusMessage);
//							else
//								profileUpdateNotifier.notifyProfileUpdate(userName);
//						}
//						if(Build.VERSION.SDK_INT >= 11)
//							new BitmapDownloader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,fileId, BitmapDownloader.THUMB_REQUEST);
//						else
//							new BitmapDownloader().execute(fileId, BitmapDownloader.THUMB_REQUEST);
						if (chatCountListener != null)
							chatCountListener.notifyChatRecieve(userName,null);
						if (chatListener != null)
							chatListener.notifyChatRecieve(userName,null);
						if (profileUpdateNotifier != null)
							profileUpdateNotifier.notifyProfileUpdate(userName, statusMessage, displayname);
						return;
					}else if (xMPPMessageType == XMPPMessageType.atMeXmppMessageTypeUserRegistered.ordinal()){
						Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeUserRegistered: "+ xMPPMessageType);
						String captionTag  = message.getMediaTagMessage();
						String displayname = null;
						String userName = null;
						String statusMessage = null;
						String fileId = null;
						String country_code = null;
						String mobile_number = null;
						if(captionTag != null){
							if(captionTag.contains("&quot;"))
								captionTag = captionTag.replace("&quot;", "\"");
							try {
								JSONObject jsonobj = new JSONObject(captionTag);
								if(jsonobj.has("displayname") && jsonobj.getString("displayname").toString().trim().length() > 0) {
									displayname = jsonobj.getString("displayname").toString();
								}
								if(jsonobj.has("userName") && jsonobj.getString("userName").toString().trim().length() > 0) {
									userName = jsonobj.getString("userName").toString();
								}
								if(jsonobj.has("statusMessage") && jsonobj.getString("statusMessage").toString().trim().length() > 0) {
									statusMessage = jsonobj.getString("statusMessage").toString();
								}
								if(jsonobj.has("mobilenumber") && jsonobj.getString("mobilenumber").toString().trim().length() > 0) {
									mobile_number = jsonobj.getString("mobilenumber").toString();
								}
								if(jsonobj.has("contryCode") && jsonobj.getString("contryCode").toString().trim().length() > 0) {
									country_code = jsonobj.getString("contryCode").toString();
								}
								if(jsonobj.has("fileId") && jsonobj.getString("fileId").toString().trim().length() > 0) {
									fileId = jsonobj.getString("fileId").toString();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						//Save User DisplayName
						if(displayname != null){
							if(userName.equals(prefManager.getUserName()))
								prefManager.saveDisplayName(displayname);
							else{
								prefManager.saveUserServerName(userName, displayname);
								DBWrapper.getInstance().updateUserDisplayName(displayname, userName);
							}
						}
						//Save User statusMessage
						if(statusMessage != null)
							prefManager.saveUserStatusMessage(userName, statusMessage);
						//Save group picture
						if(fileId != null)
							prefManager.saveUserFileId(userName, fileId);
						
						//Save user joined - not invited now
						prefManager.saveUserInvited(userName, false);
						
						//Need to add that contact in DB
						if(message.getDisplayName() != null && !userName.equals(prefManager.getUserName()))
							updateContactcInDB(userName, message.getDisplayName(), mobile_number, 1);
//						if (chatCountListener != null)
//							chatCountListener.notifyChatRecieve(userName,null);
//						if (chatListener != null)
//							chatListener.notifyChatRecieve(userName,null);
//						if (profileUpdateNotifier != null)
//							profileUpdateNotifier.notifyProfileUpdate(userName, statusMessage, displayname);
						return;
					}else if (xMPPMessageType == XMPPMessageType.atMeXmppMessageTypeSharedIDCreated.ordinal() ||
							xMPPMessageType == XMPPMessageType.atMeXmppMessageTypeSharedIDUpdated.ordinal()){
						Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeUserRegistered: "+ xMPPMessageType);
						String captionTag  = message.getMediaTagMessage();
						String userName = null;
						String displayname = null;
						String fileId = null;
						String removedAdmin;
						String sharedID = null;
						String sharedIDName = null;
						String sharedIDDisplayName = null;
						String sharedIDFileID = null;
						if(captionTag != null){
							if(captionTag.contains("&quot;"))
								captionTag = captionTag.replace("&quot;", "\"");
							try {
								JSONObject jsonobj = new JSONObject(captionTag);
								if(jsonobj.has("userName") && jsonobj.getString("userName").toString().trim().length() > 0) {
									userName = jsonobj.getString("userName").toString();
								}
								if(jsonobj.has("adminuser") && jsonobj.getString("adminuser").toString().trim().length() > 0) {
									removedAdmin = jsonobj.getString("adminuser").toString();
								}
								if(jsonobj.has("displayname") && jsonobj.getString("displayname").toString().trim().length() > 0) {
									displayname = jsonobj.getString("displayname").toString();
								}
								if(jsonobj.has("fileId") && jsonobj.getString("fileId").toString().trim().length() > 0) {
									fileId = jsonobj.getString("fileId").toString();
								}
								if(jsonobj.has("sharedID") && jsonobj.getString("sharedID").toString().trim().length() > 0) {
									sharedID = jsonobj.getString("sharedID").toString();
								}
								if(jsonobj.has("sharedIDName") && jsonobj.getString("sharedIDName").toString().trim().length() > 0) {
									sharedIDName = jsonobj.getString("sharedIDName").toString();
								}
								if(jsonobj.has("sharedIDDisplayName") && jsonobj.getString("sharedIDDisplayName").toString().trim().length() > 0) {
									sharedIDDisplayName = jsonobj.getString("sharedIDDisplayName").toString();
								}
								if(jsonobj.has("sharedIDFileID") && jsonobj.getString("sharedIDFileID").toString().trim().length() > 0) {
									sharedIDFileID = jsonobj.getString("sharedIDFileID").toString();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						
//						prefManager.saveSharedIDDisplayName(sharedIDName, sharedIDDisplayName);
//						prefManager.setSharedIDContact(sharedIDName, true);
//						if(sharedIDFileID != null)
//							prefManager.saveSharedIDFileId(sharedIDName, sharedIDFileID);
						
						//Save Shared ID in Preferences
						if(!prefManager.isDomainAdmin() && xMPPMessageType == XMPPMessageType.atMeXmppMessageTypeSharedIDUpdated.ordinal()){
							prefManager.saveSharedIDDisplayName(sharedIDName, sharedIDDisplayName);
							prefManager.setSharedIDContact(sharedIDName, true);
							if(sharedIDFileID != null)
								prefManager.saveSharedIDFileId(sharedIDName, sharedIDFileID);
							if(Build.VERSION.SDK_INT >= 11)
								new GetSharedIDListFromServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							else
								new GetSharedIDListFromServer().execute();
							HomeScreen.refreshContactList = true;
						}else if(prefManager.getSharedIDDisplayName(sharedIDName) == null){
							if(!prefManager.isDomainAdmin())
							{
								prefManager.saveSharedIDDisplayName(sharedIDName, sharedIDDisplayName);
								prefManager.setSharedIDContact(sharedIDName, true);
								if(sharedIDFileID != null)
									prefManager.saveSharedIDFileId(sharedIDName, sharedIDFileID);
								//Update Shared Id'd to add on top of contact list
								ContentValues contentvalues = new ContentValues();
								//Shared ID name is saved in username field
								contentvalues.put(DatabaseConstants.USER_NAME_FIELD, sharedIDName);
								contentvalues.put(DatabaseConstants.VOPIUM_FIELD, Integer.valueOf(1));
								if(sharedID != null)
									contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD, sharedID);
								else
									contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD, (new Random()).nextInt());	
								int id = userName.hashCode();
								if (id < -1)
									id = -(id);
								contentvalues.put(DatabaseConstants.NAME_CONTACT_ID_FIELD, Integer.valueOf(id));
								contentvalues.put(DatabaseConstants.RAW_CONTACT_ID, Integer.valueOf(id));
								//Shared ID Display name is saved in Display name field
								contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, Constants.SHARED_ID_START_STRING + sharedIDDisplayName);
								contentvalues.put(DatabaseConstants.IS_FAVOURITE_FIELD, Integer.valueOf(0));
								contentvalues.put(DatabaseConstants.DATA_ID_FIELD, Integer.valueOf("5"));
								contentvalues.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, "1");
								contentvalues.put(DatabaseConstants.STATE_FIELD, Integer.valueOf(0));
								contentvalues.put(com.superchat.data.db.DatabaseConstants.CONTACT_COMPOSITE_FIELD, "9999999999");
								DBWrapper.getInstance().insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,contentvalues);
								HomeScreen.refreshContactList = true;
							}
						}
						if (profileUpdateNotifier != null)
							profileUpdateNotifier.notifyProfileUpdate(sharedIDName, null, null);
						return;
					}else if(xMPPMessageType == XMPPMessageType.atMeXmppMessageTypeSharedIDDeleted.ordinal()){

						Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeUserRegistered: "+ xMPPMessageType);
						String captionTag  = message.getMediaTagMessage();
						String userName = null;
						String displayname = null;
						String fileId = null;
						boolean deactivated = false;;
						String sharedID = null;
						String sharedIDName = null;
						String sharedIDDisplayName = null;
						String sharedIDFileID = null;
						if(captionTag != null){
							if(captionTag.contains("&quot;"))
								captionTag = captionTag.replace("&quot;", "\"");
							try {
								JSONObject jsonobj = new JSONObject(captionTag);
								if(jsonobj.has("userName") && jsonobj.getString("userName").toString().trim().length() > 0) {
									userName = jsonobj.getString("userName").toString();
								}
								if(jsonobj.has("displayname") && jsonobj.getString("displayname").toString().trim().length() > 0) {
									displayname = jsonobj.getString("displayname").toString();
								}
								if(jsonobj.has("fileId") && jsonobj.getString("fileId").toString().trim().length() > 0) {
									fileId = jsonobj.getString("fileId").toString();
								}
								if(jsonobj.has("sharedID") && jsonobj.getString("sharedID").toString().trim().length() > 0) {
									sharedID = jsonobj.getString("sharedID").toString();
								}
								if(jsonobj.has("sharedIDName") && jsonobj.getString("sharedIDName").toString().trim().length() > 0) {
									sharedIDName = jsonobj.getString("sharedIDName").toString();
								}
								if(jsonobj.has("sharedIDDisplayName") && jsonobj.getString("sharedIDDisplayName").toString().trim().length() > 0) {
									sharedIDDisplayName = jsonobj.getString("sharedIDDisplayName").toString();
								}
								if(jsonobj.has("deactivated") && jsonobj.getString("deactivated").toString().trim().length() > 0) {
									deactivated = jsonobj.getBoolean("deactivated");
								}
								if(jsonobj.has("sharedIDFileID") && jsonobj.getString("sharedIDFileID").toString().trim().length() > 0) {
									sharedIDFileID = jsonobj.getString("sharedIDFileID").toString();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						//Save Deactivated Status in Preferences Shared ID in Preferences
						if(!prefManager.isDomainAdmin()){
							prefManager.setSharedIDDeactivated(sharedIDName, true);
							//Delete From Shared ID list
							DBWrapper.getInstance().deleteContact(sharedIDName);
							HomeScreen.removeSharedID(sharedIDName);
						}
						HomeScreen.refreshContactList = true;
						if (profileUpdateNotifier != null)
							profileUpdateNotifier.notifyProfileUpdate(sharedIDName, null, null);
						return;
					}else if(xMPPMessageType == XMPPMessageType.atMeXmppMessageTypeSharedIDAdminRemoved.ordinal()){

						Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeUserRegistered: "+ xMPPMessageType);
						String captionTag  = message.getMediaTagMessage();
						String userName = null;
						String displayname = null;
						String fileId = null;
						String removed_user = null;
						String sharedID = null;
						String sharedIDName = null;
						String sharedIDDisplayName = null;
						String sharedIDFileID = null;
						if(captionTag != null){
							if(captionTag.contains("&quot;"))
								captionTag = captionTag.replace("&quot;", "\"");
							try {
								JSONObject jsonobj = new JSONObject(captionTag);
								if(jsonobj.has("userName") && jsonobj.getString("userName").toString().trim().length() > 0) {
									userName = jsonobj.getString("userName").toString();
								}
								if(jsonobj.has("displayname") && jsonobj.getString("displayname").toString().trim().length() > 0) {
									displayname = jsonobj.getString("displayname").toString();
								}
								if(jsonobj.has("fileId") && jsonobj.getString("fileId").toString().trim().length() > 0) {
									fileId = jsonobj.getString("fileId").toString();
								}
								if(jsonobj.has("sharedID") && jsonobj.getString("sharedID").toString().trim().length() > 0) {
									sharedID = jsonobj.getString("sharedID").toString();
								}
								if(jsonobj.has("sharedIDName") && jsonobj.getString("sharedIDName").toString().trim().length() > 0) {
									sharedIDName = jsonobj.getString("sharedIDName").toString();
								}
								if(jsonobj.has("sharedIDDisplayName") && jsonobj.getString("sharedIDDisplayName").toString().trim().length() > 0) {
									sharedIDDisplayName = jsonobj.getString("sharedIDDisplayName").toString();
								}
								if(jsonobj.has("adminuser") && jsonobj.getString("adminuser").toString().trim().length() > 0) {
									removed_user = jsonobj.getString("adminuser").toString();
								}
								if(jsonobj.has("sharedIDFileID") && jsonobj.getString("sharedIDFileID").toString().trim().length() > 0) {
									sharedIDFileID = jsonobj.getString("sharedIDFileID").toString();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						if(prefManager.isDomainAdmin())
							HomeScreen.removeAdminFromSharedID(sharedIDName, removed_user);
						else{
							if(Build.VERSION.SDK_INT >= 11)
								new GetSharedIDListFromServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							else
								new GetSharedIDListFromServer().execute();
						}
						return;
					}
					else if(xMPPMessageType == XMPPMessageType.atMeXmppMessageTypeRemoveBroadCast.ordinal()){
						String broadcastUUID = message.getGroupId();
						if(broadcastUUID!=null && !broadcastUUID.equals("")){
							chatDBWrapper.deleteRecentUserChatByUserName(broadcastUUID);
							if (chatCountListener != null)
								chatCountListener.notifyChatRecieve(broadcastUUID,null);
							if (chatListener != null)
								chatListener.notifyChatRecieve(broadcastUUID,null);
						}
					}else if(xMPPMessageType == XMPPMessageType.atMeXmppMessageTypeNewCreateBroadCast.ordinal()){
						String broadcastDisplayName = message.getGroupDisplayname();
						String createrName = message.getDisplayName();
						String broadcastUUID = message.getGroupId();
						String broadcastDiscription = message.getBody();
						String broadcastPicId = message.getGroupPICId();
						String ownersPicId = message.getPicId();
						if(broadcastUUID!=null && !broadcastUUID.equals("") && broadcastDisplayName != null){
							prefManager.saveBroadCastName(broadcastUUID, broadcastDisplayName);
							if(createrName!=null && !createrName.equals(""))
								prefManager.saveGroupOwnerName(broadcastUUID, createrName);
							if(broadcastDiscription!=null && !broadcastDiscription.equals(""))
								prefManager.saveUserStatusMessage(broadcastUUID, broadcastDiscription);
							if(broadcastDisplayName!=null && !broadcastDisplayName.equals(""))
								prefManager.saveBroadCastDisplayName(broadcastUUID, broadcastDisplayName);
							saveMessage(broadcastDisplayName, broadcastUUID,broadcastDisplayName+" broadcast created.");
							prefManager.saveUseBroadCastInfo(broadcastUUID,userMe,SharedPrefManager.BROADCAST_ACTIVE_INFO,true);
							if(broadcastPicId!=null && !broadcastPicId.equals("")){
								if(Build.VERSION.SDK_INT >= 11)
									new BitmapDownloader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,broadcastPicId,BitmapDownloader.THUMB_REQUEST);
								else
									new BitmapDownloader().execute(broadcastPicId,BitmapDownloader.THUMB_REQUEST);
								prefManager.saveUserFileId(broadcastUUID, broadcastPicId);
							}
							if(ownersPicId!=null && !ownersPicId.equals("")){
								if(Build.VERSION.SDK_INT >= 11)
									new BitmapDownloader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,ownersPicId,BitmapDownloader.THUMB_REQUEST);
								else
									new BitmapDownloader().execute(ownersPicId,BitmapDownloader.THUMB_REQUEST);
//								prefManager.saveUserFileId(broadcastUUID, ownersPicId);
							}
							if (chatCountListener != null)
								chatCountListener.notifyChatRecieve(broadcastUUID,null);
							if (chatListener != null)
								chatListener.notifyChatRecieve(broadcastUUID,null);
						}else{
							Log.d(TAG, "BroadcastUUID is null during new broadcast creation.");
						}						
						return;
					}else if (xMPPMessageType == XMPPMessageType.atMeXmppMessageTypeNewCreateGroup.ordinal()){
						String groupDisplayName = message.getGroupDisplayname();
						String createrDisplayName = message.getDisplayName();
						String groupOwnerName = message.getGroupOwnerName();
						String groupUUID = message.getGroupId();
						String groupDiscription = message.getMediaTagMessage();
						String groupPicId = message.getGroupPICId();
						String createrFileID = message.getPicId();
						String groupMemberCount = message.getGroupMemberCount();
						if(groupUUID!=null && !groupUUID.equals("")){
							prefManager.saveGroupName(groupUUID, groupDisplayName);
							prefManager.saveGroupDisplayName(groupUUID, groupDisplayName);
							//save owner display name
							prefManager.saveUserServerName(groupOwnerName, createrDisplayName);
							//save owner file id
							prefManager.saveUserFileId(groupOwnerName, createrFileID);
							if(groupOwnerName!=null && groupOwnerName.equals(userMe))
								prefManager.saveUserGroupInfo(groupUUID,groupOwnerName,SharedPrefManager.GROUP_OWNER_INFO,true);
							prefManager.saveGroupInfo(groupUUID,SharedPrefManager.GROUP_ACTIVE_INFO,true);
							prefManager.saveUserGroupInfo(groupUUID,userMe,SharedPrefManager.GROUP_ACTIVE_INFO,true);
							if(groupMemberCount != null)
								prefManager.saveGroupMemberCount(groupUUID, String.valueOf(groupMemberCount));
							 String message_ID = message.getPacketID();
							if(groupDiscription!=null && !groupDiscription.equals(""))
								prefManager.saveUserStatusMessage(groupUUID, groupDiscription);
							if(message_ID!=null)
								saveInfoMessage(groupDisplayName, groupUUID,groupDisplayName+" group created.", message_ID);
							else
								saveInfoMessage(groupDisplayName, groupUUID,groupDisplayName+" group created.", UUID.randomUUID().toString());
							sendGroupPresence(groupUUID,0);
							if(groupPicId!=null && !groupPicId.equals("")){
								if(Build.VERSION.SDK_INT >= 11)
									new BitmapDownloader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupPicId, BitmapDownloader.THUMB_REQUEST);
								else
									new BitmapDownloader().execute(groupPicId,BitmapDownloader.THUMB_REQUEST);
								prefManager.saveUserFileId(groupUUID, groupPicId);
							}
							if (chatCountListener != null)
								chatCountListener.notifyChatRecieve(groupUUID, null);
							if (chatListener != null)
								chatListener.notifyChatRecieve(groupUUID, null);
//							if (profileUpdateNotifier != null)
//								profileUpdateNotifier.notifyProfileUpdate(groupUUID, null, groupDisplayName);
						}else{
							Log.d(TAG, "GroupUUID is null during new group creation.");
						}
						return;
					}
					fromName = StringUtils.parseBareAddress(message.getFrom());
					// Log.d(TAG, "Got text [" + message.getBody()
					// + "] from [" + fromName + "]");
					String user = fromName;
					if (fromName != null && fromName.contains("@")) {
						user = fromName.substring(0, fromName.indexOf('@'));
						fromName = DBWrapper.getInstance().getChatName(user);
					}
					if (user.equals(userMe)) {
						Log.d(TAG, "Self messaging is not allowed.");
						return;
					}
					if (message.getMessageSeenState() == Message.SeenState.recieved
							|| message.getMessageSeenState() == Message.SeenState.seen) {
						String whreStr = convertStringToWhereAs(message.getPacketID());
						if(chatDBWrapper.isBroadCastMessage(whreStr)){
							message.setStatusMessageType(Message.StatusMessageType.broadcast);
						}
						if(message.getStatusMessageType() == Message.StatusMessageType.group||message.getStatusMessageType() == Message.StatusMessageType.broadcast){
							saveGroupOrBroadcastStatus(user, whreStr, message.getMessageSeenState());
							}else{
								chatDBWrapper.updateSeenStatus(user,whreStr,message.getMessageSeenState());
								if(message.getMessageSeenState() == Message.SeenState.seen){
									long currentTime = System.currentTimeMillis();
									chatDBWrapper.updateP2PReadTime(whreStr,currentTime);
								}
							}
						if (chatCountListener != null)
							chatCountListener.notifyChatRecieve(user,null);
						if (chatListener != null)
							chatListener.notifyChatRecieve(user,null);
						return;
					}
					if (message.getBody() != null && !message.getBody().equals("")) {
						if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.broadcasttoall.ordinal()){
							prefManager.saveBulletinChatCounter(prefManager.getBulletinChatCounter() + 1);
							int messageCount = prefManager.getBulletinChatCounter();
							ShortcutBadger.with(SuperChatApplication.context).count(messageCount);
						}else if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.sharedID.ordinal()){
//							prefManager.saveBulletinChatCounter(prefManager.getBulletinChatCounter() + 1);
//							int messageCount = prefManager.getBulletinChatCounter();
//							ShortcutBadger.with(SuperChatApplication.context).count(messageCount);
						}else{
							prefManager.saveChatCounter(prefManager.getChatCounter() + 1);
							int messageCount = prefManager.getChatCounter();
							ShortcutBadger.with(SuperChatApplication.context).count(messageCount);
							prefManager.saveChatCountOfUser(user, prefManager.getChatCountOfUser(user) + 1);
						}
						message.setMessageSeenState(Message.SeenState.recieved);
//						showNotificationForMessage(user, fromName, message.getBody(), message,(byte)0);
						if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.sharedID.ordinal()){
							prefManager.saveUserServerName(user, message.getDisplayName());
							showNotificationForMessage(user, message.getGroupId(), message.getDisplayName(), message.getBody(), message);
						}
						else
							showNotificationForMessage(user, fromName, message.getBody(), message, (byte)message.getXMPPMessageType().ordinal());
						if (chatCountListener != null){
							chatCountListener.notifyChatRecieve(user,null);
						}
//						if(chatListener!= null){
//							System.out.println("[Chat recieved] - "+chatListener.getClass().toString());
//							chatListener.notifyChatHome(user, null);
//						}else
//							System.out.println("[Chat recieved - ELSE] = > "+ChatListScreen.onForeground);
//						if (message.getMessageSeenState() == Message.SeenState.recieved
//								&& ((onForeground && currentUser
//										.equals(user)) || !onForeground)) {
						if (((ChatListScreen.onForeground && !ChatListScreen.currentUser.equals(user)) || !ChatListScreen.onForeground)) {
							List<String> strList = new ArrayList<String>();
							strList.add(message.getPacketID());
							if(message.getStatusMessageType() == Message.StatusMessageType.broadcast)
								sendGroupOrBroadcastAck(user, strList, Message.SeenState.recieved, null);
							else
								sendAck(user, strList, Message.SeenState.recieved);
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.d(TAG,
						"Exception occured during processPacket."
								+ e.toString());
			}
			sendOffLineMessages1();
			if(prefManager!=null) //new1
				prefManager.saveLastOnline(System.currentTimeMillis()); //new1
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "Creating onBind Service.");
		return mBinder;
	}

	public void showNotificationForMessage(String senderName, String from,
			String displayName, String msg, Message message) {
		
		 if(senderName!=null && senderName.contains("#786#"))
			 senderName = senderName.substring(0, senderName.indexOf("#786#"));
		CharSequence tickerText = msg;// buildTickerMessage(context,
		// from, msg.getBody());
		String user = from;
		String PollMessageType = "1";
		String PollID = null;
		String grpDisplayName = SharedPrefManager.getInstance().getGroupDisplayName(user);
		String captionTag  = message.getMediaTagMessage();
		if(captionTag != null){
			try {
				JSONObject jsonobj = new JSONObject(captionTag);
				if(jsonobj.has("PollMessageType") && jsonobj.getString("PollMessageType").toString().trim().length() > 0) {
					PollMessageType = jsonobj.getString("PollMessageType").toString();
				}
				if(jsonobj.has("PollID") && jsonobj.getString("PollID").toString().trim().length() > 0) {
					PollID = jsonobj.getString("PollID").toString();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(PollMessageType.equals("2")){
			//Save that message in shared pref.
			savePoll(from, PollID, captionTag);
		}else
			saveMessage(displayName, from, userMe, msg, message);
		//Save that user in DB
		String number = senderName;
		if(number.indexOf('_') != -1)
			number = senderName.substring(0, number.indexOf('_'));
		if(message.getDisplayName() != null)
			updateContactcInDB(senderName, message.getDisplayName(), number, 2);
		if (messageNotification == null) {
			messageNotification = new NotificationCompat.Builder(context);
//			if(R.drawable.chatgreen == -1){
//				if(bitmap==null)
//					bitmap = Bitmap.createBitmap(createColors(), 0, STRIDE, WIDTH, HEIGHT,Bitmap.Config.RGB_565);
//				messageNotification.setLargeIcon(bitmap);
//			}
			messageNotification.setSmallIcon(R.drawable.chatgreen);
			messageNotification.setAutoCancel(true);
			messageNotification.setLights(Color.RED, 3000, 3000);
			
			
		}
		Notification note = messageNotification.build();
		if(prefManager.isMute(from))
			note.defaults = 0;
		else
			note.defaults |= Notification.DEFAULT_SOUND;
		note.defaults |= Notification.DEFAULT_LIGHTS;
		// messageNotification.setOnlyAlertOnce(true);
		Uri alarmSound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		if(prefManager.isMute(from))
			messageNotification.setSound(null);
		else
			messageNotification.setSound(alarmSound);
		
		String notificationSenderName = senderName;
			notificationSenderName = DBWrapper.getInstance().getChatName(senderName);
			if(notificationSenderName!=null && notificationSenderName.contains("#786#"))
				notificationSenderName = notificationSenderName.substring(0, notificationSenderName.indexOf("#786#"));
			if(notificationSenderName.equals(senderName)){
//				notificationSenderName = notificationSenderName.replaceFirst("m", "+");
				if(notificationSenderName.contains("_"))
					notificationSenderName = "+"+notificationSenderName.substring(0, notificationSenderName.indexOf("_"));
				}
			if(message.getDisplayName() != null)
				notificationSenderName = message.getDisplayName();
			else
				notificationSenderName = "New user";
			if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.sharedID.ordinal())
				tickerText = "Message from " + notificationSenderName + "@" + SharedPrefManager.getInstance().getSharedIDDisplayName(grpDisplayName);
			else
				tickerText = "Message from " + notificationSenderName + "@" + grpDisplayName;
		messageNotification.setWhen(System.currentTimeMillis());
		messageNotification.setTicker(tickerText);
		Intent notificationIntent = new Intent();
		Log.d(TAG, "notificationPackage: "+notificationPackage+" , "+notificationActivity);
		notificationIntent.setClassName(notificationPackage, notificationPackage+notificationActivity);
//		Intent notificationIntent = new Intent(context,
//				ChatListScreen.class);
		notificationIntent.putExtra(ChatDBConstants.CONTACT_NAMES_FIELD,
				grpDisplayName);
		notificationIntent.putExtra(ChatDBConstants.USER_NAME_FIELD, user);
		notificationIntent.putExtra("FROM_NOTIFICATION", true);
		if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.broadcasttoall.ordinal())
			notificationIntent.putExtra("FROM_BULLETIN_NOTIFICATION", true);
		// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
		// Intent.FLAG_ACTIVITY_CLEAR_TASK);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
				//| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);// FLAG_ACTIVITY_SINGLE_TOP);
		notificationIntent.setAction(Long.toString(System.currentTimeMillis()));
		PendingIntent contentIntent = PendingIntent.getActivity(
				context, 0, notificationIntent,
				PendingIntent.FLAG_ONE_SHOT);
		if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.sharedID.ordinal())
			messageNotification.setContentTitle(notificationSenderName + "@" + SharedPrefManager.getInstance().getSharedIDDisplayName(grpDisplayName));
		else
			messageNotification.setContentTitle(notificationSenderName + "@" + grpDisplayName);
		messageNotification.setContentText(msg);
		messageNotification.setContentIntent(contentIntent);
		int count = prefManager.getChatCountOfUser(user);
		Notification notification = messageNotification.build();
		if(R.layout.message_notifier!=-1){
			RemoteViews contentView = new RemoteViews(
					context.getPackageName(),
					R.layout.message_notifier);
			if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.sharedID.ordinal())
				contentView.setTextViewText(R.id.chat_person_name, notificationSenderName+"@"+SharedPrefManager.getInstance().getSharedIDDisplayName(grpDisplayName));
			else
				contentView.setTextViewText(R.id.chat_person_name, notificationSenderName+"@"+grpDisplayName);
			
			Uri uri = getPicUri(user);
			if(uri!=null)
				contentView.setImageViewUri(R.id.imagenotileft, uri);
			else{
				if(SharedPrefManager.getInstance().isSharedIDContact(user))
					contentView.setImageViewResource(R.id.imagenotileft, R.drawable.small_helpdesk);
				else
					contentView.setImageViewResource(R.id.imagenotileft, R.drawable.chat_person);
			}
			if(message.getMediaBody()!=null){
				int mediaType = 0;
				try{
				mediaType = Integer.parseInt(message.getMediaBody().getType());
				}catch(NumberFormatException e){}
				if(mediaType == XMPPMessageType.atMeXmppMessageTypeVideo.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Video message");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeImage.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Picture message");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Voice message");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeDoc.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Doc file");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypePdf.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Pdf file");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeXLS.ordinal())
					contentView.setTextViewText(R.id.chat_message, "XLS file");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypePPT.ordinal())
					contentView.setTextViewText(R.id.chat_message, "PPT file");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeLocation.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Shared a location");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeContact.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Shared contact");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypePoll.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Poll");
			}else
			contentView.setTextViewText(R.id.chat_message, msg);
			if (count > 0) {
				contentView.setTextViewText(R.id.chat_notification_bubble_text,
						String.valueOf(count));
			}
			notification.contentView = contentView;
		}
		Random random = new Random();
		int id = (senderName + "@" + grpDisplayName).hashCode();
		if (id < -1)
			id = -(id);
		Log.d(TAG, "showNotificationForMessage1: "+from+" , "+currentUser+" , "+onForeground);
		if(prefManager.isSnoozeExpired() && ((ChatListScreen.onForeground && !ChatListScreen.currentUser
										.equals(from) && !ChatListScreen.currentUser.endsWith("-all")) || !ChatListScreen.onForeground))
		notificationManager.notify(id, notification);

	}
	
	private void updateContactcInDB(String username, String dislay_name, String mobile_number, int type){
		try{
			String number = DBWrapper.getInstance().getContactNumber(username);
			if(number!=null && !number.equals(""))
				return;
		ContentValues contentvalues = new ContentValues();
		contentvalues.put(DatabaseConstants.USER_NAME_FIELD,username);
		contentvalues.put(DatabaseConstants.VOPIUM_FIELD, type);
		contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,mobile_number);	
		int id = username.hashCode();
		if (id < -1)
			id = -(id);
		contentvalues.put(DatabaseConstants.NAME_CONTACT_ID_FIELD,Integer.valueOf(id));
		contentvalues.put(DatabaseConstants.RAW_CONTACT_ID,Integer.valueOf(id));
		contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, dislay_name);
		contentvalues.put(DatabaseConstants.CONTACT_TYPE_FIELD, "");
		contentvalues.put(DatabaseConstants.IS_FAVOURITE_FIELD,Integer.valueOf(0));//Integer.valueOf(0)
		contentvalues.put(DatabaseConstants.DATA_ID_FIELD,Integer.valueOf("5"));
		contentvalues.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, ""+type);
		contentvalues.put(DatabaseConstants.STATE_FIELD,Integer.valueOf(0));
		contentvalues.put(com.superchat.data.db.DatabaseConstants.CONTACT_COMPOSITE_FIELD, mobile_number);
		if(!username.equalsIgnoreCase(SharedPrefManager.getInstance().getUserName()))
			DBWrapper.getInstance().insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,contentvalues);

		}catch(Exception ex){
			
		}
	}

	private void savePoll(String grp_name, String poll_id, String json_data)
	{
		//create test hashmap
		Log.i(TAG, "savePoll for poll ID = "+poll_id+", Poll New Received JSON : "+json_data);
		HashMap<String, String> pollHashMap = null;
		pollHashMap = getPollForGroup(grp_name);
		if(pollHashMap == null)
			pollHashMap = new HashMap<String, String>();

		if(pollHashMap.containsKey(poll_id)){
			//Update here JSON Values for selected options.
			JSONObject jsonobj;
			try {
				jsonobj = new JSONObject(json_data);
				JSONArray poll_options = null;
				JSONArray poll_reply_options = null;
				String replied_option_id = null;
				String[] poll_ids = null;
				String[] poll_value = null;
				int[] poll_option_count = null;
				if(jsonobj.has("PollReplyOption")){
					poll_reply_options = jsonobj.getJSONArray("PollReplyOption");
					for(int i = 0; i < poll_reply_options.length(); i++){
						JSONObject obj = (JSONObject) poll_reply_options.get(i);
						if(obj.has("OptionId")){
							replied_option_id = obj.getString("OptionId");
						}
					}
				}
				
				//Work on stored JSON, get the updated % count 
				String stored_json = pollHashMap.get(poll_id);
				Log.i(TAG, "savePoll for poll ID = "+poll_id+", Poll Stored JSON : "+json_data);
				jsonobj = new JSONObject(stored_json);
				if(jsonobj.has("PollOption"))
					poll_options = jsonobj.getJSONArray("PollOption");
				
				if(poll_options.length() > 0){
					poll_ids = new String[poll_options.length()];
					poll_value = new String[poll_options.length()];
					poll_option_count = new int[poll_options.length()];
					for(int i = 0; i < poll_options.length(); i++){
						JSONObject obj = (JSONObject) poll_options.get(i);
						if(obj.has("OptionId")){
							poll_ids[i] = obj.getString("OptionId");
						}
						if(obj.has("OptionText")){
							poll_value[i] = obj.getString("OptionText");
						}
						if(obj.has("PollOptionCount")){
							if(poll_ids[i].equalsIgnoreCase(replied_option_id))
								poll_option_count[i] = Integer.parseInt(obj.getString("PollOptionCount")) + 1;
							else
								poll_option_count[i] = Integer.parseInt(obj.getString("PollOptionCount"));
						}
					}
				}
				//Make new JSON Array to store in DB
                poll_options = new JSONArray();
                for(int i = 0; i < poll_ids.length; i++)
                {
                    JSONObject options_element = new JSONObject();
                    options_element.put("OptionId", poll_ids[i]);
                    options_element.put("OptionText", poll_value[i]);
                    options_element.put("PollOptionCount", poll_option_count[i]);
                    poll_options.put(options_element);
                }
                
                //Update new JSON Array
                JSONObject finalJSONbject = new JSONObject(json_data);
                finalJSONbject.remove("PollOption");
                finalJSONbject.put("PollOption", poll_options);
				
                //Remove Older JSON and update newer one.
                pollHashMap.remove(poll_id);
                pollHashMap.put(poll_id, finalJSONbject.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else
			pollHashMap.put(poll_id, json_data);

		//convert to string using gson
		Gson gson = new Gson();
		String hashMapString = gson.toJson(pollHashMap);

		//save in shared prefs
		SharedPreferences prefs = getSharedPreferences("poll_data", MODE_PRIVATE);
		prefs.edit().putString(grp_name, hashMapString).apply();

	}

	private HashMap<String, String> getPollForGroup(String grp_name)
	{
		//save in shared prefs
		SharedPreferences prefs = getSharedPreferences("poll_data", MODE_PRIVATE);
		Gson gson = new Gson();

		//get from shared prefs
		String storedHashMapString = prefs.getString(grp_name, null);
		java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();
		if(storedHashMapString != null) {
			HashMap<String, String> testHashMap2 = gson.fromJson(storedHashMapString, type);
			return testHashMap2;
		}
		return null;
	}
	static Bitmap bitmap = null;
	boolean isSameUser = false;
	public void showNotificationForMessage(String from, String displayName,
			String msg, Message message,byte messageType) {
		
		 if(displayName!=null && displayName.contains("#786#"))
			 displayName = displayName.substring(0, displayName.indexOf("#786#"));
		CharSequence tickerText = msg;// buildTickerMessage(context,
		// from, msg.getBody());
		String user = from;
		saveMessage(from, userMe, msg, message, messageType);
		if(displayName.equals(from)){
			displayName = SharedPrefManager.getInstance().getUserServerName(from);
			if(displayName.equals(from)){
				if(displayName.contains("_")){
//					displayName = "+"+displayName.substring(0, displayName.indexOf("_"));
					displayName = message.getDisplayName();
					if(displayName!=null){
						SharedPrefManager.getInstance().saveUserServerName(from, displayName);
						String picId = message.getPicId();
						if(picId!=null && !picId.equals(""))
							SharedPrefManager.getInstance().saveUserFileId(from, picId);
					}
				}
				}
			}
		
//		int messageCount = prefManager.getChatCounter();
//		if(isFirstMessage){
//			isSameUser = true;
//			notificationAllMessage = msg;
//		}else{
//			if(!previousUser.equals(from))
//				isSameUser = false;
//			if(isSameUser){
//				notificationManager.cancelAll();
//				notificationAllMessage+="\n"+msg;
//			}else
//				notificationAllMessage = msg;
//		}
		
		
		if (messageNotification == null) {
			messageNotification = new NotificationCompat.Builder(context);
			if(R.drawable.chatgreen==-1){
				if(bitmap==null)
					bitmap = Bitmap.createBitmap(createColors(), 0, STRIDE, WIDTH, HEIGHT,Bitmap.Config.RGB_565);
				messageNotification.setLargeIcon(bitmap);//R.drawable.chatgreen);
				
			}
				messageNotification.setSmallIcon(R.drawable.chatgreen);//messageNotification.setDefaults(Notification.DEFAULT_ALL);
			messageNotification.setAutoCancel(true);
			messageNotification.setLights(Color.RED, 3000, 3000);
			
		}
		Notification note = messageNotification.build();
		if(prefManager.isMute(from))
			note.defaults = 0;
		else
			note.defaults |= Notification.DEFAULT_SOUND;
		note.defaults |= Notification.DEFAULT_LIGHTS;
		// messageNotification.setOnlyAlertOnce(true);
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		if(prefManager.isMute(from))
			messageNotification.setSound(null);
		else
			messageNotification.setSound(alarmSound);
		
//		if(displayName!=null && displayName.equals(user)){
//			if(displayName.startsWith("m"))
//				displayName = displayName.replace("m", "+");
//			}
		tickerText = "Message from " + displayName;
		messageNotification.setWhen(System.currentTimeMillis());
		messageNotification.setTicker(tickerText);
		Intent notificationIntent = new Intent();
		Log.d(TAG, "notificationPackage: "+notificationPackage+" , "+notificationActivity);
		notificationIntent.setClassName(notificationPackage, notificationPackage+notificationActivity);
//		Intent notificationIntent = new Intent(context,
//				ChatListScreen.class);
		notificationIntent.putExtra(ChatDBConstants.CONTACT_NAMES_FIELD, displayName);
		notificationIntent.putExtra(ChatDBConstants.USER_NAME_FIELD, user);
		notificationIntent.putExtra("FROM_NOTIFICATION", true);
		if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.broadcasttoall.ordinal())
			notificationIntent.putExtra("FROM_BULLETIN_NOTIFICATION", true);
		// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
//				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);// FLAG_ACTIVITY_SINGLE_TOP);
		notificationIntent.setAction(Long.toString(System.currentTimeMillis()));
		PendingIntent contentIntent = PendingIntent.getActivity(
				context, 0, notificationIntent,
				PendingIntent.FLAG_ONE_SHOT);

		messageNotification.setContentTitle(displayName);
			 
			messageNotification.setContentText(msg);
		messageNotification.setContentIntent(contentIntent);
		int count = prefManager.getChatCountOfUser(user);
		Notification notification = messageNotification.build();
		
		if(R.layout.message_notifier!=-1){
			RemoteViews contentView = new RemoteViews(
					context.getPackageName(),
					R.layout.message_notifier);
			contentView.setTextViewText(R.id.chat_person_name, displayName);
			Uri uri = getPicUri(user);
			if(uri!=null)
			contentView.setImageViewUri(R.id.imagenotileft, uri);
//			setProfilePic()
			if(message.getMediaBody()!=null){
				int mediaType = 0;
				try{
				mediaType = Integer.parseInt(message.getMediaBody().getType());
				}catch(NumberFormatException e){}
				if(mediaType == XMPPMessageType.atMeXmppMessageTypeImage.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Picture message");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Voice message");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeVideo.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Video message");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeDoc.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Doc message");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypePdf.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Pdf message");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeXLS.ordinal())
					contentView.setTextViewText(R.id.chat_message, "XLS message");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypePPT.ordinal())
					contentView.setTextViewText(R.id.chat_message, "PPT message");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeLocation.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Shared a location");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeContact.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Shared contact");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypePoll.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Poll");

			}else{
//				contentView.setTextViewText(R.id.chat_message, notificationAllMessage);
				contentView.setTextViewText(R.id.chat_message, msg);
				}
			if (count > 0) {
				contentView.setTextViewText(R.id.chat_notification_bubble_text,
						String.valueOf(count));
			}
			notification.contentView = contentView;
		}
		Random random = new Random();
		int id = user.hashCode();
		if (id < -1)
			id = -(id);
		Log.d(TAG, "showNotificationForMessage: "+from+" , "+currentUser+" , "+onForeground);
		
		if(prefManager.isSnoozeExpired() && ((ChatListScreen.onForeground && !ChatListScreen.currentUser
				.equals(from) && !ChatListScreen.currentUser.endsWith("-all")) || !ChatListScreen.onForeground))
			notificationManager.notify(id, notification);
		previousUser = from;
		isFirstMessage = false;
	}
	private Uri getPicUri(String userName){
		SharedPrefManager pref = SharedPrefManager.getInstance();
		String groupPicId = null;
		if(pref.isSharedIDContact(userName))
			groupPicId = pref.getSharedIDFileId(userName);
		else
			groupPicId = pref.getUserFileId(userName);
		if(groupPicId!=null && !groupPicId.equals("")){
			String profilePicUrl = groupPicId;//AppConstants.media_get_url+
			if(!profilePicUrl.contains(".jpg"))
				profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+

			File file = Environment.getExternalStorageDirectory();
//			String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
			String filename = file.getPath()+ File.separator + Constants.contentProfilePhoto+profilePicUrl;
//			view.setTag(filename);
			File file1 = new File(filename);
			if(file1!=null && file1.exists()){
				return Uri.parse(filename);
			}
//				view.setImageURI(Uri.parse(filename));
//				view.setBackgroundDrawable(null);
//			}
		}
//		else if(SharedPrefManager.getInstance().isGroupChat(userName))
//			view.setImageResource(R.drawable.chat_person);
//		else
//			view.setImageResource(R.drawable.avatar); // 
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		prefManager = SharedPrefManager.getInstance();
		context = SuperChatApplication.context;
		chatDBWrapper = ChatDBWrapper.getInstance(context);
		calender = Calendar.getInstance(TimeZone.getDefault());
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		userMe = prefManager.getUserName();
		displayUserName = prefManager.getDisplayName();
		bitmap = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_view);//Bitmap.createBitmap(createColors(), 0, STRIDE, WIDTH, HEIGHT,Bitmap.Config.RGB_565);
		notificationPackage = context.getPackageName();
		Log.d(TAG, "Creating Chat Service."+context.getPackageName());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "Destroying Chat Service.");
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		 super.onStartCommand(intent, START_STICKY, startId);
		Log.d(TAG, "[XMPP Service started - onStartCommand]");
//		if(isServiceRunning("com.chat.sdk.ChatService")){
//			System.out.println("[SERVICE ALREADY RUNNING, SO STOP]");
//			stopService(new Intent(this, ChatService.class));
//		}
		new Connect().execute("");
		return START_STICKY;
	}

	private class Connect extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			chatLogin();
			return null;
		}
	}
	boolean processing;

	public void chatLogin() {
		Log.i(TAG, "XMPP trying to login with ChatClient inside chatLogin method. ");
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				ConnectivityManager cm = (ConnectivityManager) SuperChatApplication.context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
				boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
				if (isConnected && !xmppConectionStatus) {

					String userName = "";
					String password = "";
					if (connection != null) {
						connection.disconnect();
						connection = null;
					}
					if (prefManager != null) {
						userName = prefManager.getUserName();
						password = prefManager.getUserPassword();
					}
					SmackAndroid.init(SuperChatApplication.context);
					userMe = userName;
					ConnectionConfiguration connConfig = null;
					try {
//						 connConfig = new AndroidConnectionConfiguration( Constants.CHAT_SERVER_URL);
						Log.d(TAG, "XmppChatClient.getChatServer: "+Constants.CHAT_SERVER_URL);
						// Create the configuration for this new connection_
						connConfig = new ConnectionConfiguration(Constants.CHAT_SERVER_URL, 5222);
						connConfig.setReconnectionAllowed(true);
						connConfig.setRosterLoadedAtLogin(true);
						connConfig.setSASLAuthenticationEnabled(false);
						connConfig.setSecurityMode(SecurityMode.disabled);
//						xmppConectionStatus = true;
						
					} catch (NumberFormatException e1) {
						xmppConectionStatus = false;
					}
					catch (Exception e) {
						xmppConectionStatus = false;
					}
						 
//						if(connectionStatusListener!=null)
//							connectionStatusListener.notifyConnectionChange();
					if (connConfig == null)
						return;
					else {
						if (processing) {
							processing = false;
							cancel();
							System.out.println("successfulyy [[SECOND CALL]]");
							return;
						}
						Log.i(TAG, "ConnConfig Successfully");
					}
//						connConfig.setSendPresence(false);
						connection = new XMPPConnection(connConfig);
						processing = true;
					try {
						if (connection != null && !connection.isConnected()) {

							connection.connect();
							Log.d(TAG, "[SettingsDialog] Connected to " + connection.getHost());
							// ProviderManager.getInstance().addExtensionProvider("status","androidpn:iq:status",
							// new MUCUserProvider());
							if (!connection.isAuthenticated())
								connection.login(userName, password);
							setConnection(packetListener);
							setConnectionStatusListener(connectionListener);
							setGroupListener(groupPacketListener);
							setTypingListener(typingListener);
							setGroupTypingListener(typingGroupListener);
							setRecordingStatusListener(recordStatusListener);
							setListeningStatusListener(listeningStatusListener);
							setInvitationListener(invitationListener);
							// Set the status to available
							 Presence subscription = new Presence(Presence.Type.subscribe);
						       subscription.setTo(userName+"@"+Constants.CHAT_DOMAIN);
						       subscription.setPriority(24);
						       subscription.setMode(Presence.Mode.available);
						       connection.sendPacket(subscription);
//							 SubscriptionMode subscriptionMode =  Roster.getDefaultSubscriptionMode();
							Thread.sleep(2000);
//							System.out.println("chatLogin :: total groups to join :- "+SharedPrefManager.getInstance().getGroupNamesArray().length);
							for (String group : SharedPrefManager.getInstance().getGroupNamesArray()) {
								if (group != null && !group.equals("")){
									if(prefManager.isGroupMemberActive(group, prefManager.getUserName()))
										sendGroupPresence(group, -1);
								}
							}
							xmppConectionStatus = true;
							if(connectionStatusListener != null)
								connectionStatusListener.notifyConnectionChange();
//							System.out.println("Connected Successfully:: So cancelling Timer - cancel()");
							if(prefManager!=null)
								prefManager.saveLastOnline(System.currentTimeMillis());
							sendOffLineMessages1();
							updateBlockedUserList(prefManager.getUserName());
							cancel();
							processing = false;
							// getOnlinesList();
//							xmppConectionStatus = true;
						}else
							xmppConectionStatus = false;

					} catch (XMPPException ex) {
						xmppConectionStatus = false;
						Log.d(TAG, "[SettingsDialog] Failed to connect to " + connection);
						Log.d(TAG, ex.toString());
						// connection = null;
						processing = false;

					} catch (Exception e) {
						xmppConectionStatus = false;
						Log.d(TAG, "chatLogin connect excption: " + e.toString());
						processing = false;
					}
				} else
					cancel();
				if(connectionStatusListener!=null)
					connectionStatusListener.notifyConnectionChange();
			}
			
		}, 500, 4000);

		// if (connection == null)
		// return false;
		//
		// return true;
	}
	public boolean updateGroupDisplayName(String groupName,String groupDisplayName, String caption){
		return sendGroupInfoMessage(groupName, groupDisplayName, caption, Message.XMPPMessageType.atMeXmppMessageTypeGroupName);
	}
	public boolean removeGroupPerson(String room, String nickName) {
//		SharedPrefManager.getInstance().removeGroupName(room);
		sendInfoMessage(room,nickName,Message.XMPPMessageType.atMeXmppMessageTypeLeftGroup);
		prefManager.saveUserGroupInfo(room,nickName,SharedPrefManager.GROUP_ACTIVE_INFO,false);
//		 try{
//		 if (connection == null || !connection.isConnected() || !connection.isAuthenticated())
//			 return false;
//		 String roomPath = room + "@" + Constants.CHAT_DOMAIN;
//		 Presence leavePresence = new Presence(Presence.Type.unavailable);
//		 leavePresence.setTo(roomPath + "/" + nickName);
//		 connection.sendPacket(leavePresence);
////		 return true;
//		 }catch(Exception e){
//		 Log.d(TAG, "leave chat excption " + e.toString());
//		 return false;
//		 }
		return true;
	}
	public boolean leave(String room, String nickName) {
		SharedPrefManager.getInstance().removeGroupName(room);
		prefManager.saveUserGroupInfo(room,nickName,SharedPrefManager.GROUP_ACTIVE_INFO,false);
//		sendInfoMessage(room,nickName,Message.XMPPMessageType.atMeXmppMessageTypeLeftGroup);
		// try{
		// if (connection == null || !connection.isConnected()
		// || !connection.isAuthenticated())
		// return false;
		// String roomPath = room + "@" + AtmeChatClient.getChatServer();
		// Presence leavePresence = new Presence(Presence.Type.unavailable);
		// leavePresence.setTo(roomPath + "/" + nickName);
		// connection.sendPacket(leavePresence);
		// return true;
		// }catch(Exception e){
		// Log.d(TAG, "leave chat excption " + e.toString());
		// return false;
		// }
		return true;
	}
	 private void updateBlockedUserList(String userId) {

		    if (connection != null && connection.isConnected() && connection.isAuthenticated()){
		    try {
		        PrivacyListManager privacyManager = PrivacyListManager.getInstanceFor(connection);
		        if (privacyManager == null) {
		            return;
		        }
		        String ser = "@" + Constants.CHAT_SERVER_URL;
		        PrivacyList plist = null;
		        try {
		            plist = privacyManager.getPrivacyList("blockList");
		        } catch (Exception e) {
//		            e.printStackTrace();
		        }
		        if (plist != null) {// No blacklisted or is not listed, direct getPrivacyList error
		            List<PrivacyItem> items = plist.getItems();
		            for (PrivacyItem item : items) {


		                String from = item.getValue().substring(0,
		                        item.getValue().indexOf(ser));

//		                if (userId.equals(from)) {
//
////		                    item.isAllow();
//		                    Log.d(TAG, "Blocked status "+item.isAllow()+" , "+item.getValue());
////		                    privacyList.add(from);
//		                    
//		                }
		                boolean isUnBlocked = item.isAllow();
		                item.setValue(from);
		                item.setFilterIQ(isUnBlocked);
		                item.setFilterMessage(isUnBlocked);
		                item.setFilterPresence_in(isUnBlocked);
		                item.setFilterPresence_out(isUnBlocked);
//		                updateBlockUnblockUser(from,item,isUnBlocked);
		                prefManager.setBlockStatus(from, !isUnBlocked);
		                if(!SuperChatApplication.blockUserList.contains(from) && !isUnBlocked)
		                	SuperChatApplication.blockUserList.add(from);

		            }
		            if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
             	    try {
             	        privacyManager.updatePrivacyList("blockList", items);
//             	        privacyManager.createPrivacyList(listName, privacyItems);
             	        privacyManager.setActiveListName("blockList");
             	
             	    } catch (Exception e) {
             	        Log.e("PRIVACY_ERROR: ", " " + e.toString());
             	        e.printStackTrace();
             	    }
                 }
		        } else {
		        }
		    } catch (Exception ex) {
		    }
		    }
		}
	 public List<String> getBlockedUserList(String userId) {

		    List<String> privacyList = new ArrayList<String>();
		    if (connection != null && connection.isConnected() && connection.isAuthenticated()){
		    try {
		        PrivacyListManager privacyManager = PrivacyListManager.getInstanceFor(connection);
		        if (privacyManager == null) {
		            return privacyList;
		        }
		        String ser = "@" + Constants.CHAT_SERVER_URL;
		        PrivacyList plist = null;
		        try {
		            plist = privacyManager.getPrivacyList("blockList");
		        } catch (Exception e) {
//		            e.printStackTrace();
		        }
		        if (plist != null) {// No blacklisted or is not listed, direct getPrivacyList error
		            List<PrivacyItem> items = plist.getItems();
		            for (PrivacyItem item : items) {


		                String from = item.getValue().substring(0,
		                        item.getValue().indexOf(ser));

//		                if (userId.equals(from)) {
//
////		                    item.isAllow();
//		                    Log.d(TAG, "Blocked status "+item.isAllow()+" , "+item.getValue());
////		                    privacyList.add(from);
//		                    
//		                }
		                boolean isUnBlocked = item.isAllow();
		                if(!isUnBlocked)
		                	privacyList.add(from);

		            }
//		            if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
//                	    try {
//                	        privacyManager.updatePrivacyList("blockList", items);
////                	        privacyManager.createPrivacyList(listName, privacyItems);
////                	        privacyManager.setActiveListName(listName);
//                	
//                	    } catch (Exception e) {
//                	        Log.e("PRIVACY_ERROR: ", " " + e.toString());
//                	        e.printStackTrace();
//                	    }
//                    }
		        } else {
		            return privacyList;
		        }
		    } catch (Exception ex) {
		    }
		    }
		    return privacyList;
		}
	 // Here function for block user on xmpp
	 public boolean updateBlockUnblockUser(String userName,PrivacyItem item, boolean isUnblock) {

		    String jid = userName + "@" + Constants.CHAT_SERVER_URL;
		    String listName = "blockList";

		    // Create the list of PrivacyItem that will allow or
		    // deny some privacy aspect

		    //ArrayList privacyItems = new ArrayList();

		    List<PrivacyItem> privacyItems = new Vector<PrivacyItem>();

//		    PrivacyItem item = new PrivacyItem(PrivacyItem.Type.jid.toString(), isUnblock, 1);
		    item.setValue(jid);
		    item.setFilterIQ(isUnblock);
		    item.setFilterMessage(isUnblock);
		    item.setFilterPresence_in(isUnblock);
		    item.setFilterPresence_out(isUnblock);

		    privacyItems.add(item);
		    // Get the privacy manager for the current connection.
		    // Create the new list.
		    if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
			    PrivacyListManager privacyManager = PrivacyListManager.getInstanceFor(connection);
			    try {
			        privacyManager.updatePrivacyList(listName, privacyItems);
//			        privacyManager.createPrivacyList(listName, privacyItems);
			        privacyManager.setActiveListName(listName);
			
			        return true;
			    } catch (Exception e) {
			        Log.e("PRIVACY_ERROR: ", " " + e.toString());
			        e.printStackTrace();
			    }
		    }
		    return false;
		}
    public boolean blockUnblockUser(String userName, boolean isUnblock) {

    String jid = userName + "@" + Constants.CHAT_SERVER_URL;
    String listName = "blockList";

    // Create the list of PrivacyItem that will allow or
    // deny some privacy aspect

    //ArrayList privacyItems = new ArrayList();

    List<PrivacyItem> privacyItems = new Vector<PrivacyItem>();

    PrivacyItem item = new PrivacyItem(PrivacyItem.Type.jid.toString(), isUnblock, prefManager.getBlockOrder());
    item.setValue(jid);
    item.setFilterIQ(isUnblock);
    item.setFilterMessage(isUnblock);
    item.setFilterPresence_in(isUnblock);
    item.setFilterPresence_out(isUnblock);

    privacyItems.add(item);
    // Get the privacy manager for the current connection.
    // Create the new list.
    if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
	    PrivacyListManager privacyManager = PrivacyListManager.getInstanceFor(connection);
	    try {
	    	prefManager.saveBlockOrder(-1);
	    	if(!SuperChatApplication.blockUserList.contains(userName) && !isUnblock){
            	SuperChatApplication.blockUserList.add(userName);
            	
	    	}
	    	if(isUnblock && SuperChatApplication.blockUserList.contains(userName)){
	    		SuperChatApplication.blockUserList.remove(userName);
	    		
	    	}
	    	if(!isUnblock)
	    		prefManager.saveBlockedUser(userName);
	    	else
	    		prefManager.removeBlockedUser(userName);
	    	
	        privacyManager.updatePrivacyList(listName, privacyItems);
//	        privacyManager.createPrivacyList(listName, privacyItems);
	        privacyManager.setActiveListName(listName);
	
	        return true;
	    } catch (Exception e) {
	        Log.e("PRIVACY_ERROR: ", " " + e.toString());
	        e.printStackTrace();
	    }
    }
    return false;
}
	// public void getOfflineMessages(final XMPPConnection connection){
	// new Thread() {
	// public void run() {
	// synchronized (connection) {
	//
	//
	// connection.sendPacket(new Presence(Presence.Type.unavailable));
	// try {
	// wait(1000);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// connection.sendPacket(new Presence(Presence.Type.available));
	// }
	// }}.start();
	// }

	public boolean sendOffLineMessages1() {
		if (connection == null || !connection.isConnected()
				|| !connection.isAuthenticated())
			return false;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnected();
		if (!isConnected)
			return false;
		synchronized (this) {

			ArrayList<HashMap<String, String>> list = chatDBWrapper.getUndeliveredMessages(userMe);
			if (list != null)
				Log.d(TAG, "Total unsync items " + list.size());
			if (list != null && !list.isEmpty()) {
				Iterator<HashMap<String, String>> iterator = list.iterator();
				if (iterator != null) {
					while (iterator.hasNext()) {
						activeNetwork = cm.getActiveNetworkInfo();
						isConnected = activeNetwork != null
								&& activeNetwork.isConnected();
						if (!isConnected)
							return false;
						HashMap<String, String> items = iterator.next();
						String userName = items
								.get(ChatDBConstants.TO_USER_FIELD);
						String message = items
								.get(ChatDBConstants.MESSAGEINFO_FIELD);
						String captionMsg = items.get(ChatDBConstants.MEDIA_CAPTION_TAG);
						int msgType = XMPPMessageType.atMeXmppMessageTypeNormal.ordinal();
						try{
							msgType = Integer.parseInt(items.get(ChatDBConstants.MESSAGE_TYPE_FIELD));
						}catch(NumberFormatException e){
							
						}
						String msgId = items.get(ChatDBConstants.MESSAGE_ID);
						Log.d(TAG, "Unsync item values: " + userName + " , "
								+ message + " , " + msgId);
						if (userName == null || userName.equals("")) {
							Log.d(TAG, "userName is not correct: " + userName);
							continue;
						}
						if (message == null || message.equals("")) {
							Log.d(TAG, "Message should not be empty or null: "
									+ message);
							continue;
						}
						if (msgId == null || msgId.equals("")) {
							Log.d(TAG,
									"Message id should not be empty or null: "
											+ msgId);
							continue;
						}
						if (userName.equals(userMe)) {
							continue;
						}
						boolean isGroupChat = false;
						if (SharedPrefManager.getInstance().isGroupChat(
								userName)
								&& !message.equals("You are welcome in "
										+ userName + " group.")) {
							// sendGroupMessage(userName, message);
							// return;
							isGroupChat = true;
						}
						
						String to = userName + "@"
								+ Constants.CHAT_DOMAIN;
						if (isGroupChat) {
							to = userName + "@conference."
									+ Constants.CHAT_DOMAIN;
						}
						Log.d(TAG, "Sending text [" + message + "] to [" + to
								+ "]");
						Message msg = new Message(to);// , Message.Type.chat);
						if (isGroupChat)
							msg.setType(Message.Type.groupchat);
						else
							msg.setType(Message.Type.chat);
						msg.setMessageSeenState(Message.SeenState.sent);
						msg.setDisplayName(prefManager.getDisplayName());
						String picId = prefManager.getUserFileId(prefManager.getUserName());
						if(picId != null)
							msg.setPicId(picId);
						
						msg.setBody(message);
						msg.setPacketID(msgId);
						String url = null;
						String thumb = null;
						String media_length = null;
						if(msgType == XMPPMessageType.atMeXmppMessageTypeDoc.ordinal()){
							 url = items.get(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD);
						}else if(msgType == XMPPMessageType.atMeXmppMessageTypePdf.ordinal()){
							 url = items.get(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD);
						}else if(msgType == XMPPMessageType.atMeXmppMessageTypeXLS.ordinal()){
							 url = items.get(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD);
						}else if(msgType == XMPPMessageType.atMeXmppMessageTypePPT.ordinal()){
							 url = items.get(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD);
						}else if(msgType == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal()){
							 url = items.get(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD);
							 media_length = items.get(ChatDBConstants.MESSAGE_MEDIA_LENGTH);
						}else if(msgType == XMPPMessageType.atMeXmppMessageTypeImage.ordinal()){
							 url = items.get(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD);
							 thumb = items.get(ChatDBConstants.MESSAGE_THUMB_FIELD);
							
						}else if(msgType == XMPPMessageType.atMeXmppMessageTypeVideo.ordinal()){
							 url = items.get(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD);
							 thumb = items.get(ChatDBConstants.MESSAGE_THUMB_FIELD);
							
						}
						if(url!=null){
							msg.setXMPPMessageType((XMPPMessageType.values()[msgType]));
							MediaBody mediaBody = msg.new MediaBody();
							mediaBody.setType(String.valueOf(msgType));
//							mediaBody.setUrl(url.substring(url.indexOf(Constants.PIC_SEP) + Constants.PIC_SEP.length()));
							mediaBody.setUrl(url);
							if(thumb!=null)
							 mediaBody.setThumb_data(thumb);
							msg.setMediaBody(mediaBody);
						}
//						}
						if(captionMsg!=null && !captionMsg.equals("")){
							msg.setMediaTagMessage(captionMsg);
						}
						Log.d(TAG, "sent packet: " + msg.toXML());
						if (connection != null && connection.isConnected()
								&& connection.isAuthenticated()) {
							connection.sendPacket(msg);
							chatDBWrapper.updateSeenStatus(userName,
									"(\"" + msgId + "\")", SeenState.sent);
						} else {
							msg.setMessageSeenState(Message.SeenState.wait);
							// chatLogin();
						}
					}
				}
			}
		}
		return true;
	}

	public void updateMessageStatus(String from, String to, String msg,
			String msgId) {

	}

	public void chatLogout() {
		if (connection != null && connection.isConnected()) {
			try {
				connection.removePacketListener(packetListener);
				connection.disconnect();
				clearAllNotifications();
			} catch (Exception e) {
			}
			connection = null;
		}
	}
	public void saveGroupOrBroadcastStatus(String from,String packetId,Message.SeenState state) {
		try{
//		+ ChatDBConstants.TABLE_NAME_STATUS_INFO+ "("
//				+ ChatDBConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//				+ ChatDBConstants.MESSAGE_ID+ " TEXT NOT NULL,"
//				+ChatDBConstants.SEEN_FIELD+ " INTEGER NOT NULL,"
//				+ ChatDBConstants.DELIVER_TIME_FIELD+ " LONG NOT NULL,"
//				+ ChatDBConstants.SEEN_TIME_FIELD+ " LONG NOT NULL,"
//				+ ChatDBConstants.FROM_USER_FIELD+ " TEXT NOT NULL,"
//				+ ChatDBConstants.GROUP_UUID_FIELD+ " TEXT,"
//				+ " LONG NOT NULL"+");";
		ContentValues contentvalues = new ContentValues();
		contentvalues.put(ChatDBConstants.FROM_USER_FIELD, from);
		if(packetId.contains("("))
			packetId = packetId.replace("(", "");
		if(packetId.contains(")"))
			packetId = packetId.replace(")", "");
		if(packetId.contains("\""))
			packetId = packetId.replace("\"", "");
		packetId = packetId.trim();
		contentvalues.put(ChatDBConstants.MESSAGE_ID,packetId);
		long currentTime = System.currentTimeMillis();
		calender.setTimeInMillis(currentTime);
		if(state == Message.SeenState.recieved)
			contentvalues.put(ChatDBConstants.DELIVER_TIME_FIELD, currentTime);
		if(state == Message.SeenState.seen)
			contentvalues.put(ChatDBConstants.SEEN_TIME_FIELD, currentTime);
		contentvalues.put(ChatDBConstants.SEEN_FIELD, state.ordinal());
//		contentvalues.put(ChatDBConstants.GROUP_UUID_FIELD, groupUUID);
		long insertId = chatDBWrapper.insertInDB(ChatDBConstants.TABLE_NAME_STATUS_INFO,contentvalues);
		if(insertId == -1){
			chatDBWrapper.updateGroupOrBroadCastSeenStatus(from,"(\"" + packetId + "\")", state,currentTime);
		}
		if(state == Message.SeenState.seen)
			chatDBWrapper.updateUserReadCount(packetId, chatDBWrapper.getTotalMessageReadCount(packetId) + 1);
		}catch(Exception e){}
	}
	private void addNewContactEntry(String displayName, String tmpUserName,String tmpMobile){
		try{
		String number = DBWrapper.getInstance().getContactNumber(tmpUserName);
		if(number!=null && !number.equals(""))
			return;
		ContentValues contentvalues = new ContentValues();
		contentvalues.put(DatabaseConstants.USER_NAME_FIELD,tmpUserName);
		contentvalues.put(DatabaseConstants.VOPIUM_FIELD,Integer.valueOf(2));
		contentvalues.put(DatabaseConstants.DATA_ID_FIELD,Integer.valueOf("5"));
		contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,tmpMobile);	
		contentvalues.put(DatabaseConstants.STATE_FIELD,Integer.valueOf(0));
		contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, displayName);
		contentvalues.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, "1");
		int id = tmpUserName.hashCode();
		if (id < -1)
			id = -(id);
		contentvalues.put(DatabaseConstants.NAME_CONTACT_ID_FIELD,Integer.valueOf(id));
		contentvalues.put(DatabaseConstants.RAW_CONTACT_ID,Integer.valueOf(id));
		DBWrapper.getInstance().insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,contentvalues);
		}catch(Exception e){
			
		}
	}
	public void saveMessage(String displayName, String from, String to,
			String msg, Message message) {
		String groupSenderDisplayName = message.getDisplayName();
//		if(groupSenderDisplayName!=null && !groupSenderDisplayName.equals("")){
//			displayName = groupSenderDisplayName;
//		}
		if(groupSenderDisplayName!=null && !groupSenderDisplayName.equals("") && !groupSenderDisplayName.equals("#786#")){
			String tmpFrom = message.getFrom();
			if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.sharedID.ordinal()){
				displayName = tmpFrom.substring(0, tmpFrom.indexOf('@'));
			}else if(tmpFrom != null && tmpFrom.contains("/") && tmpFrom.length()>(tmpFrom.indexOf("/")+1))
				displayName = groupSenderDisplayName+"#786#"+tmpFrom.substring(tmpFrom.indexOf("/")+1);
		}
		try {
			String captionTag  = message.getMediaTagMessage();
			String fileName  = message.getMediaFileName();
			String locationMsg  = message.getLocationMessage();
			ContentValues contentvalues = new ContentValues();

			contentvalues.put(ChatDBConstants.FROM_USER_FIELD, from);
			contentvalues.put(ChatDBConstants.TO_USER_FIELD, to);
			contentvalues.put(ChatDBConstants.UNREAD_COUNT_FIELD, new Integer(1));
			contentvalues.put(ChatDBConstants.FROM_GROUP_USER_FIELD, displayName);
			if((message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypeImage || message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypeVideo || message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypeAudio) 
					&& msg != null && !from.equals(SharedPrefManager.getInstance().getUserName()) && captionTag != null)
				contentvalues.put(ChatDBConstants.MEDIA_CAPTION_TAG, msg);
			else if(captionTag!=null && !captionTag.equals(""))
				contentvalues.put(ChatDBConstants.MEDIA_CAPTION_TAG, captionTag);
			if(fileName != null && !fileName.equals("")){
				contentvalues.put(ChatDBConstants.MEDIA_CAPTION_TAG, fileName);
			}
			if(locationMsg!=null && !locationMsg.equals(""))
				contentvalues.put(ChatDBConstants.MESSAGE_TYPE_LOCATION, locationMsg);
			contentvalues.put(ChatDBConstants.SEEN_FIELD, message.getMessageSeenState().ordinal());

			if(message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypeImage 
					|| message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypeVideo)//This check has been made to receive picture/audio/video message URL in Subject
			{
				MediaBody media = message.getMediaBody();
				if(media!=null){
					String type = media.getType();
					if(type!=null)
						contentvalues.put(ChatDBConstants.MESSAGE_TYPE_FIELD, message.getXMPPMessageType().ordinal());
					
					String urlImage = media.getUrl();
					if(urlImage!=null)
						contentvalues.put(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD, urlImage);
					
					
					String thumbImage = media.getThumb_data();
					if(thumbImage!=null)
						contentvalues.put(ChatDBConstants.MESSAGE_THUMB_FIELD, thumbImage);
				}
			}else if(message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypeDoc 
					|| message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypePdf
					|| message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypeXLS
					|| message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypePPT)//This check has been made to receive picture/audio/video message URL in Subject
			{
				MediaBody media = message.getMediaBody();
				if(media!=null){
					String type = media.getType();
					if(type!=null)
						contentvalues.put(ChatDBConstants.MESSAGE_TYPE_FIELD, message.getXMPPMessageType().ordinal());
					
					String fileUrl = media.getUrl();
					if(fileUrl!=null){
						contentvalues.put(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD, fileUrl);
					}
					String thumbImage = media.getThumb_data();
					if(thumbImage!=null)
						contentvalues.put(ChatDBConstants.MESSAGE_THUMB_FIELD, thumbImage);
				}
			}else if(message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypeAudio)//This check has been made to receive picture/audio/video message URL in Subject
			{
				MediaBody media = message.getMediaBody();
				if(media!=null){
					String type = media.getType();
					if(type!=null)
						contentvalues.put(ChatDBConstants.MESSAGE_TYPE_FIELD, message.getXMPPMessageType().ordinal());
					
					String urlVoice = media.getUrl();
					if(urlVoice!=null){
						if(urlVoice.contains(".caf")){
							urlVoice = urlVoice.replace(".caf", MEDIA_TYPE);
						}
						contentvalues.put(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD, urlVoice);
					}
					//Save Here Audio Length
					if(media.getAudioLength() != null)
						contentvalues.put(ChatDBConstants.MESSAGE_MEDIA_LENGTH, media.getAudioLength());
					String thumbImage = media.getThumb_data();
					if(thumbImage!=null)
						contentvalues.put(ChatDBConstants.MESSAGE_THUMB_FIELD, thumbImage);
				}
			}else if(message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypeContact
					|| message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypeLocation
					|| message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypePoll){
				contentvalues.put(ChatDBConstants.MESSAGE_TYPE_FIELD, message.getXMPPMessageType().ordinal());
			}
			
			if(message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypePoll){
				captionTag  = message.getMediaTagMessage();
				String PollMessageType = null;
				String PollID = null;
				if(captionTag != null){
					try {
						JSONObject jsonobj = new JSONObject(captionTag);
						if(jsonobj.has("PollMessageType") && jsonobj.getString("PollMessageType").toString().trim().length() > 0) {
							PollMessageType = jsonobj.getString("PollMessageType").toString();
						}
						if(jsonobj.has("PollID") && jsonobj.getString("PollID").toString().trim().length() > 0) {
							PollID = jsonobj.getString("PollID").toString();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				if(PollMessageType.equals("1"))
					savePoll(from, PollID, captionTag);
			}
			
				contentvalues.put(ChatDBConstants.MESSAGEINFO_FIELD, msg);
			// String name =
			// cursor.getString(cursor.getColumnIndex(ChatDBConstants.CONTACT_NAMES_FIELD));
			String myName = prefManager.getUserName();

			String name = "";
			String oppName = "";
			if (myName.equals(from)) {
				oppName = to;
				name = DBWrapper.getInstance().getChatName(to);
				contentvalues.put(ChatDBConstants.MESSAGE_ID,
						message.getPacketID());
				contentvalues.put(ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD,
						UUID.randomUUID().toString());
			} else {
				oppName = from;
				name = DBWrapper.getInstance().getChatName(from);
				contentvalues.put(ChatDBConstants.MESSAGE_ID, UUID
						.randomUUID().toString());
				String foregin = message.getPacketID();
				if (foregin != null && !foregin.equals(""))
					contentvalues.put(
							ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD,
							message.getPacketID());
				else
					contentvalues.put(
							ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD, UUID
									.randomUUID().toString());
			}

			long currentTime = System.currentTimeMillis();
			calender.setTimeInMillis(currentTime);
			int date = calender.get(Calendar.DATE);
			int oldDate = date;
			long milis = chatDBWrapper.lastMessageInDB(oppName);
			if(milis!=-1){
				calender.setTimeInMillis(milis);
				oldDate = calender.get(Calendar.DATE);
			}
			if ((oldDate != date)
					|| chatDBWrapper.isFirstChat(oppName)) {
				contentvalues.put(ChatDBConstants.IS_DATE_CHANGED_FIELD, "1");
			} else {
				contentvalues.put(ChatDBConstants.IS_DATE_CHANGED_FIELD, "0");
			}
//			AtMeApplication.dayValue = date;
			contentvalues.put(ChatDBConstants.LAST_UPDATE_FIELD, currentTime);

			contentvalues.put(ChatDBConstants.CONTACT_NAMES_FIELD, name);
			chatDBWrapper.insertInDB(ChatDBConstants.TABLE_NAME_MESSAGE_INFO, contentvalues);
			if (chatListener != null)
				chatListener.notifyChatRecieve(from, msg);
		} catch (Exception e) {
			Log.d(TAG, "Exception during save message" + from + "-" + to + "-"
					+ msg);
		}
	}

	public String saveMessage(String from, String to, String msg, Message message, byte messageType) {
		String name = "";
		String captionTag  = message.getMediaTagMessage();
		String fileName  = message.getMediaFileName();
		String locationMsg  = message.getLocationMessage();
		try {
			ContentValues contentvalues = new ContentValues();

			contentvalues.put(ChatDBConstants.FROM_USER_FIELD, from);
			contentvalues.put(ChatDBConstants.TO_USER_FIELD, to);
			if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.sharedID.ordinal()){
				String sharedid = message.getGroupId();
				String sharedid_display_name = message.getGroupDisplayname();
				contentvalues.put(ChatDBConstants.FROM_GROUP_USER_FIELD, sharedid_display_name+"<"+sharedid+">");
			}
			else
				contentvalues.put(ChatDBConstants.FROM_GROUP_USER_FIELD, "");
			
			if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.broadcasttoall.ordinal()){	
				//This is special bulletin message
				contentvalues.put(ChatDBConstants.MESSAGE_TYPE, message.getStatusMessageType().ordinal());
			}else if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.sharedID.ordinal()){
				contentvalues.put(ChatDBConstants.MESSAGE_TYPE, message.getStatusMessageType().ordinal());
			}
			if((message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypeImage || message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypeVideo || message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypeAudio) 
					&& msg != null && !from.equals(SharedPrefManager.getInstance().getUserName()) && captionTag != null)
				contentvalues.put(ChatDBConstants.MEDIA_CAPTION_TAG, msg);
			else if(captionTag!=null && !captionTag.equals(""))
				contentvalues.put(ChatDBConstants.MEDIA_CAPTION_TAG, captionTag);
			if(fileName != null && !fileName.equals("")){
				contentvalues.put(ChatDBConstants.MEDIA_CAPTION_TAG, fileName);
			}
			if(message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypeLocation){
				if(locationMsg!=null && !locationMsg.equals(""))
					contentvalues.put(ChatDBConstants.MESSAGE_TYPE_LOCATION, locationMsg);
			}
			contentvalues.put(ChatDBConstants.MESSAGE_TYPE_FIELD, messageType);
			contentvalues.put(ChatDBConstants.UNREAD_COUNT_FIELD, new Integer(1));
			contentvalues.put(ChatDBConstants.SEEN_FIELD, message.getMessageSeenState().ordinal());

			if(message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypeImage || 
					message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypeVideo)//This check has been made to receive picture/audio/video message URL in Subject
			{
				MediaBody media = message.getMediaBody();
				if(media!=null){
					String type = media.getType();
					if(type!=null)
						contentvalues.put(ChatDBConstants.MESSAGE_TYPE_FIELD, message.getXMPPMessageType().ordinal());
					
					String urlImage = media.getUrl();
					if(urlImage != null && urlImage.endsWith(".mov"))
						urlImage = urlImage.substring(0, urlImage.indexOf(".mov")) + ".mp4";
					if(urlImage!=null)
						contentvalues.put(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD, urlImage);
					
					String thumbImage = media.getThumb_data();
					if(thumbImage!=null)
						contentvalues.put(ChatDBConstants.MESSAGE_THUMB_FIELD, thumbImage);
				}
			}else if(message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypeDoc 
					|| message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypePdf
					|| message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypeXLS
					|| message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypePPT)//This check has been made to receive picture/audio/video message URL in Subject
			{
				MediaBody media = message.getMediaBody();
				if(media!=null){
					String type = media.getType();
					if(type!=null)
						contentvalues.put(ChatDBConstants.MESSAGE_TYPE_FIELD, message.getXMPPMessageType().ordinal());
					String urlPdf = media.getUrl();
					if(urlPdf!=null){
						contentvalues.put(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD, urlPdf);
					}
				}
			}else if(message.getXMPPMessageType() == XMPPMessageType.atMeXmppMessageTypeAudio)//This check has been made to receive picture/audio/video message URL in Subject
			{
				MediaBody media = message.getMediaBody();
				if(media!=null){
					String type = media.getType();
					if(type!=null)
						contentvalues.put(ChatDBConstants.MESSAGE_TYPE_FIELD, message.getXMPPMessageType().ordinal());
					
					String urlVoice = media.getUrl();
					if(urlVoice!=null){
						if(urlVoice.contains(".caf")){
							urlVoice = urlVoice.replace(".caf", MEDIA_TYPE);
						}
						contentvalues.put(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD, urlVoice);
					}
					//Save here Audio Length
					if(media.getAudioLength() != null)
						contentvalues.put(ChatDBConstants.MESSAGE_MEDIA_LENGTH, media.getAudioLength());
//					String thumbImage = media.getThumb_data();
//					if(thumbImage!=null)
//						contentvalues.put(ChatDBConstants.MESSAGE_THUMB_FIELD, thumbImage);
				}
			}
			contentvalues.put(ChatDBConstants.MESSAGEINFO_FIELD, msg);
			// String name =
			// cursor.getString(cursor.getColumnIndex(ChatDBConstants.CONTACT_NAMES_FIELD));
			String myName = prefManager.getUserName();

			
			String oppName = "";
			boolean isBroadCast = prefManager.isBroadCast(from);
			if (myName.equals(from) || isBroadCast) {
				oppName = to;
				name = DBWrapper.getInstance(context).getChatName(to);
				contentvalues.put(ChatDBConstants.MESSAGE_ID,
						message.getPacketID());
				contentvalues.put(ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD,
						UUID.randomUUID().toString());
			} else {
				oppName = from;
				name = DBWrapper.getInstance(context).getChatName(from);
				if(!name.contains("#786#")){
					String senderDisplayName = message.getDisplayName();
					if(senderDisplayName!=null && !senderDisplayName.equals("") ){
						name = senderDisplayName+"#786#"+from;
						String tMobile = "";
						if(from.contains("_"))
							tMobile = "+"+from.substring(0, from.indexOf("_"));
						addNewContactEntry(senderDisplayName,from,tMobile);
						String tFileId = message.getPicId();
						if(tFileId!=null && !tFileId.equals(""))
							prefManager.saveUserFileId(from, tFileId);
					}
				}
				contentvalues.put(ChatDBConstants.MESSAGE_ID, UUID
						.randomUUID().toString());
				String foregin = message.getPacketID();
				if (foregin != null && !foregin.equals(""))
					contentvalues.put(
							ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD,
							message.getPacketID());
				else
					contentvalues.put(
							ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD, UUID
									.randomUUID().toString());
			}

			long currentTime = System.currentTimeMillis();
			calender.setTimeInMillis(currentTime);
			int date = calender.get(Calendar.DATE);
			int oldDate = date;
			long milis = chatDBWrapper.lastMessageInDB(oppName);
			if(milis!=-1){
				calender.setTimeInMillis(milis);
				oldDate = calender.get(Calendar.DATE);
			}
			if ((oldDate != date)
					|| chatDBWrapper.isFirstChat(oppName)) {
				contentvalues.put(ChatDBConstants.IS_DATE_CHANGED_FIELD, "1");
			} else {
				contentvalues.put(ChatDBConstants.IS_DATE_CHANGED_FIELD, "0");
			}
//			AtMeApplication.dayValue = date;
			contentvalues.put(ChatDBConstants.LAST_UPDATE_FIELD, currentTime);

			contentvalues.put(ChatDBConstants.CONTACT_NAMES_FIELD, name);
			long insertedInfo = chatDBWrapper.insertInDB(ChatDBConstants.TABLE_NAME_MESSAGE_INFO,contentvalues);
			Log.d(TAG, "insertedInfo during message save: " + insertedInfo + " , " + contentvalues.valueSet().toArray());
			if (chatListener != null)
				chatListener.notifyChatRecieve(from, msg);
		} catch (Exception e) {
			Log.d(TAG, "Exception during save message" + from + "-" + to + "-"
					+ msg);
		}
		return name;
	}

	public String convertStringToWhereAs(String strs) {
		if (strs != null && strs.contains("[") && strs.contains("]")) {
			// Gson gson = new Gson();
			// strs = gson.toJson(strs);
			strs = strs.replace('[', '(');
			strs = strs.replace(']', ')');
		}
		return strs;
	}

	public void setConnection(PacketListener lstener) {
		// this.connection = connection;
		if (connection != null) {
			// Add a packet listener to get messages sent to us
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
			connection.addPacketListener(lstener, filter);
			// connection.
		}
	}
	public void setConnectionStatusListener(ConnectionListener connectionListener) {
		// this.connection = connection;
		if (connection != null) {
			connection.addConnectionListener(connectionListener);
		}
	}

	public void setTypingListener(PacketListener lstener) {
		// this.connection = connection;
		if (connection != null) {
			// Add a packet listener to get messages sent to us
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
			PacketFilter packetFilter = new PacketExtensionFilter("composing",
					"http://jabber.org/protocol/chatstates");
			connection.addPacketListener(lstener, packetFilter);
			// connection.
		}
	}
	public void setGroupTypingListener(PacketListener lstener) {
		// this.connection = connection;
		if (connection != null) {
			// Add a packet listener to get messages sent to us
			PacketFilter filter = new MessageTypeFilter(Message.Type.groupchat);
			PacketFilter packetFilter = new PacketExtensionFilter("composing",
					"http://jabber.org/protocol/chatstates");
			connection.addPacketListener(lstener, packetFilter);
			// connection.
		}
	}
	public void setRecordingStatusListener(PacketListener lstener) {
		// this.connection = connection;
		if (connection != null) {
			// Add a packet listener to get messages sent to us
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
			PacketFilter packetFilter = new PacketExtensionFilter("recording",
					"http://jabber.org/protocol/chatstates");
			connection.addPacketListener(lstener, packetFilter);
			// connection.
		}
	}
	public void setListeningStatusListener(PacketListener lstener) {
		// this.connection = connection;
		if (connection != null) {
			// Add a packet listener to get messages sent to us
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
			PacketFilter packetFilter = new PacketExtensionFilter("listening",
					"http://jabber.org/protocol/chatstates");
			connection.addPacketListener(lstener, packetFilter);
			// connection.
		}
	}
	
	public void setInvitationListener(PacketListener lstener) {
		if (connection != null) {
			connection.addPacketListener(lstener, invitationFilter);
		}
	}

	public void setGroupListener(PacketListener lstener) {
		// this.connection = connection;
		if (connection != null) {
			// Add a packet listener to get messages sent to us
			PacketFilter filter = new MessageTypeFilter(Message.Type.groupchat);
			connection.addPacketListener(lstener, filter);
			// connection.
		}
	}

	public void sendAck(String userName, List<String> strList,
			Message.SeenState state) {
		try {
			if (userName == null || userName.equals("")) {
				Toast.makeText(context,
						"userName is not correct: " + userName, 1000).show();
				return;
			}
			if (strList == null || strList.equals("")) {
				Toast.makeText(context,
						"Message id should not be empty or null: " + strList,
						1000).show();
				return;
			}

			if (connection == null || !connection.isConnected()) {
				Toast.makeText(
						context,
						"You have not logged in on chat server. Please try after some time",
						1000).show();
				// chatLogin(chatCountListener);
				return;
			}
			if (userName.equals(userMe)) {
				Toast.makeText(context,
						"Self messaging are not allowed.", 1000).show();
				return;
			}
			String to = userName + "@" + Constants.CHAT_DOMAIN;
			Message msg = new Message(to, Message.Type.chat);
			msg.setBody("");

			String[] strs = strList.toArray(new String[strList.size()]);
			Log.d(TAG, "sent packet ids string : " + strs.toString());
			Gson gson = new Gson();
			msg.setPacketID(StringUtils.escapeForXML(gson.toJson(strs)));
			Log.d(TAG, "sent packet packet id  : " + msg.getPacketID());
			msg.setDisplayName(prefManager.getDisplayName());
			String picId = prefManager.getUserFileId(prefManager.getUserName());
			if(picId != null)
				msg.setPicId(picId);
			msg.setMessageSeenState(state);
			if (connection != null && connection.isConnected()) {
				connection.sendPacket(msg);
				Log.d(TAG, "sent packet ack : " + msg.toXML() + "---" + gson.toJson(strs));
				chatDBWrapper.updateFrndsSeenStatus(userName,
						convertStringToWhereAs(gson.toJson(strs)),
						msg.getMessageSeenState());
			} else {
				Toast.makeText( context, "You are not connected with server. Please try after some time", 1000).show();
				// connection = null;
				return;
			}
		} catch (Exception e) {
			Log.d(TAG, "Exception occured during sendAck." + e.toString());
		}
	}
	public void sendGroupOrBroadcastAck(String userName, List<String> strList,
			Message.SeenState state,String broadCastOrGroupId23) {
		try {
			if (userName == null || userName.equals("")) {
				Toast.makeText(context,
						"userName is not correct: " + userName, 1000).show();
				return;
			}
			if (strList == null || strList.equals("")) {
				Toast.makeText(context,
						"Message id should not be empty or null: " + strList,
						1000).show();
				return;
			}

			if (connection == null || !connection.isConnected()) {
				Toast.makeText(
						context,
						"You have not logged in on chat server. Please try after some time",
						1000).show();
				// chatLogin(chatCountListener);
				return;
			}
			if (userName.equals(userMe)) {
				Toast.makeText(context,
						"Self messaging are not allowed.", 1000).show();
				return;
			}
			String to = userName + "@" + Constants.CHAT_DOMAIN;
			Message msg = new Message(to, Message.Type.chat);
			msg.setBody("");

			String[] strs = strList.toArray(new String[strList.size()]);
			Log.d(TAG, "sent packet ids string : " + strs.toString());
			Gson gson = new Gson();
			msg.setPacketID(StringUtils.escapeForXML(gson.toJson(strs)));
			Log.d(TAG, "sent packet packet id  : " + msg.getPacketID());
			msg.setDisplayName(prefManager.getDisplayName());
			String picId = prefManager.getUserFileId(prefManager.getUserName());
			if(picId != null)
				msg.setPicId(picId);
			msg.setMessageSeenState(state);
			msg.setStatusMessageType(Message.StatusMessageType.group);
			if (connection != null && connection.isConnected()) {
				connection.sendPacket(msg);
				Log.d(TAG,
						"sent packet ack : " + msg.toXML() + "---"
								+ gson.toJson(strs));
				chatDBWrapper.updateFrndsSeenStatus(userName,
						convertStringToWhereAs(gson.toJson(strs)),
						msg.getMessageSeenState());
			} else {
				Toast.makeText(
						context,
						"You are not connected with server. Please try after some time",
						1000).show();
				// connection = null;
				return;
			}
		} catch (Exception e) {
			Log.d(TAG, "Exception occured during sendAck." + e.toString());
		}
	}
	public void setTypingListener(TypingListener typingNotifier) {
		this.typingNotifier = typingNotifier;
	}

	public void setChatListener(ChatCountListener chatListener) {
		this.chatListener = chatListener;
	}
	public void setProfileUpdateListener(ProfileUpdateListener profileUpdateNotifier) {
		this.profileUpdateNotifier = profileUpdateNotifier;
	}

	public void setChatCountListener(ChatCountListener chatCountListener) {
		this.chatCountListener = chatCountListener;
	}

	public void clearAllNotifications() {
		if (notificationManager != null){
			notificationManager.cancelAll();
			isFirstMessage = true;
			previousUser = "";
		}
	}
	public boolean sendGroupOwnerTaskMessage(String groupCreaterName, String groupCreaterFileID, String userName, String groupUUID,String groupDisplayName, 
			String description, String groupPicId, String memberCount, Message.XMPPMessageType xMPPMessageType) {
		String to = userName + "@" + Constants.CHAT_DOMAIN;
		Log.d(TAG, "Sending text [" + description + "] to [" + to + "]");
		Message msg = new Message(to);// , Message.Type.chat);
		int type = xMPPMessageType.ordinal();

		if (type == XMPPMessageType.atMeXmppMessageTypeNewCreateGroup.ordinal()) {
			Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeNewCreateGroup: " + xMPPMessageType);
		} else if (type == XMPPMessageType.atMeXmppMessageTypeNewCreateBroadCast.ordinal()) {
			Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeNewCreateBroadCast: " + xMPPMessageType);
		}

//		if(isGroupChat)
//			msg.setType(Message.Type.groupchat);
//		else
			msg.setType(Message.Type.chat);
		msg.setXMPPMessageType(xMPPMessageType);
		if(description!=null && !description.equals(""))
			msg.setBody(description);
		if(groupCreaterName!=null && !groupCreaterName.equals(""))
			msg.setDisplayName(groupCreaterName);
		msg.setGroupOwnerName(userName);
		if(groupCreaterFileID!=null && !groupCreaterFileID.equals(""))
			msg.setPicId(groupCreaterFileID);
		msg.setGroupDisplayName(groupDisplayName);
		if(memberCount!=null && !memberCount.equals(""))
			msg.setGroupmemberCount(memberCount);
		if(groupPicId!=null && !groupPicId.equals(""))
			msg.setGroupPicId(groupPicId);
		if(groupUUID!=null && !groupUUID.equals(""))
			msg.setGroupId(groupUUID);
		msg.setPacketID(UUID.randomUUID().toString());
		Log.d(TAG, "sent packet: " + msg.toXML());
		if (connection != null && connection.isConnected()
				&& connection.isAuthenticated()) {
			connection.sendPacket(msg);
		} else {
			return false;
		}
//		saveMessage(userMe, userName, message, msg);
//		sendOffLineMessages1();
		return true;
	}
	public boolean sendGroupTaskMessage(String createrName, String createrFileID, String groupOwner, String userName, String groupUUID,String groupDisplayName, String description, String picId, 
			String memberCount, Message.XMPPMessageType xMPPMessageType) {
//		boolean isGroupChat = false;
//		if (SharedPrefManager.getInstance().isGroupChat(userName)
//				&& !description.equals("You are welcome in " + userName + " group.")) {
//			// sendGroupMessage(userName, message);
//			// return;
//			isGroupChat = true;
//		}


//		if (userName.equals(userMe)) {
//			Toast.makeText(context,
//					"Self messaging are not allowed.", 1000).show();
//			return false;
//		}
		String to = userName + "@" + Constants.CHAT_DOMAIN;
//		if (isGroupChat) {
//			to = userName + "@conference." + Constants.CHAT_DOMAIN;
//		}
		Log.d(TAG, "Sending text [" + description + "] to [" + to + "]");
		Message msg = new Message(to);// , Message.Type.chat);
		int type = xMPPMessageType.ordinal();

		if (type == XMPPMessageType.atMeXmppMessageTypeNewCreateGroup.ordinal()) {
			Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeNewCreateGroup: " + xMPPMessageType);
		} else if (type == XMPPMessageType.atMeXmppMessageTypeNewCreateBroadCast.ordinal()) {
			Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeNewCreateBroadCast: " + xMPPMessageType);
		}

//		if(isGroupChat)
//			msg.setType(Message.Type.groupchat);
//		else
			msg.setType(Message.Type.chat);
		msg.setXMPPMessageType(xMPPMessageType);
		if(description!=null && !description.equals(""))
			msg.setMediaTagMessage(description);
		if(createrName!=null && !createrName.equals(""))
			msg.setDisplayName(createrName);
		if(groupOwner!=null && !groupOwner.equals(""))
			msg.setDisplayName(createrName);
		if(groupDisplayName!=null && !groupDisplayName.equals(""))
			msg.setGroupDisplayName(groupDisplayName);
		if(createrFileID!=null && !createrFileID.equals(""))
			msg.setPicId(createrFileID);
		if(picId!=null && !picId.equals(""))
			msg.setGroupPicId(picId);
		if(memberCount!=null && !memberCount.equals(""))
			msg.setGroupmemberCount(memberCount);
		if(groupUUID!=null && !groupUUID.equals(""))
			msg.setGroupId(groupUUID);
		msg.setPacketID(UUID.randomUUID().toString());
		Log.d(TAG, "sent packet: " + msg.toXML());
		if (connection != null && connection.isConnected()
				&& connection.isAuthenticated()) {
			connection.sendPacket(msg);
		} else {
			return false;
		}
//		saveMessage(userMe, userName, message, msg);
//		sendOffLineMessages1();
		return true;
	}
	public boolean sendSGSubAdminAddRemove(String userName, Message.XMPPMessageType xMPPMessageType) {
		String to = userName + "@" + Constants.CHAT_DOMAIN;
		Message msg = new Message(to);
		msg.setType(Message.Type.chat);
		msg.setXMPPMessageType(xMPPMessageType);
		msg.setPacketID(UUID.randomUUID().toString());
		Log.d(TAG, "sent packet: " + msg.toXML());
		if (connection != null && connection.isConnected()
				&& connection.isAuthenticated()) {
			connection.sendPacket(msg);
		} else {
			return false;
		}
		return true;
	}

	public boolean sendInfoMessage(String groupName, String message,
			Message.XMPPMessageType xMPPMessageType) {
		boolean isGroupChat = false;
		if (SharedPrefManager.getInstance().isGroupChat(groupName)
				&& !message
						.equals("You are welcome in " + groupName + " group.")) {
			// sendGroupMessage(userName, message);
			// return;
			isGroupChat = true;
		}

		if (groupName == null || groupName.equals("")) {
			Toast.makeText(context,
					"userName is not correct: " + groupName, 1000).show();
			return false;
		}
		if (message == null || message.equals("")) {
			Toast.makeText(context,
					"Message should not be empty or null: " + message, 1000)
					.show();
			return false;
		}

		if (groupName.equals(userMe)) {
			Toast.makeText(context,
					"Self messaging are not allowed.", 1000).show();
			return false;
		}
		String to = groupName + "@" + Constants.CHAT_DOMAIN;
		if (isGroupChat) {
			to = groupName + "@conference." + Constants.CHAT_DOMAIN;
		}
		Log.d(TAG, "Sending text [" + message + "] to [" + to + "]");
		Message msg = new Message(to);// , Message.Type.chat);
		int type = xMPPMessageType.ordinal();

		if (type == XMPPMessageType.atMeXmppMessageTypeGroupName.ordinal()) {
			Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeGroupName: "
					+ xMPPMessageType);
		} else if (type == XMPPMessageType.atMeXmppMessageTypeMemberList
				.ordinal()) {
			Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeMemberList: "
					+ xMPPMessageType);
		} else if (type == XMPPMessageType.atMeXmppMessageTypeJoinGroup
				.ordinal()) {
			Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeJoinGroup: "
					+ xMPPMessageType);
		} else if (type == XMPPMessageType.atMeXmppMessageTypeLeftGroup
				.ordinal()) {
			Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeLeftGroup: "
					+ xMPPMessageType);
		} else if (type == XMPPMessageType.atMeXmppMessageTypeGroupAdminUpdate
				.ordinal()) {
			Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeGroupAdminUpdate: "
					+ xMPPMessageType);
		} else if (type == XMPPMessageType.atMeXmppMessageTypeNewCreateGroup
				.ordinal()) {
			Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeNewCreateGroup: "
					+ xMPPMessageType);
		}

		if (isGroupChat)
			msg.setType(Message.Type.groupchat);
		else
			msg.setType(Message.Type.chat);
		msg.setXMPPMessageType(xMPPMessageType);
		if (type == XMPPMessageType.atMeXmppMessageTypeLeftGroup.ordinal()){
			JSONObject finalJSONbject = null;
			SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
			try {
				finalJSONbject = new JSONObject();
				
				try {
					//For User Info
					finalJSONbject.put("userName", message);
					finalJSONbject.put("displayName", iPrefManager.getUserServerName(message));
					if(iPrefManager.getUserFileId(message) != null)
						finalJSONbject.put("fileId", iPrefManager.getUserFileId(message));
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(finalJSONbject != null){
//				msg.setMediaTagMessage(finalJSONbject.toString().replace("\"", "&quot;"));
				msg.setMediaTagMessage(finalJSONbject.toString());
			}
		}
		msg.setBody(message);
		msg.setPacketID(UUID.randomUUID().toString());
		Log.d(TAG, "sent packet: " + msg.toXML());
		if (connection != null && connection.isConnected()
				&& connection.isAuthenticated()) {
			connection.sendPacket(msg);
		} else {
			return false;
			// Toast.makeText(
			// context,
			// "You are not connected with server. Please try after some time",
			// 1000).show();
			// // connection = null;
			// return;
//			msg.setMessageSeenState(Message.SeenState.wait);
		}
//		saveMessage(userMe, userName, message, msg);
		/*
		 * Toast.makeText(context, message + ": " + message,
		 * 2000) .show();
		 */
//		sendOffLineMessages1();
		return true;
	}
	public boolean sendGroupInfoMessage(String groupName, String display_name, String caption,
			Message.XMPPMessageType xMPPMessageType) {
		boolean isGroupChat = false;
		if (SharedPrefManager.getInstance().isGroupChat(groupName)
				&& !display_name
				.equals("You are welcome in " + groupName + " group.")) {
			// sendGroupMessage(userName, message);
			// return;
			isGroupChat = true;
		}
		
		if (groupName == null || groupName.equals("")) {
			Toast.makeText(context,
					"userName is not correct: " + groupName, 1000).show();
			return false;
		}
		if (display_name == null || display_name.equals("")) {
			Toast.makeText(context,
					"Message should not be empty or null: " + display_name, 1000)
			.show();
			return false;
		}
		
		if (groupName.equals(userMe)) {
			Toast.makeText(context,
					"Self messaging are not allowed.", 1000).show();
			return false;
		}
		String to = groupName + "@" + Constants.CHAT_DOMAIN;
		if (isGroupChat) {
			to = groupName + "@conference." + Constants.CHAT_DOMAIN;
		}
		Log.d(TAG, "Sending text [" + display_name + "] to [" + to + "]");
		Message msg = new Message(to);// , Message.Type.chat);
		int type = xMPPMessageType.ordinal();
		
		if (type == XMPPMessageType.atMeXmppMessageTypeGroupName.ordinal()) {
			Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeGroupName: "
					+ xMPPMessageType);
		} else if (type == XMPPMessageType.atMeXmppMessageTypeMemberList
				.ordinal()) {
			Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeMemberList: "
					+ xMPPMessageType);
		} else if (type == XMPPMessageType.atMeXmppMessageTypeJoinGroup
				.ordinal()) {
			Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeJoinGroup: "
					+ xMPPMessageType);
		} else if (type == XMPPMessageType.atMeXmppMessageTypeLeftGroup
				.ordinal()) {
			Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeLeftGroup: "
					+ xMPPMessageType);
		} else if (type == XMPPMessageType.atMeXmppMessageTypeGroupAdminUpdate
				.ordinal()) {
			Log.d(TAG, "XMPPMessageType atMeXmppMessageTypeGroupAdminUpdate: "
					+ xMPPMessageType);
		}
		
		if (isGroupChat)
			msg.setType(Message.Type.groupchat);
		else
			msg.setType(Message.Type.chat);
		msg.setXMPPMessageType(xMPPMessageType);
		if(type == XMPPMessageType.atMeXmppMessageTypeGroupName.ordinal() || type == XMPPMessageType.atMeXmppMessageTypeMemberList.ordinal()){
			if(caption!=null && !caption.equals(""))
				msg.setMediaTagMessage(caption);
		}
		if(type == XMPPMessageType.atMeXmppMessageTypeMemberList.ordinal()) {// specific for ios
			String displayName = "";
			if(caption != null){
				if(caption.contains("&quot;"))
					caption = caption.replace("&quot;", "\"");
				try {
					JSONObject jsonobj = new JSONObject(caption);
					if(jsonobj.has("displayName") && jsonobj.getString("displayName").toString().trim().length() > 0) {
						displayName = jsonobj.getString("displayName").toString();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			msg.setBody("Added you to group '"+displayName+"'.");
		}else
			msg.setBody(display_name);
		msg.setPacketID(UUID.randomUUID().toString());
		Log.d(TAG, "sent packet: " + msg.toXML());
		if (connection != null && connection.isConnected()
				&& connection.isAuthenticated()) {
			connection.sendPacket(msg);
		} else {
			return false;
		}
		return true;
	}
	public boolean sendSpecialMessageToAllDomainMembers(String broadcast_name,
			String caption, Message.XMPPMessageType xMPPMessageType){
//		String to = broadcast_name + "@" + Constants.CHAT_DOMAIN;
		String to = broadcast_name + "@broadcast." + Constants.CHAT_DOMAIN;
		Message msg = new Message(to);
		msg.setType(Message.Type.chat);
		msg.setDisplayName(prefManager.getDisplayName());
		msg.setXMPPMessageType(xMPPMessageType);
		if(caption != null && !caption.equals(""))
			msg.setMediaTagMessage(caption);
		msg.setStatusMessageType(Message.StatusMessageType.broadcast);
		msg.setPacketID(UUID.randomUUID().toString());
		msg.setBody("");
		Log.i(TAG, "sendSpecialMessageToAllDomainMembers : Sent XML Packet: " + msg.toXML());
		if (connection != null && connection.isConnected()
				&& connection.isAuthenticated()) {
			connection.sendPacket(msg);
		} else {
			return false;
		}
		return true;
	}
	
	public void sendBroadCastMessageToAll(String userName, String message) {
		if (SharedPrefManager.getInstance().isBroadCast(userName)
				&& !message.equals("You are welcome in " + userName + " group.")) {
		}
		if (userName == null || userName.equals("")) {
			Toast.makeText(context, "userName is not correct: " + userName, 1000).show();
			return;
		}
		if (message == null || message.equals("")) {
			Toast.makeText(context, "Message should not be empty or null: " + message, 1000).show();
			return;
		}
		if (userName.equals(userMe)) {
			Toast.makeText(context, "Self messaging is not allowed.", 1000).show();
			return;
		}
		String to = userName + "@broadcast." + Constants.CHAT_DOMAIN;
		Log.d(TAG, "Sending text [" + message + "] to [" + to + "]");
		Message msg = new Message(to);
		msg.setType(Message.Type.chat);
		msg.setMessageSeenState(Message.SeenState.sent);
		msg.setDisplayName(prefManager.getDisplayName());
		String picId = prefManager.getUserFileId(prefManager.getUserName());
		if(picId != null)
			msg.setPicId(picId);
		msg.setStatusMessageType(Message.StatusMessageType.broadcasttoall);
		msg.setBody(message);
		msg.setPacketID(UUID.randomUUID().toString());
		Log.d(TAG, "sent packet: " + msg.toXML());
		if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
			connection.sendPacket(msg);
			xmppConectionStatus = true;
		} else {
			msg.setMessageSeenState(Message.SeenState.wait);
		}
		saveMessage(userMe, userName, message, msg,(byte)0);
		sendOffLineMessages1();
		if(connectionStatusListener!=null)
			connectionStatusListener.notifyConnectionChange();
	}
	public void sendSharedIDMessageToAllAdmins(String shared_id, String message) {
		String groupDisplayName = prefManager.getSharedIDDisplayName(shared_id);
		String groupFileID = prefManager.getSharedIDFileId(shared_id);
		if (SharedPrefManager.getInstance().isBroadCast(shared_id)
				&& !message.equals("You are welcome in " + shared_id + " group.")) {
		}
		if (shared_id == null || shared_id.equals("")) {
			Toast.makeText(context, "userName is not correct: " + shared_id, 1000).show();
			return;
		}
		if (message == null || message.equals("")) {
			Toast.makeText(context, "Message should not be empty or null: " + message, 1000).show();
			return;
		}
		if (shared_id.equals(userMe)) {
			Toast.makeText(context, "Self messaging is not allowed.", 1000).show();
			return;
		}
		String to = shared_id + "@broadcast." + Constants.CHAT_DOMAIN;
		Log.d(TAG, "Sending text [" + message + "] to [" + to + "]");
		Message msg = new Message(to);
		msg.setType(Message.Type.chat);
		msg.setMessageSeenState(Message.SeenState.sent);
		msg.setDisplayName(prefManager.getDisplayName());
		String picId = prefManager.getUserFileId(prefManager.getUserName());
		if(picId != null)
			msg.setPicId(picId);
		//Set sharedID group name, shared ID Display name, Shared ID file id
		if(shared_id!=null && !shared_id.equals(""))
			msg.setGroupId(shared_id);
		if(groupDisplayName!=null && !groupDisplayName.equals(""))
			msg.setGroupDisplayName(groupDisplayName);
		if(groupFileID!=null && !groupFileID.equals(""))
			msg.setGroupPicId(groupFileID);
		
		msg.setStatusMessageType(Message.StatusMessageType.sharedID);
		msg.setBody(message);
		msg.setPacketID(UUID.randomUUID().toString());
		Log.d(TAG, "sent packet: " + msg.toXML());
		if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
			connection.sendPacket(msg);
			xmppConectionStatus = true;
		} else {
			msg.setMessageSeenState(Message.SeenState.wait);
		}
		saveMessage(userMe, shared_id, message, msg,(byte)0);
		sendOffLineMessages1();
		if(connectionStatusListener!=null)
			connectionStatusListener.notifyConnectionChange();
	}
	public void sendBroadCastMessage(String userName, String message) {
		if (SharedPrefManager.getInstance().isBroadCast(userName)
				&& !message.equals("You are welcome in " + userName + " group.")) {
		}
		if (userName == null || userName.equals("")) {
			Toast.makeText(context, "userName is not correct: " + userName, 1000).show();
			return;
		}
		if (message == null || message.equals("")) {
			Toast.makeText(context, "Message should not be empty or null: " + message, 1000).show();
			return;
		}
		if (userName.equals(userMe)) {
			Toast.makeText(context, "Self messaging are not allowed.", 1000).show();
			return;
		}
		String to = userName + "@broadcast." + Constants.CHAT_DOMAIN;
		Log.d(TAG, "Sending text [" + message + "] to [" + to + "]");
		Message msg = new Message(to);
		msg.setType(Message.Type.chat);
		msg.setMessageSeenState(Message.SeenState.sent);
		msg.setDisplayName(prefManager.getDisplayName());
		String picId = prefManager.getUserFileId(prefManager.getUserName());
		if(picId != null)
			msg.setPicId(picId);
		msg.setStatusMessageType(Message.StatusMessageType.broadcast);
		msg.setBody(message);
		msg.setPacketID(UUID.randomUUID().toString());
		Log.d(TAG, "sent packet: " + msg.toXML());
		if (connection != null && connection.isConnected()
				&& connection.isAuthenticated()) {
			connection.sendPacket(msg);
			xmppConectionStatus = true;
		} else {
			msg.setMessageSeenState(Message.SeenState.wait);
		}
			saveMessage(userMe, userName, message, msg,(byte)0);
		sendOffLineMessages1();
		if(connectionStatusListener!=null)
			connectionStatusListener.notifyConnectionChange();
	}
	public void sendBroadCastMessageOld(String userName, String message) {
		boolean isGroupChat = false;
		if (SharedPrefManager.getInstance().isGroupChat(userName)
				&& !message
						.equals("You are welcome in " + userName + " group.")) {
			// sendGroupMessage(userName, message);
			// return;
			isGroupChat = true;
			if(!prefManager.isGroupMemberActive(userName, userMe)){
				return;
			}
		}

		if (userName == null || userName.equals("")) {
			Toast.makeText(context,
					"userName is not correct: " + userName, 1000).show();
			return;
		}
		if (message == null || message.equals("")) {
			Toast.makeText(context,
					"Message should not be empty or null: " + message, 1000)
					.show();
			return;
		}

		// if (connection == null || !connection.isConnected()) {
		// // Toast.makeText(
		// // context,
		// //
		// "You have not logged in on chat server. Please try after some time",
		// // 1000).show();
		// chatLogin();
		// //return;
		// }
		if (userName.equals(userMe)) {
			Toast.makeText(context,
					"Self messaging are not allowed.", 1000).show();
			return;
		}
		String to = userName + "@" + Constants.CHAT_DOMAIN;
		if (isGroupChat) {
			to = userName + "@conference." + Constants.CHAT_DOMAIN;
		}
		Log.d(TAG, "Sending text [" + message + "] to [" + to + "]");
		Message msg = new Message(to);// , Message.Type.chat);
		if (isGroupChat)
			msg.setType(Message.Type.groupchat);
		else
			msg.setType(Message.Type.chat);
		msg.setMessageSeenState(Message.SeenState.sent);
//		if(message.startsWith(AppConstants.PIC_SEP))//This is to send picture/audio/video URL in Subject as decided b/w ios and android
//		{
//			msg.setSubject(message.substring(message.indexOf(AppConstants.PIC_SEP) + AppConstants.PIC_SEP.length()));
//			msg.setXMPPMessageType(XMPPMessageType.atMeXmppMessageTypeImage);
//			msg.setBody("sent you an image");
//		}
//		else
			msg.setBody(message);
		msg.setPacketID(UUID.randomUUID().toString());
		Log.d(TAG, "sent packet: " + msg.toXML());
		if (connection != null && connection.isConnected()
				&& connection.isAuthenticated()) {
			connection.sendPacket(msg);
			xmppConectionStatus = true;
		} else {
//			xmppConectionStatus = false;
			// Toast.makeText(
			// context,
			// "You are not connected with server. Please try after some time",
			// 1000).show();
			// // connection = null;
			// return;
			msg.setMessageSeenState(Message.SeenState.wait);
		}
//			saveMessage(userMe, userName, message, msg,(byte)0);
		/*
		 * Toast.makeText(context, message + ": " + message,
		 * 2000) .show();
		 */
		sendOffLineMessages1();
		if(connectionStatusListener!=null)
			connectionStatusListener.notifyConnectionChange();
	}
	public void sendMessage(String userName, String message) {
		boolean isGroupChat = false;
		if (SharedPrefManager.getInstance().isGroupChat(userName)
				&& !message.equals("You are welcome in " + userName + " group.")) {
			isGroupChat = true;
			if(!prefManager.isGroupMemberActive(userName, userMe)){
				return;
			}
		}
		if (userName == null || userName.equals("")) {
			Toast.makeText(context, "userName is not correct: " + userName, 1000).show();
			return;
		}
		if (message == null || message.equals("")) {
			Toast.makeText(context, "Message should not be empty or null: " + message, 1000).show();
			return;
		}
		if (userName.equals(userMe)) {
			Toast.makeText(context, "Self messaging are not allowed.", 1000).show();
			return;
		}
		String to = userName + "@" + Constants.CHAT_DOMAIN;
		if (isGroupChat) {
			to = userName + "@conference." + Constants.CHAT_DOMAIN;
		}
		Log.d(TAG, "Sending text [" + message + "] to [" + to + "]");
		Message msg = new Message(to);// , Message.Type.chat);
		if (isGroupChat)
			msg.setType(Message.Type.groupchat);
		else
			msg.setType(Message.Type.chat);
		msg.setDisplayName(prefManager.getDisplayName());
		String picId = prefManager.getUserFileId(prefManager.getUserName());
		if(picId != null)
			msg.setPicId(picId);
		msg.setMessageSeenState(Message.SeenState.sent);
		msg.setBody(message);
		msg.setPacketID(UUID.randomUUID().toString());
		Log.d(TAG, "sent packet: " + msg.toXML());
		if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
			connection.sendPacket(msg);
			xmppConectionStatus = true;
		} else {
//			xmppConectionStatus = false;
			msg.setMessageSeenState(Message.SeenState.wait);
			if(SuperChatApplication.isNetworkConnected()){
				//try reconnecting here
//				stopService(new Intent(this, ChatService.class));
//				startService(new Intent(this, ChatService.class));
				xmppConectionStatus = false;
				chatLogin();
			}
		}
		saveMessage(userMe, userName, message, msg,(byte)0);
		sendOffLineMessages1();
		if(connectionStatusListener!=null)
			connectionStatusListener.notifyConnectionChange();
	}
	public String sendMediaMessage(String userName, String message , String captionMsg, String url, String thumb, XMPPMessageType mediaType) 
	{
		boolean isGroupChat = false;
		String packetID = null;
		int audio_length = 0;
		boolean bulletin_broadcast = false;
		boolean shared_id_message = false;
		String groupDisplayName = null;
		String groupFileID = null;
		if(prefManager.isSharedIDContact(userName)){
			shared_id_message = true;
			groupDisplayName = prefManager.getSharedIDDisplayName(userName);
			groupFileID = prefManager.getSharedIDFileId(userName);
		}
		if (prefManager.isGroupChat(userName) && !message.equals("You are welcome in " + userName + " group.")) {
			isGroupChat = true;
			if(!prefManager.isGroupMemberActive(userName, userMe)){
				return packetID;
			}
		}
		if (userName == null || userName.equals("")) {
			Toast.makeText(context, "userName is not correct: " + userName, 1000).show();
			return packetID;
		}
		if (url == null || url.equals("")) {
			Toast.makeText(context, "Message should not be empty or null: " + url, 1000).show();
			return packetID;
		}
		if (userName.equals(userMe)) {
			Toast.makeText(context, "Self messaging are not allowed.", 1000).show();
			return packetID;
		}
		String to = userName + "@" + Constants.CHAT_DOMAIN;
		if (isGroupChat) {
			to = userName + "@conference." + Constants.CHAT_DOMAIN;
		}else if(userName.equals(SharedPrefManager.getInstance().getUserDomain() + "-all")){
			to = userName + "@broadcast." + Constants.CHAT_DOMAIN;
			bulletin_broadcast = true;
		}else if(shared_id_message){
			to = userName + "@broadcast." + Constants.CHAT_DOMAIN;
		}
		Log.d(TAG, "Sending text [" + message + "] to [" + to + "]");
		Message msg = new Message(to);// , Message.Type.chat);
		
		if(mediaType == XMPPMessageType.atMeXmppMessageTypeImage){
			message = "Sent an image";
			msg.setBody("Sent you an image");
		}else if(mediaType == XMPPMessageType.atMeXmppMessageTypeVideo){
			message = "Sent a video";
			msg.setBody("Sent you a video");
		}else if(mediaType == XMPPMessageType.atMeXmppMessageTypeDoc){
			message = "Sent a doc file";
			msg.setBody("Sent you a doc file");
		}else if(mediaType == XMPPMessageType.atMeXmppMessageTypePdf){
			message = "Sent a pdf file";
			msg.setBody("Sent you a pdf file");
		}else if(mediaType == XMPPMessageType.atMeXmppMessageTypeXLS){
			message = "Sent a xls file";
			msg.setBody("Sent you a xls file");
		}else if(mediaType == XMPPMessageType.atMeXmppMessageTypePPT){
			message = "Sent a ppt";
			msg.setBody("Sent you a ppt");
		}else if(mediaType == XMPPMessageType.atMeXmppMessageTypeLocation){
			message = "Shared location";
			msg.setBody("Shared location");
		}else if(mediaType == XMPPMessageType.atMeXmppMessageTypeContact){
			message = "Shared contact";
			msg.setBody("Shared contact");
		}else if(mediaType == XMPPMessageType.atMeXmppMessageTypePoll){
			message = "Poll";
			msg.setBody("Poll");
		}else if(mediaType == XMPPMessageType.atMeXmppMessageTypeAudio){
			audio_length = MediaPlayer.create(context, Uri.fromFile(new File(url))).getDuration()/1000;
			byte minutes = 0;
    		byte seconds = 0;
    		String len_msg = minutes + ":" + ((seconds < 10) ? ("0"+seconds) : seconds);
			if(audio_length > 0)
				message = "Sent a voice note : "+ len_msg;
			else
				message = "Sent a voice note";
			if(audio_length > 0)
				msg.setBody("Sent you a voice note : "+len_msg);
			else
				msg.setBody("Sent you a voice note");
		}
		if (isGroupChat)
			msg.setType(Message.Type.groupchat);
		else
			msg.setType(Message.Type.chat);
		msg.setMessageSeenState(Message.SeenState.sent);
		msg.setDisplayName(prefManager.getDisplayName());
		String picId = prefManager.getUserFileId(prefManager.getUserName());
		if(picId != null)
			msg.setPicId(picId);
		
		if(bulletin_broadcast)
			msg.setStatusMessageType(Message.StatusMessageType.broadcasttoall);
		else if(shared_id_message){
			//Set sharedID group name, shared ID Display name, Shared ID file id
			if(userName!=null && !userName.equals(""))
				msg.setGroupId(userName);
			if(groupDisplayName!=null && !groupDisplayName.equals(""))
				msg.setGroupDisplayName(groupDisplayName);
			if(groupFileID!=null && !groupFileID.equals(""))
				msg.setGroupPicId(groupFileID);
			msg.setStatusMessageType(Message.StatusMessageType.sharedID);
		}
		
		msg.setXMPPMessageType(mediaType);
		msg.setBody(message);
		msg.setPacketID(UUID.randomUUID().toString());
		if(captionMsg!=null && !captionMsg.equals("")){
			msg.setMediaTagMessage(captionMsg);
		}
		if(url!=null){
			MediaBody mediaBody = msg.new MediaBody();
			mediaBody.setType(String.valueOf(mediaType.ordinal()));
			mediaBody.setUrl(url);
			if(thumb!=null)
				mediaBody.setThumb_data(thumb);
			//Set Audio Length
			if(mediaType == XMPPMessageType.atMeXmppMessageTypeAudio){
				if(audio_length > 0)
					mediaBody.setAudioLength(""+audio_length);
			}
			msg.setMediaBody(mediaBody);
		}
		System.out.println("[[sent packet: " + msg.toXML());
//		if (connection != null && connection.isConnected()
//				&& connection.isAuthenticated()) {
//			connection.sendPacket(msg);
//		} else 
		{
			// Toast.makeText(
			// context,
			// "You are not connected with server. Please try after some time",
			// 1000).show();
			// // connection = null;
			// return;
			msg.setMessageSeenState(Message.SeenState.pic_wait);
		}
		saveMessage(userMe, userName, message, msg, (byte) mediaType.ordinal());
		return msg.getPacketID();
	}
	public void sendContactAndLocation(String userName, String message, String captionOrLocationMsg,
			String thumbURL, XMPPMessageType messageType, boolean is_broadcast) 
	{
		boolean isGroupChat = false;
		boolean bulletin_broadcast = false;
		boolean shared_id_message = false;
		String groupDisplayName = null;
		String groupFileID = null;
		if(prefManager.isSharedIDContact(userName)){
			shared_id_message = true;
			groupDisplayName = prefManager.getSharedIDDisplayName(userName);
			groupFileID = prefManager.getSharedIDFileId(userName);
		}
		if (SharedPrefManager.getInstance().isGroupChat(userName)
				&& !message.equals("You are welcome in " + userName + " group.")) {
			isGroupChat = true;
			if(!prefManager.isGroupMemberActive(userName, userMe)){
				return;
			}
		}
		if (userName == null || userName.equals("")) {
			Toast.makeText(context,
					"userName is not correct: " + userName, 1000).show();
			return;
		}
		if (userName.equals(userMe)) {
//			Toast.makeText(context, "Self messaging are not allowed.", 1000).show();
			return;
		}
		String to = userName + "@" + Constants.CHAT_DOMAIN;
		if (isGroupChat) {
			to = userName + "@conference." + Constants.CHAT_DOMAIN;
		}else if (SharedPrefManager.getInstance().isBroadCast(userName)){
			to = userName + "@broadcast." + Constants.CHAT_DOMAIN;
		}else if(userName.equals(SharedPrefManager.getInstance().getUserDomain() + "-all")){
			to = userName + "@broadcast." + Constants.CHAT_DOMAIN;
			bulletin_broadcast = true;
		}else if(shared_id_message){
			to = userName + "@broadcast." + Constants.CHAT_DOMAIN;
		}
		Log.d(TAG, "Sending text [" + message + "] to [" + to + "]");
		Message msg = new Message(to);
		if (isGroupChat)
			msg.setType(Message.Type.groupchat);
		else
			msg.setType(Message.Type.chat);
		msg.setMessageSeenState(Message.SeenState.sent);
		msg.setDisplayName(prefManager.getDisplayName());
		String picId = prefManager.getUserFileId(prefManager.getUserName());
		if(picId != null)
			msg.setPicId(picId);
		if(bulletin_broadcast)
			msg.setStatusMessageType(Message.StatusMessageType.broadcasttoall);
		if(shared_id_message){
			//Set sharedID group name, shared ID Display name, Shared ID file id
			if(userName!=null && !userName.equals(""))
				msg.setGroupId(userName);
			if(groupDisplayName!=null && !groupDisplayName.equals(""))
				msg.setGroupDisplayName(groupDisplayName);
			if(groupFileID!=null && !groupFileID.equals(""))
				msg.setGroupPicId(groupFileID);
			msg.setStatusMessageType(Message.StatusMessageType.sharedID);
		}
		
		msg.setXMPPMessageType(messageType);
		if(messageType == XMPPMessageType.atMeXmppMessageTypeContact){
			if(captionOrLocationMsg!=null && !captionOrLocationMsg.equals("")){
				msg.setMediaTagMessage(captionOrLocationMsg);
			}
			message = "Shared contact";
			msg.setBody("Shared contact");
		}
		else if(messageType == XMPPMessageType.atMeXmppMessageTypeLocation){
			if(message!=null && !message.equals("")){
				msg.setMediaTagMessage(message);
			}
			if(captionOrLocationMsg!=null && !captionOrLocationMsg.equals("")){
				msg.setLocationMessage(captionOrLocationMsg);
			}
			msg.setBody("Shared location");
		}
		
		if(thumbURL!=null){
			MediaBody mediaBody = msg.new MediaBody();
			mediaBody.setType(String.valueOf(messageType.ordinal()));
			mediaBody.setUrl(thumbURL);
			if(thumbURL!=null)
			mediaBody.setThumb_data(thumbURL);
			msg.setMediaBody(mediaBody);
		}
		msg.setPacketID(UUID.randomUUID().toString());
		Log.d(TAG, "sent packet: " + msg.toXML());
		if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
			connection.sendPacket(msg);
			xmppConectionStatus = true;
		} else {
//			xmppConectionStatus = false;
			msg.setMessageSeenState(Message.SeenState.wait);
		}
		if(!is_broadcast)
			saveMessage(userMe, userName, message, msg, (byte)messageType.ordinal());
		sendOffLineMessages1();
		if(connectionStatusListener!=null)
			connectionStatusListener.notifyConnectionChange();
	}

	public void sendPoll(String userName, String message, String captionOrLocationMsg,
			XMPPMessageType messageType, int poll_type)
	{
		if (userName == null || userName.equals("")) {
			Toast.makeText(context, "userName is not correct: " + userName, Toast.LENGTH_SHORT).show();
			return;
		}
		if (userName.equals(userMe)) {
//			Toast.makeText(context, "Self messaging are not allowed.", 1000).show();
			return;
		}
		String to = userName + "@" + Constants.CHAT_DOMAIN;
		to = userName + "@conference." + Constants.CHAT_DOMAIN;
		Log.d(TAG, "Sending text [" + message + "] to [" + to + "]");
		Message msg = new Message(to);
		msg.setType(Message.Type.groupchat);
		msg.setMessageSeenState(Message.SeenState.sent);
		msg.setDisplayName(prefManager.getDisplayName());
		String picId = prefManager.getUserFileId(prefManager.getUserName());
		if(picId != null)
			msg.setPicId(picId);
		
		msg.setXMPPMessageType(messageType);
			if(captionOrLocationMsg!=null && !captionOrLocationMsg.equals("")){
				msg.setMediaTagMessage(captionOrLocationMsg);
			}
		if(poll_type == 2){
			message = "Replied Poll";
			msg.setBody("Replied Poll");
		}else{
			message = "Created Poll";
			msg.setBody("Created Poll");
		}

		msg.setPacketID(UUID.randomUUID().toString());
		Log.d(TAG, "sent packet: " + msg.toXML());
		if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
			connection.sendPacket(msg);
			xmppConectionStatus = true;
		} else {
//			xmppConectionStatus = false;
			msg.setMessageSeenState(Message.SeenState.wait);
		}

		if(poll_type != 2) {
			saveMessage(userMe, userName, message, msg, (byte) messageType.ordinal());
			sendOffLineMessages1();
		}
		if(connectionStatusListener!=null)
			connectionStatusListener.notifyConnectionChange();
	}

	public void sendMediaURL(String userName, String message,  String packetID , String captionMsg, String fileName, String url, String thumb, XMPPMessageType mediaType) 
	{
		boolean isGroupChat = false;
		boolean bulletin_broadcast = false;
		int audio_length = 0;
		boolean shared_id_message = false;
		String groupDisplayName = null;
		String groupFileID = null;
		if(prefManager.isSharedIDContact(userName)){
			shared_id_message = true;
			groupDisplayName = prefManager.getSharedIDDisplayName(userName);
			groupFileID = prefManager.getSharedIDFileId(userName);
		}
		if (SharedPrefManager.getInstance().isGroupChat(userName)
				&& !message.equals("You are welcome in " + userName + " group.")) {
			isGroupChat = true;
			if(!prefManager.isGroupMemberActive(userName, userMe)){
				return;
			}
		}
		
		if (userName == null || userName.equals("")) {
			Toast.makeText(context,
					"userName is not correct: " + userName, 1000).show();
			return;
		}
		if (url == null || url.equals("")) {
			Toast.makeText(context,
					"Message should not be empty or null: " + url, 1000)
					.show();
			return;
		}
		if (userName.equals(userMe)) {
			return;
		}
		String to = userName + "@" + Constants.CHAT_DOMAIN;
		if (isGroupChat) {
			to = userName + "@conference." + Constants.CHAT_DOMAIN;
		}else if (SharedPrefManager.getInstance().isBroadCast(userName)){
			to = userName + "@broadcast." + Constants.CHAT_DOMAIN;
		}else if(userName.equals(SharedPrefManager.getInstance().getUserDomain() + "-all")){
			to = userName + "@broadcast." + Constants.CHAT_DOMAIN;
			bulletin_broadcast = true;
		}else if(shared_id_message){
			to = userName + "@broadcast." + Constants.CHAT_DOMAIN;
		}
			
		Log.d(TAG, "Sending text [" + message + "] to [" + to + "]");
		Message msg = new Message(to);// , Message.Type.chat);
		if (isGroupChat)
			msg.setType(Message.Type.groupchat);
		else
			msg.setType(Message.Type.chat);
		msg.setMessageSeenState(Message.SeenState.sent);
		msg.setDisplayName(prefManager.getDisplayName());
		String picId = prefManager.getUserFileId(prefManager.getUserName());
		if(picId != null)
			msg.setPicId(picId);
		if(bulletin_broadcast)
			msg.setStatusMessageType(Message.StatusMessageType.broadcasttoall);
		if(shared_id_message){
			//Set sharedID group name, shared ID Display name, Shared ID file id
			if(userName!=null && !userName.equals(""))
				msg.setGroupId(userName);
			if(groupDisplayName!=null && !groupDisplayName.equals(""))
				msg.setGroupDisplayName(groupDisplayName);
			if(groupFileID!=null && !groupFileID.equals(""))
				msg.setGroupPicId(groupFileID);
			msg.setStatusMessageType(Message.StatusMessageType.sharedID);
		}
		
		if(captionMsg!=null && !captionMsg.equals("")){
			msg.setMediaTagMessage(captionMsg);
		}
		if(mediaType != XMPPMessageType.atMeXmppMessageTypeAudio && fileName!=null && !fileName.equals("")){
			msg.setMediaFileName(fileName);
		}
//		if(message.startsWith(AppConstants.PIC_SEP))//This is to send picture/audio/video URL in Subject as decided b/w ios and android
		{
//			msg.setSubject(message.substring(message.indexOf(AppConstants.PIC_SEP) + AppConstants.PIC_SEP.length()));
			msg.setXMPPMessageType(mediaType);
			if(mediaType == XMPPMessageType.atMeXmppMessageTypeImage){
				if(captionMsg!=null && !captionMsg.equals(""))
					msg.setBody(captionMsg);
				else
					msg.setBody("Sent you an image");
			}else if(mediaType == XMPPMessageType.atMeXmppMessageTypeAudio){
				if(captionMsg!=null && !captionMsg.equals(""))
					msg.setBody(captionMsg);
				else
					msg.setBody("Sent you a voice note");
			}else if(mediaType == XMPPMessageType.atMeXmppMessageTypeVideo){
				if(captionMsg!=null && !captionMsg.equals(""))
					msg.setBody(captionMsg);
				else
				msg.setBody("Sent you a video");
			}else if(mediaType == XMPPMessageType.atMeXmppMessageTypeDoc)
				msg.setBody("Sent you a doc");
			else if(mediaType == XMPPMessageType.atMeXmppMessageTypePdf)
				msg.setBody("Sent you a pdf");
			else if(mediaType == XMPPMessageType.atMeXmppMessageTypeXLS)
				msg.setBody("Sent you a xls");
			else if(mediaType == XMPPMessageType.atMeXmppMessageTypePPT)
				msg.setBody("Sent you a ppt");
			else if(mediaType == XMPPMessageType.atMeXmppMessageTypeAudio){
				try{
					if(fileName != null)//Here caption is used for actual local audio file path to get duration
						audio_length = MediaPlayer.create(context, Uri.fromFile(new File(fileName))).getDuration()/1000;
				}catch(Exception ex){
					ex.printStackTrace();
				}
				byte minutes = (byte) (audio_length / 60);
	    		byte seconds = (byte) (audio_length % 60);
	    		String len_msg = minutes + ":" + ((seconds < 10) ? ("0"+seconds) : seconds);
				if(audio_length > 0)
					msg.setBody("Sent you a voice note : "+len_msg);
				else
					msg.setBody("Sent you a voice note");
					
				}
			if(url!=null){
				MediaBody mediaBody = msg.new MediaBody();
				mediaBody.setType(String.valueOf(mediaType.ordinal()));
//				mediaBody.setUrl(url.substring(url.indexOf(Constants.PIC_SEP) + Constants.PIC_SEP.length()));
				mediaBody.setUrl(url);
				if(thumb!=null)
					mediaBody.setThumb_data(thumb);
				//Set Audio Length
				if(mediaType == XMPPMessageType.atMeXmppMessageTypeAudio){
					if(audio_length > 0)
						mediaBody.setAudioLength(""+audio_length);
				}
				msg.setMediaBody(mediaBody);
			}
		}
		
		msg.setPacketID(packetID);
		Log.d(TAG, "sent packet: " + msg.toXML());
		if (connection != null && connection.isConnected()
				&& connection.isAuthenticated()) {
			connection.sendPacket(msg);
			chatDBWrapper.updateSeenStatus(userName,
					"(\"" + packetID + "\")", SeenState.sent);
			xmppConectionStatus = true;
		} else 
		{
			msg.setMessageSeenState(Message.SeenState.wait);
			chatDBWrapper.updateSeenStatus(userName,
					"(\"" + packetID + "\")", SeenState.wait);
		}
		if(connectionStatusListener!=null)
			connectionStatusListener.notifyConnectionChange();
	}

	public void clearNotifications(int id) {
		try {
			if (notificationManager != null)
				notificationManager.cancel(id);
		} catch (Exception e) {
		}
	}

	public XMPPConnection getconnection() {
		if (connection != null) {
			Log.d(TAG, "connection send");
			return connection;
		} else {
			Log.d(TAG, "connection null");
			return null;
		}
	}

	public class MyBinder extends Binder {
		public ChatService getService() {
			return ChatService.this;
		}
	}

	public void inviteUserInRoom(String roomName, String displayName, String groupDiscription, String userName, String caption) {
		if (connection != null && connection.isConnected()
				&& connection.isAuthenticated()) {
			try {
				String room = roomName + "@conference."
						+ Constants.CHAT_DOMAIN;

				// MultiUserChat muc = new MultiUserChat(connection,room);
				// muc.invite(userName+"@"+AtmeChatClient.getChatServer(),
				// "Invitation Message");
				//
				// Message message = new Message(userName+"@"+
				// AtmeChatClient.getChatServer());
				// message.setBody("Join me for a group chat!");
				// message.addExtension(new GroupChatInvitation(roomName + "@" +
				// AtmeChatClient.getChatServer()));
				// connection.sendPacket(message);

				Message message = new Message(room);// , Message.Type.chat);
				MUCUser mucUser = new MUCUser();
				MUCUser.Invite invite = new MUCUser.Invite();
				invite.setTo(userName + "@" + Constants.CHAT_DOMAIN);
				// atMEMulti
				if(displayName!=null && displayName.equals("Multi Chat Room"))
					invite.setReason("atMEMulti>>> "+groupDiscription);
				else
					invite.setReason(displayName + ">>> "+groupDiscription);
				mucUser.setInvite(invite);
				// Add the MUCUser packet that includes the invitation to the
				// message
				message.addExtension(mucUser);
				Log.i(TAG, "inviteUserInRoom request " + message.toXML());
				connection.sendPacket(message);
//				roster.createEntry(userName, prefManager.getUserServerName(userName), new String[]{roomName});
				prefManager.saveUsersOfGroup(roomName, userName);
				String userDisplayName = DBWrapper.getInstance().getChatName(userName);
				if(userDisplayName!=null && userDisplayName.contains("#786#"))
					userDisplayName = userDisplayName.substring(0, userDisplayName.indexOf("#786#"));
				if(userName!=null && !userName.equals("") && !userName.equals(userMe)){
				Log.d(TAG,"You group persons added "+ SharedPrefManager.getInstance().getGroupDisplayName(roomName));
				if(userName.equalsIgnoreCase(userDisplayName))
					userDisplayName = SharedPrefManager.getInstance().getUserServerName(userName);
				saveInfoMessage(SharedPrefManager.getInstance().getGroupDisplayName(roomName), roomName, "You added \""+userDisplayName+"\".");
				}
//				sendInfoMessage(roomName,userName,Message.XMPPMessageType.atMeXmppMessageTypeMemberList);//
				sendGroupInfoMessage(roomName, userName, caption, Message.XMPPMessageType.atMeXmppMessageTypeMemberList);//
//				sendMessage(roomName, userDisplayName + " has invited.");
			} catch (Exception e) {
				Log.d(TAG, " Exception in inviting userName " + userName);
			}
			xmppConectionStatus = true;
		} else {
//			xmppConectionStatus = false;
			Log.d(TAG, userMe + " are not inviting userName " + userName
					+ " due to connection not available.");
		}
		if(connectionStatusListener!=null)
			connectionStatusListener.notifyConnectionChange();
	}

	public void sendGroupPresence(String roomName, int historySeconds) {
		try {
			//For testing, to join directly.
//			joinMultiUserChat2(roomName);
			int currentTime = (int)((System.currentTimeMillis() - (prefManager.getLastOnline() - (5*1000)))/1000); //new1
			if(prefManager.getLastOnline() <= 0)
				currentTime = 0;
			if(historySeconds!=-1)
				currentTime = historySeconds;
			if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
				Presence joinPresence = new Presence(Presence.Type.available);
				String room = roomName;
				if (!room.contains("@conference."))
					room = roomName + "@conference." + Constants.CHAT_DOMAIN;
				joinPresence.setTo(room + "/" + userMe);
//				System.out.println("sendGroupPresence :: JOIN ROOM :- "+(room + "/" + userMe));
				// Indicate the the client supports MUC
				MUCInitialPresence initialPresence = new MUCInitialPresence();
				MUCInitialPresence.History history = new History();
				history.setSeconds(currentTime); //new1
				initialPresence.setHistory(history);
				joinPresence.addExtension(initialPresence);
				connection.sendPacket(joinPresence);
//				xmppConectionStatus = true;
			}else
				Log.i(TAG, "sendGroupPresence :: [NOT CONNECTED YET]");
//			else
//				xmppConectionStatus = false;
		} catch (Exception e) {
			Log.d(TAG, roomName + " presence availibility not sent.");
		}
		if(connectionStatusListener!=null)
			connectionStatusListener.notifyConnectionChange();
	}

	public void joinMultiUserChat(String roomName) {
		Log.d(TAG, userMe + " createMultiUserChat method calling.");
		if (connection != null && connection.isConnected()
				&& connection.isAuthenticated()) {
			Presence joinPresence = new Presence(Presence.Type.available);
			String room = roomName;
			if (!room.contains("@conference."))
				room = roomName + "@conference." + Constants.CHAT_DOMAIN;
			joinPresence.setTo(room + "/" + userMe);
			// Indicate the the client supports MUC
			joinPresence.addExtension(new MUCInitialPresence());
			Log.d(TAG, " joinPresence: " + joinPresence.toXML());
			connection.sendPacket(joinPresence);
//			sendInfoMessage(roomName, userMe + " has joined this group.", XMPPMessageType.atMeXmppMessageTypeJoinGroup );
			xmppConectionStatus = true;
		} else {
//			xmppConectionStatus = false;
			Log.d(TAG,
					userMe
							+ " joinMultiUserChat method created MultiUserChat instance is null.");
		}

		Log.d(TAG, userMe + " createMultiUserChat metho calling end.");
		if(connectionStatusListener!=null)
			connectionStatusListener.notifyConnectionChange();
	}

	 public void joinMultiUserChat2(String roomName) {
			 MultiUserChat mMultiUserChat = new MultiUserChat(connection, roomName + "@conference." + Constants.CHAT_SERVER_URL);
			 if (mMultiUserChat != null) {
			 try {
				 mMultiUserChat.join(userMe, prefManager.getUserPassword());
				 Log.i(TAG, userMe+ " joinMultiUserChat method created MultiUserChat has joined group.");
			 } catch (XMPPException e) {
				 e.printStackTrace();
			 } catch (Exception e) {
				 e.printStackTrace();
			 }
		 }
	 }

	// muc#roomconfig_roomname
	// muc#roomconfig_roomdesc
	// muc#roomconfig_changesubject
	// muc#roomconfig_maxusers
	// muc#roomconfig_presencebroadcast
	// muc#roomconfig_publicroom
	// muc#roomconfig_persistentroom
	// muc#roomconfig_moderatedroom
	// muc#roomconfig_membersonly
	// muc#roomconfig_allowinvites
	// muc#roomconfig_passwordprotectedroom
	// muc#roomconfig_roomsecret
	// muc#roomconfig_whois
	// muc#roomconfig_enablelogging
	// x-muc#roomconfig_reservednick
	// x-muc#roomconfig_canchangenick
	// x-muc#roomconfig_registration
	// muc#roomconfig_roomadmins
	// muc#roomconfig_roomowners

	// public void createMultiUserChat1(String roomName){
	// try{
	// MultiUserChat muc = new MultiUserChat(connection, roomName +
	// "@conference." + AtmeChatClient.getChatServer());
	// muc.create(userMe);
	//
	// Form form = muc.getConfigurationForm();
	// Form submitForm = form.createAnswerForm();
	// for (Iterator fields = form.getFields();fields.hasNext();){
	// FormField field = (FormField) fields.next();
	// if(!FormField.TYPE_HIDDEN.equals(field.getType()) &&
	// field.getVariable()!= null){
	// Log.d(TAG, userMe + " field.getVariable() "+field.getVariable());
	// submitForm.setDefaultAnswer(field.getVariable());
	// }
	// }
	// submitForm.setAnswer("muc#roomconfig_publicroom", true);
	// muc.sendConfigurationForm(submitForm);
	// muc.join("userMe");
	// }catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	public String createMultiUserChat(String groupDisplayName,
			ArrayList<String> inviters,String groupDiscription) {
		Log.d(TAG, userMe + " createMultiUserChat method calling.");
		String groupName = UUID.randomUUID().toString();
		if (connection != null && connection.isConnected()
				&& connection.isAuthenticated()) {
			Log.d(TAG, userMe
					+ " createMultiUserChat method connection available.");
			MultiUserChat mMultiUserChat = new MultiUserChat(connection,
					groupName + "@conference." + Constants.CHAT_DOMAIN);
			Log.d(TAG, userMe
							+ " createMultiUserChat method are creating MultiUserChat instance: "
							+ mMultiUserChat);
			if (mMultiUserChat != null) {
//				if(roster!=null){
//					roster.createGroup(groupName);
//					
//				}
				try {
					mMultiUserChat.create(userMe);
					Log.d(TAG,
							userMe
									+ " createMultiUserChat method created MultiUserChat group with nick name.");
					Form form = mMultiUserChat.getConfigurationForm();
					Form submitForm = form.createAnswerForm();
					for (Iterator fields = form.getFields(); fields.hasNext();) {
						FormField field = (FormField) fields.next();
						if (!FormField.TYPE_HIDDEN.equals(field.getType())
								&& field.getVariable() != null) {
							submitForm.setDefaultAnswer(field.getVariable());
							Log.d(TAG,
									"default fields "
											+ field.getVariable()
											+ "::"
											+ submitForm.getField(
													field.getVariable())
													.toXML());
						}
					}
					submitForm.setAnswer("muc#roomconfig_roomname", groupName);
					List<String> owners = new ArrayList<String>();
					owners.add(userMe + "@" + Constants.CHAT_DOMAIN);
					submitForm.setAnswer("muc#roomconfig_roomowners", owners);
					submitForm.setAnswer("muc#roomconfig_persistentroom", true);

					mMultiUserChat.sendConfigurationForm(submitForm);
					Log.d(TAG,
							userMe
									+ " createMultiUserChat method sendConfigurationForm called.");
					mMultiUserChat.join(userMe);
					Log.d(TAG,
							userMe
									+ " createMultiUserChat method created MultiUserChat has joined group.");
					prefManager.saveGroupName(groupName,
							groupDisplayName);
					// prefManager.saveUsersOfGroup(groupName,userDisplayName);
//					inviters.add(userMe);
					try {
						String infoList = "";
						for (String inviter : inviters) {
							if (inviter != null && !inviter.equals("") && !inviter.equals(userMe))
								inviteUserInRoom(groupName, groupDisplayName, groupDiscription, inviter, null);
							infoList +=  inviter+",";
//							roster.createEntry(inviter, prefManager.getUserServerName(inviter), new String[]{groupName});
//							roster.getEntry(inviter).setName(prefManager.getUserServerName(inviter));
						}
//						if(infoList.endsWith(","))
//							infoList+=userMe;
//						else
//							infoList = userMe;
						prefManager.saveUserGroupInfo(groupName,userMe,SharedPrefManager.GROUP_ACTIVE_INFO,true);
						prefManager.saveGroupInfo(groupName,SharedPrefManager.GROUP_ACTIVE_INFO,true);
						if(!infoList.equals("")){
							if(infoList.endsWith(","))
								infoList = infoList.substring(0, infoList.length()-1);
							sendInfoMessage(groupName,infoList,Message.XMPPMessageType.atMeXmppMessageTypeMemberList);
						}
					} catch (Exception e) {
						Log.d(TAG, "users are not able to joined.");
					}

//					sendMessage(groupName, "You are welcome in " + groupDisplayName
//							+ " group.");

				} catch (XMPPException e) {
					e.printStackTrace();
					Log.d(TAG, userMe + " are not creating roomname "
							+ groupDisplayName + " " + e.toString());
					return "";
				} catch (Exception e) {
					e.printStackTrace();
					Log.d(TAG, userMe + " are not creating roomname "
							+ groupDisplayName + " " + e.toString());
					return "";
				}

			} else {
				Log.d(TAG,
						userMe
								+ " createMultiUserChat method created MultiUserChat instance is null.");
				return "";
			}
			xmppConectionStatus = true;
		} else {
//			xmppConectionStatus = false;
			Log.d(TAG, userMe + " are not creating roomname " + groupDisplayName
					+ " due to connection not available.");
			return "";
		}
		Log.d(TAG, userMe + " createMultiUserChat metho calling end.");
		if(connectionStatusListener!=null)
			connectionStatusListener.notifyConnectionChange();
		return groupName;
	}

//	public void sendGroupMessage(String room, String message) {
//		if (connection != null && connection.isConnected()
//				&& connection.isAuthenticated()) {
//			String to = room;
//			if (!to.contains("@conference."))
//				to = room + "@conference." + AtmeChatClient.getChatServer();
//			Message msg = new Message(to, Message.Type.groupchat);
//			if(message.startsWith(AppConstants.PIC_SEP))//This is to send picture/audio/video URL in Subject as decided b/w ios and android
//			{
//				msg.setSubject(message.substring(message.indexOf(AppConstants.PIC_SEP) + AppConstants.PIC_SEP.length()));
//				msg.setXMPPMessageType(XMPPMessageType.atMeXmppMessageTypeImage);
//				msg.setBody("sent you an image");
//			}
//			else
//				msg.setBody(message);
//			connection.sendPacket(msg);
//		} else {
//			Log.d(TAG, message + " are not sending group message in the room "
//					+ room);
//		}
//	}
	public void sendListeningStatus(String userName) {
		if (connection != null && connection.isConnected()
				&& connection.isAuthenticated()) {
			if(!xmppConectionStatus)
				return;
			boolean isGroupChat = false;
			if (SharedPrefManager.getInstance().isGroupChat(userName)) {
				isGroupChat = true;
			}
			// MessageEventManager event=new MessageEventManager(connection);
			String to = userName + "@" + Constants.CHAT_DOMAIN;
			if (isGroupChat) {
				to = userName + "@conference." + Constants.CHAT_DOMAIN;
			}
			// //
			// event.addMessageEventNotificationListener(messageEventNotificationListener);
			// event.sendComposingNotification(to, "message_id is");
			Message msg = new Message(to);
			// Create a MessageEvent Package and add it to the message
			MessageEvent messageEvent = new MessageEvent();
			messageEvent.setListening(true);
			messageEvent.setPacketID(null);
			if (isGroupChat)
				msg.setType(Message.Type.groupchat);
			else
				msg.setType(Message.Type.chat);
			msg.addExtension(messageEvent);
			// Send the packet
			connection.sendPacket(msg);
			xmppConectionStatus = true;
		}else
//			xmppConectionStatus = false;
		if(connectionStatusListener!=null)
			connectionStatusListener.notifyConnectionChange();
	}
	public void sendRecordingStatus(String userName) {
		if (connection != null && connection.isConnected()
				&& connection.isAuthenticated()) {
			if(!xmppConectionStatus)
				return;
			boolean isGroupChat = false;
			if (SharedPrefManager.getInstance().isGroupChat(userName)) {
				isGroupChat = true;
			}
			// MessageEventManager event=new MessageEventManager(connection);
			String to = userName + "@" + Constants.CHAT_DOMAIN;
			if (isGroupChat) {
				to = userName + "@conference." + Constants.CHAT_DOMAIN;
			}
			// //
			// event.addMessageEventNotificationListener(messageEventNotificationListener);
			// event.sendComposingNotification(to, "message_id is");
			Message msg = new Message(to);
			// Create a MessageEvent Package and add it to the message
			MessageEvent messageEvent = new MessageEvent();
			messageEvent.setRecording(true);
			messageEvent.setPacketID(null);
			if (isGroupChat)
				msg.setType(Message.Type.groupchat);
			else
				msg.setType(Message.Type.chat);
			msg.addExtension(messageEvent);
			// Send the packet
			connection.sendPacket(msg);
			xmppConectionStatus = true;
		}
//		else
//			xmppConectionStatus = false;
		if(connectionStatusListener!=null)
			connectionStatusListener.notifyConnectionChange();
	}
	public void sendTypingStatus(String userName) {
		if (connection != null && connection.isConnected()
				&& connection.isAuthenticated()) {
			if(!xmppConectionStatus)
				return;
			boolean isGroupChat = false;
			if (SharedPrefManager.getInstance().isGroupChat(userName)) {
				isGroupChat = true;
			}
			// MessageEventManager event=new MessageEventManager(connection);
			String to = userName + "@" + Constants.CHAT_DOMAIN;
			if (isGroupChat) {
				to = userName + "@conference." + Constants.CHAT_DOMAIN;
			}
			// //
			// event.addMessageEventNotificationListener(messageEventNotificationListener);
			// event.sendComposingNotification(to, "message_id is");
			Message msg = new Message(to);
			// Create a MessageEvent Package and add it to the message
			MessageEvent messageEvent = new MessageEvent();
			messageEvent.setComposing(true);
			messageEvent.setPacketID(null);
			if (isGroupChat)
				msg.setType(Message.Type.groupchat);
			else
				msg.setType(Message.Type.chat);
			msg.addExtension(messageEvent);
			// Send the packet
			connection.sendPacket(msg);
			Log.i("ChatService", "sendTypingStatus : "+msg.toXML());
			xmppConectionStatus = true;
		}
//		else
//			xmppConectionStatus = false;
		if(connectionStatusListener!=null)
			connectionStatusListener.notifyConnectionChange();
	}
	public void saveInfoMessage(String displayName, String from, String msg, String msgId) {
		if(prefManager.isGroupChat(from) && !prefManager.isGroupMemberActive(from, userMe)){
			return;
		}
		try {
//			ChatDBWrapper chatDBWrapper = chatDBWrapper;
			ContentValues contentvalues = new ContentValues();
			String myName = SharedPrefManager.getInstance().getUserName();
			contentvalues.put(ChatDBConstants.FROM_USER_FIELD, from);
			contentvalues.put(ChatDBConstants.TO_USER_FIELD, myName);
			contentvalues.put(ChatDBConstants.UNREAD_COUNT_FIELD,
					new Integer(1));
			contentvalues.put(ChatDBConstants.FROM_GROUP_USER_FIELD, "");
			contentvalues.put(ChatDBConstants.SEEN_FIELD,
					SeenState.sent.ordinal());
//			 if(msg!=null && msg.contains("#786#")){
//				 msg = msg.replace("#786#"+from,"");
//				 msg = msg.replace("#786#"+myName,"");
//				}
			contentvalues.put(ChatDBConstants.MESSAGEINFO_FIELD, msg);
			// String name =
			// cursor.getString(cursor.getColumnIndex(ChatDBConstants.CONTACT_NAMES_FIELD));

			String name = "";
			String oppName = "";
			{
				oppName = from;
				name = DBWrapper.getInstance().getChatName(from);
				if(name!=null && name.equals(from))
					name = displayName+"#786#"+from;
				contentvalues.put(ChatDBConstants.MESSAGE_ID, UUID
						.randomUUID().toString());
				if(msgId == null)
					msgId = UUID.randomUUID().toString();
				contentvalues.put(ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD,msgId);
//						UUID.randomUUID().toString());
			}

			long currentTime = System.currentTimeMillis();
			Calendar calender = Calendar.getInstance();
			calender.setTimeInMillis(currentTime);
			int date = calender.get(Calendar.DATE);
			int oldDate = date;
			long milis = chatDBWrapper.lastMessageInDB(oppName);
			if(milis!=-1){
				calender.setTimeInMillis(milis);
				oldDate = calender.get(Calendar.DATE);
			}
			if ((oldDate != date)
					|| chatDBWrapper.isFirstChat(oppName)) {
				contentvalues.put(ChatDBConstants.IS_DATE_CHANGED_FIELD, "1");
			} else {
				contentvalues.put(ChatDBConstants.IS_DATE_CHANGED_FIELD, "0");
			}
//			AtMeApplication.dayValue = date;
			contentvalues.put(ChatDBConstants.LAST_UPDATE_FIELD, currentTime);

			contentvalues.put(ChatDBConstants.CONTACT_NAMES_FIELD, name);
			chatDBWrapper.insertInDB(ChatDBConstants.TABLE_NAME_MESSAGE_INFO, contentvalues);
//			if (chatListener != null)
//				chatListener.notifyChatRecieve(from,msg);
		} catch (Exception e) {

		}
	}
	public void saveInfoMessage(String displayName, String from, String msg) {
		if(prefManager.isGroupChat(from) && !prefManager.isGroupMemberActive(from, userMe)){
			return;
		}
		try {
//			ChatDBWrapper chatDBWrapper = chatDBWrapper;
			ContentValues contentvalues = new ContentValues();
			String myName = SharedPrefManager.getInstance().getUserName();
			contentvalues.put(ChatDBConstants.FROM_USER_FIELD, from);
			contentvalues.put(ChatDBConstants.TO_USER_FIELD, myName);
			contentvalues.put(ChatDBConstants.UNREAD_COUNT_FIELD,
					new Integer(1));
			contentvalues.put(ChatDBConstants.FROM_GROUP_USER_FIELD, "");
			contentvalues.put(ChatDBConstants.SEEN_FIELD,
					SeenState.sent.ordinal());
//			 if(msg!=null && msg.contains("#786#")){
//				 msg = msg.replace("#786#"+from,"");
//				 msg = msg.replace("#786#"+myName,"");
//				}
			contentvalues.put(ChatDBConstants.MESSAGEINFO_FIELD, msg);
			// String name =
			// cursor.getString(cursor.getColumnIndex(ChatDBConstants.CONTACT_NAMES_FIELD));

			String name = "";
			String oppName = "";
			{
				oppName = from;
				name = DBWrapper.getInstance().getChatName(from);
				contentvalues.put(ChatDBConstants.MESSAGE_ID, UUID
						.randomUUID().toString());
				contentvalues.put(ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD,
						UUID.randomUUID().toString());
			}

			long currentTime = System.currentTimeMillis();
			Calendar calender = Calendar.getInstance();
			calender.setTimeInMillis(currentTime);
			int date = calender.get(Calendar.DATE);
			int oldDate = date;
			long milis = chatDBWrapper.lastMessageInDB(oppName);
			if(milis!=-1){
				calender.setTimeInMillis(milis);
				oldDate = calender.get(Calendar.DATE);
			}
			if ((oldDate != date)
					|| chatDBWrapper.isFirstChat(oppName)) {
				contentvalues.put(ChatDBConstants.IS_DATE_CHANGED_FIELD, "1");
			} else {
				contentvalues.put(ChatDBConstants.IS_DATE_CHANGED_FIELD, "0");
			}
//			AtMeApplication.dayValue = date;
			contentvalues.put(ChatDBConstants.LAST_UPDATE_FIELD, currentTime);

			contentvalues.put(ChatDBConstants.CONTACT_NAMES_FIELD, name);
			chatDBWrapper.insertInDB(ChatDBConstants.TABLE_NAME_MESSAGE_INFO,
					contentvalues);
			if (chatListener != null)
				chatListener.notifyChatRecieve(from,msg);
		} catch (Exception e) {

		}
	}
//	public void sendXMPPFile(String filenameWithPath)
//	{
//		FileTransferManager manager = new FileTransferManager(connection);
//		OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer("mahesh@78.129.179.96/Smack");
//		File file = new File(filenameWithPath);
//		try {
//		   transfer.sendFile(file, "test_file");
//		} catch (XMPPException e) {
//		   e.printStackTrace();
//		}
//		while(!transfer.isDone()) {
//		   if(transfer.getStatus().equals(Status.error)) {
//		      System.out.println("ERROR!!! " + transfer.getError());
//		   } else if (transfer.getStatus().equals(Status.cancelled)
//		                    || transfer.getStatus().equals(Status.refused)) {
//		      System.out.println("Cancelled!!! " + transfer.getError());
//		   }
//		   try {
//		      Thread.sleep(1000L);
//		   } catch (InterruptedException e) {
//		      e.printStackTrace();
//		   }
//		}
//		if(transfer.getStatus().equals(Status.refused) || transfer.getStatus().equals(Status.error)
//		 || transfer.getStatus().equals(Status.cancelled)){
//		   System.out.println("refused cancelled error " + transfer.getError());
//		} else {
//		   System.out.println("Success");
//		}
//
//	}
//	public void receiveXMPPFile(String filenameWithPath)
//	{
//		FileTransferManager manager = new FileTransferManager(connection);
//		manager.addFileTransferListener(new FileTransferListener() {
//		   public void fileTransferRequest(final FileTransferRequest request) {
//		      new Thread(){
//		         @Override
//		         public void run() {
//		            IncomingFileTransfer transfer = request.accept();
//		            File mf = Environment.getExternalStorageDirectory();
//		            File file = new File(mf.getAbsoluteFile()+"/DCIM/Camera/" + transfer.getFileName());
//		            try{
//		                transfer.recieveFile(file);
//		                while(!transfer.isDone()) {
//		                   try{
//		                      Thread.sleep(1000L);
//		                   }catch (Exception e) {
//		                      Log.e("", e.getMessage());
//		                   }
//		                   if(transfer.getStatus().equals(Status.error)) {
//		                      Log.e("ERROR!!! ", transfer.getError() + "");
//		                   }
//		                   if(transfer.getException() != null) {
//		                      transfer.getException().printStackTrace();
//		                   }
//		                }
//		             }catch (Exception e) {
//		                Log.e("", e.getMessage());
//		            }
//		         };
//		       }.start();
//		    }
//		 });
//	}
	public void saveMessage(String displayName, String from, String msg) {
		try {
			ChatDBWrapper chatDBWrapper = ChatDBWrapper.getInstance();
			ContentValues contentvalues = new ContentValues();
			String myName = SharedPrefManager.getInstance().getUserName();
			contentvalues.put(DatabaseConstants.FROM_USER_FIELD, from);
			contentvalues.put(DatabaseConstants.TO_USER_FIELD, myName);
			contentvalues.put(DatabaseConstants.UNREAD_COUNT_FIELD, new Integer(1));
			contentvalues.put(DatabaseConstants.FROM_GROUP_USER_FIELD, "");
			contentvalues.put(DatabaseConstants.SEEN_FIELD, com.chatsdk.org.jivesoftware.smack.packet.Message.SeenState.sent.ordinal());

			contentvalues.put(DatabaseConstants.MESSAGEINFO_FIELD, msg);
			// String name =
			// cursor.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_NAMES_FIELD));

			String name = "";
			String oppName = "";
			{
				oppName = from;
				name = chatDBWrapper.getChatName(from);
				if(name!=null && name.equals(from))
						name = displayName+"#786#"+from;
				contentvalues.put(DatabaseConstants.MESSAGE_ID, UUID
						.randomUUID().toString());
				contentvalues.put(DatabaseConstants.FOREIGN_MESSAGE_ID_FIELD,
						UUID.randomUUID().toString());
			}

			long currentTime = System.currentTimeMillis();
			Calendar calender = Calendar.getInstance();
			calender.setTimeInMillis(currentTime);
			int date = calender.get(Calendar.DATE);
			int oldDate = date;
			long milis = ChatDBWrapper.getInstance().lastMessageInDB(oppName);
			if(milis!=-1){
				calender.setTimeInMillis(milis);
				oldDate = calender.get(Calendar.DATE);
			}
			if ((oldDate != date)
					|| ChatDBWrapper.getInstance().isFirstChat(oppName)) {
				contentvalues.put(DatabaseConstants.IS_DATE_CHANGED_FIELD, "1");
			} else {
				contentvalues.put(DatabaseConstants.IS_DATE_CHANGED_FIELD, "0");
			}
//			AtMeApplication.dayValue = date;
			contentvalues.put(DatabaseConstants.LAST_UPDATE_FIELD, currentTime);

			contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, name);
			chatDBWrapper.insertInDB(DatabaseConstants.TABLE_NAME_MESSAGE_INFO,contentvalues);
		} catch (Exception e) {

		}
	}
	//=====================================================

    public boolean isServiceRunning(String serviceClassName){
        final ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (RunningServiceInfo runningServiceInfo : services) {
//        	System.out.println("ClassName : "+runningServiceInfo.service.getClassName());
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }
        return false;
     }
}