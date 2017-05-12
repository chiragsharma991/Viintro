package com.lkland.videocompressor.compressor;

import android.content.Context;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.Iterator;

import com.lkland.CompressionCompleted;
import com.lkland.Configuration_Parameter;
import com.lkland.util.Logger;
import com.lkland.videocompressor.services.CompressionService;
import com.lkland.videocompressor.video.IVideo;
import com.lkland.videocompressor.workqueue.IQueueable;


public class QueuedFFmpegCompressor implements ICompressor ,IQueueable<IVideo>{
	private ArrayDeque<IVideo> mQueue;
	private ICompressor mCompressor;
	private OnQueueListener<IVideo> mOnQueueListener;
	private static final int STATUS_NOTSTARTED = 0;
	private static final int STATUS_COMPRESSING = 1;
	private int status;
    Configuration_Parameter m_config;


    public QueuedFFmpegCompressor(ICompressor compressor){
		this.status = 0;
		this.mCompressor = compressor;
		this.mQueue = new ArrayDeque<IVideo>();
		mOnQueueListener = null;
        m_config = Configuration_Parameter.getInstance();

	}

    public QueuedFFmpegCompressor(CompressionCompleted compressionComplete) {

        m_config = Configuration_Parameter.getInstance();
        Log.e("compressionComplete"," "+compressionComplete);
        m_config.compressionCompleted = compressionComplete;
    }

    @Override
	public void add(IVideo v){
		mQueue.add(v);		
		if(mOnQueueListener !=null)
			mOnQueueListener.onAdd(v);
		Logger.log("Video added"+v.getInPath());
		Logger.log("Video Count:"+ mQueue.size());
		start();
	}

	@Override
	public IVideo remove(IVideo v) {
		mQueue.remove(v);
		if(mOnQueueListener !=null)
			mOnQueueListener.onRemove(v);
		return v;
	}
	
	@Override
	public void start(){
		if(status ==STATUS_COMPRESSING ) return;
		status = STATUS_COMPRESSING;
		
	   	new Thread(new Runnable(){
			@Override
			public void run() {
				//Logger.log("Begin to compressor");
				if(mOnQueueListener !=null)
					mOnQueueListener.onQueueStart();
				while(mQueue.size()>0){
					IVideo v = mQueue.pop();
					mOnQueueListener.onPop(v);
					//Logger.log("Compressing:"+v.getInPath());
					mCompressor.compressVideo(v);
				}
				status = STATUS_NOTSTARTED;
				if(mOnQueueListener !=null)
					mOnQueueListener.onQueueFinish();
				//Logger.log("All Video Compressed");
                m_config.compressionCompleted.oncompressioncompleted(true);

			}
    	}).start();

	}
	
	@Override
	public void compressVideo(IVideo v) {
		mQueue.add(v);		
	}

	@Override
	public void setOnProgressListener(OnProgressListener lis) {
		mCompressor.setOnProgressListener(lis);
	}

	@Override
	public void removeOnProgressListener() {
		mCompressor.removeOnProgressListener();
	}

	@Override
	public void setOnQueueListener(OnQueueListener<IVideo> lis) {
		this.mOnQueueListener = lis;
	}

	@Override
	public void removeOnQueueListener() {
		this.mOnQueueListener = null;		
	}

	@Override
	public IVideo getCurrent() {
		return this.mCompressor.getCurrent();
	}

	@Override
	public int getSize() {
		return mQueue.size();
	}

	@Override
	public Iterator<IVideo> getWaitingList() {
		
		return mQueue.clone().iterator();
	}

	@Override
	public com.lkland.videocompressor.workqueue.IQueueable.OnQueueListener<IVideo> getOnQueueListener() {

		return this.mOnQueueListener;
	}

	@Override
	public OnProgressListener getOnProgressListener() {

        return mCompressor.getOnProgressListener();
	}


}
