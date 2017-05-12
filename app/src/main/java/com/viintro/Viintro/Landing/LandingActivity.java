package com.viintro.Viintro.Landing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import com.crashlytics.android.Crashlytics;
import com.viintro.Viintro.Login.LoginActivity;
import com.viintro.Viintro.Login.RegistrationActivity;
import com.viintro.Viintro.R;
import io.fabric.sdk.android.Fabric;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
/**
 * Created by rkanawade on 25/01/17.
 */

public class LandingActivity extends Activity {

    Context context = LandingActivity.this;
    ArrayList myList = new ArrayList();
    String otp = "OTP";
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
                    "http://res.cloudinary.com/dklb21dyh/video/upload/v1484564303/lhcbzrcko2ed5rlgxfdc.mp4",
                    "http://res.cloudinary.com/dklb21dyh/video/upload/v1484569652/vx02kfwrndyjmtrreoh8.mp4",
                    "http://res.cloudinary.com/dklb21dyh/video/upload/v1484564303/lhcbzrcko2ed5rlgxfdc.mp4",
                    "http://res.cloudinary.com/dklb21dyh/video/upload/v1484564303/lhcbzrcko2ed5rlgxfdc.mp4",
                    "http://res.cloudinary.com/dklb21dyh/video/upload/v1484569652/vx02kfwrndyjmtrreoh8.mp4",
                    "http://res.cloudinary.com/dklb21dyh/video/upload/v1484564303/lhcbzrcko2ed5rlgxfdc.mp4"
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_landing);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }

        //call to pagerAdapter class
        MyPagerAdapter adapter = new MyPagerAdapter();
        ViewPager myPager = (ViewPager) findViewById(R.id.viewpager);
        myPager.setAdapter(adapter);
        myPager.setCurrentItem(0);

        SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mySPrefs.edit();
        editor.remove(otp);
        editor.apply();

    }

    private class MyPagerAdapter extends PagerAdapter {

        public int getCount() {
            return 3;
        }

        public Object instantiateItem(View collection, int position) {

            View view=null;

            LayoutInflater inflater = (LayoutInflater) collection.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            int resId = 0;
            //loading different layouts
            switch (position) {
                case 0:
                    resId = R.layout.activity_landing_one;
                    view = inflater.inflate(resId, null);
                    Button btnLogin = (Button) view.findViewById(R.id.btnLogin);
                    btnLogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent login = new Intent(LandingActivity.this, LoginActivity.class);
                            startActivity(login);
                        }
                    });

                    Button textSignUp = (Button) view.findViewById(R.id.textSignUp);
                    textSignUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent signup = new Intent(LandingActivity.this, RegistrationActivity.class);
                            startActivity(signup);
                        }
                    });
                    break;

                case 1:
                    resId = R.layout.activity_landing_two;
                    view = inflater.inflate(resId, null);
                    Button btnLogin1 = (Button) view.findViewById(R.id.btnLogin);
                    btnLogin1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent login = new Intent(LandingActivity.this, LoginActivity.class);
                            startActivity(login);
                        }
                    });

                    Button textSignUp1 = (Button) view.findViewById(R.id.textSignUp);
                    textSignUp1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent signup = new Intent(LandingActivity.this, RegistrationActivity.class);
                            startActivity(signup);
                        }
                    });
                    break;

                case 2:
                    resId = R.layout.activity_landing_three;
                    view = inflater.inflate(resId, null);
                    Button btnLogin2 = (Button) view.findViewById(R.id.btnLogin);
                    btnLogin2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent login = new Intent(LandingActivity.this, LoginActivity.class);
                            startActivity(login);
                        }
                    });

                    Button textSignUp2 = (Button) view.findViewById(R.id.textSignUp);
                    textSignUp2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent signup = new Intent(LandingActivity.this, RegistrationActivity.class);
                            startActivity(signup);
                        }
                    });
                    GridView list = (GridView) view.findViewById(R.id.videoGridview);
                    myList.clear();
                    getDataInList();
                    GridviewAdapter memoListAdapter = new GridviewAdapter(context, myList);
                    list.setAdapter(memoListAdapter);


                    // memoListAdapter.notifyDataSetChanged();

                    break;
            }

            ((ViewPager) collection).addView(view, 0);

            return view;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);

        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);

        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

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
