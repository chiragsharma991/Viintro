package com.viintro.Viintro.Connections;

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
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.BlockUser_Request;
import com.viintro.Viintro.Model.Connections;
import com.viintro.Viintro.Model.MySuggestions_Response;
import com.viintro.Viintro.PublicProfile.PublicProfileActivity;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Search_Post.LazyLoadingHolder;
import com.viintro.Viintro.listener.OnLoadMoreListener;

import java.util.ArrayList;

import static com.viintro.Viintro.Connections.ConnectionActivity.arr_count_myconnection;
import static com.viintro.Viintro.Connections.ConnectionActivity.listView_Connection;
import static com.viintro.Viintro.Connections.ConnectionFragment.listView_Suggestion;
import static com.viintro.Viintro.Connections.ConnectionFragment.upd_conn_text_on_suggestion_page;
import static com.viintro.Viintro.Webservices.BlockUserAPI.req_block_user_API;
import static com.viintro.Viintro.Webservices.ConnectionRemoveAPI.req_connection_remove_API;

/**
 * Created by hasai on 02/03/17.
 */
public class ConnectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    static ArrayList arr_myconnection;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    static TextView txt_no_of_connections;


    public ConnectionAdapter(Context context, ArrayList arr_myconnection, TextView txt_no_of_connections) {
        this.context = context;
        this.arr_myconnection = arr_myconnection;
        this.txt_no_of_connections = txt_no_of_connections;


        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) listView_Connection.getLayoutManager();
        listView_Connection.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

//                Log.e("isLoading"," "+isLoading);
//                Log.e("totalItemCount"," "+totalItemCount);
//                Log.e("lastVisibleItem"," "+(lastVisibleItem));

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && arr_count_myconnection == 20) {
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
        return arr_myconnection.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                View v0 = inflater.inflate(R.layout.adapter_connections, parent, false);
                viewHolder = new ConnectionViewHolder(v0, context);
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
                ConnectionViewHolder connectionViewHolder = (ConnectionViewHolder) holder;
                configureconnectionViewHolder(connectionViewHolder, position);
                break;
            case 1:
                LazyLoadingHolder lazyloadingholder = (LazyLoadingHolder) holder;
                configureLazyLoading(lazyloadingholder, position);
                break;
        }



    }

    private void configureconnectionViewHolder(ConnectionViewHolder holder, final int position)
    {
        ConnectionViewHolder connectionViewHolder = (ConnectionViewHolder) holder;
        final Connections response = (Connections) arr_myconnection.get(position);
        if (response.getDisplay_pic() != null)
        {
            Glide.with(context).load(Uri.parse(response.getDisplay_pic())).centerCrop().into(connectionViewHolder.img_connections);
        }
        else
        {
            Glide.with(context).load(R.drawable.ic_launcher).centerCrop().into(connectionViewHolder.img_connections);
        }

        connectionViewHolder.txt_connection_name.setText(response.getFullname());

        if(response.getUser_type().equals("student"))
        {
            connectionViewHolder.txt_designation.setText(response.getCourse()+", "+response.getUniversity());
        }
        else
        {
            connectionViewHolder.txt_designation.setText(response.getDesignation()+"@ "+response.getCompany());
        }




        connectionViewHolder.rel_connections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent int_publicprofile = new Intent(context, PublicProfileActivity.class);
                int_publicprofile.putExtra("user_id", response.getId());
                int_publicprofile.putExtra("from_Activity","My_Connections");
                int_publicprofile.putExtra("position",position);
                context.startActivity(int_publicprofile);


            }
        });


        connectionViewHolder.btn_More.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("pos ", " " + position);

                Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                PopupMenu popup = new PopupMenu(wrapper, view);
                CommonFunctions.setForceShowIcon(popup);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.popup, popup.getMenu());
                popup.show();
//                PopupMenu popup = new PopupMenu(context, view);
//                popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());

                for (int i = 0; i < popup.getMenu().size(); i++)
                {
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

                        if (item.getTitle().toString().equals("Save Profile"))
                        {

                        }
                        else if (item.getTitle().toString().equals("Remove Connections"))
                        {
                            if(CommonFunctions.chkStatus(context))
                            {
                                CommonFunctions.sDialog(context, "Removing Connection");
                                req_connection_remove_API(context, response.getId(), position, "My_Connections");

                            }
                            else
                            {
                                CommonFunctions.displayToast(context,context.getResources().getString(R.string.network_connection));
                            }
                        }
                        else if (item.getTitle().toString().equals("Block"))
                        {
                            if(CommonFunctions.chkStatus(context))
                            {
                                CommonFunctions.sDialog(context, "Blocking Connection");
                                BlockUser_Request blockUser_request = new BlockUser_Request();
                                blockUser_request.setClient_id(Constcore.client_Id);
                                blockUser_request.setClient_secret(Constcore.client_Secret);
                                blockUser_request.setBlocked_user_id(response.getId());
                                blockUser_request.setReason("");
                                req_block_user_API(context, blockUser_request, position, "My_Connections");

                            }
                            else
                            {
                                CommonFunctions.displayToast(context,context.getResources().getString(R.string.network_connection));
                            }
                        }

                        return true;
                    }
                });


                //popup.show();

            }
        });
    }

    private void configureLazyLoading(LazyLoadingHolder lazyloadingholder, int position)
    {
        lazyloadingholder.progressbar.setIndeterminate(true);
    }


    @Override
    public int getItemViewType(int position) {
        if(arr_myconnection.get(position) == null)
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

    //update row in Connection Activity after removing connections
    public static void upd_conn_row_in_Conn_page(int position, String condition)
    {
        if(condition.equals("remove_connection"))
        {
            arr_myconnection.remove(position);
            if (arr_myconnection.size() == 0)
            {
                txt_no_of_connections.setText("No Connection");
                txt_no_of_connections.setVisibility(View.VISIBLE);
            }
            listView_Connection.getAdapter().notifyDataSetChanged();
            //update connection count in Connection Tab
            upd_conn_text_on_suggestion_page(arr_myconnection.size());

        }
        else if(condition.equals("block") || condition.equals("unblock"))
        {

            arr_myconnection.remove(position);
            if (arr_myconnection.size() == 0)
            {
                txt_no_of_connections.setText("No Connection");
                txt_no_of_connections.setVisibility(View.VISIBLE);
            }
            listView_Connection.getAdapter().notifyDataSetChanged();
            //update connection count in Connection Tab
            upd_conn_text_on_suggestion_page(arr_myconnection.size());
        }

    }




}
