package com.viintro.Viintro.Invitations;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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
import com.viintro.Viintro.Model.Invitations;
import com.viintro.Viintro.Model.InvitationsSent;
import com.viintro.Viintro.Model.Invitations_Response;
import com.viintro.Viintro.Model.Sent_Invitations_Response;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;
import com.viintro.Viintro.listener.OnLoadMoreListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rkanawade on 13/04/17.
 */

public class InvitationsSentFragment extends Fragment{


    public static RecyclerView recyclerView_sentInvitations;
    private InvitationsSentAdapter adapter_invitationssent;
    private Context context;
    private String bearertoken;
    private Configuration_Parameter m_config;
    private SharedPreferences sharedPreferences;
    private TextView txt_no_internet_connection;
    public static TextView txt_no_invitations_sent;
    private int index = 0;
    public static ArrayList arr_invitations_sent;
    public static int arr_count_invitations_sent = 0;
    View v;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        bearertoken = sharedPreferences.getString(m_config.login_access_token,"");
        arr_invitations_sent = new ArrayList();
        arr_count_invitations_sent = 0;
        index = 0;
        v = getView();
        txt_no_internet_connection = (TextView) v.findViewById(R.id.txt_no_internet_connection);

        CommonFunctions.sDialog(context,"Loading...");
        initialiseUI(v);
        if(!CommonFunctions.chkStatus(context))
        {
            CommonFunctions.hDialog();
            txt_no_internet_connection.setVisibility(View.VISIBLE);
            return;
        }
        req_invitations_sent_list_API(context);
        setListAdapter();

        adapter_invitationssent.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("suggestion", "Load More");
                arr_invitations_sent.add(null);
                adapter_invitationssent.notifyItemInserted(arr_invitations_sent.size() - 1);

                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //Remove loading item
                        arr_invitations_sent.remove(arr_invitations_sent.size() - 1);
                        adapter_invitationssent.notifyItemRemoved(arr_invitations_sent.size());

                        index = index + 1;
                        req_invitations_sent_list_API(context);

                        adapter_invitationssent.notifyDataSetChanged();
                        adapter_invitationssent.setLoaded();
                    }
                }, 5000);
            }
        });
    }

    private void initialiseUI(View v)
    {
        recyclerView_sentInvitations = (RecyclerView) v.findViewById(R.id.recyclerView_sentInvitations);
        txt_no_invitations_sent = (TextView) v.findViewById(R.id.txt_no_invitations);

    }

    private void setListAdapter()
    {
        if(arr_invitations_sent.size() == 0)
        {
            recyclerView_sentInvitations.setLayoutManager(new LinearLayoutManager(context));
            adapter_invitationssent = new InvitationsSentAdapter(context, arr_invitations_sent);
            recyclerView_sentInvitations.setAdapter(adapter_invitationssent);
        }
        else
        {
            adapter_invitationssent.notifyDataSetChanged();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View connection_view = inflater.inflate(R.layout.fragment_invitations_sent, container, false);
        return connection_view;
    }

    private void req_invitations_sent_list_API(final Context context) {

        final Gson gson = new Gson();
        String url =  m_config.web_api_link_header+ "/connection/my-sent-invitations?page="+index;

        final JsonObjectRequest jsonarrayreq = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("", "req_invitations_sent_list_API : " + " " + response);

                        try {
                            Sent_Invitations_Response invitations_response = gson.fromJson(response.toString(), Sent_Invitations_Response.class);
                            if (invitations_response.getSent_invitations() == null && index == 0) {
                                CommonFunctions.hDialog();
                                txt_no_internet_connection.setVisibility(View.GONE);
                                txt_no_invitations_sent.setVisibility(View.VISIBLE);

                            } else {
                                if (invitations_response.getSent_invitations() != null) {
                                    arr_count_invitations_sent = invitations_response.getSent_invitations().size();
                                    for (int i = 0; i < invitations_response.getSent_invitations().size(); i++)
                                    {
                                        Invitations invitations = invitations_response.getSent_invitations().get(i);
                                        arr_invitations_sent.add(invitations);
                                    }
                                    setListAdapter();
                                    Log.e("arr_invitations", " " + arr_invitations_sent.size());
                                    txt_no_internet_connection.setVisibility(View.GONE);
                                    CommonFunctions.hDialog();
                                }
                                else
                                {
                                    arr_count_invitations_sent = 0;
                                }

                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        CommonFunctions.hDialog();
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
        ViintroApplication.getInstance().addToRequestQueue(jsonarrayreq, "sent_invitations_api");


    }
}
