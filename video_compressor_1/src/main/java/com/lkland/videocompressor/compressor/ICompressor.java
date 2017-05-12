package com.lkland.videocompressor.compressor;

import com.lkland.videocompressor.video.IVideo;

public interface ICompressor {
	public void compressVideo(IVideo v);
	
	public void setOnProgressListener(OnProgressListener lis);
	public OnProgressListener getOnProgressListener();
	public void removeOnProgressListener();
	public IVideo getCurrent();

	public interface OnProgressListener{
		public void onProgress(IVideo v, String str);
		
		public void onFinished();

	}
	
}
