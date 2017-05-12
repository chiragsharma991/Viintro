package com.viintro.Viintro.HomeFeed;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viintro.Viintro.R;

import org.json.JSONException;
import org.json.JSONObject;

public class InternalShareActivity extends AppCompatActivity
{
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.share_tab_layout);

        TextView txt_Header = (TextView) findViewById(R.id.header);
        txt_Header.setText("Share");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(R.color.orange);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Share"));
        tabLayout.addTab(tabLayout.newTab().setText("Message"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        JSONObject postObject = new JSONObject();
        if(getIntent() != null)
        {
            try
            {
                postObject.put("post_id",getIntent().getExtras().getString("post_id"));
                Log.d("post_id",""+getIntent().getExtras().getString("post_id"));
                postObject.put("post_slug",getIntent().getExtras().getString("post_slug"));
                Log.d("post_slug",""+getIntent().getExtras().getString("post_slug"));

                postObject.put("post_owner_id",getIntent().getExtras().getInt("post_owner_id"));
                Log.d("post_owner_id",""+getIntent().getExtras().getInt("post_owner_id"));

                postObject.put("post_owner_name",getIntent().getExtras().getString("post_owner_name"));
                Log.d("post_owner_name",""+getIntent().getExtras().getString("post_owner_name"));

                postObject.put("post_description",getIntent().getExtras().getString("post_description"));
                Log.d("post_description",getIntent().getExtras().getString("post_description"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final InternalShareAdapter adapter = new InternalShareAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), postObject);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
