package com.viintro.Viintro.HomeFeed;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.viintro.Viintro.Model.Followers;
import com.viintro.Viintro.MyProfile.FollowersHolder;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Search_Post.LazyLoadingHolder;
import com.viintro.Viintro.listener.OnLoadMoreListener;

import java.util.ArrayList;

import static com.viintro.Viintro.HomeFeed.LikesActivity.arr_count_mylikes;
import static com.viintro.Viintro.MyProfile.MyFollowersActivity.arr_count_myfollowers;

/**
 * Created by rkanawade on 26/04/17.
 */

public class LikesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<Followers> arr_likes;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    RecyclerView recycler_view_likes;
    private TextView txt_no_likes;
    public LikesAdapter(){
        this.context = context;

    }


    public LikesAdapter(Context context, ArrayList<Followers> arr_likes, RecyclerView recycler_view_likes, TextView txt_no_likes) {
        this.context = context;
        this.arr_likes = arr_likes;
        this.recycler_view_likes = recycler_view_likes;
        this.txt_no_likes = txt_no_likes;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recycler_view_likes.getLayoutManager();
        recycler_view_likes.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

//                Log.e("isLoading"," "+isLoading);
//                Log.e("totalItemCount"," "+totalItemCount);
//                Log.e("lastVisibleItem"," "+(lastVisibleItem));

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && arr_count_mylikes == 20) {
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                View v0 = inflater.inflate(R.layout.adapter_likes, parent, false);
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                LikesHolder likesHolder = (LikesHolder) holder;
                //configurefollowersHolder(likesHolder, position);
                break;
            case 1:
                LazyLoadingHolder lazyloadingholder = (LazyLoadingHolder) holder;
                configureLazyLoading(lazyloadingholder, position);
                break;
        }
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    private void configureLazyLoading(LazyLoadingHolder lazyloadingholder, int position)
    {
        lazyloadingholder.progressbar.setIndeterminate(true);
    }

    @Override
    public int getItemViewType(int position) {
        if(arr_likes.get(position) == null)
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


    public class LikesHolder extends RecyclerView.ViewHolder{
        ImageView img_likes;
        TextView txt_likes_name, txt_designation;

        public LikesHolder(View v, Context context) {
            super(v);
            img_likes = (ImageView) v.findViewById(R.id.img_likes);
            txt_likes_name = (TextView) v.findViewById(R.id.txt_likes_name);
            txt_designation = (TextView) v.findViewById(R.id.txt_designation);


        }
    }
}
