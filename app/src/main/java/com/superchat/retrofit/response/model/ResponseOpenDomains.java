package com.superchat.retrofit.response.model;

import com.superchat.model.SGroupListObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by MotoBeans on 7/26/2016.
 */
public class ResponseOpenDomains extends CommonResponse {
    private String domainSearchText;
    private ArrayList<SGroupListObject> openDomainList;

    public String getDomainSearchText() {
        return domainSearchText;
    }

    public void setDomainSearchText(String domainSearchText) {
        this.domainSearchText = domainSearchText;
    }

    public ArrayList<SGroupListObject> getOpenDomainList() {
        return openDomainList;
    }

    public void setOpenDomainList(ArrayList<SGroupListObject> openDomainList) {
        this.openDomainList = openDomainList;
    }
}
