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
public class ConnectionButtonHolder extends RecyclerView.ViewHolder {
    TextView btn_connections;
    public ConnectionButtonHolder(View v, Context context) {
        super(v);
        btn_connections = (TextView) v.findViewById(R.id.btn_connections);
        btn_connections.setTypeface(TextFont.opensans_bold(context));
    }
}
