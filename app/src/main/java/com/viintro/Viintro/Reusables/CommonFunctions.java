package com.viintro.Viintro.Reusables;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Webservices.AccessTokenAPI;
import com.viintro.Viintro.widgets.Configuration;
import com.viintro.Viintro.widgets.Crouton;
import com.viintro.Viintro.widgets.Style;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.zip.Inflater;

import static com.viintro.Viintro.Constants.Constcore.cloudinary;


/**
 * Created by hasai on 04/02/17.
 */

public class CommonFunctions {

    static ProgressDialog progressDialog = null;
    static ProgressDialog progressDialog_PlayVideo = null;
    static SharedPreferences sharedpreferences;
    static Configuration_Parameter m_config;



    public CommonFunctions(CommonFunctions commonFunctions) {
        commonFunctions = this;
    }

    //Function to show loader
    public static void sDialog(Context context, String message)
    {
        if(progressDialog == null)
        {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }
    }

    //Function to remove loader
    public static void hDialog(){
        if(progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog.cancel();
                progressDialog = null;

            }
        }
    }

    //Function to display toast
    public static void displayToast(Context context, String msg)
    {
//        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
//        View layout = inflater.inflate(R.layout.custom_toast,
//                (ViewGroup) ((Activity)context).findViewById(R.id.custom_container));
//
//        TextView text = (TextView) layout.findViewById(R.id.text);
//        text.setText(msg);
//
//
//        Toast toast = Toast.makeText(context, ""+msg, Toast.LENGTH_LONG);
//        toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 0);
//        toast.setView(layout);
//        toast.show();


        Configuration croutonConfiguration = new Configuration.Builder()
                .setDuration(5000).build();
        Style style = new Style.Builder()
                .setConfiguration(croutonConfiguration)
                .setBackgroundColor(R.color.white)
                .setPaddingInPixels(5)
                .setGravity(Gravity.CENTER)
                .setTextColor(R.color.orange)
                .setHeight(100)
                .setWidth(FrameLayout.LayoutParams.MATCH_PARENT)
                .build();


        Crouton crouton = Crouton.makeText((Activity) context, msg, style);
        crouton.show();
    }

    //Check Network availability
    public static boolean chkStatus(Context context)
    {
        //final ConnectivityManager connMgr = (ConnectivityManager)cont.getSystemService(Context.CONNECTIVITY_SERVICE);
//        final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        final NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        return wifi.isConnectedOrConnecting() || mobile.isConnectedOrConnecting();

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    /** Check if this device has a camera */
    public static boolean checkCameraHardware(Context context)
    {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
        {
            // this device has a camera
            return true;
        }
        else
        {
            // no camera on this device
            return false;
        }
    }

    public static String getTimeZoneIST()
    {
        Calendar calender = Calendar.getInstance();
        calender.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));

        System.out.println("timezone "+calender.get(Calendar.HOUR_OF_DAY) + ":" + calender.get(Calendar.MINUTE) +  ":" + calender.getActualMinimum(Calendar.SECOND));

        return "UTC";//calender.get(Calendar.HOUR_OF_DAY) + ":" + calender.get(Calendar.MINUTE) +  ":" + calender.getActualMinimum(Calendar.SECOND);

    }


    /** function to get time stamp */
    public static String getTimeStamp()
    {
        Date date = new Date();
        System.out.println("Date Object:" + date);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Formatted Date:" + sdf2.format(date));

        return sdf2.format(date);

    }

    //Function to show loader
    public static void sDialog_PlayVideo(final Context context, final String message)
    {

        Log.e("sdialog "," "+progressDialog_PlayVideo);
        if (progressDialog_PlayVideo == null) {
            progressDialog_PlayVideo = new ProgressDialog(context);
            progressDialog_PlayVideo.setMessage(message);
            progressDialog_PlayVideo.setCancelable(true);
            if (!progressDialog_PlayVideo.isShowing()) {
                progressDialog_PlayVideo.show();
            }
        }


    }

    //Function to remove loader
    public static void hDialog_PlayVideo(){
        Log.e("== "," "+progressDialog_PlayVideo);
        if(progressDialog_PlayVideo != null) {
//            if (progressDialog_PlayVideo.isShowing()) {
            progressDialog_PlayVideo.dismiss();
            progressDialog_PlayVideo.cancel();
            progressDialog_PlayVideo = null;

//            }
        }
    }

    /** function to clear all shared preferences on logout */
    public static void clrAllFlags(final Context context){
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        m_config = Configuration_Parameter.getInstance();

        editor.putBoolean("login_done", false);
        editor.putBoolean("onboard_process", false);
        editor.putBoolean("profile_video", false);
        editor.putBoolean("my_profile_flag",false);
        editor.putString(m_config.login_refresh_token,"");
        editor.putString(m_config.login_access_token,"");
        editor.putString(m_config.login_fullname,"");
        editor.putString(m_config.login_display_pic,null);
        editor.putString(m_config.Profile_user_type,"");
        editor.putString(m_config.profile_url,null);
        editor.putString(m_config.profile_website,null);
        editor.putString(m_config.University,null);
        editor.putString(m_config.Course,null);
        editor.putString(m_config.Company,null);
        editor.putString(m_config.Designation,null);
        editor.putString(m_config.Profile_city,null);
        editor.putString(m_config.Profile_state,null);
        editor.putString(m_config.Profile_country,null);
        editor.putString(m_config.Mobile_Number,null);
        editor.putString(m_config.Profile_view_count,null);
        editor.putString(m_config.Profile_following_count,null);
        editor.putString(m_config.Profile_followers_count,null);
        editor.putString(m_config.Profile_Strength,null);
        editor.putString(m_config.Profile_dob_date,null);
        editor.putString(m_config.Profile_dob_month,null);
        editor.putString(m_config.Profile_dob_year,null);
        editor.commit();
    }

    /** function to show alert dialog after expiry of accesstoken and refresh token*/
    public static void alertdialog_token(final Context context, final String check)
    {
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.access_token_expired))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if(check.equals("accesstokenexpired"))
                        {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(m_config.login_access_token, sharedPreferences.getString(m_config.login_refresh_token,""));
                            editor.commit();
                            dialog.dismiss();
                        }
                        else if(check.equals("refreshtokenexpired"))
                        {
                            AccessTokenAPI.req_gen_accesstoken(context);
                        }





                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /** function to upload image on cloudinary */
    public static String getImageUrlfromCloudinary(Context cont, String picturePath){

        Log.e("picturePath"," "+picturePath);
        String url = "";

        try {
            String Id = UUID.randomUUID().toString();
            cloudinary.uploader().upload(picturePath, ObjectUtils.asMap("public_id", Id));
            url = cloudinary.url().generate(String.valueOf(Id));
            Log.e("public_id"," "+Id+" "+url);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return url;
    }

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() > maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
                else if(maxLine == tv.getLineCount())
                {
                    Log.e("no see more","");
                }
                else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {


            ssb.setSpan(new MySpannable(false){
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "See Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 2, ".. See More", true);//3
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

    public static class MySpannable extends ClickableSpan {

        private boolean isUnderline = true;

        /**
         * Constructor
         */
        public MySpannable(boolean isUnderline) {
            this.isUnderline = isUnderline;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(isUnderline);
            ds.setColor(Color.parseColor("#1b76d3"));
        }

        @Override
        public void onClick(View widget) {


        }
    }


    public static void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] mFields = popupMenu.getClass().getDeclaredFields();
            for (Field field : mFields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> popupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method mMethods = popupHelper.getMethod("setForceShowIcon", boolean.class);
                    mMethods.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static String getLocalTime(String time)
    {
        String localtime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = sdf.parse(time);
            //SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf2 =
                    new SimpleDateFormat("dd MMMM, hh:mm a");//Thursday, November 14, 2013 05:00 PM
            //"EEEE, MMMM dd, yyyy hh:mm a" will return

            localtime = sdf2.format(date);
            System.out.println("in milliseconds: " + localtime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return localtime;
    }

    public static String getTime(String time)
    {
        String localtime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = sdf.parse(time);
            //SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf2 =
                    new SimpleDateFormat("hh:mm a");//Thursday, November 14, 2013 05:00 PM
            //"EEEE, MMMM dd, yyyy hh:mm a" will return

            localtime = sdf2.format(date);
            System.out.println("in milliseconds: " + localtime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return localtime;
    }



}
