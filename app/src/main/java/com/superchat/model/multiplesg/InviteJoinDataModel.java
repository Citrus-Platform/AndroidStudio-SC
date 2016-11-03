package com.superchat.model.multiplesg;

/**
 * Created by Mahesh on 03/11/16.
 */

public class InviteJoinDataModel {

    public InviteJoinDataModel() {
    }

    public String getInviteMobileNumber() {
        return inviteMobileNumber;
    }

    public void setInviteMobileNumber(String inviteMobileNumber) {
        this.inviteMobileNumber = inviteMobileNumber;
    }

    String inviteMobileNumber;

    public long getInviteUserID() {
        return inviteUserID;
    }

    public void setInviteUserID(long inviteUserID) {
        this.inviteUserID = inviteUserID;
    }

    public String getInviteSGName() {
        return inviteSGName;
    }

    public void setInviteSGName(String inviteSGName) {
        this.inviteSGName = inviteSGName;
    }

    public String getInviteSGDisplayName() {
        return inviteSGDisplayName;
    }

    public void setInviteSGDisplayName(String inviteSGDisplayName) {
        this.inviteSGDisplayName = inviteSGDisplayName;
    }

    public String getInviteSGFileID() {
        return inviteSGFileID;
    }

    public void setInviteSGFileID(String inviteSGFileID) {
        this.inviteSGFileID = inviteSGFileID;
    }

    public String getInviteUserName() {
        return inviteUserName;
    }

    public void setInviteUserName(String inviteUserName) {
        this.inviteUserName = inviteUserName;
    }

    public String getInviteUserPassword() {
        return inviteUserPassword;
    }

    public void setInviteUserPassword(String inviteUserPassword) {
        this.inviteUserPassword = inviteUserPassword;
    }

    String inviteSGName;
    String inviteSGDisplayName;
    String inviteSGFileID;
    String inviteUserName;
    String inviteUserPassword;
    long inviteUserID;
}
