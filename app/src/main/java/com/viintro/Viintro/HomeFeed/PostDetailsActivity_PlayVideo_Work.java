package com.viintro.Viintro.HomeFeed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Landing.VideoControllerView;
import com.viintro.Viintro.Model.GetPost_Response;
import com.viintro.Viintro.Model.User;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.VideoRecord.VideoRecordingActivity;
import com.viintro.Viintro.ViintroApplication;
import com.viintro.Viintro.Webservices.PostLikeORUnlikeAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class PostDetailsActivity_PlayVideo_Work extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControl, VideoControllerView.VideoController {

    private SurfaceView videoSurface;
    private MediaPlayer player;
    private FrameLayout framelayout;
    private VideoControllerView controller;
    private ImageView img_play_pause, img_avatar;
    private TextView txt_Fullname, txt_Designation, txt_no_of_likes, txt_no_of_views;
    public static  TextView txt_no_of_comments;
    private LinearLayout lin_name,llLikes,lin_btn_like_comment_share;
    private LinearLayout lin_like, lin_comment, lin_share;
    private TextView txt_like, txt_comment, txt_share;
    private ImageView img_like, img_comment, img_share;
    private View divider;
    private boolean flag_stop = false;
    private boolean flag_onresume = false;
    private Context context;
    String post_id, url;
    private Boolean like_flag = false;
    private int like_count = 0;
    private int comment_count = 0;


    public static void start(Context context, String post_id, String url)
    {
        Intent intent = new Intent(context, PostDetailsActivity_PlayVideo_Work.class);
        intent.putExtra("post_id",post_id);
        intent.putExtra("url", url);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_post_details_video);
        context = this;
        post_id = getIntent().getStringExtra("post_id");
        url = getIntent().getStringExtra("url");
        like_count = 0;
        comment_count = 0;

        if(CommonFunctions.chkStatus(context))
        {
            CommonFunctions.sDialog_PlayVideo(context,"Loading...");
            mediaContent();
            req_get_post_API(context);
        }
        else
        {
            CommonFunctions.displayToast(context,getResources().getString(R.string.network_connection));
            return;
        }



    }

    public void mediaContent()
    {
        framelayout = (FrameLayout) findViewById(R.id.framelayot1);
        img_play_pause = (ImageView) findViewById(R.id.img_play_pause);
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

                    Log.e("in setOnVideoSizeChangedListener"," ");
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


        img_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player.isPlaying())
                {
                    flag_stop = true;
                    player.pause();
                    controller.hide();


                }
                else
                {
                    flag_stop = false;
                    player.start();
                    controller.show();


                }
            }
        });
    }

    private void initialiseUI(final GetPost_Response getPost_response) {

        videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
        lin_name = (LinearLayout) findViewById(R.id.lin_name);
        llLikes = (LinearLayout) findViewById(R.id.llLikes);
        lin_btn_like_comment_share = (LinearLayout) findViewById(R.id.lin_btn_like_comment_share);
        divider = (View) findViewById(R.id.divider);
        img_avatar = (ImageView) findViewById(R.id.img_avatar);
        txt_Fullname = (TextView) findViewById(R.id.txt_FullName);
        txt_Designation = (TextView) findViewById(R.id.txt_Designation);
        txt_no_of_likes = (TextView) findViewById(R.id.txt_no_of_likes);
        txt_no_of_comments = (TextView) findViewById(R.id.txt_no_of_comments);
        txt_no_of_views = (TextView) findViewById(R.id.txt_no_of_views);
        lin_like = (LinearLayout) findViewById(R.id.lin_like);
        lin_comment = (LinearLayout) findViewById(R.id.lin_comment);
        lin_share = (LinearLayout) findViewById(R.id.lin_share);
        txt_like = (TextView) findViewById(R.id.txt_like);
        txt_comment = (TextView) findViewById(R.id.txt_comment);
        txt_share = (TextView) findViewById(R.id.txt_share);
        img_like = (ImageView) findViewById(R.id.img_like);
        img_comment = (ImageView) findViewById(R.id.img_comment);
        img_share = (ImageView) findViewById(R.id.img_share);


        User user = getPost_response.getUser();
        if (user.getDisplay_pic() != null)
        {
            Glide.with(context).load(Uri.parse(user.getDisplay_pic())).centerCrop().into(img_avatar);
        }

        like_count = getPost_response.getLikes();
        comment_count = getPost_response.getComments();

        txt_Fullname.setText(user.getFullname());
        txt_Designation.setText(getPost_response.getDescription());
        txt_no_of_likes.setText(String.valueOf(like_count) +" Likes");
        txt_no_of_comments.setText(String.valueOf(comment_count)+" Comments");
        txt_no_of_views.setText(String.valueOf(getPost_response.getView_count())+" Views");
        Boolean user_liked = getPost_response.getUser_liked();


        if(user_liked)
        {
            txt_like.setTextColor(Color.RED);
            img_like.setBackgroundColor(Color.RED);
            like_flag = true;
        }
        else
        {
            txt_like.setTextColor(Color.WHITE);
            img_like.setBackgroundColor(Color.WHITE);
            like_flag = false;
        }

        lin_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if(like_flag == true)
                {
                    txt_like.setTextColor(Color.WHITE);
                    img_like.setBackgroundColor(Color.WHITE);
                }
                else
                {
                    txt_like.setTextColor(Color.RED);
                    img_like.setBackgroundColor(Color.RED);
                }

                // check user_liked status for post
                ViintroApplication.getInstance().cancelPendingRequests("post_likeorunlike_api");
                PostLikeORUnlikeAPI postLikeORUnlikeAPI = new PostLikeORUnlikeAPI();
                //postLikeORUnlikeAPI.req_post_likeunlike_API(context, post_id);

            }
        });

        lin_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(CommonFunctions.checkCameraHardware(context))
                {
                    Intent int_video_record = new Intent(context, VideoRecordingActivity.class);
                    int_video_record.putExtra("from","comment");
                    int_video_record.putExtra("from_Activity","Post_Details_Video");
                    int_video_record.putExtra("post_id",post_id);
                    context.startActivity(int_video_record);

                }
            }
        });

        lin_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show();

        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        controller.setMediaPlayer(this);
        controller.setAnchorView(framelayout);
        player.start();
        CommonFunctions.hDialog_PlayVideo();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay(holder);
        try
        {
            player.prepareAsync();

        }
        catch (IllegalStateException e)
        {
            Log.e("on catch surfaceCreated"," ");
            e.printStackTrace();

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e("4==="," ");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("5==="," ");
    }

    @Override
    public void start() {
        player.start();
        Log.e("6==="," ");
    }

    @Override
    public void pause() {
        //controller.hide();
        player.stop();
        Log.e("7==="," ");

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

    }



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
            img_play_pause.setVisibility(View.VISIBLE);
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
            img_play_pause.setVisibility(View.GONE);
            lin_name.setVisibility(View.GONE);
            llLikes.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
            lin_btn_like_comment_share.setVisibility(View.GONE);
//        }
//        else
//        {
//            // on pause click
//            img_play_pause.setVisibility(View.VISIBLE);
//            lin_name.setVisibility(View.VISIBLE);
//            llLikes.setVisibility(View.VISIBLE);
//            divider.setVisibility(View.VISIBLE);
//            lin_btn_like_comment_share.setVisibility(View.VISIBLE);
//
//
//
//        }
    }


    // API Call to Get posts
    public void req_get_post_API(final Context cont)
    {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String bearertoken = sharedPreferences.getString(m_config.login_access_token,"");


        final Gson gson = new Gson();
        String url = m_config.web_api_link_header + "/posts/id/"+post_id;

        // prepare the Request
        JsonObjectRequest profilerequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        int code = -1;
                        try {
                            Log.e("response code", " " + response.getInt("code"));
                            code = response.getInt("code");
                            if (code != -1) {
                                CommonFunctions.hDialog_PlayVideo();
                                CommonFunctions.displayToast(context, response.getString("message"));
                            }
                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        GetPost_Response getPost_response = gson.fromJson(response.toString(), GetPost_Response.class);
                        initialiseUI(getPost_response);



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CommonFunctions.hDialog_PlayVideo();
                        if (error.networkResponse != null) {
                            String networkresponse_data = new String(error.networkResponse.data);
                            if(error.networkResponse.statusCode == 500)
                            {
                                CommonFunctions.displayToast(context,context.getResources().getString(R.string.internal_server_error));
                            }
                            else {
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
                                    }

                                }
                            }
                        } else {

                            CommonFunctions.displayToast(context, context.getResources().getString(R.string.post_details_open_failed));
                        }


                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("version", Constcore.version);
                params.put("devicetype", "android");
                params.put("timezone:IST", CommonFunctions.getTimeZoneIST());
                params.put("Authorization","Bearer "+bearertoken);
                params.put("timesstamp",CommonFunctions.getTimeStamp());
                params.put("os_version",m_config.os_version);
                return params;
            }
        };

        // add it to the RequestQueue
        int socketTimeout = 10000;//60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        profilerequest.setRetryPolicy(policy);

        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(profilerequest, "req_post_details_api");

    }


    @Override
    protected void onResume() {
        super.onResume();
        if(flag_onresume == true)
        {
            player.stop();
            flag_stop = false;
            flag_onresume = false;
            ViintroApplication.getInstance().cancelPendingRequests("req_post_details_api");
            finish();
            PostDetailsActivity_PlayVideo_Work.start(this, post_id, url);

        }
        flag_onresume = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        CommonFunctions.hDialog_PlayVideo();

    }

    public void updatelike(String api_response, int like_count)
    {
        if(api_response.equals("success"))
        {
            //if response of api is success
            txt_no_of_likes.setText(String.valueOf(like_count) + " Likes");
            if(like_flag)
            {
                like_flag = false;
            }
            else
            {
                like_flag = true;
            }

//            Intent i = new Intent();
//            i.putExtra("like_count", like_count);
//            i.putExtra("like_flag", like_flag);
//            setResult(RESULT_OK,i);


        }
        if(api_response.equals("error"))
        {
            //if response of api is error
            if(like_flag)
            {
                txt_like.setTextColor(Color.RED);
                img_like.setBackgroundColor(Color.RED);
            }
            else
            {
                txt_like.setTextColor(Color.WHITE);
                img_like.setBackgroundColor(Color.WHITE);
            }

        }

    }

    public void updatecomment(String api_response, int comment_count) {
        if(api_response.equals("success"))
        {
            txt_no_of_comments.setText(String.valueOf(comment_count) + " Comments");
        }

    }


    @Override
    public void onBackPressed() {
        Log.e("onBackPressed",""+flag_onresume);
        like_count = 0;
        CommonFunctions.hDialog_PlayVideo();
        controller.hide();
        player.release();
        finish();

    }



}
