package com.viintro.Viintro;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cloudinary.Cloudinary;
import com.facebook.FacebookSdk;
import com.google.firebase.FirebaseApp;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.viintro.Viintro.Constants.Constcore.cloudinary;
import static com.viintro.Viintro.Constants.Constcore.config;

/**
 * Created by rkanawade on 31/01/17.
 */

public class ViintroApplication extends Application {

    Configuration_Parameter m_config;

    //
    public static final String TAG = ViintroApplication.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private static ViintroApplication mInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        mInstance = this;

        m_config = Configuration_Parameter.getInstance();
        m_config.os_version = getDeviceOSName();
        config = new HashMap();
        config.put("cloud_name", Constcore.cloud_name);
        config.put("api_key", Constcore.api_key);
        config.put("api_secret", Constcore.api_secret);
        cloudinary = new Cloudinary(Constcore.CLOUDINARY_URL);


    }

    public static synchronized ViintroApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue()
    {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(ViintroApplication.this);
    }

    public String getDeviceOSName()
    {

        String osname = "";
        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int fieldValue = -1;

            try {
                fieldValue = field.getInt(new Object());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
//
            if (fieldValue == Build.VERSION.SDK_INT) {
//                builder.append(" : ").append(fieldName).append(" : ");
//                builder.append("sdk=").append(fieldValue);
                Log.d("", "OS: " + fieldName);
                osname = fieldName;
            }

        }


        return osname;
    }
//http://res.cloudinary.com/dklb21dyh/video/upload/so_0,eo_10/lhcbzrcko2ed5rlgxfdc.mp4

}
