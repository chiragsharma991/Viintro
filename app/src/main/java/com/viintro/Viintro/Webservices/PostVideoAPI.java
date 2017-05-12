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
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.PostVideo_Request;
import com.viintro.Viintro.Model.Youtube_Request;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.viintro.Viintro.VideoRecord.VideoRecordingActivity.videorecording_activity;
import static life.knowledge4.videotrimmer.K4LVideoTrimmer.hDialog_Upload;
import static life.knowledge4.videotrimmer.K4LVideoTrimmer.showDialog_Upload;

/**
 * Created by hasai on 23/02/17.
 */

public class PostVideoAPI {

    // Call to upload youtubelink
    public static void req_post_video_api(Context cont, PostVideo_Request postVideo_request)
    {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String bearertoken = sharedPreferences.getString(m_config.login_access_token,"");

        String url = m_config.web_api_link_header+ "/posts";

        JSONObject obj = new JSONObject();
        JSONObject obj1 = new JSONObject();
        try
        {
            obj.put("client_id", postVideo_request.getClient_id());
            obj.put("client_secret", postVideo_request.getClient_secret());
            obj.put("description", postVideo_request.getDescription());
            obj1.put("type",postVideo_request.getVideo().getType());
            obj1.put("source_default",postVideo_request.getVideo().getSource_default());
            obj1.put("source_hls",postVideo_request.getVideo().getSource_hls());
            obj1.put("source_mpd",postVideo_request.getVideo().getSource_mpd());
            obj1.put("thumbnail",postVideo_request.getVideo().getThumbnail());
            obj1.put("public_id",postVideo_request.getVideo().getPublic_id());
            obj.put("video", obj1);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        Log.e("-object--"," "+obj);

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.e("response "," "+response.toString());

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
                                {
                                    videorecording_activity.finish();
                                }
                            }
                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                        if(showDialog_Upload(context) != null)
                        {
                            showDialog_Upload(context).setProgress(100);
                        }
                        hDialog_Upload();
                        CommonFunctions.hDialog();
                        CommonFunctions.displayToast(context,"Video Uploaded");
                        ((Activity)context).finish();
                        if(videorecording_activity != null)
                        {
                            videorecording_activity.finish();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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
                                        if (network_obj.getInt("code") == 12) {
                                            // Alert Dialog for access token expired
                                            CommonFunctions.alertdialog_token(context, "accesstokenexpired");
                                        } else if (network_obj.getInt("code") == 16) {
                                            // Alert Dialog for refresh token expired
                                            CommonFunctions.alertdialog_token(context, "refreshtokenexpired");
                                        } else {
                                            CommonFunctions.displayToast(context, network_obj.getString("message"));
                                        }


                                    } catch (JSONException e) {

                                        CommonFunctions.displayToast(context, context.getResources().getString(R.string.video_upload_failed));

                                    }

                                }
                            }

                            ((Activity)context).finish();
                            if(videorecording_activity != null)
                            {
                                videorecording_activity.finish();
                            }

                        } else
                        {
                            
                            CommonFunctions.displayToast(context,context.getResources().getString(R.string.video_upload_failed));
                            ((Activity)context).finish();
                            if(videorecording_activity != null)
                            {
                                videorecording_activity.finish();
                            }
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
                params.put("Authorization","Bearer "+bearertoken);
                params.put("timesstamp",CommonFunctions.getTimeStamp());
                params.put("os_version",m_config.os_version);

                return params;
            }
        };

        // add it to the RequestQueue
        int socketTimeout = 10000;//60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        getRequest.setRetryPolicy(policy);

        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(getRequest, "post_video_api");


    }
}
