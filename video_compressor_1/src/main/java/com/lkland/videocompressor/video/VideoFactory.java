package com.lkland.videocompressor.video;

public class VideoFactory {
	public static IVideo defaultVideo(){
		return new Video();
	}
}
