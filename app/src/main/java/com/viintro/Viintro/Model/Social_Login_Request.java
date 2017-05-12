package com.viintro.Viintro.Model;

import com.viintro.Viintro.Constants.Constcore;

/**
 * Created by hasai on 01/02/17.
 */

public class Social_Login_Request {

    private int client_id;
    private String client_secret;
    private String signup_type;
    private String fullname;
    private String email;
    private String gender;
    private String profilepic;
    private String coverpic;
    private String givenname;
    private String familyname;
    private String id;
    private String device_id;
    private String push_token;

    public String getPush_token() {
        return push_token;
    }

    public void setPush_token(String push_token) {
        this.push_token = push_token;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setSignup_type(String signup_type) {
        this.signup_type = signup_type;
    }

    public String getSignup_type() {
        return signup_type;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setCoverpic(String coverpic) {
        this.coverpic = coverpic;
    }

    public String getCoverpic() {
        return coverpic;
    }

    public void setGivenname(String givenname) {
        this.givenname = givenname;
    }

    public String getGivenname() {
        return givenname;
    }

    public void setFamilyname(String familyname) {
        this.familyname = familyname;
    }

    public String getFamilyname() {
        return familyname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static Social_Login_Request json_social_login_request(String Logintype, String Fullname, String Email, String Gender, String ProfilePic, String CoverPic, String GivenName, String FamilyName, String Id, String device_id, String push_token)
    {

        Social_Login_Request social_login_request = new Social_Login_Request();
        social_login_request.setClient_id(Constcore.client_Id);
        social_login_request.setClient_secret(Constcore.client_Secret);
        social_login_request.setSignup_type(Logintype);
        social_login_request.setFullname(Fullname);
        social_login_request.setEmail(Email);
        social_login_request.setGender(Gender);
        social_login_request.setProfilepic(ProfilePic);
        social_login_request.setCoverpic(CoverPic);
        social_login_request.setGivenname(GivenName);
        social_login_request.setFamilyname(FamilyName);
        social_login_request.setId(Id);
        social_login_request.setDevice_id(device_id);
        social_login_request.setPush_token(push_token);


        return social_login_request;
    }
}
