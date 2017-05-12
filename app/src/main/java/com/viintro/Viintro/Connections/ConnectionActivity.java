package com.viintro.Viintro.Connections;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
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
import com.viintro.Viintro.Model.MyConnections_Response;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Reusables.TextFont;
import com.viintro.Viintro.ViintroApplication;
import com.viintro.Viintro.listener.OnLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import static com.viintro.Viintro.Connections.ConnectionFragment.upd_conn_text_on_suggestion_page;
import static com.viintro.Viintro.Webservices.ConnectionRemoveAPI.req_connection_remove_API;

/**
 * Created by hasai on 14/04/17.
 */

public class ConnectionActivity extends AppCompatActivity {

    private Context context;
    private Configuration_Parameter m_config;
    private SharedPreferences sharedPreferences;
    private EditText edt_search_Connections;
    private ConnectionAdapter connection_adapter;
    private static TextView txt_no_of_connections;
    private ImageView img_sort_filter;
    private TextView txt_no_internet_connection;
    private String bearertoken;
    private RelativeLayout rel_search_Connections;
    private int index = 0;
    public static ArrayList arr_myconnection;
    public static int arr_count_myconnection = 0;
    public static RecyclerView listView_Connection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_connections);
        context = this;
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        bearertoken = sharedPreferences.getString(m_config.login_access_token,"");
        arr_myconnection = new ArrayList();
        arr_count_myconnection = 0;
        index = 0;
        TextView txt_header = (TextView) findViewById(R.id.header);
        txt_header.setText("Connections");
        txt_header.setTypeface(TextFont.opensans_bold(context));
        txt_no_internet_connection = (TextView) findViewById(R.id.txt_no_internet_connection);

        RelativeLayout img_back = (RelativeLayout) findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


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


        img_sort_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final ListPopupWindow popupWindow;
                List<HashMap<String, String>> data = new ArrayList<>();
                HashMap<String, String> map = new HashMap<>();
                map.put("Title", "Sort connection by");
                data.add(map);
                map = new HashMap<>();
                map.put("Title", "Ascending");
                data.add(map);
                map = new HashMap<>();
                map.put("Title", "Descending");
                data.add(map);
                map = new HashMap<>();
                map.put("Title", "Recently added");
                data.add(map);

                popupWindow = new ListPopupWindow(context);

                ListAdapter adapter = new SimpleAdapter(
                        context,
                        data,
                        R.layout.popup_connection_filter,
                        new String[] {"Title"},
                        new int[] {R.id.txt_header_pop});


                popupWindow.setAnchorView(view);
                popupWindow.setAdapter(adapter);
                popupWindow.setWidth(400);
                popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position){
                            case 1:
                                Log.e("ascending click","");
                                popupWindow.dismiss();
                                break;
                            case 2:
                                Log.e("descending click","");
                                popupWindow.dismiss();
                                break;
                            case 3:
                                Log.e("recently added click","");
                                popupWindow.dismiss();
                                break;
                            default:
                                break;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });

                    }
                });
                popupWindow.show();

            }
        });

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
                        index = index + 1;
                        req_connectionlist_API(context);

                        connection_adapter.notifyDataSetChanged();
                        connection_adapter.setLoaded();
                    }
                }, 5000);
            }
        });
    }

    private void setListAdapter() {

        if(arr_myconnection.size() == 0)
        {

            listView_Connection.setLayoutManager(new LinearLayoutManager(context));
            connection_adapter = new ConnectionAdapter(context, arr_myconnection, txt_no_of_connections);
            listView_Connection.setAdapter(connection_adapter);
        }
        else
        {
            connection_adapter.notifyDataSetChanged();
        }

    }


    private void initialiseUI()
    {
        TextView txt_sortby = (TextView) findViewById(R.id.txt_sort_by);
        txt_sortby.setTypeface(TextFont.opensans_regular(context));
        TextView txt_sort_by_name = (TextView) findViewById(R.id.txt_sort_by_name);
        txt_sort_by_name.setTypeface(TextFont.opensans_bold(context));

        img_sort_filter = (ImageView) findViewById(R.id.img_sort_filter);
        edt_search_Connections = (EditText) findViewById(R.id.edt_search_Connections);
        rel_search_Connections = (RelativeLayout) findViewById(R.id.rel_search_Connections);
        rel_search_Connections.setVisibility(View.GONE);
        listView_Connection = (RecyclerView) findViewById(R.id.listview_Connection);
        txt_no_of_connections = (TextView) findViewById(R.id.txt_no_of_connections);

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
                            txt_no_of_connections.setText("No Connection");
                            txt_no_of_connections.setVisibility(View.VISIBLE);
                            txt_no_internet_connection.setVisibility(View.GONE);
                            return;
                        }
                        else
                        {
                            if(myConnections_response.getConnections() != null)
                            {

                                ArrayList<Connections> connections = myConnections_response.getConnections();
                                arr_count_myconnection = connections.size();
                                for (int i = 0; i < connections.size(); i++)
                                {
                                    arr_myconnection.add(connections.get(i));
                                }
                                setListAdapter();
                                txt_no_of_connections.setVisibility(View.GONE);
                                txt_no_internet_connection.setVisibility(View.GONE);
                                CommonFunctions.hDialog();
                            }
                            else
                            {
                                arr_count_myconnection = 0;
                                txt_no_of_connections.setVisibility(View.GONE);
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
                            arr_count_myconnection = response.length();
                            for(int i = 0; i < response.length(); i++)
                            {
                                Connections connections = gson.fromJson(response.get(i).toString(),Connections.class);
                                arr_myconnection.add(connections);
                            }
                            setListAdapter();
                            txt_no_internet_connection.setVisibility(View.GONE);
                            txt_no_of_connections.setVisibility(View.GONE);
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
                            txt_no_of_connections.setVisibility(View.VISIBLE);
                            CommonFunctions.displayToast(context,context.getResources().getString(R.string.no_connection_found));
                            arr_count_myconnection = 0;

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

    @Override
    public void onBackPressed()
    {
        finish();
    }
}
