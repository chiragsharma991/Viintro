package com.viintro.Viintro.Invitations;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.TextFont;

import java.util.ArrayList;

/**
 * Created by hasai on 22/03/17.
 */

public class InvitationsActivity extends AppCompatActivity
{

    private Context context;
    TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_invitations_tabs);
        context = this;

        TextView txt_header = (TextView) findViewById(R.id.header);
        txt_header.setText("Invitations");
        txt_header.setTypeface(TextFont.opensans_bold(context));

        RelativeLayout img_back = (RelativeLayout) findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Received"));
        tabLayout.addTab(tabLayout.newTab().setText("Sent"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(R.color.tab_txt_selection);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final InvitationsAdapter adapter = new InvitationsAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tabLayout.setBackgroundResource(R.drawable.tab_background_selected);
                //  tab.setTextColor(Color.parseColor("#FFFFFF"));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }





}
