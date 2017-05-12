package com.viintro.Viintro.Model;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hasai on 24/04/17.
 */

public class MessageDetails extends JSONObject {
    GroupDetails groupdetails;
    ArrayList<Messages> messages;

    public GroupDetails getGroupdetails() {
        return groupdetails;
    }

    public void setGroupdetails(GroupDetails groupdetails) {
        this.groupdetails = groupdetails;
    }

    public ArrayList<Messages> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Messages> messages) {
        this.messages = messages;
    }
}
