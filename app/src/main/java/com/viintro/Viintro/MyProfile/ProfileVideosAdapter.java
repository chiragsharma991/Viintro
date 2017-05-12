package com.viintro.Viintro.MyProfile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.viintro.Viintro.Landing.SampleVideoFullScreen;
import com.viintro.Viintro.Model.Videos;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.VideoRecord.VideoRecordingActivity;
import com.viintro.Viintro.ViintroApplication;
import com.viintro.Viintro.Webservices.AccessTokenAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.viintro.Viintro.Constants.Configuration_Parameter.intro_video_myprofile;


/**
 * Created by rkanawade on 22/02/17.
 */

public class ProfileVideosAdapter extends RecyclerView.Adapter<ProfileVideosAdapter.ViewHolder> {

    Context context;
    ArrayList<Videos> profile_video_list;
    ImageView img_intro_video_thumbnail;
    Button btn_upload_videos;

    public ProfileVideosAdapter(Context context, ArrayList<Videos> profile_video_list, ImageView img_intro_video_thumbnail, Button btn_upload_videos) {
        super();
        this.context = context;
        this.profile_video_list = profile_video_list;
        this.img_intro_video_thumbnail = img_intro_video_thumbnail;
        this.btn_upload_videos = btn_upload_videos;


    }
    @Override
    public ProfileVideosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_profile_videos_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Videos profile_videos = null;
        String no = String.valueOf(position + 1);
        try
        {
            profile_videos = profile_video_list.get(position);
            holder.img_video_number.setText(no);
            Glide.with(context).
                    load(Uri.parse(profile_videos.getThumbnail())).
                    into(holder.image_profile);
            holder.text_video_views.setText(String.valueOf(profile_videos.getView_count())+" views");
            holder.img_more.setVisibility(View.VISIBLE);


        }
        catch(Exception e)
        {

            holder.img_video_number.setText(no);
            Glide.with(context).
                    load(R.color.grey).
                    into(holder.image_profile);
            holder.img_more.setVisibility(View.INVISIBLE);

        }

        final Videos finalProfile_videos = profile_videos;
        holder.imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(finalProfile_videos != null)
                {
                    Intent int_sample_videos = new Intent(context, SampleVideoFullScreen.class);
                    int_sample_videos.putExtra("Current url", finalProfile_videos.getVideo_mp4());
                    int_sample_videos.putExtra("public_id", finalProfile_videos.getPublic_id());
                    context.startActivity(int_sample_videos);
                }
                else
                {
                    CommonFunctions.displayToast(context,"There is no video to play");

                }

            }
        });


        holder.img_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("position", " " + position);

                if (profile_video_list.size() == 0) {
                    CommonFunctions.displayToast(context, "You cannot delete this video");
                } else
                {


                    PopupMenu popup = new PopupMenu(context, view);
                    popup.getMenuInflater().inflate(R.menu.popup_profile, popup.getMenu());
                    for (int i = 0; i < popup.getMenu().size(); i++) {
                        MenuItem items = popup.getMenu().getItem(i);
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

                            if(finalProfile_videos != null)
                            {

                                if (item.getTitle().toString().equals("Set as Intro Video")) {
                                    CommonFunctions.sDialog(context, "Loading...");
                                    setprofile_as_intro_video(context, profile_video_list.get(position).getPublic_id(), position);

                                } else if (item.getTitle().toString().equals("Delete")) {
                                    //delete profile video
                                    CommonFunctions.sDialog(context, "Deleting...");
                                    delete_profile_video(context, profile_video_list.get(position).getPublic_id(), position);
                                }
                            }else
                            {
                                CommonFunctions.displayToast(context,"There is no video to play");
                            }

                            return true;
                        }
                    });


                    popup.show();




                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return 3;//profile_video_list.size();
    }

//    public Videos getItem(int position) {
//        return (Videos) profile_video_list.get(position);
//    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {

        public ImageView img_more,imgPlay,image_profile;
        public TextView img_video_number, text_video_views;
        public Button btn_addVideo;

        public ViewHolder(View itemView) {
            super(itemView);
            img_more = (ImageView) itemView.findViewById(R.id.img_more);
            imgPlay = (ImageView) itemView.findViewById(R.id.imgPlay);
            image_profile = (ImageView) itemView.findViewById(R.id.image_profile);
          //  img_video_number = (TextView) itemView.findViewById(R.id.img_video_number);
          //  text_video_views = (TextView) itemView.findViewById(R.id.text_video_views);

        }

    }

    // API call to set profile video as intro video
    public void setprofile_as_intro_video(final Context context, String public_id, final int position)
    {

        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);


        final Gson gson = new Gson();
        String url = m_config.web_api_link+ "/profile/mark-as-intro-video";

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("client_id", Constcore.client_Id);
            obj.put("client_secret", Constcore.client_Secret);
            obj.put("public_id", public_id);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


        // prepare the Request
        JsonObjectRequest profilevideorequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());

                        CommonFunctions.hDialog();
                        try {
                            CommonFunctions.displayToast(ProfileVideosAdapter.this.context,response.getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Videos temp_videos = profile_video_list.get(position);
                        profile_video_list.set(position, intro_video_myprofile);
                        intro_video_myprofile = temp_videos;
                        Glide.with(context)
                                .load(Uri.parse(intro_video_myprofile.getThumbnail()))
                                .into(img_intro_video_thumbnail);
                        notifyDataSetChanged();

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
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
                                            CommonFunctions.displayToast(ProfileVideosAdapter.this.context, network_obj.getString("message"));
                                        }

                                        ((Activity) ProfileVideosAdapter.this.context).finish();
                                    } catch (JSONException e) {

                                    }

                                }
                            }

                        } else
                        {



                            CommonFunctions.displayToast(ProfileVideosAdapter.this.context, ProfileVideosAdapter.this.context.getResources().getString(R.string.Process_failed));
                            ((Activity) ProfileVideosAdapter.this.context).finish();
                        }


                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("version","1");
                params.put("devicetype","android");
                params.put("timezone",CommonFunctions.getTimeZoneIST());
                params.put("Authorization","Bearer "+sharedPreferences.getString(m_config.login_access_token,""));
                params.put("timesstamp",CommonFunctions.getTimeStamp());
                params.put("os_version",m_config.os_version);
                return params;
            }
        };

        // add it to the RequestQueue
        int socketTimeout = 10000;//60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        profilevideorequest.setRetryPolicy(policy);

        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(profilevideorequest, "profile_as_intro_video_api");
    }


    // API call to delete profile video
    public void delete_profile_video(final Context context, String public_id, final int position)
    {

        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String url = m_config.web_api_link+ "/profile-video/delete";

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("client_id", Constcore.client_Id);
            obj.put("client_secret", Constcore.client_Secret);
            obj.put("public_id", public_id);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


        // prepare the Request
        JsonObjectRequest profilevideorequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());

                        CommonFunctions.hDialog();
                        try {
                            CommonFunctions.displayToast(ProfileVideosAdapter.this.context,response.getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        profile_video_list.remove(position);
                        notifyDataSetChanged();

//                        if(profile_video_list.size() == 3)
//                        {
//                            btn_upload_videos.setEnabled(false);
//                        }
//                        else
//                        {
//                            btn_upload_videos.setEnabled(true);
//                        }



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
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
                                            CommonFunctions.displayToast(ProfileVideosAdapter.this.context, network_obj.getString("message"));
                                        }

                                    } catch (JSONException e) {

                                    }

                                }
                            }

                        } else
                        {
                            CommonFunctions.displayToast(ProfileVideosAdapter.this.context, ProfileVideosAdapter.this.context.getResources().getString(R.string.Process_failed));

                        }


                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("version","1");
                params.put("devicetype","android");
                params.put("timezone",CommonFunctions.getTimeZoneIST());
                params.put("Authorization","Bearer "+sharedPreferences.getString(m_config.login_access_token,""));
                params.put("timesstamp",CommonFunctions.getTimeStamp());
                params.put("os_version",m_config.os_version);
                return params;
            }
        };

        // add it to the RequestQueue
        int socketTimeout = 10000;//60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        profilevideorequest.setRetryPolicy(policy);

        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(profilevideorequest, "del_profile_video_api");
    }


}
