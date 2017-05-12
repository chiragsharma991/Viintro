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
import com.facebook.login.LoginManager;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Login.LoginActivity;
import com.viintro.Viintro.MyProfile.MyFollowersActivity;
import com.viintro.Viintro.MyProfile.MyFollowingsActivity;
import com.viintro.Viintro.MyProfile.MyProfileFragment;
import com.viintro.Viintro.MyProfile.WhoViewedProfileActivity;
import com.viintro.Viintro.PublicProfile.PublicProfileActivity;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.viintro.Viintro.Reusables.CommonFunctions.clrAllFlags;

/**
 * Created by rkanawade on 15/04/17.
 */

public class LogoutAPI {

    //api call to logout from the app
    public static void req_logout_API(final Context cont)
    {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String bearertoken = sharedPreferences.getString(m_config.login_access_token,"");

        String url = m_config.web_api_link+ "/logout";
        Log.d("url", url);

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url,
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("","inside logout success");
                        CommonFunctions.hDialog();
                        clrAllFlags(context);
                       // LoginManager.getInstance().logOut();
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        ((Activity)context).finish();

                       // LogoutAPI.req_logout_API(context);




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

                                        CommonFunctions.displayToast(context, context.getResources().getString(R.string.Logout_request_failed));
                                    }

                                }
                            }
                        } else
                        {

                            CommonFunctions.displayToast(context,context.getResources().getString(R.string.Logout_request_failed));
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
        ViintroApplication.getInstance().addToRequestQueue(getRequest, "logout_api");



    }

}
