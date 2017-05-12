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
import com.viintro.Viintro.Model.MyFollowers_Response;
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

public class MyFollowersActivity extends AppCompatActivity {

    private RecyclerView recycler_view_followers;
    public static MyFollowersAdapter myFollowersAdapter;
    private Context context;
    private String bearertoken;
    private Configuration_Parameter m_config;
    private SharedPreferences sharedPreferences;
    public static ArrayList arr_followers;
    private TextView txt_no_internet_connection;
    private TextView txt_no_followers;
    private int index = 0;
    public static int arr_count_myfollowers = 0;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_my_followers);
        context = this;
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        bearertoken = sharedPreferences.getString(m_config.login_access_token,"");
        arr_followers = new ArrayList();
        arr_count_myfollowers = 0;
        index = 0;
        TextView txt_Header = (TextView) findViewById(R.id.header);
        txt_Header.setText("Followers");
        txt_no_internet_connection = (TextView) findViewById(R.id.txt_no_internet_connection);

        CommonFunctions.sDialog(context,"Loading...");
        initialiseUI();
        if(!CommonFunctions.chkStatus(context))
        {
            CommonFunctions.hDialog();
            txt_no_internet_connection.setVisibility(View.VISIBLE);
            return;
        }
        req_followers_API(context);
        setListAdapter();

        myFollowersAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("followers", "Load More");
                arr_followers.add(null);
                myFollowersAdapter.notifyItemInserted(arr_followers.size() - 1);

                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            //Remove loading item
                            arr_followers.remove(arr_followers.size() - 1);
                            myFollowersAdapter.notifyItemRemoved(arr_followers.size());
                            index = index + 1;
                            req_followers_API(context);

                            //myFollowersAdapter.notifyDataSetChanged();
                            myFollowersAdapter.setLoaded();
                        }
                        catch (Exception e)
                        {

                        }
                    }
                }, 5000);
            }
        });

    }

    private void initialiseUI()
    {
        recycler_view_followers = (RecyclerView) findViewById(R.id.recycler_view_followers);
        txt_no_followers = (TextView) findViewById(R.id.txt_no_followers);


    }

    private void setListAdapter() {

        if(arr_followers.size() == 0)
        {
            recycler_view_followers.setLayoutManager(new LinearLayoutManager(context));
            myFollowersAdapter = new MyFollowersAdapter(context, arr_followers, recycler_view_followers, txt_no_followers);
            recycler_view_followers.setAdapter(myFollowersAdapter);
        }
        else
        {
            myFollowersAdapter.notifyDataSetChanged();
        }

    }

    private void req_followers_API(final Context context) {
        Log.i("", "req_followers_API : " + " ");

        final Gson gson = new Gson();
        String url =  m_config.web_api_link+ "/followers?page="+index;
        Log.i("url", url);

        final JsonObjectRequest jsonarrayreq = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("response", ""+response);


                        MyFollowers_Response myFollowers_response = gson.fromJson(response.toString(),MyFollowers_Response.class);
//                        if(myFollowers_response.getFollowers_count() == 0 && index == 0)
//                        {
//                            CommonFunctions.hDialog();
//                            txt_no_followers.setVisibility(View.VISIBLE);
//                            txt_no_internet_connection.setVisibility(View.GONE);
//                            return;
//                        }

                        if(myFollowers_response.getFollowers() == null && index == 0)
                        {
                            CommonFunctions.hDialog();
                            txt_no_followers.setVisibility(View.VISIBLE);
                            txt_no_internet_connection.setVisibility(View.GONE);
                            return;
                        }
                        else
                        {
                            if(myFollowers_response.getFollowers() != null)
                            {
                                ArrayList<Followers> followers = myFollowers_response.getFollowers();
                                arr_count_myfollowers = followers.size();
                                for(int i = 0; i< followers.size(); i++)
                                {
                                    arr_followers.add(followers.get(i));
                                }
                                setListAdapter();
                                txt_no_internet_connection.setVisibility(View.GONE);
                                CommonFunctions.hDialog();
                            }
                            else
                            {
                                arr_count_myfollowers = 0;
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
                        } else
                        {
                            if(index == 0)
                            {
                                txt_no_followers.setVisibility(View.VISIBLE);
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
        ViintroApplication.getInstance().addToRequestQueue(jsonarrayreq, "my_followers_api");


    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case (Constcore.REQUEST_CODE_CONNECT):
//                if(data != null)
//                {
//                    Log.e("came here "," on result followers activity");
//                    int position = data.getExtras().getInt("position");
//                    Followers followers = (Followers) arr_followers.get(position);
//                    followers.setConnection_requested(true);
//                    arr_followers.set(position, followers);
//                    myFollowersAdapter.notifyDataSetChanged();
//                }
//                break;
//        }
//    }

   public void onRowUpdate(int position, String condition)
   {
       if(condition.equals("connect"))
       {
           //Update row after sending request for connection
           Followers followers = (Followers) arr_followers.get(position);
           followers.setConnection_requested(true);
           arr_followers.set(position, followers);
           myFollowersAdapter.notifyDataSetChanged();
       }
       else if(condition.equals("follow"))
       {
           // Update row after after api success to "Following"
           Followers followers = (Followers) arr_followers.get(position);
           followers.setFollowing(true);
           arr_followers.set(position, followers);
           myFollowersAdapter.notifyDataSetChanged();
       }
       else if(condition.equals("unfollow"))
       {
           // Update row after after api success to "Follow"
           // change text of follow btn after api success to "Follow"
           Followers followers = (Followers) arr_followers.get(position);
           followers.setFollowing(false);
           arr_followers.set(position, followers);
           myFollowersAdapter.notifyDataSetChanged();
       }

   }

    @Override
    public void onBackPressed() {
        arr_followers = new ArrayList();
        finish();
    }

}
