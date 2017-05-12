package com.viintro.Viintro.Model;

import org.json.JSONObject;

/**
 * Created by hasai on 01/02/17.
 */

public class Login_ResponseWork extends JSONObject{

    private int code;
    private String message;
    private DataArray dataArray;
    private boolean onboard_process;
    private boolean profile_video;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }



    public void setOnboard_process(boolean onboard_process) {
        this.onboard_process = onboard_process;
    }

    public boolean getOnboard_process() {
        return onboard_process;
    }

    public void setProfile_video(boolean profile_video) {
        this.profile_video = profile_video;
    }

    public boolean getProfile_video() {
        return profile_video;
    }

    public void setDataArray(DataArray dataArray) {
        this.dataArray = dataArray;
    }

    public DataArray getDataArray() {
        return dataArray;
    }

    public class DataArray {

        private String token_type;
        private String access_token;
        private String refresh_token;
        private String fullname;
        private String email;
        private int id;
        private String display_pic;

        public void setToken_type(String token_type) {
            this.token_type = token_type;
        }

        public String getToken_type() {
            return token_type;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getAccess_token() {
            return access_token;
        }

        public void setRefresh_token(String refresh_token) {
            this.refresh_token = refresh_token;
        }

        public String getRefresh_token() {
            return refresh_token;
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

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setDisplay_pic(String display_pic) {
            this.display_pic = display_pic;
        }

        public String getDisplay_pic() {
            return display_pic;
        }

    }





}

