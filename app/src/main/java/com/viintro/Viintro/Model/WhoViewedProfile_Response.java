package com.viintro.Viintro.Model;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hasai on 23/03/17.
 */

public class WhoViewedProfile_Response extends JSONObject{

    private int profile_view_count;
    private ArrayList<Profile_Views> profile_views;

    public int getProfile_view_count() {
        return profile_view_count;
    }

    public void setProfile_view_count(int profile_view_count) {
        this.profile_view_count = profile_view_count;
    }

    public ArrayList<Profile_Views> getProfile_views() {
        return profile_views;
    }

    public void setProfile_views(ArrayList<Profile_Views> profile_views) {
        this.profile_views = profile_views;
    }

}
