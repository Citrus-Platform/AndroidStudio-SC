package com.superchat.model;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by maheshsonker on 07/09/16.
 */
public class BulletinMessageDataModel {


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<MessageData> getBulletinMessageAPIList() {
        return bulletinMessageAPIList;
    }

    public void setBulletinMessageAPIList(Set<MessageData> bulletinMessageAPIList) {
        this.bulletinMessageAPIList = bulletinMessageAPIList;
    }

    public Set<MessageData> bulletinMessageAPIList;
    public String status;
    public String message;
    public String nextUrl;

    public String userId;
//    public String bulletinMessageId;
    public String packetId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

//    public String getBulletinMessageId() {
//        return bulletinMessageId;
//    }
//
//    public void setBulletinMessageId(String bulletinMessageId) {
//        this.bulletinMessageId = bulletinMessageId;
//    }

    public String getPacketId() {
        return packetId;
    }

    public void setPacketId(String packetId) {
        this.packetId = packetId;
    }

    public String getNextUrl() {
        return nextUrl;
    }

    public void setNextUrl(String nextUrl) {
        this.nextUrl = nextUrl;
    }



    public static class MessageData{
        private long bulletinMessageId;
        private String bulletinName;
        private String domainName;
        private String sender;
        private String senderName;
        private String senderProfileUrl;
        private String packetId;
        private String text;
        private String fileId;

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        private String filename;
        private Map<String, String> jsonBody;
        private String type;
        private Long likes;
        private Long comments;
        private Date createdDate;

        public long getBulletinMessageId() {
            return bulletinMessageId;
        }

        public void setBulletinMessageId(long bulletinMessageId) {
            this.bulletinMessageId = bulletinMessageId;
        }

        public String getBulletinName() {
            return bulletinName;
        }

        public void setBulletinName(String bulletinName) {
            this.bulletinName = bulletinName;
        }

        public String getDomainName() {
            return domainName;
        }

        public void setDomainName(String domainName) {
            this.domainName = domainName;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        public String getSenderProfileUrl() {
            return senderProfileUrl;
        }

        public void setSenderProfileUrl(String senderProfileUrl) {
            this.senderProfileUrl = senderProfileUrl;
        }

        public String getPacketId() {
            return packetId;
        }

        public void setPacketId(String packetId) {
            this.packetId = packetId;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

        public Map<String, String> getJsonBody() {
            return jsonBody;
        }

        public void setJsonBody(Map<String, String> jsonBody) {
            this.jsonBody = jsonBody;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Long getLikes() {
            return likes;
        }

        public void setLikes(Long likes) {
            this.likes = likes;
        }

        public Long getComments() {
            return comments;
        }

        public void setComments(Long comments) {
            this.comments = comments;
        }

        public Date getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(Date createdDate) {
            this.createdDate = createdDate;
        }
    }
}
