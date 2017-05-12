package com.viintro.Viintro.Model;

/**
 * Created by rkanawade on 03/03/17.
 */

public class Connection_Req_Acc_Rej_Rem {

    private int client_id;
    private String client_secret;
    private int user_id;
    private String message;
    private String public_id;
    private String created_at;
    private String secure_url_mpd;
    private String secure_url_hls;
    private String secure_url_mp4;
    private String thumbnail;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPublic_id() {
        return public_id;
    }

    public void setPublic_id(String public_id) {
        this.public_id = public_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getSecure_url_mpd() {
        return secure_url_mpd;
    }

    public void setSecure_url_mpd(String secure_url_mpd) {
        this.secure_url_mpd = secure_url_mpd;
    }

    public String getSecure_url_hls() {
        return secure_url_hls;
    }

    public void setSecure_url_hls(String secure_url_hls) {
        this.secure_url_hls = secure_url_hls;
    }

    public String getSecure_url_mp4() {
        return secure_url_mp4;
    }

    public void setSecure_url_mp4(String secure_url_mp4) {
        this.secure_url_mp4 = secure_url_mp4;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

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

