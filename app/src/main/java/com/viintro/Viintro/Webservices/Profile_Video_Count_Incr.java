package com.viintro.Viintro.Webservices;

import android.app.Activity;
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
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Login.ResendLinkActivity;
import com.viintro.Viintro.Model.ForgotPassword_Request;
import com.viintro.Viintro.Model.Video;
import com.viintro.Viintro.Model.Videos;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.viintro.Viintro.Constants.Configuration_Parameter.profile_video_list;
import static com.viintro.Viintro.MyProfile.MyProfileFragment.profile_videos_adapter;

/**
 * Created by hasai on 04/04/17.
 */

public class Profile_Video_Count_Incr {

    // API Call for increment profile video count
    public static void req_profile_video_count_incr(final Context cont, final String public_id) {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String url = m_config.web_api_link+ "/profile-video/view_count";

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("client_id", Constcore.client_Id);
            obj.put("client_secret", Constcore.client_Secret);
            obj.put("public_id", public_id);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


        // prepare the Request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        if(profile_video_list != null)
                        {
                            for(int i = 0; i < profile_video_list.size(); i++)
                            {
                                if(profile_video_list.get(i).getPublic_id().equals(public_id))
                                {
                                    Videos videos = profile_video_list.get(i);
                                    int newcount = videos.getView_count() + 1;
                                    videos.setView_count(newcount);
                                    profile_video_list.set(i,videos);
                                    profile_videos_adapter.notifyDataSetChanged();
                                }
                            }
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("version","1");
                params.put("devicetype","android");
                params.put("timezone",CommonFunctions.getTimeZoneIST());
                params.put("Authorization","Bearer "+sharedPreferences.getString(m_config.login_access_token,""));
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
        ViintroApplication.getInstance().addToRequestQueue(request, "profile_video_count_incr");



    }

}
