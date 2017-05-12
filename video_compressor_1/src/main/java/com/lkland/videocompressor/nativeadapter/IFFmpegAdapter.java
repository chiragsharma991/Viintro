package com.lkland.videocompressor.nativeadapter;

public interface IFFmpegAdapter {
	public void setOnMessageListener(OnMessageListener lis);
	public void removeOnMessageListener();
	public int getDuration(String fileName);
	
	public boolean isDecoderAva(String fileName);
	
	public void compressVideo(String[] params);
	
	public interface OnMessageListener{
		public void onMessage(String str);
	}
	 
}
