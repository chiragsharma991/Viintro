package com.viintro.Viintro.Model;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rkanawade on 03/03/17.
 */

public class MyProfile_Response extends JSONObject {

    private int id;
    private String user_type;
    private String fullname;
    private String university;
    private String profile_url;
    private String course;
    private String country_code;
    private String mobile_number;
    private String website;
    private String company;
    private String designation;
    private String city;
    private String state;
    private String country;
    private String latitude;
    private String longitude;
    private String dob_day;
    private String dob_month;
    private String dob_year;
    private int profile_strength;
    private int connection_count;
    private int profile_view_count;
    private int following_count;
    private int followers_count;
    private String display_pic;
    private int live_video;
    private ArrayList<Videos> videos;
    private ArrayList<Companies> companies;
    private ArrayList<Designations> designations;
    private ArrayList<Colleges> colleges;
    private ArrayList<Qualifications> qualifications;
    private ArrayList<Schools> schools;
    private ArrayList<Skills> skills;
    private ArrayList<Others> others;

    public String getDob_day() {
        return dob_day;
    }

    public void setDob_day(String dob_day) {
        this.dob_day = dob_day;
    }

    public String getDob_month() {
        return dob_month;
    }

    public void setDob_month(String dob_month) {
        this.dob_month = dob_month;
    }

    public String getDob_year() {
        return dob_year;
    }

    public void setDob_year(String dob_year) {
        this.dob_year = dob_year;
    }

    public ArrayList<Others> getOthers() {
        return others;
    }

    public void setOthers(ArrayList<Others> others) {
        this.others = others;
    }

    public ArrayList<Skills> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<Skills> skills) {
        this.skills = skills;
    }

    public ArrayList<Schools> getSchools() {
        return schools;
    }

    public void setSchools(ArrayList<Schools> schools) {
        this.schools = schools;
    }

    public ArrayList<Qualifications> getQualifications() {
        return qualifications;
    }

    public void setQualifications(ArrayList<Qualifications> qualifications) {
        this.qualifications = qualifications;
    }

    public ArrayList<Colleges> getColleges() {
        return colleges;
    }

    public void setColleges(ArrayList<Colleges> colleges) {
        this.colleges = colleges;
    }

    public ArrayList<Designations> getDesignations() {
        return designations;
    }

    public void setDesignations(ArrayList<Designations> designations) {
        this.designations = designations;
    }

    public ArrayList<com.viintro.Viintro.Model.Companies> getCompanies() {
        return companies;
    }

    public void setCompanies(ArrayList<com.viintro.Viintro.Model.Companies> companies) {
        this.companies = companies;
    }

    public int getProfile_strength() {
        return profile_strength;
    }

    public void setProfile_strength(int profile_strength) {
        this.profile_strength = profile_strength;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
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

    public int getConnection_count() {
        return connection_count;
    }

    public void setConnection_count(int connection_count) {
        this.connection_count = connection_count;
    }

    public int getProfile_view_count() {
        return profile_view_count;
    }

    public void setProfile_view_count(int profile_view_count) {
        this.profile_view_count = profile_view_count;
    }

    public int getFollowing_count() {
        return following_count;
    }

    public void setFollowing_count(int following_count) {
        this.following_count = following_count;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(int followers_count) {
        this.followers_count = followers_count;
    }

    public ArrayList<Videos> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<Videos> videos) {
        this.videos = videos;
    }

    public String getDisplay_pic() {
        return display_pic;
    }

    public void setDisplay_pic(String display_pic) {
        this.display_pic = display_pic;
    }

    public int getLive_video() {
        return live_video;
    }

    public void setLive_video(int live_video) {
        this.live_video = live_video;
    }
}
