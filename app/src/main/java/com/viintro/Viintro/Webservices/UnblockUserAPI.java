package com.viintro.Viintro.Webservices;

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
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.BlockUser_Request;
import com.viintro.Viintro.Model.UnBlockUser_Request;
import com.viintro.Viintro.PublicProfile.PublicProfileActivity;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rkanawade on 08/04/17.
 */

public class UnblockUserAPI {

    //api to unblock user
    public static void req_unblock_user_API(final Context cont, UnBlockUser_Request unBlockUser_request, final int position, final String from_Activity)
    {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String bearertoken = sharedPreferences.getString(m_config.login_access_token,"");

        String url = m_config.web_api_link+ "/unblock";

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("client_id", Constcore.client_Id);
            obj.put("client_secret", Constcore.client_Secret);
            obj.put("user_id", unBlockUser_request.getUser_id());


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

                        CommonFunctions.hDialog();
                        try {
                            condition(context, from_Activity, position, response.getString("message"));
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

                                        CommonFunctions.displayToast(context, context.getResources().getString(R.string.Unblock_user_failed));
                                    }

                                }
                            }
                        } else
                        {

                            CommonFunctions.displayToast(context,context.getResources().getString(R.string.Unblock_user_failed));
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
        //queue.add(getRequest);

        // Adding request to request queue

        ViintroApplication.getInstance().addToRequestQueue(getRequest, "unblock_user_api");


    }

    private static void condition(Context context, String from_Activity, int position, String unblock_messsage) {
        if(from_Activity.equals("Public_Profile"))
        {
            PublicProfileActivity publicProfileActivity = (PublicProfileActivity) context;
            publicProfileActivity.update_menu_title(position,"unblock",unblock_messsage);
        }
    }
}
