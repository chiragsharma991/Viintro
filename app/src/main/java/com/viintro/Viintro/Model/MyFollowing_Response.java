package com.viintro.Viintro.Model;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hasai on 22/03/17.
 */

public class MyFollowing_Response extends JSONObject {

    private int following_count;
    private ArrayList<Following> following;

    public int getFollowing_count() {
        return following_count;
    }

    public void setFollowing_count(int following_count) {
        this.following_count = following_count;
    }

    public ArrayList<Following> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<Following> following) {
        this.following = following;
    }
}
