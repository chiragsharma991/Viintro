package com.viintro.Viintro.Connections;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.Connection;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.Connections;
import com.viintro.Viintro.Model.Follow_Request;
import com.viintro.Viintro.Model.Invitations;
import com.viintro.Viintro.Model.Invitations_Response;
import com.viintro.Viintro.Model.MyConnections_Response;
import com.viintro.Viintro.Model.MySuggestions_Response;
import com.viintro.Viintro.Model.Profile_Views;
import com.viintro.Viintro.Model.SuggestionsClass;
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

import retrofit2.http.GET;

import static com.viintro.Viintro.HomeFeed.HomeActivity.img_settings;
import static com.viintro.Viintro.HomeFeed.HomeActivity.rel_search;

/**
 * Created by hasai on 11/01/17.
 **/
public class ConnectionFragment extends Fragment
{

    private Context context;
    private Configuration_Parameter m_config;
    private SharedPreferences sharedPreferences;
    public static RecyclerView listView_Suggestion;
    private SuggestionAdapter suggestion_adapter;
    private String bearertoken;
    private View v;
    private TextView txt_no_internet_connection;
    private int index = 0;
    public static ArrayList arr_invitations_suggestions, arr_invitations_id;
    public static int connection_count = 0;
    public static int arr_count_suggestions = 0;

    public static ConnectionFragment newInstance(int instance)
    {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        ConnectionFragment connectionFragment = new ConnectionFragment();
        connectionFragment.setArguments(args);
        return connectionFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        bearertoken = sharedPreferences.getString(m_config.login_access_token,"");
        arr_invitations_suggestions = new ArrayList();
        arr_invitations_id = new ArrayList();
        connection_count = 0;
        arr_count_suggestions = 0;
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

        if(savedInstanceState == null)
        {
            img_settings.setVisibility(View.GONE);
            req_invitations_list_API(context);
        }
        setSuggestionListAdapter();


        suggestion_adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("suggestion", "Load More");
                arr_invitations_suggestions.add(null);
                suggestion_adapter.notifyItemInserted(arr_invitations_suggestions.size() - 1);

                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //Remove loading item
                        arr_invitations_suggestions.remove(arr_invitations_suggestions.size() - 1);
                        suggestion_adapter.notifyItemRemoved(arr_invitations_suggestions.size());

                        index = index + 1;
                        req_suggestions_list_API(arr_invitations_suggestions, context);

                        suggestion_adapter.notifyDataSetChanged();
                        suggestion_adapter.setLoaded();
                    }
                }, 5000);
            }
        });



    }

    private void setSuggestionListAdapter() {

        if(arr_invitations_suggestions.size() == 0)
        {
            listView_Suggestion.setLayoutManager(new LinearLayoutManager(context));
            suggestion_adapter = new SuggestionAdapter(context, arr_invitations_suggestions, arr_invitations_id);
            listView_Suggestion.setAdapter(suggestion_adapter);
        }
        else
        {
            suggestion_adapter.notifyDataSetChanged();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View connection_view = inflater.inflate(R.layout.fragment_connection, container, false);
        rel_search.setVisibility(View.VISIBLE);
        img_settings.setVisibility(View.GONE);
        return connection_view;
    }

    private void initialiseUI()
    {

        listView_Suggestion = (RecyclerView) v.findViewById(R.id.listview_Suggestion);

    }


    public void req_invitations_list_API(final Context context) {

        Log.e("arr_invitations_suggestions"," "+arr_invitations_suggestions.size());

        final Gson gson = new Gson();
        String url =  m_config.web_api_link_header+ "/connection/invitations?page=0";//

        final JsonObjectRequest jsonarrayreq = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("", "req_invitations_list_API : " + " " + response);

                        try
                        {
                            Invitations invitations = new Invitations();
                            invitations.setType("connections");
                            arr_invitations_suggestions.add(invitations);

                            Invitations_Response invitations_response = gson.fromJson(response.toString(), Invitations_Response.class);
                            ArrayList<Invitations> arr_list_invitations = invitations_response.getInvitations();
                            connection_count = invitations_response.getConnection_count();

                            Log.e("arr_list_invitations"," "+arr_list_invitations);
                            if(arr_list_invitations == null)
                            {
                                txt_no_internet_connection.setVisibility(View.GONE);
                                // Add "No invitations" as header in RecyclerView
                                Invitations invitations1 = new Invitations();
                                invitations1.setType("noinvitations");
                                arr_invitations_suggestions.add(invitations1);
                                req_suggestions_list_API(arr_invitations_suggestions, context);

                            }
                            else {
                                // Add "Invitations" as Header in RecyclerView
                                Invitations invitations_header = new Invitations();
                                invitations_header.setType("header_invitations");
                                arr_invitations_suggestions.add(invitations_header);


                                for (int i = 0; i < arr_list_invitations.size(); i++) {
                                    if (i == 2) {
                                        break;
                                    }
                                    // Add "Response" as Header in RecyclerView
                                    Invitations invitations2 = arr_list_invitations.get(i);
                                    invitations2.setType("invitations");
                                    arr_invitations_suggestions.add(invitations2);
                                    arr_invitations_id.add(invitations2.getId());


                                }

                                // Add "Show All" as button in RecyclerView
                                Invitations invitations3 = new Invitations();
                                invitations3.setType("see all");
                                arr_invitations_suggestions.add(invitations3);
                                req_suggestions_list_API(arr_invitations_suggestions, context);
                                txt_no_internet_connection.setVisibility(View.GONE);
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        if (error.networkResponse != null)
                        {

                            String networkresponse_data = new String(error.networkResponse.data);
                            if(error.networkResponse.statusCode == 500)
                            {
                                CommonFunctions.hDialog();
                                CommonFunctions.displayToast(context,context.getResources().getString(R.string.internal_server_error));

                            }
                            else
                            {
                                if (networkresponse_data != null)
                                {

                                    JSONObject network_obj;
                                    try {
                                        network_obj = new JSONObject(networkresponse_data);
                                        if (network_obj.getInt("code") == 12) {
                                            // Alert Dialog for access token expired
                                            CommonFunctions.hDialog();
                                            CommonFunctions.alertdialog_token(context, "accesstokenexpired");
                                        } else if (network_obj.getInt("code") == 16) {
                                            // Alert Dialog for refresh token expired
                                            CommonFunctions.hDialog();
                                            CommonFunctions.alertdialog_token(context, "refreshtokenexpired");
                                        }
                                        else
                                        {
                                            CommonFunctions.hDialog();
                                            CommonFunctions.displayToast(context, network_obj.getString("message"));
                                        }

                                    } catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                        CommonFunctions.hDialog();

                                    }

                                }
                            }
                        } else {

                            CommonFunctions.hDialog();
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
        //queue.add(jsonarrayreq);

        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(jsonarrayreq, "connection_invitation_api");

    }

    public void req_suggestions_list_API(final ArrayList arr_invitations_suggestions, final Context context)
    {

        final Gson gson = new Gson();
        String url = m_config.web_api_link_header+"/connection/people-you-may-know?page="+index;

        final JsonArrayRequest jsonarrayreq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("", "req_suggestions_list_API : " + " " + response);

                        try
                        {

                            if (response == null || response.equals(null) || response.length() == 0 && index == 0)
                            {
                                setSuggestionListAdapter();
                                CommonFunctions.hDialog();
                            }
                            else
                            {
                                if(response != null && response.length() > 0)
                                {
                                    if (index == 0)
                                    {
                                        Invitations invitations = new Invitations();
                                        invitations.setType("People you may know");
                                        arr_invitations_suggestions.add(invitations);
                                    }


                                    arr_count_suggestions = response.length();
                                    for (int i = 0; i < response.length(); i++) {
                                        MySuggestions_Response mySuggestions_response = gson.fromJson(response.get(i).toString(), MySuggestions_Response.class);
                                        mySuggestions_response.setType("suggestions");
                                        arr_invitations_suggestions.add(mySuggestions_response);

                                    }

                                    setSuggestionListAdapter();
                                    txt_no_internet_connection.setVisibility(View.GONE);
                                    CommonFunctions.hDialog();
                                }
                                else
                                {
                                    arr_count_suggestions = 0;
                                }

                            }


                        }
                        catch (JSONException e)
                        {
                            arr_count_suggestions = 0;
                            CommonFunctions.hDialog();
                            e.printStackTrace();
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
                            else
                            {
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
                                            CommonFunctions.displayToast(context,network_obj.getString("message"));
                                        }

                                    } catch (JSONException e) {

                                    }

                                }
                            }
                        } else
                        {
                            arr_count_suggestions = 0;
                            setSuggestionListAdapter();// if response is null in people you may know API

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
        ViintroApplication.getInstance().addToRequestQueue(jsonarrayreq, "connection_suggestion_api");

    }


    // update connection count after removing connection from Connection Activity
    public static void upd_conn_text_on_suggestion_page(int val)
    {
            connection_count = val;
            listView_Suggestion.getAdapter().notifyDataSetChanged();

    }



}