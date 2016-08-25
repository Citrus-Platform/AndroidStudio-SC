package com.superchat.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by maheshsonker on 22/08/16.
 */
public class MessageStatusModel {
    public MessageStatusModel() {
        super();
    }

    @SerializedName(value = "from_user")
    public String from_user;

    @SerializedName(value = "message_id")
    public String message_id;

    @SerializedName(value = "deliver_time")
    public String deliver_time;

    @SerializedName(value = "seen_time")
    public String seen_time;

    @SerializedName(value = "currentStatus")
    public int currentStatus;

    @SerializedName(value = "seen")
    public int seen;
}
