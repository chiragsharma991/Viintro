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
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.viintro.Viintro.Model.Following;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Search_Post.LazyLoadingHolder;
import com.viintro.Viintro.VideoRecord.VideoRecordingActivity;
import com.viintro.Viintro.Webservices.FollowAPI;
import com.viintro.Viintro.listener.OnLoadMoreListener;

import java.util.ArrayList;

import static com.viintro.Viintro.MyProfile.MyFollowingsActivity.arr_count_myfollowings;
import static com.viintro.Viintro.Webservices.FollowAPI.req_follow_request_API;
import static com.viintro.Viintro.Webservices.UnfollowAPI.req_Unfollow_request_API;

/**
 * Created by rkanawade on 23/03/17.
 */

public class MyFollowingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context context;
    ArrayList<Following> arr_followings;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    RecyclerView recycler_view_followings;
    private TextView txt_no_followings;




    public MyFollowingsAdapter(Context context, final ArrayList<Following> arr_followings, RecyclerView recycler_view_followings, TextView txt_no_followings) {
        this.context = context;
        this.arr_followings = arr_followings;
        this.recycler_view_followings = recycler_view_followings;
        this.txt_no_followings = txt_no_followings;


        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recycler_view_followings.getLayoutManager();
        recycler_view_followings.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

//                Log.e("isLoading"," "+isLoading);
//                Log.e("totalItemCount"," "+totalItemCount);
//                Log.e("lastVisibleItem"," "+(lastVisibleItem));

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && arr_count_myfollowings == 20) {
                    Log.e("in condition check","");
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public MyFollowingsAdapter() {

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
        return arr_followings.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                View v0 = inflater.inflate(R.layout.adapter_my_followings, parent, false);
                viewHolder = new FollowingsHolder(v0, context);
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
                FollowingsHolder followingsHolder = (FollowingsHolder) holder;
                configurefollowingsHolder(followingsHolder, position);
                break;
            case 1:
                LazyLoadingHolder lazyloadingholder = (LazyLoadingHolder) holder;
                configureLazyLoading(lazyloadingholder, position);
                break;
        }

    }

    private void configurefollowingsHolder(FollowingsHolder holder, final int position)
    {
        final FollowingsHolder followingsHolder = (FollowingsHolder) holder;
        final Following response = arr_followings.get(position);

        if (response.getDisplay_pic() != null)
        {
            Glide.with(context).load(Uri.parse(response.getDisplay_pic())).centerCrop().into(followingsHolder.img_followings);
        }
        else
        {
            Glide.with(context).load(R.drawable.ic_launcher).centerCrop().into(followingsHolder.img_followings);
        }
        if(response.getFullname() != null){

            followingsHolder.txt_followings_name.setText(response.getFullname());
        }
        if(response.getUser_type().equals("student"))
        {
            followingsHolder.txt_designation_followings.setText(response.getCourse()+", "+response.getUniversity());
        }
        else
        {
            followingsHolder.txt_designation_followings.setText(response.getDesignation()+"@ "+response.getCompany());
        }

        if(response.getConnected() == true)
        {
            followingsHolder.btn_Connect_followings.setText("Connected");
        }
        if(response.getConnected() == false && response.getConnection_requested() == true)
        {
            followingsHolder.btn_Connect_followings.setText("Request Sent");
        }
        if(response.getConnected() == false && response.getConnection_requested() == false)
        {
            followingsHolder.btn_Connect_followings.setText("Connect");
        }
        if(response.getFollowing() == true)
        {
            followingsHolder.btn_Follow_followings.setText(context.getResources().getString(R.string.Following));
        }
        if(response.getFollowing() == false)
        {
            followingsHolder.btn_Follow_followings.setText(context.getResources().getString(R.string.Follow));
        }

//        followingsHolder.btn_More.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e("pos ", " " + position);
//
//                PopupMenu popup = new PopupMenu(context, view);
//                popup.getMenuInflater().inflate(R.menu.popup_unfollow, popup.getMenu());
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
//                        if (item.getTitle().toString().equalsIgnoreCase("Unfollow")) {
//                            if (CommonFunctions.chkStatus(context)) {
//                                CommonFunctions.sDialog(context, "Unfollowing...");
//                                req_Unfollow_request_API(context, response.getId(), position, "MyFollowings");
//
//
//                            } else {
//                                CommonFunctions.displayToast(context, context.getResources().getString(R.string.network_connection));
//                            }
//                        }
//                        return true;
//                    }
//                });
//
//
//                popup.show();
//
//            }
//        });

        followingsHolder.btn_Connect_followings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn_Connect = (Button) v;
                if(btn_Connect.getText().equals("Connect"))
                {
                    if(CommonFunctions.chkStatus(context))
                    {
                        if (CommonFunctions.checkCameraHardware(context))
                        {
                            Intent int_video_record = new Intent(context, VideoRecordingActivity.class);
                            int_video_record.putExtra("from", "connect");
                            int_video_record.putExtra("user_id", response.getId());
                            int_video_record.putExtra("position", position);
                            int_video_record.putExtra("from_Activity", "MyFollowings");
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

        followingsHolder.btn_Follow_followings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn_follow = (Button) v;
                if(btn_follow.getText().equals(context.getResources().getString(R.string.Follow))){
                    if(CommonFunctions.chkStatus(context))
                    {
                        CommonFunctions.sDialog(context, "Following...");
                        req_follow_request_API(context, response.getId(), position, "MyFollowings");

                    }
                    else
                    {
                        CommonFunctions.displayToast(context,context.getResources().getString(R.string.network_connection));
                    }
                }else if(btn_follow.getText().equals(context.getResources().getString(R.string.Following)))
                {
                    if(CommonFunctions.chkStatus(context))
                    {
                        CommonFunctions.sDialog(context, "Unfollowing...");
                        req_Unfollow_request_API(context, response.getId(), position,"MyFollowings");
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
    public int getItemViewType(int position)
    {
        if(arr_followings.get(position) == null)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    public void setLoaded() {
        isLoading = false;
    }



}
