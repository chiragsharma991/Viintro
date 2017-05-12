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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Login.ResendLinkActivity;
import com.viintro.Viintro.Model.ForgotPassword_Request;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by hasai on 09/02/17.
 */

public class ForgotPasswordAPI {


    // API Call for forgot password
    public static void req_forgotpw_API(final Context cont, final ForgotPassword_Request forgotPassword_request) {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String url = m_config.web_api_link+ "/forgot-password";

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("client_id", forgotPassword_request.getClient_id());
            obj.put("client_secret", forgotPassword_request.getClient_secret());
            obj.put("email", forgotPassword_request.getEmail());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        Log.e("-object--"," "+obj);

        // prepare the Request
        JsonObjectRequest forgotpwrequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        //Log.d("Response", response.toString());

                        // for error response
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

                        // for succes response
                        if(context instanceof ResendLinkActivity)
                        {
                            CommonFunctions.hDialog();
                            try
                            {
                                CommonFunctions.displayToast(context,response.getString("message"));
                            } catch (JSONException e) {
                            }
                            //resendlink_activity.finish();
                            ((Activity)context).finish();
                        }
                        else
                        {
                            Intent int_resendLink = new Intent(context, ResendLinkActivity.class);
                            int_resendLink.putExtra("email",forgotPassword_request.getEmail());
                            context.startActivity(int_resendLink);
                            CommonFunctions.hDialog();
                            //forgotpw_activity.finish();
                            ((Activity)context).finish();
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


                                    } catch (JSONException e)
                                    {
                                        CommonFunctions.displayToast(context, context.getResources().getString(R.string.forgot_pw));
                                    }

                                }
                            }
                        } else
                        {
                            CommonFunctions.displayToast(context,context.getResources().getString(R.string.forgot_pw));
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
                params.put("timesstamp",CommonFunctions.getTimeStamp());
                params.put("os_version",m_config.os_version);
                return params;
            }
        };

        // add it to the RequestQueue
        int socketTimeout = 10000;//60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        forgotpwrequest.setRetryPolicy(policy);
        //queue.add(forgotpwrequest);

        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(forgotpwrequest, "forgot_pw_api");



    }

}
