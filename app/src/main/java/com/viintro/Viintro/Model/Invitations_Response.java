package com.viintro.Viintro.Model;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hasai on 20/03/17.
 */

public class Invitations_Response extends JSONObject
{

    int connection_count;
    int invitation_count;
    ArrayList<Invitations> invitations;


    public int getConnection_count() {
        return connection_count;
    }

    public void setConnection_count(int connection_count) {
        this.connection_count = connection_count;
    }

    public int getInvitation_count() {
        return invitation_count;
    }

    public void setInvitation_count(int invitation_count) {
        this.invitation_count = invitation_count;
    }

    public ArrayList<Invitations> getInvitations() {
        return invitations;
    }

    public void setInvitations(ArrayList<Invitations> invitations) {
        this.invitations = invitations;
    }


}
