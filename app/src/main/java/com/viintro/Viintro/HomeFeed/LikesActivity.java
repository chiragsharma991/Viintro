package com.viintro.Viintro.HomeFeed;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.MyProfile.MyFollowersAdapter;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;

import java.util.ArrayList;

import static com.viintro.Viintro.R.id.txt_no_followers;
import static com.viintro.Viintro.R.id.txt_no_internet_connection;

public class LikesActivity extends AppCompatActivity {

    private TextView txt_no_internet_connection;
    public static LikesAdapter likesAdapter;
    private RecyclerView recycler_view_likes;
    private TextView txt_no_likes;
    private Context context;
    private String bearertoken;
    private Configuration_Parameter m_config;
    private SharedPreferences sharedPreferences;
    public static ArrayList arr_likes;
    public static int arr_count_mylikes = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);
        context = this;
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        bearertoken = sharedPreferences.getString(m_config.login_access_token,"");
        arr_likes = new ArrayList();
        TextView txt_Header = (TextView) findViewById(R.id.header);
        txt_Header.setText("Likes");
        txt_no_internet_connection = (TextView) findViewById(R.id.txt_no_internet_connection);

        CommonFunctions.sDialog(context,"Loading...");
        initialiseUI();
        if(!CommonFunctions.chkStatus(context))
        {
            CommonFunctions.hDialog();
            txt_no_internet_connection.setVisibility(View.VISIBLE);
            return;
        }

        setListAdapter();

    }

    private void setListAdapter() {

        if(arr_likes.size() == 0)
        {
            recycler_view_likes.setLayoutManager(new LinearLayoutManager(context));
            likesAdapter = new LikesAdapter(context, arr_likes, recycler_view_likes, txt_no_likes);
            recycler_view_likes.setAdapter(likesAdapter);
        }
        else
        {
            likesAdapter.notifyDataSetChanged();
        }

    }

    private void initialiseUI()
    {
        recycler_view_likes = (RecyclerView) findViewById(R.id.recycler_view_likes);
        txt_no_likes = (TextView) findViewById(R.id.txt_no_likes);

    }
}
