package com.lalongooo.videocompressor;/*
* By Jorge E. Hernandez (@lalongooo) 2015
* */

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.lalongooo.videocompressor.file.FileUtils;

public class VideoCompressorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FileUtils.createApplicationFolder();

    }

}