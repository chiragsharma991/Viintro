package com.viintro.Viintro.MyProfile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.Otp_Request;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static com.viintro.Viintro.Constants.Constcore.REQUEST_CODE_ADDCONTACT_OTP_VERIFY;
import static com.viintro.Viintro.MyProfile.MyProfileFragment.text_profile_strength;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.viintro.Viintro.Constants.Constcore.REQUEST_CODE_ADDCONTACT_OTP_VERIFY;
import static com.viintro.Viintro.Webservices.GenerateOtpAPI.req_otp_API;

public class AddContactDetailsActivity extends Activity {

    private EditText edt_profile, edt_website, edt_mobilenoStudent;
    private ImageView img_close;
    private Button btn_saveInfo;
    private Spinner spinnerCountryCodes;
    private TextView text_verified;
    ArrayAdapter<String> adp3;
    private Context mContext;
    Configuration_Parameter m_config;
    SharedPreferences sharedpreferences;
    private Activity addContactDetails_activity;

    private String phone, country_code;
    private String profile_website = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // getSupportActionBar().hide();
        setContentView(R.layout.activity_add_contact_details);
        mContext = this;
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        m_config = Configuration_Parameter.getInstance();
        initialiseUI();

    }

    private void initialiseUI()
    {
        img_close = (ImageView) findViewById(R.id.img_close);
        edt_profile = (EditText) findViewById(R.id.edt_profile);
        edt_website = (EditText) findViewById(R.id.edt_website);
        edt_mobilenoStudent = (EditText) findViewById(R.id.edt_mobilenoStudent);
        btn_saveInfo = (Button) findViewById(R.id.btn_saveInfo);
        spinnerCountryCodes = (Spinner) findViewById(R.id.spinnerCountryCodes);
        text_verified = (TextView) findViewById(R.id.text_verified);

        String profile_url = sharedpreferences.getString(m_config.profile_url, null);
        profile_website = sharedpreferences.getString(m_config.profile_website, null);

        if(!(profile_url == null) || !profile_url.equals("")){
            edt_profile.setText(profile_url);
        }

        if((profile_website == null) || profile_website.equals("")){

        }else{
            edt_website.setText(profile_website);
        }

        String Mobile_Number = sharedpreferences.getString(m_config.Mobile_Number,null);
        if((!(Mobile_Number == null)) && (!(Mobile_Number.equals(""))) && (!(Mobile_Number.length()==0))){
            edt_mobilenoStudent.setText(Mobile_Number);
            text_verified.setVisibility(View.VISIBLE);
        }else{
            text_verified.setVisibility(View.GONE);

        }

        edt_profile.setEnabled(false);
        adp3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.countryCodes));
        adp3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCountryCodes.setAdapter(adp3);


        edt_mobilenoStudent.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enableVerifyStudentReady();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        String [] strb = getResources().getStringArray(R.array.countryCodes);

        final String[] country_code = {sharedpreferences.getString(m_config.Country_code, null)};
        if(country_code[0] == null || country_code[0].equals(""))
        {
            int index = Arrays.asList(strb).indexOf("91");
            spinnerCountryCodes.setSelection(index);
        }
        else
        {

            int index = Arrays.asList(strb).indexOf(country_code[0]);
            spinnerCountryCodes.setSelection(index);
        }



        btn_saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_mobilenoStudent.getText().toString().equals("") || edt_website.getText().toString().equals("")){
                    CommonFunctions.displayToast(mContext,"Please fill empty fields");


                }
                else if(!isValidUrl(edt_website.getText().toString().trim()))
                {
                    CommonFunctions.displayToast(mContext,"Website format is invalid");


                }
                else
                {

                    if(text_verified.getVisibility() == View.VISIBLE)
                    {

                        if(CommonFunctions.chkStatus(mContext))
                        {

                            CommonFunctions.sDialog(mContext, "Saving data..");
                            req_add_contact_details_API(mContext);

                        }
                        else
                        {
                            CommonFunctions.displayToast(mContext,getResources().getString(R.string.network_connection));
                            CommonFunctions.hDialog();
                            return;
                        }
                    }
                    else
                    {
                        phone = edt_mobilenoStudent.getText().toString().trim();
                        //country_code = spinnerCountryCodes.getSelectedItem().toString();
                        if (CommonFunctions.chkStatus(mContext)) {

                            CommonFunctions.sDialog(mContext, "Loading");
                            Otp_Request otp_request = json_otp_request(phone);
                            req_otp_API(mContext, otp_request, (Activity)mContext, "Contact_Details");
                        }
                        else
                        {

                            CommonFunctions.displayToast(mContext, getResources().getString(R.string.network_connection));

                        }
                    }
                }

            }
        });

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        if(m.matches())
            return true;
        else
            return false;
    }

    public Otp_Request json_otp_request(String mobile) {
        Otp_Request otp_request = new Otp_Request();
        otp_request.setClient_id(Constcore.client_Id);
        otp_request.setClient_secret(Constcore.client_Secret);
        otp_request.setCountryCode(spinnerCountryCodes.getSelectedItem().toString());
        otp_request.setMobileNumber(mobile);
        otp_request.setGetGeneratedOTP(true);
        return otp_request;
    }

    public void enableVerifyStudentReady() {

        int len = edt_mobilenoStudent.getText().toString().length();
        if(len == 10){
            if(!edt_mobilenoStudent.getText().toString().equals(m_config.Mobile_Number)){

                text_verified.setVisibility(View.GONE);

            }else{
                text_verified.setVisibility(View.VISIBLE);
            }


        }
    }

    // API Call to get contact details details
    public void req_add_contact_details_API(final Context cont)
    {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String bearertoken = sharedPreferences.getString(m_config.login_access_token,"");

        String url = m_config.web_api_link+ "/add-contact-details";
        profile_website = edt_website.getText().toString().trim();


        JSONObject obj = new JSONObject();
        try
        {
            obj.put("client_id", Constcore.client_Id);
            obj.put("client_secret", Constcore.client_Secret);
            obj.put("website", profile_website);
            obj.put("country_code", spinnerCountryCodes.getSelectedItem().toString());
            obj.put("mobile_number", edt_mobilenoStudent.getText().toString().trim());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        int count = -1;
                        try {
                            Log.e("response code"," "+response.getInt("code"));
                            count = response.getInt("code");
                            if(count != -1)
                            {
                                CommonFunctions.hDialog();
                                CommonFunctions.displayToast(context,response.getString("message"));
                            }
                            else
                            {

                            }
                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        if(sharedpreferences.getString(m_config.Mobile_Number,null) == null || sharedpreferences.getString(m_config.Mobile_Number,null).equals(""))
                        {
                            int profile_strength = sharedpreferences.getInt(m_config.Profile_Strength,0) + 10;
                            editor.putString(m_config.Mobile_Number, edt_mobilenoStudent.getText().toString().trim());
                            editor.putInt(m_config.Profile_Strength, profile_strength);
                            editor.commit();
                            text_profile_strength.setText(String.valueOf(sharedpreferences.getInt(m_config.Profile_Strength,0))+"%");
                        }

                        editor.putString(m_config.profile_website, edt_website.getText().toString().trim());
                        editor.commit();

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("profile", edt_profile.getText().toString().replaceAll("\\s+", " ").trim());
                        resultIntent.putExtra("website", edt_website.getText().toString().replaceAll("\\s+", " ").trim());
                        resultIntent.putExtra("mobno", edt_mobilenoStudent.getText().toString().replaceAll("\\s+", " ").trim());
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CommonFunctions.hDialog();

                        if(error.networkResponse != null) {
                            String networkresponse_data = new String(error.networkResponse.data);
                            if (error.networkResponse.statusCode == 500) {
                                CommonFunctions.displayToast(context, context.getResources().getString(R.string.internal_server_error));
                            } else {
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

                                        CommonFunctions.displayToast(context, context.getResources().getString(R.string.Contact_details_failed));
                                    }

                                }
                            }
                        }else
                        {

                            CommonFunctions.displayToast(context,context.getResources().getString(R.string.Contact_details_failed));
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
        ViintroApplication.getInstance().addToRequestQueue(getRequest, "add_contact_details_api");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADDCONTACT_OTP_VERIFY && resultCode == RESULT_OK)
        {

            Log.d("","onActivityResult if");
            text_verified.setVisibility(View.VISIBLE);
            CommonFunctions.sDialog(mContext, "Saving data..");
            req_add_contact_details_API(mContext);

        }else{
            Log.d("","onActivityResult else");

        }



    }

}
