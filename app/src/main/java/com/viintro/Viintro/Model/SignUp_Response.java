package com.viintro.Viintro.Model;

import org.json.JSONObject;

/**
 * Created by hasai on 01/02/17.
 */

public class SignUp_Response extends JSONObject{

    private String token_type;
    private String access_token;
    private String refresh_token;
    private String fullname;
    private String email;
    private int id;
    private String display_pic;
    private int onboarding_process;
    private Videos videos;

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setDisplay_pic(String display_pic) {
        this.display_pic = display_pic;
    }

    public String getDisplay_pic() {
        return display_pic;
    }

    public void setOnboarding_process(int onboarding_process) {
        this.onboarding_process = onboarding_process;
    }

    public int getOnboarding_process() {
        return onboarding_process;
    }


    public void setVideos(Videos videos) {
        this.videos = videos;
    }

    public Videos getVideos() {
        return videos;
    }
}


