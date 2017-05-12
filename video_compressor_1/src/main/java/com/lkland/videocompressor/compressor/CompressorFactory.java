package com.lkland.videocompressor.compressor;

import com.lkland.videocompressor.nativeadapter.IFFmpegAdapter;

public class CompressorFactory {
	public static ICompressor defaultCompressor(){
		return new FFmpegCompressor();
	}
}
