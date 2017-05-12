package com.viintro.Viintro.Model;

/**
 * Created by hasai on 01/02/17.
 */

public class Login_Request {

    private int client_id;
    private String client_secret;
    private String email;
    private String password;
    private String device_id;
    private String push_token;

    public String getPush_token() {
        return push_token;
    }

    public void setPush_token(String push_token) {
        this.push_token = push_token;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

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

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
