package com.viintro.Viintro.Model;

import org.json.JSONObject;

/**
 * Created by hasai on 13/04/17.
 */
public class Invitations extends JSONObject
{

    private int id;
    private String fullname;
    private String display_pic;
    private String user_type;
    private String course;
    private String university;
    private String company;
    private String designation;
    private String public_id;
    private String secure_url_mpd;
    private String secure_url_hls;
    private String secure_url_mp4;
    private String thumbnail;
    private String message;
    private String type;//for bifarcation purpose

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

    public String getPublic_id() {
        return public_id;
    }

    public void setPublic_id(String public_id) {
        this.public_id = public_id;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
