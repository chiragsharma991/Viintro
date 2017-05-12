package com.viintro.Viintro.Model;

import org.json.JSONObject;

/**
 * Created by hasai on 10/04/17.
 */
public class Comment_Video_Request extends JSONObject{

    private int client_id;
    private String client_secret;
    private String comment_data;
    private Video video;

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

    public String getComment_data() {
        return comment_data;
    }

    public void setComment_data(String comment_data) {
        this.comment_data = comment_data;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }
}
