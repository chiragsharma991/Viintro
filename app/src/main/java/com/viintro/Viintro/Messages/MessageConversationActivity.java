package com.viintro.Viintro.Messages;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.viintro.Viintro.Model.MessageDetails;
import com.viintro.Viintro.Model.Messages;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;
import com.viintro.Viintro.Webservices.MarkLastMessageAsReadAPi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hasai on 24/04/17.
 */



public class MessageConversationActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{

    private Context context;
    private Configuration_Parameter m_config;
    private SharedPreferences sharedPreferences;
    public static ListView listview_Message_Conversation;
    private MessageConversationAdapter adapter;
    private String bearertoken;
    private View v;
    private int index = 0;
    public static ArrayList arr_messages_conversation;
    private TextView txt_no_internet_connection, txt_no_messages;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText edt_message;
    private Button btn_Send;
    private int group_id;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_message_conversation);
        context = this;
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        bearertoken = sharedPreferences.getString(m_config.login_access_token,"");
        arr_messages_conversation = new ArrayList();
        index = 0;
        group_id = getIntent().getExtras().getInt("group_id");
        txt_no_internet_connection = (TextView) findViewById(R.id.txt_no_internet_connection);
        TextView txt_header = (TextView) findViewById(R.id.header);
        txt_header.setText(getIntent().getExtras().getString("name"));
        RelativeLayout img_back = (RelativeLayout) findViewById(R.id.img_back);

        img_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        RelativeLayout btn_More = (RelativeLayout) findViewById(R.id.btn_More);
        btn_More.setVisibility(View.VISIBLE);
        btn_More.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

            }
        });

        CommonFunctions.sDialog(context,"Loading...");
        initialiseUI();
        if(!CommonFunctions.chkStatus(context))
        {
            CommonFunctions.hDialog();
            txt_no_internet_connection.setVisibility(View.VISIBLE);
            return;
        }
        req_groupmessage_API(context,"load page");
        setAdapter();
    }

    private void initialiseUI()
    {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        listview_Message_Conversation = (ListView) findViewById(R.id.listview_Message_Conversation);
        listview_Message_Conversation.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listview_Message_Conversation.setStackFromBottom(true);
        txt_no_messages = (TextView) findViewById(R.id.txt_no_messages);
        edt_message = (EditText) findViewById(R.id.edt_message);
        btn_Send = (Button) findViewById(R.id.btn_Send);

        edt_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(edt_message.getText().toString().trim().length() == 0)
                {
                    btn_Send.setEnabled(false);
                }
                else
                {
                    btn_Send.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btn_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                req_reply_message_API(context);
            }
        });


    }

    private void setAdapter() {

        if(arr_messages_conversation.size() == 0)
        {

            adapter = new MessageConversationAdapter(context, arr_messages_conversation);
            listview_Message_Conversation.setAdapter(adapter);
            swipeRefreshLayout.setOnRefreshListener(this);
        }
        else
        {
            adapter.notifyDataSetChanged();
        }

    }

    public void req_groupmessage_API(final Context context, final String fromwhere)
    {

        final Gson gson = new Gson();
        String url =  m_config.web_api_link_header+ "/messages/group/"+group_id+"?page="+index;
        Log.e("req_groupmessage_API"," "+url);

        final JsonObjectRequest jsonarrayreq = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.e("", "req_messages_list_API : " + " " + response);

                        try
                        {
                            if (response.equals(null) || response == null || response.length() == 0 && index == 0)
                            {

                                txt_no_messages.setVisibility(View.VISIBLE);
                                txt_no_internet_connection.setVisibility(View.GONE);
                                CommonFunctions.hDialog();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            else
                            {
                                if (response.equals(null) || response == null || response.length() == 0)
                                {

                                    txt_no_messages.setVisibility(View.GONE);
                                    txt_no_internet_connection.setVisibility(View.GONE);
                                    CommonFunctions.hDialog();
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                                else
                                {

                                    MessageDetails messageDetails = gson.fromJson(response.toString(), MessageDetails.class);
                                    Boolean flag_read = getIntent().getExtras().getBoolean("flag_read");
                                    int position = getIntent().getExtras().getInt("position");
                                    for(int i = 0; i < messageDetails.getMessages().size(); i++)
                                    {
                                        Messages messages = messageDetails.getMessages().get(i);

                                        // Last message as read //
                                        if(index == 0 && i == 0)
                                        {
                                            if(!flag_read)
                                            {
                                                MarkLastMessageAsReadAPi.req_mark_last_message_API(context, group_id, messages.getMessage_id(), position);
                                            }
                                        }
                                        //**//

                                        arr_messages_conversation.add(0,messages);

                                    }

                                    setAdapter();
                                    CommonFunctions.hDialog();
                                    txt_no_messages.setVisibility(View.GONE);
                                    txt_no_internet_connection.setVisibility(View.GONE);
                                    if(fromwhere.equals("load page"))
                                    {
                                        scrollDown();
                                    }
                                    else
                                    {

                                    }

                                    swipeRefreshLayout.setRefreshing(false);


                                }


                            }
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                            CommonFunctions.hDialog();
                            txt_no_messages.setVisibility(View.GONE);
                            txt_no_internet_connection.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        CommonFunctions.hDialog();
                        swipeRefreshLayout.setRefreshing(false);
                        if (error.networkResponse != null)
                        {

                            String networkresponse_data = new String(error.networkResponse.data);
                            if(error.networkResponse.statusCode == 500)
                            {
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
                                            CommonFunctions.displayToast(context, network_obj.getString("message"));
                                        }

                                    } catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                        else {
                            if(index == 0)
                            {

                                txt_no_messages.setVisibility(View.VISIBLE);
                                txt_no_internet_connection.setVisibility(View.GONE);
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
        //queue.add(jsonarrayreq);

        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(jsonarrayreq, "messages_api");

    }

    @Override
    public void onRefresh() {
        index = index + 1;
        req_groupmessage_API(context, "load page");
    }

    private void scrollDown()
    {
        listview_Message_Conversation.setSelection(listview_Message_Conversation.getCount() - 1);
    }



    //api call to reply  message
    public void req_reply_message_API(final Context cont)
    {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String bearertoken = sharedPreferences.getString(m_config.login_access_token,"");

        String url = m_config.web_api_link_header+ "/messages/reply";
        final String replymessage = edt_message.getText().toString().trim();

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("client_id", Constcore.client_Id);
            obj.put("client_secret", Constcore.client_Secret);
            obj.put("group_id", group_id);
            obj.put("type", "text");
            obj.put("text", replymessage);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response

                        Log.d("Response", response.toString());
                        int code = -1;
                        try {
                            Log.e("response code"," "+response.getInt("code"));
                            code = response.getInt("code");
                            if(code != -1)
                            {
                                CommonFunctions.hDialog();
                                CommonFunctions.displayToast(context,response.getString("message"));
                            }
                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        CommonFunctions.hDialog();
                        try
                        {
                            Messages messages = new Messages();
                            messages.setText(replymessage);
                            messages.setSender_id(sharedPreferences.getInt(m_config.login_id,0));
                            messages.setTime_ago(response.getString("time_ago"));
                            arr_messages_conversation.add(messages);
                            adapter.notifyDataSetChanged();
                            scrollDown();
                            edt_message.setText("");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        CommonFunctions.hDialog();
                        if(error.networkResponse != null)
                        {
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

                            CommonFunctions.displayToast(context,"Message send failed");
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
        ViintroApplication.getInstance().addToRequestQueue(getRequest, "send_message_api");



    }
}
