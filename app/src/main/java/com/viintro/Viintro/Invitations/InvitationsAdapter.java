package com.viintro.Viintro.Invitations;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


/**
 * Created by hasai on 22/03/17.
 */
public class InvitationsAdapter extends FragmentStatePagerAdapter {


    int mNumOfTabs;

    public InvitationsAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                InvitationsReceivedFragment tab1 = new InvitationsReceivedFragment();
                return tab1;
            case 1:
                InvitationsSentFragment tab2 = new InvitationsSentFragment();
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




