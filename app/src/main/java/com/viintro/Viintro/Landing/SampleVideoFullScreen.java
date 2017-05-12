package com.viintro.Viintro.Landing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.VideoRecord.PlayVideo;
import com.viintro.Viintro.VideoRecord.VideoRecordingActivity;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import static com.viintro.Viintro.Webservices.Profile_Video_Count_Incr.req_profile_video_count_incr;

public class SampleVideoFullScreen extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControl, VideoControllerView.VideoController {

    private SurfaceView videoSurface;
    private  MediaPlayer player;
    private FrameLayout videoSurfaceContainer;
    private  VideoControllerView controller;
    private ImageView ipause;
    private String url;
    private Boolean flag_onresume = false;
    private Context context;
    int fadeCount = 0;
    AlphaAnimation out;
    TextView txt;


    public static void start(Context context, String url)
    {
        Intent intent = new Intent(context, SampleVideoFullScreen.class);
        intent.putExtra("Current url",url);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_sample_video_full_screen);
        context = this;
        Intent intent = getIntent();
        url = intent.getExtras().getString("Current url");
        if(CommonFunctions.chkStatus(context))
        {
            CommonFunctions.sDialog_PlayVideo(context,"Loading...");
            mediaContent();
        }
        else
        {
            CommonFunctions.displayToast(context,getResources().getString(R.string.network_connection));
            finish();
        }

        TextView txt = (TextView) findViewById(R.id.txt);
        txt.setSelected(true);
    }

    public void mediaContent()
    {
        videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);

        player = new MediaPlayer();
        controller = new VideoControllerView(this,this);

        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(this, Uri.parse(url));
            player.setOnPreparedListener(this);
            player.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
                    // // Get the dimensions of the video
                    int videoWidth = mediaPlayer.getVideoWidth();
                    int videoHeight = mediaPlayer.getVideoHeight();
                    float videoProportion = (float) videoWidth / (float) videoHeight;
                    //Log.e("videoWidth "," "+videoWidth+" "+videoHeight+" "+videoProportion);

                    // Get the width of the screen
                    int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
                    int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
                    float screenProportion = (float) screenWidth / (float) screenHeight;
                    //Log.e("screenWidth "," "+screenWidth+" "+screenHeight+" "+screenProportion);

                    // Get the SurfaceView layout parameters
                    android.view.ViewGroup.LayoutParams lp = videoSurface.getLayoutParams();
                    if (videoProportion > screenProportion) {
                        lp.width = screenWidth;
                        lp.height = (int) ((float) screenWidth / videoProportion);
                    } else {
                        lp.width = (int) (videoProportion * (float) screenHeight);
                        lp.height = screenHeight;
                    }
                    // Commit the layout parameters
                    videoSurface.setLayoutParams(lp);
                    //CommonFunctions.hDialog_PlayVideo();




                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            CommonFunctions.displayToast(context,e.getMessage().toString());
            CommonFunctions.hDialog_PlayVideo();
        } catch (SecurityException e) {
            e.printStackTrace();
            CommonFunctions.displayToast(context,e.getMessage().toString());
            CommonFunctions.hDialog_PlayVideo();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            CommonFunctions.displayToast(context,e.getMessage().toString());
            CommonFunctions.hDialog_PlayVideo();
        } catch (IOException e) {
            e.printStackTrace();
            CommonFunctions.displayToast(context,e.getMessage().toString());
            CommonFunctions.hDialog_PlayVideo();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //  client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        videoSurfaceContainer = (FrameLayout) findViewById(R.id.videoSurfaceContainer);
        ipause = (ImageView) findViewById(R.id.ipause);

        ipause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player.isPlaying()){
                    player.pause();
                    controller.hide();
                    ipause.setVisibility(View.VISIBLE);

                }
                else{
                    player.start();
                    controller.show();
                    ipause.setVisibility(View.GONE);
                }
            }
        });


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show();
        ipause.setVisibility(View.VISIBLE);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        controller.setMediaPlayer(this);
        controller.setAnchorView(videoSurfaceContainer);
        player.start();
        CommonFunctions.hDialog_PlayVideo();
        if(getIntent().getExtras().getString("public_id") != null)
        {
            String public_id = getIntent().getExtras().getString("public_id");
            req_profile_video_count_incr(context, public_id);
        }

        //fadeFunction();

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay(holder);
        try
        {
            player.prepareAsync();

        }
        catch (IllegalStateException e){
            e.printStackTrace();

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void start() {
        player.start();

    }

    @Override
    public void pause() {
        //Added(to remove crash for illegalstateexception in player.getCurrentPosition())
        controller.hide();
        player.stop();
    }

    @Override
    public int getDuration() {
        return player.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        player.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public boolean isFullScreen() {
        //return false;
        return getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? true : false;
    }

    @Override
    public void toggleFullScreen() {
        //return getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? true : false;

    }

//    public Action getIndexApiAction() {
//        Thing object = new Thing.Builder()
//                .setName("FullScreenVideo Page") // TODO: Define a title for the content shown.
//                // TODO: Make sure this auto-generated URL is correct.
//                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
//                .build();
//        return new Action.Builder(Action.TYPE_VIEW)
//                .setObject(object)
//                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
//                .build();
//    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        AppIndex.AppIndexApi.end(client, getIndexApiAction());
//        client.disconnect();
    }

    @Override
    public void onBackPressed() {
        Log.e("onBackPressed",""+flag_onresume);
        CommonFunctions.hDialog_PlayVideo();
        controller.hide();
        player.release();
        finish();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        int videoWidth = player.getVideoWidth();
        int videoHeight = player.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
//        Log.e("videoWidth "," "+videoWidth+" "+videoHeight+" "+videoProportion);

        // Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
//        Log.e("screenWidth "," "+screenWidth+" "+screenHeight+" "+screenProportion);

        // Get the SurfaceView layout parameters
        android.view.ViewGroup.LayoutParams lp = videoSurface.getLayoutParams();
        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        // Commit the layout parameters
        videoSurface.setLayoutParams(lp);
    }

    @Override
    public void showviews(Boolean val) {

    }

    @Override
    public void hideviews(Boolean val) {

    }

    @Override
    protected void onResume() {
//        Log.e("onResume"," "+flag_onresume);
        super.onResume();
        if(flag_onresume == true)
        {
            player.stop();
            flag_onresume = false;
            finish();
            SampleVideoFullScreen.start(this, url);

        }
        flag_onresume = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        CommonFunctions.hDialog_PlayVideo();
    }

    public void fadeFunction()
    {

        final Handler handler = new Handler();
        final ArrayList arrayList = new ArrayList();
        arrayList.add("Text 1 : Play Video");
        arrayList.add("Text 2 : Play Video");
        arrayList.add("Text 3 : Play Video");


        txt = (TextView) findViewById(R.id.txt);
        txt.setText((CharSequence) arrayList.get(fadeCount));

        final AlphaAnimation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(1000);

        out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(1000);
        out.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                fadeCount++;
                if (fadeCount == 3){
                    fadeCount = 0;
                    txt.setText((CharSequence) arrayList.get(fadeCount));
                    txt.startAnimation(in);
                    handler.postDelayed(mFadeOut, 1000);

                }
                else
                {
                    txt.setText((CharSequence) arrayList.get(fadeCount));
                    txt.startAnimation(in);
                    handler.postDelayed(mFadeOut, 1000);
                }
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }
        });

        txt.startAnimation(in);
        handler.postDelayed(mFadeOut, 1000);
    }

    private Runnable mFadeOut =new Runnable(){

        @Override
        public void run() {
            //Speed up the last fade-out so that the Activity opens faster

            txt.startAnimation(out);
        }
    };
}
