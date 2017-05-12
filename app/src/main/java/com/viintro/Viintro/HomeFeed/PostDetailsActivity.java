package com.viintro.Viintro.HomeFeed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetDialog;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.gson.Gson;
import com.thefinestartist.ytpa.YouTubePlayerActivity;
import com.thefinestartist.ytpa.enums.Orientation;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.viintro.Viintro.Search_Post.PostAdapter.search_post_update_comments;
import static com.viintro.Viintro.Search_Post.PostAdapter.search_post_update_like;

public class PostDetailsActivity extends Activity
{

    private ImageView img_play_pause, img_avatar;
    private String url;
    private TextView txt_Fullname, txt_Designation, txt_no_of_views;
    private LinearLayout lin_name,llLikes,lin_btn_like_comment_share;
    private LinearLayout lin_like, lin_comment, lin_share;
    private TextView txt_comment;
    private TextView txt_share;
    private ImageView img_comment;
    private ImageView img_share;
    private View divider;
    private boolean flag_onresume = false;
    private Context context;
    private String post_id;
    private int like_count = 0;
    private int comment_count = 0;
    private int view_count = 0;


    public static TextView txt_no_of_likes, txt_no_of_comments, txt_like;
    private static ImageView img_like;
    private static Boolean like_flag = false;
    public static int position;
    private String from_Activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_post_details);
        context = this;
        like_count = 0;
        comment_count = 0;
        view_count = 0;

        /*Post post_data = new Post();
        String user_obj = getIntent().getExtras().getString("user");
        Log.e("user_obj", " "+user_obj);
        String video_obj = getIntent().getExtras().getString("video");
        Log.e("video_obj", " "+video_obj);
        Gson gson = new Gson();
        User user = gson.fromJson(user_obj,User.class);
        Video video = gson.fromJson(video_obj, Video.class);

        post_data.setId(getIntent().getExtras().getString("id"));
        post_data.setDescription(getIntent().getExtras().getString("description"));
        post_data.setUser(user);
        post_data.setVideo(video);
        post_data.setLikes(getIntent().getExtras().getInt("likes"));
        post_data.setComments(getIntent().getExtras().getInt("comments"));
        post_data.setView_count(getIntent().getExtras().getInt("view_count"));
        post_data.setUser_liked(getIntent().getExtras().getBoolean("user_liked"));

        post_id = post_data.getId();
        position = getIntent().getExtras().getInt("position");
        initialiseUI(post_data);*/


        position = getIntent().getExtras().getInt("position");
        post_id = getIntent().getExtras().getString("id");
        from_Activity = getIntent().getExtras().getString("from_Activity");

        if(CommonFunctions.chkStatus(context))
        {
            CommonFunctions.sDialog(context,"Loading...");
            req_get_post_API(context, from_Activity);
        }
        else
        {
            CommonFunctions.displayToast(context,getResources().getString(R.string.network_connection));
            return;
        }

    }

    private void initialiseUI(final Post post_data)
    {
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
        img_play_pause = (ImageView) findViewById(R.id.img_play_pause);
        lin_like = (LinearLayout) findViewById(R.id.lin_like);
        lin_comment = (LinearLayout) findViewById(R.id.lin_comment);
        lin_share = (LinearLayout) findViewById(R.id.lin_share);
        txt_like = (TextView) findViewById(R.id.txt_like);
        txt_comment = (TextView) findViewById(R.id.txt_comment);
        txt_share = (TextView) findViewById(R.id.txt_share);
        img_like = (ImageView) findViewById(R.id.img_like);
        img_comment = (ImageView) findViewById(R.id.img_comment);
        img_share = (ImageView) findViewById(R.id.img_share);

        ImageView img_thumbnail = (ImageView) findViewById(R.id.img_thumbnail);
        
        final Owner owner = post_data.getOwner();
        final Video video = post_data.getVideo();
        
        if (owner.getDisplay_pic() != null)
        {
            Glide.with(context).load(Uri.parse(owner.getDisplay_pic())).centerCrop().into(img_avatar);
        }
        else
        {
            Glide.with(context).load(R.drawable.ic_launcher).centerCrop().into(img_avatar);
        }

        
        if(video.getThumbnail() != null)
        {
            Glide.with(context).load(Uri.parse(video.getThumbnail())).centerCrop().into(img_thumbnail);
        }
        else
        {
            Glide.with(context).load(R.drawable.ic_launcher).centerCrop().into(img_thumbnail);
        }

        like_count = post_data.getLikes();
        comment_count = post_data.getComments();
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

        img_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = post_data.getId();
                String description = "";
                if(post_data.getDescription() != null || (!post_data.getDescription().equals("")))
                {
                    description = post_data.getDescription();
                }

                int likes = post_data.getLikes();
                int comments = post_data.getComments();
                int view_count = post_data.getView_count();
                Boolean user_liked = post_data.getUser_liked();

                Gson gson = new Gson();
                String user_obj = gson.toJson(post_data.getOwner());
                String video_obj = gson.toJson(post_data.getVideo());

                if(video.getType().equals("system"))
                {
                    Intent int_post_video = new Intent(context, PostDetailsActivity_PlayVideo.class);
                    int_post_video.putExtra("position", getIntent().getExtras().getInt("position"));
                    int_post_video.putExtra("from_Activity", "Post_Details");
                    int_post_video.putExtra("id", id);
                    int_post_video.putExtra("description", description);
                    int_post_video.putExtra("user", user_obj.toString());
                    int_post_video.putExtra("video", video_obj.toString());
                    int_post_video.putExtra("likes", likes);
                    int_post_video.putExtra("comments", comments);
                    int_post_video.putExtra("view_count", view_count);
                    int_post_video.putExtra("user_liked", user_liked);
                    int_post_video.putExtra("post_slug", post_data.getSlug());
                    context.startActivity(int_post_video);
                }
                else
                {
                    String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
                    Pattern compiledPattern = Pattern.compile(pattern);
                    Matcher matcher = compiledPattern.matcher(video.getSource_default());
                    if(matcher.find()) {
                        //return matcher.group();
                        //Toast.makeText(AddLinkActivity.this, "pattern: " + matcher.group(), Toast.LENGTH_SHORT).show();
                        String videoId = matcher.group();
                        Intent intent = new Intent(context, YouTubePlayerActivity.class);
                        intent.putExtra(YouTubePlayerActivity.EXTRA_VIDEO_ID, videoId);
                        intent.putExtra(YouTubePlayerActivity.EXTRA_PLAYER_STYLE, YouTubePlayer.PlayerStyle.DEFAULT);
                        intent.putExtra(YouTubePlayerActivity.EXTRA_ORIENTATION, Orientation.AUTO);
                        intent.putExtra(YouTubePlayerActivity.EXTRA_SHOW_AUDIO_UI, true);
                        intent.putExtra(YouTubePlayerActivity.EXTRA_HANDLE_ERROR, true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ((Activity)context).startActivityForResult(intent, 1);
                    }

                }
            }
        });


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
                postLikeORUnlikeAPI.req_post_likeunlike_API(context, post_id, "Post_Details");

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
                    int_video_record.putExtra("from_Activity","Post_Details");
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
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause()
    {
        super.onPause();

    }

    // API Call to Get posts
    public void req_get_post_API(final Context cont, final String from_Activity)
    {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String bearertoken = sharedPreferences.getString(m_config.login_access_token,"");


        final Gson gson = new Gson();
        String url = "";
        if(from_Activity.equals("Post_Search"))
        {
            url = m_config.web_api_link_header + "/posts/id/"+post_id;
        }
        else
        {
            url = m_config.web_api_link_header+"/sharepost?encode=";
        }

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
                                CommonFunctions.hDialog();
                                CommonFunctions.displayToast(context, response.getString("message"));
                            }
                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Post post = gson.fromJson(response.toString(), Post.class);
                        initialiseUI(post);
                        CommonFunctions.hDialog();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        CommonFunctions.hDialog();

                        if (error.networkResponse != null) {
                            String networkresponse_data = new String(error.networkResponse.data);
                            if(error.networkResponse.statusCode == 500)
                            {
                                CommonFunctions.displayToast(context,context.getResources().getString(R.string.internal_server_error));
                            }
                            else
                            {
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

                        } else
                        {

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

    // Function called when like button is cliked and based on success or error response of api likecount is incremented or decremented
    public static void post_details_updatelike(String api_response, int like_count)
    {

        if(api_response.equals("success"))
        {
            //if response of api is success
            txt_no_of_likes.setText(String.valueOf(like_count) + " Likes");
            search_post_update_like(like_count, position);

            if(like_flag)
            {
                like_flag = false;
                txt_like.setTextColor(Color.WHITE);
                img_like.setBackgroundColor(Color.WHITE);
            }
            else
            {
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

    // Function called when comment button is cliked and based on success of api comment count is incremented or decremented
    public static void post_details_updatecomment(String api_response, int comment_count) {
        //Log.e("comment_count"," "+comment_count);
        if(api_response.equals("success"))
        {
            txt_no_of_comments.setText(String.valueOf(comment_count) + " Comments");
            search_post_update_comments(comment_count, position);

        }

    }

    @Override
    public void onBackPressed() {
        Log.e("onBackPressed",""+flag_onresume);
        like_count = 0;
        comment_count = 0;
        view_count = 0;
        CommonFunctions.hDialog();
        finish();

    }


}
