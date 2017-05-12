package com.viintro.Viintro.Connections;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.viintro.Viintro.Model.Invitations;
import com.viintro.Viintro.Model.Invitations_Response;
import com.viintro.Viintro.Model.MyConnections_Response;
import com.viintro.Viintro.Model.MySuggestions_Response;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;
import com.viintro.Viintro.listener.OnLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.viintro.Viintro.HomeFeed.HomeActivity.rel_search;

/**
 * Created by hasai on 11/01/17.
 **/
public class ConnectionFragment_Old extends Fragment
{
/*
    private Context context;
    private Configuration_Parameter m_config;
    private SharedPreferences sharedPreferences;
    private EditText edt_search_Connections;
    private Button btn_connections, btn_suggestions;
    public static RecyclerView listView_Connection;
    public static RecyclerView listView_Suggestion;
    private TextView txt_no_suggestions;
    private ConnectionAdapter connection_adapter;
    private SuggestionAdapter suggestion_adapter;
    private String bearertoken;
    private ArrayList arr_myconnection;
    private View v;
    private TextView txt_no_internet_connection;
    private String selectedVal;
    private TextView txt_no_of_connections;
    private RelativeLayout rel_search_Connections;
    private int index = 0;
    private int count = 20;
    public static ArrayList arr_invitations_suggestions, arr_invitations_id;
    public static int connection_count = 0;

    public static ConnectionFragment_Old newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        ConnectionFragment_Old connectionFragment = new ConnectionFragment_Old();
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
        arr_myconnection = new ArrayList();
        arr_invitations_suggestions = new ArrayList();
        arr_invitations_id = new ArrayList();
        connection_count = 0;
        index = 0;
        count = 20;
        v = getView();
        txt_no_internet_connection = (TextView) v.findViewById(R.id.txt_no_internet_connection);

        CommonFunctions.sDialog(context,"Loading...");
        initialiseUI();
        if(!CommonFunctions.chkStatus(context))
        {
            CommonFunctions.hDialog();
            txt_no_of_connections.setVisibility(View.GONE);
            txt_no_internet_connection.setVisibility(View.VISIBLE);
            return;
        }
        req_connectionlist_API(context);
        setListAdapter();
        setSuggestionListAdapter();

        edt_search_Connections.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {

                Boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE) || (actionId == EditorInfo.IME_ACTION_SEARCH)) {
                    edt_search_Connections.clearFocus();
                    InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(inputManager != null){
                        inputManager.hideSoftInputFromWindow(edt_search_Connections.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    if(edt_search_Connections.getText().toString().replaceAll("\\s+", " ").trim().equals(""))
                    {
                        txt_no_of_connections.setVisibility(View.VISIBLE);
                        txt_no_of_connections.setText("0 connection");
                        arr_myconnection = new ArrayList();
                        setListAdapter();
                        CommonFunctions.sDialog(context,"Searching Connection...");
                        req_connectionlist_API(context);

                    }
                    else
                    {
                        txt_no_of_connections.setVisibility(View.GONE);
                        arr_myconnection = new ArrayList();
                        setListAdapter();
                        CommonFunctions.sDialog(context,"Searching Connection...");
                        req_search_connection_API(edt_search_Connections.getText().toString().replaceAll("\\s+", " ").trim());

                    }
                    handled = true;
                }
                return handled;
            }

        });

        edt_search_Connections.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                Log.e("text value "," "+editable.toString().replaceAll("\\s+", " ").trim());

                if(editable.toString().replaceAll("\\s+", " ").trim().equals(""))
                {
                    edt_search_Connections.clearFocus();
                    InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(inputManager != null){
                        inputManager.hideSoftInputFromWindow(edt_search_Connections.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    txt_no_of_connections.setVisibility(View.VISIBLE);
                    txt_no_of_connections.setText("0 connection");
                    arr_myconnection = new ArrayList();
                    setListAdapter();
                    CommonFunctions.sDialog(context,"Searching Connection...");
                    req_connectionlist_API(context);

                }
            }
        });

        connection_adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("connection", "Load More");
                arr_myconnection.add(null);
                connection_adapter.notifyItemInserted(arr_myconnection.size() - 1);

                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //Remove loading item
                        arr_myconnection.remove(arr_myconnection.size() - 1);
                        connection_adapter.notifyItemRemoved(arr_myconnection.size());
                        index = index + 20;
                        count = count + 20;
                        req_connectionlist_API(context);

                        connection_adapter.notifyDataSetChanged();
                        connection_adapter.setLoaded();
                    }
                }, 5000);
            }
        });


        if(suggestion_adapter != null)
        {
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

                            index = index + 20;
                            count = count + 20;
                            req_suggestions_list_API(arr_invitations_suggestions, context);

                            suggestion_adapter.notifyDataSetChanged();
                            suggestion_adapter.setLoaded();
                        }
                    }, 5000);
                }
            });
        }


    }

    private void setListAdapter() {

        if(arr_myconnection.size() == 0)
        {
            connection_adapter = new ConnectionAdapter(context, arr_myconnection, txt_no_of_connections);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            listView_Connection.setLayoutManager(mLayoutManager);
            listView_Connection.setAdapter(connection_adapter);
        }
        else
        {
            connection_adapter.notifyDataSetChanged();
        }

    }

    private void setSuggestionListAdapter() {

        if(arr_invitations_suggestions.size() == 0) {
            suggestion_adapter = new SuggestionAdapter(context, arr_invitations_suggestions, arr_invitations_id);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            listView_Suggestion.setLayoutManager(mLayoutManager);
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
        rel_search.setVisibility(View.GONE);
        return connection_view;
    }

    private void initialiseUI()
    {
        edt_search_Connections = (EditText) v.findViewById(R.id.edt_search_Connections);
        btn_connections = (Button) v.findViewById(R.id.btn_connections);
        btn_connections.setTextColor(R.color.grey);
        btn_connections.setBackgroundResource(R.drawable.border_round_left_selected);
        btn_suggestions = (Button) v.findViewById(R.id.btn_suggestions);
        rel_search_Connections = (RelativeLayout) v.findViewById(R.id.rel_search_Connections);
        rel_search_Connections.setVisibility(View.VISIBLE);
        listView_Connection = (RecyclerView) v.findViewById(R.id.listview_Connection);
        listView_Suggestion = (RecyclerView) v.findViewById(R.id.listview_Suggestion);
        txt_no_of_connections = (TextView) v.findViewById(R.id.txt_no_of_connections);
        txt_no_of_connections.setText("0 Connections");
        txt_no_suggestions = (TextView) v.findViewById(R.id.txt_no_suggestions);
        selectedVal = "Connections";



        btn_connections.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                connectionclick();
            }
        });

        btn_suggestions.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                suggestionclick();
            }
        });


    }

    public void connectionclick()
    {
        if(!selectedVal.equals("Connections"))
        {
            btn_connections.setTextColor(R.color.grey);
            btn_connections.setBackgroundResource(R.drawable.border_round_left_selected);
            btn_suggestions.setBackgroundResource(R.drawable.border_round_right);
            btn_suggestions.setTextColor(Color.WHITE);
            selectedVal = "Connections";
            listView_Suggestion.setVisibility(View.GONE);
            listView_Connection.setVisibility(View.VISIBLE);
            rel_search_Connections.setVisibility(View.VISIBLE);
            txt_no_of_connections.setText("0 Connections");
            txt_no_of_connections.setVisibility(View.VISIBLE);
            txt_no_suggestions.setVisibility(View.GONE);
            arr_myconnection = new ArrayList();
            arr_invitations_suggestions = new ArrayList();
            arr_invitations_id = new ArrayList();
            setSuggestionListAdapter();
            setListAdapter();

            if(!CommonFunctions.chkStatus(context))
            {
                txt_no_internet_connection.setVisibility(View.VISIBLE);
                txt_no_of_connections.setVisibility(View.GONE);
                return;
            }
            index = 0;
            count = 20;
            CommonFunctions.sDialog(context,"Loading...");
            req_connectionlist_API(context);

        }
    }

    public void suggestionclick()
    {
        if(!selectedVal.equals("Suggestions"))
        {
            btn_suggestions.setTextColor(R.color.grey);
            btn_suggestions.setBackgroundResource(R.drawable.border_round_right_selected);
            btn_connections.setBackgroundResource(R.drawable.border_round_left);
            btn_connections.setTextColor(Color.WHITE);
            selectedVal = "Suggestions";
            listView_Connection.setVisibility(View.GONE);
            listView_Suggestion.setVisibility(View.VISIBLE);
            rel_search_Connections.setVisibility(View.GONE);
            txt_no_of_connections.setVisibility(View.GONE);
            txt_no_suggestions.setVisibility(View.GONE);
            arr_invitations_suggestions = new ArrayList();
            arr_invitations_id = new ArrayList();
            arr_myconnection = new ArrayList();
            setSuggestionListAdapter();
            setListAdapter();

            if(!CommonFunctions.chkStatus(context))
            {
                txt_no_internet_connection.setVisibility(View.VISIBLE);
                return;
            }
            index = 0;
            count = 20;
            CommonFunctions.sDialog(context,"Loading...");
            req_invitations_list_API(context);

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
                            txt_no_of_connections.setText("0 Connection");
                            txt_no_of_connections.setVisibility(View.VISIBLE);
                            txt_no_internet_connection.setVisibility(View.GONE);
                            return;
                        }
                        else
                        {
                            ArrayList<Connections> connections = myConnections_response.getConnections();
                            if(myConnections_response.getConnection_count() == 1)
                            {
                                txt_no_of_connections.setText(String.valueOf(myConnections_response.getConnection_count())+" connection");
                            }
                            else
                            {
                                txt_no_of_connections.setText(String.valueOf(myConnections_response.getConnection_count())+" connections");
                            }

                            for(int i = 0; i< connections.size(); i++)
                            {
                                arr_myconnection.add(connections.get(i));
                            }
                            setListAdapter();
                            txt_no_internet_connection.setVisibility(View.GONE);
                            CommonFunctions.hDialog();
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

    public void req_invitations_list_API(final Context context) {

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

                            if(arr_list_invitations == null)
                            {
                                txt_no_internet_connection.setVisibility(View.GONE);
                                // Add "No invitations" as header in RecyclerView
                                Invitations invitations1 = new Invitations();
                                invitations1.setType("noinvitations");
                                arr_invitations_suggestions.add(invitations1);
                                req_suggestions_list_API(arr_invitations_suggestions, context);

                            }
                            else
                            {
                                // Add "Invitations" as Header in RecyclerView
                                Invitations invitations_header = new Invitations();
                                invitations_header.setType("header_invitations");
                                arr_invitations_suggestions.add(invitations_header);


                                for (int i = 0; i < arr_list_invitations.size(); i++)
                                {
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


                        } catch (Exception e)
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
                                            CommonFunctions.alertdialog_token(context, "accesstokenexpired");
                                        } else if (network_obj.getInt("code") == 16) {
                                            // Alert Dialog for refresh token expired
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
                                if (index == 0)
                                {
                                    Invitations invitations = new Invitations();
                                    invitations.setType("People you may know");
                                    arr_invitations_suggestions.add(invitations);
                                }

                                for (int i = 0; i < response.length(); i++)
                                {
                                    MySuggestions_Response mySuggestions_response = gson.fromJson(response.get(i).toString(), MySuggestions_Response.class);
                                    mySuggestions_response.setType("suggestions");
                                    arr_invitations_suggestions.add(mySuggestions_response);

                                }

                                setSuggestionListAdapter();
                                txt_no_internet_connection.setVisibility(View.GONE);
                                CommonFunctions.hDialog();


                        } catch (JSONException e) {
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

                            setSuggestionListAdapter();


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

    public void req_search_connection_API(String txt_search)
    {

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String bearertoken = sharedPreferences.getString(m_config.login_access_token,"");
        final Gson gson =  new Gson();

        String url = m_config.web_api_link_header+ "/connection/search?page="+index;

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("client_id", Constcore.client_Id);
            obj.put("client_secret", Constcore.client_Secret);
            obj.put("search", txt_search);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        // prepare the Request
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        Log.d("Response", response.toString());
                        try
                        {
                            for(int i = 0; i < response.length(); i++)
                            {
                                Connections connections = gson.fromJson(response.get(i).toString(),Connections.class);
                                arr_myconnection.add(connections);
                            }
                            setListAdapter();
                            txt_no_internet_connection.setVisibility(View.GONE);
                            CommonFunctions.hDialog();

                        } catch (JSONException e) {
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

                                    }

                                }
                            }
                        } else
                        {
                            CommonFunctions.displayToast(context,context.getResources().getString(R.string.no_connection_found));

                        }



                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("version","1");
                params.put("devicetype","android");
                params.put("timezone:IST","IST");
                params.put("Authorization","Bearer "+bearertoken);
                params.put("timesstamp",CommonFunctions.getTimeStamp());
                params.put("os_version",m_config.os_version);

                return params;
            }
        };

        // add it to the RequestQueue
        int socketTimeout = 10000;//60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        getRequest.setRetryPolicy(policy);
        //queue.add(getRequest);

        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(getRequest, "search_connection_api");



    }


*/
}