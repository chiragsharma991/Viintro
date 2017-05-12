package com.viintro.Viintro.Model;

/**
 * Created by hasai on 04/02/17.
 */

public class Verify_Otp_Request {

    private int client_id;
    private String client_secret;
    private String countryCode;
    private String mobileNumber;
    private String oneTimePassword;

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

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setOneTimePassword(String oneTimePassword) {
        this.oneTimePassword = oneTimePassword;
    }

    public String getOneTimePassword() {
        return oneTimePassword;
    }
}
