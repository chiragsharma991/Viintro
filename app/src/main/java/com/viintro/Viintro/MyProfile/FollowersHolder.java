package com.viintro.Viintro.MyProfile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.viintro.Viintro.R;

/**
 * Created by rkanawade on 23/03/17.
 */

public class FollowersHolder extends RecyclerView.ViewHolder {

    ImageView img_followers;
    TextView txt_followers_name, txt_designation;
    Button btn_More, btn_Connect_followers,btn_Follow_followers;

    public FollowersHolder(View v, Context context) {
        super(v);
        img_followers = (ImageView) v.findViewById(R.id.img_followers);
        txt_followers_name = (TextView) v.findViewById(R.id.txt_followers_name);
        txt_designation = (TextView) v.findViewById(R.id.txt_designation);
        btn_More = (Button) v.findViewById(R.id.btn_More);
        btn_Connect_followers = (Button) v.findViewById(R.id.btn_Connect_followers);
        btn_Follow_followers = (Button) v.findViewById(R.id.btn_Follow_followers);

    }


}