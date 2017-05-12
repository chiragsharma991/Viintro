package com.viintro.Viintro.Model;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hasai on 22/03/17.
 */

public class MyFollowers_Response extends JSONObject {

    private int followers_count;
    private ArrayList<Followers> followers;

    public int getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(int followers_count) {
        this.followers_count = followers_count;
    }

    public ArrayList<Followers>  getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<Followers>  followers) {
        this.followers = followers;
    }
}
