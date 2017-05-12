package com.viintro.Viintro.Reusables;

import android.content.Context;
import android.graphics.Typeface;

public class TextFont {

    public static Typeface opensans_regular(Context context)
    {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),
                "fonts/OpenSans-Regular.ttf");

        return typeface;
    }

    public static Typeface opensans_semibold(Context context)
    {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),
                "fonts/OpenSans-Semibold.ttf");

        return typeface;
    }


    public static Typeface opensans_bold(Context context)
    {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),
                "fonts/OpenSans-Bold.ttf");

        return typeface;
    }
}