package com.viintro.Viintro.Connections;

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
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.viintro.Viintro.Invitations.InvitationsActivity;
import com.viintro.Viintro.Landing.SampleVideoFullScreen;
import com.viintro.Viintro.Model.Invitations;
import com.viintro.Viintro.Model.MySuggestions_Response;
import com.viintro.Viintro.PublicProfile.PublicProfileActivity;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Search_Post.LazyLoadingHolder;
import com.viintro.Viintro.VideoRecord.VideoRecordingActivity;
import com.viintro.Viintro.listener.OnLoadMoreListener;

import java.util.ArrayList;

import static com.viintro.Viintro.Connections.ConnectionFragment.arr_count_suggestions;
import static com.viintro.Viintro.Connections.ConnectionFragment.connection_count;
import static com.viintro.Viintro.Connections.ConnectionFragment.listView_Suggestion;
import static com.viintro.Viintro.Connections.ConnectionFragment.upd_conn_text_on_suggestion_page;
import static com.viintro.Viintro.Webservices.ConnectionAcceptAPI.req_connection_accept_API;
import static com.viintro.Viintro.Webservices.ConnectionRejectAPI.req_connection_reject_API;
import static com.viintro.Viintro.Webservices.ConnectionRemoveAPI.req_connection_remove_API;
import static com.viintro.Viintro.Webservices.FollowAPI.req_follow_request_API;

/**
 * Created by hasai on 02/03/17.
 */
public class SuggestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    static ArrayList arr_invitations_suggestions;
    static ArrayList arr_invitations_id;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;


    public SuggestionAdapter(Context context, ArrayList arr_invitations_suggestions, ArrayList arr_invitations_id) {

        this.context = context;
        this.arr_invitations_suggestions = arr_invitations_suggestions;
        this.arr_invitations_id = arr_invitations_id;


        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) listView_Suggestion.getLayoutManager();
        listView_Suggestion.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

//                Log.e("isLoading"," "+isLoading);
//                Log.e("totalItemCount"," "+totalItemCount);
//                Log.e("lastVisibleItem"," "+(lastVisibleItem));

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && arr_count_suggestions == 20)
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
        return arr_invitations_suggestions.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (viewType) {
            case 0:
                View v = inflater.inflate(R.layout.adapter_connections_button, viewGroup, false);
                viewHolder = new ConnectionButtonHolder(v, context);
                break;
            case 1:
                View v1 = inflater.inflate(R.layout.adapter_connections_header, viewGroup, false);
                viewHolder = new ConnectionHeaderHolder(v1, context);
                break;
            case 2:
                View v2 = inflater.inflate(R.layout.adapter_no_invitations, viewGroup, false);
                viewHolder = new NoInvitationHolder(v2, context);
                break;
            case 3:
                View v3 = inflater.inflate(R.layout.adapter_invitations, viewGroup, false);
                viewHolder = new InvitationHolder(v3, context);
                break;
            case 4:
                View v4 = inflater.inflate(R.layout.adapter_seeall, viewGroup, false);
                viewHolder = new SeeAllHolder(v4, context);
                break;
            case 5:
                View v5 = inflater.inflate(R.layout.adapter_suggestions, viewGroup, false);
                viewHolder = new SuggestionsViewHolder(v5, context);
                break;
            case 6:
                View v6 = inflater.inflate(R.layout.layout_loading_item, viewGroup, false);
                viewHolder = new LazyLoadingHolder(v6);
                break;

        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        switch (holder.getItemViewType()) {
            case 0:
                ConnectionButtonHolder vh = (ConnectionButtonHolder) holder;
                configureViewHolder(vh, position);
                break;
            case 1:
                ConnectionHeaderHolder vh1 = (ConnectionHeaderHolder) holder;
                configureViewHolder1(vh1, position);
                break;
            case 2:
                NoInvitationHolder vh2 = (NoInvitationHolder) holder;
                configureViewHolder2(vh2, position);
                break;
            case 3:
                InvitationHolder vh3 = (InvitationHolder) holder;
                configureViewHolder3(vh3, position);
                break;
            case 4:
                SeeAllHolder vh4 = (SeeAllHolder) holder;
                configureViewHolder4(vh4, position);
                break;
            case 5:
                SuggestionsViewHolder vh5 = (SuggestionsViewHolder) holder;
                configureViewHolder5(vh5, position);
                break;
            case 6:
                LazyLoadingHolder lazyloadingholder = (LazyLoadingHolder) holder;
                configureLazyLoading(lazyloadingholder, position);
                break;

        }


    }




    @Override
    public int getItemViewType(int position) {

        String type;
        //Log.e("arr_invitations_suggestions"," "+arr_invitations_suggestions.size());
//
        if (arr_invitations_suggestions.get(position) instanceof Invitations)
        {
            ArrayList<Invitations> arr_invitations_suggestions_list = arr_invitations_suggestions;
            type = arr_invitations_suggestions_list.get(position).getType();
            //Log.e("type", " " + type);
            if(type.equals("connections"))
            {
                return 0;
            }
            if(type.equals("header_invitations"))
            {
                return 1;
            }
            if (type.equals("noinvitations"))
            {
                return 2;
            }
            if (type.equals("invitations"))
            {
                return 3;
            }
            if (type.equals("see all"))
            {
                return 4;
            }
            if (type.equals("People you may know")) {
                return 1;
            }
        }
        else if (arr_invitations_suggestions.get(position) instanceof MySuggestions_Response)
        {
            ArrayList<MySuggestions_Response> arr_suggestion_list = arr_invitations_suggestions;
            type = arr_suggestion_list.get(position).getType();
            //Log.e("type", " " + type);
            if (type.equals("suggestions")) {
                return 5;
            }
        }
        else
        {
            return 6;
        }


        return -1;

    }

    private void configureViewHolder(ConnectionButtonHolder holder, final int position)
    {

        holder.btn_connections.setText(connection_count +" Connections");
        holder.btn_connections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent int_connection = new Intent(context, ConnectionActivity.class);
                context.startActivity(int_connection);
            }
        });

    }

    private void configureViewHolder1(ConnectionHeaderHolder holder, final int position)
    {
        final Invitations response = (Invitations) arr_invitations_suggestions.get(position);
        if(response.getType().equals("header_invitations"))
        {
            holder.txt_header_connections.setText("Invitations");
        }
        else
        {
            holder.txt_header_connections.setText("People you may know");
        }

    }

    private void configureViewHolder2(NoInvitationHolder holder, final int position) {

        holder.txt_no_invitations.setText("No pending invitations");
        holder.btn_Manage_All.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int_invitations = new Intent(context, InvitationsActivity.class);
                context.startActivity(int_invitations);
            }
        });

    }

    private void configureViewHolder3(InvitationHolder holder, final int position) {
        final InvitationHolder invitationHolder =  holder;
        final Invitations response = (Invitations) arr_invitations_suggestions.get(position);
        if (response.getDisplay_pic() != null)
        {
            Glide.with(context).load(Uri.parse(response.getDisplay_pic())).centerCrop().error(R.color.black).into(invitationHolder.img_invitation_avatar);
        }
        else
        {
            Glide.with(context).load(R.drawable.ic_launcher).centerCrop().into(invitationHolder.img_invitation_avatar);
        }
        invitationHolder.txt_invitation_name.setText(response.getFullname());
        if(response.getUser_type().equals("student"))
        {
            invitationHolder.txt_designation.setText(response.getCourse()+", "+response.getUniversity());
        }
        else
        {
            invitationHolder.txt_designation.setText(response.getDesignation()+"@ "+response.getCompany());
        }

        if(response.getMessage() == null || response.getMessage().equals(""))
        {
            invitationHolder.txt_message.setVisibility(View.GONE);
        }
        else
        {
            invitationHolder.txt_message.setVisibility(View.VISIBLE);
            invitationHolder.txt_message.setText(response.getMessage());
        }


        if (response.getThumbnail() == null || response.getThumbnail().equals(""))
        {
            invitationHolder.rel_img_thumbnail_connect_video.setVisibility(View.GONE);
            Glide.with(context).load(R.drawable.ic_launcher).centerCrop().into(invitationHolder.img_thumbnail_connect_video);
        }
        else
        {
            invitationHolder.rel_img_thumbnail_connect_video.setVisibility(View.VISIBLE);
            Glide.with(context).load(response.getThumbnail()).into(invitationHolder.img_thumbnail_connect_video);
        }




        invitationHolder.btn_Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(CommonFunctions.chkStatus(context))
                {
                    CommonFunctions.sDialog(context, "Accepting Request");
                    req_connection_accept_API(context, response.getId(), position, "My_Suggestions_Invitations");

                }
                else
                {
                    CommonFunctions.displayToast(context,context.getResources().getString(R.string.network_connection));
                }

            }
        });

        invitationHolder.btn_Remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(CommonFunctions.chkStatus(context))
                {
                    CommonFunctions.sDialog(context, "Rejecting Request");
                    req_connection_reject_API(context, response.getId(), position, "My_Suggestions_Invitations");

                }
                else
                {
                    CommonFunctions.displayToast(context,context.getResources().getString(R.string.network_connection));
                }
            }
        });

        invitationHolder.rel_img_thumbnail_connect_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent int_video_screen = new Intent(context, SampleVideoFullScreen.class);
                int_video_screen.putExtra("Current url", response.getSecure_url_mp4());
                context.startActivity(int_video_screen);
            }
        });

        invitationHolder.rel_invitations_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int_publicprofile = new Intent(context, PublicProfileActivity.class);
                int_publicprofile.putExtra("user_id", response.getId());
                int_publicprofile.putExtra("from_Activity","My_Suggestions_Invitations");
                int_publicprofile.putExtra("position",position);
                context.startActivity(int_publicprofile);
            }
        });

    }

    private void configureViewHolder4(SeeAllHolder holder, final int position)
    {
        holder.txt_see_All.setText("See all");
        holder.txt_see_All.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int_invitations = new Intent(context, InvitationsActivity.class);
                context.startActivity(int_invitations);
            }
        });

    }

    private void configureViewHolder5(SuggestionsViewHolder holder, final int position) {

        final SuggestionsViewHolder suggestion_viewholder =  holder;
        final MySuggestions_Response response = (MySuggestions_Response) arr_invitations_suggestions.get(position);
        if (response.getDisplay_pic() != null)
        {
            Glide.with(context).load(response.getDisplay_pic()).centerCrop().into(suggestion_viewholder.img_suggestions);
        }
        else
        {
            Glide.with(context).load(R.drawable.ic_launcher).centerCrop().into(suggestion_viewholder.img_suggestions);
        }

        if(response.getUser_type().equals("student"))
        {
            suggestion_viewholder.txt_designation.setText(response.getCourse()+", "+response.getUniversity());
        }
        else
        {
            suggestion_viewholder.txt_designation.setText(response.getDesignation()+"@ "+response.getCompany());
        }

//        if(response.getBlock())
//        {
//            suggestion_viewholder.btn_Connect_suggestions.setVisibility(View.GONE);
//            suggestion_viewholder.btn_Follow_suggestions.setVisibility(View.GONE);
//        }
//        else
//        {
            suggestion_viewholder.btn_Connect_suggestions.setVisibility(View.VISIBLE);
            suggestion_viewholder.btn_Follow_suggestions.setVisibility(View.VISIBLE);
        //}

        if(response.getConnection_requested() == true)
        {
            suggestion_viewholder.btn_Connect_suggestions.setText("Request Sent");
        }

        if(response.getConnection_requested() == false)
        {
            suggestion_viewholder.btn_Connect_suggestions.setText("Connect");
        }
        if(response.getFollowing() == true)
        {
            suggestion_viewholder.btn_Follow_suggestions.setText(context.getResources().getString(R.string.Following));

        }
        if(response.getFollowing() == false)
        {
            suggestion_viewholder.btn_Follow_suggestions.setText(context.getResources().getString(R.string.Follow));
        }

        suggestion_viewholder.txt_suggestions_name.setText(response.getFullname());

        suggestion_viewholder.btn_Connect_suggestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btn_Connect = (Button) view;
                if(btn_Connect.getText().equals("Connect"))
                {
                    Intent int_video_record = new Intent(context, VideoRecordingActivity.class);
                    int_video_record.putExtra("from", "connect");
                    int_video_record.putExtra("from_Activity", "My_Suggestions");
                    int_video_record.putExtra("user_id", response.getId());
                    int_video_record.putExtra("position", position);
                    ((Activity)context).startActivity(int_video_record);
                }

            }
        });

        suggestion_viewholder.btn_Follow_suggestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn_follow = (Button) v;
                if(btn_follow.getText().equals(context.getResources().getString(R.string.Follow))) {
                    CommonFunctions.sDialog(context, "Following...");
                    req_follow_request_API(context, response.getId(), position, "My_Suggestions");
                }

            }
        });

        suggestion_viewholder.rel_suggestions_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int_publicprofile = new Intent(context, PublicProfileActivity.class);
                int_publicprofile.putExtra("user_id", response.getId());
                int_publicprofile.putExtra("from_Activity","My_Suggestions");
                int_publicprofile.putExtra("position",position);
                context.startActivity(int_publicprofile);
            }
        });
    }

    private void configureLazyLoading(LazyLoadingHolder lazyloadingholder, int position)
    {
        lazyloadingholder.progressbar.setIndeterminate(true);
    }

    public void setLoaded() {
        isLoading = false;
    }

    // update row on click of "Connect" and "Follow"  button in "People you may know List" and from "PublicProfileActivity"
    public static void upd_row_in_sugg_page(int position, String condition)
    {
        if(condition.equals("follow"))
        {
            MySuggestions_Response suggestions_response = (MySuggestions_Response) arr_invitations_suggestions.get(position);
            suggestions_response.setFollowing(true);
            arr_invitations_suggestions.set(position, suggestions_response);
            listView_Suggestion.getAdapter().notifyDataSetChanged();
        }
        if(condition.equals("unfollow"))
        {
            MySuggestions_Response suggestions_response = (MySuggestions_Response) arr_invitations_suggestions.get(position);
            suggestions_response.setFollowing(false);
            arr_invitations_suggestions.set(position, suggestions_response);
            listView_Suggestion.getAdapter().notifyDataSetChanged();
        }
        else if(condition.equals("connect"))
        {
            arr_invitations_suggestions.remove(position);
            listView_Suggestion.getAdapter().notifyDataSetChanged();
        }
        else if(condition.equals("block"))
        {
            arr_invitations_suggestions.remove(position);
            listView_Suggestion.getAdapter().notifyDataSetChanged();
        }


    }

    // update invitation row on click of "Accept and "Ignore"
    public static void update_invitation_row_on_suggestion_page(int position, int user_id, String condition)
    {
        arr_invitations_suggestions.remove(position);
        arr_invitations_id.remove(arr_invitations_id.indexOf(user_id));

        if(arr_invitations_id.size() == 0)
        {
            for(int i = 0; i < arr_invitations_suggestions.size(); i++)
            {
                if (arr_invitations_suggestions.get(i) instanceof Invitations)
                {
                    Invitations invitations_response = (Invitations) arr_invitations_suggestions.get(i);
                    if (invitations_response.getType().equals("see all")) {
                        arr_invitations_suggestions.remove(i);
                    }
                }
            }
//            arr_invitations_suggestions.remove(1);// Remove Invitations Header

            Invitations invitations_response = new Invitations();
            invitations_response.setType("noinvitations");
            arr_invitations_suggestions.set(1,invitations_response);

        }
        listView_Suggestion.getAdapter().notifyDataSetChanged();

        if(condition.equals("connect"))
        {
            //update connection count in Connection Tab
            upd_conn_text_on_suggestion_page(connection_count + 1);
        }
    }

    // update invitation row on click of "Accept and "Ignore" from InvitationActivity && Public Profile Page
    public static void upd_inv_row_in_sugg_page_frm_invitation(int user_id, String condition)
    {
        for(int i = 0; i < arr_invitations_suggestions.size(); i++) {
            if (arr_invitations_suggestions.get(i) instanceof Invitations) {

                Invitations invitations_response = (Invitations) arr_invitations_suggestions.get(i);
                if (invitations_response.getId() == user_id)
                {
                    arr_invitations_suggestions.remove(i);
                    arr_invitations_id.remove(arr_invitations_id.indexOf(user_id));
                    if (arr_invitations_id.size() == 0)
                    {
                        for (int j = 0; j < arr_invitations_suggestions.size(); j++)
                        {
                            if (arr_invitations_suggestions.get(i) instanceof Invitations) {
                                Invitations invitations = (Invitations) arr_invitations_suggestions.get(j);
                                if (invitations.getType().equals("see all"))
                                {
                                    arr_invitations_suggestions.remove(j);
//                                    arr_invitations_suggestions.remove(1); // Remove Invitations Header
                                    Invitations invitations_new = new Invitations();
                                    invitations_new.setType("noinvitations");
                                    arr_invitations_suggestions.set(1, invitations_new);
                                    break;

                                }
                            }

                        }
                    }
                    listView_Suggestion.getAdapter().notifyDataSetChanged();
                    break;
                }

            }
        }

            if(condition.equals("connect"))
            {
                //update connection count in Connection Tab
                upd_conn_text_on_suggestion_page(connection_count + 1);
            }
        }

//    // update people you may know row on click of "Connect"  from PublicProfileActivity
//    public static void upd_peopleyou_row_in_sugg_page_frm_publicprofile(int user_id, String condition)
//    {
//
//        for(int i = 0; i < arr_invitations_suggestions.size(); i++)
//        {
//            if (arr_invitations_suggestions.get(i) instanceof MySuggestions_Response)
//            {
//
//                MySuggestions_Response mySuggestions_response = (MySuggestions_Response) arr_invitations_suggestions.get(i);
//                if (mySuggestions_response.getId() == user_id)
//                {
//                    if(condition.equals("connect"))
//                    {
//                        //mySuggestions_response.setConnection_requested(true);
//                        arr_invitations_suggestions.remove(i);
//                    }
//                    else if(condition.equals("follow"))
//                    {
//                        mySuggestions_response.setFollowing(true);
//                        arr_invitations_suggestions.set(i, mySuggestions_response);
//                    }
//                    else if(condition.equals("unfollow"))
//                    {
//                        mySuggestions_response.setFollowing(false);
//                        arr_invitations_suggestions.set(i, mySuggestions_response);
//                    }
//
//
//                    listView_Suggestion.getAdapter().notifyDataSetChanged();
//                    break;
//                }
//
//            }
//        }
//
//
//    }




}
