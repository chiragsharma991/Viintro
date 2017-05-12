package com.viintro.Viintro.Connections;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.MyConnections_Response;
import com.viintro.Viintro.Model.MySuggestions_Response;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.viintro.Viintro.HomeFeed.HomeActivity.rel_search;

/**
 * Created by hasai on 11/01/17.
 */
public class ConnectionFragment_ListView extends Fragment
{
/*
    private Context context;
    private Configuration_Parameter m_config;
    private SharedPreferences sharedPreferences;
    private Button btn_connections, btn_suggestions;
    private ListView listView_Connection, listView_Suggestion;
    private TextView txt_no_suggestions;
    private ConnectionAdapter connection_adapter;
    private SuggestionAdapter suggestion_adapter;
    private String bearertoken;
    private ArrayList arr_myconnection, arr_mysuggestions;
    private View v;
    private TextView txt_no_internet_connection;
    private String selectedVal;
    private TextView txt_no_of_connections;
    private RelativeLayout rel_search_Connections;

    public static ConnectionFragment_ListView newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        ConnectionFragment_ListView connectionFragment = new ConnectionFragment_ListView();
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
        arr_mysuggestions = new ArrayList();
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
        btn_connections = (Button) v.findViewById(R.id.btn_connections);
        btn_connections.setTextColor(R.color.grey);
        btn_connections.setBackgroundResource(R.drawable.border_round_left_selected);
        btn_suggestions = (Button) v.findViewById(R.id.btn_suggestions);
        rel_search_Connections = (RelativeLayout) v.findViewById(R.id.rel_search_Connections);
        rel_search_Connections.setVisibility(View.VISIBLE);
        listView_Connection = (ListView) v.findViewById(R.id.listview_Connection);
        listView_Suggestion = (ListView) v.findViewById(R.id.listview_Suggestion);
        txt_no_of_connections = (TextView) v.findViewById(R.id.txt_no_of_connections);
        txt_no_of_connections.setText("0 Connections");
        txt_no_suggestions = (TextView) v.findViewById(R.id.txt_no_suggestions);
        selectedVal = "Connections";

//        listView_Connection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
//
//            }
//        });
//
//
//
//
//        listView_Suggestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l)
//            {
//                Log.e("adapterView suggestion"," "+view.getId()+" "+pos);
//            }
//        });

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
            txt_no_suggestions.setVisibility(View.GONE);
            arr_myconnection = new ArrayList();
            arr_mysuggestions = new ArrayList();
            if(connection_adapter != null)
            {
                connection_adapter = new ConnectionAdapter(context, arr_myconnection);
                listView_Connection.setAdapter(connection_adapter);
            }

            if(suggestion_adapter != null)
            {
                suggestion_adapter = new SuggestionAdapter(context, arr_mysuggestions);
                listView_Suggestion.setAdapter(suggestion_adapter);
            }
            if(!CommonFunctions.chkStatus(context))
            {
                txt_no_internet_connection.setVisibility(View.VISIBLE);
                return;
            }

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
            txt_no_of_connections.setText("People you may know");
            txt_no_suggestions.setVisibility(View.GONE);
            arr_mysuggestions = new ArrayList();
            arr_myconnection = new ArrayList();
            if(suggestion_adapter != null)
            {
                suggestion_adapter = new SuggestionAdapter(context, arr_mysuggestions);
                listView_Suggestion.setAdapter(suggestion_adapter);
            }

            if(connection_adapter != null)
            {
                connection_adapter = new ConnectionAdapter(context, arr_myconnection);
                listView_Connection.setAdapter(connection_adapter);
            }
            if(!CommonFunctions.chkStatus(context))
            {
                txt_no_internet_connection.setVisibility(View.VISIBLE);
                return;
            }

            CommonFunctions.sDialog(context,"Loading...");
            req_suggestionlist_API(context);

        }
    }

    public void req_connectionlist_API(final Context context) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue((Activity) context);
        final Gson gson = new Gson();
        String url =  m_config.web_api_link+ "/connections";//

        final JsonArrayRequest jsonarrayreq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("", "req_connectionlist_API : " + " " + response);

                        try
                        {
                            for(int i = 0; i < response.length(); i++)
                            {
                                MyConnections_Response myConnections_response = gson.fromJson(response.get(i).toString(),MyConnections_Response.class);
                                arr_myconnection.add(myConnections_response);
                            }
                            connection_adapter = new ConnectionAdapter(context, arr_myconnection);
                            listView_Connection.setAdapter(connection_adapter);
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
                        CommonFunctions.displayToast(context,context.getResources().getString(R.string.no_connection_found));
                        txt_no_internet_connection.setVisibility(View.GONE);
                        connection_adapter = new ConnectionAdapter(context, arr_myconnection);
                        listView_Connection.setAdapter(connection_adapter);


                    }
                }

        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + bearertoken);
                params.put("version",Constcore.version);
                params.put("devicetype","android");
                params.put("timezone",CommonFunctions.getTimeZoneIST());
                return params;
            }
        };
        int socketTimeout = 60000;//5 seconds

        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonarrayreq.setRetryPolicy(policy);
        queue.add(jsonarrayreq);

    }

    public void req_suggestionlist_API(final Context context) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue((Activity) context);
        final Gson gson = new Gson();
        String url =  m_config.web_api_link_header+ "/connection/suggestion";//

        final JsonArrayRequest jsonarrayreq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("", "req_suggestionlist_API : " + " " + response);
                        try
                        {
                            for(int i = 0; i < response.length(); i++)
                            {
                                MySuggestions_Response mySuggestions_response = gson.fromJson(response.get(i).toString(),MySuggestions_Response.class);
                                arr_mysuggestions.add(mySuggestions_response);
                            }
                            suggestion_adapter = new SuggestionAdapter(context, arr_mysuggestions);
                            listView_Suggestion.setAdapter(suggestion_adapter);
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
                        CommonFunctions.displayToast(context,context.getResources().getString(R.string.no_suggestions_found));
                        txt_no_suggestions.setVisibility(View.VISIBLE);
                        suggestion_adapter = new SuggestionAdapter(context, arr_mysuggestions);
                        listView_Suggestion.setAdapter(suggestion_adapter);
                        txt_no_internet_connection.setVisibility(View.GONE);

                    }
                }

        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + bearertoken);
                params.put("version",Constcore.version);
                params.put("devicetype","android");
                params.put("timezone",CommonFunctions.getTimeZoneIST());
                return params;
            }
        };
        int socketTimeout = 60000;//5 seconds

        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonarrayreq.setRetryPolicy(policy);
        queue.add(jsonarrayreq);



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult"," ");
        switch (requestCode) {
            case Constcore.REQUEST_CODE_CONNECT:
                Log.e("call back","");
                break;

        }
    }
*/
}