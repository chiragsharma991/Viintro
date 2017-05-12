package com.lkland.videocompressor.validations;

/**
 * Created by DavidY on 28/9/2015.
 */
public interface IValidationFactory {
    public IValidator getValidator(String inPathAndName, String outPath, String outName, String outSize);

    public IErrorMsgPresenter getErrorMsgPresenter();
}
