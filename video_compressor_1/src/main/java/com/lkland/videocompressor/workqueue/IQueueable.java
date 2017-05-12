package com.lkland.videocompressor.workqueue;

import java.util.Iterator;

public interface IQueueable<T> {
	public void add(T t);
	public T getCurrent();
	public Iterator<T> getWaitingList();
	public int getSize();
	public T remove(T t);
	public void start();
	public void setOnQueueListener(OnQueueListener<T> lis);
	public OnQueueListener<T> getOnQueueListener();
	public void removeOnQueueListener();
	
	public interface OnQueueListener<T>{
		public void onAdd(T t);
		public void onRemove(T t);
		public void onPop(T t);
		public void onQueueStart();
		public void onQueueFinish();
	}


}
