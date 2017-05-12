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
import com.google.gson.Gson;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.GetPost_Response;
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

import static com.viintro.Viintro.Constants.Constcore.REQUEST_LIKE;

public class PostDetailsActivity_Work extends Activity
{

    private ImageView img_play_pause, img_avatar;
    private String url;
    private TextView txt_Fullname, txt_Designation, txt_no_of_likes, txt_no_of_views;
    public static TextView txt_no_of_comments;
    private LinearLayout lin_name,llLikes,lin_btn_like_comment_share;
    private LinearLayout lin_like, lin_comment, lin_share;
    private TextView txt_like, txt_comment, txt_share;
    private ImageView img_like, img_comment, img_share;
    private View divider;
    private boolean flag_onresume = false;
    private Context context;
    private String post_id;
    private Boolean like_flag = false;
    private int like_count = 0;
    private int comment_count = 0;


    public static void start(Context context, String post_id)
    {
        Intent intent = new Intent(context, PostDetailsActivity_Work.class);
        intent.putExtra("post_id",post_id);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_post_details);
        context = this;
        post_id = getIntent().getStringExtra("post_id");
        like_count = 0;
        comment_count = 0;

        if(CommonFunctions.chkStatus(context))
        {
            CommonFunctions.sDialog(context,"Loading...");
            req_get_post_API(context);
        }
        else
        {
            CommonFunctions.displayToast(context,getResources().getString(R.string.network_connection));
            return;
        }

    }

    private void initialiseUI(final GetPost_Response getPost_response) {


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


        final User user = getPost_response.getUser();
        if (user.getDisplay_pic() != null)
        {
            Glide.with(context).load(Uri.parse(user.getDisplay_pic())).centerCrop().into(img_avatar);
        }

        final Video video = getPost_response.getVideo();

        if(video.getThumbnail() != null)
        {
            Glide.with(context).load(Uri.parse(video.getThumbnail())).centerCrop().into(img_thumbnail);
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

        img_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int_post_video_play = new Intent(context, PostDetailsActivity_PlayVideo.class);
                int_post_video_play.putExtra("post_id",getIntent().getExtras().getString("post_id"));
                int_post_video_play.putExtra("url", video.getSource_default());
                ((Activity)context).startActivityForResult(int_post_video_play, REQUEST_LIKE);
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
                    int_video_record.putExtra("from_Activity","Post_Details");
                    int_video_record.putExtra("post_id",post_id);
                    context.startActivity(int_video_record);

                }
            }
        });

        lin_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                PopupMenu popup = new PopupMenu(wrapper, view);
                CommonFunctions.setForceShowIcon(popup);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.popup_share, popup.getMenu());
                popup.show();
                //PopupMenu popup = new PopupMenu(context, view);
                for (int i = 0; i < popup.getMenu().size(); i++)
                {
                    MenuItem items = popup.getMenu().getItem(i);
                    if(i == 2)
                    {
                        popup.getMenu().getItem(i).setTitle("Unfollow "+user.getFullname());
                    }

                    SpannableString spanstring = new SpannableString(items.getTitle().toString());
                    spanstring.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, spanstring.length(), 0); //fix the color to white


                    items.setTitle(spanstring);
                }
                popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu menu) {

                    }
                });

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {



                        if (item.getTitle().toString().equalsIgnoreCase("Share via"))
                        {
                            Log.e("share via"," ");
                        }
                        else  if (item.getTitle().toString().equalsIgnoreCase("Hide this post"))
                        {
                            Log.e("Hide"," ");
                        }
                        else  if (item.getTitle().toString().contains("Unfollow"))
                        {
                            Log.e("Unfollow "," ");
                        }
                        else  if (item.getTitle().toString().equalsIgnoreCase("Report this post"))
                        {
                            Log.e("Report "," ");
                            final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(context);
                            View sheetView = ((Activity)context).getLayoutInflater().inflate(R.layout.bottom_popup_report, null);
                            mBottomSheetDialog.setContentView(sheetView);
                            mBottomSheetDialog.show();

                            TextView txt_1 = (TextView) sheetView.findViewById(R.id.txt_inappropriate_1);
                            TextView txt_2 = (TextView) sheetView.findViewById(R.id.txt_spam_2);
                            TextView txt_3 = (TextView) sheetView.findViewById(R.id.txt_hacked_3);
                            TextView txt_4 = (TextView) sheetView.findViewById(R.id.txt_something_4);

                            txt_1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //mBottomSheetDialog.cancel();
                                }
                            });

                            txt_2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //mBottomSheetDialog.dismiss();
                                }
                            });

                            txt_3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //mBottomSheetDialog.dismiss();
                                }
                            });

                            txt_4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //mBottomSheetDialog.dismiss();
                                }
                            });

                        }
                        return true;
                    }
                });


                //popup.show();
            }
        });

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
                                CommonFunctions.hDialog();
                                CommonFunctions.displayToast(context, response.getString("message"));
                            }
                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        GetPost_Response getPost_response = gson.fromJson(response.toString(), GetPost_Response.class);
                        initialiseUI(getPost_response);
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


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

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
        Log.e("comment_count"," "+comment_count);
        if(api_response.equals("success"))
        {
            txt_no_of_comments.setText(String.valueOf(comment_count) + " Comments");
        }

    }

    @Override
    public void onBackPressed() {
        Log.e("onBackPressed",""+flag_onresume);
        like_count = 0;
        CommonFunctions.hDialog();
        finish();

    }



//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == REQUEST_LIKE && data != null)
//        {
//            txt_like.setText(data.getExtras().getString("like_count"));
//            if(data.getExtras().getBoolean("like_flag") == true)
//            {
//                txt_like.setTextColor(Color.WHITE);
//                img_like.setBackgroundColor(Color.WHITE);
//                like_flag = false;
//            }
//            else
//            {
//                txt_like.setTextColor(Color.RED);
//                img_like.setBackgroundColor(Color.RED);
//                like_flag = true;
//            }
//        }
//    }



}
