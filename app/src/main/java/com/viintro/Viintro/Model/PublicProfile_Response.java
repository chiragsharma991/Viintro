package com.viintro.Viintro.Model;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rkanawade on 09/03/17.
 */

public class PublicProfile_Response extends JSONObject
{

    private int id;
    private Boolean block;
    private String block_message;
    private Boolean connection_requested;
    private Boolean connected;
    private Boolean following;
    private String user_type;
    private String profile_url;
    private String fullname;
    private String website;
    private String display_pic;
    private String university;
    private String course;
    private String company;
    private String designation;
    private String city;
    private String state;
    private String country;
    private String mobile_number;
    private int connection_count;
    private int followers_count;
    private int following_count;
    private ArrayList<Videos> profile_video;

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

    public Boolean getConnection_requested() {
        return connection_requested;
    }

    public void setConnection_requested(Boolean connection_requested) {
        this.connection_requested = connection_requested;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

    public Boolean getFollowing() {
        return following;
    }

    public void setFollowing(Boolean following) {
        this.following = following;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDisplay_pic() {
        return display_pic;
    }

    public void setDisplay_pic(String display_pic) {
        this.display_pic = display_pic;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public int getConnection_count() {
        return connection_count;
    }

    public void setConnection_count(int connection_count) {
        this.connection_count = connection_count;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(int followers_count) {
        this.followers_count = followers_count;
    }

    public int getFollowing_count() {
        return following_count;
    }

    public void setFollowing_count(int following_count) {
        this.following_count = following_count;
    }

    public ArrayList<Videos> getProfile_video() {
        return profile_video;
    }

    public void setProfile_video(ArrayList<Videos> profile_video) {
        this.profile_video = profile_video;
    }
}
