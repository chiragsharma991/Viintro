package com.viintro.Viintro.Connections;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.TextFont;

/**
 * Created by hasai on 13/04/17.
 */
public class ConnectionHeaderHolder extends RecyclerView.ViewHolder
{
    TextView txt_header_connections;
    public ConnectionHeaderHolder(View v1, Context context)
    {
        super(v1);
        txt_header_connections = (TextView) v1.findViewById(R.id.txt_header_connections);
        txt_header_connections.setTypeface(TextFont.opensans_bold(context));
    }
}
