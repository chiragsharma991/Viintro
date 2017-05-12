package com.viintro.Viintro.Model;

/**
 * Created by rkanawade on 12/04/17.
 */

public class ShareMsgContents {

    private String name;
    private String designation;
    private String location;
    private String image;
    private Boolean ischeck;

    public Boolean getIscheck() {
        return ischeck;
    }

    public void setIscheck(Boolean ischeck) {
        this.ischeck = ischeck;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
