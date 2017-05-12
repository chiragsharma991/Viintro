package com.viintro.Viintro.Constants;

import com.android.volley.RequestQueue;
import com.facebook.CallbackManager;
import com.viintro.Viintro.Model.Social_Login_Request;
import com.viintro.Viintro.Model.Videos;

import java.util.ArrayList;

/**
 * Created by rKanawade on 24/01/2017.
 */
//Singleton pattern class. Variables used across multiple activities are used here
public class Configuration_Parameter
{
    private static Configuration_Parameter myObj;
    public static String web_api_link = "http://dev.api.viintro.com/api/v1/user";
    public static String web_api_link_header = "http://dev.api.viintro.com/api/v1";
    public static CallbackManager callbackManager;
    public static Social_Login_Request social_login_request;
    public static String login_access_token = "login_access_token";
    public static String login_refresh_token = "login_refresh_token";
    public static String login_fullname = "login_fullname";
    public static String login_id = "login_id";
    public static String login_email = "login_email";
    public static String login_display_pic = "login_display_pic";
    public static String from_activity_record_video = "";


    public static String Profile_user_type = "Profile_user_type";
    public static String profile_url = "profile_url";
    public static String profile_website = "profile_website";
    public static String University = "University";
    public static String Course = "Course";
    public static String Country_code = "Country_code";
    public static String Company = "Company";
    public static String Designation = "Designation";
    public static String Mobile_Number = "Mobile_Number";
    public static String Profile_city = "Profile_city";
    public static String Profile_state = "Profile_state";
    public static String Profile_country = "Profile_country";
    public static String Profile_latitude = "Profile_latitude";
    public static String Profile_longitude = "Profile_longitude";
    public static String Profile_connection_count = "Profile_connection_count";
    public static String Profile_view_count = "Profile_view_count";
    public static String Profile_following_count = "Profile_following_count";
    public static String Profile_followers_count = "Profile_followers_count";
    public static String Profile_Strength = "Profile_Strength";
    public static String Profile_dob_date = "Profile_dob_date";
    public static String Profile_dob_month = "Profile_dob_month";
    public static String Profile_dob_year = "Profile_dob_year";
    public static String Profile_live_videos = "Profile_live_videos";

    //public profile data
    public static String Public_profile_id = "Public_profile_id";
    public static String Public_profile_fullname = "Public_profile_fullname";
    public static String Public_profile_university = "Public_profile_university";
    public static String Public_profile_course = "Public_profile_course";
    public static String Public_profile_company = "Public_profile_company";
    public static String Public_profile_designation = "Public_profile_designation";
    public static String Public_profile_city = "Public_profile_city";
    public static String Public_profile_state = "Public_profile_state";
    public static String Public_profile_country = "Public_profile_country";
    public static String Public_profile_displaypic = "Public_profile_displaypic";
    public static String os_version;

    public static ArrayList<Videos> profile_video_list = new ArrayList<>();
    public static ArrayList profile_companies_list  = new ArrayList<>();
    public static ArrayList profile_designations_list  = new ArrayList<>();
    public static ArrayList profile_colleges_list  = new ArrayList<>();
    public static ArrayList profile_qualifications_list  = new ArrayList<>();
    public static ArrayList profile_schools_list = new ArrayList<>();
    public static ArrayList profile_skills_list = new ArrayList<>();
    public static ArrayList profile_others_list = new ArrayList<>();
    public static Videos intro_video_myprofile = new Videos();

    public static RequestQueue queue;


    /*
     * Create private constructor
     */
    private Configuration_Parameter()
    {

    }

    /**
     * Create a static method to get instance.
     */
    public static Configuration_Parameter getInstance()
    {
        if(myObj == null)
        {
            myObj = new Configuration_Parameter();
        }
        return myObj;
    }
}
