package com.viintro.Viintro.Model;

/**
 * Created by rkanawade on 08/04/17.
 */

public class BlockUser_Request {

    private int client_id;
    private String client_secret;
    private int blocked_user_id;
    private String reason;

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

    public int getBlocked_user_id() {
        return blocked_user_id;
    }

    public void setBlocked_user_id(int blocked_user_id) {
        this.blocked_user_id = blocked_user_id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
