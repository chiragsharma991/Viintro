package com.lkland.videocompressor.nativeadapter;

public class BaseFFmpegAdapter implements IFFmpegAdapter{
	@Override
	public int getDuration(String fileName){
		return 0;
	}
	@Override
	public boolean isDecoderAva(String fileName){
		return false;
	}
	@Override
	public void compressVideo(String[] params){
		
	}
	@Override
	public void setOnMessageListener(OnMessageListener lis) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void removeOnMessageListener() {
		// TODO Auto-generated method stub
		
	}

}
