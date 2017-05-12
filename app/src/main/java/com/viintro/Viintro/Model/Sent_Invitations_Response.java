package com.viintro.Viintro.Model;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hasai on 20/03/17.
 */

public class Sent_Invitations_Response extends JSONObject
{

    int connection_count;
    int sent_invitation_count;
    ArrayList<Invitations> sent_invitations;


    public int getConnection_count() {
        return connection_count;
    }

    public void setConnection_count(int connection_count) {
        this.connection_count = connection_count;
    }

    public int getSent_invitation_count() {
        return sent_invitation_count;
    }

    public void setSent_invitation_count(int sent_invitation_count) {
        this.sent_invitation_count = sent_invitation_count;
    }

    public ArrayList<Invitations> getSent_invitations() {
        return sent_invitations;
    }

    public void setSent_invitations(ArrayList<Invitations> sent_invitations) {
        this.sent_invitations = sent_invitations;
    }
}
