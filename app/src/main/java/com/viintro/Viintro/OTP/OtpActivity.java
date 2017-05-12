package com.viintro.Viintro.OTP;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Toast;

import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.Otp_Request;
import com.viintro.Viintro.Model.Verify_Otp_Request;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Reusables.TextFont;
import com.viintro.Viintro.Reusables.Validations;
import com.viintro.Viintro.Webservices.ResendOtpAPI;
import com.viintro.Viintro.Webservices.VerifyOtpAPI;

import org.w3c.dom.Text;

public class OtpActivity extends AppCompatActivity {
    private EditText edt_otp;
    private Button btn_sendOtp;
    private TextView btn_changeNo, text_resend, txt_incorrect_otp, text_otp, textNumber, textStar;
    private ImageView imgCorrectOtp;
    SharedPreferences sharedpreferences;
    Activity context;
    String mobile, code, otp, text, no, star;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_otp);
        context = this;
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        getHeight();
        initialiseUI();

        //get data from previous activity
        Intent intent = getIntent();
        mobile = intent.getExtras().getString("Mobile");
        code = intent.getExtras().getString("CountryCode");

        //set last two digits of the mobile number to textview
        String lastFourDigits = mobile.substring(mobile.length() - 2);
        Log.d("last", lastFourDigits);
        textNumber.setText(lastFourDigits);

        text = text_otp.getText().toString();
        no = textNumber.getText().toString();
        star = textStar.getText().toString();

        //used to concat two strings
        String concat = " " + text + " " + star + "" + no;
        text_otp.setText(concat);

        btn_sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_otp.getWindowToken(), 0);

                if (Validations.isEmpty(edt_otp))
                {
                    txt_incorrect_otp.setText("Please enter OTP");
                    txt_incorrect_otp.setVisibility(View.VISIBLE);
                    edt_otp.setBackgroundResource(R.drawable.edittext_red_border);
                } else if(edt_otp.getText().toString().length() != 6)
                {
                    txt_incorrect_otp.setText("Incorrect otp");
                    txt_incorrect_otp.setVisibility(View.VISIBLE);
                    edt_otp.setBackgroundResource(R.drawable.edittext_red_border);
                }else
                {
                    txt_incorrect_otp.setText("");
                    txt_incorrect_otp.setVisibility(View.GONE);
                    edt_otp.setBackgroundResource(R.drawable.edittext_orange_border);

                    if(CommonFunctions.chkStatus(context))
                    {
                        otp = edt_otp.getText().toString();
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("OTP", edt_otp.getText().toString());
                        editor.commit();
                        Log.d("finish","");
                        Verify_Otp_Request verify_otp_request = json_verify_otp_request(mobile, code, otp);
                        VerifyOtpAPI.req_verify_API(context, verify_otp_request, getIntent().getExtras().getString("from"));
                    }
                    else
                    {

                        CommonFunctions.displayToast(context,getResources().getString(R.string.network_connection));
                    }
                }

            }
        });

        text_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CommonFunctions.chkStatus(context)){
                    //call to api
                    Otp_Request otp_request = json_otp_request(code, mobile);
                    ResendOtpAPI.req_resend_API(context, otp_request);
                    Toast.makeText(context,"OTP sent to your mobile number",Toast.LENGTH_LONG).show();
                }
                else{

                    CommonFunctions.displayToast(context,getResources().getString(R.string.network_connection));
                }


            }
        });

        edt_otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                verifyOtp();
            }
        });

        edt_otp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    if(edt_otp.getText().toString().replaceAll("\\s+", " ").trim().length()>0)
                    {
                        edt_otp.setBackgroundResource(R.drawable.edittext_orange_border);
                    }
                    edt_otp.clearFocus();
                    btn_sendOtp.performClick();
                    handled = true;

                }
                return handled;
            }
        });

    }

    public void verifyOtp() {

        if(edt_otp.getText().toString().replaceAll("\\s+", " ").trim().length()>0)
        {
            btn_sendOtp.setBackgroundResource(R.drawable.btn_border_orange);
            btn_sendOtp.setEnabled(true);
            btn_sendOtp.setTextColor(Color.parseColor("#ff704c"));
        }
        else
        {

            btn_sendOtp.setBackgroundResource(R.drawable.btn_border_grey);
            btn_sendOtp.setEnabled(false);
            btn_sendOtp.setTextColor(Color.parseColor("#d6d6d6"));

        }


//        int len = edt_otp.getText().toString().length();
//        if(len == 6)
//        {
//            //imgCorrectOtp.setVisibility(View.VISIBLE);
//
//
//        }
    }

    private void initialiseUI()
    {
        TextView txt_Verification = (TextView) findViewById(R.id.txt_Verification);
        txt_Verification.setTypeface(TextFont.opensans_semibold(context));

        text_otp = (TextView) findViewById(R.id.text_otp);
        text_otp.setTypeface(TextFont.opensans_regular(context));
        textNumber = (TextView) findViewById(R.id.textNumber);
        textNumber.setTypeface(TextFont.opensans_regular(context));
        textStar = (TextView) findViewById(R.id.textStar);
        textStar.setTypeface(TextFont.opensans_regular(context));

        edt_otp = (EditText) findViewById(R.id.edt_otp);
        edt_otp.setTypeface(TextFont.opensans_regular(context));
        txt_incorrect_otp = (TextView) findViewById(R.id.txt_incorrect_otp);
        txt_incorrect_otp.setTypeface(TextFont.opensans_regular(context));
        btn_sendOtp = (Button) findViewById(R.id.btn_sendOtp);
        btn_sendOtp.setTypeface(TextFont.opensans_semibold(context));
        btn_changeNo = (TextView) findViewById(R.id.btn_changeNo);
        btn_changeNo.setTypeface(TextFont.opensans_semibold(context));
        text_resend = (TextView) findViewById(R.id.text_resend);
        text_resend.setTypeface(TextFont.opensans_semibold(context));

        //imgCorrectOtp = (ImageView) findViewById(R.id.imgCorrectOtp);

        btn_changeNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public Verify_Otp_Request json_verify_otp_request(String mobile, String countryCode, String otp) {
        Verify_Otp_Request verify_otp_request = new Verify_Otp_Request();
        verify_otp_request.setClient_id(Constcore.client_Id);
        verify_otp_request.setClient_secret(Constcore.client_Secret);
        verify_otp_request.setMobileNumber(mobile);
        verify_otp_request.setCountryCode(countryCode);
        verify_otp_request.setOneTimePassword(otp);
        return verify_otp_request;
    }

    public Otp_Request json_otp_request(String countryCode, String mobile) {
        Otp_Request otp_request = new Otp_Request();
        otp_request.setClient_id(Constcore.client_Id);
        otp_request.setClient_secret(Constcore.client_Secret);
        otp_request.setCountryCode(countryCode);
        otp_request.setMobileNumber(mobile);
        otp_request.setGetGeneratedOTP(true);
        return otp_request;
    }

    public void getHeight()
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

            }
        });

    }


}