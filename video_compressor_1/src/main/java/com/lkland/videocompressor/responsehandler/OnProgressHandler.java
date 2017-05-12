package com.lkland.videocompressor.responsehandler;

import com.lkland.util.Logger;
import com.lkland.videocompressor.compressor.ICompressor.OnProgressListener;
import com.lkland.videocompressor.video.IVideo;

public class OnProgressHandler extends ResponseHandler implements OnProgressListener{

	@Override
	public void onProgress(IVideo v, String str) {
		for(int i=0;i<this.mListManager.size();i++){
			this.mListManager.get(i).onProgress(v, str);
		}
	}
	
	@Override
	public void onFinished() {

	}
}
