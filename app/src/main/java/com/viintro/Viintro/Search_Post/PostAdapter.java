package com.viintro.Viintro.Search_Post;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.server.response.FastJsonResponse;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.thefinestartist.ytpa.YouTubePlayerActivity;
import com.thefinestartist.ytpa.enums.Orientation;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.HomeFeed.PostDetailsActivity;
import com.viintro.Viintro.HomeFeed.PostDetailsActivity_PlayVideo;
import com.viintro.Viintro.Model.MySuggestions_Response;
import com.viintro.Viintro.Model.Owner;
import com.viintro.Viintro.Model.People;
import com.viintro.Viintro.Model.Post;
import com.viintro.Viintro.Model.PostReport_Request;
import com.viintro.Viintro.Model.User;
import com.viintro.Viintro.Model.Video;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.VideoRecord.AddLinkActivity;
import com.viintro.Viintro.VideoRecord.VideoRecordingActivity;
import com.viintro.Viintro.Webservices.FollowAPI;
import com.viintro.Viintro.Webservices.HidePostAPI;
import com.viintro.Viintro.listener.OnLoadMoreListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.viintro.Viintro.Constants.Constcore.REQUEST_LIKE;
import static com.viintro.Viintro.Reusables.CommonFunctions.makeTextViewResizable;
import static com.viintro.Viintro.Search_Post.Search_Post_Activity.arr_count_people;
import static com.viintro.Viintro.Search_Post.Search_Post_Activity.arr_count_post;
import static com.viintro.Viintro.Search_Post.Search_Post_Activity.listView_People;
import static com.viintro.Viintro.Search_Post.Search_Post_Activity.listView_Post;
import static com.viintro.Viintro.Webservices.CreatePostShareUrlAPI.req_postShareUrl_API;
import static com.viintro.Viintro.Webservices.PostReportAPI.req_post_report_API;
import static com.viintro.Viintro.Webservices.UnfollowAPI.req_Unfollow_request_API;

/**
 * Created by hasai on 02/03/17.
 */
public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    static ArrayList arr_post;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private int like_count, comment_count, share_count, view_count;


    public PostAdapter(Context context, ArrayList arr_post) {
        this.context = context;
        this.arr_post = arr_post;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) listView_Post.getLayoutManager();

        listView_Post.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

//                Log.e("isLoading"," "+isLoading);
//                Log.e("totalItemCount"," "+totalItemCount);
//                Log.e("lastVisibleItem"," "+(lastVisibleItem));
                Log.e("arr_count_post"," "+arr_count_post);

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && arr_count_post == 20) {
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
        return arr_post.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case 0:
                View v0 = inflater.inflate(R.layout.adapter_post, parent, false);
                viewHolder = new PostViewHolder(v0, context);
                break;
            case 1:
                View v1 = inflater.inflate(R.layout.layout_loading_item, parent, false);
                viewHolder = new LazyLoadingHolder(v1);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {

        switch (holder.getItemViewType()) {
            case 0:
                PostViewHolder postviewholder = (PostViewHolder) holder;
                configurepostviewholder(postviewholder, position);
                break;
            case 1:
                LazyLoadingHolder lazyloadingholder = (LazyLoadingHolder) holder;
                configureLazyLoading(lazyloadingholder, position);
                break;
        }


    }

    private void configureLazyLoading(LazyLoadingHolder lazyloadingholder, int position)
    {
        lazyloadingholder.progressbar.setIndeterminate(true);
    }

    private void configurepostviewholder(PostViewHolder holder, final int position)
    {
        PostViewHolder postviewholder = (PostViewHolder) holder;
        final Post post = (Post) arr_post.get(position);
        final Owner owner = post.getOwner();
        final Video video = post.getVideo();
        like_count = post.getLikes();
        comment_count = post.getComments();
        view_count = post.getView_count();

        Log.e("user "," "+owner.getDisplay_pic()+" "+postviewholder.img_avatar_post);
        if (owner.getDisplay_pic() != null)
        {
            Glide.with(context).load(Uri.parse(owner.getDisplay_pic())).centerCrop().into(postviewholder.img_avatar_post);
        }
        else
        {
            Glide.with(context).load(R.drawable.ic_launcher).centerCrop().into(postviewholder.img_avatar_post);
        }

        postviewholder.txt_fullname_post.setText(owner.getFullname());
        //postviewholder.txt_designation_post.setText(user.getFullname());

        if (video.getThumbnail() == null || video.getThumbnail().equals(""))
        {
            postviewholder.rel_img_thumbnail_post_video.setVisibility(View.GONE);
            Glide.with(context).load(R.drawable.ic_launcher).centerCrop().into(postviewholder.img_thumbnail_post_video);
        }
        else
        {
            postviewholder.rel_img_thumbnail_post_video.setVisibility(View.VISIBLE);
            Glide.with(context).load(video.getThumbnail()).into(postviewholder.img_thumbnail_post_video);
        }

        if(owner.getUser_type() != null)
        {
            if(owner.getUser_type().equals("student"))
            {
                postviewholder.txt_designation_post.setText(owner.getCourse()+", "+owner.getUniversity());
            }
            else
            {
                postviewholder.txt_designation_post.setText(owner.getDesignation()+"@ "+owner.getCompany());
            }
        }

        if(post.getDescription() != null || (!post.equals("")))
        {
            postviewholder.txt_description_post.setText(post.getDescription());
            makeTextViewResizable(postviewholder.txt_description_post, 2, "See More", true);//3

        }

        postviewholder.txt_no_of_likes.setText(String.valueOf(like_count) +" Likes");
        postviewholder.txt_no_of_comments.setText(String.valueOf(comment_count) +" Comments");
        postviewholder.txt_no_of_views.setText(String.valueOf(view_count) +" Views");


        postviewholder.img_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = post.getId();
                String description = "";
                if(post.getDescription() != null || (!post.getDescription().equals("")))
                {
                    description = post.getDescription();
                }

                int likes = post.getLikes();
                int comments = post.getComments();
                int view_count = post.getView_count();
                Boolean user_liked = post.getUser_liked();

                Gson gson = new Gson();
                String user_obj = gson.toJson(post.getOwner());
                String video_obj = gson.toJson(post.getVideo());

                if(video.getType().equals("system")) {

                    Intent int_post_video = new Intent(context, PostDetailsActivity_PlayVideo.class);
                    int_post_video.putExtra("position", position);
                    int_post_video.putExtra("from_Activity", "Post_Search");
                    int_post_video.putExtra("id", id);
                    int_post_video.putExtra("description", description);
                    int_post_video.putExtra("user", user_obj.toString());
                    int_post_video.putExtra("video", video_obj.toString());
                    int_post_video.putExtra("likes", likes);
                    int_post_video.putExtra("comments", comments);
                    int_post_video.putExtra("view_count", view_count);
                    int_post_video.putExtra("user_liked", user_liked);
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


        postviewholder.rel_mainview_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                String id = post.getId();
                String description = "";
                if(post.getDescription() != null || (!post.getDescription().equals("")))
                {
                    description = post.getDescription();
                }

                int likes = post.getLikes();
                int comments = post.getComments();
                int view_count = post.getView_count();
                Boolean user_liked = post.getUser_liked();

                Gson gson = new Gson();
                String user_obj = gson.toJson(post.getOwner());
                String video_obj = gson.toJson(post.getVideo());

                Intent int_post = new Intent(context, PostDetailsActivity.class);
                int_post.putExtra("position", position);
                int_post.putExtra("id",id);
                int_post.putExtra("from_Activity","Post_Search");
//                int_post.putExtra("description",description);
//                int_post.putExtra("user",user_obj.toString());
//                int_post.putExtra("video",video_obj.toString());
//                int_post.putExtra("likes",likes);
//                int_post.putExtra("comments",comments);
//                int_post.putExtra("view_count",view_count);
//                int_post.putExtra("user_liked",user_liked);

                context.startActivity(int_post);




            }
        });

        postviewholder.btn_More.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        popup.getMenu().getItem(i).setTitle("Unfollow "+owner.getFullname());
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
                            req_postShareUrl_API(context, post.getId());
                        }
                        else  if (item.getTitle().toString().equalsIgnoreCase("Hide this post"))
                        {
                            Log.e("Hide"," ");
                            HidePostAPI.req_post_hide_API(context, json_hide_post(post.getId()), position, "Post_Search");
                        }
                        else  if (item.getTitle().toString().contains("Unfollow"))
                        {
                            Log.e("Unfollow "," ");
                            if (CommonFunctions.chkStatus(context)) {
                                CommonFunctions.sDialog(context, "Unfollowing...");
                                req_Unfollow_request_API(context, post.getOwner().getId(), position, "Post_Search");

                            } else
                            {
                                CommonFunctions.displayToast(context, context.getResources().getString(R.string.network_connection));
                            }
                        }
                        else  if (item.getTitle().toString().equalsIgnoreCase("Report this post"))
                        {

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
                                    mBottomSheetDialog.dismiss();
                                    req_post_report_API(context, json_post_report(post.getId(), 1), position, "Post_Search");
                                }
                            });

                            txt_2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mBottomSheetDialog.dismiss();
                                    req_post_report_API(context, json_post_report(post.getId(), 2), position, "Post_Search");
                                }
                            });

                            txt_3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mBottomSheetDialog.dismiss();
                                    req_post_report_API(context, json_post_report(post.getId(), 3), position, "Post_Search");
                                }
                            });

                            txt_4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mBottomSheetDialog.dismiss();
                                    req_post_report_API(context, json_post_report(post.getId(), 4), position, "Post_Search");
                                }
                            });

                        }
                        return true;
                    }
                });
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if(arr_post.get(position) == null)
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

    private class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView img_avatar_post,img_thumbnail_post_video, img_play_pause;
        TextView txt_fullname_post, txt_designation_post, txt_post_time, txt_description_post, txt_no_of_likes, txt_no_of_comments, txt_no_of_views;
        Button btn_More;
        RelativeLayout rel_img_thumbnail_post_video, rel_mainview_post;


        public PostViewHolder(View v1, Context context) {
            super(v1);
            img_avatar_post = (ImageView)v1.findViewById(R.id.img_avatar_post);
            img_play_pause = (ImageView)v1.findViewById(R.id.img_play_pause);
            img_thumbnail_post_video = (ImageView)v1.findViewById(R.id.img_thumbnail_post_video);
            txt_fullname_post = (TextView) v1.findViewById(R.id.txt_fullname_post);
            txt_designation_post = (TextView) v1.findViewById(R.id.txt_designation_post);
            txt_post_time = (TextView) v1.findViewById(R.id.txt_post_time);
            txt_description_post = (TextView) v1.findViewById(R.id.txt_description_post);
            btn_More = (Button) v1.findViewById(R.id.btn_More);
            rel_img_thumbnail_post_video = (RelativeLayout) v1.findViewById(R.id.rel_img_thumbnail_post_video);
            rel_mainview_post = (RelativeLayout) v1.findViewById(R.id.rel_mainview_post);
            txt_no_of_likes = (TextView) v1.findViewById(R.id.txt_no_of_likes);
            txt_no_of_comments = (TextView) v1.findViewById(R.id.txt_no_of_comments);
            txt_no_of_views = (TextView) v1.findViewById(R.id.txt_no_of_views);
        }
    }

    private PostReport_Request json_post_report(String post_id, int message_id) {

        PostReport_Request postreportreq = new PostReport_Request();
        postreportreq.setClient_id(Constcore.client_Id);
        postreportreq.setClient_secret(Constcore.client_Secret);
        postreportreq.setPost_id(post_id);
        postreportreq.setMessage_id(message_id);
        return postreportreq;
    }

    private PostReport_Request json_hide_post(String post_id) {

        PostReport_Request postreportreq = new PostReport_Request();
        postreportreq.setClient_id(Constcore.client_Id);
        postreportreq.setClient_secret(Constcore.client_Secret);
        postreportreq.setPost_id(post_id);
        return postreportreq;
    }

    public static void search_post_update_like(int like_count, int position)
    {
        Boolean val = false;
        Post post = (Post) arr_post.get(position);
        post.setLikes(like_count);
        if(post.getUser_liked())
        {
            post.setUser_liked(false);
        }
        else
        {
            post.setUser_liked(true);
        }

        arr_post.set(position, post);
        listView_Post.getAdapter().notifyDataSetChanged();

    }

    public static void search_post_update_comments(int comment_count, int position)
    {
        Post post = (Post) arr_post.get(position);
        post.setComments(comment_count);
        arr_post.set(position, post);
        listView_Post.getAdapter().notifyDataSetChanged();

    }

    public static void search_post_unfollowuser_hide_report_post(int position)
    {
        arr_post.remove(position);
        listView_Post.getAdapter().notifyDataSetChanged();

    }




}
