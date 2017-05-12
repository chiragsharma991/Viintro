package com.viintro.Viintro.Model;

import java.util.ArrayList;

/**
 * Created by rkanawade on 20/04/17.
 */
//APR35BHUG
    //http://www.firstcry.com/young-birds/young-birds-floral-jumpsuit-pink/1370066/product-detail
public class SendNewMsg_Request {

    private int client_id;
    private String client_secret;
    private String group_name;
    private String group_display_pic;
    private int user_ids;
    private String type;
    private String text;
    private String post_id;
    private String post_slug;
    private int post_owner_id;
    private String post_owner_name;
    private String post_description;

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
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

    public int getUser_ids() {
        return user_ids;
    }

    public void setUser_ids(int user_ids) {
        this.user_ids = user_ids;
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

    public int getPost_owner_id() {
        return post_owner_id;
    }

    public void setPost_owner_id(int post_owner_id) {
        this.post_owner_id = post_owner_id;
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

