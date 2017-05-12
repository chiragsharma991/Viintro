package com.viintro.Viintro.SocialLogin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Login.LoginActivity;
import com.viintro.Viintro.Login.RegistrationActivity;
import com.viintro.Viintro.Model.Social_Login_Request;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Webservices.LoginAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import static com.viintro.Viintro.Login.LoginActivity.rel_email_popup_login;
import static com.viintro.Viintro.Login.RegistrationActivity.rel_email_popup_register;


/**
 * Created by rkanawade on 24/01/17.
 */

public class FacebookLogin  {

    static Configuration_Parameter m_config;
    static SharedPreferences sharedPreferences;

    // LoginManager loginManager;

    //This method is used to sign in with facebook
    public static void fbWithFirebase(final Context mContext){
        Log.d("fb","inside fb with firebase");
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        LoginManager.getInstance().registerCallback(m_config.callbackManager , new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {

                Log.e("onSuccess"," "+loginResult);

                CommonFunctions.hDialog();

                AccessToken accessToken = loginResult.getAccessToken();

                final ArrayList<String> declined_permissions = new ArrayList<String>();
                Set<String> declined_perm = accessToken.getDeclinedPermissions();
                Iterator iterator = declined_perm.iterator();

                String permissionname = declined_perm.toString().replace("[","");
                permissionname = permissionname.replace("]","");

                while (iterator.hasNext())
                {
                    String perm_name = iterator.next().toString();
                    declined_permissions.add(perm_name);
                }

                if (declined_perm.size() > 0)
                {


                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    alertDialogBuilder

                            .setMessage(mContext.getResources().getString(R.string.permission_fb_email))
                            .setCancelable(false)
                            .setNegativeButton("Don't Allow", null)
                            .setPositiveButton("Allow",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            askForDeclinedFBPermissions(declined_permissions, mContext);
                                        }
                                    });
                    alertDialogBuilder.show();
                }
                else
                {
//                    Profile profile = Profile.getCurrentProfile();
//                    Log.e("profile"," "+profile);
//                    if(profile != null)
//                    {
//                        Log.d("fb"," "+profile.getFirstName());
                    CommonFunctions.sDialog(mContext,"Loading...");
                    //getFbData(profile, mContext);
                    getFbData(mContext);
                    //}

                }


            }

            @Override

            public void onCancel()
            {
                CommonFunctions.hDialog();
                CommonFunctions.displayToast(mContext,mContext.getResources().getString(R.string.Facebook_cancel));
            }

            @Override
            public void onError(FacebookException error)
            {
                if(error.getMessage().equals("net::ERR_CONNECTION_CLOSED"))
                {
                    CommonFunctions.hDialog();
                    CommonFunctions.displayToast(mContext,"FB is blocked on your server.");
                }
                else
                {
                    CommonFunctions.hDialog();
                    CommonFunctions.displayToast(mContext,error.getMessage());
                }


            }
        });
    }

    public static void askForDeclinedFBPermissions(ArrayList<String> declined_perm, Context mContext)
    {
        LoginManager.getInstance().logInWithReadPermissions((Activity) mContext,declined_perm);
        // FacebookDataRetieval();
    }


    public static void signInByFacebook(Context mContext){

        LoginManager
                .getInstance()
                .logInWithReadPermissions(
                        (Activity) mContext,
                        Arrays.asList("public_profile", "email")
                );
    }

    private static void getFbData(final Context mContext) {
//        if (profile != null)
//        {
//            final String fullname = profile.getName();
//            final String givenname = profile.getFirstName();
//            final String familyname = profile.getLastName();
//            final String id = profile.getId();



        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        String fullname = "", givenname = "", familyname = "",id = "", gender = "", email = "", profilepic = "", coverpic = "";
                        Object picture,cover;

                        Log.e("FB response "," "+object.toString());

                        try
                        {
                            fullname = object.getString("name");
                            gender = object.getString("gender");
                            email = object.getString("email");
                            id = object.getString("id");
                            givenname = object.getString("first_name");
                            familyname = object.getString("last_name");
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                        try {
                            picture = object.getJSONObject("picture");
                            if(picture != null)
                            {
                                JSONObject pic_obj = new JSONObject(picture.toString());
                                Object data = pic_obj.getJSONObject("data");
                                if(data != null)
                                {
                                    JSONObject data_obj = new JSONObject(data.toString());
                                    profilepic = data_obj.getString("url");

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            cover = object.getJSONObject("cover");
                            if(cover != null)
                            {
                                JSONObject cover_obj = new JSONObject(cover.toString());
                                coverpic = cover_obj.getString("source");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final String device_id = sharedPreferences.getString("device_id",null);
                        m_config.social_login_request = Social_Login_Request.json_social_login_request("facebook", fullname, email, gender, profilepic, coverpic, givenname, familyname, id, device_id, "");


                        if(m_config.social_login_request.getEmail().equals(""))
                        {
                            CommonFunctions.hDialog();
                            if(mContext instanceof RegistrationActivity)
                            {
                                rel_email_popup_register.setVisibility(View.VISIBLE);
                            }
                            else if(mContext instanceof LoginActivity)
                            {
                                rel_email_popup_login.setVisibility(View.VISIBLE);
                            }
                        }
                        else
                        {
                            // call to social login API
                            LoginAPI.req_sociallogin_API(mContext, m_config.social_login_request);

                        }




                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "gender,name,first_name,last_name,email,cover,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();



    }



}
