package com.viintro.Viintro.Login;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.ForgotPassword_Request;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Reusables.TextFont;
import com.viintro.Viintro.Webservices.ForgotPasswordAPI;

public class ResendLinkActivity extends Activity {
    private TextView text_resetLink, text_resend;
    private Button btn_BacktoLogin;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resend_link);
        context = this;

        getHeight();
        text_resetLink = (TextView) findViewById(R.id.text_resetLink);
        text_resetLink.setTypeface(TextFont.opensans_regular(context));
        text_resend = (TextView) findViewById(R.id.text_resend);
        text_resend.setTypeface(TextFont.opensans_bold(context));
        btn_BacktoLogin = (Button) findViewById(R.id.btn_BacktoLogin);
        btn_BacktoLogin.setTypeface(TextFont.opensans_semibold(context));
        text_resetLink.setText("Reset link has been sent to \n"+getIntent().getExtras().getString("email"));

        text_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CommonFunctions.chkStatus(context))
                {
                    CommonFunctions.sDialog(context,"Loading...");
                    String email = getIntent().getExtras().getString("email");
                    ForgotPasswordAPI.req_forgotpw_API(context,json_forgotpw_request(email));


                }
                else
                {
                    CommonFunctions.displayToast(context,getResources().getString(R.string.network_connection));
                }
            }
        });

        btn_BacktoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public ForgotPassword_Request json_forgotpw_request(String email) {
        ForgotPassword_Request forgotPassword_request = new ForgotPassword_Request();
        forgotPassword_request.setClient_id(Constcore.client_Id);
        forgotPassword_request.setClient_secret(Constcore.client_Secret);
        forgotPassword_request.setEmail(email);


        return forgotPassword_request;
    }

    public int getHeight()
    {
        final int[] height = new int[1];

        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.rel_white_background);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int width = layout.getMeasuredWidth();
                height[0] = layout.getMeasuredHeight();
                Log.e("height"," "+height[0]);
                RelativeLayout rel_orange_background = (RelativeLayout) findViewById(R.id.rel_orange_background);
                rel_orange_background.getLayoutParams().height = height[0];
                rel_orange_background.requestLayout();
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height[0]);
//                int top =  (int) mContext.getResources().getDimension(R.dimen.login_orange_top);
//                int left =  (int) mContext.getResources().getDimension(R.dimen.left_orange_background);
//                int right =  (int) mContext.getResources().getDimension(R.dimen.right_orange_background);
//                int bottom = (int) mContext.getResources().getDimension(R.dimen.login_orange_bottom);
//
//                params.setMargins(left, top, right, bottom);
//                rel_orange_background.setLayoutParams(params);


            }
        });
        return height[0];
    }
}
