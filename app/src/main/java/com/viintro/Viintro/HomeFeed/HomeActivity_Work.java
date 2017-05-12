package com.viintro.Viintro.HomeFeed;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Window;
import android.widget.RelativeLayout;

import com.ncapdevi.fragnav.FragNavController;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import com.viintro.Viintro.Connections.ConnectionFragment;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Messages.MessagesFragment;
import com.viintro.Viintro.MyProfile.MyProfileFragment;
import com.viintro.Viintro.Notifications.NotificationFragment;
import com.viintro.Viintro.R;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity_Work extends AppCompatActivity {
    private BottomBar mBottomBar;
    private FragNavController fragNavController;

    //indices to fragments
    private final int TAB_FIRST = FragNavController.TAB1;
    private final int TAB_SECOND = FragNavController.TAB2;
    private final int TAB_THIRD = FragNavController.TAB3;
    private final int TAB_FOURTH = FragNavController.TAB4;
    private final int TAB_FIFTH = FragNavController.TAB5;

    Context context;
    Configuration_Parameter m_config;
    public static RelativeLayout rel_search;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);
        context = this;
        m_config = Configuration_Parameter.getInstance();
        rel_search = (RelativeLayout) findViewById(R.id.rel_search);
        //searchView = (SearchView) findViewById(R.id.edt_search);


        // perform set on query text listener event
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // do something on text submit
//                Log.e("text submit"," ");
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // do something when text changes
//                Log.e("text changes"," ");
//                return false;
//            }
//        });


        //FragNav
        //list of fragments
        List<Fragment> fragments = new ArrayList<>(5);

        //add fragments to list
        fragments.add(HomeFragment.newInstance(0));
        fragments.add(ConnectionFragment.newInstance(0));
        fragments.add(MessagesFragment.newInstance(0));
        fragments.add(MyProfileFragment.newInstance(0));
        fragments.add(NotificationFragment.newInstance(0));

        //link fragments to container
        fragNavController = new FragNavController(getSupportFragmentManager(),R.id.container,fragments);
        //End of FragNav

        //BottomBar menu
        mBottomBar = (BottomBar)findViewById(R.id.bottomBar);

        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_home:
                        fragNavController.switchTab(TAB_FIRST);
                        break;
                    case R.id.tab_connections:
                        fragNavController.switchTab(TAB_SECOND);
                        break;
                    case R.id.tab_messages:
                        fragNavController.switchTab(TAB_THIRD);
                        break;
                    case R.id.tab_myprofile:
                        fragNavController.switchTab(TAB_FOURTH);
                        break;
                    case R.id.tab_notification:
                        fragNavController.switchTab(TAB_FIFTH);
                        break;
                }
            }
        });

        mBottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_home) {
                    fragNavController.clearStack();
                }
            }
        });

        //End of BottomBar menu


    }
    @Override
    public void onBackPressed() {
        if (fragNavController.getCurrentStack().size() > 1) {
            fragNavController.pop();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        // mBottomBar.onSaveInstanceState(outState);


    }
}