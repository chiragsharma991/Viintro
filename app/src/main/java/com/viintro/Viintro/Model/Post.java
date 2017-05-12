package com.viintro.Viintro.Model;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hasai on 08/04/17.
 */

public class Post extends JSONObject{

    private String id;
    private String description;
    private Video video;
    private Owner owner;
    private String slug;
    private int likes;
    private int comments;
    private int view_count;
    private String status;
    private String created_by;
    private Boolean user_liked;
    private String updated_at;
    private String created_at;
    private String post_type;
    private Boolean following;
    private ArrayList<Report> report;// will be returned in share post APi


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public Boolean getUser_liked() {
        return user_liked;
    }

    public void setUser_liked(Boolean user_liked) {
        this.user_liked = user_liked;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }

    public Boolean getFollowing() {
        return following;
    }

    public void setFollowing(Boolean following) {
        this.following = following;
    }

    public ArrayList<Report> getReport() {
        return report;
    }

    public void setReport(ArrayList<Report> report) {
        this.report = report;
    }
}
