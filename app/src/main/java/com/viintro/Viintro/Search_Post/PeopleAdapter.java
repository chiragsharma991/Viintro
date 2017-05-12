package com.viintro.Viintro.Search_Post;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.viintro.Viintro.Model.MySuggestions_Response;
import com.viintro.Viintro.Model.People;
import com.viintro.Viintro.PublicProfile.PublicProfileActivity;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.VideoRecord.VideoRecordingActivity;
import com.viintro.Viintro.Webservices.FollowAPI;
import com.viintro.Viintro.listener.OnLoadMoreListener;

import java.util.ArrayList;

import static com.viintro.Viintro.Search_Post.Search_Post_Activity.arr_count_people;
import static com.viintro.Viintro.Search_Post.Search_Post_Activity.listView_People;

/**
 * Created by hasai on 02/03/17.
 */
public class PeopleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList arr_people;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;


    public PeopleAdapter(Context context, ArrayList arr_people) {
        this.context = context;
        this.arr_people = arr_people;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) listView_People.getLayoutManager();
        listView_People.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

//                Log.e("isLoading"," "+isLoading);
//                Log.e("totalItemCount"," "+totalItemCount);
//                Log.e("lastVisibleItem"," "+(lastVisibleItem));
                Log.e("arr_count_people"," "+arr_count_people);

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && arr_count_people == 20) {
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
        return arr_people.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case 0:
                View v0 = inflater.inflate(R.layout.adapter_people, parent, false);
                viewHolder = new PeopleViewHolder(v0, context);
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
                PeopleViewHolder peopleviewholder = (PeopleViewHolder) holder;
                configurepeopleviewholder(peopleviewholder, position);
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

    private void configurepeopleviewholder(PeopleViewHolder holder, final int position)
    {
        PeopleViewHolder peopleviewholder = (PeopleViewHolder) holder;
        final People people = (People) arr_people.get(position);
        if (people.getDisplay_pic() != null)
        {
            Glide.with(context).load(Uri.parse(people.getDisplay_pic())).centerCrop().into(peopleviewholder.img_people);
        }
        else
        {
            Glide.with(context).load(R.drawable.ic_launcher).centerCrop().into(peopleviewholder.img_people);
        }

        peopleviewholder.txt_peoplename.setText(people.getFullname());

        if(people.getUser_type() != null)
        {
            if(people.getUser_type().equals("student"))
            {
                peopleviewholder.txt_designation.setText(people.getCourse()+", "+people.getUniversity());
            }
            else
            {
                peopleviewholder.txt_designation.setText(people.getDesignation()+"@ "+people.getCompany());
            }
        }

        peopleviewholder.inner_rel_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int_publicprofile = new Intent(context, PublicProfileActivity.class);
                int_publicprofile.putExtra("user_id", people.getId());
                int_publicprofile.putExtra("from_Activity","Search_People");
                int_publicprofile.putExtra("position",position);
                context.startActivity(int_publicprofile);
            }
        });

    }


    @Override
    public int getItemViewType(int position) {

        if(arr_people.get(position) == null)
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



    private class PeopleViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout inner_rel_people;
        TextView txt_peoplename, txt_designation, txt_place;
        ImageView img_people;

        public PeopleViewHolder(View v1, Context context) {
            super(v1);
            inner_rel_people = (RelativeLayout)v1.findViewById(R.id.inner_rel_people);
            txt_peoplename = (TextView)v1.findViewById(R.id.txt_people_name);
            txt_designation = (TextView)v1.findViewById(R.id.txt_designation);
            txt_place = (TextView)v1.findViewById(R.id.txt_place);
            img_people = (ImageView)v1.findViewById(R.id.img_people);
        }
    }
}
