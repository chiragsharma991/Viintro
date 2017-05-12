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
import com.viintro.Viintro.Model.Followers;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Search_Post.LazyLoadingHolder;
import com.viintro.Viintro.Search_Post.PostAdapter;
import com.viintro.Viintro.VideoRecord.VideoRecordingActivity;
import com.viintro.Viintro.listener.OnLoadMoreListener;

import java.util.ArrayList;

import static com.viintro.Viintro.MyProfile.MyFollowersActivity.arr_count_myfollowers;
import static com.viintro.Viintro.Webservices.FollowAPI.req_follow_request_API;
import static com.viintro.Viintro.Webservices.UnfollowAPI.req_Unfollow_request_API;

/**
 * Created by rkanawade on 23/03/17.
 */

public class MyFollowersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    ArrayList<Followers> arr_followers;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    RecyclerView recycler_view_followers;
    private TextView txt_no_followers;



    public MyFollowersAdapter(Context context, ArrayList<Followers> arr_followers, RecyclerView recycler_view_followers, TextView txt_no_followers) {
        this.context = context;
        this.arr_followers = arr_followers;
        this.recycler_view_followers = recycler_view_followers;
        this.txt_no_followers = txt_no_followers;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recycler_view_followers.getLayoutManager();
        recycler_view_followers.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

//                Log.e("isLoading"," "+isLoading);
//                Log.e("totalItemCount"," "+totalItemCount);
//                Log.e("lastVisibleItem"," "+(lastVisibleItem));

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && arr_count_myfollowers == 20) {
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
        return arr_followers.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                View v0 = inflater.inflate(R.layout.adapter_my_followers, parent, false);
                viewHolder = new FollowersHolder(v0, context);
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
                FollowersHolder followersHolder = (FollowersHolder) holder;
                configurefollowersHolder(followersHolder, position);
                break;
            case 1:
                LazyLoadingHolder lazyloadingholder = (LazyLoadingHolder) holder;
                configureLazyLoading(lazyloadingholder, position);
                break;
        }

    }

    private void configurefollowersHolder(FollowersHolder holder, final int position)
    {
        final FollowersHolder followersHolder = (FollowersHolder) holder;
        final Followers response = arr_followers.get(position);
        if (response.getDisplay_pic() != null)
        {
            Glide.with(context).load(Uri.parse(response.getDisplay_pic())).centerCrop().into(followersHolder.img_followers);
        }
        else
        {
            Glide.with(context).load(R.drawable.ic_launcher).centerCrop().into(followersHolder.img_followers);
        }
        followersHolder.txt_followers_name.setText(response.getFullname());
        if(response.getUser_type().equals("student"))
        {
            followersHolder.txt_designation.setText(response.getCourse()+", "+response.getUniversity());
        }
        else
        {
            followersHolder.txt_designation.setText(response.getDesignation()+"@ "+response.getCompany());
        }

        if(response.getConnected() == true)
        {
            followersHolder.btn_Connect_followers.setText("Connected");
        }
        if(response.getConnected() == false && response.getConnection_requested() == true)
        {
            followersHolder.btn_Connect_followers.setText("Request Sent");
        }
        if(response.getConnected() == false && response.getConnection_requested() == false)
        {
            followersHolder.btn_Connect_followers.setText("Connect");
        }
        if(response.getFollowing() == true)
        {
            followersHolder.btn_Follow_followers.setText(context.getResources().getString(R.string.Following));
            //followersHolder.btn_More.setVisibility(View.VISIBLE);
        }
        if(response.getFollowing() == false)
        {
            followersHolder.btn_Follow_followers.setText(context.getResources().getString(R.string.Follow));
            //followersHolder.btn_More.setVisibility(View.INVISIBLE);
        }


//        followersHolder.btn_More.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e("pos ", " " + position);
//
//                PopupMenu popup = new PopupMenu(context, view);
//                popup.getMenuInflater().inflate(R.menu.popup_unfollow, popup.getMenu());
//                for (int i = 0; i < popup.getMenu().size(); i++)
//                {
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
//                        if (item.getTitle().toString().equalsIgnoreCase("Unfollow"))
//                        {
//                            if(CommonFunctions.chkStatus(context))
//                            {
//                                CommonFunctions.sDialog(context, "Unfollowing...");
//                                req_Unfollow_request_API(context, response.getId(), position,"MyFollowers");
//
//                            }
//                            else
//                            {
//                                CommonFunctions.displayToast(context,context.getResources().getString(R.string.network_connection));
//                            }
//
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

        followersHolder.btn_Connect_followers.setOnClickListener(new View.OnClickListener() {
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
                            int_video_record.putExtra("user_id", response.getId());
                            int_video_record.putExtra("position", position);
                            int_video_record.putExtra("from_Activity", "MyFollowers");
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

        followersHolder.btn_Follow_followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn_Follow = (Button) v;
                if(btn_Follow.getText().equals(context.getResources().getString(R.string.Follow)))
                {
                    if(CommonFunctions.chkStatus(context))
                    {
                        CommonFunctions.sDialog(context, "Following...");
                        req_follow_request_API(context, response.getId(), position, "MyFollowers");
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
                        req_Unfollow_request_API(context, response.getId(), position,"MyFollowers");
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
        if(arr_followers.get(position) == null)
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
