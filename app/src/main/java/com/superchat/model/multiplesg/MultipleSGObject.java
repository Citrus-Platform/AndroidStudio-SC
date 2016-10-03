package com.superchat.model.multiplesg;

import java.util.ArrayList;

/**
 * Created by maheshsonker on 03/10/16.
 */

public class MultipleSGObject
{
    private OwnerDomainName ownerDomainName;

    public OwnerDomainName getOwnerDomainName() { return this.ownerDomainName; }

    public void setOwnerDomainName(OwnerDomainName ownerDomainName) { this.ownerDomainName = ownerDomainName; }

    private ArrayList<InvitedDomainNameSet> invitedDomainNameSet;

    public ArrayList<InvitedDomainNameSet> getInvitedDomainNameSet() { return this.invitedDomainNameSet; }

    public void setInvitedDomainNameSet(ArrayList<InvitedDomainNameSet> invitedDomainNameSet) { this.invitedDomainNameSet = invitedDomainNameSet; }

    private ArrayList<JoinedDomainNameSet> joinedDomainNameSet;

    public ArrayList<JoinedDomainNameSet> getJoinedDomainNameSet() { return this.joinedDomainNameSet; }

    public void setJoinedDomainNameSet(ArrayList<JoinedDomainNameSet> joinedDomainNameSet) { this.joinedDomainNameSet = joinedDomainNameSet; }

    private String status;

    public String getStatus() { return this.status; }

    public void setStatus(String status) { this.status = status; }

    private String message;

    public String getMessage() { return this.message; }

    public void setMessage(String message) { this.message = message; }
}
