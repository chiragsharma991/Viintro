package com.lkland.videocompressor.responsehandler;

import com.lkland.videocompressor.video.IVideo;
import com.lkland.videocompressor.workqueue.IQueueable;

public class OnQueueHandler extends ResponseHandler implements IQueueable.OnQueueListener<IVideo>{

	@Override
	public void onAdd(IVideo t) {
		for(int i=0;i<this.mListManager.size();i++){
			this.mListManager.get(i).onAdd(t);
		}			
	}

	@Override
	public void onRemove(IVideo t) {
		for(int i=0;i<this.mListManager.size();i++){
			this.mListManager.get(i).onRemove(t);
		}		
	}
	
	@Override
	public void onPop(IVideo t){
		for(int i=0;i<this.mListManager.size();i++){
			this.mListManager.get(i).onPop(t);
		}
	}

	@Override
	public void onQueueStart() {
		for(int i=0;i<this.mListManager.size();i++){
			this.mListManager.get(i).onQueueStart();
		}		
	}

	@Override
	public void onQueueFinish() {
		for(int i=0;i<this.mListManager.size();i++){
			this.mListManager.get(i).onQueueFinished();
		}
	}

}
