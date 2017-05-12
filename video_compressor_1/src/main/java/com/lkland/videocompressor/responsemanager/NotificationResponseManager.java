package com.lkland.videocompressor.responsemanager;

import android.app.Notification;

import com.lkland.util.Logger;
import com.lkland.videocompressor.R;
import com.lkland.videocompressor.compressor.ICompressor;
import com.lkland.videocompressor.parser.ProgressPaser;
import com.lkland.videocompressor.services.AbstractCompressionService;
import com.lkland.videocompressor.video.IVideo;
import com.lkland.videocompressor.workqueue.IQueueable;

public class NotificationResponseManager extends AbstractResponseManager{
	AbstractCompressionService mService;
	public NotificationResponseManager(AbstractCompressionService ser) {
		mService = ser;
	}

	@Override
	public void onProgress(IVideo v, String str) {
		String msg = "Compressing: ";
		ICompressor compressor = mService.getCompressor();
		msg+=v.getInName();
		
		if(compressor instanceof IQueueable){
			if(((IQueueable)compressor).getSize()>0)
				msg+=", "+((IQueueable)compressor).getSize()+" Queued";
		}
		
		ProgressPaser pp = new ProgressPaser();
		pp.parse(str);
		
		Notification.Builder builder = new Notification.Builder(mService)
			.setContentTitle("Video Compressor")
			.setContentText(msg)
			.setSmallIcon(R.drawable.ic_launcher)
			.setProgress(pp.getTotalTime(), pp.getTime(), false);
		mService.startForeground(1115, builder.build());
		
	}

	@Override
	public void onQueueFinished() {
		Logger.log("");
		mService.stopForeground(true);		
		mService.stopSelf();

	}

	@Override
	public void onPop(IVideo v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onQueueStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRemove(IVideo v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAdd(IVideo v) {
		// TODO Auto-generated method stub
		
	}

}
