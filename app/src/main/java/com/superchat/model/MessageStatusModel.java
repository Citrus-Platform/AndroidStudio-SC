package com.superchat.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by maheshsonker on 22/08/16.
 */
public class MessageStatusModel {
    public MessageStatusModel() {
        super();
    }

    @SerializedName(value = "foruserName")
    public String foruserName;

    @SerializedName(value = "messageID")
    public String messageID;

    @SerializedName(value = "messageDeliveryTime")
    public String messageDeliveryTime;

    @SerializedName(value = "currentStatus")
    public int currentStatus;

    @SerializedName(value = "messageDeliveryStatus")
    public int messageDeliveryStatus;
}
