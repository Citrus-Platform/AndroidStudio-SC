/*
 * Created on 2009-05-12
 */
package com.chatsdk.org.jivesoftware.smackx.pubsub;

abstract public class NodeEvent
{
	private String nodeId;
	
	protected NodeEvent(String id)
	{
		nodeId = id;
	}
	
	public String getNodeId()
	{
		return nodeId;
	}
}
