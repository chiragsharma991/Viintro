package com.viintro.Viintro.Splash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.HomeFeed.HomeActivity;
import com.viintro.Viintro.Landing.LandingActivity;
import com.viintro.Viintro.MyProfile.LiveVideosActivity;
import com.viintro.Viintro.OnBoarding.OnBoardingActivity;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.VideoRecord.VideoRecordingActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "DQfwFbNxTwdDhyrFQ5u8Ev7C8";
    private static final String TWITTER_SECRET = "498fgbnOWP7QTYzluC3anJ1H122DJrEN8UzgyCfXIjDHOm3CV5";
    Context context;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        //Fabric.with(this, new Crashlytics());
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);
        context = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String device_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("device_id",device_id );
        editor.commit();

        Thread timer = new Thread() {
            public void run() {
                try
                {
                    sleep(3000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if(sharedPreferences.getBoolean("login_done",false) == true)
                    {
                        if (sharedPreferences.getBoolean("onboard_process", false) == true)
                        {
                            // call to home page
                            Intent int_home_page = new Intent(context, HomeActivity.class);
                            startActivity(int_home_page);
                            finish();
                        }
                        else
                        {
                            if (sharedPreferences.getBoolean("profile_video", false) == true)
                            {
                                // call to onboard screen
                                Intent int_onboarding  = new Intent(context, OnBoardingActivity.class);
                                int_onboarding.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(int_onboarding);
                                ((Activity)context).finish();
                            }
                            else
                            {
                                // call to record video
                                if (CommonFunctions.checkCameraHardware(context))
                                {
                                    Intent int_video_record = new Intent(context, VideoRecordingActivity.class);
                                    int_video_record.putExtra("from", "onboarding");
                                    context.startActivity(int_video_record);
                                    finish();
                                }
                            }
                        }
                    }
                    else
                    {
                        Intent int_landing = new Intent(SplashActivity.this, LandingActivity.class);
                        startActivity(int_landing);
                        finish();
                    }

                }
            }

        };
        timer.start();
    }
}

/*
*    <com.paging.gridview.PagingGridView
            android:id="@+id/videoGridview"
            android:numColumns="2"
            android:gravity="center"
            android:layout_gravity="center"
            android:stretchMode="columnWidth"
            android:layout_width="match_parent"
            android:layout_centerInParent="true"
            android:layout_height="wrap_content"/>*/