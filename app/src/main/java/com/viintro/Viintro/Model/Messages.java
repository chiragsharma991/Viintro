package com.viintro.Viintro.Model;

import org.json.JSONObject;

/**
 * Created by hasai on 19/04/17.
 */

public class Messages extends JSONObject
{

    // Messages List
    private int group_id;
    private String group_name;
    private String group_display_pic;
    private String last_message;
    private String time_ago;
    private Boolean read;

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_display_pic() {
        return group_display_pic;
    }

    public void setGroup_display_pic(String group_display_pic) {
        this.group_display_pic = group_display_pic;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getTime_ago() {
        return time_ago;
    }

    public void setTime_ago(String time_ago) {
        this.time_ago = time_ago;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }


    /// Group Conversation

    private int message_id;
    private int sender_id;
    private String sender_name;
    private String sender_display_pic;
    private String type;
    private String text;
    private String post_id;
    private String post_slug;
    private String post_owner_name;
    private String post_description;

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getSender_display_pic() {
        return sender_display_pic;
    }

    public void setSender_display_pic(String sender_display_pic) {
        this.sender_display_pic = sender_display_pic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPost_slug() {
        return post_slug;
    }

    public void setPost_slug(String post_slug) {
        this.post_slug = post_slug;
    }

    public String getPost_owner_name() {
        return post_owner_name;
    }

    public void setPost_owner_name(String post_owner_name) {
        this.post_owner_name = post_owner_name;
    }

    public String getPost_description() {
        return post_description;
    }

    public void setPost_description(String post_description) {
        this.post_description = post_description;
    }
}
