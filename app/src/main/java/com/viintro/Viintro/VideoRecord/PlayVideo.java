package com.viintro.Viintro.VideoRecord;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.viintro.Viintro.Landing.VideoControllerView;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.VideoRecord.VideoRecordingActivity;

import java.io.IOException;

public class PlayVideo extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControl, VideoControllerView.VideoController {

    private SurfaceView videoSurface;
    private MediaPlayer player;
    private FrameLayout framelayout;
    private VideoControllerView controller;
    private ImageView ipause;
    private String url;
    private LinearLayout lin_name,llLikes,lin_btn_like_comment_share;
    private LinearLayout btn_like, btn_comment, btn_share;
    private View divider;
    private boolean flag_stop = false;
    private boolean flag_onresume = false;
    private Context context;

    public static void start(Context context, String url)
    {
        Intent intent = new Intent(context, PlayVideo.class);
        intent.putExtra("Current url",url);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_play_video);
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

    }

    public void mediaContent(){

        initialise();

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
                    Log.e("videoWidth "," "+videoWidth+" "+videoHeight+" "+videoProportion);

                    // Get the width of the screen
                    int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
                    int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
                    float screenProportion = (float) screenWidth / (float) screenHeight;
                    Log.e("screenWidth "," "+screenWidth+" "+screenHeight+" "+screenProportion);


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
                    CommonFunctions.hDialog_PlayVideo();
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


        ipause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player.isPlaying())
                {
                    flag_stop = true;
                    player.pause();


                }
                else
                {
                    flag_stop = false;
                    player.start();


                }
            }
        });
    }

    private void initialise() {

        videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
        lin_name = (LinearLayout) findViewById(R.id.lin_name);
        llLikes = (LinearLayout) findViewById(R.id.llLikes);
        lin_btn_like_comment_share = (LinearLayout) findViewById(R.id.lin_btn_like_comment_share);
        divider = (View) findViewById(R.id.divider);
        btn_like = (LinearLayout) findViewById(R.id.btn_like);
        btn_comment = (LinearLayout) findViewById(R.id.btn_comment);
        btn_share = (LinearLayout) findViewById(R.id.btn_share);
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);

        player = new MediaPlayer();
        controller = new VideoControllerView(this, this);
        framelayout = (FrameLayout) findViewById(R.id.framelayot1);
        ipause = (ImageView) findViewById(R.id.img_play_pause);

        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show();
//        ipause.setVisibility(View.VISIBLE);
//        llLikes.setVisibility(View.VISIBLE);
//        divider.setVisibility(View.VISIBLE);
//        lin_btn_like_comment_share.setVisibility(View.VISIBLE);

        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        controller.setMediaPlayer(this);
        controller.setAnchorView(framelayout);
        player.start();
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
        //controller.hide();
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
        Log.e("orientation"," "+getResources().getConfiguration().orientation);

        int videoWidth = player.getVideoWidth();
        int videoHeight = player.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        Log.e("videoWidth "," "+videoWidth+" "+videoHeight+" "+videoProportion);

        // Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        Log.e("screenWidth "," "+screenWidth+" "+screenHeight+" "+screenProportion);

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
        Log.e("showViews"," "+val);
        if(val == true)
        {  //on touch of screen
            ipause.setVisibility(View.VISIBLE);
            lin_name.setVisibility(View.VISIBLE);
            llLikes.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
            lin_btn_like_comment_share.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void hideviews(Boolean val) {
        Log.e("hideviews"," "+val+" "+flag_stop);
//        if(val == true && flag_stop == false)
//        {
            //after timeout
            ipause.setVisibility(View.GONE);
            lin_name.setVisibility(View.GONE);
            llLikes.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
            lin_btn_like_comment_share.setVisibility(View.GONE);
//        }
//        else
//        {
//            // on pause click
//            ipause.setVisibility(View.VISIBLE);
//            lin_name.setVisibility(View.VISIBLE);
//            llLikes.setVisibility(View.VISIBLE);
//            divider.setVisibility(View.VISIBLE);
//            lin_btn_like_comment_share.setVisibility(View.VISIBLE);
//
//
//
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(flag_onresume == true)
        {
            player.stop();
            flag_stop = false;
            flag_onresume = false;
            finish();
            PlayVideo.start(this, url);

        }
        flag_onresume = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        CommonFunctions.hDialog_PlayVideo();
    }
}
