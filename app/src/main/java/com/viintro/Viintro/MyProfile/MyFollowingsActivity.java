package com.viintro.Viintro.MyProfile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.Followers;
import com.viintro.Viintro.Model.Following;
import com.viintro.Viintro.Model.MyFollowing_Response;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;
import com.viintro.Viintro.Webservices.AccessTokenAPI;
import com.viintro.Viintro.listener.OnLoadMoreListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyFollowingsActivity extends AppCompatActivity {

    private RecyclerView recycler_view_followings;
    public static MyFollowingsAdapter myFollowingsAdapter;
    private Context context;
    private String bearertoken;
    private Configuration_Parameter m_config;
    private SharedPreferences sharedPreferences;
    public static ArrayList arr_followings;
    private TextView txt_no_internet_connection;
    private TextView txt_no_followings;
    private int index = 0;
    public static int arr_count_myfollowings = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_my_followings);
        context = this;
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        bearertoken = sharedPreferences.getString(m_config.login_access_token,"");
        arr_followings = new ArrayList();
        arr_count_myfollowings = 0;
        index = 0;
        TextView txt_Header = (TextView) findViewById(R.id.header);
        txt_Header.setText("Following");
        txt_no_internet_connection = (TextView) findViewById(R.id.txt_no_internet_connection);

        CommonFunctions.sDialog(context,"Loading...");
        initialiseUI();
        if(!CommonFunctions.chkStatus(context))
        {
            CommonFunctions.hDialog();
            txt_no_internet_connection.setVisibility(View.VISIBLE);
            return;
        }
        req_followings_API(context);
        setListAdapter();



        myFollowingsAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("followers", "Load More");
                arr_followings.add(null);
                myFollowingsAdapter.notifyItemInserted(arr_followings.size() - 1);

                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //Remove loading item
                        arr_followings.remove(arr_followings.size() - 1);
                        myFollowingsAdapter.notifyItemRemoved(arr_followings.size());

                        index = index + 1;
                        req_followings_API(context);

                        //myFollowingsAdapter.notifyDataSetChanged();
                        myFollowingsAdapter.setLoaded();
                    }
                }, 5000);
            }
        });

    }


    private void initialiseUI()
    {
        recycler_view_followings = (RecyclerView) findViewById(R.id.recycler_view_followings);
        txt_no_followings = (TextView) findViewById(R.id.txt_no_followings);

    }

    private void setListAdapter() {

        if(arr_followings.size() == 0)
        {
            recycler_view_followings.setLayoutManager(new LinearLayoutManager(context));
            myFollowingsAdapter= new MyFollowingsAdapter(context, arr_followings, recycler_view_followings, txt_no_followings);
            recycler_view_followings.setAdapter(myFollowingsAdapter);
        }
        else
        {
            myFollowingsAdapter.notifyDataSetChanged();
        }

    }
    //http://dev.api.viintro.com/api/v1/user/followers?page=0
    private void req_followings_API(final Context context) {
        Log.i("", "req_followings_API : " + " ");

        final Gson gson = new Gson();
        String url =  m_config.web_api_link+ "/followings?page="+index;
        Log.i("url", url);

        final JsonObjectRequest jsonarrayreq = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("response", ""+response);


                        MyFollowing_Response myFollowing_response = gson.fromJson(response.toString(),MyFollowing_Response.class);
                        if(myFollowing_response.getFollowing() == null && index == 0)
                        {
                            CommonFunctions.hDialog();
                            txt_no_followings.setVisibility(View.VISIBLE);
                            txt_no_internet_connection.setVisibility(View.GONE);
                            return;
                        }
                        else
                        {
                            if(myFollowing_response.getFollowing() != null)
                            {
                                ArrayList<Following> followings = myFollowing_response.getFollowing();
                                arr_count_myfollowings = followings.size();
                                for(int i = 0; i< followings.size(); i++)
                                {
                                    arr_followings.add(followings.get(i));
                                }
                                setListAdapter();
                                txt_no_internet_connection.setVisibility(View.GONE);
                                CommonFunctions.hDialog();
                            }
                            else
                            {
                                arr_count_myfollowings = 0;
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        CommonFunctions.hDialog();
                        txt_no_internet_connection.setVisibility(View.GONE);

                        if (error.networkResponse != null) {
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
                                        CommonFunctions.hDialog();
                                        if(network_obj.getInt("code") == 12)
                                        {
                                            // Alert Dialog for access token expired
                                            CommonFunctions.alertdialog_token(context,"accesstokenexpired");
                                        }
                                        else if(network_obj.getInt("code") == 16)
                                        {
                                            // Alert Dialog for refresh token expired
                                            CommonFunctions.alertdialog_token(context,"refreshtokenexpired");
                                        }
                                        else
                                        {

                                        }

                                    } catch (JSONException e)
                                    {
                                    }

                                }
                            }
                        } else {
                            if(index == 0)
                            {
                                txt_no_followings.setVisibility(View.VISIBLE);
                            }
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
        ViintroApplication.getInstance().addToRequestQueue(jsonarrayreq, "my_followings_api");

    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case (Constcore.REQUEST_CODE_CONNECT):
//                if(data != null)
//                {
//                    int position = data.getExtras().getInt("position");
//                    Following following = (Following) arr_followings.get(position);
//                    following.setConnection_requested(true);
//                    arr_followings.set(position, following);
//                    myFollowingsAdapter.notifyDataSetChanged();
//                }
//                break;
//        }
//    }

    public void onRowUpdate(int position, String condition)
    {

        if(condition.equals("connect")) {
            //Update row after sending request for connection
            Following following = (Following) arr_followings.get(position);
            following.setConnection_requested(true);
            arr_followings.set(position, following);
            myFollowingsAdapter.notifyDataSetChanged();
        }
        else if(condition.equals("follow"))
        {
            Following following = (Following) arr_followings.get(position);
            following.setFollowing(true);
            arr_followings.set(position, following);
            myFollowingsAdapter.notifyDataSetChanged();
        }
        else if(condition.equals("unfollow"))
        {
            arr_followings.remove(position);
            myFollowingsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        arr_followings = new ArrayList();
        finish();
    }
}
