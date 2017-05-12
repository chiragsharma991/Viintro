package com.lkland.videocompressor.validations;

/**
 * Created by DavidY on 28/9/2015.
 */
public class ValidationFactory implements IValidationFactory {
    @Override
    public IValidator getValidator(String inPathAndName, String outPath, String outName, String outSize) {
        return new CompressionOptionsValidator(inPathAndName, outPath, outName, outSize);
    }

    @Override
    public IErrorMsgPresenter getErrorMsgPresenter() {
        return new ErrorMsgPresenter();
    }
}
