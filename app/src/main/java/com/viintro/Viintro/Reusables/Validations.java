package com.viintro.Viintro.Reusables;

import android.text.InputFilter;
import android.util.Log;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hasai on 30/01/17.
 */

public class Validations {

    public static boolean isValidEmailId(String email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    public static boolean isEmpty(EditText edtText)
    {
        if (edtText.getText().toString().replaceAll("\\s+", " ").length() > 0)
            return false;

        return true;
    }

    public static String checkWordsCount_Caption(String input, String Msg){
        String regex = "^[a-zA-Z0-9]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        String[] words = input.trim().split("\\s+");
        Log.e("words length "," "+words.length);

        String msg;
        if(input.equals("") || input.equals(" "))
        {
            msg = empty_field_msg(Msg);
        }
        else if(words.length > 50){

            msg = exceed_words_Count(Msg);

        }else{

            msg = "true";
        }

        return msg;

    }

    public static String empty_field_msg(String Msg)
    {
        String msg = null;
        if(Msg.equals("thought"))
        {
            msg =  "Please enter caption.";
        }
        else if(Msg.equals("connect_public_profile") || Msg.equals("connect"))
        {
            msg =  "Please enter a message to connect.";
        }
        else if(Msg.equals("comment"))
        {
            msg = "Please enter comment.";
        }
        else if(Msg.equals("description"))
        {
            msg = "Please enter description";
        }
        return msg;
    }

    public static String exceed_words_Count(String Msg)
    {
        String msg = null;
        if(Msg.equals("thought"))
        {
            msg =  "Caption should be of 50 words only.";
        }
        else if(Msg.equals("connect_public_profile") || Msg.equals("connect"))
        {
            msg =  "Message should be of 50 words only.";
        }
        else if(Msg.equals("comment"))
        {
            msg =  "Comment should be of 50 words only.";
        }
        else if(Msg.equals("description"))
        {
            msg = "Description should be of 50 words only.";
        }

        return msg;
    }


    public static String checkWordsCount_Connect(String input)
    {
        String regex = "^[a-zA-Z0-9]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        String[] words = input.trim().split("\\s+");
        Log.e("words length "," "+words.length);

        String msg;
        if(input.equals("") || input.equals(" "))
        {
            msg = "Please type a message to connect.";
        }
//        else if(!matcher.matches())
//        {
//            msg = "No special characters allowed.";
//        }
//        else if(words.length > 50){
//
//            msg = "Caption should be of 50 words only.";
//
//        }
       else{

            msg = "true";
        }

        return msg;

    }

    public static int countWords(String s) {
        String trim = s.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\s+").length; // separate string around spaces
    }

    public static InputFilter filter;

    public static void setCharLimit(EditText et, int max)
    {
        filter = new InputFilter.LengthFilter(max);
        et.setFilters(new InputFilter[] { filter });
    }

    public static void removeFilter(EditText et) {
        if (filter != null) {
            et.setFilters(new InputFilter[0]);
            filter = null;
        }
    }

}
