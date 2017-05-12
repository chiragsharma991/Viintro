package com.viintro.Viintro.Connections;

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
public class ConnectionViewHolder extends RecyclerView.ViewHolder {

    RelativeLayout rel_connections;
    ImageView img_connections;
    TextView txt_connection_name, txt_designation;
    RelativeLayout btn_More;

    public ConnectionViewHolder(View v, Context context) {
        super(v);
        rel_connections = (RelativeLayout) v.findViewById(R.id.rel_connections);
        img_connections = (ImageView) v.findViewById(R.id.img_connections);
        txt_connection_name = (TextView) v.findViewById(R.id.txt_connection_name);
        txt_connection_name.setTypeface(TextFont.opensans_bold(context));
        txt_designation = (TextView) v.findViewById(R.id.txt_designation);
        txt_designation.setTypeface(TextFont.opensans_regular(context));
        btn_More = (RelativeLayout) v.findViewById(R.id.btn_More);
    }


}