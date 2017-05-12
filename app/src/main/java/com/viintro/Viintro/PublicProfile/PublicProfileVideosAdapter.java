package com.viintro.Viintro.PublicProfile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.viintro.Viintro.Landing.SampleVideoFullScreen;
import com.viintro.Viintro.Model.Videos;
import com.viintro.Viintro.MyProfile.ProfileVideosAdapter;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;

import java.util.ArrayList;

/**
 * Created by rkanawade on 27/03/17.
 */

public class PublicProfileVideosAdapter extends RecyclerView.Adapter<PublicProfileVideosAdapter.ViewHolder>{
    Context context;
    ArrayList<Videos> public_profile_video_list;
    ImageView img_intro_video_thumbnail;

    public PublicProfileVideosAdapter(Context context, ArrayList<Videos> public_profile_video_list) {
        super();
        this.context = context;
        this.public_profile_video_list = public_profile_video_list;
        this.img_intro_video_thumbnail = img_intro_video_thumbnail;


    }
    @Override
    public PublicProfileVideosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.public_profile_video_list, parent, false);
        PublicProfileVideosAdapter.ViewHolder viewHolder = new PublicProfileVideosAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PublicProfileVideosAdapter.ViewHolder holder, final int position) {

        Videos profile_videos = null;
        String no = String.valueOf(position + 1);
        try
        {
            profile_videos = public_profile_video_list.get(position);
            Glide.with(context).
                    load(Uri.parse(profile_videos.getThumbnail())).
                    into(holder.image_profile);


        }
        catch(Exception e)
        {

            Glide.with(context).
                    load(R.color.grey).
                    into(holder.image_profile);

        }


        final Videos finalProfile_videos = profile_videos;
        holder.imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(finalProfile_videos != null)
                {
                    Intent int_sample_videos = new Intent(context, SampleVideoFullScreen.class);
                    int_sample_videos.putExtra("Current url", finalProfile_videos.getVideo_mp4());
                    context.startActivity(int_sample_videos);
                }
                else
                {

                    CommonFunctions.displayToast(context,"There is no video to play");
                }

            }
        });



    }

    @Override
    public int getItemCount() {
        return 3;//profile_video_list.size();
    }

//    public Videos getItem(int position) {
//        return (Videos) profile_video_list.get(position);
//    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {

        public ImageView img_more,imgPlay,image_profile;

        public ViewHolder(View itemView) {
            super(itemView);
            imgPlay = (ImageView) itemView.findViewById(R.id.imgPlay);
            image_profile = (ImageView) itemView.findViewById(R.id.image_profile);

        }


    }

}
