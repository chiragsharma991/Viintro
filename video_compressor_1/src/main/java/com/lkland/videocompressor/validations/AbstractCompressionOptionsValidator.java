package com.lkland.videocompressor.validations;

/**
 * Created by DavidY on 28/9/2015.
 */
public abstract class AbstractCompressionOptionsValidator implements IValidator{
    public static final int FAILED_NOT_A_LOCAL_FILE = 1;
    public static final int FAILED_EMPTY_INPUT_FILE = 2;
    public static final int FAILED_NO_SUCH_INPUT_FILE = 3;

    public static final int FAILED_EMPTY_OUTPUT_FOLDER = 4;
    public static final int FAILED_EMPTY_OUTPUT_FILE_NAME = 5;
    public static final int FAILED_EMPTY_OUTPUT_SIZE = 6;

    public static final int FAILED_EQUAL_INOUT_PATH = 7;

    public static final int PASS = 0;

}
