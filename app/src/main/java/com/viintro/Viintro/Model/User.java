package com.viintro.Viintro.Model;

import org.json.JSONObject;

/**
 * Created by rkanawade on 31/03/17.
 */

public class User extends JSONObject{

    private int id;
    private String fullname;
    private String display_pic;

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
}
