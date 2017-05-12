package com.lkland.videocompressor.responsehandler;

import java.util.ArrayList;

import com.lkland.videocompressor.responsemanager.IResponseManager;

public abstract class ResponseHandler implements IResponseHandler{
	protected ArrayList<IResponseManager> mListManager;
	public ResponseHandler(){
		mListManager = new ArrayList<IResponseManager>();
	}
	
	@Override
	public void addResponseManager(IResponseManager manager) {
		removeResponseManager(manager.getClass());
		mListManager.add(manager);
	}
	
	@Override
	public void removeResponseManager(Class c) {
		for(int i=0;i<mListManager.size();i++){
			if(mListManager.get(i).getClass().equals(c)){
				mListManager.remove(i);
				return;
			}
		}		
	}
}
