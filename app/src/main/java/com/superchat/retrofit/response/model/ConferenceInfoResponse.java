package com.superchat.retrofit.response.model;

import java.util.ArrayList;

/**
 * Created by Mahesh on 10/01/17.
 */

public class ConferenceInfoResponse {
    private ArrayList<Participant> participants;

    public ArrayList<Participant> getParticipants() { return this.participants; }

    public void setParticipants(ArrayList<Participant> participants) { this.participants = participants; }

    public class Participant
    {
        private String cli;

        public String getCli() { return this.cli; }

        public void setCli(String cli) { this.cli = cli; }

        private String id;

        public String getId() { return this.id; }

        public void setId(String id) { this.id = id; }

        private int duration;

        public int getDuration() { return this.duration; }

        public void setDuration(int duration) { this.duration = duration; }

        private boolean muted;

        public boolean getMuted() { return this.muted; }

        public void setMuted(boolean muted) { this.muted = muted; }
    }
}
