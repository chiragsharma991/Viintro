package com.viintro.Viintro.Model;

/**
 * Created by hasai on 23/02/17.
 */

public class Youtube_Request {


    private int client_id;
    private String client_secret;
    private String description;
    private String video_source;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideo_source() {
        return video_source;
    }

    public void setVideo_source(String video_source) {
        this.video_source = video_source;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }


}
