package com.viintro.Viintro.Messages;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.viintro.Viintro.Model.Connections;
import com.viintro.Viintro.Model.MyConnections_Response;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Search_Post.LazyLoadingHolder;
import com.viintro.Viintro.listener.OnLoadMoreListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.viintro.Viintro.Messages.NewMessageActivity.arr_count_connection_new_message;
import static com.viintro.Viintro.Messages.NewMessageActivity.listView_NewMsgConnection;


/**
 * Created by hasai on 02/03/17.
 */
public class NewMessageAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private final LinearLayout selectedUser_view;
    Context context;
    private ArrayList arr_connection_new_message;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    static TextView txt_no_of_connections;
    private boolean check=false;
    private boolean[] checkArray;
    private ViewGroup addView;
    private NewMessageViewHolder newMessageViewHolder;
    private ViewGroup addV;


    public NewMessageAdpter(Context context, ArrayList arr_connection_new_message, TextView txt_no_of_connections, LinearLayout selectedUser_view) {
        this.context = context;
        this.arr_connection_new_message = arr_connection_new_message;
        this.txt_no_of_connections = txt_no_of_connections;
        this.selectedUser_view = selectedUser_view;
        checkArray= new boolean[arr_connection_new_message.size()];
        Log.e("TAG", "array List: "+arr_connection_new_message.size() );
        for (int i = 0; i <arr_connection_new_message.size() ; i++) {
            checkArray[i]=false;
        }


        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) listView_NewMsgConnection.getLayoutManager();
        listView_NewMsgConnection.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

//                Log.e("isLoading"," "+isLoading);
//                Log.e("totalItemCount"," "+totalItemCount);
//                Log.e("lastVisibleItem"," "+(lastVisibleItem));

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && arr_count_connection_new_message == 20) {
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
        Log.e("TAG", "getItemCount: "+arr_connection_new_message.size() );
        return arr_connection_new_message.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                View v0 = inflater.inflate(R.layout.adapter_new_msg, parent, false);
                viewHolder = new NewMessageViewHolder(v0, context);
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
                NewMessageViewHolder newMessageViewHolder = (NewMessageViewHolder) holder;
                configurenewmessageViewHolder(newMessageViewHolder, position);
                break;
            case 1:
                LazyLoadingHolder lazyloadingholder = (LazyLoadingHolder) holder;
                configureLazyLoading(lazyloadingholder, position);
                break;
        }



    }

    private void configurenewmessageViewHolder(NewMessageViewHolder holder, final int position)
    {
        newMessageViewHolder = (NewMessageViewHolder) holder;
        final Connections response = (Connections) arr_connection_new_message.get(position);
        if (response.getDisplay_pic() != null)
        {
            Glide.with(context).load(Uri.parse(response.getDisplay_pic())).centerCrop().into(newMessageViewHolder.img_new_msg_connections);
        }
        else
        {
            Glide.with(context).load(R.drawable.ic_launcher).centerCrop().into(newMessageViewHolder.img_new_msg_connections);
        }

        newMessageViewHolder.txt_new_msg_connection_name.setText(response.getFullname());

        if(response.getUser_type().equals("student"))
        {
            newMessageViewHolder.txt_designation_new_msg.setText(response.getCourse()+", "+response.getUniversity());
        }
        else
        {
            newMessageViewHolder.txt_designation_new_msg.setText(response.getDesignation()+"@ "+response.getCompany());
        }

        SelectedView(response,position);
        newMessageViewHolder.CheckUncheck_btn.setBackgroundResource(checkArray[position] ? R.mipmap.new_message_choose_people_selected : R.mipmap.new_message_choose_people_unselected);





        newMessageViewHolder.rel_new_msg_connections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(checkArray[position]){
                    checkArray[position]=false;
                    int a=(int)view.getTag();
                    Log.e("TAG", "check tag position:"+a);
                    // CancelSelectedView(view);
                    notifyItemChanged(position);
                }else if(!checkArray[position]) {
                    checkArray[position]=true;
                    Log.e("TAG", "check false: ");
                    UserSelectedView(response,position);
                    notifyItemChanged(position);


                }


            }
        });



    }

    private void CancelSelectedView(View view) {
        ViewGroup a=(ViewGroup)newMessageViewHolder.rel_new_msg_connections.getTag();
        selectedUser_view.removeView(a);

    }

    private void UserSelectedView(Connections response, int position ) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        addView = (ViewGroup) layoutInflater.inflate(R.layout.activity_add_user_inflectorview, null);
        TextView SelectedUser=(TextView)addView.findViewById(R.id.selected_user);
        ImageButton Selected_cancel=(ImageButton)addView.findViewById(R.id.selected_cancel);
        Selected_cancel.setTag(addView);
        SelectedUser.setText(response.getFullname());
        Selected_cancel.setOnClickListener(this);
        selectedUser_view.addView(addView);



    }
    private void SelectedView(Connections response, int position ) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        addV = (ViewGroup) layoutInflater.inflate(R.layout.activity_add_user_inflectorview, null);
        TextView SelectedUser=(TextView)addV.findViewById(R.id.selected_user);
        ImageButton Selected_cancel=(ImageButton)addV.findViewById(R.id.selected_cancel);
        Selected_cancel.setTag(addV);
        SelectedUser.setText(response.getFullname());
        Selected_cancel.setOnClickListener(this);
        newMessageViewHolder.rel_new_msg_connections.setTag(position);




    }

    private void configureLazyLoading(LazyLoadingHolder lazyloadingholder, int position)
    {
        lazyloadingholder.progressbar.setIndeterminate(true);
    }


    @Override
    public int getItemViewType(int position) {
        if(arr_connection_new_message.get(position) == null)
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


    @Override
    public void onClick(View view) {
       // int tagPosition=(int)view.getTag();
       // Log.e("TAG", "onClick: "+tagPosition );
        ViewGroup a=(ViewGroup) view.getTag();
        selectedUser_view.removeView(a);

       // ((LinearLayout)view.getParent()).removeView((LinearLayout) view.getParent());


       // CancelSelectedView(null,tagPosition);

    }
}
