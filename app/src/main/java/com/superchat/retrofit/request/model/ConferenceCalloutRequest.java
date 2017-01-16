package com.superchat.retrofit.request.model;

/**
 * Created by Mahesh on 09/01/17.
 */

public class ConferenceCalloutRequest {

    private String method = "conferenceCallout";

    public String getMethod() { return this.method; }

    public void setMethod(String method) { this.method = method; }

    private ConferenceCallout conferenceCallout;

    public ConferenceCallout getConferenceCallout() { return this.conferenceCallout; }

    public void setConferenceCallout(ConferenceCallout conferenceCallout) { this.conferenceCallout = conferenceCallout; }

    public class Destination
    {
        private String type;

        public String getType() { return this.type; }

        public void setType(String type) { this.type = type; }

        private String endpoint;

        public String getEndpoint() { return this.endpoint; }

        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    }

    public class ConferenceCallout
    {
        private String cli;

        public String getCli() { return this.cli; }

        public void setCli(String cli) { this.cli = cli; }

        private Destination destination;

        public Destination getDestination() { return this.destination; }

        public void setDestination(Destination destination) { this.destination = destination; }

        private String domain = "mxp";

        public String getDomain() { return this.domain; }

        public void setDomain(String domain) { this.domain = domain; }

        private String custom = "customData";

        public String getCustom() { return this.custom; }

        public void setCustom(String custom) { this.custom = custom; }

        private String locale = "en-US";

        public String getLocale() { return this.locale; }

        public void setLocale(String locale) { this.locale = locale; }

        private String greeting = "Welcome to my conference";

        public String getGreeting() { return this.greeting; }

        public void setGreeting(String greeting) { this.greeting = greeting; }

        private String conferenceId;

        public String getConferenceId() { return this.conferenceId; }

        public void setConferenceId(String conferenceId) { this.conferenceId = conferenceId; }

        private boolean enableAce = false;

        public boolean getEnableAce() { return this.enableAce; }

        public void setEnableAce(boolean enableAce) { this.enableAce = enableAce; }

        private boolean enableDice = false;

        public boolean getEnableDice() { return this.enableDice; }

        public void setEnableDice(boolean enableDice) { this.enableDice = enableDice; }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        private String event = "ice";
    }

}
