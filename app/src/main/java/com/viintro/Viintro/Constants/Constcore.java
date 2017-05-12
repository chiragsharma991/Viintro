package com.viintro.Viintro.Constants;

import com.cloudinary.Cloudinary;

import java.util.Map;

/**
 * Created by rkanawade on 24/01/17.
 */

public class Constcore {

    final public static int RC_SIGN_IN = 100;
    public static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    public static final int OPEN_GALLERY = 200;
    public static final int CAMERA_WRITE_EXTERNAL_RECORD = 103;
    public static final int MY_PERMISSIONS_ACCESS_CF_LOCATION = 3;
    public static final int MY_PERMISSIONS_REQUEST_RWFRMCAM=60;
    public static final String EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH";

    public static final int REQUEST_CODE_ONBOARDING = 1;
    public static final int REQUEST_CODE_ADDCOLLEGES = 2;
    public static final int REQUEST_CODE_ADDCOMPANY = 4;
    public static final int REQUEST_CODE_ADDCONTACTDETAILS = 5;
    public static final int REQUEST_CODE_ADDDESIGNATION= 6;
    public static final int REQUEST_CODE_ADDOTHERS= 7;
    public static final int REQUEST_CODE_ADDQUALIFICATION= 8;
    public static final int REQUEST_CODE_ADDSCHOOLS= 9;
    public static final int REQUEST_CODE_ADDSKILLS= 10;
    public static final int REQUEST_CODE_EDITPROFILE= 11;
    public static final int REQUEST_CODE_CONNECT= 12;
    public static final int REQUEST_CODE_PROFILE_VIDEO= 13;
    public static final int REQUEST_CODE_ADDCONTACT_OTP_VERIFY = 14;
    public static final int REQUEST_LIKE = 15;
    public static final int REQUEST_CONNECTION = 16;

    public static boolean flashmode = false;
    //API Client ID and Client Secret key for API
    public static final int client_Id = 3;
    public static final String client_Secret = "Ti2xuao0OzSi95WYw117mw1HDTta8XOfcChswRnQ";
    //

    // Cloudinary
    public static final String CLOUDINARY_URL = "cloudinary://196476762657184:J3vteZZBwgm18g-TAaq7jA1OPp4@dep7kjzyg";
    public static final String cloud_name = "dep7kjzyg";
    public static final String api_key = "196476762657184";
    public static final String api_secret = "J3vteZZBwgm18g-TAaq7jA1OPp4";
    public static Map config;
    public static Cloudinary cloudinary;
    //

    public static final long record_time_onboarding = 120000;
    public static final long record_time_thought = 30000;
    public static final long record_time_comment = 30000;
    public static final long record_time_profile = 60000;
    public static final long record_time_connect = 30000;


    public static final int MAX_NUM_COMMENT = 140;
    public static final int MAX_NUM_SKILLS = 150;
    public static final int MAX_WORDS = 50;

    public static final String version = "1";



}
