package com.viintro.Viintro.MyProfile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.viintro.Viintro.Constants.Configuration_Parameter.profile_skills_list;
import static com.viintro.Viintro.Constants.Constcore.MAX_NUM_SKILLS;
import static com.viintro.Viintro.MyProfile.MyProfileFragment.text_profile_strength;

public class AddSkillsActivity extends Activity {
    private EditText edt_skills;
    private TextView text_char_left;
    private ImageView img_close;
    private Button btn_saveInfo;
    private Context mContext;
    SharedPreferences sharedpreferences;
    private String skills_length = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // getSupportActionBar().hide();
        setContentView(R.layout.activity_add_skills);
        mContext = this;
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        initialiseUI();
    }

    private void initialiseUI()
    {
        edt_skills = (EditText) findViewById(R.id.edt_skills);
        edt_skills.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAX_NUM_SKILLS) });
        text_char_left = (TextView) findViewById(R.id.text_char_left);
        img_close = (ImageView) findViewById(R.id.img_close);
        btn_saveInfo = (Button) findViewById(R.id.btn_saveInfo);


        if(getIntent().getExtras() != null) {
            String skl = getIntent().getExtras().getString("skl");
            if (!(skl == null)) {
                edt_skills.setText(skl);
            }
        }

        edt_skills.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                skills_length = edt_skills.getText().toString().replaceAll("\\s+", " ").trim();
                text_char_left.setText(MAX_NUM_SKILLS - skills_length.length()  + " characters left");

                Log.e("skills_length"," "+skills_length+" "+MAX_NUM_SKILLS);

                if (skills_length.length() == MAX_NUM_SKILLS)
                {
                    edt_skills.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_skills.getWindowToken(), 0);
                }
            }
        });

        btn_saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(CommonFunctions.chkStatus(mContext))
                {
                    CommonFunctions.sDialog(mContext, "Saving data..");
                    req_add_skills_API(mContext);

                }
                else
                {
                    CommonFunctions.displayToast(mContext,getResources().getString(R.string.network_connection));
                    CommonFunctions.hDialog();
                    return;
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

    //API call to send skills data
    public void req_add_skills_API(final Context cont)
    {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String bearertoken = sharedPreferences.getString(m_config.login_access_token,"");

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue((Activity) context);
        String url = m_config.web_api_link+ "/add-skills";

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("client_id", Constcore.client_Id);
            obj.put("client_secret", Constcore.client_Secret);
            obj.put("skills", edt_skills.getText().toString().replaceAll("\\s+", " ").trim());  //m_config.profile_website

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        Log.d("obj", ""+ obj);
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
                        CommonFunctions.hDialog();
                        CommonFunctions.displayToast(context,"Skills details has been saved");
                        if(profile_skills_list.size() == 0)
                        {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            int profile_strength = sharedPreferences.getInt(m_config.Profile_Strength,0) + 10;
                            editor.putInt(m_config.Profile_Strength, profile_strength);
                            editor.commit();
                            text_profile_strength.setText(String.valueOf(sharedPreferences.getInt(m_config.Profile_Strength,0))+"%");
                        }

                        String skills = edt_skills.getText().toString().replaceAll("\\s+", " ").trim();
                        profile_skills_list = new ArrayList();
                        profile_skills_list.add(Arrays.asList(skills.split(",")));
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("skills", skills);
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

                                        CommonFunctions.displayToast(context, context.getResources().getString(R.string.Skills_details_failed));
                                    }

                                }
                            }
                        }else
                        {
                            CommonFunctions.displayToast(context,context.getResources().getString(R.string.Skills_details_failed));
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
        ViintroApplication.getInstance().addToRequestQueue(getRequest, "add_skills_api");

    }
}
