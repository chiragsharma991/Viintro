package com.viintro.Viintro.MyProfile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.viintro.Viintro.Landing.GridviewAdapter;
import com.viintro.Viintro.Landing.SampleVideoFullScreen;
import com.viintro.Viintro.Landing.SampleVideosData;
import com.viintro.Viintro.Model.GetPost_Response;
import com.viintro.Viintro.Model.User;
import com.viintro.Viintro.Model.Video;
import com.viintro.Viintro.R;

import java.util.ArrayList;

/**
 * Created by rkanawade on 10/03/17.
 */

public class LiveVideosAdapter extends BaseAdapter{

    LayoutInflater inflater;
    Context context;
    ArrayList<GetPost_Response> arr_livevideos;

    public LiveVideosAdapter(Context context, ArrayList<GetPost_Response> arr_livevideos) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        this.arr_livevideos = arr_livevideos;

    }

    @Override
    public int getCount() {
        return arr_livevideos.size();
    }

    @Override
    public GetPost_Response getItem(int position) {
        return arr_livevideos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final LiveVideosAdapter.MyViewHolder mViewHolder;

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.live_videos_items, parent, false);
            mViewHolder = new LiveVideosAdapter.MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else
        {
            mViewHolder = (LiveVideosAdapter.MyViewHolder) convertView.getTag();
        }


        GetPost_Response getPost_response = arr_livevideos.get(position);
        User user = getPost_response.getUser();
        mViewHolder.text_videoname.setText(user.getFullname());
        Video video = getPost_response.getVideo();
        mViewHolder.text_views.setText(String.valueOf(getPost_response.getView_count()) +" Views");

        Glide.with(context).load(Uri.parse(video.getThumbnail())).centerCrop().into(mViewHolder.img_video_thumbnail);

//        final LiveVideosData currentListData = getItem(position);
//        mViewHolder.text_videoname.setText(currentListData.getNames());
//        mViewHolder.text_views.setText(currentListData.getViews());
     //   mViewHolder.img_delete.setImageBitmap(currentListData.getImages());





//
        return convertView;
    }

    private class MyViewHolder {
        TextView text_videoname, text_views;
        ImageView img_delete, img_video_thumbnail;

        public MyViewHolder(View item)
        {
            text_videoname = (TextView) item.findViewById(R.id.text_videoname);
            text_views = (TextView) item.findViewById(R.id.text_views);
            img_delete = (ImageView) item.findViewById(R.id.img_delete);
            img_video_thumbnail = (ImageView) item.findViewById(R.id.img_video_thumbnail);

        }
    }
}
