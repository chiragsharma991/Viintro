package com.viintro.Viintro.Model;

import org.json.JSONObject;

/**
 * Created by hasai on 03/03/17.
 */

public class MySuggestions_Response extends JSONObject
{

    int id;
    private Boolean block;
    private String block_message;
    String fullname;
    String display_pic;
    String user_type;
    String course;
    String university;
    String company;
    String designation;
    Boolean following;
    Boolean connection_requested;
    String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getBlock() {
        return block;
    }

    public void setBlock(Boolean block) {
        this.block = block;
    }

    public String getBlock_message() {
        return block_message;
    }

    public void setBlock_message(String block_message) {
        this.block_message = block_message;
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

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Boolean getFollowing() {
        return following;
    }

    public void setFollowing(Boolean following) {
        this.following = following;
    }

    public Boolean getConnection_requested() {
        return connection_requested;
    }

    public void setConnection_requested(Boolean connection_requested) {
        this.connection_requested = connection_requested;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
