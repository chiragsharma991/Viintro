package com.viintro.Viintro.MyProfile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
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

import com.bumptech.glide.Glide;
import com.viintro.Viintro.Model.Profile_Views;
import com.viintro.Viintro.Model.WhoViewedProfile_Response;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Search_Post.LazyLoadingHolder;
import com.viintro.Viintro.VideoRecord.VideoRecordingActivity;
import com.viintro.Viintro.listener.OnLoadMoreListener;

import java.util.ArrayList;

import static com.viintro.Viintro.MyProfile.WhoViewedProfileActivity.arr_count_whoviewedprofile;
import static com.viintro.Viintro.Webservices.FollowAPI.req_follow_request_API;
import static com.viintro.Viintro.Webservices.UnfollowAPI.req_Unfollow_request_API;

/**
 * Created by hasai on 22/03/17.
 */
public class WhoViewedProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<Profile_Views> arr_whoviewedprofile;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    RecyclerView recyclerView_whoviewedprofile;


    public WhoViewedProfileAdapter(Context context, ArrayList<Profile_Views> arr_whoviewedprofile, RecyclerView recyclerView_whoviewedprofile) {
        this.context = context;
        this.arr_whoviewedprofile = arr_whoviewedprofile;
        this.recyclerView_whoviewedprofile = recyclerView_whoviewedprofile;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView_whoviewedprofile.getLayoutManager();
        recyclerView_whoviewedprofile.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

//                Log.e("isLoading"," "+isLoading);
//                Log.e("totalItemCount"," "+totalItemCount);
//                Log.e("lastVisibleItem"," "+(lastVisibleItem));

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && arr_count_whoviewedprofile == 20)
                {
                    Log.e("in condition check","");
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }


    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public int getItemCount() {
        return arr_whoviewedprofile.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                View v0 = inflater.inflate(R.layout.adapter_who_viewed_your_profile, parent, false);
                viewHolder = new WhoViewedHolder(v0, context);
                break;
            case 1:
                View v1 = inflater.inflate(R.layout.layout_loading_item, parent, false);
                viewHolder = new LazyLoadingHolder(v1);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        switch (holder.getItemViewType()) {
            case 0:
                WhoViewedHolder whoviewedholder = (WhoViewedHolder) holder;
                configurewhoviewedholder(whoviewedholder, position);
                break;
            case 1:
                LazyLoadingHolder lazyloadingholder = (LazyLoadingHolder) holder;
                configureLazyLoading(lazyloadingholder, position);
                break;
        }


    }

    private void configurewhoviewedholder(WhoViewedHolder holder, final int position) {
        WhoViewedHolder whoviewedholder = (WhoViewedHolder) holder;
        final Profile_Views response = arr_whoviewedprofile.get(position);
        if (response.getDisplay_pic() != null)
        {
            Glide.with(context).load(Uri.parse(response.getDisplay_pic())).centerCrop().into(whoviewedholder.img_whoviewed);
        }
        else
        {
            Glide.with(context).load(R.drawable.ic_launcher).centerCrop().into(whoviewedholder.img_whoviewed);
        }
        whoviewedholder.txt_whoviwed_name.setText(response.getFullname());
        if(response.getUser_type().equals("student"))
        {
            whoviewedholder.txt_designation.setText(response.getCourse()+", "+response.getUniversity());
        }
        else
        {
            whoviewedholder.txt_designation.setText(response.getDesignation()+"@ "+response.getCompany());
        }

        if(response.getConnected() == true)
        {
            whoviewedholder.btn_Connect_Whoviewed.setText("Connected");
        }

        if(response.getConnected() == false && response.getConnection_requested() == true)
        {
            whoviewedholder.btn_Connect_Whoviewed.setText("Request Sent");
        }
        if(response.getConnected() == false && response.getConnection_requested() == false)
        {
            whoviewedholder.btn_Connect_Whoviewed.setText("Connect");
        }
        if(response.getFollowing() == true)
        {
            whoviewedholder.btn_Follow_Whoviewed.setText(context.getResources().getString(R.string.Following));

        }
        if(response.getFollowing() == false)
        {
            whoviewedholder.btn_Follow_Whoviewed.setText(context.getResources().getString(R.string.Follow));

        }

//        whoviewedholder.btn_More.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e("pos ", " " + position);
//
//                PopupMenu popup = new PopupMenu(context, view);
//                popup.getMenuInflater().inflate(R.menu.popup_share_profile, popup.getMenu());
//                for (int i = 0; i < popup.getMenu().size(); i++) {
//                    MenuItem items = popup.getMenu().getItem(i);
//                    SpannableString spanstring = new SpannableString(items.getTitle().toString());
//                    spanstring.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, spanstring.length(), 0); //fix the color to white
//                    items.setTitle(spanstring);
//                }
//                popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
//                    @Override
//                    public void onDismiss(PopupMenu menu) {
//
//                    }
//                });
//
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    public boolean onMenuItemClick(MenuItem item) {
//
//                        if (item.getTitle().toString().equals("Share this profile")) {
//
//                        }
//
//                        return true;
//                    }
//                });
//
//
//                popup.show();
//
//            }
//        });


        whoviewedholder.btn_Connect_Whoviewed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn_Connect = (Button) v;
                if(btn_Connect.getText().equals("Connect"))
                {
                    if(CommonFunctions.chkStatus(context))
                    {
                        if(CommonFunctions.checkCameraHardware(context))
                        {
                            Intent int_video_record = new Intent(context, VideoRecordingActivity.class);
                            int_video_record.putExtra("from", "connect");
                            int_video_record.putExtra("from_Activity", "WhoViewed");
                            int_video_record.putExtra("user_id", response.getId());
                            int_video_record.putExtra("position", position);
                            ((Activity)context).startActivity(int_video_record);
                        }
                    }
                    else
                    {
                        CommonFunctions.displayToast(context,context.getResources().getString(R.string.network_connection));
                    }
                }

            }
        });

        whoviewedholder.btn_Follow_Whoviewed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn_Follow = (Button) v;
                if(btn_Follow.getText().equals(context.getResources().getString(R.string.Follow)))
                {
                    if(CommonFunctions.chkStatus(context))
                    {
                        CommonFunctions.sDialog(context, "Following...");
                        req_follow_request_API(context, response.getId(), position, "WhoViewed");
                    }
                    else
                    {
                        CommonFunctions.displayToast(context,context.getResources().getString(R.string.network_connection));
                    }
                }
                else if(btn_Follow.getText().equals(context.getResources().getString(R.string.Following)))
                {
                    if(CommonFunctions.chkStatus(context))
                    {
                        CommonFunctions.sDialog(context, "Unfollowing...");
                        req_Unfollow_request_API(context, response.getId(), position,"WhoViewed");
                    }
                    else
                    {
                        CommonFunctions.displayToast(context,context.getResources().getString(R.string.network_connection));
                    }
                }
            }
        });

    }

    private void configureLazyLoading(LazyLoadingHolder lazyloadingholder, int position)
    {
        lazyloadingholder.progressbar.setIndeterminate(true);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setLoaded() {
        isLoading = false;
    }

    class WhoViewedHolder extends RecyclerView.ViewHolder {

        ImageView img_whoviewed;
        TextView txt_whoviwed_name, txt_designation;
        Button btn_Connect_Whoviewed,  btn_Follow_Whoviewed, btn_More;

        public WhoViewedHolder(View v, Context context) {
            super(v);
            img_whoviewed = (ImageView) v.findViewById(R.id.img_whoviewed);
            txt_whoviwed_name = (TextView) v.findViewById(R.id.txt_whoviwed_name);
            txt_designation = (TextView) v.findViewById(R.id.txt_designation);
            btn_Connect_Whoviewed = (Button) v.findViewById(R.id.btn_Connect_Whoviewed);
            btn_Follow_Whoviewed = (Button) v.findViewById(R.id.btn_Follow_Whoviewed);
            btn_More = (Button) v.findViewById(R.id.btn_More);
        }


    }


}




