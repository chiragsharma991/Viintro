package com.lkland.videocompressor.services;

import com.lkland.util.Logger;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public abstract class AbstractCompressionService extends Service implements ICompressionService{
    protected final LocalBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
		Logger.log("");

        return mBinder;
    }
    
    @Override
    public boolean onUnbind (Intent intent){
    	Logger.log("");
    	return false;
    }




    public class LocalBinder extends Binder{
    	public AbstractCompressionService getService() {
            return AbstractCompressionService.this;
        }
    }
}
