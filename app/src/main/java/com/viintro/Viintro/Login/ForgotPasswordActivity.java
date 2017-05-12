package com.viintro.Viintro.Login;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.ForgotPassword_Request;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Reusables.TextFont;
import com.viintro.Viintro.Reusables.Validations;
import com.viintro.Viintro.Webservices.ForgotPasswordAPI;


/**
 * Created by rkanawade on 24/01/17.
 */

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText edtInputEmail;
    private Button btnSendLink;
    private TextView txt_incorrect_email;
    Context context;
    public static  Activity forgotpw_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_forgotpassword);
        context = this;
        forgotpw_activity = this;
        getHeight();
        initialiseUI();

        edtInputEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    //edtInputEmail.setBackgroundResource(R.drawable.edittext_orange_border);
                    edtInputEmail.clearFocus();
                    btnSendLink.performClick();
                    handled = true;

                }
                return handled;
            }
        });


        edtInputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(edtInputEmail.getText().toString().replaceAll("\\s+", " ").trim().length()>0)
                {
                    btnSendLink.setBackgroundResource(R.drawable.btn_border_orange);
                    btnSendLink.setEnabled(true);
                    btnSendLink.setTextColor(Color.parseColor("#ff704c"));
                }
                else
                {
                    btnSendLink.setBackgroundResource(R.drawable.btn_border_grey);
                    btnSendLink.setEnabled(false);
                    btnSendLink.setTextColor(Color.parseColor("#d6d6d6"));

                }
            }
        });


        btnSendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtInputEmail.getWindowToken(), 0);

                if (Validations.isEmpty(edtInputEmail))
                {
                    if (Validations.isEmpty(edtInputEmail))
                    {
                        txt_incorrect_email.setText(context.getResources().getString(R.string.reg_email_id));
                        txt_incorrect_email.setVisibility(View.VISIBLE);
                        edtInputEmail.setBackgroundResource(R.drawable.edittext_red_border);

                    }
                    else if(!Validations.isEmpty(edtInputEmail) && !Validations.isValidEmailId(edtInputEmail.getText().toString().trim()))
                    {
                        txt_incorrect_email.setText(context.getResources().getString(R.string.reg_email_id));
                        txt_incorrect_email.setVisibility(View.VISIBLE);
                        edtInputEmail.setBackgroundResource(R.drawable.edittext_red_border);
                    }
                    else
                    {
                        txt_incorrect_email.setText("");
                        txt_incorrect_email.setVisibility(View.GONE);
                        edtInputEmail.setBackgroundResource(R.drawable.edittext_orange_border);
                    }

                }else
                {
                    if (Validations.isValidEmailId(edtInputEmail.getText().toString().trim()))
                    {
                        dismissCursor();
                        if(CommonFunctions.chkStatus(context))
                        {
                            CommonFunctions.sDialog(context,"Loading...");
                            String email = edtInputEmail.getText().toString().trim();
                            ForgotPasswordAPI.req_forgotpw_API(context,json_forgotpw_request(email));

                        }
                        else
                        {
                            CommonFunctions.displayToast(context,getResources().getString(R.string.network_connection));
                        }


                    }
                    else
                    {
                        txt_incorrect_email.setText(context.getResources().getString(R.string.reg_email_id));
                        txt_incorrect_email.setVisibility(View.VISIBLE);
                        edtInputEmail.setBackgroundResource(R.drawable.edittext_red_border);

                    }
                }

            }
        });

    }

    private void initialiseUI()
    {
        TextView txt_forgotPw = (TextView) findViewById(R.id.txt_forgotPw);
        txt_forgotPw.setTypeface(TextFont.opensans_semibold(context));
        TextInputLayout input_email = (TextInputLayout) findViewById(R.id.input_email);
        input_email.setTypeface(TextFont.opensans_regular(context));
        edtInputEmail = (EditText) findViewById(R.id.edtInputEmail);
        edtInputEmail.setTypeface(TextFont.opensans_regular(context));
        txt_incorrect_email = (TextView) findViewById(R.id.txt_incorrect_email);
        txt_incorrect_email.setTypeface(TextFont.opensans_regular(context));
        btnSendLink = (Button) findViewById(R.id.btnSendLink);
        btnSendLink.setTypeface(TextFont.opensans_semibold(context));

        ImageView img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void dismissCursor()
    {
        //Clear Focus from all edit texts
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtInputEmail.getWindowToken(), 0);
        edtInputEmail.clearFocus();
        txt_incorrect_email.setText("");
        txt_incorrect_email.setVisibility(View.GONE);
        edtInputEmail.setBackgroundResource(R.drawable.edittext_orange_border);

    }

    public ForgotPassword_Request json_forgotpw_request(String email)
    {
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
