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
 * Created by hasai on 21/03/17.
 */
public class SuggestionsViewHolder extends RecyclerView.ViewHolder {

    RelativeLayout rel_suggestions_main;
    ImageView img_suggestions;
    TextView txt_suggestions_name,txt_designation;
    Button btn_Connect_suggestions, btn_Follow_suggestions;

    public SuggestionsViewHolder(View v, Context context) {
        super(v);
        rel_suggestions_main = (RelativeLayout) v.findViewById(R.id.rel_suggestions_main);
        img_suggestions = (ImageView) v.findViewById(R.id.img_suggestions);
        txt_suggestions_name = (TextView) v.findViewById(R.id.txt_suggestions_name);
        txt_suggestions_name.setTypeface(TextFont.opensans_semibold(context));
        txt_designation = (TextView) v.findViewById(R.id.txt_designation);
        txt_designation.setTypeface(TextFont.opensans_regular(context));
        btn_Connect_suggestions = (Button) v.findViewById(R.id.btn_Connect_suggestions);
        btn_Connect_suggestions.setTypeface(TextFont.opensans_semibold(context));
        btn_Follow_suggestions = (Button) v.findViewById(R.id.btn_Follow_suggestions);
        btn_Follow_suggestions.setTypeface(TextFont.opensans_semibold(context));
    }


}