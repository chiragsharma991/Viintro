package com.lkland.videocompressor.validations;

import com.lkland.videocompressor.nativeadapter.FFmpegAdapter;

import java.io.File;

/**
 * Created by DavidY on 28/9/2015.
 */
public class CompressionOptionsValidator extends AbstractCompressionOptionsValidator{
    private String mInPathAndName;
    private String mOutPath;
    private String mOutName;
    private String mOutSize;
    public CompressionOptionsValidator(String inPathAndName, String outPath, String outName, String outSize){
        this.mInPathAndName = inPathAndName;
        this.mOutPath = outPath;
        this.mOutName = outName;
        this.mOutSize = outSize;
    }
    @Override
    public int validate() {
//        FFmpegAdapter adapter = new FFmpegAdapter();
//        boolean isAva = adapter.isDecoderAva(mInPathAndName);

        //inPath ==""
        if(mInPathAndName.equals("")) return FAILED_EMPTY_INPUT_FILE;
        if(mOutName.equals("")) return FAILED_EMPTY_OUTPUT_FILE_NAME;
        if(mOutPath.equals("")) return FAILED_EMPTY_OUTPUT_FOLDER;
        if(mOutSize.equals("")) return FAILED_EMPTY_OUTPUT_SIZE;

        //inFile !exist
        File file = new File(mInPathAndName);
        if(!file.exists()) return this.FAILED_NO_SUCH_INPUT_FILE;

        //infile == outfile
        if(mInPathAndName.equals(mOutPath+File.separator+mOutName+".mp4")) return this.FAILED_EQUAL_INOUT_PATH;

        return PASS;

    }
}
