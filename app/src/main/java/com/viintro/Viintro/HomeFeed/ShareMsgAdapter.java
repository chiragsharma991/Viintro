package com.viintro.Viintro.HomeFeed;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.Connection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.viintro.Viintro.Landing.SampleVideosData;
import com.viintro.Viintro.Messages.MessagesViewHolder;
import com.viintro.Viintro.Model.Connections;
import com.viintro.Viintro.Model.Messages;
import com.viintro.Viintro.Model.SendNewMsg_Request;
import com.viintro.Viintro.Model.ShareMsgContents;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Search_Post.LazyLoadingHolder;
import com.viintro.Viintro.Webservices.MarkLastMessageAsReadAPi;
import com.viintro.Viintro.listener.OnLoadMoreListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.viintro.Viintro.HomeFeed.InternalMessageFragment.arr_messages_in_post;
import static com.viintro.Viintro.HomeFeed.InternalMessageFragment.lv_Messages_inPost;
import static com.viintro.Viintro.Messages.MessagesFragment.arr_count_messages;
import static com.viintro.Viintro.Messages.MessagesFragment.listview_Messages;
import static com.viintro.Viintro.R.id.tvName;

/**
 * Created by rkanawade on 12/04/17.
 */

public class ShareMsgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context context;
    LayoutInflater mInflater;
    private static int checkBoxCounter = 0;
    ArrayList arr_messages_in_post;
    ArrayList user_id_count;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    JSONObject postObject;
    public static SendNewMsg_Request sendNewMsg_request;

    public ShareMsgAdapter(Context context, ArrayList arr_messages_in_post, JSONObject postObject) {

        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.arr_messages_in_post = arr_messages_in_post;
        checkBoxCounter = 0;
        this.postObject = postObject;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) lv_Messages_inPost.getLayoutManager();
        lv_Messages_inPost.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

//                Log.e("isLoading"," "+isLoading);
//                Log.e("totalItemCount"," "+totalItemCount);
//                Log.e("lastVisibleItem"," "+(lastVisibleItem));


                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && arr_count_messages == 20) {
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
        return arr_messages_in_post.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                View v0 = inflater.inflate(R.layout.share_msg_contents, parent, false);
                viewHolder = new ShareMessagesViewHolder(v0, context);
                break;
            case 1:
                View v1 = inflater.inflate(R.layout.layout_loading_item, parent, false);
                viewHolder = new LazyLoadingHolder(v1);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ShareMessagesViewHolder messagesViewHolder = (ShareMessagesViewHolder) holder;
                configuremessagesViewHolder(messagesViewHolder, position);
                break;
            case 1:
                LazyLoadingHolder lazyloadingholder = (LazyLoadingHolder) holder;
                configureLazyLoading(lazyloadingholder, position);
                break;
        }

    }

    private void configuremessagesViewHolder(final ShareMessagesViewHolder holder, final int position)
    {
        final ShareMessagesViewHolder messagesViewHolder = (ShareMessagesViewHolder) holder;
        final Connections response = (Connections) arr_messages_in_post.get(position);
        if (response.getDisplay_pic() != null)
        {
            Glide.with(context).load(Uri.parse(response.getDisplay_pic())).centerCrop().into(messagesViewHolder.img_share_messages);
        }
        else
        {
            Glide.with(context).load(R.drawable.ic_launcher).centerCrop().into(messagesViewHolder.img_share_messages);
        }

        messagesViewHolder.txt_name.setText(response.getFullname());

        if(response.getUser_type().equals("student"))
        {
            messagesViewHolder.txt_designation.setText(response.getCourse()+", "+response.getUniversity());
        }
        else
        {
            messagesViewHolder.txt_designation.setText(response.getDesignation()+"@ "+response.getCompany());
        }

        messagesViewHolder.chk_msgs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked && checkBoxCounter>=5)
                {
                    //share.setIscheck(false);
                    messagesViewHolder.chk_msgs.setChecked(false);
                    Log.d("","You can select maximum 5 users");
                    Toast.makeText(context,"Maximum 5 users can be selected",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    if (isChecked)
                    {
                        //viewHolder.cbBackground.setBackgroundResource(R.drawable.checkbox_background_checked);
                        checkBoxCounter++;
                        for(int i=0; i<4; i++){
                            user_id_count = new ArrayList();
                            user_id_count.add(response.getId());
                            Log.d("user_id_count",""+user_id_count);
                        }

                    }
                    else
                    {
                        // viewHolder.cbBackground.setBackgroundResource(R.drawable.checkbox_background);
                        checkBoxCounter--;
                    }

                    sendNewMsg_request = new SendNewMsg_Request();
                    try {
                        sendNewMsg_request.setGroup_name("");
                        sendNewMsg_request.setGroup_display_pic("");
                        sendNewMsg_request.setPost_id(postObject.getString("post_id"));
                        sendNewMsg_request.setPost_slug(postObject.getString("post_slug"));
                        sendNewMsg_request.setPost_owner_id(postObject.getInt("post_owner_id"));
                        sendNewMsg_request.setPost_owner_name(postObject.getString("post_owner_name"));
                        sendNewMsg_request.setPost_description(postObject.getString("post_description"));
                        sendNewMsg_request.setType("post");
                        sendNewMsg_request.setText("");
                        sendNewMsg_request.setUser_ids(5);
                    } catch (JSONException e) {
                        e.printStackTrace();
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
        if(arr_messages_in_post.get(position) == null)
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

    private class ShareMessagesViewHolder extends RecyclerView.ViewHolder {
        ImageView img_share_messages;
        TextView txt_name, txt_designation, txt_location;
        CheckBox chk_msgs;

        public ShareMessagesViewHolder(View v, Context context)
        {
            super(v);
            txt_name = (TextView) v.findViewById(R.id.txt_name);
            txt_designation = (TextView) v.findViewById(R.id.txt_designation);
            //txt_location = (TextView) v.findViewById(R.id.txt_location);
            img_share_messages = (ImageView) v.findViewById(R.id.img_share_messages);
            chk_msgs = (CheckBox) v.findViewById(R.id.chk_msgs);
        }

    }
}
