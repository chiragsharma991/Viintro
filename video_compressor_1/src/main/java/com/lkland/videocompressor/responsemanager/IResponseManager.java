package com.lkland.videocompressor.responsemanager;

import com.lkland.videocompressor.video.IVideo;

public interface IResponseManager {
	public void onQueueStart();
	public void onRemove(IVideo v);
	public void onAdd(IVideo v);
	public void onProgress(IVideo v, String str);
	public void onQueueFinished();
	public void onPop(IVideo v);
}
