package com.viintro.Viintro.Messages;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
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
import com.viintro.Viintro.Connections.ConnectionAdapter;
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

/**
 * Created by csuthar on 11/05/17.
 */

public class NewMessageActivity extends AppCompatActivity {

    private Context context;
    private Configuration_Parameter m_config;
    private SharedPreferences sharedPreferences;
    private NewMessageAdpter new_message_adapter;
    private static TextView txt_no_of_connections;
    private TextView txt_no_internet_connection;
    private String bearertoken;
    private int index = 0;
    public static ArrayList arr_connection_new_message;
    public static int arr_count_connection_new_message = 0;
    public static RecyclerView listView_NewMsgConnection;
    private MyConnections_Response myConnections_response;
    private LinearLayout SelectedUser_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_new_message);
        context = this;
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        bearertoken = sharedPreferences.getString(m_config.login_access_token,"");
        arr_connection_new_message = new ArrayList();
        arr_count_connection_new_message = 0;
        index = 0;
        TextView txt_header = (TextView) findViewById(R.id.header);
        txt_header.setText("New Message");
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
        req_new_message_connection_list_API(context);
       // setListAdapter();




    }

    private void setListAdapter() {
        Log.e("TAG", "setListAdapter: "+arr_connection_new_message.size() );

  //      if(arr_connection_new_message.size() == 0)
//        {

            listView_NewMsgConnection.setLayoutManager(new LinearLayoutManager(context));
            new_message_adapter = new NewMessageAdpter(context, arr_connection_new_message, txt_no_of_connections,SelectedUser_view);
            listView_NewMsgConnection.setAdapter(new_message_adapter);
        //}
       //else
       // {
          //  new_message_adapter.notifyDataSetChanged();
     //   }

        new_message_adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("connection", "Load More");
                arr_connection_new_message.add(null);
                new_message_adapter.notifyItemInserted(arr_connection_new_message.size() - 1);

                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //Remove loading item
                        arr_connection_new_message.remove(arr_connection_new_message.size() - 1);
                        new_message_adapter.notifyItemRemoved(arr_connection_new_message.size());
                        index = index + 1;
                        req_new_message_connection_list_API(context);

                        new_message_adapter.notifyDataSetChanged();
                        new_message_adapter.setLoaded();
                    }
                }, 5000);
            }
        });

    }


    private void initialiseUI()
    {
        listView_NewMsgConnection = (RecyclerView) findViewById(R.id.listView_NewMsgConnection);
        SelectedUser_view = (LinearLayout) findViewById(R.id.selectedUser_view);
        txt_no_of_connections = (TextView) findViewById(R.id.txt_no_of_connections);

    }

    public void req_new_message_connection_list_API(final Context context) {


        final Gson gson = new Gson();
        String url =  m_config.web_api_link+ "/connections?page="+index;//

        final JsonObjectRequest jsonarrayreq = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("", "req_connectionlist_API : " + " " + response);
                        Log.e("", "req_connectionlist_API response length : " + " " + response.length());

                         myConnections_response = gson.fromJson(response.toString(),MyConnections_Response.class);
                        if(myConnections_response.getConnections() == null && index == 0)
                        {
                            CommonFunctions.hDialog();
                            txt_no_of_connections.setVisibility(View.VISIBLE);
                            txt_no_internet_connection.setVisibility(View.GONE);
                            return;
                        }
                        else
                        {
                            if(myConnections_response.getConnections() != null)
                            {

                                ArrayList<Connections> connections = myConnections_response.getConnections();
                                arr_count_connection_new_message = connections.size();
                                for (int i = 0; i < connections.size(); i++)
                                {
                                    arr_connection_new_message.add(connections.get(i));
                                }
                                setListAdapter();
                                txt_no_of_connections.setVisibility(View.GONE);
                                txt_no_internet_connection.setVisibility(View.GONE);
                                CommonFunctions.hDialog();
                            }
                            else
                            {
                                arr_count_connection_new_message = 0;
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
        ViintroApplication.getInstance().addToRequestQueue(jsonarrayreq, "connection_new_msg_api");

    }


    @Override
    public void onBackPressed()
    {
        finish();
    }
}

