package com.viintro.Viintro.Messages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.Messages;
import com.viintro.Viintro.MyProfile.MyFollowingsActivity;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;
import com.viintro.Viintro.listener.OnLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.viintro.Viintro.HomeFeed.HomeActivity.img_settings;
import static com.viintro.Viintro.HomeFeed.HomeActivity.rel_search;

/**
 * Created by hasai on 11/01/17.
 **/
public class MessagesFragment extends Fragment
{

    private Context context;
    private Configuration_Parameter m_config;
    private SharedPreferences sharedPreferences;
    public static RecyclerView listview_Messages;
    private MessagesAdapter messages_adapter;
    private String bearertoken;
    private View v;
    private static TextView txt_no_internet_connection;
    private static TextView txt_no_of_messages;
    private Button btn_send_new_message;
    private EditText edt_search_messages;
    private int index = 0;
    public static ArrayList arr_messages;
    public static ArrayList<Messages> array_list;
    public static int arr_count_messages = 0;

    public static MessagesFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        MessagesFragment messagesFragment = new MessagesFragment();
        messagesFragment.setArguments(args);
        return messagesFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        bearertoken = sharedPreferences.getString(m_config.login_access_token,"");
        arr_messages = new ArrayList();
        array_list = new ArrayList();
        arr_count_messages = 0;
        index = 0;
        v = getView();
        TextView txt_Header = (TextView) v.findViewById(R.id.header);
        txt_Header.setText("Messages");
        txt_no_internet_connection = (TextView) v.findViewById(R.id.txt_no_internet_connection);

        CommonFunctions.sDialog(context,"Loading...");
        initialiseUI();
        if(!CommonFunctions.chkStatus(context))
        {
            CommonFunctions.hDialog();
            txt_no_internet_connection.setVisibility(View.VISIBLE);
            return;
        }
        req_messages_list_API(context);
        setMessagesListAdapter();

        edt_search_messages.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = edt_search_messages.getText().toString().toLowerCase(Locale.getDefault());
                Log.e("text"," "+text);
                messages_adapter.filter(text);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        messages_adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("messages", "Load More");
                arr_messages.add(null);
                messages_adapter.notifyItemInserted(arr_messages.size() - 1);

                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //Remove loading item
                        arr_messages.remove(arr_messages.size() - 1);
                        messages_adapter.notifyItemRemoved(arr_messages.size());

                        index = index + 1;
                        req_messages_list_API(context);

                        messages_adapter.notifyDataSetChanged();
                        messages_adapter.setLoaded();
                    }
                }, 5000);
            }
        });


    }


    private void setMessagesListAdapter() {

        if(arr_messages.size() == 0)
        {

            listview_Messages.setLayoutManager(new LinearLayoutManager(context));
            messages_adapter = new MessagesAdapter(context, arr_messages);
            listview_Messages.setAdapter(messages_adapter);
        }
        else
        {
            messages_adapter.notifyDataSetChanged();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View connection_view = inflater.inflate(R.layout.fragment_messages, container, false);
        rel_search.setVisibility(View.GONE);
        img_settings.setVisibility(View.GONE);
        return connection_view;
    }

    private void initialiseUI()
    {
        listview_Messages = (RecyclerView) v.findViewById(R.id.listview_Messages);
        edt_search_messages = (EditText) v.findViewById(R.id.edt_search_messages);
        txt_no_of_messages = (TextView) v.findViewById(R.id.txt_no_of_messages);
        btn_send_new_message = (Button) v.findViewById(R.id.btn_send_new_message);

        btn_send_new_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(context, NewMessageActivity.class);
                startActivity(i);
            }
        });



    }


    public void req_messages_list_API(final Context context)
    {

        final Gson gson = new Gson();
        String url =  m_config.web_api_link_header+ "/messages/list";

        final JsonArrayRequest jsonarrayreq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("", "req_messages_list_API : " + " " + response);

                        try
                        {
                            if (response.equals(null) || response == null || response.length() == 0 && index == 0)
                            {
                                txt_no_of_messages.setText("No Messages");
                                txt_no_of_messages.setVisibility(View.VISIBLE);
                                txt_no_internet_connection.setVisibility(View.GONE);
                                CommonFunctions.hDialog();
                            }
                            else
                            {
                                if (response.equals(null) || response == null || response.length() == 0)
                                {
                                    arr_count_messages = 0;
                                    txt_no_of_messages.setVisibility(View.GONE);
                                    txt_no_internet_connection.setVisibility(View.GONE);
                                }
                                else
                                {
                                    arr_count_messages = response.length();
                                    for (int i = 0; i < response.length(); i++) {
                                        Messages messages = gson.fromJson(response.get(i).toString(), Messages.class);
                                        arr_messages.add(messages);
                                        array_list.add(messages);

                                    }
                                    setMessagesListAdapter();
                                    CommonFunctions.hDialog();
                                    txt_no_of_messages.setVisibility(View.GONE);
                                    txt_no_internet_connection.setVisibility(View.GONE);
                                }
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                            CommonFunctions.hDialog();
                            txt_no_of_messages.setVisibility(View.GONE);
                            txt_no_internet_connection.setVisibility(View.GONE);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        CommonFunctions.hDialog();
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
                                txt_no_of_messages.setText("No Messages");
                                txt_no_of_messages.setVisibility(View.VISIBLE);
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


}