package com.viintro.Viintro.Login;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;

import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.TextFont;


public class CannonballTwitterLoginButton extends TwitterLoginButton {
    public CannonballTwitterLoginButton(Context context) {
        super(context);
        init();
    }

    public CannonballTwitterLoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CannonballTwitterLoginButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (isInEditMode()){
            return;
        }

//        setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.color.tw__transparent), null, null, null);
//        setBackgroundResource(R.mipmap.twitter_button);
        setBackgroundColor(Color.TRANSPARENT);
        setText("");
//        setGravity(Gravity.CENTER);
//        setTextAlignment(Gravity.CENTER);
//        setTextSize(12);
//        setTextColor(getResources().getColor(R.color.white));
        setTypeface(TextFont.opensans_semibold(getContext()));
        //setPadding(10, 0, 5, 5);


    }
}