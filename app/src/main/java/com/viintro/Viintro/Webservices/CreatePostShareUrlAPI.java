package com.viintro.Viintro.Webservices;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.Colleges;
import com.viintro.Viintro.Model.Companies;
import com.viintro.Viintro.Model.Designations;
import com.viintro.Viintro.Model.MyProfile_Response;
import com.viintro.Viintro.Model.Others;
import com.viintro.Viintro.Model.Qualifications;
import com.viintro.Viintro.Model.Schools;
import com.viintro.Viintro.Model.Skills;
import com.viintro.Viintro.Model.Videos;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.viintro.Viintro.Constants.Configuration_Parameter.intro_video_myprofile;
import static com.viintro.Viintro.Constants.Configuration_Parameter.profile_colleges_list;
import static com.viintro.Viintro.Constants.Configuration_Parameter.profile_companies_list;
import static com.viintro.Viintro.Constants.Configuration_Parameter.profile_designations_list;
import static com.viintro.Viintro.Constants.Configuration_Parameter.profile_others_list;
import static com.viintro.Viintro.Constants.Configuration_Parameter.profile_qualifications_list;
import static com.viintro.Viintro.Constants.Configuration_Parameter.profile_schools_list;
import static com.viintro.Viintro.Constants.Configuration_Parameter.profile_skills_list;
import static com.viintro.Viintro.Constants.Configuration_Parameter.profile_video_list;

/**
 * Created by rkanawade on 17/04/17.
 */

public class CreatePostShareUrlAPI {

    public static void req_postShareUrl_API(final Context cont, String post_id)
    {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String bearertoken = sharedPreferences.getString(m_config.login_access_token,"");


        final Gson gson = new Gson();
        String url = m_config.web_api_link_header + "/shareurl?post_id="+post_id;
        Log.d("url", url.toString());
        // prepare the Request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
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

                        try
                        {
                            if(response.getString("url") != null)
                            {
                                String shareBody = response.getString("url");
                                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                sharingIntent.setType("text/plain");
                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


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
                            else
                            {
                                if (networkresponse_data != null)
                                {
                                    JSONObject network_obj;
                                    try {
                                        network_obj = new JSONObject(networkresponse_data);

                                        if (network_obj.getInt("code") == 12) {
                                            // Alert Dialog for access token expired
                                            CommonFunctions.alertdialog_token(context, "accesstokenexpired");
                                        } else if (network_obj.getInt("code") == 16) {
                                            // Alert Dialog for refresh token expired
                                            CommonFunctions.alertdialog_token(context, "refreshtokenexpired");
                                        } else {
                                            CommonFunctions.displayToast(context, network_obj.getString("message"));
                                        }
                                    } catch (JSONException e)
                                    {

                                    }

                                }
                            }
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
        request.setRetryPolicy(policy);

        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(request, "create_postshare_url_api");

    }
}
