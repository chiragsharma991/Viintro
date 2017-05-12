package com.viintro.Viintro.HomeFeed;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.GetPost_Response;
import com.viintro.Viintro.MyProfile.MyProfileFragment;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.VideoRecord.VideoRecordingActivity;
import com.viintro.Viintro.ViintroApplication;
import com.viintro.Viintro.Webservices.AccessTokenAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.viintro.Viintro.HomeFeed.HomeActivity.img_settings;
import static com.viintro.Viintro.HomeFeed.HomeActivity.rel_search;

/**
 * Created by hasai on 23/02/17.
 */

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Context context;
    private Configuration_Parameter m_config;
    private SharedPreferences sharedPreferences;
    private String bearertoken;
    private View v;
    SwipeRefreshLayout swipeRefreshLayout_Home;
    RecyclerView listview_Home;
    //HomeAdapter adapter_home;
    int index_load_more = 0;

    public static HomeFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(args);
        return homeFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        bearertoken = sharedPreferences.getString(m_config.login_access_token, "");
        index_load_more = 1;
        v = getView();
        initialiseUI();

        swipeRefreshLayout_Home.setOnRefreshListener(this);

    }

    private void initialiseUI()
    {
        swipeRefreshLayout_Home = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout_Home);
        listview_Home = (RecyclerView) v.findViewById(R.id.listview_Home);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int_video_record = new Intent(context, VideoRecordingActivity.class);
                int_video_record.putExtra("from","thought");
                context.startActivity(int_video_record);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        rel_search.setVisibility(View.VISIBLE);
        img_settings.setVisibility(View.GONE);
        return view;
    }


    @Override
    public void onRefresh()
    {
        swipeRefreshLayout_Home.setRefreshing(false);
    }
}
