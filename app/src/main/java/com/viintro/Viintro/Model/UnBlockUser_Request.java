package com.viintro.Viintro.Model;

/**
 * Created by rkanawade on 08/04/17.
 */

public class UnBlockUser_Request {

    private int client_id;
    private String client_secret;
    private int user_id;

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

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
