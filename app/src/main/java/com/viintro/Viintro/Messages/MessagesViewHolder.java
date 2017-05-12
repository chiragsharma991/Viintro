package com.viintro.Viintro.Messages;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.TextFont;

/**
 * Created by hasai on 17/04/17.
 */
public class MessagesViewHolder extends RecyclerView.ViewHolder {

    RelativeLayout rel_messages;
    ImageView img_messages;
    TextView txt_person_name, txt_last_message, txt_message_time;
    public MessagesViewHolder(View v, Context context)
    {
        super(v);
        rel_messages = (RelativeLayout) v.findViewById(R.id.rel_messages);
        img_messages = (ImageView)v.findViewById(R.id.img_messages);
        txt_person_name = (TextView)v.findViewById(R.id.txt_person_name);
        txt_person_name.setTypeface(TextFont.opensans_bold(context));
        txt_last_message = (TextView)v.findViewById(R.id.txt_last_message);
        txt_message_time = (TextView)v.findViewById(R.id.txt_message_time);
        txt_message_time.setTypeface(TextFont.opensans_regular(context));
    }
}
