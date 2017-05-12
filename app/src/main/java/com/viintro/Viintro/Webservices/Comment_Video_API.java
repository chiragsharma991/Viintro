package com.viintro.Viintro.Webservices;

import android.app.Activity;
import android.content.Context;
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
import com.viintro.Viintro.HomeFeed.PostDetailsActivity;
import com.viintro.Viintro.HomeFeed.PostDetailsActivity_PlayVideo;
import com.viintro.Viintro.Model.Comment_Video_Request;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.viintro.Viintro.HomeFeed.PostDetailsActivity.post_details_updatecomment;
import static com.viintro.Viintro.HomeFeed.PostDetailsActivity_PlayVideo.post_details_video_updatecomment;
import static com.viintro.Viintro.VideoRecord.VideoRecordingActivity.videorecording_activity;
import static life.knowledge4.videotrimmer.K4LVideoTrimmer.hDialog_Upload;
import static life.knowledge4.videotrimmer.K4LVideoTrimmer.showDialog_Upload;

/**
 * Created by hasai on 14/02/17.
 */

public class Comment_Video_API
{
    int comment_count;

    // API Call for comment video
    public void req_commentvideo_API(final Context cont, Comment_Video_Request comment_video_request, String post_id, final String from_Activity)
    {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);


        final Gson gson = new Gson();
        String url = m_config.web_api_link_header+ "/posts/comment/"+post_id;

        JSONObject obj = new JSONObject();
        JSONObject obj1 = new JSONObject();
        try
        {
            obj.put("client_id", comment_video_request.getClient_id());
            obj.put("client_secret", comment_video_request.getClient_secret());
            obj.put("comment_data", comment_video_request.getComment_data());

            obj1.put("type", comment_video_request.getVideo().getType());
            obj1.put("source_default", comment_video_request.getVideo().getSource_default());
            obj1.put("source_hls", comment_video_request.getVideo().getSource_hls());
            obj1.put("source_mpd", comment_video_request.getVideo().getSource_mpd());
            obj1.put("thumbnail", comment_video_request.getVideo().getThumbnail());
            obj1.put("public_id", comment_video_request.getVideo().getPublic_id());
            obj.put("video", obj1);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        Log.e("-object--"," "+obj);

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
                                condition("error", from_Activity);
                            }
                            return;
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                        CommonFunctions.hDialog();

                        try
                        {
                            comment_count = response.getInt("postComment_count");
                            condition("success", from_Activity);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        if(showDialog_Upload(context) != null)
                        {
                            showDialog_Upload(context).setProgress(100);
                        }
                        hDialog_Upload();
                        ((Activity)context).finish();
                        if(videorecording_activity != null)
                            videorecording_activity.finish();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                        CommonFunctions.hDialog();
                        condition("error", from_Activity);
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

    public void condition(String api_response, String from_Activity)
    {
        if(from_Activity.equals("Post_Details"))
        {
           post_details_updatecomment(api_response, comment_count);
        }
        else if(from_Activity.equals("Post_Details_Video"))
        {
            post_details_video_updatecomment(api_response, comment_count);
        }

    }


}
