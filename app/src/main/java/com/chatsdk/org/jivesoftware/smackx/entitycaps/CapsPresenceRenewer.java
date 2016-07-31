package com.chatsdk.org.jivesoftware.smackx.entitycaps;

import com.chatsdk.org.jivesoftware.smack.XMPPConnection;
import com.chatsdk.org.jivesoftware.smack.packet.Presence;

public class CapsPresenceRenewer implements CapsVerListener {
    private XMPPConnection connection;
    private EntityCapsManager capsManager;

    public CapsPresenceRenewer(XMPPConnection connection, EntityCapsManager capsManager) {
        this.connection = connection;
        this.capsManager = capsManager;
    }

    public void capsVerUpdated(String ver) {
        // Send an empty presence, and let the packet interceptor
        // add a <c/> node to it.
        if (connection.isAuthenticated() &&
                ( connection.isSendPresence() ||
                           capsManager.isSendPresence())) {
            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);
        }
    }
}
