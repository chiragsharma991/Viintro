package com.viintro.Viintro.Messages;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.TextFont;

/**
 * Created by hasai on 20/03/17.
 */
public class NewMessageViewHolder extends RecyclerView.ViewHolder {

    RelativeLayout rel_new_msg_connections;
    ImageView img_new_msg_connections;
    TextView txt_new_msg_connection_name, txt_designation_new_msg;
    Button CheckUncheck_btn;


    public NewMessageViewHolder(View v, Context context) {
        super(v);
        rel_new_msg_connections = (RelativeLayout) v.findViewById(R.id.rel_new_msg_connections);
        img_new_msg_connections = (ImageView) v.findViewById(R.id.img_new_msg_connections);
        txt_new_msg_connection_name = (TextView) v.findViewById(R.id.txt_new_msg_connection_name);
        txt_new_msg_connection_name.setTypeface(TextFont.opensans_bold(context));
        txt_designation_new_msg = (TextView) v.findViewById(R.id.txt_designation_new_msg);
        CheckUncheck_btn = (Button) v.findViewById(R.id.checkUncheck_btn);
        txt_designation_new_msg.setTypeface(TextFont.opensans_regular(context));
    }


}