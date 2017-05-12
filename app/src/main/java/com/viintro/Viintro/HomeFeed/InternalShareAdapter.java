package com.viintro.Viintro.HomeFeed;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.json.JSONObject;

/**
 * Created by rkanawade on 11/04/17.
 */

public class InternalShareAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    JSONObject postObject;

    public InternalShareAdapter(FragmentManager fm, int NumOfTabs, JSONObject postObject) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.postObject = postObject;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                InternalShareFragment tab1 = new InternalShareFragment();
                return tab1;
            case 1:
                InternalMessageFragment tab2 = new InternalMessageFragment(postObject);
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
