package com.viintro.Viintro.MyProfile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.Followers;
import com.viintro.Viintro.Model.Following;
import com.viintro.Viintro.Model.Profile_Views;
import com.viintro.Viintro.Model.WhoViewedProfile_Response;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;
import com.viintro.Viintro.Webservices.AccessTokenAPI;
import com.viintro.Viintro.listener.OnLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hasai on 22/03/17.
 */

public class WhoViewedProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerView_whoviewedprofile;
    public static  WhoViewedProfileAdapter whoViewedProfileAdapter;
    private Context context;
    private String bearertoken;
    private Configuration_Parameter m_config;
    private SharedPreferences sharedPreferences;
    public static ArrayList arr_whoviewedprofile;
    private TextView txt_no_internet_connection;
    private TextView txt_no_views;
    private int index = 0;
    public static  int arr_count_whoviewedprofile = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_who_viewed_your_profile);
        context = this;
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        bearertoken = sharedPreferences.getString(m_config.login_access_token,"");
        arr_whoviewedprofile = new ArrayList();
        arr_count_whoviewedprofile = 0;
        index = 0;
        TextView txt_Header = (TextView) findViewById(R.id.header);
        txt_Header.setText("Who's viewed your profile");
        txt_no_internet_connection = (TextView) findViewById(R.id.txt_no_internet_connection);

        CommonFunctions.sDialog(context,"Loading...");
        initialiseUI();
        if(!CommonFunctions.chkStatus(context))
        {
            CommonFunctions.hDialog();
            txt_no_internet_connection.setVisibility(View.VISIBLE);
            return;
        }
        req_who_viewed_profile_API(context);
        setListAdapter();

        whoViewedProfileAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("suggestion", "Load More");
                arr_whoviewedprofile.add(null);
                whoViewedProfileAdapter.notifyItemInserted(arr_whoviewedprofile.size() - 1);

                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //Remove loading item
                        arr_whoviewedprofile.remove(arr_whoviewedprofile.size() - 1);
                        whoViewedProfileAdapter.notifyItemRemoved(arr_whoviewedprofile.size());

                        index = index + 1;
                        req_who_viewed_profile_API(context);

                        //whoViewedProfileAdapter.notifyDataSetChanged();
                        whoViewedProfileAdapter.setLoaded();
                    }
                }, 5000);
            }
        });

    }


    private void initialiseUI()
    {
        recyclerView_whoviewedprofile = (RecyclerView) findViewById(R.id.recycler_view_whoviewedprofile);
        txt_no_views = (TextView) findViewById(R.id.txt_no_views);

    }

    private void setListAdapter() {

        if(arr_whoviewedprofile.size() == 0)
        {
            recyclerView_whoviewedprofile.setLayoutManager(new LinearLayoutManager(context));
            whoViewedProfileAdapter = new WhoViewedProfileAdapter(context, arr_whoviewedprofile, recyclerView_whoviewedprofile);
            recyclerView_whoviewedprofile.setAdapter(whoViewedProfileAdapter);
        }
        else
        {
            whoViewedProfileAdapter.notifyDataSetChanged();
        }

    }

    private void req_who_viewed_profile_API(final Context context)
    {

        final Gson gson = new Gson();
        String url =  m_config.web_api_link+ "/my/profile-views/?page="+index;

        final JsonObjectRequest jsonarrayreq = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("", "req_who_viewed_profile_API : " + " " + response);

                        WhoViewedProfile_Response whoViewedProfile_response = gson.fromJson(response.toString(),WhoViewedProfile_Response.class);

                        if(whoViewedProfile_response.getProfile_views() == null && index == 0)
                        {
                            CommonFunctions.hDialog();
                            txt_no_views.setVisibility(View.VISIBLE);
                            txt_no_internet_connection.setVisibility(View.GONE);
                            return;
                        }
                        else
                        {
                            if(whoViewedProfile_response.getProfile_views() != null)
                            {
                                ArrayList<Profile_Views> profile_views = whoViewedProfile_response.getProfile_views();
                                arr_count_whoviewedprofile = profile_views.size();
                                for(int i = 0; i< profile_views.size(); i++)
                                {
                                    arr_whoviewedprofile.add(profile_views.get(i));
                                }
                                setListAdapter();
                                txt_no_internet_connection.setVisibility(View.GONE);
                                CommonFunctions.hDialog();
                            }
                            else
                            {
                                arr_count_whoviewedprofile = 0;
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        CommonFunctions.hDialog();
                        txt_no_internet_connection.setVisibility(View.GONE);

                        if(error.networkResponse != null)
                        {

                            String networkresponse_data = new String(error.networkResponse.data);
                            if(error.networkResponse.statusCode == 500)
                            {
                                CommonFunctions.displayToast(context,context.getResources().getString(R.string.internal_server_error));
                            }
                            else {
                                if (networkresponse_data != null) {
                                    JSONObject network_obj;
                                    try {
                                        network_obj = new JSONObject(networkresponse_data);

                                        if (network_obj.getInt("code") == 12) {
                                            // Alert Dialog for access token expired
                                            CommonFunctions.alertdialog_token(context, "accesstokenexpired");
                                        } else if (network_obj.getInt("code") == 16) {
                                            // Alert Dialog for refresh token expired
                                            CommonFunctions.alertdialog_token(context, "refreshtokenexpired");
                                        } else {
                                            CommonFunctions.displayToast(context, network_obj.getString("message"));
                                        }


                                    } catch (JSONException e) {

                                    }
                                }

                            }
                        } else {

                        }

                    }
                }

        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("version", Constcore.version);
                params.put("devicetype","android");
                params.put("timezone",CommonFunctions.getTimeZoneIST());
                params.put("Authorization","Bearer "+bearertoken);
                params.put("timesstamp",CommonFunctions.getTimeStamp());
                params.put("os_version",m_config.os_version);
                return params;
            }
        };
        int socketTimeout = 60000;//5 seconds

        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonarrayreq.setRetryPolicy(policy);
        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(jsonarrayreq, "who_viewed_api");


    }

    public void onRowUpdate(int position, String condition)
    {
        if(condition.equals("connect"))
        {
            //Update row after sending request for connection
            Profile_Views profile_views = (Profile_Views) arr_whoviewedprofile.get(position);
            profile_views.setConnection_requested(true);
            arr_whoviewedprofile.set(position, profile_views);
            whoViewedProfileAdapter.notifyDataSetChanged();
        }
        else if(condition.equals("follow"))
        {
            // Update row after after api success to "Following"
            Profile_Views profile_views = (Profile_Views) arr_whoviewedprofile.get(position);
            profile_views.setFollowing(true);
            arr_whoviewedprofile.set(position, profile_views);
            whoViewedProfileAdapter.notifyDataSetChanged();
        }
        else if(condition.equals("unfollow"))
        {
            // Update row after after api success to "UnFollow"
            Profile_Views profile_views = (Profile_Views) arr_whoviewedprofile.get(position);
            profile_views.setFollowing(false);
            arr_whoviewedprofile.set(position, profile_views);
            whoViewedProfileAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onBackPressed()
    {
        arr_whoviewedprofile = new ArrayList();
        finish();
    }
}
