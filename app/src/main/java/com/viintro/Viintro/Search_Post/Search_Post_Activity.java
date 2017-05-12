package com.viintro.Viintro.Search_Post;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.viintro.Viintro.Model.People;
import com.viintro.Viintro.Model.Post;
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

/**
 * Created by hasai on 20/03/17.
 */

public class Search_Post_Activity extends AppCompatActivity
{
    Context context;
    private Configuration_Parameter m_config;
    private SharedPreferences sharedPreferences;
    ArrayList arr_people;
    private TextView txt_no_internet_connection;
    private String bearertoken;
    private PostAdapter post_adapter;
    private PeopleAdapter people_adapter;
    private EditText edt_search_post_people;
    private ImageView img_back;
    private Button btn_people, btn_post;

    public static ArrayList arr_post;
    public static RecyclerView listView_People;
    public static RecyclerView listView_Post;
    private TextView txt_no_post;
    private String selectedVal;

    // variables for lazy loading(pagination)
    private int index = 0;
    public static int arr_count_people = 0;
    public static int arr_count_post = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_search_post);
        context = this;
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        bearertoken = sharedPreferences.getString(m_config.login_access_token,"");
        arr_people = new ArrayList();
        arr_post = new ArrayList();
        txt_no_internet_connection = (TextView) findViewById(R.id.txt_no_internet_connection);
        arr_count_people = 0;
        arr_count_post = 0;
        initialiseUI();
        if(!CommonFunctions.chkStatus(context))
        {
            txt_no_internet_connection.setVisibility(View.VISIBLE);
            return;
        }


        btn_people.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                peopleclick();
            }
        });

        btn_post.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                postclick();
            }
        });

        people_adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("people ", "Load More"+" "+arr_people.size());
                arr_people.add(null);
                people_adapter.notifyItemInserted(arr_people.size() - 1);

                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("people run", " "+" "+arr_people.size());
                        try {
                            //Remove loading item
                            arr_people.remove(arr_people.size() - 1);
                            people_adapter.notifyItemRemoved(arr_people.size());
                            index = index + 1;
                            req_search_people(context);

                            //people_adapter.notifyDataSetChanged();
                            people_adapter.setLoaded();
                        }
                        catch (Exception e)
                        {

                        }
                    }
                }, 5000);
            }
        });


        post_adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("post ", "Load More"+" "+arr_post.size());
                arr_post.add(null);
                post_adapter.notifyItemInserted(arr_post.size() - 1);

                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("post run", " "+" "+arr_post.size());
                        try {
                            //Remove loading item
                            arr_post.remove(arr_post.size() - 1);
                            post_adapter.notifyItemRemoved(arr_post.size());
                            index = index + 1;
                            req_search_post(context);

                            //post_adapter.notifyDataSetChanged();
                            post_adapter.setLoaded();
                        }
                        catch (Exception e)
                        {

                        }
                    }
                }, 5000);
            }
        });



    }


    private void initialiseUI()
    {
        img_back = (ImageView) findViewById(R.id.img_back);
        edt_search_post_people = (EditText) findViewById(R.id.edt_search_post_people);
        btn_people = (Button) findViewById(R.id.btn_people);
        btn_people.setTextColor(R.color.grey);
        btn_people.setBackgroundResource(R.drawable.border_round_left_selected);
        btn_post = (Button) findViewById(R.id.btn_post);
        listView_People = (RecyclerView) findViewById(R.id.listview_People);
        listView_Post = (RecyclerView) findViewById(R.id.listview_Post);
        txt_no_post = (TextView) findViewById(R.id.txt_no_post);
        selectedVal = "People";
        setPeopleAdapter();
        setPostAdapter();

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              onBackPressed();
            }
        });

        edt_search_post_people.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE) || (actionId == EditorInfo.IME_ACTION_SEARCH))
                {
//
                    if(!edt_search_post_people.getText().toString().replaceAll("\\s+", " ").trim().equals(""))
                    {
                        Log.e("selectedValue "," "+selectedVal);
                        if(selectedVal.equalsIgnoreCase("People"))
                        {
                            arr_count_people = 0;
                            arr_people = new ArrayList();
                            setPeopleAdapter();
                            CommonFunctions.sDialog(context,"Loading...");
                            req_search_people(context);
                        }
                        else if(selectedVal.equalsIgnoreCase("Post"))
                        {
                            arr_count_post = 0;
                            arr_post = new ArrayList();
                            setPostAdapter();
                            CommonFunctions.sDialog(context,"Loading...");
                            req_search_post(context);
                        }
                    }
                    handled = true;

                }
                return handled;
            }
        });


        edt_search_post_people.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("length "," "+charSequence.length());

                Log.e("selectedValue "," "+selectedVal);
                if(selectedVal.equalsIgnoreCase("People"))
                {
                    arr_count_people = 0;
                    arr_people = new ArrayList();
                    setPeopleAdapter();
                    CommonFunctions.sDialog(context,"Loading...");
                    req_search_people(context);
                }
                else if(selectedVal.equalsIgnoreCase("Post"))
                {
                    arr_count_post = 0;
                    arr_post = new ArrayList();
                    setPostAdapter();
                    CommonFunctions.sDialog(context,"Loading...");
                    req_search_post(context);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



    }

    public void peopleclick()
    {
        if(!selectedVal.equals("People"))
        {
            Log.e("here"," 2");

            btn_people.setTextColor(R.color.grey);
            btn_people.setBackgroundResource(R.drawable.border_round_left_selected);
            btn_post.setBackgroundResource(R.drawable.border_round_right);
            btn_post.setTextColor(Color.WHITE);
            selectedVal = "People";
            listView_Post.setVisibility(View.GONE);
            listView_People.setVisibility(View.VISIBLE);
            txt_no_post.setVisibility(View.GONE);
            arr_people = new ArrayList();
            arr_post = new ArrayList();
            arr_count_people = 0;
            arr_count_post = 0;
            setPeopleAdapter();
            setPostAdapter();
            if(!CommonFunctions.chkStatus(context))
            {
                txt_no_internet_connection.setVisibility(View.VISIBLE);
                return;
            }
            index = 0;
            CommonFunctions.sDialog(context,"Loading...");
            req_search_people(context);
        }
    }

    public void postclick()
    {
        if(!selectedVal.equals("Post"))
        {
            btn_post.setTextColor(R.color.grey);
            btn_post.setBackgroundResource(R.drawable.border_round_right_selected);
            btn_people.setBackgroundResource(R.drawable.border_round_left);
            btn_people.setTextColor(Color.WHITE);
            selectedVal = "Post";
            listView_People.setVisibility(View.GONE);
            listView_Post.setVisibility(View.VISIBLE);
            txt_no_post.setVisibility(View.GONE);
            arr_post = new ArrayList();
            arr_people = new ArrayList();
            arr_count_people = 0;
            arr_count_post = 0;
            setPostAdapter();
            setPeopleAdapter();
            if(!CommonFunctions.chkStatus(context))
            {
                txt_no_internet_connection.setVisibility(View.VISIBLE);
                return;
            }
            index = 0;
            CommonFunctions.sDialog(context,"Loading...");
            req_search_post(context);

        }
    }

    private void setPeopleAdapter() {
        if(arr_people.size() == 0)
        {
            listView_People.setLayoutManager(new LinearLayoutManager(context));
            people_adapter = new PeopleAdapter(context, arr_people);
            listView_People.setAdapter(people_adapter);
        }
        else
        {
            people_adapter.notifyDataSetChanged();
        }
    }

    private void setPostAdapter() {
        if(arr_post.size() == 0)
        {
            listView_Post.setLayoutManager(new LinearLayoutManager(context));
            post_adapter = new PostAdapter(context, arr_post);
            listView_Post.setAdapter(post_adapter);
        }
        else
        {
            post_adapter.notifyDataSetChanged();
        }
    }


    public void req_search_people(final Context context) {

        final Gson gson = new Gson();
        String url =  m_config.web_api_link_header+ "/search/people?search="+edt_search_post_people.getText().toString().trim()+"&page="+index;//

        final JsonArrayRequest jsonarrayreq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("", "req_search_people : " + " " + response);

                        try
                        {
                            arr_count_people = response.length();
                            Log.e("arr_count_people"," "+arr_count_people);
                            for(int i = 0; i < response.length(); i++)
                            {
                                People searchPeople = gson.fromJson(response.get(i).toString(), People.class);
                                arr_people.add(searchPeople);
                            }


                            txt_no_internet_connection.setVisibility(View.GONE);
                            txt_no_post.setVisibility(View.GONE);
                            setPeopleAdapter();
                            CommonFunctions.hDialog();


                        }
                        catch (JSONException e) {
                            e.printStackTrace();
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
                        } else {

                            if(index == 0)
                            {
                                txt_no_post.setVisibility(View.VISIBLE);
                                txt_no_post.setText("No People Found");

                            }
                            else
                            {
                                // if data is null and index count is greater than 0
                                arr_count_people = 0;
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
                params.put("devicetype", "android");
                params.put("timezone:IST", CommonFunctions.getTimeZoneIST());
                params.put("Authorization","Bearer "+bearertoken);
                params.put("timesstamp",CommonFunctions.getTimeStamp());
                params.put("os_version",m_config.os_version);
                return params;
            }
        };
        int socketTimeout = 60000;//5 seconds

        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonarrayreq.setRetryPolicy(policy);
        ViintroApplication.getInstance().addToRequestQueue(jsonarrayreq, "search_people_api");

    }


    public void req_search_post(final Context context) {

        // Instantiate the RequestQueue.
        final Gson gson = new Gson();
        String url =  m_config.web_api_link_header+ "/search/posts?search="+edt_search_post_people.getText().toString().trim()+"&page="+index;//

        final JsonArrayRequest jsonarrayreq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("", "req_search_post : " + " " + response);
                        try {

                                arr_count_post = response.length();
                                for (int i = 0; i < response.length(); i++) {

                                    Post searchPost = gson.fromJson(response.get(i).toString(), Post.class);
                                    arr_post.add(searchPost);

                                }

                                setPostAdapter();
                                txt_no_internet_connection.setVisibility(View.GONE);
                                txt_no_post.setVisibility(View.GONE);
                                CommonFunctions.hDialog();

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
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
                        } else {

                            if(index == 0)
                            {
                                txt_no_post.setVisibility(View.VISIBLE);
                                txt_no_post.setText("No Post Found");

                            }
                            else
                            {
                                // if data is null and index count is greater than 0
                                arr_count_post = 0;
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
                params.put("devicetype", "android");
                params.put("timezone:IST", CommonFunctions.getTimeZoneIST());
                params.put("Authorization","Bearer "+bearertoken);
                params.put("timesstamp",CommonFunctions.getTimeStamp());
                params.put("os_version",m_config.os_version);
                return params;
            }
        };
        int socketTimeout = 60000;//5 seconds

        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonarrayreq.setRetryPolicy(policy);
        ViintroApplication.getInstance().addToRequestQueue(jsonarrayreq, "search_post_people_api");

    }



    @Override
    public void onBackPressed() {
        finish();
    }
}
