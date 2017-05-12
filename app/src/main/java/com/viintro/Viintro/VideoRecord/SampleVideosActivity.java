package com.viintro.Viintro.VideoRecord;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;

import com.viintro.Viintro.Landing.GridviewAdapter;
import com.viintro.Viintro.Landing.LandingActivity;
import com.viintro.Viintro.Landing.SampleVideosData;
import com.viintro.Viintro.R;

import java.util.ArrayList;

public class SampleVideosActivity extends AppCompatActivity {

    Context context = this;
    ArrayList myList = new ArrayList();
    String[] names = new String[]
            {
                    "Sample video name 1",
                    "Sample video name 2",
                    "Sample video name 3",
                    "Sample video name 4",
                    "Sample video name 5",
                    "Sample video name 6"

            };

    String[] url = new String[]
            {
                    "http://res.cloudinary.com/dklb21dyh/video/upload/v1484640615/iy06xoitedcqvrf2r1gs.mp4",
                    "http://res.cloudinary.com/dklb21dyh/video/upload/v1484569652/vx02kfwrndyjmtrreoh8.mp4",
                    "http://res.cloudinary.com/dklb21dyh/video/upload/v1484564303/lhcbzrcko2ed5rlgxfdc.mp4",
                    "http://res.cloudinary.com/dklb21dyh/video/upload/v1484640615/iy06xoitedcqvrf2r1gs.mp4",
                    "http://res.cloudinary.com/dklb21dyh/video/upload/v1484569652/vx02kfwrndyjmtrreoh8.mp4",
                    "http://res.cloudinary.com/dklb21dyh/video/upload/v1484564303/lhcbzrcko2ed5rlgxfdc.mp4"

            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sample_videos);

        GridView list = (GridView)findViewById(R.id.videoGridview);
        myList.clear();
        getDataInList();
        GridviewAdapter memoListAdapter = new GridviewAdapter(context, myList);
        list.setAdapter(memoListAdapter);
    }

    //add data in arraylist
    private void getDataInList(){
        for (int i = 0; i < names.length; i++) {
            // Create a new object for each list item
            SampleVideosData ld = new SampleVideosData();
            ld.setNames(names[i]);
            ld.setUrl(url[i]);
            // Add this object into the ArrayList myList
            myList.add(ld);
        }
    }
}
