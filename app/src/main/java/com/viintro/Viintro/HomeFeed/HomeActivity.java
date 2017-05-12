package com.viintro.Viintro.HomeFeed;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
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
import com.viintro.Viintro.Search_Post.Search_Post_Activity;


public class HomeActivity extends AppCompatActivity {
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
    Boolean flag_search = false;
    public static ImageView img_settings;


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
        img_settings = (ImageView) findViewById(R.id.img_settings);

        RelativeLayout rel_search_home = (RelativeLayout) findViewById(R.id.rel_search_home);
        rel_search_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Fragment search_post_fragment = new Search_Post_Fragment();
//                FragmentManager fmsearch_post_fragment = getSupportFragmentManager();
//                FragmentTransaction transactionsearch_post_fragment = fmsearch_post_fragment.beginTransaction();
//                transactionsearch_post_fragment.replace(R.id.container, search_post_fragment);
//                transactionsearch_post_fragment.addToBackStack(null);
//                transactionsearch_post_fragment.commit();

                Intent i = new Intent(context, Search_Post_Activity.class);
                startActivity(i);
            }
        });


        //FragNav
        //list of fragments
       /* List<Fragment> fragments = new ArrayList<>(5);

        //add fragments to list
        fragments.add(HomeFragment.newInstance(0));
        fragments.add(ConnectionFragment.newInstance(0));
        fragments.add(MessagesFragment.newInstance(0));
        fragments.add(MyProfileFragment.newInstance(0));
        fragments.add(NotificationFragment.newInstance(0));

        //link fragments to container
        fragNavController = new FragNavController(getSupportFragmentManager(),R.id.container,fragments);
        //End of FragNav
*/

        Fragment fragment = new HomeFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();

        //BottomBar menu
        mBottomBar = (BottomBar)findViewById(R.id.bottomBar);

        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_home:
                        //fragNavController.switchTab(TAB_FIRST);
//
                        Fragment fragment1 = new HomeFragment();
                        FragmentManager fm1 = getSupportFragmentManager();
                        FragmentTransaction transaction1 = fm1.beginTransaction();
                        transaction1.replace(R.id.container, fragment1);
                        //transaction1.addToBackStack(null);
                        transaction1.commit();
                        break;
                    case R.id.tab_connections:
                        //fragNavController.switchTab(TAB_SECOND);
//
                        Fragment fragment2 = new ConnectionFragment();
                        FragmentManager fm2 = getSupportFragmentManager();
                        FragmentTransaction transaction2 = fm2.beginTransaction();
                        transaction2.replace(R.id.container, fragment2);
                        //transaction2.addToBackStack(null);
                        transaction2.commit();
                        break;
                    case R.id.tab_messages:
                        //fragNavController.switchTab(TAB_THIRD);
//
                        Fragment fragment3 = new MessagesFragment();
                        FragmentManager fm3 = getSupportFragmentManager();
                        FragmentTransaction transaction3 = fm3.beginTransaction();
                        transaction3.replace(R.id.container, fragment3);
                        //transaction3.addToBackStack(null);
                        transaction3.commit();
                        break;
                    case R.id.tab_myprofile:
                        //fragNavController.switchTab(TAB_FOURTH);
//                        if (getFragmentManager().getBackStackEntryCount() > 0) {
//                            getFragmentManager().popBackStack();
//                        }

                        Fragment fragment4 = new MyProfileFragment();
                        FragmentManager fm4 = getSupportFragmentManager();
                        FragmentTransaction transaction4 = fm4.beginTransaction();
                        transaction4.replace(R.id.container, fragment4);
                        //transaction4.addToBackStack(null);
                        transaction4.commit();
                        break;
                    case R.id.tab_notification:
                        //fragNavController.switchTab(TAB_FIFTH);

                        Fragment fragment5 = new NotificationFragment();
                        FragmentManager fm5 = getSupportFragmentManager();
                        FragmentTransaction transaction5 = fm5.beginTransaction();
                        transaction5.replace(R.id.container, fragment5);
                        //transaction5.addToBackStack(null);
                        transaction5.commit();
                        break;
                }
            }
        });

        mBottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
//                if (tabId == R.id.tab_home) {
//                    fragNavController.clearStack();
//                }
            }
        });

        //End of BottomBar menu

    }
    @Override
    public void onBackPressed()
    {
        Log.e("fm"," "+getFragmentManager().getBackStackEntryCount() +" "+mBottomBar.getCurrentTabPosition());

        if(mBottomBar.getCurrentTabPosition() == 0)
        {
            //super.onBackPressed();
            finish();
        }
        else
        {
            mBottomBar.selectTabAtPosition(0);
        }

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        // mBottomBar.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }
}