package com.superchat.model;

import java.util.ArrayList;

/**
 * Created by citrus on 9/19/2016.
 */
public class SlidingMenuData {

    private ArrayList<SlidingMenuObject> slidingMenuObjects;

    private ArrayList<SGroupListObject> ownerDomainNameSet;
    private ArrayList<SGroupListObject> joinedDomainNameSet;
    private ArrayList<SGroupListObject> invitedDomainNameSet;

    public ArrayList<SlidingMenuObject> getSlidingMenuObjects() {
        return slidingMenuObjects;
    }

    public void setSlidingMenuObjects(ArrayList<SlidingMenuObject> slidingMenuObjects) {
        this.slidingMenuObjects = slidingMenuObjects;
    }

    public ArrayList<SGroupListObject> getOwnerDomainNameSet() {
        return ownerDomainNameSet;
    }

    public ArrayList<SGroupListObject> getJoinedDomainNameSet() {
        return joinedDomainNameSet;
    }

    public ArrayList<SGroupListObject> getInvitedDomainNameSet() {
        return invitedDomainNameSet;
    }

    public void setOwnerDomainNameSet(ArrayList<SGroupListObject> ownerDomainNameSet) {
        this.ownerDomainNameSet = ownerDomainNameSet;
    }

    public void setJoinedDomainNameSet(ArrayList<SGroupListObject> joinedDomainNameSet) {
        this.joinedDomainNameSet = joinedDomainNameSet;
    }

    public void setInvitedDomainNameSet(ArrayList<SGroupListObject> invitedDomainNameSet) {
        this.invitedDomainNameSet = invitedDomainNameSet;
    }
}
