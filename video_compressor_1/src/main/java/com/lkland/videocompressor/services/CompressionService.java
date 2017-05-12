package com.lkland.videocompressor.services;


import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.lkland.util.Logger;
import com.lkland.videocompressor.compressor.CompressorFactory;
import com.lkland.videocompressor.compressor.ICompressor;
import com.lkland.videocompressor.compressor.QueuedFFmpegCompressor;
import com.lkland.videocompressor.responsehandler.OnProgressHandler;
import com.lkland.videocompressor.responsehandler.OnQueueHandler;
import com.lkland.videocompressor.responsemanager.IResponseManager;
import com.lkland.videocompressor.responsemanager.NotificationResponseManager;
import com.lkland.videocompressor.video.IVideo;
import com.lkland.videocompressor.video.VideoFactory;


/**
 * Created by david on 4/29/15.
 */


public class CompressionService extends AbstractCompressionService{
    public static final String TAG_ACTION = "action";

    public static final String TAG_DATA_INPUT_FILE_PATH = "input_file_path";
    public static final String TAG_DATA_OUTPUT_FILE_PATH = "output_file_path";
    public static final String TAG_DATA_OUTPUT_FILE_NAME = "output_file_name";
    public static final String TAG_DATA_OUTPUT_FILE_SIZE = "output_file_size";
    public static final String FLAG_ACTION_ADD_VIDEO = "action_add_video";
    private QueuedFFmpegCompressor _compressor;

    
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    @Override
    public void onCreate(){
        Logger.log("onCreate");
        _compressor = new QueuedFFmpegCompressor(CompressorFactory.defaultCompressor());

        configureCompressionResponse();
        super.onCreate();
    }
    
    private void configureCompressionResponse(){
    	OnProgressHandler prh = new OnProgressHandler();
        OnQueueHandler qrh = new OnQueueHandler();
    	IResponseManager nrm = new NotificationResponseManager(this);
        prh.addResponseManager(nrm);
        qrh.addResponseManager(nrm);
		//_compressor.setOnProgressListener(prh); Blocks display of progress bar in notification panel
		_compressor.setOnQueueListener(qrh);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        Logger.log("onstartcommand");
        Log.e("intent"," "+intent);
     
        if(intent==null)
        	return 0;
        String action = intent.getStringExtra(TAG_ACTION);
        switch(action){
            case FLAG_ACTION_ADD_VIDEO:
                String input_file_path = intent.getStringExtra(TAG_DATA_INPUT_FILE_PATH);
                String output_file_path = intent.getStringExtra(TAG_DATA_OUTPUT_FILE_PATH);
                String output_file_name = intent.getStringExtra(TAG_DATA_OUTPUT_FILE_NAME);
                String output_file_size = intent.getStringExtra(TAG_DATA_OUTPUT_FILE_SIZE);
                //addVideo(input_file_path, output_file_path, output_file_name, output_file_size);

                Log.e("FLAG_ACTION_ADD_VIDEO","");
                addVideo(input_file_path, output_file_path, output_file_name, output_file_size);
                break;
        }

        return Service.START_NOT_STICKY;//super.onStartCommand(intent, flags, this.START_STICKY);
    }


    
    @Override
    public ICompressor getCompressor(){
    	return this._compressor;
    }

    private void addVideo(String input_file_path, final String output_file_path, final String output_file_name, final String output_file_size){
    	IVideo video = VideoFactory.defaultVideo();
    	String inName = "";
    	Pattern p= Pattern.compile("(.*"+File.separator+")(.*)");
		Matcher mat = p.matcher(input_file_path);
		if(mat.matches()){
			inName = mat.group(2);
			input_file_path = mat.group(1);
		}
		video.setInName(inName);
        video.setInPath(input_file_path);
        video.setOutPath(output_file_path+File.separator);
        video.setOutName(output_file_name);
        video.setOutSize(output_file_size);
        _compressor.add(video);

    }


}
