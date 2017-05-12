package com.lkland.videocompressor.responsehandler;

import com.lkland.videocompressor.responsemanager.IResponseManager;

public interface IResponseHandler {
	public void addResponseManager(IResponseManager manager);
	public void removeResponseManager(Class c);
}
