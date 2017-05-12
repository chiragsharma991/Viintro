package com.viintro.Viintro.MyProfile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.twitter.sdk.android.Twitter;
import com.viintro.Viintro.Login.TermsnConditionsActivity;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Webservices.AccessTokenAPI;
import com.viintro.Viintro.Webservices.LogoutAPI;

import static com.viintro.Viintro.Webservices.LogoutAPI.req_logout_API;

/**
 * Created by rkanawade on 14/04/17.
 */

public class SettingsActivity extends AppCompatActivity {
    private Context context;
    private GoogleApiClient mGoogleApiClient;
    SharedPreferences sharedPreferences;
    private Button btn_privacy, btn_terms, btn_change_pswd, btn_rate, btn_share, btn_logout, btn_invite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_settings);
        context = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        TextView txt_Header = (TextView) findViewById(R.id.header);
        txt_Header.setText("Settings");
        txt_Header.setTextColor(Color.BLACK);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.WHITE);

        initializaUI();

    }

    public void initializaUI(){

        btn_privacy = (Button) findViewById(R.id.btn_privacy);
        btn_terms = (Button) findViewById(R.id.btn_terms);
        btn_change_pswd = (Button) findViewById(R.id.btn_change_pswd);
        btn_rate = (Button) findViewById(R.id.btn_rate);
        btn_share = (Button) findViewById(R.id.btn_share);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_invite = (Button) findViewById(R.id.btn_invite);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getResources().getString(R.string.logout));
                builder.setCancelable(false);
                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //dialog.cancel();
                                //logout from facebook
                                //logout from google plus
                                //   if(mGoogleApiClient.isConnected()){
//                                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                                        new ResultCallback<Status>() {
//                                            @Override
//                                            public void onResult(Status status) {
//                                                // ...
//                                                // Intent i=new Intent(getApplicationContext(),MainActivity.class);
//                                                // startActivity(i);
//                                                System.exit(0);
//                                            }
//                                        });
                                //        }
                                //logout from twitter
//                                CookieSyncManager.createInstance(SettingsActivity.this);
//                                CookieManager cookieManager = CookieManager.getInstance();
//                                cookieManager.removeSessionCookie();
                           //     Twitter.getSessionManager().clearActiveSession();
//                                Twitter.logOut();
//                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                editor.putBoolean("login_done", false);
//                                editor.putBoolean("onboard_process", false);
//                                editor.putBoolean("profile_video", false);
//                                editor.putBoolean("my_profile_flag",false);
//                                editor.commit();
                                req_logout_API(context);
//                                Intent intent = getIntent();
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                finish();
//                                System.exit(0);
                            }
                        });

                builder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String shareBody = "Check out the Viintro app for Android at http://bit.ly/2oyc5Nz and at http://apple.co/2nLOzzE for iOS.";
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));

            }
        });

        btn_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent terms_conditions = new Intent(SettingsActivity.this, TermsnConditionsActivity.class);
                startActivity(terms_conditions);
            }
        });

        btn_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent privacy_policies = new Intent(context, TermsnConditionsActivity.class);
                startActivity(privacy_policies);
            }
        });

    }
}
