package com.lkland;

import android.app.ProgressDialog;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rKanawade on 24/01/2017.
 */
//Singleton pattern class. Variables used across multiple activities are used here
public class Configuration_Parameter
{
    private static Configuration_Parameter myObj;
    public CompressionCompleted compressionCompleted;

    /**
     * Create private constructor
     */
    private Configuration_Parameter()
    {
        //
    }

    /**
     * Create a static method to get instance.
     */
    public static Configuration_Parameter getInstance()
    {
        if(myObj == null)
        {
            myObj = new Configuration_Parameter();
        }
        return myObj;
    }
}
