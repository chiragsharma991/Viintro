package com.viintro.Viintro.Landing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.viintro.Viintro.R;

import java.util.ArrayList;

/**
 * Created by rkanawade on 25/01/17.
 */

public class GridviewAdapter extends BaseAdapter
{
    LayoutInflater inflater;
    Context context;
    ArrayList myList = new ArrayList();

    public GridviewAdapter(Context context, ArrayList myList) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        this.myList = myList;
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public SampleVideosData getItem(int position) {
        return (SampleVideosData) myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.gridview_contents, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        final SampleVideosData currentListData = getItem(position);
        mViewHolder.textSampleVideo.setText(currentListData.getNames());

//        MediaController mc = new MediaController(context); //
//        mc.setAnchorView(mViewHolder.videoView);  //
//        mc.setMediaPlayer(mViewHolder.videoView);//
//        mViewHolder.videoView.setMediaController(mc);//

        try {
            // Start the MediaController
//            MediaController mediacontroller = new MediaController(context);
//            mediacontroller.setAnchorView(mViewHolder.videoView);
//
//            Uri videoUri = Uri.parse(currentListData.getUrl());
//            mViewHolder.videoView.setMediaController(mediacontroller);
            // mViewHolder.videoView.setVideoURI(videoUri);
            //    Bitmap bm = ThumbnailUtils.createVideoThumbnail("android.resource://com.aperotechnologies.testvideo/" + R.raw.big_buck_bunny_144p_2mb, MediaStore.Video.Thumbnails.MICRO_KIND);
            //    BitmapDrawable bitmapDrawable = new BitmapDrawable(bm);
           // mViewHolder.videoView.setVideoURI(videoUri);
//            Bitmap bm = ThumbnailUtils.createVideoThumbnail("android.resource://com.aperotechnologies.testvideo/" + R.raw.big_buck_bunny_144p_2mb, MediaStore.Video.Thumbnails.MICRO_KIND);
//            BitmapDrawable bitmapDrawable = new BitmapDrawable(bm);
//            Log.d("log","outside onclick");
//            mViewHolder.videoView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.d("log","inside onclick");
//                    Intent fullScreen = new Intent(context, SampleVideoFullScreen.class);
//                    fullScreen.putExtra("Current url",currentListData.getUrl());
//                    view.getContext().startActivity(fullScreen);
//                }
//            });

//            mViewHolder.videoView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    Log.d("log","inside onclick");
//
//                    return true;
//                }
//            });


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent fullScreen = new Intent(context, SampleVideoFullScreen.class);
                    fullScreen.putExtra("Current url",currentListData.getUrl());
                    context.startActivity(fullScreen);
                }
            });

        } catch (Exception e) {

            e.printStackTrace();
        }

//        mViewHolder.videoView.requestFocus();
//        mViewHolder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            // Close the progress bar and play the video
//            public void onPrepared(MediaPlayer mp) {
//                //pDialog.dismiss();
//                mViewHolder.videoView.start();
//            }
//        });


        return convertView;
    }

    private class MyViewHolder {
        TextView textSampleVideo;

        public MyViewHolder(View item)
        {
            textSampleVideo = (TextView) item.findViewById(R.id.textSampleVideo);
        }
    }


}

