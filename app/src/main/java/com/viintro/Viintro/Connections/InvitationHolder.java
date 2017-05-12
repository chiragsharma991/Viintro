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
public class InvitationHolder extends RecyclerView.ViewHolder {
    public RelativeLayout rel_invitations_main;
    public RelativeLayout rel_img_thumbnail_connect_video;
    public ImageView img_invitation_avatar;
    public ImageView img_thumbnail_connect_video;
    public TextView txt_invitation_name;
    public TextView txt_designation;
    public TextView txt_message;
    public Button btn_Remove;
    public Button btn_Connect;
    public Button btn_More;

    public InvitationHolder(View v, Context context) {
        super(v);
        rel_invitations_main = (RelativeLayout) v.findViewById(R.id.rel_invitations_main);
        rel_img_thumbnail_connect_video = (RelativeLayout) v.findViewById(R.id.rel_img_thumbnail_connect_video);
        img_invitation_avatar = (ImageView) v.findViewById(R.id.img_invitation_avatar);
        txt_invitation_name = (TextView) v.findViewById(R.id.txt_invitation_name);
        txt_invitation_name.setTypeface(TextFont.opensans_bold(context));
        txt_designation = (TextView) v.findViewById(R.id.txt_designation);
        txt_designation.setTypeface(TextFont.opensans_regular(context));
        btn_Remove = (Button) v.findViewById(R.id.btn_Remove);
        btn_Remove.setTypeface(TextFont.opensans_semibold(context));
        btn_Connect = (Button) v.findViewById(R.id.btn_Connect);
        btn_Connect.setTypeface(TextFont.opensans_semibold(context));
        txt_message = (TextView) v.findViewById(R.id.txt_message);
        img_thumbnail_connect_video = (ImageView) v.findViewById(R.id.img_thumbnail_connect_video);
        btn_More = (Button) v.findViewById(R.id.btn_More);
    }
}
