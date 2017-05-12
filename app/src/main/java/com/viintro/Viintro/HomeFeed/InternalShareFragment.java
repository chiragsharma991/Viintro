package com.viintro.Viintro.HomeFeed;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viintro.Viintro.R;

/**
 * Created by rkanawade on 11/04/17.
 */

public class InternalShareFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_internal_share, container, false);
    }
}
