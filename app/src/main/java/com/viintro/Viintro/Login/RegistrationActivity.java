package com.viintro.Viintro.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.SignUp_Request;
import com.viintro.Viintro.Model.Social_Login_Request;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Reusables.TextFont;
import com.viintro.Viintro.Reusables.Validations;
import com.viintro.Viintro.SocialLogin.FacebookLogin;
import com.viintro.Viintro.SocialLogin.GooglePlusLogin;
import com.viintro.Viintro.Webservices.LoginAPI;
import com.viintro.Viintro.Webservices.RegisterAPI;

import org.w3c.dom.Text;

/**
 * Created by rkanawade on 24/01/17.
 */

public class RegistrationActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    String TAG = "RegistrationActivity";
    private EditText edt_Name,edt_Email,edt_Password;
    private TextView txt_incorrect_name,txt_incorrect_email,txt_incorrect_pw;
    public static TextView btn_show_pw;
    private Button btn_Register, btn_Login;
    private RelativeLayout rel_fb, rel_gplus, rel_Terms_conditions;
    private CannonballTwitterLoginButton btn_twitterlogin;
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    Configuration_Parameter m_config;
    String flag_check_login = "";
    SharedPreferences sharedPreferences;
    private String fullname;
    private String email;
    private String password;
    public static RelativeLayout rel_email_popup_register;
    EditText edt_email_popup;
    Button btn_submitemail_popup;
    TextView txt_incorrect_email_popup;
    private String device_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_registration);
        mContext = this;
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        device_id = sharedPreferences.getString("device_id",null);
        m_config.callbackManager = CallbackManager.Factory.create();
        getHeight();
        initialiseUI();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(RegistrationActivity.this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)//this is used to get person profile here we need to fetch gender
                .build();

        edt_Name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    edt_Name.clearFocus();
                    edt_Email.requestFocus();
                    if (Validations.isEmpty(edt_Name))
                    {
                        txt_incorrect_name.setText(mContext.getResources().getString(R.string.enter_name));
                        txt_incorrect_name.setVisibility(View.VISIBLE);
                        edt_Name.setBackgroundResource(R.drawable.edittext_red_border);

                    }

                    else
                    {
                        txt_incorrect_name.setText("");
                        txt_incorrect_name.setVisibility(View.GONE);
                        edt_Name.setBackgroundResource(R.drawable.edittext_orange_border);
                    }
                    handled = true;

                }
                return handled;
            }
        });

        edt_Email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    if (Validations.isEmpty(edt_Name))
                    {
                        txt_incorrect_name.setText(mContext.getResources().getString(R.string.enter_name));
                        txt_incorrect_name.setVisibility(View.VISIBLE);
                        edt_Name.setBackgroundResource(R.drawable.edittext_red_border);

                    }

                    else
                    {
                        txt_incorrect_name.setText("");
                        txt_incorrect_name.setVisibility(View.GONE);
                        edt_Name.setBackgroundResource(R.drawable.edittext_orange_border);
                    }
                }
            }
        });

        edt_Email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    edt_Email.clearFocus();
                    edt_Password.requestFocus();
                    if (Validations.isEmpty(edt_Email))
                    {
                        txt_incorrect_email.setText(mContext.getResources().getString(R.string.enter_email_id));
                        txt_incorrect_email.setVisibility(View.VISIBLE);
                        edt_Email.setBackgroundResource(R.drawable.edittext_red_border);

                    }
                    else if(!Validations.isEmpty(edt_Email) && !Validations.isValidEmailId(edt_Email.getText().toString().replaceAll("\\s+", " ").trim()))
                    {
                        txt_incorrect_email.setText(mContext.getResources().getString(R.string.valid_email_id));
                        txt_incorrect_email.setVisibility(View.VISIBLE);
                        edt_Email.setBackgroundResource(R.drawable.edittext_red_border);
                    }
                    else
                    {
                        txt_incorrect_email.setText("");
                        txt_incorrect_email.setVisibility(View.GONE);
                        edt_Email.setBackgroundResource(R.drawable.edittext_orange_border);
                    }
                    handled = true;

                }
                return handled;
            }
        });

        edt_Password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    if (Validations.isEmpty(edt_Name))
                    {
                        txt_incorrect_name.setText(mContext.getResources().getString(R.string.enter_name));
                        txt_incorrect_name.setVisibility(View.VISIBLE);
                        edt_Name.setBackgroundResource(R.drawable.edittext_red_border);

                    }

                    else
                    {
                        txt_incorrect_name.setText("");
                        txt_incorrect_name.setVisibility(View.GONE);
                        edt_Name.setBackgroundResource(R.drawable.edittext_orange_border);
                    }

                    if (Validations.isEmpty(edt_Email))
                    {
                        txt_incorrect_email.setText(mContext.getResources().getString(R.string.enter_email_id));
                        txt_incorrect_email.setVisibility(View.VISIBLE);
                        edt_Email.setBackgroundResource(R.drawable.edittext_red_border);

                    }
                    else if(!Validations.isEmpty(edt_Email) && !Validations.isValidEmailId(edt_Email.getText().toString().replaceAll("\\s+", " ").trim()))
                    {
                        txt_incorrect_email.setText(mContext.getResources().getString(R.string.valid_email_id));
                        txt_incorrect_email.setVisibility(View.VISIBLE);
                        edt_Email.setBackgroundResource(R.drawable.edittext_red_border);
                    }
                    else
                    {
                        txt_incorrect_email.setText("");
                        txt_incorrect_email.setVisibility(View.GONE);
                        edt_Email.setBackgroundResource(R.drawable.edittext_orange_border);
                    }
                }
            }
        });


        edt_Password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    edt_Password.clearFocus();
                    btn_Register.performClick();
                    handled = true;

                }
                return handled;
            }
        });



        edt_Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateFields();
            }
        });

        edt_Email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateFields();
            }
        });

        edt_Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateFields();
            }
        });

        edt_email_popup.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    edt_email_popup.clearFocus();
                    btn_submitemail_popup.performClick();
                    handled = true;

                }
                return handled;
            }
        });

        edt_email_popup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(edt_email_popup.getText().toString().replaceAll("\\s+", " ").trim().length()>0)
                {

                    btn_submitemail_popup.setBackgroundResource(R.drawable.btn_border_orange);
                    btn_submitemail_popup.setEnabled(true);
                    btn_submitemail_popup.setTextColor(Color.parseColor("#ff704c"));

                }
                else
                {
                    btn_submitemail_popup.setBackgroundResource(R.drawable.btn_border_grey);
                    btn_submitemail_popup.setEnabled(false);
                    btn_submitemail_popup.setTextColor(Color.parseColor("#d6d6d6"));

                }
            }
        });

//        btn_show_pw.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(btn_show_pw.getText().toString().equals("Show"))
//                {
//                    edt_Password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//                    btn_show_pw.setText(getResources().getString(R.string.btn_disable_pw));
//                }
//                else
//                {
//                    edt_Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                    btn_show_pw.setText(getResources().getString(R.string.btn_show_Pw));
//                }
//
//            }
//        });//


        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_Name.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(edt_Email.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(edt_Password.getWindowToken(), 0);


                if (Validations.isEmpty(edt_Name) || Validations.isEmpty(edt_Email) || Validations.isEmpty(edt_Password))
                {
                    if (Validations.isEmpty(edt_Name))
                    {
                        txt_incorrect_name.setText(mContext.getResources().getString(R.string.enter_name));
                        txt_incorrect_name.setVisibility(View.VISIBLE);
                        edt_Name.setBackgroundResource(R.drawable.edittext_red_border);

                    }

                    else
                    {
                        txt_incorrect_name.setText("");
                        txt_incorrect_name.setVisibility(View.GONE);
                        edt_Name.setBackgroundResource(R.drawable.edittext_orange_border);
                    }


                    if (Validations.isEmpty(edt_Email))
                    {
                        txt_incorrect_email.setText(mContext.getResources().getString(R.string.enter_email_id));
                        txt_incorrect_email.setVisibility(View.VISIBLE);
                        edt_Email.setBackgroundResource(R.drawable.edittext_red_border);

                    }
                    else if(!Validations.isEmpty(edt_Email) && !Validations.isValidEmailId(edt_Email.getText().toString().replaceAll("\\s+", " ").trim()))
                    {
                        txt_incorrect_email.setText(mContext.getResources().getString(R.string.valid_email_id));
                        txt_incorrect_email.setVisibility(View.VISIBLE);
                        edt_Email.setBackgroundResource(R.drawable.edittext_red_border);
                    }
                    else
                    {
                        txt_incorrect_email.setText("");
                        txt_incorrect_email.setVisibility(View.GONE);
                        edt_Email.setBackgroundResource(R.drawable.edittext_orange_border);
                    }


                    if (Validations.isEmpty(edt_Password))
                    {
                        txt_incorrect_pw.setText(mContext.getResources().getString(R.string.enter_password));
                        txt_incorrect_pw.setVisibility(View.VISIBLE);
                        edt_Password.setBackgroundResource(R.drawable.edittext_red_border);
                    }
                    else if(!Validations.isEmpty(edt_Password) && edt_Password.getText().toString().replaceAll("\\s+", " ").trim().length() < 6)
                    {
                        txt_incorrect_pw.setText(mContext.getResources().getString(R.string.password_validation));
                        txt_incorrect_pw.setVisibility(View.VISIBLE);
                        edt_Password.setBackgroundResource(R.drawable.edittext_red_border);
                    }
                    else
                    {
                        txt_incorrect_pw.setText("");
                        txt_incorrect_pw.setVisibility(View.GONE);
                        edt_Password.setBackgroundResource(R.drawable.edittext_orange_border);
                    }
                }
                else
                {

                    if (!Validations.isValidEmailId(edt_Email.getText().toString().replaceAll("\\s+", " ").trim()) || edt_Password.getText().toString().replaceAll("\\s+", " ").trim().length() < 6)
                    {

                        if (!Validations.isValidEmailId(edt_Email.getText().toString().replaceAll("\\s+", " ").trim()))
                        {
                            txt_incorrect_email.setText(mContext.getResources().getString(R.string.valid_email_id));
                            txt_incorrect_email.setVisibility(View.VISIBLE);
                            edt_Email.setBackgroundResource(R.drawable.edittext_red_border);
                            txt_incorrect_name.setText("");
                            txt_incorrect_name.setVisibility(View.GONE);
                            edt_Name.setBackgroundResource(R.drawable.edittext_orange_border);
                        }

                        if(edt_Password.getText().toString().replaceAll("\\s+", " ").trim().length() < 6)
                        {
                            txt_incorrect_pw.setText(mContext.getResources().getString(R.string.password_validation));
                            txt_incorrect_pw.setVisibility(View.VISIBLE);
                            edt_Password.setBackgroundResource(R.drawable.edittext_red_border);
                            txt_incorrect_name.setText("");
                            txt_incorrect_name.setVisibility(View.GONE);
                            edt_Name.setBackgroundResource(R.drawable.edittext_orange_border);

                        }
                        else
                        {
                            txt_incorrect_pw.setText("");
                            txt_incorrect_pw.setVisibility(View.GONE);
                            edt_Password.setBackgroundResource(R.drawable.edittext_orange_border);
                            txt_incorrect_name.setText("");
                            txt_incorrect_name.setVisibility(View.GONE);
                            edt_Name.setBackgroundResource(R.drawable.edittext_orange_border);
                        }

                    }

                    else
                    {

                        dismissCursor();
                        if(CommonFunctions.chkStatus(mContext))
                        {
//                            btn_show_pw.setVisibility(View.GONE);
//                            btn_show_pw.setText(getResources().getString(R.string.btn_show_Pw));
                            edt_Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            CommonFunctions.sDialog(mContext, "Loading...");
                            fullname = edt_Name.getText().toString().replaceAll("\\s+", " ").trim();
                            email = edt_Email.getText().toString().replaceAll("\\s+", " ").trim();
                            password = edt_Password.getText().toString().replaceAll("\\s+", " ").trim();
                            SignUp_Request signUp_request = json_signup_request(fullname, email, password);
                            RegisterAPI.req_register_API(mContext, signUp_request);
                        }
                        else
                        {
                            CommonFunctions.displayToast(mContext,getResources().getString(R.string.network_connection));
                        }


                    }
                }


            }
        });

        rel_Terms_conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshAllValues();
                Intent int_forgot_pw = new Intent(RegistrationActivity.this, TermsnConditionsActivity.class);
                startActivity(int_forgot_pw);
            }
        });

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshAllValues();
                Intent int_registration = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(int_registration);
            }
        });

        rel_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshAllValues();
                if(CommonFunctions.chkStatus(mContext))
                {
                    flag_check_login = "fb";
                    FacebookLogin.fbWithFirebase(mContext);
                    FacebookLogin.signInByFacebook(mContext);
                }
                else
                {
                    CommonFunctions.displayToast(mContext,getResources().getString(R.string.network_connection));
                }
            }
        });


        btn_twitterlogin.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                refreshAllValues();
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                flag_check_login = "";
                TwitterSession session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model

                String id = String.valueOf(session.getUserId());
                String fullname = session.getUserName();
                String email = "";
                //String email = TwitterLogin.getTwitterEmail(session);

                m_config.social_login_request = Social_Login_Request.json_social_login_request("twitter", fullname, email, "", "", "", "", "", id, device_id,"");

                // call to social login API

                if (m_config.social_login_request.getEmail().equals(""))
                {
                    edt_email_popup.setText("");
                    edt_email_popup.setBackgroundResource(R.drawable.edittext_border);
                    txt_incorrect_email_popup.setText("");
                    txt_incorrect_email_popup.setVisibility(View.GONE);
                    btn_submitemail_popup.setBackgroundResource(R.drawable.btn_border_grey);
                    btn_submitemail_popup.setEnabled(false);
                    btn_submitemail_popup.setTextColor(Color.parseColor("#d6d6d6"));
                    rel_email_popup_register.setVisibility(View.VISIBLE);

                } else {
                    // call to social login API
                }

            }

            @Override
            public void failure(TwitterException exception)
            {
                flag_check_login = "";
                if(CommonFunctions.chkStatus(mContext))
                {
                    CommonFunctions.displayToast(mContext,exception.getMessage());
                }
                else
                {
                    CommonFunctions.displayToast(mContext,getResources().getString(R.string.network_connection));
                }

            }

        });

        rel_gplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshAllValues();
                if(CommonFunctions.chkStatus(mContext))
                {
                    CommonFunctions.sDialog(mContext,"Loading...");
                    flag_check_login = "gplus";
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, Constcore.RC_SIGN_IN);
                }
                else
                {
                    CommonFunctions.displayToast(mContext,getResources().getString(R.string.network_connection));
                }

            }
        });

        btn_submitemail_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_email_popup.getWindowToken(), 0);


                if (Validations.isEmpty(edt_email_popup)) {
                    txt_incorrect_email_popup.setText(mContext.getResources().getString(R.string.enter_email_id));
                    txt_incorrect_email_popup.setVisibility(View.VISIBLE);
                    edt_email_popup.setBackgroundResource(R.drawable.edittext_red_border);
                } else if (!Validations.isEmpty(edt_email_popup) && !Validations.isValidEmailId(edt_email_popup.getText().toString().replaceAll("\\s+", " ").trim())) {
                    txt_incorrect_email_popup.setText(mContext.getResources().getString(R.string.valid_email_id));
                    txt_incorrect_email_popup.setVisibility(View.VISIBLE);
                    edt_email_popup.setBackgroundResource(R.drawable.edittext_red_border);
                } else {
                    // call to social login API
                    edt_email_popup.setBackgroundResource(R.drawable.edittext_orange_border);
                    CommonFunctions.sDialog(mContext,"Loading...");
                    String email = edt_email_popup.getText().toString().replaceAll("\\s+", " ").trim();
                    m_config.social_login_request.setEmail(email);
                    LoginAPI.req_sociallogin_API(mContext,  m_config.social_login_request);
                    resetAllfields();
                    Log.e("social_login_request", "  " + m_config.social_login_request.getEmail() + " " + m_config.social_login_request.getFullname() + " " + m_config.social_login_request.getProfilepic());


                }
            }

        });

        rel_email_popup_register.setOnClickListener(null);

    }

    private void initialiseUI() {


        RelativeLayout rel_register_main = (RelativeLayout) findViewById(R.id.rel_register_main);
        RelativeLayout rel_register_main1 = (RelativeLayout) findViewById(R.id.rel_register_main1);
        rel_register_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_Name.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(edt_Email.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(edt_Password.getWindowToken(), 0);
                edt_Name.clearFocus();
                edt_Email.clearFocus();
                edt_Password.clearFocus();
            }
        });
        rel_register_main1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_Name.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(edt_Email.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(edt_Password.getWindowToken(), 0);
                edt_Name.clearFocus();
                edt_Email.clearFocus();
                edt_Password.clearFocus();
            }
        });

        TextInputLayout input_name = (TextInputLayout) findViewById(R.id.input_name);
        input_name.setTypeface(TextFont.opensans_regular(mContext));
        TextInputLayout input_email = (TextInputLayout) findViewById(R.id.input_email);
        input_email.setTypeface(TextFont.opensans_regular(mContext));
        TextInputLayout input_password = (TextInputLayout) findViewById(R.id.input_password);
        input_password.setTypeface(TextFont.opensans_regular(mContext));
        edt_Name = (EditText) findViewById(R.id.edt_Name);
        edt_Name.setTypeface(TextFont.opensans_regular(mContext));
        edt_Email = (EditText) findViewById(R.id.edt_Email);
        edt_Email.setTypeface(TextFont.opensans_regular(mContext));
        edt_Password = (EditText) findViewById(R.id.edt_Password);
        edt_Password.setTypeface(TextFont.opensans_regular(mContext));
        txt_incorrect_name = (TextView) findViewById(R.id.txt_incorrect_name);
        txt_incorrect_name.setTypeface(TextFont.opensans_regular(mContext));
        txt_incorrect_email = (TextView) findViewById(R.id.txt_incorrect_email);
        txt_incorrect_email.setTypeface(TextFont.opensans_regular(mContext));
        txt_incorrect_pw = (TextView) findViewById(R.id.txt_incorrect_pw);
        txt_incorrect_pw.setTypeface(TextFont.opensans_regular(mContext));
        btn_show_pw = (TextView) findViewById(R.id.btn_show_pw);
        btn_Register = (Button) findViewById(R.id.btn_Register);
        btn_Register.setTypeface(TextFont.opensans_semibold(mContext));

        rel_Terms_conditions = (RelativeLayout) findViewById(R.id.rel_Terms_conditions);
        TextView txt_by_registering = (TextView) findViewById(R.id.txt_by_registering);
        txt_by_registering.setTypeface(TextFont.opensans_regular(mContext));
        TextView btn_TermsnConditions = (TextView) findViewById(R.id.btn_TermsnConditions);
        btn_TermsnConditions.setTypeface(TextFont.opensans_regular(mContext));
        btn_Login = (Button) findViewById(R.id.btn_login);
        btn_Login.setTypeface(TextFont.opensans_bold(mContext));
        TextView txt_or_register_with = (TextView) findViewById(R.id.or_register_with);
        txt_or_register_with.setTypeface(TextFont.opensans_regular(mContext));


        rel_fb = (RelativeLayout) findViewById(R.id.rel_fb);
        TextView txt_facebook = (TextView) findViewById(R.id.txt_facebook);
        txt_facebook.setTypeface(TextFont.opensans_semibold(mContext));

        btn_twitterlogin = (CannonballTwitterLoginButton) findViewById(R.id.btn_twitterlogin);
        btn_twitterlogin.setTypeface(TextFont.opensans_semibold(mContext));

        rel_gplus= (RelativeLayout) findViewById(R.id.rel_gplus);
        TextView txt_google = (TextView) findViewById(R.id.txt_google);
        txt_google.setTypeface(TextFont.opensans_semibold(mContext));

        //popup for Email
        rel_email_popup_register = (RelativeLayout) findViewById(R.id.rel_email_popup_register);
        edt_email_popup = (EditText) findViewById(R.id.edt_email_popup);
        edt_email_popup.setTypeface(TextFont.opensans_regular(mContext));
        btn_submitemail_popup = (Button) findViewById(R.id.btn_submitemail_popup);
        btn_submitemail_popup.setTypeface(TextFont.opensans_semibold(mContext));
        txt_incorrect_email_popup = (TextView) findViewById(R.id.txt_incorrect_email_popup);
        txt_incorrect_email_popup.setTypeface(TextFont.opensans_regular(mContext));
    }

    public void dismissCursor()
    {
        //Clear Focus from all edit texts
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_Name.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edt_Email.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edt_Password.getWindowToken(), 0);
        edt_Name.clearFocus();
        edt_Email.clearFocus();
        edt_Password.clearFocus();
        txt_incorrect_name.setText("");
        txt_incorrect_name.setVisibility(View.GONE);
        edt_Name.setBackgroundResource(R.drawable.edittext_orange_border);
        txt_incorrect_email.setText("");
        txt_incorrect_email.setVisibility(View.GONE);
        edt_Email.setBackgroundResource(R.drawable.edittext_orange_border);
        txt_incorrect_pw.setText("");
        txt_incorrect_pw.setVisibility(View.GONE);
        edt_Password.setBackgroundResource(R.drawable.edittext_orange_border);


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (flag_check_login.equals("gplus"))//requestCode == Constcore.RC_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess())
            {
                GooglePlusLogin.handleSignInResult(result, mContext, mGoogleApiClient);

            }
            else
            {
                // Google Sign In failed, update UI appropriately
                CommonFunctions.hDialog();
                CommonFunctions.displayToast(mContext,getResources().getString(R.string.Google_Login_Failed));
            }
            flag_check_login = "";
        }
        else if(flag_check_login.equals("fb"))
        {
            m_config.callbackManager.onActivityResult(requestCode, resultCode, data);
            flag_check_login = "";
        }
        else
        {
            btn_twitterlogin.onActivityResult(requestCode, resultCode, data);
            flag_check_login = "";
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
        //logout from facebook
        LoginManager.getInstance().logOut();
    }

    @Override
    public void onBackPressed() {

        if(rel_email_popup_register.getVisibility() == View.VISIBLE)
        {
            rel_email_popup_register.setVisibility(View.GONE);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public SignUp_Request json_signup_request(String fullname, String email, String password)
    {


        SignUp_Request signUp_request = new SignUp_Request();
        signUp_request.setClient_id(Constcore.client_Id);
        signUp_request.setClient_secret(Constcore.client_Secret);
        signUp_request.setFullname(fullname);
        signUp_request.setEmail(email);
        signUp_request.setPassword(password);
        signUp_request.setDevice_id(device_id);
        signUp_request.setPush_token("");

        return signUp_request;
    }


    private void resetAllfields()
    {
        rel_email_popup_register.setVisibility(View.GONE);
        edt_email_popup.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_email_popup.getWindowToken(), 0);
        edt_email_popup.clearFocus();
        txt_incorrect_email_popup.setText("");
        txt_incorrect_email_popup.setVisibility(View.GONE);
        edt_email_popup.setBackgroundResource(R.drawable.edittext_border);
    }

    private void validateFields() {
        if (edt_Name.getText().toString().replaceAll("\\s+", " ").trim().length() > 0 && edt_Email.getText().toString().replaceAll("\\s+", " ").trim().length() > 0 && edt_Password.getText().toString().replaceAll("\\s+", " ").trim().length() > 0) {
            btn_Register.setBackgroundResource(R.drawable.btn_border_orange);
            btn_Register.setEnabled(true);
            btn_Register.setTextColor(Color.parseColor("#ff704c"));
        }
        else
        {
            btn_Register.setBackgroundResource(R.drawable.btn_border_grey);
            btn_Register.setEnabled(false);
            btn_Register.setTextColor(Color.parseColor("#d6d6d6"));

        }

//        if (edt_Password.getText().toString().replaceAll("\\s+", " ").trim().length() > 0) {
//            btn_show_pw.setEnabled(true);
//
//        } else {
//            btn_show_pw.setEnabled(false);
//
//        }
    }

    public void refreshAllValues()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_Name.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edt_Email.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edt_Password.getWindowToken(), 0);
        edt_Name.setText("");
        edt_Email.setText("");
        edt_Password.setText("");
        edt_Name.clearFocus();
        edt_Email.clearFocus();
        edt_Password.clearFocus();
        txt_incorrect_name.setText("");
        txt_incorrect_name.setVisibility(View.GONE);
        edt_Name.setBackgroundResource(R.drawable.edittext_border);
        txt_incorrect_email.setText("");
        txt_incorrect_email.setVisibility(View.GONE);
        edt_Email.setBackgroundResource(R.drawable.edittext_border);
        txt_incorrect_pw.setText("");
        txt_incorrect_pw.setVisibility(View.GONE);
        edt_Password.setBackgroundResource(R.drawable.edittext_border);
        btn_Register.setBackgroundResource(R.drawable.btn_border_grey);
        btn_Register.setEnabled(false);
        btn_Register.setTextColor(Color.parseColor("#d6d6d6"));
    }

    public int getHeight()
    {
        final int[] height = new int[1];

        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.rel_white_background);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int width = layout.getMeasuredWidth();
                height[0] = layout.getMeasuredHeight();
                Log.e("height"," "+height[0]);
                RelativeLayout rel_orange_background = (RelativeLayout) findViewById(R.id.rel_orange_background);
                rel_orange_background.getLayoutParams().height = height[0];
                rel_orange_background.requestLayout();

            }
        });
        return height[0];
    }

}
