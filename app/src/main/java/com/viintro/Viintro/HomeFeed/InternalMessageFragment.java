package com.viintro.Viintro.HomeFeed;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import com.viintro.Viintro.Model.Connections;
import com.viintro.Viintro.Model.MyConnections_Response;
import com.viintro.Viintro.Model.SendNewMsg_Request;
import com.viintro.Viintro.Model.ShareMsgContents;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;
import com.viintro.Viintro.Webservices.SendNewMsgAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.viintro.Viintro.HomeFeed.ShareMsgAdapter.sendNewMsg_request;
import static com.viintro.Viintro.Webservices.SendNewMsgAPI.req_send_message_API;

/**
 * Created by rkanawade on 11/04/17.
 */

public class InternalMessageFragment extends Fragment
{
    private static Context context;
    private Button btn_send;
    private Configuration_Parameter m_config;
    private SharedPreferences sharedPreferences;
    public static RecyclerView lv_Messages_inPost;
    private static ShareMsgAdapter share_messages_adapter;
    private String bearertoken;
    private View v;
    private static TextView txt_no_internet_connection;
    private int index = 0;
    public static ArrayList arr_messages_in_post;
    public static int arr_count_messages_in_post = 0;
    JSONObject postObject;

    public InternalMessageFragment(JSONObject postObject) {
        this.postObject = postObject;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_internal_messages, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        bearertoken = sharedPreferences.getString(m_config.login_access_token,"");
        arr_messages_in_post = new ArrayList();
        arr_count_messages_in_post = 0;
        index = 0;
        v = getView();
        txt_no_internet_connection = (TextView) v.findViewById(R.id.txt_no_internet_connection);

        CommonFunctions.sDialog(context,"Loading...");

        initialiseUI();
        if(!CommonFunctions.chkStatus(context))
        {
            CommonFunctions.hDialog();
            txt_no_internet_connection.setVisibility(View.VISIBLE);
            return;
        }
        req_connectionlist_API(context);
        setMessagesListAdapter(context);


    }



    private void initialiseUI()
    {
        lv_Messages_inPost = (RecyclerView) v.findViewById(R.id.list_msgs);
        btn_send = (Button) v.findViewById(R.id.btn_send);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                    SendNewMsg_Request sendNewMsg_request = new SendNewMsg_Request();
//
//                        sendNewMsg_request.setPost_id(postObject.getString("post_id"));
//                        sendNewMsg_request.setPost_slug(postObject.getString("post_slug"));
//                        sendNewMsg_request.setPost_owner_id(postObject.getString("post_owner_id"));
//                        sendNewMsg_request.setPost_owner_name(postObject.getString("post_owner_name"));
//                        sendNewMsg_request.setPost_description(postObject.getString("post_description"));
//                        sendNewMsg_request.setType("post");

                req_send_message_API(context, sendNewMsg_request, "Internal_message_fragment");
            }
        });
    }

    private void setMessagesListAdapter(Context context)
    {
        if(arr_messages_in_post.size() == 0)
        {
            lv_Messages_inPost.setLayoutManager(new LinearLayoutManager(context));
            share_messages_adapter = new ShareMsgAdapter(context, arr_messages_in_post, postObject);
            lv_Messages_inPost.setAdapter(share_messages_adapter);
        }
        else
        {
            share_messages_adapter.notifyDataSetChanged();
        }

    }

    public void req_connectionlist_API(final Context context) {


        final Gson gson = new Gson();
        String url =  m_config.web_api_link+ "/connections?page="+index;//

        final JsonObjectRequest jsonarrayreq = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("", "req_connectionlist_API : " + " " + response);

                        MyConnections_Response myConnections_response = gson.fromJson(response.toString(),MyConnections_Response.class);

                        if(myConnections_response.getConnections() == null && index == 0)
                        {
                            CommonFunctions.hDialog();
                            txt_no_internet_connection.setVisibility(View.GONE);
                            return;
                        }
                        else
                        {
                            if(myConnections_response.getConnections() != null)
                            {

                                ArrayList<Connections> connections = myConnections_response.getConnections();
                                arr_count_messages_in_post = connections.size();
                                for (int i = 0; i < connections.size(); i++)
                                {
                                    arr_messages_in_post.add(connections.get(i));
                                }
                                setMessagesListAdapter(context);
                                txt_no_internet_connection.setVisibility(View.GONE);
                                CommonFunctions.hDialog();
                            }
                            else
                            {
                                arr_count_messages_in_post = 0;
                                txt_no_internet_connection.setVisibility(View.GONE);
                            }
                        }



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        CommonFunctions.hDialog();
                        txt_no_internet_connection.setVisibility(View.GONE);
                        if (error.networkResponse != null)
                        {
                            String networkresponse_data = new String(error.networkResponse.data);
                            if(error.networkResponse.statusCode == 500)
                            {
                                CommonFunctions.displayToast(context,context.getResources().getString(R.string.internal_server_error));
                            }
                            else {
                                if (networkresponse_data != null)
                                {
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
                        } else
                        {
                            txt_no_internet_connection.setVisibility(View.GONE);
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
        ViintroApplication.getInstance().addToRequestQueue(jsonarrayreq, "connection_frag_api");

    }

}
