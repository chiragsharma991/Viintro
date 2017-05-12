package com.viintro.Viintro.Model;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by rkanawade on 18/03/17.
 */

public class ProfileViews_Response extends JSONArray{

    private int id;
    private String fullname;
    private String display_pic;
    private String updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDisplay_pic() {
        return display_pic;
    }

    public void setDisplay_pic(String display_pic) {
        this.display_pic = display_pic;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
