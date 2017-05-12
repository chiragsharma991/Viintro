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
import com.viintro.Viintro.Login.RegistrationActivity;
import com.viintro.Viintro.Model.SignUp_Request;
import com.viintro.Viintro.Model.SignUp_Response;
import com.viintro.Viintro.OnBoarding.OnBoardingActivity;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.VideoRecord.VideoRecordingActivity;
import com.viintro.Viintro.ViintroApplication;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import static com.viintro.Viintro.Login.RegistrationActivity.btn_show_pw;

/**
 * Created by hasai on 04/02/17.
 */

public class RegisterAPI {

    // API Call for registration
    public static void req_register_API(Context cont, SignUp_Request signUp_request) {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue((Activity) context);
        final Gson gson = new Gson();

        String url = m_config.web_api_link+ "/signup";


        JSONObject obj = new JSONObject();
        try
        {
            obj.put("client_id", signUp_request.getClient_id());
            obj.put("client_secret", signUp_request.getClient_secret());
            obj.put("fullname", signUp_request.getFullname());
            obj.put("email", signUp_request.getEmail());
            obj.put("password", signUp_request.getPassword());
            obj.put("device_id", signUp_request.getDevice_id());
            obj.put("push_token", signUp_request.getPush_token());


        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        Log.e("-object--"," "+obj);

        // prepare the Request
        JsonObjectRequest signuprequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        //Log.d("Response", response.toString());

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

                        Log.e("-res--"," "+response.toString());

                        SignUp_Response signUp_response = gson.fromJson(response.toString(),SignUp_Response.class);
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(m_config.login_access_token, signUp_response.getAccess_token());
                        editor.putString(m_config.login_refresh_token, signUp_response.getRefresh_token());
                        editor.putString(m_config.login_fullname, signUp_response.getFullname());
                        editor.putString(m_config.login_email, signUp_response.getEmail());
                        editor.putInt(m_config.login_id, signUp_response.getId());
                        editor.putString(m_config.login_display_pic, signUp_response.getDisplay_pic());
                        editor.putBoolean("login_done",true);
                        editor.commit();
                        CommonFunctions.hDialog();

                        if(signUp_response.getOnboarding_process() == 4)
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
                            if(signUp_response.getVideos() == null)
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

                                editor.putBoolean("profile_video", true);
                                editor.commit();
                                // call to onboard screen
                                Intent int_onboarding  = new Intent(context, OnBoardingActivity.class);
                                int_onboarding.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(int_onboarding);
                                ((Activity)context).finish();
                                //RegistrationActivity.registration_activity.finish();
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
                            String networkresponse_data = new String(error.networkResponse.data);
                            if(error.networkResponse.statusCode == 500)
                            {
                                CommonFunctions.displayToast(context,context.getResources().getString(R.string.internal_server_error));
                            }
                            else {
                                if (networkresponse_data != null) {
                                    JSONObject network_obj;
                                    try {
                                        network_obj = new JSONObject(networkresponse_data);
                                        CommonFunctions.hDialog();
                                        CommonFunctions.displayToast(context, network_obj.getString("message"));


                                        if (network_obj.getInt("code") == 4) {
//                                            btn_show_pw.setVisibility(View.VISIBLE);
                                        }
                                    } catch (JSONException e) {
                                        CommonFunctions.hDialog();
                                        CommonFunctions.displayToast(context, context.getResources().getString(R.string.Login_failed));
                                    }

                                }
                            }
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
        signuprequest.setRetryPolicy(policy);

        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(signuprequest, "register_api");

    }

}
