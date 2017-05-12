package com.viintro.Viintro.Invitations;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.viintro.Viintro.Model.Invitations;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Search_Post.LazyLoadingHolder;
import com.viintro.Viintro.listener.OnLoadMoreListener;

import java.util.ArrayList;

import static com.viintro.Viintro.Invitations.InvitationsSentFragment.arr_count_invitations_sent;
import static com.viintro.Viintro.Invitations.InvitationsSentFragment.recyclerView_sentInvitations;
import static com.viintro.Viintro.Invitations.InvitationsSentFragment.txt_no_invitations_sent;
import static com.viintro.Viintro.Webservices.Cancel_Connection_RequestAPI.req_cancel_connection_API;

/**
 * Created by rkanawade on 13/04/17.
 */

public class InvitationsSentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    static ArrayList arr_invitations_sent;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;


    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public InvitationsSentAdapter(Context context, ArrayList<Invitations> arr_invitations_sent)
    {
        this.context = context;
        this.arr_invitations_sent = arr_invitations_sent;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView_sentInvitations.getLayoutManager();
        recyclerView_sentInvitations.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

//                Log.e("isLoading"," "+isLoading);
//                Log.e("totalItemCount"," "+totalItemCount);
//                Log.e("lastVisibleItem"," "+(lastVisibleItem));


                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && arr_count_invitations_sent == 20) {
                    Log.e("in condition check","");
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }


    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public int getItemCount() {
        return arr_invitations_sent.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                View v0 = inflater.inflate(R.layout.adapter_invitations_sent, parent, false);
                viewHolder = new InvitationSentHolder(v0, context);
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
                InvitationSentHolder invitationHolder = (InvitationSentHolder) holder;
                configureinvitationHolder(invitationHolder, position);
                break;
            case 1:
                LazyLoadingHolder lazyloadingholder = (LazyLoadingHolder) holder;
                configureLazyLoading(lazyloadingholder, position);
                break;
        }



    }

    private void configureinvitationHolder(InvitationSentHolder holder, final int position) {
        InvitationSentHolder invitationHolder = (InvitationSentHolder) holder;
        final Invitations response = (Invitations) arr_invitations_sent.get(position);
        if (response.getDisplay_pic() != null)
        {
            Glide.with(context).load(Uri.parse(response.getDisplay_pic())).centerCrop().error(R.color.black).into(invitationHolder.img_sent_invitation_avatar);
        }
        else
        {
            Glide.with(context).load(R.drawable.ic_launcher).centerCrop().into(invitationHolder.img_sent_invitation_avatar);
        }
        invitationHolder.txt_sent_invitation_name.setText(response.getFullname());

        if(response.getUser_type().equals("student"))
        {
            invitationHolder.txt_sent_designation.setText(response.getCourse()+" @ "+response.getUniversity());
        }
        else
        {
            invitationHolder.txt_sent_designation.setText(response.getDesignation()+" @ "+response.getCompany());
        }




        invitationHolder.btn_Cancel_Request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("position"," "+position);
                if(CommonFunctions.chkStatus(context))
                {
                    CommonFunctions.sDialog(context, "Cancelling Request");
                    req_cancel_connection_API(context, response.getId(), position, "Sent_Invitations");

                }
                else
                {
                    CommonFunctions.displayToast(context,context.getResources().getString(R.string.network_connection));
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
        if (arr_invitations_sent.get(position) == null) {
            return 1;
        } else {
            return 0;
        }
    }

    public void setLoaded() {
        isLoading = false;
    }

    public class InvitationSentHolder extends RecyclerView.ViewHolder {
        public RelativeLayout rel_invitations_Sent;
        public ImageView img_sent_invitation_avatar;
        public TextView txt_sent_invitation_name;
        public TextView txt_sent_designation;
        public Button btn_Cancel_Request;


        public InvitationSentHolder(View v, Context context) {
            super(v);
            rel_invitations_Sent = (RelativeLayout) v.findViewById(R.id.rel_invitations_Sent);
            img_sent_invitation_avatar = (ImageView) v.findViewById(R.id.img_sent_invitation_avatar);
            txt_sent_invitation_name = (TextView) v.findViewById(R.id.txt_sent_invitation_name);
            txt_sent_designation = (TextView) v.findViewById(R.id.txt_sent_designation);
            btn_Cancel_Request = (Button) v.findViewById(R.id.btn_Cancel_Request);

        }
    }



    public static void update_row_on_send_invitation_page(int position)
    {
        arr_invitations_sent.remove(position);
        if(arr_invitations_sent.size() == 0)
        {
            txt_no_invitations_sent.setText("No Sent Invitations");
        }
        recyclerView_sentInvitations.getAdapter().notifyDataSetChanged();

    }

}

