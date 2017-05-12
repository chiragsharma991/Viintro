package com.lkland.videocompressor.video;

public interface IVideo {
	public void setInPath(String inPath);
	public void setInName(String inName);
	public void setOutPath(String outPath);
	public void setOutName(String outName);
	public void setOutSize(String outSize);
	public void setDuration(int duration);
	
	public int getDuration();
	
	public String getInPath();
	
	public String getInName();
	
	public String getOutPath();
	
	public String getOutName();
	
	public String getOutSize();
	
	@Override
	public String toString();
	
}
