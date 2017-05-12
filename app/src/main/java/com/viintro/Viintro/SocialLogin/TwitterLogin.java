package com.viintro.Viintro.SocialLogin;

import android.util.Log;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;

/**
 * Created by rkanawade on 24/01/17.
 */

public class TwitterLogin {

    static String TAG = "TwitterLogin";

    public static String getTwitterEmail(final TwitterSession session) {
        final String[] email = {""};


        TwitterAuthClient authClient = new TwitterAuthClient();
        authClient.requestEmail(session, new Callback<String>() {
            @Override
            public void success(Result<String> result) {

                Log.e("result"," "+result.data.toString());

//                AccountService ac = Twitter.getApiClient(session).getAccountService();
//                ac.verifyCredentials(true, true, new Callback<com.icon_twitter.sdk.android.core.models.User>() {
//                    @Override
//                    public void success(Result<com.icon_twitter.sdk.android.core.models.User> result) {
//                        String imageUrl = result.data.profileImageUrl;
//                        String email = result.data.email;
//                        String userName = result.data.name;
//                        System.out.println(imageUrl);
//                        System.out.println(email);
//                        System.out.println(userName);
//                    }
//
//                    @Override
//                    public void failure(TwitterException e) {
//
//                    }
//                });


                email[0] = "";
                Log.e(TAG, "email success: " + email[0]);

                // Do something with the result, which provides the email address
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                email[0] = "";
                Log.e(TAG, "email failure: " + email[0]);

            }
        });


        return email[0];


    }



}
