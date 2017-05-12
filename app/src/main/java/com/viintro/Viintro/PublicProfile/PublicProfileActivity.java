package com.viintro.Viintro.PublicProfile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
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
import com.viintro.Viintro.Landing.SampleVideoFullScreen;
import com.viintro.Viintro.Model.BlockUser_Request;
import com.viintro.Viintro.Model.PublicProfile_Response;
import com.viintro.Viintro.Model.UnBlockUser_Request;
import com.viintro.Viintro.Model.Videos;
import com.viintro.Viintro.MyProfile.ProfileVideosAdapter;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.VideoRecord.VideoRecordingActivity;
import com.viintro.Viintro.ViintroApplication;
import com.viintro.Viintro.Webservices.HidePostAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.viintro.Viintro.Connections.ConnectionAdapter.upd_conn_row_in_Conn_page;
import static com.viintro.Viintro.Connections.SuggestionAdapter.upd_inv_row_in_sugg_page_frm_invitation;
import static com.viintro.Viintro.Connections.SuggestionAdapter.upd_row_in_sugg_page;
import static com.viintro.Viintro.Connections.SuggestionAdapter.update_invitation_row_on_suggestion_page;
import static com.viintro.Viintro.Constants.Constcore.REQUEST_CODE_CONNECT;
import static com.viintro.Viintro.Invitations.InvitationsReceivedAdapter.update_row_on_invitation_page;
import static com.viintro.Viintro.Webservices.BlockUserAPI.req_block_user_API;
import static com.viintro.Viintro.Webservices.ConnectionAcceptAPI.req_connection_accept_API;
import static com.viintro.Viintro.Webservices.ConnectionRejectAPI.req_connection_reject_API;
import static com.viintro.Viintro.Webservices.ConnectionRemoveAPI.req_connection_remove_API;
import static com.viintro.Viintro.Webservices.CreatePostShareUrlAPI.req_postShareUrl_API;
import static com.viintro.Viintro.Webservices.FollowAPI.req_follow_request_API;
import static com.viintro.Viintro.Webservices.PostReportAPI.req_post_report_API;
import static com.viintro.Viintro.Webservices.UnblockUserAPI.req_unblock_user_API;
import static com.viintro.Viintro.Webservices.UnfollowAPI.req_Unfollow_request_API;

public class PublicProfileActivity extends AppCompatActivity
{
    private ImageView intro_video_thumbnail, img_profile;
    private TextView text_ProfileName, text_profile_designation, text_profile_company, text_profile_university, text_profile_course, text_profile_location;
    private TextView txt_followers, txt_not_connected, text_profile, text_website,text_phone;
    public static Button btn_connect, btn_follow;
    private static Button btn_More;
    private RelativeLayout rl_profile_videos, relative_contact_details;
    private LinearLayout llPhone, llWebsite, llProfile;
    RecyclerView mRecyclerView;
    PublicProfileVideosAdapter publicProfileVideosAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    Configuration_Parameter m_config;
    SharedPreferences sharedpreferences;
    private ArrayList<Videos> public_profile_video_list;
    private static Context context;
    public static String from_Activity;
    public static int user_id;
    public static TextView txt_connections, txt_following;
    LinearLayout llbtnconnectfollow;
    TextView txt_block_text;
    RelativeLayout rel_public_profile;
    int position;
    public static PopupMenu popup;
    public static Activity public_profile_activity;
    public static PublicProfile_Response publicProfile_response;
    public Boolean flag_blockunblk_clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);
        getSupportActionBar().hide();
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        m_config = Configuration_Parameter.getInstance();
        context = this;
        public_profile_activity = this;
        public_profile_video_list = new ArrayList();
        user_id = getIntent().getIntExtra("user_id",0);
        from_Activity = getIntent().getExtras().getString("from_Activity","");
        position = getIntent().getExtras().getInt("position");
        rel_public_profile = (RelativeLayout) findViewById(R.id.rel_public_profile);
        TextView txt_no_internet_connection = (TextView) findViewById(R.id.txt_no_internet_connection);
        if(CommonFunctions.chkStatus(context))
        {
            CommonFunctions.sDialog(context, "Loading data..");
            req_publicprofile_API(context, user_id);
            initialiseUI();
        }else
        {

            txt_no_internet_connection.setVisibility(View.VISIBLE);
            rel_public_profile.setVisibility(View.GONE);
            CommonFunctions.displayToast(context,getResources().getString(R.string.network_connection));
        }
        CommonFunctions.hDialog();

    }

    private void initialiseUI()
    {
        text_ProfileName = (TextView) findViewById(R.id.text_ProfileName);
        text_profile_designation = (TextView) findViewById(R.id.text_profile_designation);
        text_profile_company = (TextView) findViewById(R.id.text_profile_company);
        text_profile_university = (TextView) findViewById(R.id.text_profile_university);
        text_profile_course = (TextView) findViewById(R.id.text_profile_course);
        text_profile_location = (TextView) findViewById(R.id.text_profile_location);
        intro_video_thumbnail = (ImageView) findViewById(R.id.intro_video_thumbnail);
        img_profile = (ImageView) findViewById(R.id.img_profile);
        txt_connections = (TextView) findViewById(R.id.txt_connections);
        txt_following = (TextView) findViewById(R.id.txt_following);
        txt_followers = (TextView) findViewById(R.id.txt_followers);
        text_profile = (TextView) findViewById(R.id.text_profile);
        text_website = (TextView) findViewById(R.id.text_website);
        text_phone = (TextView) findViewById(R.id.text_phone);
        btn_connect = (Button) findViewById(R.id.btn_connect);
        btn_follow = (Button) findViewById(R.id.btn_follow);
        btn_More = (Button) findViewById(R.id.btn_More);
        txt_not_connected = (TextView) findViewById(R.id.txt_not_connected);
        rl_profile_videos = (RelativeLayout) findViewById(R.id.rl_profile_videos);
        relative_contact_details = (RelativeLayout) findViewById(R.id.relative_contact_details);
        llPhone = (LinearLayout) findViewById(R.id.llPhone);
        llProfile = (LinearLayout) findViewById(R.id.llProfile);
        llWebsite = (LinearLayout) findViewById(R.id.llWebsite);
        llbtnconnectfollow = (LinearLayout) findViewById(R.id.llbtnconnectfollow);
        txt_block_text = (TextView) findViewById(R.id.txt_block_text);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getDataInList();

    }

    private void setPublicProfileData(JSONObject response)
    {
        final Gson gson = new Gson();
        publicProfile_response = gson.fromJson(response.toString(), PublicProfile_Response.class);
        ArrayList<Videos> profile_video = publicProfile_response.getProfile_video();
        Log.e("profile_video",""+profile_video);

        String thumbnail = null;
        String video_url = null;

        final Boolean connection_requested = publicProfile_response.getConnection_requested();
        final Boolean connected_user = publicProfile_response.getConnected();
        final Boolean following = publicProfile_response.getFollowing();
        final Boolean block = publicProfile_response.getBlock();
        String fullname = publicProfile_response.getFullname();
        String location = publicProfile_response.getCity()+", "+publicProfile_response.getState()+", "+publicProfile_response.getCountry();
        String display_pic = publicProfile_response.getDisplay_pic();
        String profile_url = publicProfile_response.getProfile_url();
        String mob_no = publicProfile_response.getMobile_number();
        String website = publicProfile_response.getWebsite();
        text_profile.setText(profile_url);
        //text_phone.setText(mob_no);
        text_ProfileName.setText(fullname);
        text_profile_location.setText(location);
        txt_connections.setText(String.valueOf(publicProfile_response.getConnection_count()));
        txt_followers.setText(String.valueOf(publicProfile_response.getFollowers_count()));
        txt_following.setText(String.valueOf(publicProfile_response.getFollowing_count()));

        if(block)
        {
            txt_block_text.setText(publicProfile_response.getBlock_message());
            txt_block_text.setVisibility(View.VISIBLE);
            llbtnconnectfollow.setVisibility(View.GONE);
        }
        else
        {
            txt_block_text.setVisibility(View.GONE);
            llbtnconnectfollow.setVisibility(View.VISIBLE);
        }

        if(profile_url != null)
        {
            Log.d("==","llProfile");
            llProfile.setVisibility(View.VISIBLE);
            text_profile.setText(profile_url);
        }else
        {
            llProfile.setVisibility(View.GONE);

        }

        if(mob_no != null)
        {
            Log.d("==","llPhone");

            llPhone.setVisibility(View.VISIBLE);
            text_phone.setText(mob_no);
        }else
        {
            llPhone.setVisibility(View.GONE);

        }

        if(website != null)
        {
            Log.d("==","llWebsite");
            llWebsite.setVisibility(View.VISIBLE);
            text_website.setText(website);
        }else
        {
            llWebsite.setVisibility(View.GONE);

        }

        Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
        popup = new PopupMenu(wrapper, btn_More);
        CommonFunctions.setForceShowIcon(popup);
        MenuInflater inflater = popup.getMenuInflater();
        if(publicProfile_response.getConnected())
        {
            inflater.inflate(R.menu.popup_public_profile, popup.getMenu());
        }
        else
        {
            inflater.inflate(R.menu.popup_public_profile1, popup.getMenu());
        }



        if(profile_video != null) {
            for (int i = 0; i < profile_video.size(); i++) {
                if (profile_video.get(i).getIntro() == true) {
                    thumbnail = profile_video.get(i).getThumbnail();
                    video_url = profile_video.get(i).getVideo_mp4();

                } else {
                    public_profile_video_list.add(profile_video.get(i));

                }
            }
        }
        publicProfileVideosAdapter.notifyDataSetChanged();

        Log.e("publicProfile_response.getUser_type()", " "+publicProfile_response.getUser_type());

        if(publicProfile_response.getUser_type() != null) {
            if (publicProfile_response.getUser_type().equals("student")) {
                text_profile_course.setText(publicProfile_response.getCourse());
                text_profile_university.setText(publicProfile_response.getUniversity());
                text_profile_designation.setVisibility(View.GONE);
                text_profile_company.setVisibility(View.GONE);
            } else {
                text_profile_designation.setText(publicProfile_response.getDesignation());
                text_profile_company.setText(publicProfile_response.getCompany());
                text_profile_course.setVisibility(View.GONE);
                text_profile_university.setVisibility(View.GONE);
            }
        }


        if(!(display_pic == null)) {
            Glide.with(context)
                    .load(Uri.parse(display_pic))
                    .into(img_profile);
        }

        if(!(thumbnail == null)) {
            Glide.with(context)
                    .load(Uri.parse(thumbnail))
                    .into(intro_video_thumbnail);
        }

        if(from_Activity.equals("Invitations") || from_Activity.equals("My_Suggestions_Invitations"))
        {
            btn_connect.setText("Accept");
            txt_not_connected.setVisibility(View.GONE);
            rl_profile_videos.setVisibility(View.VISIBLE);
            relative_contact_details.setVisibility(View.VISIBLE);
            btn_follow.setText("Ignore");
        }
        else
        {

            if(connected_user)
            {
                btn_connect.setText("Connected");
                txt_not_connected.setVisibility(View.GONE);
                rl_profile_videos.setVisibility(View.VISIBLE);
                relative_contact_details.setVisibility(View.VISIBLE);
            }
            if(connected_user == false && connection_requested == true)
            {
                btn_connect.setText("Request Sent");
                txt_not_connected.setVisibility(View.VISIBLE);
                rl_profile_videos.setVisibility(View.GONE);
                relative_contact_details.setVisibility(View.GONE);
            }
            if(connected_user == false && connection_requested == false)
            {
                btn_connect.setText("Connect");
                txt_not_connected.setVisibility(View.VISIBLE);
                rl_profile_videos.setVisibility(View.GONE);
                relative_contact_details.setVisibility(View.GONE);
            }

            if (following) {
                btn_follow.setText(context.getResources().getString(R.string.Following));
            } else {
                btn_follow.setText(context.getResources().getString(R.string.Follow));
            }
        }


        final String finalVideo_url = video_url;
        intro_video_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(publicProfile_response.getConnected())
                {
                    Intent int_show_teaser = new Intent(PublicProfileActivity.this, SampleVideoFullScreen.class);
                    int_show_teaser.putExtra("Current url", finalVideo_url);
                    startActivity(int_show_teaser);
                }
                else
                {
                    Intent int_show_teaser = new Intent(PublicProfileActivity.this, TeaserVideoActivity.class);
                    int_show_teaser.putExtra("Current url", finalVideo_url);
                    startActivity(int_show_teaser);
                }


            }
        });


        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CommonFunctions.chkStatus(context))
                {
                    if (btn_connect.getText().toString().equals("Connect"))
                    {
                        if(CommonFunctions.checkCameraHardware(context))
                        {

                            Intent int_video_record = new Intent(context, VideoRecordingActivity.class);
                            int_video_record.putExtra("from", "connect_public_profile");
                            int_video_record.putExtra("user_id", publicProfile_response.getId());
                            int_video_record.putExtra("position", position);
                            int_video_record.putExtra("from_Activity", "Public_Profile");
                            ((Activity)context).startActivityForResult(int_video_record, REQUEST_CODE_CONNECT);
                        }

                    }
                    else if (btn_connect.getText().toString().equals("Accept"))
                    {

                        CommonFunctions.sDialog(context, "Accepting Request");
                        req_connection_accept_API(context, publicProfile_response.getId(), position, "Public_Profile");
                    }

                }
                else
                {
                    CommonFunctions.displayToast(context, context.getResources().getString(R.string.network_connection));
                }
            }
        });

        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CommonFunctions.chkStatus(context))
                {
//
                    if (btn_follow.getText().toString().equals(context.getResources().getString(R.string.Follow)))
                    {
                        CommonFunctions.sDialog(context, "Following...");
                        req_follow_request_API(context, publicProfile_response.getId(), position, "Public_Profile");
                    }
                    else if (btn_follow.getText().toString().equals(context.getResources().getString(R.string.Following)))
                    {
                        CommonFunctions.sDialog(context, "UnFollowing...");
                        req_Unfollow_request_API(context, publicProfile_response.getId(), position, "Public_Profile");
                    }
                    else if (btn_follow.getText().toString().equals("Ignore"))
                    {
                        CommonFunctions.sDialog(context, "Rejecting Request");
                        req_connection_reject_API(context, publicProfile_response.getId(), position, "Public_Profile");
                    }

                }
                else
                {
                    CommonFunctions.displayToast(context, context.getResources().getString(R.string.network_connection));
                }
            }
        });


        btn_More.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popup.show();

                Log.e("block "," "+publicProfile_response.getBlock()+" "+publicProfile_response.getFollowing());

                for (int i = 0; i < popup.getMenu().size(); i++)
                {
                    MenuItem items = popup.getMenu().getItem(i);


                        if (i == 1 && publicProfile_response.getFollowing() == true)
                        {
                            popup.getMenu().getItem(1).setTitle("Unfollow");

                        } else if (i == 1)
                        {
                            popup.getMenu().getItem(1).setTitle("Follow");
                        }

                        if (i == 2 && publicProfile_response.getBlock() == true)
                        {
                            popup.getMenu().getItem(2).setTitle("Unblock");
                        } else if (i == 2)
                        {
                            popup.getMenu().getItem(2).setTitle("Block");
                        }

                        if (i == 3 && publicProfile_response.getConnected() == true)
                        {
                            popup.getMenu().getItem(3).setTitle("Remove Connection");
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
                        if(CommonFunctions.chkStatus(context))
                        {
                            if(publicProfile_response.getBlock() == false)
                            {
                                if (item.getTitle().toString().equalsIgnoreCase("Share Profile")) {

                                } else if (item.getTitle().toString().equalsIgnoreCase("Follow")) {
                                    CommonFunctions.sDialog(context, "Following...");
                                    req_follow_request_API(context, publicProfile_response.getId(), position, "Public_Profile");

                                } else if (item.getTitle().toString().contains("Unfollow")) {
                                    CommonFunctions.sDialog(context, "UnFollowing...");
                                    req_Unfollow_request_API(context, publicProfile_response.getId(), position, "Public_Profile");

                                } else if(item.getTitle().toString().equalsIgnoreCase("Remove Connection"))
                                {
                                    req_connection_remove_API(context, publicProfile_response.getId(), position, "Public_Profile");
                                }
                                else if (item.getTitle().toString().equalsIgnoreCase("Block"))
                                {
                                    BlockUser_Request blockUser_request = new BlockUser_Request();
                                    blockUser_request.setClient_id(Constcore.client_Id);
                                    blockUser_request.setClient_secret(Constcore.client_Secret);
                                    blockUser_request.setBlocked_user_id(publicProfile_response.getId());
                                    blockUser_request.setReason("");
                                    req_block_user_API(context, blockUser_request, position,"Public_Profile");
                                }
                                else  if (item.getTitle().toString().equalsIgnoreCase("Unblock"))
                                {
                                    UnBlockUser_Request unBlockUser_request = new UnBlockUser_Request();
                                    unBlockUser_request.setClient_id(Constcore.client_Id);
                                    unBlockUser_request.setClient_secret(Constcore.client_Secret);
                                    unBlockUser_request.setUser_id(publicProfile_response.getId());
                                    req_unblock_user_API(context,unBlockUser_request, position,"Public_Profile");
                                }
                            }
                            else
                            {
                                if (item.getTitle().toString().equalsIgnoreCase("Block"))
                                {
                                    BlockUser_Request blockUser_request = new BlockUser_Request();
                                    blockUser_request.setClient_id(Constcore.client_Id);
                                    blockUser_request.setClient_secret(Constcore.client_Secret);
                                    blockUser_request.setBlocked_user_id(publicProfile_response.getId());
                                    blockUser_request.setReason("");
                                    req_block_user_API(context, blockUser_request, position,"Public_Profile");
                                }
                                else  if (item.getTitle().toString().equalsIgnoreCase("Unblock"))
                                {
                                    UnBlockUser_Request unBlockUser_request = new UnBlockUser_Request();
                                    unBlockUser_request.setClient_id(Constcore.client_Id);
                                    unBlockUser_request.setClient_secret(Constcore.client_Secret);
                                    unBlockUser_request.setUser_id(publicProfile_response.getId());
                                    req_unblock_user_API(context,unBlockUser_request, position,"Public_Profile");
                                }
                                else
                                {
                                    CommonFunctions.displayToast(context,"You have "+publicProfile_response.getBlock_message());
                                }
                            }

                        }
                        else
                        {
                            CommonFunctions.displayToast(context,context.getResources().getString(R.string.network_connection));
                        }
                        return true;
                    }
                });
            }
        });
    }

    private void getDataInList()
    {
        publicProfileVideosAdapter =  new PublicProfileVideosAdapter(context, public_profile_video_list);
        mRecyclerView.setAdapter(publicProfileVideosAdapter);
    }

    // API Call for Public profile
    public void req_publicprofile_API(final Context cont, int user_id)
    {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String bearertoken = sharedPreferences.getString(m_config.login_access_token,"");

        String url = m_config.web_api_link + "/profile-id/"+user_id;
        Log.d("url", url.toString());
        // prepare the Request
        JsonObjectRequest profilerequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        setPublicProfileData(response);
                        CommonFunctions.hDialog();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        CommonFunctions.hDialog();
                        if(error.networkResponse != null)
                        {
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

                            CommonFunctions.displayToast(context,context.getResources().getString(R.string.PublicProfile_failed));
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
                Log.e("accestoken"," "+bearertoken);
                return params;
            }
        };

        // add it to the RequestQueue
        int socketTimeout = 10000;//60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        profilerequest.setRetryPolicy(policy);


        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(profilerequest, "public_profile_api");



    }

    //update text on "Connect" and "Follow" button (People you may know)
    public void upd_btn_in_pub_prof_after_conn_follow_unfollow(int position, String condition)
    {
        if(condition.equals("connect"))
        {
            btn_connect.setText("Request Sent");

        }
//        else if(condition.equals("follow"))
//        {
//            //btn_follow.setText(context.getResources().getString(R.string.Following));
////            int incr_foll_count = Integer.parseInt(txt_following.getText().toString()) + 1;
////            txt_following.setText(String.valueOf(incr_foll_count));//update following count in public profile page
//
//        }
//        else if(condition.equals("unfollow"))
//        {
//            //btn_follow.setText(context.getResources().getString(R.string.Follow));
////            int decr_foll_count = Integer.parseInt(txt_following.getText().toString()) - 1;
////            txt_following.setText(String.valueOf(decr_foll_count));//update following count in public profile page
//
//        }

        if(from_Activity.equals("My_Suggestions"))
        {
            //upd_peopleyou_row_in_sugg_page_frm_publicprofile(user_id,"connect");
            upd_row_in_sugg_page(position, condition);
        }


    }

    //update invitation row on "Accept" and "Ignore" button (Invitations or MySuggesstions_Invitations)
    public static void upd_btn_on_pub_prof_after_inv_acc_rej(int position, int user_id, String condition)
    {
        if(condition.equals("connect"))
        {
            btn_connect.setText("Connected");
            btn_follow.setVisibility(View.GONE);
            publicProfile_response.setConnected(true);
            int incr_conn_count = Integer.parseInt(txt_connections.getText().toString()) + 1;
            txt_connections.setText(String.valueOf(incr_conn_count));//update connection count in public profile page when invitation is accepted
            int incr_foll_count = Integer.parseInt(txt_following.getText().toString()) + 1;
            txt_following.setText(String.valueOf(incr_foll_count));//update following count in public profile page
        }
        else if(condition.equals("remove"))
        {
            public_profile_activity.finish();
        }


        if(from_Activity.equals("My_Suggestions_Invitations"))
        {
            upd_inv_row_in_sugg_page_frm_invitation(user_id, condition);
        }
        else if(from_Activity.equals("Invitations"))
        {
            update_row_on_invitation_page(position, user_id, condition);
        }
    }

    public void update_menu_title(int position, String condition, String msg_block)
    {
        Log.e("condition"," "+condition+" "+popup.getMenu().getItem(1).getTitle());
        MenuItem item = null;
        if(condition.equals("follow"))
        {
            publicProfile_response.setFollowing(true);
            if(from_Activity.equals("My_Suggestions") || from_Activity.equals("My_Connections")) {
                btn_follow.setText(context.getResources().getString(R.string.Following));
            }
            int incr_foll_count = Integer.parseInt(txt_following.getText().toString()) + 1;
            txt_following.setText(String.valueOf(incr_foll_count));//update following count in public profile page

        }
        else if(condition.equals("unfollow"))
        {
            publicProfile_response.setFollowing(false);
            if(from_Activity.equals("My_Suggestions") || from_Activity.equals("My_Connections"))
            {
                btn_follow.setText(context.getResources().getString(R.string.Follow));
            }

            int decr_foll_count = Integer.parseInt(txt_following.getText().toString()) - 1;
            txt_following.setText(String.valueOf(decr_foll_count));//update following count in public profile page

        }
        else if(condition.equals("block"))
        {
            flag_blockunblk_clicked = true;
            publicProfile_response.setBlock(true);
            publicProfile_response.setBlock_message(msg_block);
            txt_block_text.setText(msg_block);
            txt_block_text.setVisibility(View.VISIBLE);
            llbtnconnectfollow.setVisibility(View.GONE);

            if(publicProfile_response.getConnected())
            {
                int decr_conn_count = Integer.parseInt(txt_connections.getText().toString()) - 1;
                txt_connections.setText(String.valueOf(decr_conn_count));//update connection count in public profile page when invitation is accepted
                int decr_foll_count = Integer.parseInt(txt_following.getText().toString()) - 1;
                txt_following.setText(String.valueOf(decr_foll_count));//update following count in public profile page

            }

        }
        else if(condition.equals("unblock"))
        {
            flag_blockunblk_clicked = true;
            publicProfile_response.setBlock(false);

            if(from_Activity.equals("My_Suggestions_Invitations") || from_Activity.equals("Invitations") || from_Activity.equals("My_Connections"))
            {
                txt_block_text.setText(msg_block);
                txt_block_text.setVisibility(View.VISIBLE);
                llbtnconnectfollow.setVisibility(View.GONE);
            }
            else if(from_Activity.equals("My_Suggestions"))
            {
                txt_block_text.setVisibility(View.GONE);
                llbtnconnectfollow.setVisibility(View.VISIBLE);
            }


        }
        else if(condition.equals("remove_connection"))
        {
            publicProfile_response.setConnected(false);
            int decr_conn_count = Integer.parseInt(txt_connections.getText().toString()) - 1;
            txt_connections.setText(String.valueOf(decr_conn_count));//update connection count in public profile page when invitation is accepted
            public_profile_activity.finish();
        }

        if(from_Activity.equals("My_Suggestions") && (condition.equals("follow") || condition.equals("unfollow")))
        {
            upd_row_in_sugg_page(position, condition);
        }

        else if(from_Activity.equals("My_Connections") && (condition.equals("remove_connection")))
        {
            upd_conn_row_in_Conn_page(position, condition);

        }

    }
    @Override
    public void onBackPressed()
    {
        // Delete row if user is blocked/unblocked
        if(publicProfile_response != null && publicProfile_response.getBlock() == true && flag_blockunblk_clicked == true)
        {
            if(from_Activity.equals("My_Suggestions_Invitations"))
            {
                upd_inv_row_in_sugg_page_frm_invitation(user_id, "block");
            }
            else if(from_Activity.equals("Invitations"))
            {
                update_row_on_invitation_page(position, user_id, "block");
            }
            else if(from_Activity.equals("My_Suggestions"))
            {
                upd_row_in_sugg_page(position, "block");
            }
            else if(from_Activity.equals("My_Connections"))
            {
                upd_conn_row_in_Conn_page(position, "block");
            }


        }
        else if(publicProfile_response != null && publicProfile_response.getBlock() == false && flag_blockunblk_clicked == true)
        {
            if(from_Activity.equals("My_Suggestions_Invitations"))
            {
                upd_inv_row_in_sugg_page_frm_invitation(user_id, "unblock");
            }
            else if(from_Activity.equals("Invitations"))
            {
                update_row_on_invitation_page(position, user_id, "unblock");
            }
            else if(from_Activity.equals("My_Connections"))
            {
                upd_conn_row_in_Conn_page(position, "unblock");
            }
        }

        finish();
    }

}

