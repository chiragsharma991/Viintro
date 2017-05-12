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

public class FollowingsHolder extends RecyclerView.ViewHolder{

    ImageView img_followings;
    TextView txt_followings_name, txt_designation_followings;
    Button btn_More, btn_Connect_followings,btn_Follow_followings;

    public FollowingsHolder(View v, Context context) {
        super(v);
        img_followings = (ImageView) v.findViewById(R.id.img_followings);
        txt_followings_name = (TextView) v.findViewById(R.id.txt_followings_name);
        txt_designation_followings = (TextView) v.findViewById(R.id.txt_designation_followings);
        btn_More = (Button) v.findViewById(R.id.btn_More);
        btn_Connect_followings = (Button) v.findViewById(R.id.btn_Connect_followings);
        btn_Follow_followings = (Button) v.findViewById(R.id.btn_Follow_followings);

    }

}
