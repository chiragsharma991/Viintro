package com.viintro.Viintro.MyProfile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.GetPost_Response;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

public class LiveVideosActivity extends AppCompatActivity
{
    int index = 0 , count = 20;
    int tempindex = 0;
    ArrayList<GetPost_Response> arr_livevideos;
    Context context;
    GridViewWithHeaderAndFooter videoGridview;
    LiveVideosAdapter livevideosadapter;
    View footerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_live_videos);
        TextView txt_Header = (TextView) findViewById(R.id.header);
        txt_Header.setText("Live videos");
        context = this;
        index = 0;
        tempindex = 0;
        count = 20;

        videoGridview = (GridViewWithHeaderAndFooter) findViewById(R.id.videoGridview);
        arr_livevideos = new ArrayList();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        footerView = layoutInflater.inflate(R.layout.footer_view, null);
        videoGridview.addFooterView(footerView);
        footerView.setVisibility(View.GONE);

        CommonFunctions.sDialog(context, "Loading data..");
        if(CommonFunctions.chkStatus(context))
        {
            req_get_all_post_API(context);
        }
        else
        {
            CommonFunctions.displayToast(context,getResources().getString(R.string.network_connection));
            CommonFunctions.hDialog();
            return;
        }


    }

    private void getDataInList()
    {


        Log.e("arra_live_videos"," "+arr_livevideos.size());

        if(livevideosadapter == null)
        {
            livevideosadapter = new LiveVideosAdapter(context, arr_livevideos);
            videoGridview.setAdapter(livevideosadapter);
        }
        else
        {
            livevideosadapter.notifyDataSetChanged();
        }



    }

    private AbsListView.OnScrollListener onScrollListener() {

        return new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (scrollState == SCROLL_STATE_IDLE && index < count)
                {
                    index = index + 1;
                    count = count + 20;

                    req_get_all_post_API(context);
                    livevideosadapter.notifyDataSetChanged();
                    footerView.setVisibility(View.GONE);

                    if(index >= count)
                    {
                        index = tempindex;
                        footerView.setVisibility(View.GONE);
                        return;
                    }
                    tempindex = index;

                }
                else
                {

                    footerView.setVisibility(View.GONE);

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {

            }


        };
    }

    // API Call to get all the posts
    public void req_get_all_post_API(final Context cont)
    {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String bearertoken = sharedPreferences.getString(m_config.login_access_token,"");


        final Gson gson = new Gson();
        String url = m_config.web_api_link_header + "/posts?page="+index;
        Log.d("url", url.toString());
        // prepare the Request
        JsonArrayRequest profilerequest = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        Log.d("Response", response.toString());

                            try
                            {

                                if (response.equals(null) || response == null || response.length() == 0 && index == 0) {
                                    CommonFunctions.hDialog();
                                    CommonFunctions.displayToast(context, "no data found");
                                }
                                else if (response.length() == 10)
                                {
                                    for (int i = 0; i < response.length(); i++)
                                    {
                                        JSONObject jsonObject = (JSONObject) response.get(i);
                                        Log.e("jsonObject", " " + jsonObject);
                                        GetPost_Response getPost_response = gson.fromJson(jsonObject.toString(), GetPost_Response.class);
                                        arr_livevideos.add(getPost_response);
                                    }
                                    CommonFunctions.hDialog();
                                    getDataInList();
                                    footerView.setVisibility(View.VISIBLE);
                                    videoGridview.setOnScrollListener(onScrollListener());

                                }
                                else if(response.length() < 10)
                                {
                                    for (int i = 0; i < response.length(); i++)
                                    {
                                        JSONObject jsonObject = (JSONObject) response.get(i);
                                        Log.e("jsonObject", " " + jsonObject);
                                        GetPost_Response getPost_response = gson.fromJson(jsonObject.toString(), GetPost_Response.class);
                                        arr_livevideos.add(getPost_response);
                                    }
                                    CommonFunctions.hDialog();
                                    getDataInList();
                                    index = 0;
                                    count = 10;
                                 }



                            } catch (Exception e) {
                                e.printStackTrace();
                            }



//                            Video video  =  getPost_response.getVideo();
//                            Log.e("video",""+video.toString());


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error.networkResponse != null) {
                            String networkresponse_data = new String(error.networkResponse.data);
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
                                        CommonFunctions.displayToast(context,network_obj.getString("message"));
                                    }

                                } catch (JSONException e) {
                                    CommonFunctions.hDialog();
                                    CommonFunctions.displayToast(context, context.getResources().getString(R.string.Live_videos_failed));
                                }

                            } else {
                                CommonFunctions.hDialog();
                                CommonFunctions.displayToast(context, context.getResources().getString(R.string.Live_videos_failed));
                            }
                        } else {
                            CommonFunctions.hDialog();
                            CommonFunctions.displayToast(context, context.getResources().getString(R.string.Live_videos_failed));
                        }


                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("version", Constcore.version);
                params.put("devicetype", "android");
                params.put("timezone:IST", CommonFunctions.getTimeZoneIST());
                params.put("Authorization","Bearer "+bearertoken);
                params.put("timesstamp",CommonFunctions.getTimeStamp());
                params.put("os_version",m_config.os_version);
                return params;
            }
        };

        // add it to the RequestQueue
        int socketTimeout = 10000;//60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        profilerequest.setRetryPolicy(policy);

        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(profilerequest, "my_profile_api");

    }
}
