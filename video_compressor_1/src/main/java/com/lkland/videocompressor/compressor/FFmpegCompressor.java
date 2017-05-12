package com.lkland.videocompressor.compressor;

import java.util.ArrayList;

import android.util.Log;

import com.lkland.util.Logger;
import com.lkland.videocompressor.nativeadapter.FFmpegAdapterFactory;
import com.lkland.videocompressor.nativeadapter.IFFmpegAdapter;
import com.lkland.videocompressor.video.IVideo;


public class FFmpegCompressor implements ICompressor{

    IFFmpegAdapter mFFmpegAdapter;
    private ICompressor.OnProgressListener mListener;
    private IFFmpegAdapter.OnMessageListener mOnMessageListener;
    private IVideo mCurrentVideo;
    public FFmpegCompressor(){
        mCurrentVideo = null;
        mListener = null;
        mFFmpegAdapter = FFmpegAdapterFactory.defaultFFmpegAdapter();
        mOnMessageListener = new IFFmpegAdapter.OnMessageListener() {
            @Override
            public void onMessage(String str) {
                if(mListener != null){
                    mListener.onProgress(FFmpegCompressor.this.getCurrent(),str+" totalDuration="+mCurrentVideo.getDuration()+" ");
                }
            }
        };
        mFFmpegAdapter.setOnMessageListener(mOnMessageListener);
    }

    private int calBitrate(int duration, int targetSize){
        targetSize = targetSize*1024*1024;
        int audiosize = (120*1000/8)*duration;
        return (targetSize-audiosize)*8/duration;
    }

    @Override
    public void compressVideo(IVideo v) {

        try {
//	        	android.os.Debug.waitForDebugger();
            if(!mFFmpegAdapter.isDecoderAva(v.getInPath()+v.getInName())){
                return;
            }
            int duration = mFFmpegAdapter.getDuration(v.getInPath()+v.getInName());


            //Log.e("duration"," "+duration);
            v.setDuration(duration);
            this.mCurrentVideo = v;
            //Log.e("getOutSize"," "+Integer.parseInt(v.getOutSize()));

            int bitrate = 500000;//calBitrate(duration/1000000,Integer.parseInt(v.getOutSize()));
            Log.d(" bitrate ",bitrate+"");
            ArrayList<String> paras = new ArrayList<String>();
            paras.add("ffmpeg");

            paras.add("-i");
            paras.add(v.getInPath()+v.getInName());

            paras.add("-vcodec");
            paras.add("libx264");

            paras.add("-strict");
            paras.add("-2");

            paras.add("-acodec");
            paras.add("aac");

            paras.add("-s");
            paras.add("640x480");

            paras.add("-preset");
            paras.add("superfast");

            paras.add("-r");
            paras.add("24");

            paras.add("-b:v");
            paras.add(bitrate+"");

            paras.add("-b:a");
            paras.add("64K");

            paras.add("-maxrate");
            paras.add(bitrate+"");

            paras.add("-minrate");
            paras.add(bitrate+"");

            paras.add("-bufsize");
            paras.add(bitrate*3+"");

            //paras.add(v.getOutPath()+v.getOutName()+".mp4");

            paras.add(v.getOutPath()+v.getOutName());
            String str = "";

            for(int i=0;i<paras.size();i++)
            {
                str+=paras.get(i)+" ";
            }
            Logger.log(str);
            mFFmpegAdapter.compressVideo(paras.toArray(new String[paras.size()]));
            Logger.log("done");
            this.mCurrentVideo = null;
            mListener.onFinished();

        }
        catch (Exception e) {
            Logger.log(e);
        }
    }


    @Override
    public void setOnProgressListener(OnProgressListener lis) {
        this.mListener = lis;
    }

    @Override
    public void removeOnProgressListener() {
        this.mListener = null;
    }

    @Override
    public IVideo getCurrent() {
        return this.mCurrentVideo;
    }

    @Override
    public OnProgressListener getOnProgressListener() {
        return this.mListener;
    }
}

