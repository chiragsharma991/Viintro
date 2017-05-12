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
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Model.MyProfile_Response;
import com.viintro.Viintro.Model.ProfileIntro_Video_Request;
import com.viintro.Viintro.Model.Videos;
import com.viintro.Viintro.OnBoarding.OnBoardingActivity;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

import static com.viintro.Viintro.Constants.Configuration_Parameter.profile_video_list;
import static com.viintro.Viintro.MyProfile.MyProfileFragment.profile_videos_adapter;
import static com.viintro.Viintro.VideoRecord.VideoRecordingActivity.videorecording_activity;
import static life.knowledge4.videotrimmer.K4LVideoTrimmer.hDialog_Upload;
import static life.knowledge4.videotrimmer.K4LVideoTrimmer.showDialog_Upload;

/**
 * Created by hasai on 14/02/17.
 */

public class Profile_Video_API
{

    // API Call for profile video
    public static void req_profilevideo_API(final Context cont, ProfileIntro_Video_Request profileVideo_request)
    {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);


        final Gson gson = new Gson();
        String url = m_config.web_api_link+ "/profile/video";

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("client_id", profileVideo_request.getClient_id());
            obj.put("client_secret", profileVideo_request.getClient_secret());
            obj.put("public_id", profileVideo_request.getPublic_id());
            obj.put("created_at", profileVideo_request.getCreated_at());
            obj.put("secure_url_mpd", profileVideo_request.getSecure_url_mpd());
            obj.put("secure_url_hls", profileVideo_request.getSecure_url_hls());
            obj.put("secure_url_mp4", profileVideo_request.getSecure_url_mp4());
            obj.put("thumbnail", profileVideo_request.getThumbnail());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        //Log.e("-object--"," "+obj);

        // prepare the Request
        JsonObjectRequest profilevideorequest = new JsonObjectRequest(Request.Method.POST, url, obj,
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
                                ((Activity)context).finish();
                                if(videorecording_activity != null)
                                    videorecording_activity.finish();
                            }
                            return;
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }


                        Videos videos = gson.fromJson(response.toString(), Videos.class);
                        if(videos.getIntro() == false)
                        {

                            CommonFunctions.displayToast(context,"Video uploaded successfully");
                            profile_video_list.add(videos);
                            profile_videos_adapter.notifyDataSetChanged();
//                            Intent resultIntent = new Intent();
//                            videorecording_activity.setResult(Activity.RESULT_OK, resultIntent);
                            CommonFunctions.hDialog();
                            if(showDialog_Upload(context) != null)
                            {
                                showDialog_Upload(context).setProgress(100);
                            }
                            hDialog_Upload();
                            ((Activity)context).finish();
                            if(videorecording_activity != null)
                            videorecording_activity.finish();
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
                            if(networkresponse_data != null)
                            {
                                JSONObject network_obj;
                                try
                                {
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


                                }
                                catch (JSONException e)
                                {

                                    CommonFunctions.displayToast(context,context.getResources().getString(R.string.video_upload_failed));

                                }

                            }
                            }

                            ((Activity)context).finish();
                            if(videorecording_activity != null)
                                videorecording_activity.finish();
                        } else
                        {
                            CommonFunctions.displayToast(context,context.getResources().getString(R.string.video_upload_failed));
                            ((Activity)context).finish();
                            if(videorecording_activity != null)
                                videorecording_activity.finish();
                        }


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
        profilevideorequest.setRetryPolicy(policy);

        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(profilevideorequest, "profile_video_api");



    }


}
