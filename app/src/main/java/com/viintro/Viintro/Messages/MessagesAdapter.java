package com.viintro.Viintro.Messages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.viintro.Viintro.Model.Messages;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Reusables.TextFont;
import com.viintro.Viintro.Search_Post.LazyLoadingHolder;
import com.viintro.Viintro.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.Locale;

import static com.viintro.Viintro.Messages.MessagesFragment.arr_count_messages;
import static com.viintro.Viintro.Messages.MessagesFragment.array_list;
import static com.viintro.Viintro.Messages.MessagesFragment.listview_Messages;

/**
 * Created by hasai on 02/03/17.
 */
public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    static ArrayList arr_messages;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    TextView txt_no_of_connections;



    public MessagesAdapter(Context context, ArrayList arr_messages)
    {
        this.context = context;
        this.arr_messages = arr_messages;


        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) listview_Messages.getLayoutManager();
        listview_Messages.addOnScrollListener(new RecyclerView.OnScrollListener() {

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
        return arr_messages.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                View v0 = inflater.inflate(R.layout.adapter_messages, parent, false);
                viewHolder = new MessagesViewHolder(v0, context);
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
                MessagesViewHolder messagesViewHolder = (MessagesViewHolder) holder;
                configuremessagesViewHolder(messagesViewHolder, position);
                break;
            case 1:
                LazyLoadingHolder lazyloadingholder = (LazyLoadingHolder) holder;
                configureLazyLoading(lazyloadingholder, position);
                break;
        }



    }

    private void configuremessagesViewHolder(MessagesViewHolder holder, final int position)
    {
        MessagesViewHolder messagesViewHolder = (MessagesViewHolder) holder;
        final Messages response = (Messages) arr_messages.get(position);
        if (response.getGroup_display_pic() != null)
        {
            Glide.with(context).load(Uri.parse(response.getGroup_display_pic())).centerCrop().into(messagesViewHolder.img_messages);
        }
        else
        {
            Glide.with(context).load(R.drawable.ic_launcher).centerCrop().into(messagesViewHolder.img_messages);
        }


        messagesViewHolder.txt_message_time.setText(CommonFunctions.getTime(response.getTime_ago()));
        messagesViewHolder.txt_person_name.setText(response.getGroup_name());

        if(response.getRead())
        {
            messagesViewHolder.txt_last_message.setTypeface(TextFont.opensans_regular(context));
            messagesViewHolder.txt_last_message.setText(response.getLast_message());
        }
        else
        {
            messagesViewHolder.txt_last_message.setTypeface(TextFont.opensans_semibold(context));
            messagesViewHolder.txt_last_message.setText(response.getLast_message());
        }

        messagesViewHolder.rel_messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // open chat page
                Intent int_message_conversation = new Intent(context, MessageConversationActivity.class);
                int_message_conversation.putExtra("flag_read", response.getRead());
                int_message_conversation.putExtra("group_id", response.getGroup_id());
                int_message_conversation.putExtra("position", position);
                int_message_conversation.putExtra("name", response.getGroup_name());
                context.startActivity(int_message_conversation);


            }
        });


    }

    private void configureLazyLoading(LazyLoadingHolder lazyloadingholder, int position)
    {
        lazyloadingholder.progressbar.setIndeterminate(true);
    }


    @Override
    public int getItemViewType(int position) {
        if(arr_messages.get(position) == null)
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

    public static void markasread(int position)
    {
        Messages messages = (Messages) arr_messages.get(position);
        messages.setRead(true);
        arr_messages.set(position,messages);
        listview_Messages.getAdapter().notifyDataSetChanged();
    }

    // Filter Class
    public void filter(String charText)
    {
//        if (arr_messages.size() != 0)
//        {
//            arraylist = new ArrayList<>();
//        }
//        arraylist.addAll(arr_messages);

        Log.e("charText"," "+charText);
        charText = charText.toLowerCase(Locale.getDefault());
        arr_messages.clear();
        if (charText.length() == 0) {
            arr_messages.addAll(array_list);
        }
        else
        {
            Log.e("arraylist size"," "+array_list.size());
            for (Messages wp : array_list)
            {
                if (wp.getGroup_name().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    arr_messages.add(wp);
                }

                if (wp.getLast_message().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    arr_messages.add(wp);
                }


            }
        }
        notifyDataSetChanged();
    }


}
