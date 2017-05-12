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
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Model.Connection_Req_Acc_Rej_Rem;
import com.viintro.Viintro.MyProfile.MyFollowersActivity;
import com.viintro.Viintro.MyProfile.MyFollowingsActivity;
import com.viintro.Viintro.MyProfile.MyFollowingsAdapter;
import com.viintro.Viintro.MyProfile.WhoViewedProfileActivity;
import com.viintro.Viintro.PublicProfile.PublicProfileActivity;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.VideoRecord.VideoRecordingActivity;
import com.viintro.Viintro.ViintroApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.viintro.Viintro.Connections.SuggestionAdapter.upd_row_in_sugg_page;
import static life.knowledge4.videotrimmer.K4LVideoTrimmer.hDialog_Upload;
import static life.knowledge4.videotrimmer.K4LVideoTrimmer.showDialog_Upload;


/**
 * Created by rkanawade on 03/03/17.
 */

public class ConnectionRequestAPI {

    //api call for connection request
    public void req_connection_API(Context cont, final Connection_Req_Acc_Rej_Rem connection_request, final int position, final String from_Activity)
    {
        final Boolean[] success = {false};
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String bearertoken = sharedPreferences.getString(m_config.login_access_token,"");


        // Instantiate the RequestQueue.
        String url = m_config.web_api_link_header+ "/connection/request";

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("client_id", connection_request.getClient_id());
            obj.put("client_secret", connection_request.getClient_secret());
            obj.put("user_id", connection_request.getUser_id());
            obj.put("message",connection_request.getMessage());
            obj.put("public_id", connection_request.getPublic_id());
            obj.put("created_at", connection_request.getCreated_at());
            obj.put("secure_url_mpd", connection_request.getSecure_url_mpd());
            obj.put("secure_url_hls",connection_request.getSecure_url_hls());
            obj.put("secure_url_mp4", connection_request.getSecure_url_mp4());
            obj.put("thumbnail",connection_request.getThumbnail());


        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
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
                                ((Activity) context).finish();
                                if(VideoRecordingActivity.videorecording_activity != null)
                                {
                                    VideoRecordingActivity.videorecording_activity.finish();
                                }

                            }
                            return;
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                        CommonFunctions.hDialog();
                        try {
                            CommonFunctions.displayToast(context,response.getString("message"));

                            if(response.getString("message").equals("Connection Request Sent"))
                            {
                                condition(context, from_Activity, position);

                            }


                        }
                        catch (JSONException e)
                        {
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

                            String networkresponse_data = new String(error.networkResponse.data);
                            if(error.networkResponse.statusCode == 500)
                            {
                                CommonFunctions.displayToast(context,context.getResources().getString(R.string.internal_server_error));
                                ((Activity) context).finish();
                                if (VideoRecordingActivity.videorecording_activity != null) {
                                    VideoRecordingActivity.videorecording_activity.finish();
                                }

                            }
                            else {

                                if (networkresponse_data != null) {
                                    JSONObject network_obj;
                                    try {
                                        network_obj = new JSONObject(networkresponse_data);
                                        CommonFunctions.hDialog();
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
                                        CommonFunctions.hDialog();
                                        CommonFunctions.displayToast(context, context.getResources().getString(R.string.Connection_request_failed));
                                    }

                                }

                                ((Activity) context).finish();
                                if (VideoRecordingActivity.videorecording_activity != null) {
                                    VideoRecordingActivity.videorecording_activity.finish();
                                }
                            }

                        }
                        else
                        {
                            ((Activity) context).finish();
                            if(VideoRecordingActivity.videorecording_activity != null)
                            {
                                VideoRecordingActivity.videorecording_activity.finish();
                            }
                            CommonFunctions.hDialog();
                            CommonFunctions.displayToast(context,context.getResources().getString(R.string.Connection_request_failed));
                        }


                        ((Activity)context).finish();

                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("version","1");
                params.put("devicetype","android");
                params.put("timezone:IST","IST");
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

        ViintroApplication.getInstance().addToRequestQueue(getRequest, "connect_video_api");

    }

    private void condition(Context context, String from_Activity, int position)
    {
        if(from_Activity.equals("MyFollowers"))
        {
            MyFollowersActivity followersActivity = new MyFollowersActivity();
            followersActivity.onRowUpdate(position, "connect");
            if(showDialog_Upload(context) != null) {
                showDialog_Upload(context).setProgress(100);
            }
            hDialog_Upload();
            ((Activity) context).finish();
            if(VideoRecordingActivity.videorecording_activity != null)
            {
                VideoRecordingActivity.videorecording_activity.finish();
            }
        }
        else if(from_Activity.equals("MyFollowings"))
        {
            MyFollowingsActivity followingsActivity = new MyFollowingsActivity();
            followingsActivity.onRowUpdate(position, "connect");
            if(showDialog_Upload(context) != null) {
                showDialog_Upload(context).setProgress(100);
            }
            hDialog_Upload();
            ((Activity) context).finish();
            if(VideoRecordingActivity.videorecording_activity != null)
            {
                VideoRecordingActivity.videorecording_activity.finish();
            }
        }
        else if(from_Activity.equals("Public_Profile"))
        {

            PublicProfileActivity publicProfileActivity = new PublicProfileActivity();
            publicProfileActivity.upd_btn_in_pub_prof_after_conn_follow_unfollow(position,"connect");
            if(showDialog_Upload(context) != null) {
                showDialog_Upload(context).setProgress(100);
            }
            hDialog_Upload();
            ((Activity) context).finish();
            if(VideoRecordingActivity.videorecording_activity != null)
            {
                VideoRecordingActivity.videorecording_activity.finish();
            }
        }
        else if(from_Activity.equals("My_Suggestions"))
        {
            upd_row_in_sugg_page(position, "connect");
            if(showDialog_Upload(context) != null)
            {
                showDialog_Upload(context).setProgress(100);
            }
            hDialog_Upload();
            ((Activity) context).finish();
            if(VideoRecordingActivity.videorecording_activity != null)
            {
                VideoRecordingActivity.videorecording_activity.finish();
            }
        }
        else if(from_Activity.equals("WhoViewed"))
        {
            WhoViewedProfileActivity whoViewedProfileActivity = new WhoViewedProfileActivity();
            whoViewedProfileActivity.onRowUpdate(position, "connect");
            if(showDialog_Upload(context) != null)
            {
                showDialog_Upload(context).setProgress(100);
            }
            hDialog_Upload();
            ((Activity) context).finish();
            if(VideoRecordingActivity.videorecording_activity != null)
            {
                VideoRecordingActivity.videorecording_activity.finish();
            }
        }
    }


}
