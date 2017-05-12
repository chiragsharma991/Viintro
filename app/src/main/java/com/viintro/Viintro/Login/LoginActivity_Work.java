package com.viintro.Viintro.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.Login_Request;
import com.viintro.Viintro.Model.Social_Login_Request;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Reusables.Validations;
import com.viintro.Viintro.SocialLogin.FacebookLogin;
import com.viintro.Viintro.SocialLogin.GooglePlusLogin;
import com.viintro.Viintro.Webservices.LoginAPI;


/**
 * Created by rkanawade on 24/01/17.
 */

public class LoginActivity_Work extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener {

    String TAG = "LoginActivity";
    private EditText edt_Email, edt_Password;
    private TextView txt_incorrect_email, txt_incorrect_pw;
    public static TextView btn_show_pw;
    private Button btn_Login, btn_ForgotPW, btn_JoinNow;
    private RelativeLayout rel_fb, rel_gplus;
    private CannonballTwitterLoginButton btn_twitterlogin;
    private GoogleApiClient mGoogleApiClient;
    //Signin constant to check the activity result
    private Context mContext;
    Configuration_Parameter m_config;
    SharedPreferences sharedPreferences;
    public String flag_check_login = "";
    private String email;
    private String password;

    public static RelativeLayout rel_email_popup_login;
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
        setContentView(R.layout.activity_login);
        mContext = this;
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        device_id = sharedPreferences.getString("device_id",null);
        m_config.callbackManager = CallbackManager.Factory.create();
//        RelativeLayout rel_white_background = (RelativeLayout) findViewById(R.id.rel_white_background);
//        RelativeLayout rel_orange_background = (RelativeLayout) findViewById(R.id.rel_orange_background);
//
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rel_white_background.getLayoutParams();
//
//
//        Log.e("height"," "+params.height);

        initialiseUI();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(LoginActivity_Work.this.getResources().getString(R.string.server_client_id))// web client id to get status code
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestScopes(new Scope(Scopes.PLUS_ME))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(LoginActivity_Work.this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)//this is used to get person profile here we need to fetch gender
                .build();

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

                    } else if (!Validations.isEmpty(edt_Email) && !Validations.isValidEmailId(edt_Email.getText().toString().replaceAll("\\s+", " ").trim())) {
                        txt_incorrect_email.setText(mContext.getResources().getString(R.string.valid_email_id));
                        txt_incorrect_email.setVisibility(View.VISIBLE);
                        edt_Email.setBackgroundResource(R.drawable.edittext_red_border);
                    } else
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


        edt_Password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    edt_Password.clearFocus();
                    if (Validations.isEmpty(edt_Password))
                    {
                        txt_incorrect_pw.setText(mContext.getResources().getString(R.string.enter_password));
                        txt_incorrect_pw.setVisibility(View.VISIBLE);
                        edt_Password.setBackgroundResource(R.drawable.edittext_red_border);

                    }
                    else
                    {
                        txt_incorrect_pw.setText("");
                        txt_incorrect_pw.setVisibility(View.GONE);
                        edt_Password.setBackgroundResource(R.drawable.edittext_orange_border);
                    }
                    //btn_Login.performClick();
                    handled = true;

                }
                return handled;
            }
        });



        edt_Email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Validations.isEmpty(edt_Email))
                {
                    edt_Email.setBackgroundResource(R.drawable.edittext_border);

                } else if (!Validations.isEmpty(edt_Email) && !Validations.isValidEmailId(edt_Email.getText().toString().replaceAll("\\s+", " ").trim()))
                {

                } else
                {
                    edt_Email.setBackgroundResource(R.drawable.edittext_orange_border);
                }
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

        btn_show_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_show_pw.getText().toString().equals("Show"))
                {
                    edt_Password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btn_show_pw.setText(getResources().getString(R.string.btn_disable_pw));
                }
                else
                {
                    edt_Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btn_show_pw.setText(getResources().getString(R.string.btn_show_Pw));
                }
            }
        });

        edt_email_popup.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    edt_email_popup.setBackgroundResource(R.drawable.edittext_orange_border);
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
                    btn_submitemail_popup.setTextColor(R.color.btn_Login);
                }
                else
                {
                    btn_submitemail_popup.setBackgroundResource(R.drawable.btn_border_grey);
                    btn_submitemail_popup.setEnabled(false);
                    btn_submitemail_popup.setTextColor(R.color.grey);

                }
            }
        });


        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Validations.isEmpty(edt_Email) || Validations.isEmpty(edt_Password))
                {
                    if (Validations.isEmpty(edt_Email)) {
                        txt_incorrect_email.setText(mContext.getResources().getString(R.string.enter_email_id));
                        txt_incorrect_email.setVisibility(View.VISIBLE);
                        edt_Email.setBackgroundResource(R.drawable.edittext_red_border);

                    } else if (!Validations.isEmpty(edt_Email) && !Validations.isValidEmailId(edt_Email.getText().toString().replaceAll("\\s+", " ").trim())) {
                        txt_incorrect_email.setText(mContext.getResources().getString(R.string.valid_email_id));
                        txt_incorrect_email.setVisibility(View.VISIBLE);
                        edt_Email.setBackgroundResource(R.drawable.edittext_red_border);
                    } else {
                        txt_incorrect_email.setText("");
                        txt_incorrect_email.setVisibility(View.GONE);
                        edt_Email.setBackgroundResource(R.drawable.edittext_orange_border);
                    }


                    if (Validations.isEmpty(edt_Password)) {
                        txt_incorrect_pw.setText(mContext.getResources().getString(R.string.enter_password));
                        txt_incorrect_pw.setVisibility(View.VISIBLE);
                        edt_Password.setBackgroundResource(R.drawable.edittext_red_border);
                    }
//                    else if(!Validations.isEmpty(edt_Password) && edt_Password.getText().toString().replaceAll("\\s+", " ").trim().length() < 6)
//                    {
//                        txt_incorrect_pw.setText("Password must consist at least six characters");
//                        txt_incorrect_pw.setVisibility(View.VISIBLE);
//                        edt_Password.setBackgroundResource(R.drawable.edittext_red_border);
//                    }
                    else
                    {
                        txt_incorrect_pw.setText("");
                        txt_incorrect_pw.setVisibility(View.GONE);
                        edt_Password.setBackgroundResource(R.drawable.edittext_orange_border);
                    }
                } else {

                    if (!Validations.isValidEmailId(edt_Email.getText().toString().replaceAll("\\s+", " ").trim()))
                    {

                        txt_incorrect_email.setText(mContext.getResources().getString(R.string.valid_email_id));
                        txt_incorrect_email.setVisibility(View.VISIBLE);
                        edt_Email.setBackgroundResource(R.drawable.edittext_red_border);

                    }
//                    else if(edt_Password.getText().toString().replaceAll("\\s+", " ").trim().length() < 6)
//                    {
//                        txt_incorrect_pw.setText("Password must consist at least six characters");
//                        txt_incorrect_pw.setVisibility(View.VISIBLE);
//                        edt_Password.setBackgroundResource(R.drawable.edittext_red_border);
//
//                    }
                    else
                    {
                        dismissCursor();
                        if(CommonFunctions.chkStatus(mContext))
                        {
                            btn_show_pw.setVisibility(View.GONE);
                            btn_show_pw.setText(getResources().getString(R.string.btn_show_Pw));
                            edt_Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            CommonFunctions.sDialog(mContext,"Loading...");
                            email = edt_Email.getText().toString().replaceAll("\\s+", " ").trim();
                            password = edt_Password.getText().toString().replaceAll("\\s+", " ").trim();
                            Login_Request login_request = json_login_request(email, password);
                            LoginAPI.req_login_API(mContext, login_request);
                        }
                        else
                        {
                            CommonFunctions.displayToast(mContext,getResources().getString(R.string.network_connection));
                        }
                    }
                }

            }
        });

        btn_ForgotPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissCursor();
                Intent int_forgot_pw = new Intent(LoginActivity_Work.this, ForgotPasswordActivity.class);
                startActivity(int_forgot_pw);
            }
        });

        btn_JoinNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissCursor();
                Intent int_registration = new Intent(LoginActivity_Work.this, RegistrationActivity.class);
                startActivity(int_registration);
            }
        });

        rel_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissCursor();
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
                Log.e("here"," ");
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

                if (m_config.social_login_request.getEmail().equals(""))
                {
                    rel_email_popup_login.setVisibility(View.VISIBLE);

                } else {
                    // call to social login API

                }

            }

            @Override
            public void failure(TwitterException exception) {
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
                dismissCursor();
                if(CommonFunctions.chkStatus(mContext)) {
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
                    CommonFunctions.sDialog(mContext,"Loading...");
                    String email = edt_email_popup.getText().toString().replaceAll("\\s+", " ").trim();
                    m_config.social_login_request.setEmail(email);
                    LoginAPI.req_sociallogin_API(mContext, m_config.social_login_request);
                    resetAllfields();
                    Log.e("social_login_request", "  " + m_config.social_login_request.getEmail() + " " + m_config.social_login_request.getFullname() + " " + m_config.social_login_request.getProfilepic());

                }
            }

        });

        rel_email_popup_login.setOnClickListener(null);
    }

    private void validateFields()
    {
        if(edt_Email.getText().toString().replaceAll("\\s+", " ").trim().length()>0 && edt_Password.getText().toString().replaceAll("\\s+", " ").trim().length()>0)
        {
            btn_Login.setBackgroundResource(R.drawable.btn_border_orange);
            btn_Login.setEnabled(true);
            btn_Login.setTextColor(R.color.btn_Login);
        }
        else
        {
            btn_Login.setBackgroundResource(R.drawable.btn_border_grey);
            btn_Login.setEnabled(false);
            btn_Login.setTextColor(R.color.grey);

        }


        if(edt_Password.getText().toString().replaceAll("\\s+"," ").trim().length() > 0)
        {
            btn_show_pw.setEnabled(true);

        }
        else
        {
            btn_show_pw.setEnabled(false);

        }
    }

    private void initialiseUI()
    {

        RelativeLayout rel_login_main = (RelativeLayout) findViewById(R.id.rel_login_main);
        RelativeLayout rel_login_main1 = (RelativeLayout) findViewById(R.id.rel_login_main1);
        rel_login_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissCursor();
            }
        });

        rel_login_main1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissCursor();
            }
        });

        edt_Email = (EditText) findViewById(R.id.edt_Email);
        edt_Password = (EditText) findViewById(R.id.edt_Password);
        txt_incorrect_email = (TextView) findViewById(R.id.txt_incorrect_email);
        txt_incorrect_pw = (TextView) findViewById(R.id.txt_incorrect_pw);
        btn_show_pw = (TextView) findViewById(R.id.btn_show_pw);
        btn_Login = (Button) findViewById(R.id.btn_Login);
        btn_ForgotPW = (Button) findViewById(R.id.btn_ForgotPW);
        btn_JoinNow = (Button) findViewById(R.id.btn_JoinNow);
        rel_fb = (RelativeLayout) findViewById(R.id.rel_fb);
        //rel_twitter = (RelativeLayout) findViewById(R.id.rel_twt);
        btn_twitterlogin = (CannonballTwitterLoginButton) findViewById(R.id.btn_twitterlogin);
        rel_gplus = (RelativeLayout) findViewById(R.id.rel_gplus);

        //popup for Email
        rel_email_popup_login = (RelativeLayout) findViewById(R.id.rel_email_popup_login);
        edt_email_popup = (EditText) findViewById(R.id.edt_email_popup);
        btn_submitemail_popup = (Button) findViewById(R.id.btn_submitemail_popup);
        txt_incorrect_email_popup = (TextView) findViewById(R.id.txt_incorrect_email_popup);
    }

    public void dismissCursor()
    {
        //Meghana
        //Clear Focus from all edit texts
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_Email.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edt_Password.getWindowToken(), 0);
        edt_Email.clearFocus();
        edt_Password.clearFocus();
        txt_incorrect_email.setText("");
        txt_incorrect_email.setVisibility(View.GONE);
        //edt_Email.setBackgroundResource(R.drawable.edittext_border);
        txt_incorrect_pw.setText("");
        txt_incorrect_pw.setVisibility(View.GONE);
        //edt_Password.setBackgroundResource(R.drawable.edittext_border);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (flag_check_login.equals("gplus"))//requestCode == Constcore.RC_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            int statusCode = result.getStatus().getStatusCode();
            Log.e("status code", " " + statusCode);
            Log.e("Gplus result", " " + result.isSuccess());
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
        else if (flag_check_login.equals("fb"))
        {
            m_config.callbackManager.onActivityResult(requestCode, resultCode, data);
            flag_check_login = "";
        }
        else {
            btn_twitterlogin.onActivityResult(requestCode, resultCode, data);
            flag_check_login = "";
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
//        Auth.GoogleSignInApi.signOut(mGoogleApiClient);  //signout from google plus login
//        LoginManager.getInstance().logOut(); //logout from fb
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
        //Log.d("fb","logout");
        //logout from facebook
        // LoginManager.getInstance().logOut();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {

        if(rel_email_popup_login.getVisibility() == View.VISIBLE)
        {
            rel_email_popup_login.setVisibility(View.GONE);
        }
        else
        {
            super.onBackPressed();
        }

    }

    public Login_Request json_login_request(String email, String password) {
        Login_Request login_request = new Login_Request();
        login_request.setClient_id(Constcore.client_Id);
        login_request.setClient_secret(Constcore.client_Secret);
        login_request.setEmail(email);
        login_request.setPassword(password);
        login_request.setDevice_id(device_id);
        login_request.setPush_token("");

        return login_request;
    }


    private void resetAllfields()
    {
        rel_email_popup_login.setVisibility(View.GONE);
        edt_email_popup.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_email_popup.getWindowToken(), 0);
        edt_email_popup.clearFocus();
        txt_incorrect_email_popup.setText("");
        txt_incorrect_email_popup.setVisibility(View.GONE);
        edt_email_popup.setBackgroundResource(R.drawable.edittext_border);

    }

    public void setErrorMsg(String condition, String error_msg)
    {
        if(condition.equals("email"))
        {
            txt_incorrect_email.setText(error_msg);
            txt_incorrect_email.setVisibility(View.VISIBLE);
            edt_Email.setBackgroundResource(R.drawable.edittext_red_border);

        }
        else if(condition.equals("password"))
        {
            txt_incorrect_pw.setText(error_msg);
            txt_incorrect_pw.setVisibility(View.VISIBLE);
            edt_Password.setBackgroundResource(R.drawable.edittext_red_border);
        }

    }


    public int getHeight() {
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

            }
        });
        return height[0];
    }




}
