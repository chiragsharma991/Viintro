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
import com.viintro.Viintro.Model.Follow_Request;
import com.viintro.Viintro.Model.PostReport_Request;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.viintro.Viintro.Search_Post.PostAdapter.search_post_unfollowuser_hide_report_post;

/**
 * Created by rkanawade on 18/03/17.
 */

public class PostReportAPI {

    //api call for post report
    public static void req_post_report_API(Context cont, PostReport_Request postReport_request, final int position, final String from_Activity) {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String bearertoken = sharedPreferences.getString(m_config.login_access_token,"");

        // Instantiate the RequestQueue.
     //   RequestQueue queue = Volley.newRequestQueue((Activity) context);
        String url = m_config.web_api_link_header+ "/posts/report";

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("client_id", postReport_request.getClient_id());
            obj.put("client_secret", postReport_request.getClient_secret());
            obj.put("post_id", postReport_request.getPost_id());
            obj.put("message_id", postReport_request.getMessage_id());


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
                            }

                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            CommonFunctions.displayToast(context,response.getString("message"));
                            if(from_Activity.equals("Post_Search"))
                            {
                                search_post_unfollowuser_hide_report_post(position);
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
                                    CommonFunctions.displayToast(context,context.getResources().getString(R.string.Post_report_failed));
                                }

                            }
                            }
                        } else
                        {
                            CommonFunctions.hDialog();
                            CommonFunctions.displayToast(context,context.getResources().getString(R.string.Post_report_failed));
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
      //  queue.add(getRequest);
        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(getRequest, "post_report_api");

    }
}
