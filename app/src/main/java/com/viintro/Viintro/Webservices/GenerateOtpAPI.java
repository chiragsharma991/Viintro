package com.viintro.Viintro.Webservices;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

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
import com.viintro.Viintro.Model.Otp_Request;
import com.viintro.Viintro.OTP.OtpActivity;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rkanawade on 08/02/17.
 */

public class GenerateOtpAPI {


    //api call for generate otp
    public static void req_otp_API(Context cont, final Otp_Request otp_request, final Activity activity, final String from) {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String bearertoken = sharedPreferences.getString(m_config.login_access_token,"");


        String url = m_config.web_api_link_header+ "/otp/generate";

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("client_id", otp_request.getClient_id());
            obj.put("client_secret", otp_request.getClient_secret());
            obj.put("country_code", otp_request.getCountryCode());
            obj.put("mobile_number", otp_request.getMobileNumber());
            obj.put("get_generated_otp", otp_request.getGetGeneratedOTP());

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



                        // display response
                        CommonFunctions.hDialog();
                        Log.d("Response", response.toString());
//                        if(from.equals("Contact_Details")){
//                            Intent int_otp = new Intent(context, OtpActivity.class);
//                            int_otp.putExtra("Mobile",otp_request.getMobileNumber());
//                            int_otp.putExtra("CountryCode", otp_request.getCountryCode());
//                            int_otp.putExtra("from",from);
//                            activity.startActivityForResult(int_otp, Constcore.REQUEST_CODE_ONBOARDING);
//                        }
                        Intent int_otp = new Intent(context, OtpActivity.class);
                        int_otp.putExtra("Mobile",otp_request.getMobileNumber());
                        int_otp.putExtra("CountryCode", otp_request.getCountryCode());
                        int_otp.putExtra("from",from);
                        if(from.equals("Contact_Details"))
                        {
                            activity.startActivityForResult(int_otp, Constcore.REQUEST_CODE_ADDCONTACT_OTP_VERIFY);
                            Toast.makeText(context,"Enter OTP sent to your mobile number",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            activity.startActivityForResult(int_otp, Constcore.REQUEST_CODE_ONBOARDING);
                            Toast.makeText(context,"Enter OTP sent to your mobile number",Toast.LENGTH_LONG).show();
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
                                    CommonFunctions.displayToast(context,context.getResources().getString(R.string.Otp_failed));
                                }

                            }
                            }
                        } else
                        {
                            CommonFunctions.hDialog();
                            CommonFunctions.displayToast(context,context.getResources().getString(R.string.Otp_failed));
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
        ViintroApplication.getInstance().addToRequestQueue(getRequest, "generate_otp_api");



    }

}
