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
import com.viintro.Viintro.Model.Owner;
import com.viintro.Viintro.Model.Post;
import com.viintro.Viintro.Model.User;
import com.viintro.Viintro.Model.Video;
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

import static com.viintro.Viintro.HomeFeed.PostDetailsActivity.post_details_updatecomment;
import static com.viintro.Viintro.HomeFeed.PostDetailsActivity.post_details_updatelike;
import static com.viintro.Viintro.Search_Post.PostAdapter.search_post_update_comments;
import static com.viintro.Viintro.Search_Post.PostAdapter.search_post_update_like;


public class PostDetailsActivity_PlayVideo extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControl, VideoControllerView.VideoController {

    private SurfaceView videoSurface;
    private MediaPlayer player;
    private FrameLayout framelayout;
    private VideoControllerView controller;
    private ImageView img_play_pause, img_avatar;
    private TextView txt_Fullname, txt_Designation, txt_no_of_views;
    private LinearLayout lin_name,llLikes,lin_btn_like_comment_share;
    private LinearLayout lin_like, lin_comment, lin_share;
    private TextView txt_comment;
    private TextView txt_share;
    private ImageView img_comment;
    private ImageView img_share;
    private View divider;
    private boolean flag_stop = false;
    private boolean flag_onresume = false;
    private Context context;
    private String post_id, url;
    private int like_count = 0;
    private int comment_count = 0;
    private int view_count = 0;

    private static Post post_data;
    private static Boolean like_flag = false;
    public static  TextView txt_no_of_comments, txt_no_of_likes, txt_like;
    private static ImageView img_like;
    private static int position;
    private static String from_Activity;


    public static void start(Context context, int position, Post post_data, String from_Activity)
    {
        String id = post_data.getId();
        String description = post_data.getDescription();
        int likes = post_data.getLikes();
        int comments = post_data.getComments();
        int view_count = post_data.getView_count();
        Boolean user_liked = post_data.getUser_liked();

        Gson gson = new Gson();
        String user_obj = gson.toJson(post_data.getOwner());
        String video_obj = gson.toJson(post_data.getVideo());

        Intent int_post_video = new Intent(context, PostDetailsActivity_PlayVideo.class);
        int_post_video.putExtra("position", position);
        int_post_video.putExtra("from_Activity",from_Activity);
        int_post_video.putExtra("id",id);
        int_post_video.putExtra("description",description);
        int_post_video.putExtra("user",user_obj.toString());
        int_post_video.putExtra("video",video_obj.toString());
        int_post_video.putExtra("likes",likes);
        int_post_video.putExtra("comments",comments);
        int_post_video.putExtra("view_count",view_count);
        int_post_video.putExtra("user_liked",user_liked);
        context.startActivity(int_post_video);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_post_details_video);
        context = this;
        like_count = 0;
        comment_count = 0;
        view_count = 0;

        CommonFunctions.sDialog_PlayVideo(context,"Loading...");
        post_data = new Post();

        String user_obj = getIntent().getExtras().getString("user");
        String video_obj = getIntent().getExtras().getString("video");
        Gson gson = new Gson();
        Owner owner = gson.fromJson(user_obj,Owner.class);
        Video video = gson.fromJson(video_obj, Video.class);

        post_data.setId(getIntent().getExtras().getString("id"));
        post_data.setDescription(getIntent().getExtras().getString("description"));
        post_data.setOwner(owner);
        post_data.setVideo(video);
        post_data.setLikes(getIntent().getExtras().getInt("likes"));
        post_data.setComments(getIntent().getExtras().getInt("comments"));
        post_data.setView_count(getIntent().getExtras().getInt("view_count"));
        post_data.setUser_liked(getIntent().getExtras().getBoolean("user_liked"));
        post_data.setSlug(getIntent().getExtras().getString("post_slug"));

        post_id = post_data.getId();
        position = getIntent().getExtras().getInt("position");
        url = post_data.getVideo().getSource_default();
        from_Activity = getIntent().getExtras().getString("from_Activity");
        mediaContent();
        initialiseUI(post_data);

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

                    // // Get the dimensions of the video
                    int videoWidth = mediaPlayer.getVideoWidth();
                    int videoHeight = mediaPlayer.getVideoHeight();
                    float videoProportion = (float) videoWidth / (float) videoHeight;

                    // Get the width of the screen
                    int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
                    int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
                    float screenProportion = (float) screenWidth / (float) screenHeight;



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

    private void initialiseUI(final Post post_data) {

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


        Owner owner = post_data.getOwner();
        if (owner.getDisplay_pic() != null)
        {
            Glide.with(context).load(Uri.parse(owner.getDisplay_pic())).centerCrop().into(img_avatar);
        }
        else
        {
            Glide.with(context).load(R.drawable.ic_launcher).centerCrop().into(img_avatar);
        }

        like_count = post_data.getLikes();
        comment_count = post_data.getComments();
        view_count = post_data.getView_count();

        txt_Fullname.setText(owner.getFullname());
        txt_Designation.setText(post_data.getDescription());
        txt_no_of_likes.setText(String.valueOf(like_count) +" Likes");
        txt_no_of_comments.setText(String.valueOf(comment_count)+" Comments");
        txt_no_of_views.setText(String.valueOf(post_data.getView_count())+" Views");
        Boolean user_liked = post_data.getUser_liked();


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
                postLikeORUnlikeAPI.req_post_likeunlike_API(context, post_id, "Post_Details_Video");

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
                    int_video_record.putExtra("position", position);
                    context.startActivity(int_video_record);

                }
            }
        });

        lin_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(context, InternalShareActivity.class);
                i.putExtra("post_id",post_data.getId());
                i.putExtra("post_slug",post_data.getSlug());
                i.putExtra("post_owner_id",post_data.getOwner().getId());
                i.putExtra("post_owner_name",post_data.getOwner().getFullname());
                i.putExtra("post_description",post_data.getDescription());
                context.startActivity(i);
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
            e.printStackTrace();

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("5==="," ");
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

        int videoWidth = player.getVideoWidth();
        int videoHeight = player.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;

        // Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;

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
            PostDetailsActivity_PlayVideo.start(this, position, post_data, from_Activity);

        }
        flag_onresume = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        CommonFunctions.hDialog_PlayVideo();

    }

    public static void post_details_video_updatelike(String api_response, int like_count)
    {
        if(api_response.equals("success"))
        {
            //if response of api is success

            //update previous post details data like count
            post_data.setLikes(like_count);
            txt_no_of_likes.setText(String.valueOf(like_count) + " Likes");
            if(from_Activity.equals("Post_Details"))
            {
                post_details_updatelike(api_response, like_count);
            }
            else if(from_Activity.equals("Post_Search"))
            {
                search_post_update_like(like_count, position);
            }


            if(like_flag)
            {
                //update previous post details data user_liked flag
                post_data.setUser_liked(false);
                //
                like_flag = false;
                txt_like.setTextColor(Color.WHITE);
                img_like.setBackgroundColor(Color.WHITE);
            }
            else
            {
                //update previous post details data user_liked flag
                post_data.setUser_liked(true);
                //
                like_flag = true;
                txt_like.setTextColor(Color.RED);
                img_like.setBackgroundColor(Color.RED);
            }

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

    public static void post_details_video_updatecomment(String api_response, int comment_count) {
        if(api_response.equals("success"))
        {
            //update previous post details data comment count
            post_data.setComments(comment_count);
            txt_no_of_comments.setText(String.valueOf(comment_count) + " Comments");
            if(from_Activity.equals("Post_Details"))
            {
                post_details_updatecomment(api_response, comment_count);
            }
            else if(from_Activity.equals("Post_Search"))
            {
                search_post_update_comments(comment_count, position);
            }
        }

    }

    @Override
    public void onBackPressed() {
        //Log.e("onBackPressed",""+flag_onresume);
        like_count = 0;
        comment_count = 0;
        view_count = 0;
        CommonFunctions.hDialog_PlayVideo();
        controller.hide();
        player.release();
        finish();
    }



}
