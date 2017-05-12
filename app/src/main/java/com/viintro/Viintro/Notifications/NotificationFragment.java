package com.viintro.Viintro.Notifications;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viintro.Viintro.R;

import static com.viintro.Viintro.HomeFeed.HomeActivity.img_settings;
import static com.viintro.Viintro.HomeFeed.HomeActivity.rel_search;

/**
 * Created by hasai on 11/01/17.
 */
public class NotificationFragment extends Fragment {

    public static NotificationFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        NotificationFragment notificationFragment = new NotificationFragment();
        notificationFragment.setArguments(args);
        return notificationFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rel_search.setVisibility(View.VISIBLE);
        img_settings.setVisibility(View.GONE);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}