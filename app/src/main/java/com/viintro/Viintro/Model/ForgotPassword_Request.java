package com.viintro.Viintro.Model;

/**
 * Created by hasai on 09/02/17.
 */

public class ForgotPassword_Request {

    private int client_id;
    private String client_secret;
    private String email;

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

}
