package com.viintro.Viintro.Webservices;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

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
import com.viintro.Viintro.Model.Onboarding_Request;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rkanawade on 15/02/17.
 */

public class OnboardingAPI {


    // API Call for onboarding
    public static void req_onboarding_API(final Context cont, Onboarding_Request onboarding_request)
    {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String bearertoken = sharedPreferences.getString(m_config.login_access_token,"");

        String url = m_config.web_api_link+ "/onboard-process";

        JSONObject obj = new JSONObject();

        try
        {
            obj.put("client_id", onboarding_request.getClient_id());
            obj.put("client_secret", onboarding_request.getClient_secret());
            obj.put("user_type", onboarding_request.getUser_type());
            obj.put("university", onboarding_request.getUniversity());
            obj.put("course", onboarding_request.getCourse());
            obj.put("country_code", onboarding_request.getCountry_code());
            obj.put("mobile_number", onboarding_request.getMobile_number());
            obj.put("company", onboarding_request.getCompany());
            obj.put("designation", onboarding_request.getDesignation());
            obj.put("city", onboarding_request.getCity());
            obj.put("state", onboarding_request.getState());
            obj.put("country", onboarding_request.getCountry());
            obj.put("latitude", onboarding_request.getLatitude());
            obj.put("longitude", onboarding_request.getLongitude());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        Log.e("-object--"," "+obj);

        // prepare the Request
        JsonObjectRequest on_boarding = new JsonObjectRequest(Request.Method.POST, url, obj,
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

                        CommonFunctions.hDialog();
                        try {
                            CommonFunctions.displayToast(context, response.getString("message"));
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("onboard_process", true);
                            editor.commit();
                            Intent int_homepage = new Intent(context, HomeActivity.class);
                            int_homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(int_homepage);
                            ((Activity)context).finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
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

                                }

                            }
                        } else
                        {
                            CommonFunctions.hDialog();
                            CommonFunctions.displayToast(context,context.getResources().getString(R.string.Login_failed));
                        }


                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("version", Constcore.version);
                params.put("devicetype","android");
                params.put("timezone",CommonFunctions.getTimeZoneIST());
                params.put("Authorization","Bearer "+bearertoken);
                params.put("timesstamp",CommonFunctions.getTimeStamp());
                params.put("os_version",m_config.os_version);


                return params;
            }
        };



        // add it to the RequestQueue
        int socketTimeout = 10000;//60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        on_boarding.setRetryPolicy(policy);

        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(on_boarding, "on_boarding_api");


    }


}
