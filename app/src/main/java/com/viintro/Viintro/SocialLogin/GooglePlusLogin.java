package com.viintro.Viintro.SocialLogin;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.HomeFeed.HomeActivity;
import com.viintro.Viintro.Login.LoginActivity;
import com.viintro.Viintro.Login.RegistrationActivity;
import com.viintro.Viintro.Model.Social_Login_Request;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Webservices.LoginAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.viintro.Viintro.Login.LoginActivity.rel_email_popup_login;
import static com.viintro.Viintro.Login.RegistrationActivity.rel_email_popup_register;


/**
 * Created by rkanawade on 24/01/17.
 */

public class GooglePlusLogin {

    static Configuration_Parameter m_config;
    static SharedPreferences sharedPreferences;


    public static void handleSignInResult(GoogleSignInResult result, Context mContext, GoogleApiClient mGoogleApiClient) {

        m_config = Configuration_Parameter.getInstance();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (result.isSuccess())
        {
            Log.d("google plus", "successful");
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String fullname = "", givenname = "", familyname = "";

            if(!acct.getDisplayName().equals("null"))
            {
                fullname = acct.getDisplayName();
            }
            if(!acct.getGivenName().equals("null"))
            {
                givenname = acct.getGivenName();
            }
            if(!acct.getFamilyName().equals("null"))
            {
                familyname = acct.getFamilyName();
            }

            String id = acct.getId();
            String gender = "";
            String email = "";


            if (!acct.getEmail().equals("null")) {
                email = acct.getEmail();
            }
            String profilepic = "";
            if(acct.getPhotoUrl() != null) {
                profilepic = String.valueOf(acct.getPhotoUrl());
            }

            String coverpic = "";

            //** used to fetch person profile using google play services plus **//
            if (mGoogleApiClient.hasConnectedApi(Plus.API)) {
                Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                if (person != null) {

                    int genderval = person.getGender();
                    if (genderval == 0) {
                        gender = "male";
                    } else if (genderval == 1) {
                        gender = "female";
                    } else if (genderval == 2) {
                        gender = "other";
                    }

                    Log.i("GooglePlusLogin", "Display Name: " + person.getDisplayName());
                    Log.i("GooglePlusLogin", "Gender: " + person.getGender());
                    Log.i("GooglePlusLogin", "About Me: " + person.getAboutMe());
                    Log.i("GooglePlusLogin", "Birthday: " + person.getBirthday());
                    Log.i("GooglePlusLogin", "Current Location: " + person.getCurrentLocation());
                    Log.i("GooglePlusLogin", "Language: " + person.getLanguage());
                } else {
                    Log.e("GooglePlusLogin", "Error!");

                }
            }

            //**//

            String device_id = sharedPreferences.getString("device_id",null);
            m_config.social_login_request = Social_Login_Request.json_social_login_request("google", fullname, email, gender, profilepic, coverpic, givenname, familyname, id, device_id, "");

            if (m_config.social_login_request.getEmail().equals("")) {
                CommonFunctions.hDialog();
                if (mContext instanceof RegistrationActivity) {
                    rel_email_popup_register.setVisibility(View.VISIBLE);
                } else if (mContext instanceof LoginActivity) {
                    rel_email_popup_login.setVisibility(View.VISIBLE);
                }
            } else {
                // call to social login API
                LoginAPI.req_sociallogin_API(mContext, m_config.social_login_request);
            }
            //**//




        } else {
            Log.d("google plus", "failed");
        }
    }


}
