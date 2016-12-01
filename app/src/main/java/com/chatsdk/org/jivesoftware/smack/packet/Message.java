/**
 * $RCSfile$
 * $Revision$
 * $Date$
 *
 * Copyright 2003-2007 Jive Software.
 *
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chatsdk.org.jivesoftware.smack.packet;

import com.chatsdk.org.jivesoftware.smack.util.StringUtils;

import java.util.*;

/**
 * Represents XMPP message packets. A message can be one of several types:
 * 
 * <ul>
 * <li>Message.Type.NORMAL -- (Default) a normal text message used in email like
 * interface.
 * <li>Message.Type.CHAT -- a typically short text message used in line-by-line
 * chat interfaces.
 * <li>Message.Type.GROUP_CHAT -- a chat message sent to a groupchat server for
 * group chats.
 * <li>Message.Type.HEADLINE -- a text message to be displayed in scrolling
 * marquee displays.
 * <li>Message.Type.ERROR -- indicates a messaging error.
 * </ul>
 * 
 * For each message type, different message fields are typically used as
 * follows:
 * <p>
 * <table border="1">
 * <tr>
 * <td>&nbsp;</td>
 * <td colspan="5"><b>Message type</b></td>
 * </tr>
 * <tr>
 * <td><i>Field</i></td>
 * <td><b>Normal</b></td>
 * <td><b>Chat</b></td>
 * <td><b>Group Chat</b></td>
 * <td><b>Headline</b></td>
 * <td><b>XMPPError</b></td>
 * </tr>
 * <tr>
 * <td><i>subject</i></td>
 * <td>SHOULD</td>
 * <td>SHOULD NOT</td>
 * <td>SHOULD NOT</td>
 * <td>SHOULD NOT</td>
 * <td>SHOULD NOT</td>
 * </tr>
 * <tr>
 * <td><i>thread</i></td>
 * <td>OPTIONAL</td>
 * <td>SHOULD</td>
 * <td>OPTIONAL</td>
 * <td>OPTIONAL</td>
 * <td>SHOULD NOT</td>
 * </tr>
 * <tr>
 * <td><i>body</i></td>
 * <td>SHOULD</td>
 * <td>SHOULD</td>
 * <td>SHOULD</td>
 * <td>SHOULD</td>
 * <td>SHOULD NOT</td>
 * </tr>
 * <tr>
 * <td><i>error</i></td>
 * <td>MUST NOT</td>
 * <td>MUST NOT</td>
 * <td>MUST NOT</td>
 * <td>MUST NOT</td>
 * <td>MUST</td>
 * </tr>
 * </table>
 * 
 * @author Matt Tucker
 */
public class Message extends Packet {

	private Type type = Type.normal;
	private String thread = null;
	private String language;
	private String mediaTagMessage;
	private String mediaFileName;
	public String getMediaFileName() {
		return mediaFileName;
	}

	public void setMediaFileName(String mediaFileName) {
		this.mediaFileName = mediaFileName;
	}

	private String locationMessage;
	private String jsonBody;

	private XMPPMessageType xMPPMessageType = XMPPMessageType.atMeXmppMessageTypeNormal;
	private StatusMessageType statusMessageType = StatusMessageType.normal;
	private SeenState seenState = SeenState.none;
	private final Set<Subject> subjects = new HashSet<Subject>();
	private final Set<Body> bodies = new HashSet<Body>();
	private MediaBody mediaBody;

	public MessageDelay getMessageDelay() {
		return messageDelay;
	}

	public void setMessageDelay(MessageDelay messageDelay) {
		this.messageDelay = messageDelay;
	}

	private MessageDelay messageDelay;
	private String displayName;
	private String groupOwnerName;
	private String groupDisplayname;
	private String picId;
	private String groupId;
	private String groupPicId;
	private String memberCount;

	private String isConsoleMessage;
	private String custDest;
	private String packetId;
	/**
	 * Creates a new, "normal" message.
	 */
	public Message() {
	}

	/**
	 * Creates a new "normal" message to the specified recipient.
	 * 
	 * @param to
	 *            the recipient of the message.
	 */
	public Message(String to) {
		setTo(to);
	}

	/**
	 * Creates a new message of the specified type to a recipient.
	 * 
	 * @param to
	 *            the user to send the message to.
	 * @param type
	 *            the message type.
	 */
	public Message(String to, Type type) {
		setTo(to);
		this.type = type;
	}

	/**
	 * Returns the type of the message. If no type has been set this method will
	 * return {@link sdk.org.jivesoftware.smack.packet.Message.Type#normal}.
	 * 
	 * @return the type of the message.
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Sets the type of the message.
	 * 
	 * @param type
	 *            the type of the message.
	 * @throws IllegalArgumentException
	 *             if null is passed in as the type
	 */
	public void setType(Type type) {
		if (type == null) {
			throw new IllegalArgumentException("Type cannot be null.");
		}
		this.type = type;
	}

	/**
	 * Sets the seen state of the message.
	 * 
	 * @param seenState
	 *            the seen state of the message.
	 * @throws IllegalArgumentException
	 *             if null is passed in as the type
	 */
	public void setXMPPMessageType(XMPPMessageType xMPPMessageType) {
		if (xMPPMessageType == null) {
			throw new IllegalArgumentException("Type cannot be null.");
		}
		this.xMPPMessageType = xMPPMessageType;
	}

	public void setStatusMessageType(StatusMessageType statusMessageType) {
		if (statusMessageType == null) {
			throw new IllegalArgumentException("Type cannot be null.");
		}
		this.statusMessageType = statusMessageType;
	}
	/**
	 * 
	 * @return Message seen state
	 */
	public XMPPMessageType getXMPPMessageType() {
		return xMPPMessageType;
	}
	public StatusMessageType getStatusMessageType() {
		return statusMessageType;
	}
	public String getMediaTagMessage() {
		return mediaTagMessage;
	}

	public void setMediaTagMessage(String mediaTagMessage) {
		this.mediaTagMessage = mediaTagMessage;
	}
	public String getLocationMessage() {
		return locationMessage;
	}

	public String getJsonBody() {
		return jsonBody;
	}

	public void setLocationMessage(String locationMessage) {
		this.locationMessage = locationMessage;
	}

	public void setJsonBody(String jsonBody) {
		this.jsonBody = jsonBody;
	}
	/**
	 * Sets the seen state of the message.
	 * 
	 * @param seenState
	 *            the seen state of the message.
	 * @throws IllegalArgumentException
	 *             if null is passed in as the type
	 */
	public void setMessageSeenState(SeenState seenState) {
		if (seenState == null) {
			throw new IllegalArgumentException("Type cannot be null.");
		}
		this.seenState = seenState;
	}

	public void setCustDest(String custDest) {
		this.custDest = custDest;
	}
	public void setPacketId(String packetId) {
		this.packetId = packetId;
	}
	public void setConsoleMessage(String isConsoleMessage) {
		this.isConsoleMessage = isConsoleMessage;
	}

	public void setDisplayName(String displayName) {
//		if (displayName == null) {
//			throw new IllegalArgumentException("Type cannot be null.");
//		}
		this.displayName = displayName;
	}
	public void setGroupOwnerName(String groupOwnerName) {
//		if (displayName == null) {
//			throw new IllegalArgumentException("Type cannot be null.");
//		}
		this.groupOwnerName = groupOwnerName;
	}
	public void setGroupDisplayName(String groupDisplayname) {
//		if (displayName == null) {
//			throw new IllegalArgumentException("Type cannot be null.");
//		}
		this.groupDisplayname = groupDisplayname;
	}
	public void setPicId(String picId) {
//		if (picId == null) {
//			throw new IllegalArgumentException("Type cannot be null.");
//		}
		this.picId = picId;
	}
	public void setGroupId(String groupId) {
//		if (picId == null) {
//			throw new IllegalArgumentException("Type cannot be null.");
//		}
		this.groupId = groupId;
	}
	public void setGroupPicId(String groupPicId) {
//		if (picId == null) {
//			throw new IllegalArgumentException("Type cannot be null.");
//		}
		this.groupPicId = groupPicId;
	}
	public void setGroupmemberCount(String count) {
		this.memberCount = count;
	}
	/**
	 * 
	 * @return Message seen state
	 */
	public SeenState getMessageSeenState() {
		return seenState;
	}
	public String getGroupOwnerName() {
		return groupOwnerName;
	}
	public String getGroupDisplayname() {
		return groupDisplayname;
	}
	public String getDisplayName() {
		return displayName;
	}
	public String getPicId() {
		return picId;
	}
	public String getGroupId() {
		return groupId;
	}
	public String getGroupPICId() {
		return groupPicId;
	}
	public String getGroupMemberCount() {
		return memberCount;
	}

	public String isConsoleMessage() {
		return isConsoleMessage;
	}
	public String getCustDest() {
		return custDest;
	}
	public String getConsolePacketId() {
		return packetId;
	}
	
	/**
	 * Returns the default subject of the message, or null if the subject has
	 * not been set. The subject is a short description of message contents.
	 * <p>
	 * The default subject of a message is the subject that corresponds to the
	 * message's language. (see {@link #getLanguage()}) or if no language is set
	 * to the applications default language (see
	 * {@link Packet#getDefaultLanguage()}).
	 * 
	 * @return the subject of the message.
	 */
	public String getSubject() {
		return getSubject(null);
	}

	/**
	 * Returns the subject corresponding to the language. If the language is
	 * null, the method result will be the same as {@link #getSubject()}. Null
	 * will be returned if the language does not have a corresponding subject.
	 * 
	 * @param language
	 *            the language of the subject to return.
	 * @return the subject related to the passed in language.
	 */
	public String getSubject(String language) {
		Subject subject = getMessageSubject(language);
		return subject == null ? null : subject.subject;
	}

	private Subject getMessageSubject(String language) {
		language = determineLanguage(language);
		for (Subject subject : subjects) {
			if (language.equals(subject.language)) {
				return subject;
			}
		}
		return null;
	}

	/**
	 * Returns a set of all subjects in this Message, including the default
	 * message subject accessible from {@link #getSubject()}.
	 * 
	 * @return a collection of all subjects in this message.
	 */
	public Collection<Subject> getSubjects() {
		return Collections.unmodifiableCollection(subjects);
	}

	/**
	 * Sets the subject of the message. The subject is a short description of
	 * message contents.
	 * 
	 * @param subject
	 *            the subject of the message.
	 */
	public void setSubject(String subject) {
		if (subject == null) {
			removeSubject(""); // use empty string because #removeSubject(null)
								// is ambiguous
			return;
		}
		addSubject(null, subject);
	}

	/**
	 * Adds a subject with a corresponding language.
	 * 
	 * @param language
	 *            the language of the subject being added.
	 * @param subject
	 *            the subject being added to the message.
	 * @return the new {@link sdk.org.jivesoftware.smack.packet.Message.Subject}
	 * @throws NullPointerException
	 *             if the subject is null, a null pointer exception is thrown
	 */
	public Subject addSubject(String language, String subject) {
		language = determineLanguage(language);
		Subject messageSubject = new Subject(language, subject);
		subjects.add(messageSubject);
		return messageSubject;
	}

	/**
	 * Removes the subject with the given language from the message.
	 * 
	 * @param language
	 *            the language of the subject which is to be removed
	 * @return true if a subject was removed and false if it was not.
	 */
	public boolean removeSubject(String language) {
		language = determineLanguage(language);
		for (Subject subject : subjects) {
			if (language.equals(subject.language)) {
				return subjects.remove(subject);
			}
		}
		return false;
	}

	public void setMediaBody(MediaBody mediaBody) {
		this.mediaBody = mediaBody;
	}

	public MediaBody getMediaBody() {
		return mediaBody;
	}

	/**
	 * Removes the subject from the message and returns true if the subject was
	 * removed.
	 * 
	 * @param subject
	 *            the subject being removed from the message.
	 * @return true if the subject was successfully removed and false if it was
	 *         not.
	 */
	public boolean removeSubject(Subject subject) {
		return subjects.remove(subject);
	}

	/**
	 * Returns all the languages being used for the subjects, not including the
	 * default subject.
	 * 
	 * @return the languages being used for the subjects.
	 */
	public Collection<String> getSubjectLanguages() {
		Subject defaultSubject = getMessageSubject(null);
		List<String> languages = new ArrayList<String>();
		for (Subject subject : subjects) {
			if (!subject.equals(defaultSubject)) {
				languages.add(subject.language);
			}
		}
		return Collections.unmodifiableCollection(languages);
	}

	/**
	 * Returns the default body of the message, or null if the body has not been
	 * set. The body is the main message contents.
	 * <p>
	 * The default body of a message is the body that corresponds to the
	 * message's language. (see {@link #getLanguage()}) or if no language is set
	 * to the applications default language (see
	 * {@link Packet#getDefaultLanguage()}).
	 * 
	 * @return the body of the message.
	 */
	public String getBody() {
		return getBody(null);
	}

	/**
	 * Returns the body corresponding to the language. If the language is null,
	 * the method result will be the same as {@link #getBody()}. Null will be
	 * returned if the language does not have a corresponding body.
	 * 
	 * @param language
	 *            the language of the body to return.
	 * @return the body related to the passed in language.
	 * @since 3.0.2
	 */
	public String getBody(String language) {
		Body body = getMessageBody(language);
		return body == null ? null : body.message;
	}

	private Body getMessageBody(String language) {
		language = determineLanguage(language);
		for (Body body : bodies) {
			if (language.equals(body.language)) {
				return body;
			}
		}
		return null;
	}

	/**
	 * Returns a set of all bodies in this Message, including the default
	 * message body accessible from {@link #getBody()}.
	 * 
	 * @return a collection of all bodies in this Message.
	 * @since 3.0.2
	 */
	public Collection<Body> getBodies() {
		return Collections.unmodifiableCollection(bodies);
	}

	/**
	 * Sets the body of the message. The body is the main message contents.
	 * 
	 * @param body
	 *            the body of the message.
	 */
	public void setBody(String body) {
		if (body == null) {
			removeBody(""); // use empty string because #removeBody(null) is
							// ambiguous
			return;
		}
		addBody(null, body);
	}

	/**
	 * Adds a body with a corresponding language.
	 * 
	 * @param language
	 *            the language of the body being added.
	 * @param body
	 *            the body being added to the message.
	 * @return the new {@link sdk.org.jivesoftware.smack.packet.Message.Body}
	 * @throws NullPointerException
	 *             if the body is null, a null pointer exception is thrown
	 * @since 3.0.2
	 */
	public Body addBody(String language, String body) {
		language = determineLanguage(language);
		Body messageBody = new Body(language, body);
		bodies.add(messageBody);
		return messageBody;
	}

	/**
	 * Removes the body with the given language from the message.
	 * 
	 * @param language
	 *            the language of the body which is to be removed
	 * @return true if a body was removed and false if it was not.
	 */
	public boolean removeBody(String language) {
		language = determineLanguage(language);
		for (Body body : bodies) {
			if (language.equals(body.language)) {
				return bodies.remove(body);
			}
		}
		return false;
	}

	/**
	 * Removes the body from the message and returns true if the body was
	 * removed.
	 * 
	 * @param body
	 *            the body being removed from the message.
	 * @return true if the body was successfully removed and false if it was
	 *         not.
	 * @since 3.0.2
	 */
	public boolean removeBody(Body body) {
		return bodies.remove(body);
	}

	/**
	 * Returns all the languages being used for the bodies, not including the
	 * default body.
	 * 
	 * @return the languages being used for the bodies.
	 * @since 3.0.2
	 */
	public Collection<String> getBodyLanguages() {
		Body defaultBody = getMessageBody(null);
		List<String> languages = new ArrayList<String>();
		for (Body body : bodies) {
			if (!body.equals(defaultBody)) {
				languages.add(body.language);
			}
		}
		return Collections.unmodifiableCollection(languages);
	}

	/**
	 * Returns the thread id of the message, which is a unique identifier for a
	 * sequence of "chat" messages. If no thread id is set, <tt>null</tt> will
	 * be returned.
	 * 
	 * @return the thread id of the message, or <tt>null</tt> if it doesn't
	 *         exist.
	 */
	public String getThread() {
		return thread;
	}

	/**
	 * Sets the thread id of the message, which is a unique identifier for a
	 * sequence of "chat" messages.
	 * 
	 * @param thread
	 *            the thread id of the message.
	 */
	public void setThread(String thread) {
		this.thread = thread;
	}

	/**
	 * Returns the xml:lang of this Message.
	 * 
	 * @return the xml:lang of this Message.
	 * @since 3.0.2
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the xml:lang of this Message.
	 * 
	 * @param language
	 *            the xml:lang of this Message.
	 * @since 3.0.2
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	private String determineLanguage(String language) {

		// empty string is passed by #setSubject() and #setBody() and is the
		// same as null
		language = "".equals(language) ? null : language;

		// if given language is null check if message language is set
		if (language == null && this.language != null) {
			return this.language;
		} else if (language == null) {
			return getDefaultLanguage();
		} else {
			return language;
		}

	}

	public String toXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<message");
		if (getXmlns() != null) {
			buf.append(" xmlns=\"").append(getXmlns()).append("\"");
		}
		if (language != null) {
			buf.append(" xml:lang=\"").append(getLanguage()).append("\"");
		}
		if (getPacketID() != null) {
			buf.append(" id=\"").append(getPacketID()).append("\"");
		}
		if (getTo() != null) {
			buf.append(" to=\"").append(StringUtils.escapeForXML(getTo()))
					.append("\"");
		}

		if (getFrom() != null) {
			buf.append(" from=\"").append(StringUtils.escapeForXML(getFrom()))
					.append("\"");
		}
		if (seenState != null) {
			buf.append(" status=\"").append(seenState.ordinal()).append("\"");
		}
		if (groupOwnerName != null) {
			buf.append(" groupowner=\"").append(StringUtils.escapeForXML(groupOwnerName)).append("\"");
		}
		if (groupDisplayname != null) {
			buf.append(" groupDisplayname=\"").append(StringUtils.escapeForXML(groupDisplayname)).append("\"");
		}
		if (displayName != null) {
			buf.append(" displayname=\"").append(StringUtils.escapeForXML(displayName)).append("\"");
		}
		if (picId != null) {
			buf.append(" picid=\"").append(picId).append("\"");
		}
		if (memberCount != null) {
			buf.append(" memberCount=\"").append(memberCount).append("\"");
		}
		if (groupId != null) {
			buf.append(" groupId=\"").append(StringUtils.escapeForXML(groupId)).append("\"");
		}		
		if (groupPicId != null) {
			buf.append(" groupPicId=\"").append(StringUtils.escapeForXML(groupPicId)).append("\"");
		}		
		if (mediaTagMessage != null) {
			buf.append(" caption=\"").append(StringUtils.escapeForXML(mediaTagMessage)).append("\"");
		}
		if (mediaFileName != null) {
			buf.append("OriginalFileName=\"").append(StringUtils.escapeForXML(mediaFileName)).append("\"");
		}		
		if (locationMessage != null) {
			buf.append(" location=\"").append(StringUtils.escapeForXML(locationMessage)).append("\"");
		}
		if (jsonBody != null) {
			buf.append(" jsonBody=\"").append(StringUtils.escapeForXML(jsonBody)).append("\"");
		}
		if (custDest != null) {
			buf.append(" custDest=\"").append(StringUtils.escapeForXML(custDest)).append("\"");
		}
		if (packetId != null) {
			buf.append(" packetId=\"").append(StringUtils.escapeForXML(packetId)).append("\"");
		}
		if (isConsoleMessage != null) {
			buf.append(" isConsoleMessage=\"").append(StringUtils.escapeForXML(isConsoleMessage)).append("\"");
		}
		if (xMPPMessageType != null) {
			buf.append(" XMPPMessageType=\"").append(xMPPMessageType.ordinal())
					.append("\"");
		}
		if (statusMessageType != null) {
			buf.append("status_message_type=\"").append(statusMessageType.ordinal())
					.append("\"");
		}
		if (type != Type.normal) {
			buf.append(" type=\"").append(type).append("\"");
		}
		buf.append(">");
		// Add the subject in the default language
		Subject defaultSubject = getMessageSubject(null);
		if (defaultSubject != null) {
			buf.append("<subject>")
					.append(StringUtils.escapeForXML(defaultSubject.subject))
					.append("</subject>");
		}
		// Add the subject in other languages
		for (Subject subject : getSubjects()) {
			// Skip the default language
			if (subject.equals(defaultSubject))
				continue;
			buf.append("<subject xml:lang=\"").append(subject.language)
					.append("\">");
			buf.append(StringUtils.escapeForXML(subject.subject));
			buf.append("</subject>");
		}
		// Add the body in the default language
		Body defaultBody = getMessageBody(null);
		if (defaultBody != null) {
			buf.append("<body>")
					.append(StringUtils.escapeForXML(defaultBody.message))
					.append("</body>");
		}
		// Add the body in the default language
				MediaBody obj = getMediaBody();
				if (obj != null) {
					buf.append("<media");
					if(obj.getType()!=null)
						buf.append(" type=\"").append(obj.getType()).append("\"");
					if(obj.getUrl()!=null)
						buf.append(" url=\"").append(obj.getUrl()).append("\"");
					if(obj.getThumb_data()!=null)
						buf.append(" thumb_data=\"").append(obj.getThumb_data()).append("\"");
					if(obj.getAudioLength()!=null)
						buf.append(" audioLength=\"").append(obj.getAudioLength()).append("\"");
					buf.append("></media>");
				}
		// Add the bodies in other languages
		for (Body body : getBodies()) {
			// Skip the default language
			if (body.equals(defaultBody))
				continue;
			buf.append("<body xml:lang=\"").append(body.getLanguage())
					.append("\">");
			buf.append(StringUtils.escapeForXML(body.getMessage()));
			buf.append("</body>");
		}
		if (thread != null) {
			buf.append("<thread>").append(thread).append("</thread>");
		}
		// Append the error subpacket if the message type is an error.
		if (type == Type.error) {
			XMPPError error = getError();
			if (error != null) {
				buf.append(error.toXML());
			}
		}
		// Add packet extensions, if any are defined.
		buf.append(getExtensionsXML());
		buf.append("</message>");
		return buf.toString();
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Message message = (Message) o;

		if (!super.equals(message)) {
			return false;
		}
		if (bodies.size() != message.bodies.size()
				|| !bodies.containsAll(message.bodies)) {
			return false;
		}
		if (language != null ? !language.equals(message.language)
				: message.language != null) {
			return false;
		}
		if (subjects.size() != message.subjects.size()
				|| !subjects.containsAll(message.subjects)) {
			return false;
		}
		if (thread != null ? !thread.equals(message.thread)
				: message.thread != null) {
			return false;
		}
		return type == message.type;

	}

	public int hashCode() {
		int result;
		result = (type != null ? type.hashCode() : 0);
		result = 31 * result + subjects.hashCode();
		result = 31 * result + (thread != null ? thread.hashCode() : 0);
		result = 31 * result + (language != null ? language.hashCode() : 0);
		result = 31 * result + bodies.hashCode();
		return result;
	}

	/**
	 * Represents a message subject, its language and the content of the
	 * subject.
	 */
	public static class Subject {

		private String subject;
		private String language;

		private Subject(String language, String subject) {
			if (language == null) {
				throw new NullPointerException("Language cannot be null.");
			}
			if (subject == null) {
				throw new NullPointerException("Subject cannot be null.");
			}
			this.language = language;
			this.subject = subject;
		}

		/**
		 * Returns the language of this message subject.
		 * 
		 * @return the language of this message subject.
		 */
		public String getLanguage() {
			return language;
		}

		/**
		 * Returns the subject content.
		 * 
		 * @return the content of the subject.
		 */
		public String getSubject() {
			return subject;
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.language.hashCode();
			result = prime * result + this.subject.hashCode();
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Subject other = (Subject) obj;
			// simplified comparison because language and subject are always set
			return this.language.equals(other.language)
					&& this.subject.equals(other.subject);
		}

	}

	/**
	 * Represents a message body, its language and the content of the message.
	 */
	public static class Body {

		private String message;
		private String language;

		private Body(String language, String message) {
			if (language == null) {
				throw new NullPointerException("Language cannot be null.");
			}
			if (message == null) {
				throw new NullPointerException("Message cannot be null.");
			}
			this.language = language;
			this.message = message;
		}

		/**
		 * Returns the language of this message body.
		 * 
		 * @return the language of this message body.
		 */
		public String getLanguage() {
			return language;
		}

		/**
		 * Returns the message content.
		 * 
		 * @return the content of the message.
		 */
		public String getMessage() {
			return message;
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.language.hashCode();
			result = prime * result + this.message.hashCode();
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Body other = (Body) obj;
			// simplified comparison because language and message are always set
			return this.language.equals(other.language)
					&& this.message.equals(other.message);
		}

	}

	/**
	 * Represents a message media body, its language and the content of the
	 * message.
	 */
	public class MediaBody {

		private String type;
		private String url;
		private String thumb_data;
		private String audioLength;

		public MediaBody() {

		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getThumb_data() {
			return thumb_data;
		}

		public void setThumb_data(String thumb_data) {
			this.thumb_data = thumb_data;
		}
		public void setAudioLength(String audioLength) {
			this.audioLength = audioLength;
		}
		public String getAudioLength() {
			return audioLength;
		}
		
	}

	/**
	 * Represents a message delay, its language and the content of the
	 * message.
	 */
	public class MessageDelay {
		public String getStamp() {
			return stamp;
		}

		public void setStamp(String stamp) {
			this.stamp = stamp;
		}

		private String stamp;


	}

	/**
	 * Represents the XMPPMessageType of a message.
	 */
	public enum XMPPMessageType {
		/**
		 * Chat message for normal message - 0
		 */
		atMeXmppMessageTypeNormal,

		/**
		 * Chat message changing the group name - 1
		 */
		atMeXmppMessageTypeGroupName,

		/**
		 * Chat message for left the group - 2
		 */
		atMeXmppMessageTypeLeftGroup,

		/**
		 * Chat message for join group - 3
		 */
		atMeXmppMessageTypeJoinGroup,
		/**
		 * Chat message for change group image - 4
		 */
		atMeXmppMessageTypeGroupImage,
		/**
		 * Chat message for group admin update - 5
		 */
		atMeXmppMessageTypeGroupAdminUpdate,
		/**
		 * Chat message for getting member list- 6
		 */
		atMeXmppMessageTypeMemberList,
		/**
		 * Chat message for getting getting Image- 7
		 */
		atMeXmppMessageTypeImage,
		/**
		 * Chat message for getting getting Audio - 8
		 */
		atMeXmppMessageTypeAudio,
		/**
		 * Chat message for getting getting Video - 9
		 */
		atMeXmppMessageTypeVideo,
		/**
		 * Chat message for getting getting Location - 10
		 */
		atMeXmppMessageTypeLocation,
		/**
		 * Chat message for getting PDF File - 11
		 */
		atMeXmppMessageTypePdf,
		/**
		 * Chat message for getting Doc File - 12
		 */
		atMeXmppMessageTypeDoc,
		/**
		 * Chat message for getting PPT File - 13
		 */
		atMeXmppMessageTypePPT,
		/**
		 * Chat message for getting XLS File - 14
		 */
		atMeXmppMessageTypeXLS,		
		/**
		 * Chat message for getting getting GroupWelcomeMessage - 15
		 */
		atMeXmppMessageTypeGroupWelcomeMessage,
		/**
		 * Chat message for getting getting Contact sharing - 16
		 */
		atMeXmppMessageTypeContact,
		/**
		 * Chat message for getting getting Poll sharing - 17
		 */
		atMeXmppMessageTypePoll,
		/**
		 * Chat message for getting getting DeactivateUser - 18
		 */
		atMeXmppMessageTypeDeactivateUser,
		/**
		 * Chat message for creation of new group - 19
		 */
		atMeXmppMessageTypeNewCreateGroup,
		
		/**
		 * Chat message for creation of new broadcast - 20
		 */
		atMeXmppMessageTypeNewCreateBroadCast,
		/**
		 * Chat message for creation of new broadcast - 21
		 */
		atMeXmppMessageTypeRemoveBroadCast,
		/**
		 * Chat message for User Profile Update - 22
		 */
		atMeXmppMessageTypeUserProfileUpdate,
		/**
		 * Chat message for member activation - 23
		 */
		atMeXmppMessageTypeActivateUser,
		/**
		 * Chat message for member Registered - 24
		 */
		atMeXmppMessageTypeUserRegistered,
		/**
		 * Chat message for New Shared ID Created - 25
		 */
		atMeXmppMessageTypeSharedIDCreated,
		/**
		 * Chat message for New Shared ID Created - 26
		 */
		atMeXmppMessageTypeSharedIDUpdated,
		/**
		 * Chat message for Admin Removed From Shared ID - 27
		 */
		atMeXmppMessageTypeSharedIDAdminRemoved,
		/**
		 * Chat message for New Shared ID Deactivated - 28
		 */
		atMeXmppMessageTypeSharedIDDeleted,
		/**
		 * Chat message for Make SG SubAdmin - 29
		 */
		atMeXmppMessageTypeMakeSGSubAdmin,
		/**
		 * Chat message for Make SG SubAdmin - 30
		 */
		atMeXmppMessageTypeRemoveSGSubAdmin,
		/**
		 * Chat message type to be shown in center like group update, create etc. - 31
		 */
		atMeXmppMessageTypeSpecialMessage,
		/**
		 * Chat message type to be sent for SG profile update - 32
		 */
		atMeXmppMessageTypeSGUpdate,

		/**
		 * Chat message type to be sent for Bulletin image update - 33
		 */
		atMeXmppMessageTypeUpdateBulletin,

		/**
		 * Group Permission Update - 34
		 */
		atMeXmppMessageTypeAdminPermission
		;
		;

		public static XMPPMessageType fromString(String name) {
			try {
				return XMPPMessageType.valueOf(name);
			} catch (Exception e) {
				return atMeXmppMessageTypeNormal;
			}
		}

	}
	/**
	 * Represents the SeenState of a message.
	 */
	public enum StatusMessageType {
		/**
		 * Chat message for the none by user.
		 */
		normal,

		/**
		 * Chat message sent by user.
		 */
		group,

		/**
		 * Chat message received by user.
		 */
		broadcast,
		
		/**
		 * Chat broadcast message to all
		 */
		broadcasttoall,
		/**
		 * Shared ID message to all
		 */
		sharedID,
		/**
		 * stateAllSGCounters for all SG's
		 */
		stateAllSGCounters;

		public static StatusMessageType fromString(String name) {
			try {
				return StatusMessageType.valueOf(name);
			} catch (Exception e) {
				return normal;
			}
		}

	}
	/**
	 * Represents the SeenState of a message.
	 */
	public enum SeenState {
		/**
		 * Chat message for the none by user.
		 */
		none,

		/**
		 * Chat message sent by user.
		 */
		sent,

		/**
		 * Chat message received by user.
		 */
		recieved,

		/**
		 * Chat message seen by user.
		 */
		seen,
		/**
		 * Chat message for the status by user.
		 */
		wait,
		/**
		 * Chat message for image uploading and wait.
		 */
		pic_wait;

		public static SeenState fromString(String name) {
			try {
				return SeenState.valueOf(name);
			} catch (Exception e) {
				return sent;
			}
		}

	}

	/**
	 * Represents the type of a message.
	 */
	public enum Type {

		/**
		 * (Default) a normal text message used in email like interface.
		 */
		normal,

		/**
		 * Typically short text message used in line-by-line chat interfaces.
		 */
		chat,

		/**
		 * Chat message sent to a groupchat server for group chats.
		 */
		groupchat,

		/**
		 * Text message to be displayed in scrolling marquee displays.
		 */
		headline,

		/**
		 * indicates a messaging error.
		 */
		error;
		

		public static Type fromString(String name) {
			try {
				return Type.valueOf(name);
			} catch (Exception e) {
				return normal;
			}
		}

	}
}
