package com.viintro.Viintro.Connections;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.TextFont;

/**
 * Created by hasai on 21/03/17.
 */
public class NoInvitationHolder extends RecyclerView.ViewHolder {
    TextView txt_no_invitations;
    Button btn_Manage_All;
    public NoInvitationHolder(View v, Context context) {
        super(v);
        txt_no_invitations = (TextView) v.findViewById(R.id.txt_no_invitations);
        txt_no_invitations.setTypeface(TextFont.opensans_bold(context));
        btn_Manage_All = (Button) v.findViewById(R.id.btn_Manage_All);
        btn_Manage_All.setTypeface(TextFont.opensans_bold(context));
    }
}
