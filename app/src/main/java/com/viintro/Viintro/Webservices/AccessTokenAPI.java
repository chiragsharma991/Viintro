package com.viintro.Viintro.Webservices;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

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
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.Login_Request;
import com.viintro.Viintro.Model.Login_Response;
import com.viintro.Viintro.OnBoarding.OnBoardingActivity;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Reusables.Validations;
import com.viintro.Viintro.VideoRecord.VideoRecordingActivity;
import com.viintro.Viintro.ViintroApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.viintro.Viintro.Login.LoginActivity.btn_show_pw;

/**
 * Created by hasai on 23/02/17.
 */

public class AccessTokenAPI
{

    // Regenerate AccessToken
    public static void req_gen_accesstoken(final Context cont) {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);


        // Instantiate the RequestQueue.
        final Gson gson = new Gson();
        String url = m_config.web_api_link_header+ "/token/referesh";

        JSONObject obj = new JSONObject();

        try
        {
            obj.put("client_id", Constcore.client_Id);
            obj.put("client_secret", Constcore.client_Secret);
            obj.put("id", sharedPreferences.getString(m_config.login_id,""));
            obj.put("refresh_token",  sharedPreferences.getString(m_config.login_refresh_token,""));

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        Log.e("-object--"," "+obj);

        // prepare the Request
        JsonObjectRequest accesstokenrequest = new JsonObjectRequest(Request.Method.POST, url, obj,
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

                        Login_Response login_response = gson.fromJson(response.toString(),Login_Response.class);
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(m_config.login_access_token, login_response.getAccess_token());
                        editor.putString(m_config.login_refresh_token, login_response.getRefresh_token());
                        editor.putString(m_config.login_fullname, login_response.getFullname());
                        editor.putString(m_config.login_email, login_response.getEmail());
                        editor.putInt(m_config.login_id, login_response.getId());
                        editor.putString(m_config.login_display_pic, login_response.getDisplay_pic());
                        editor.commit();
                        CommonFunctions.hDialog();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                        CommonFunctions.hDialog();
                        if(error.networkResponse != null)
                        {
                            Log.e("error status code "," "+error.networkResponse.statusCode);

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
                                        CommonFunctions.displayToast(context,network_obj.getString("message"));

                                    }
                                    catch (JSONException e)
                                    {
                                        CommonFunctions.displayToast(context,context.getResources().getString(R.string.Login_failed));
                                    }
                                }
                            }
                        } else
                        {
                            CommonFunctions.displayToast(context,context.getResources().getString(R.string.Login_failed));
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
                params.put("timezone", CommonFunctions.getTimeZoneIST());
                params.put("timesstamp",CommonFunctions.getTimeStamp());
                params.put("os_version",m_config.os_version);
                return params;
            }
        };



        // add it to the RequestQueue
        int socketTimeout = 10000;//60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        accesstokenrequest.setRetryPolicy(policy);
        //queue.add(loginrequest);

        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(accesstokenrequest, "refresh_token_api");

    }

}
