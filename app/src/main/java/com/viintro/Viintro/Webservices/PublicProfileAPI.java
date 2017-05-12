package com.viintro.Viintro.Webservices;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.viintro.Viintro.Model.MyProfile_Response;
import com.viintro.Viintro.Model.PublicProfile_Response;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rkanawade on 09/03/17.
 */

public class PublicProfileAPI {


    // API Call for Public profile
    public static void req_publicprofile_API(final Context cont, int user_id)
    {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String bearertoken = sharedPreferences.getString(m_config.login_access_token,"");

        final Gson gson = new Gson();
        String url = m_config.web_api_link + "/profile-id/"+user_id;
        Log.d("url", url.toString());
        // prepare the Request
        JsonObjectRequest profilerequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        int code = -1;
                        try {
                            Log.e("response code", " " + response.getInt("code"));
                            code = response.getInt("code");
                            if (code != -1) {
                                CommonFunctions.hDialog();
                                CommonFunctions.displayToast(context, response.getString("message"));
                            }
                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        PublicProfile_Response publicProfile_response = gson.fromJson(response.toString(), PublicProfile_Response.class);
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(m_config.Public_profile_id, publicProfile_response.getId());
                        editor.putString(m_config.Public_profile_fullname, publicProfile_response.getFullname());
                        editor.putString(m_config.Public_profile_university, publicProfile_response.getUniversity());
                        editor.putString(m_config.Public_profile_course, publicProfile_response.getCourse());
                        editor.putString(m_config.Public_profile_company, publicProfile_response.getCompany());
                        editor.putString(m_config.Public_profile_designation, publicProfile_response.getDesignation());
                        editor.putString(m_config.Public_profile_city, publicProfile_response.getCity());
                        editor.putString(m_config.Public_profile_state, publicProfile_response.getState());
                        editor.putString(m_config.Public_profile_country, publicProfile_response.getCountry());
                        editor.putInt("connection_count", publicProfile_response.getConnection_count());
                        editor.putString(m_config.Public_profile_displaypic, publicProfile_response.getDisplay_pic());


                        editor.commit();
                        CommonFunctions.hDialog();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        CommonFunctions.hDialog();
                        if (error.networkResponse != null) {
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

                                    } catch (JSONException e) {

                                        CommonFunctions.displayToast(context, context.getResources().getString(R.string.PublicProfile_failed));
                                    }

                                }
                            }
                        } else {

                            CommonFunctions.displayToast(context, context.getResources().getString(R.string.PublicProfile_failed));
                        }


                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("version", Constcore.version);
                params.put("devicetype", "android");
                params.put("timezone:IST", CommonFunctions.getTimeZoneIST());
                params.put("Authorization","Bearer "+bearertoken);
                params.put("timesstamp",CommonFunctions.getTimeStamp());
                params.put("os_version",m_config.os_version);
                return params;
            }
        };

        // add it to the RequestQueue
        int socketTimeout = 10000;//60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        profilerequest.setRetryPolicy(policy);


        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(profilerequest, "public_profile_api");



    }
}
