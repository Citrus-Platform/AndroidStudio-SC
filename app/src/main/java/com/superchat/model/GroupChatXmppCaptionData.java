package com.superchat.model;

public class GroupChatXmppCaptionData {
    private String groupPermissionType;
    private String displayName;
    private String description;
    private String fileId;

    public String getGroupPermissionType() {
        return groupPermissionType;
    }

    public void setGroupPermissionType(String groupPermissionType) {
        this.groupPermissionType = groupPermissionType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}