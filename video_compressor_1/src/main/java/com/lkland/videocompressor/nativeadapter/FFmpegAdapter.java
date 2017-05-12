package com.lkland.videocompressor.nativeadapter;

import android.util.Log;

import com.lkland.util.Logger;


public class FFmpegAdapter implements IFFmpegAdapter{
	private OnMessageListener mListener;
	
	public FFmpegAdapter(){
		this.mListener = null;
	}
	
	public FFmpegAdapter(OnMessageListener lis){
		this.mListener = lis;
	}
	
	public void messageme(String str){
		if(this.mListener!=null){
			this.mListener.onMessage(str);
		}
	}
	
	@Override
	public void setOnMessageListener(OnMessageListener lis) {
		this.mListener = lis;
	}

	@Override
	public void removeOnMessageListener() {
		this.mListener = null;		
	}
	
	@Override
	public int getDuration(String fileName) {
		int ret = ffmpegGetDuration(fileName);
		return ret;
	}

	@Override
	public boolean isDecoderAva(String fileName) {
		int ret = ffmpegIsDecoderAva(fileName);
		return ret==1;
	}

	@Override
	public void compressVideo(String[] params) {
		compress(params);
	}
	
	private native int ffmpegIsDecoderAva(String fileName);
	
	private native int ffmpegGetDuration(String fileName);

    private native void compress(String[] paras);

    static {
		Logger.log("Load Library");
		System.loadLibrary("Compressor");
	}


}
