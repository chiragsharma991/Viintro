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
public class SeeAllHolder extends RecyclerView.ViewHolder {

    TextView txt_see_All;
    public SeeAllHolder(View v4, Context context) {
        super(v4);
        txt_see_All = (TextView) v4.findViewById(R.id.txt_see_All);
        txt_see_All.setTypeface(TextFont.opensans_semibold(context));
    }
}
