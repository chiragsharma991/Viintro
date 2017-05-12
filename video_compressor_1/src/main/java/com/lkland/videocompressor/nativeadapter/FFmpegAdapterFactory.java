package com.lkland.videocompressor.nativeadapter;

public class FFmpegAdapterFactory {
	public static IFFmpegAdapter defaultFFmpegAdapter(){
		return new FFmpegAdapter();
	}
}
