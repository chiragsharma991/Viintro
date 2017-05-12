package com.lkland.videocompressor.fragments;

import com.lkland.videocompressor.video.IVideo;

public interface IQueueList {
	public void queueFinished();

	public void progress(IVideo video, String str);
	
	public void add(IVideo video);
	
	public void remove(IVideo video);
	
	public void pop(IVideo video);
}
