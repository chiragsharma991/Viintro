package com.viintro.Viintro.Model;

/**
 * Created by rkanawade on 17/03/17.
 */

public class UpdateDisplayPic_Request {

    private int client_id;
    private String client_secret;
    private String display_pic;

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

    public String getDisplay_pic() {
        return display_pic;
    }

    public void setDisplay_pic(String display_pic) {
        this.display_pic = display_pic;
    }
}
