package com.viintro.Viintro.Webservices;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.HomeFeed.HomeActivity;
import com.viintro.Viintro.Login.LoginActivity;
import com.viintro.Viintro.Model.Login_Request;
import com.viintro.Viintro.Model.Login_Response;
import com.viintro.Viintro.Model.Social_Login_Request;
import com.viintro.Viintro.OnBoarding.OnBoardingActivity;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.VideoRecord.VideoRecordingActivity;
import com.viintro.Viintro.ViintroApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.viintro.Viintro.Login.LoginActivity.btn_show_pw;

/**
 * Created by hasai on 04/02/17.
 */

public class LoginAPI {

    // API Call for login
    public static void req_login_API(final Context cont, Login_Request login_request) {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final Gson gson = new Gson();
        String url = m_config.web_api_link+ "/signin";

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("client_id", login_request.getClient_id());
            obj.put("client_secret", login_request.getClient_secret());
            obj.put("email", login_request.getEmail());
            obj.put("password", login_request.getPassword());
            obj.put("device_id", login_request.getDevice_id());
            obj.put("push_token", login_request.getPush_token());

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        Log.e("-object--"," "+obj);

        // prepare the Request
        JsonObjectRequest loginrequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        int code = -1;
                        try
                        {
                            Log.e("response code"," "+response.getInt("code"));
                            code = response.getInt("code");
                            if(code != -1)
                            {
                                CommonFunctions.hDialog();
                                CommonFunctions.displayToast(context,response.getString("message"));

                            }
                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Login_Response login_response = gson.fromJson(response.toString(),Login_Response.class);
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(m_config.login_access_token, login_response.getAccess_token());
                        editor.putString(m_config.login_refresh_token, login_response.getRefresh_token());
                        editor.putString(m_config.login_fullname, login_response.getFullname());
                        editor.putString(m_config.login_email, login_response.getEmail());
                        editor.putInt(m_config.login_id, login_response.getId());
                        editor.putString(m_config.login_display_pic, login_response.getDisplay_pic());
                        editor.putBoolean("login_done",true);
                        editor.commit();
                        CommonFunctions.hDialog();


                        if(login_response.getOnboarding_process() == 4)
                        {

                            editor.putBoolean("onboard_process", true);
                            editor.putBoolean("profile_video", true);
                            editor.commit();
                            // call to home page
                            //CommonFunctions.displayToast(context,"You have finished onboarding process");
                            Intent int_home_page = new Intent(context, HomeActivity.class);
                            int_home_page.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(int_home_page);
                            ((Activity)context).finish();
                        }
                        else
                        {
                            if(login_response.getVideos() == null)
                            {
                                // call to record video
                                if(CommonFunctions.checkCameraHardware(context))
                                {
                                    Intent int_video_record = new Intent(context, VideoRecordingActivity.class);
                                    int_video_record.putExtra("from","onboarding");
                                    int_video_record.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(int_video_record);
                                    ((Activity)context).finish();

                                }

                            }
                            else
                            {

                                editor.putBoolean("profile_video", true);
                                editor.commit();
                                // call to onboard screen
                                Intent int_onboarding  = new Intent(context, OnBoardingActivity.class);
                                int_onboarding.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(int_onboarding);
                                ((Activity)context).finish();

                            }
                        }





                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                        CommonFunctions.hDialog();
                        if(error.networkResponse != null)
                        {
                            Log.e("error status code "," "+error.networkResponse.statusCode);

                            String networkresponse_data = new String(error.networkResponse.data);
                            if(error.networkResponse.statusCode == 500)
                            {
                                CommonFunctions.displayToast(context,context.getResources().getString(R.string.internal_server_error));
                            }
                            else {
                            if(networkresponse_data != null)
                            {
                                JSONObject network_obj;
                                try
                                {
                                    network_obj = new JSONObject(networkresponse_data);
                                    CommonFunctions.hDialog();
                                    if(network_obj.getInt("code") == 12)
                                    {
                                        // Alert Dialog for access token expired
                                        CommonFunctions.alertdialog_token(context,"accesstokenexpired");
                                    }
                                    else if(network_obj.getInt("code") == 16)
                                    {
                                        // Alert Dialog for refresh token expired
                                        CommonFunctions.alertdialog_token(context,"refreshtokenexpired");
                                    }
                                    else if(network_obj.getInt("code") == 4)
                                    {
                                        btn_show_pw.setVisibility(View.VISIBLE);
                                        btn_show_pw.setEnabled(true);
                                    }
                                    else if(network_obj.getInt("code") == 5)
                                    {
                                        if (network_obj.getString("message").equals("Incorrect password.")) {
                                            LoginActivity context = (LoginActivity) cont;
                                            context.setErrorMsg("password", network_obj.getString("message"));
                                        } else if (network_obj.getString("message").equals("Sorry! This email ID is not registered.")) {
                                            LoginActivity context = (LoginActivity) cont;
                                            context.setErrorMsg("email", network_obj.getString("message"));
                                        }
                                    }
                                    else
                                    {
                                        CommonFunctions.displayToast(context,network_obj.getString("message"));
                                    }
                                }
                                catch (JSONException e)
                                {
                                    CommonFunctions.hDialog();
                                    CommonFunctions.displayToast(context,context.getResources().getString(R.string.Login_failed));
                                }

                            }}
                        } else
                        {
                            CommonFunctions.hDialog();
                            CommonFunctions.displayToast(context,context.getResources().getString(R.string.Login_failed));
                        }


                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("version",Constcore.version);
                params.put("devicetype","android");
                params.put("timezone",CommonFunctions.getTimeZoneIST());
                params.put("timesstamp",CommonFunctions.getTimeStamp());
                params.put("os_version",m_config.os_version);
                return params;
            }
        };



        // add it to the RequestQueue
        int socketTimeout = 10000;//60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        loginrequest.setRetryPolicy(policy);
        //queue.add(loginrequest);

        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(loginrequest, "login_api");

    }

    // API Call for social login
    public static void req_sociallogin_API(final Context cont, Social_Login_Request social_login_request) {

        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue((Activity) context);
        final Gson gson = new Gson();
        String url = m_config.web_api_link + "/social-login";

        JSONObject obj = new JSONObject();
        try {
            obj.put("client_id", social_login_request.getClient_id());
            obj.put("client_secret", social_login_request.getClient_secret());
            obj.put("signup_type", social_login_request.getSignup_type());
            obj.put("fullname", social_login_request.getFullname());
            obj.put("email", social_login_request.getEmail());
            obj.put("gender", social_login_request.getGender());
            obj.put("profile_pic", social_login_request.getProfilepic());
            obj.put("cover_pic", social_login_request.getCoverpic());
            obj.put("given_name", social_login_request.getGivenname());
            obj.put("family_name", social_login_request.getFamilyname());
            obj.put("id", social_login_request.getId());
            obj.put("device_id", social_login_request.getDevice_id());
            obj.put("push_token", social_login_request.getPush_token());



        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("-object--", " " + obj);

        JsonObjectRequest loginrequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());

                        int code = -1;
                        try {
                            Log.e("response code"," "+response.getInt("code"));
                            code = response.getInt("code");
                            if(code != -1)
                            {
                                CommonFunctions.hDialog();
                                CommonFunctions.displayToast(context,response.getString("message"));
                            }
                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Login_Response login_response = gson.fromJson(response.toString(),Login_Response.class);

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(m_config.login_access_token, login_response.getAccess_token());
                        editor.putString(m_config.login_refresh_token, login_response.getRefresh_token());
                        editor.putString(m_config.login_fullname, login_response.getFullname());
                        editor.putString(m_config.login_email, login_response.getEmail());
                        editor.putInt(m_config.login_id, login_response.getId());
                        editor.putString(m_config.login_display_pic, login_response.getDisplay_pic());
                        editor.putBoolean("login_done",true);
                        editor.commit();
                        CommonFunctions.hDialog();


                        if(login_response.getOnboarding_process() == 4)
                        {
                            editor.putBoolean("onboard_process", true);
                            editor.putBoolean("profile_video", true);
                            editor.commit();
                            // call to home page
                            CommonFunctions.displayToast(context,"You have finished onboarding process");
                            Intent int_home_page = new Intent(context, HomeActivity.class);
                            int_home_page.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(int_home_page);
                            ((Activity)context).finish();
                        }
                        else
                        {
                            if(login_response.getVideos() == null)
                            {
                                Log.i("on profile_video in-complete","");
                                // call to record video
                                if(CommonFunctions.checkCameraHardware(context))
                                {
                                    Intent int_video_record = new Intent(context, VideoRecordingActivity.class);
                                    int_video_record.putExtra("from","onboarding");
                                    int_video_record.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(int_video_record);
                                    ((Activity)context).finish();

                                }

                            }
                            else
                            {
                                Log.i("on profile_video complete","");
                                Log.i("on boarding in-complete","");
                                editor.putBoolean("profile_video", true);
                                editor.commit();
                                // call to onboard screen
                                Intent int_onboarding  = new Intent(context, OnBoardingActivity.class);
                                int_onboarding.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(int_onboarding);
                                ((Activity)context).finish();

                            }
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if(error.networkResponse != null)
                        {
                            String networkresponse_data = new String(error.networkResponse.data);
                            if(networkresponse_data != null)
                            {
                                JSONObject network_obj;
                                try
                                {
                                    network_obj = new JSONObject(networkresponse_data);
                                    CommonFunctions.hDialog();
                                    CommonFunctions.displayToast(context,network_obj.getString("message"));
                                }
                                catch (JSONException e)
                                {
                                    CommonFunctions.hDialog();
                                    CommonFunctions.displayToast(context,context.getResources().getString(R.string.Login_failed));
                                }

                            }
                            else
                            {
                                CommonFunctions.hDialog();
                                CommonFunctions.displayToast(context,context.getResources().getString(R.string.Login_failed));
                            }
                        } else
                        {
                            CommonFunctions.hDialog();
                            CommonFunctions.displayToast(context,context.getResources().getString(R.string.Login_failed));
                        }
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("version", Constcore.version);
                params.put("devicetype","android");
                params.put("timezone",CommonFunctions.getTimeZoneIST());
                params.put("timesstamp",CommonFunctions.getTimeStamp());
                params.put("os_version",m_config.os_version);
                return params;
            }
        };

        // add it to the RequestQueue
        int socketTimeout = 10000;//60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        loginrequest.setRetryPolicy(policy);
        //queue.add(loginrequest);

        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(loginrequest, "social_login_api");

    }

}



