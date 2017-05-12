package com.viintro.Viintro.Invitations;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.viintro.Viintro.Connections.InvitationHolder;
import com.viintro.Viintro.Model.Invitations;
import com.viintro.Viintro.PublicProfile.PublicProfileActivity;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Search_Post.LazyLoadingHolder;
import com.viintro.Viintro.listener.OnLoadMoreListener;

import java.util.ArrayList;

import static com.viintro.Viintro.Connections.SuggestionAdapter.upd_inv_row_in_sugg_page_frm_invitation;
import static com.viintro.Viintro.Invitations.InvitationsReceivedFragment.arr_count_invitations;
import static com.viintro.Viintro.Webservices.ConnectionAcceptAPI.req_connection_accept_API;
import static com.viintro.Viintro.Webservices.ConnectionRejectAPI.req_connection_reject_API;

/**
 * Created by hasai on 22/03/17.
 */
public class InvitationsReceivedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    static ArrayList arr_invitations;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    static RecyclerView recyclerView_invitations;
    static TextView txt_no_invitations;


    public InvitationsReceivedAdapter(Context context, ArrayList<Invitations> arr_invitations, RecyclerView recyclerView_invitations, TextView txt_no_invitations) {
        this.context = context;
        this.arr_invitations = arr_invitations;
        this.recyclerView_invitations = recyclerView_invitations;
        this.txt_no_invitations = txt_no_invitations;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView_invitations.getLayoutManager();
        recyclerView_invitations.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

//                Log.e("isLoading"," "+isLoading);
//                Log.e("totalItemCount"," "+totalItemCount);
//                Log.e("lastVisibleItem"," "+(lastVisibleItem));


                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && arr_count_invitations == 20) {
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
        return arr_invitations.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                View v0 = inflater.inflate(R.layout.adapter_invitations, parent, false);
                viewHolder = new InvitationHolder(v0, context);
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
                InvitationHolder invitationHolder = (InvitationHolder) holder;
                configureinvitationHolder(invitationHolder, position);
                break;
            case 1:
                LazyLoadingHolder lazyloadingholder = (LazyLoadingHolder) holder;
                configureLazyLoading(lazyloadingholder, position);
                break;
        }



    }

    private void configureinvitationHolder(InvitationHolder holder, final int position) {
        InvitationHolder invitationHolder = (InvitationHolder) holder;
        final Invitations response = (Invitations) arr_invitations.get(position);
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
                Log.e("position"," "+position);
                if(CommonFunctions.chkStatus(context))
                {
                    CommonFunctions.sDialog(context, "Accepting Request");
                    req_connection_accept_API(context, response.getId(), position, "Invitations");


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
                Log.e("position"," "+position);
                if(CommonFunctions.chkStatus(context))
                {
                    CommonFunctions.sDialog(context, "Rejecting Request");
                    req_connection_reject_API(context, response.getId(), position, "Invitations");

                }
                else
                {
                    CommonFunctions.displayToast(context,context.getResources().getString(R.string.network_connection));
                }
            }
        });

        invitationHolder.rel_invitations_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int_publicprofile = new Intent(context, PublicProfileActivity.class);
                int_publicprofile.putExtra("user_id", response.getId());
                int_publicprofile.putExtra("from_Activity","Invitations");
                int_publicprofile.putExtra("position",position);
                context.startActivity(int_publicprofile);
            }
        });

    }

    private void configureLazyLoading(LazyLoadingHolder lazyloadingholder, int position)
    {
        lazyloadingholder.progressbar.setIndeterminate(true);
    }

    @Override
    public int getItemViewType(int position) {
        if (arr_invitations.get(position) == null) {
            return 1;
        } else {
            return 0;
        }
    }

    public void setLoaded() {
        isLoading = false;
    }

    // Update row in Invitation Page on click of "Connect" and "Follow" button
    public static void update_row_on_invitation_page(int position, int user_id, String condition)
    {
        arr_invitations.remove(position);
        recyclerView_invitations.getAdapter().notifyDataSetChanged();
        if(arr_invitations.size() == 0)
        {
            txt_no_invitations.setText("No Invitations");
            txt_no_invitations.setVisibility(View.VISIBLE);
        }

        upd_inv_row_in_sugg_page_frm_invitation(user_id, condition);
    }

}




