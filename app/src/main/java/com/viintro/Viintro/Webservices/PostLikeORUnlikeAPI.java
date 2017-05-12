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
import com.viintro.Viintro.HomeFeed.PostDetailsActivity;
import com.viintro.Viintro.HomeFeed.PostDetailsActivity_PlayVideo;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.viintro.Viintro.HomeFeed.PostDetailsActivity.post_details_updatelike;
import static com.viintro.Viintro.HomeFeed.PostDetailsActivity_PlayVideo.post_details_video_updatelike;

//import static com.viintro.Viintro.HomeFeed.PostDetailsActivity.like_count;

/**
 * Created by rkanawade on 18/03/17.
 */

public class PostLikeORUnlikeAPI {

    int like_count;

    //api call for post like or unlike
    public void req_post_likeunlike_API(final Context cont, String post_id, final String from_Activity) {

        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        final String bearertoken = sharedPreferences.getString(m_config.login_access_token,"");

        String url = m_config.web_api_link_header+ "/posts/like";

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("client_id", Constcore.client_Id);
            obj.put("client_secret", Constcore.client_Secret);
            obj.put("post_id", post_id);

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
                                condition("error", from_Activity);
                            }
                            else
                            {

                            }
                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        CommonFunctions.hDialog();
                        try
                        {
                            like_count = response.getInt("count");
                            condition("success", from_Activity);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        condition("error", from_Activity);
                        CommonFunctions.hDialog();

                        if(error.networkResponse != null)
                        {
                            String networkresponse_data = new String(error.networkResponse.data);
                            if(error.networkResponse.statusCode == 500)
                            {
                                CommonFunctions.displayToast(cont,cont.getResources().getString(R.string.internal_server_error));
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
                                            CommonFunctions.alertdialog_token(cont,"accesstokenexpired");
                                        }
                                        else if(network_obj.getInt("code") == 16)
                                        {
                                            // Alert Dialog for refresh token expired
                                            CommonFunctions.alertdialog_token(cont,"refreshtokenexpired");
                                        }
                                        else
                                        {
                                        }

                                    }
                                    catch (JSONException e)
                                    {
                                        CommonFunctions.hDialog();

                                    }

                                }

                            }
                        } else
                        {


                        }


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
        //   queue.add(getRequest);
        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(getRequest, "post_likeorunlike_api");

    }

    public void condition(String api_response, String from_Activity)
    {
        if(from_Activity.equals("Post_Details"))
        {
            post_details_updatelike(api_response, like_count);
        }
        else if(from_Activity.equals("Post_Details_Video"))
        {
            Log.e("came here","- "+like_count);
            post_details_video_updatelike(api_response, like_count);
        }

    }

}
