package com.lkland.videocompressor.validations;

/**
 * Created by DavidY on 28/9/2015.
 */
public class ErrorMsgPresenter implements IErrorMsgPresenter {
    @Override
    public String present(int errorCode) {
        switch (errorCode){
            case AbstractCompressionOptionsValidator.FAILED_NOT_A_LOCAL_FILE:
                return "- Please Select a Local Input Video";
            case AbstractCompressionOptionsValidator.FAILED_EMPTY_INPUT_FILE:
                return "- Please Select a Input Video";
            case AbstractCompressionOptionsValidator.FAILED_NO_SUCH_INPUT_FILE:
                return "- Input Video Not Found";
            case AbstractCompressionOptionsValidator.FAILED_EMPTY_OUTPUT_FOLDER:
                return "- Please Select a Output Folder";
            case AbstractCompressionOptionsValidator.FAILED_EMPTY_OUTPUT_FILE_NAME:
                return "- Please Enter a Output Video Name";
            case AbstractCompressionOptionsValidator.FAILED_EMPTY_OUTPUT_SIZE:
                return "- Please Enter Output Size of the Video";
            case AbstractCompressionOptionsValidator.FAILED_EQUAL_INOUT_PATH:
                return "- Input Video Path Cannot be the same as the Output Video Path";
        }
        return "- Unknown Error";
    }
}
