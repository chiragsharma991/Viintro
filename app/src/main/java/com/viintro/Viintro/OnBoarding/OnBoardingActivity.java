package com.viintro.Viintro.OnBoarding;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.Onboarding_Request;
import com.viintro.Viintro.Model.Otp_Request;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Reusables.TextFont;
import com.viintro.Viintro.Reusables.Validations;
import com.viintro.Viintro.Webservices.GenerateOtpAPI;

import java.util.Arrays;

import static com.viintro.Viintro.Webservices.GenerateOtpAPI.req_otp_API;

public class OnBoardingActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private SeekBar seekBar;
    private RelativeLayout relativeProfessional, relativeStudent;
    private LinearLayout llProfessional, llStudent, llProfessionalColor, llStudentColor;
    private TextView text_professional, text_student, text_verifiedStudent, text_verifiedProf;
    private EditText edt_mobilenoProfessional, edt_mobilenoStudent;
    private EditText edtUniversity, edtCourse, edtEmailStudent, edtCompany, edtDesignation, edtEmailProf;
    private Button btn_submit, btn_verify_ProfessionalMobNo, btn_verify_StudentMobNo;
    private TextView txt_incorrect_university, txt_incorrect_course, txt_incorrect_email, txt_incorrect_company, txt_incorrect_designation, txt_incorrect_emailProf;
    private View viewUniversity, viewCourse, viewEmailStudent, viewCompany, viewDesignation, viewEmailProf;
    private String university, course, emailStudent, company, designation, emailProf, mobileStudent, mobileProf;
    private Spinner spinnerCountryCodes, spinnerCountryCodes2;
    private String studentCountryCode, profCountryCode;
    SharedPreferences sharedpreferences;
    Configuration_Parameter m_config;
    ArrayAdapter<String> adp3;
    private Context mContext;
    ProgressDialog progressDialog;
    boolean flag_mobile_verification = false;
    private Activity onboarding_activity;
    private String blockCharacterSet = ",";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
       // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_on_boarding);
        mContext = this;
        onboarding_activity = this;
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        m_config = Configuration_Parameter.getInstance();
        getHeight();
        initialiseUI();


    }

    private void initialiseUI() {

        TextInputLayout input_company = (TextInputLayout) findViewById(R.id.input_company);
        input_company.setTypeface(TextFont.opensans_regular(mContext));
        TextInputLayout input_designation = (TextInputLayout) findViewById(R.id.input_designation);
        input_designation.setTypeface(TextFont.opensans_regular(mContext));
        TextInputLayout input_email2 = (TextInputLayout) findViewById(R.id.input_email2);
        input_email2.setTypeface(TextFont.opensans_regular(mContext));
        TextInputLayout input_university = (TextInputLayout) findViewById(R.id.input_university);
        input_university.setTypeface(TextFont.opensans_regular(mContext));
        TextInputLayout input_course = (TextInputLayout) findViewById(R.id.input_course);
        input_course.setTypeface(TextFont.opensans_regular(mContext));
        TextInputLayout input_email = (TextInputLayout) findViewById(R.id.input_email);
        input_email.setTypeface(TextFont.opensans_regular(mContext));
        TextView text_title = (TextView) findViewById(R.id.text_title);
        text_title.setTypeface(TextFont.opensans_semibold(mContext));
        relativeProfessional=(RelativeLayout) findViewById(R.id.relativeProfessional);
        relativeStudent=(RelativeLayout)findViewById(R.id.relativeStudent);
        llProfessional=(LinearLayout) findViewById(R.id.llProfessional);
        llStudent=(LinearLayout)findViewById(R.id.llStudent);
        llProfessionalColor=(LinearLayout) findViewById(R.id.llProfessionalColor);
        llStudentColor=(LinearLayout) findViewById(R.id.llStudentColor);

        text_professional=(TextView) findViewById(R.id.text_professional);
        text_title.setTypeface(TextFont.opensans_semibold(mContext));
        text_student=(TextView) findViewById(R.id.text_student);
        text_title.setTypeface(TextFont.opensans_semibold(mContext));

        edt_mobilenoStudent=(EditText) findViewById(R.id.edt_mobilenoStudent);
        edt_mobilenoProfessional=(EditText) findViewById(R.id.edt_mobilenoProfessional);
        edtUniversity=(EditText) findViewById(R.id.edtUniversity);
        edtUniversity.setTypeface(TextFont.opensans_regular(mContext));
        edtCourse=(EditText) findViewById(R.id.edtCourse);
        edtCourse.setTypeface(TextFont.opensans_regular(mContext));
        edtEmailStudent=(EditText) findViewById(R.id.edtEmailStudent);
        txt_incorrect_university=(TextView) findViewById(R.id.txt_incorrect_university);
        txt_incorrect_course=(TextView) findViewById(R.id.txt_incorrect_course);
        txt_incorrect_email=(TextView) findViewById(R.id.txt_incorrect_email);
        edtCompany=(EditText) findViewById(R.id.edtCompany);
        edtCompany.setTypeface(TextFont.opensans_regular(mContext));
        edtDesignation=(EditText) findViewById(R.id.edtDesignation);
        edtDesignation.setTypeface(TextFont.opensans_regular(mContext));
        edtEmailProf=(EditText) findViewById(R.id.edtEmailProf);
        txt_incorrect_company=(TextView) findViewById(R.id.txt_incorrect_company);
        txt_incorrect_designation=(TextView) findViewById(R.id.txt_incorrect_designation);
        txt_incorrect_emailProf=(TextView) findViewById(R.id.txt_incorrect_emailProf);
        spinnerCountryCodes = (Spinner) findViewById(R.id.spinnerCountryCodes);
        spinnerCountryCodes2 = (Spinner) findViewById(R.id.spinnerCountryCodes2);
        btn_submit=(Button) findViewById(R.id.btn_submit);

        edtEmailStudent.setText(sharedpreferences.getString(m_config.login_email,""));
        edtEmailStudent.setEnabled(false);
        edtEmailProf.setText(sharedpreferences.getString(m_config.login_email,""));
        edtEmailProf.setEnabled(false);

        adp3 = new ArrayAdapter<String>(this, R.layout.spinner_country_codes_items, getResources().getStringArray(R.array.countryCodes));
        adp3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCountryCodes.setAdapter(adp3);
        spinnerCountryCodes2.setAdapter(adp3);

        String [] strb = getResources().getStringArray(R.array.countryCodes);
        int index = Arrays.asList(strb).indexOf("91");
        spinnerCountryCodes.setSelection(index);
        spinnerCountryCodes2.setSelection(index);

        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if (source != null && blockCharacterSet.contains(("" + source))) {
                    return "";
                }
                return null;
            }
        };

        edtUniversity.setFilters(new InputFilter[]{filter});
        edtCompany.setFilters(new InputFilter[]{filter});
        edtDesignation.setFilters(new InputFilter[]{filter});
        edtCourse.setFilters(new InputFilter[]{filter});


        edtUniversity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    edtUniversity.clearFocus();
                    edtCourse.requestFocus();
                    if (Validations.isEmpty(edtUniversity))
                    {
                        txt_incorrect_university.setText(mContext.getResources().getString(R.string.enter_university));
                        txt_incorrect_university.setVisibility(View.VISIBLE);
                        edtUniversity.setBackgroundResource(R.drawable.edittext_red_border);

                    }else
                    {
                        txt_incorrect_university.setText("");
                        txt_incorrect_university.setVisibility(View.GONE);
                        edtUniversity.setBackgroundResource(R.drawable.edittext_orange_border);
                    }
                    handled = true;

                }
                return handled;
            }
        });

        edtCourse.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    dismissCursor();
                    if (Validations.isEmpty(edtCourse))
                    {
                        txt_incorrect_course.setText(mContext.getResources().getString(R.string.enter_course));
                        txt_incorrect_course.setVisibility(View.VISIBLE);
                        edtCourse.setBackgroundResource(R.drawable.edittext_red_border);

                    }else
                    {
                        txt_incorrect_course.setText("");
                        txt_incorrect_course.setVisibility(View.GONE);
                        edtCourse.setBackgroundResource(R.drawable.edittext_orange_border);
//                        btn_submit.setBackgroundResource(R.drawable.bg_orange_lefttoright);
                      //  ((Button)).setTextColor(Color.parseColor("#000000"));
                    }
                    handled = true;

                }
                return handled;
            }
        });

        edtCompany.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    edtCompany.clearFocus();
                    edtDesignation.requestFocus();
                    if (Validations.isEmpty(edtCompany))
                    {
                        txt_incorrect_company.setText(mContext.getResources().getString(R.string.enter_company));
                        txt_incorrect_company.setVisibility(View.VISIBLE);
                        edtCompany.setBackgroundResource(R.drawable.edittext_red_border);

                    }else
                    {
                        txt_incorrect_company.setText("");
                        txt_incorrect_company.setVisibility(View.GONE);
                        edtCompany.setBackgroundResource(R.drawable.edittext_orange_border);
                        btn_submit.setBackgroundResource(R.drawable.bg_orange_lefttoright);
                    }
                    handled = true;

                }
                return handled;
            }
        });

        edtDesignation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    dismissCursor();
                    if (Validations.isEmpty(edtDesignation))
                    {
                        txt_incorrect_designation.setText(mContext.getResources().getString(R.string.enter_title));
                        txt_incorrect_designation.setVisibility(View.VISIBLE);
                        edtDesignation.setBackgroundResource(R.drawable.edittext_red_border);

                    }else
                    {
                        txt_incorrect_designation.setText("");
                        txt_incorrect_designation.setVisibility(View.GONE);
                        edtDesignation.setBackgroundResource(R.drawable.edittext_orange_border);
//                        btn_submit.setBackgroundColor(R.drawable.btn_border_orange);

                    }
                    handled = true;

                }
                return handled;
            }
        });






        edtCourse.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    if (Validations.isEmpty(edtUniversity))
                    {
                        txt_incorrect_university.setText(mContext.getResources().getString(R.string.enter_university));
                        txt_incorrect_university.setVisibility(View.VISIBLE);
                        edtUniversity.setBackgroundResource(R.drawable.edittext_red_border);

                    }else
                    {
                        txt_incorrect_university.setText("");
                        txt_incorrect_university.setVisibility(View.GONE);
                        edtUniversity.setBackgroundResource(R.drawable.edittext_orange_border);
                    }
                }

            }
        });


        edtDesignation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    if (Validations.isEmpty(edtCompany))
                    {
                        txt_incorrect_company.setText(mContext.getResources().getString(R.string.enter_company));
                        txt_incorrect_company.setVisibility(View.VISIBLE);
                        edtCompany.setBackgroundResource(R.drawable.edittext_red_border);

                    }else
                    {
                        txt_incorrect_company.setText("");
                        txt_incorrect_company.setVisibility(View.GONE);
                        edtCompany.setBackgroundResource(R.drawable.edittext_orange_border);
                    }
                }

            }
        });


        edtUniversity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateFieldsStudent();
            }
        });

        edtCourse.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateFieldsStudent();

            }
        });

        edtEmailStudent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtCompany.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
            }

            @Override
            public void afterTextChanged(Editable s) {
                    validateFieldsProf();
            }
        });



        edtDesignation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateFieldsProf();

            }
        });



        llStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (relativeStudent.getVisibility() == View.GONE)
                {
                    btn_submit.setVisibility(View.VISIBLE);
                    //llStudent.setBackgroundColor(R.drawable.bg_orange_lefttoright);
                    llStudent.setVisibility(View.GONE);
                    llStudentColor.setVisibility(View.VISIBLE);
                    llProfessionalColor.setVisibility(View.GONE);
                    llProfessional.setVisibility(View.VISIBLE);
                    relativeStudent.setBackground(getResources().getDrawable(R.drawable.btn_border_orange));
                    relativeStudent.setVisibility(View.VISIBLE);
                    relativeProfessional.setVisibility(View.GONE);
                    edtCompany.setText("");
                    edtDesignation.setText("");
                    edt_mobilenoProfessional.setText("");
                    txt_incorrect_company.setText("");
                    txt_incorrect_company.setVisibility(View.GONE);
                    txt_incorrect_designation.setText("");
                    txt_incorrect_designation.setVisibility(View.GONE);
                    txt_incorrect_emailProf.setText("");
                    txt_incorrect_emailProf.setVisibility(View.GONE);
                    getHeight();


                }
                else
                {
                    getHeight();

                    // llStudent.setBackgroundColor(R.drawable.btn_border_orange);

                  //  llProfessional.setBackgroundColor(Color.WHITE);
                }

            }
        });

        llStudentColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHeight();
                btn_submit.setVisibility(View.GONE);
                llStudent.setVisibility(View.VISIBLE);
                llStudentColor.setVisibility(View.GONE);
                // text_student.setTextColor(R.color.text_onboarding);
                relativeStudent.setVisibility(View.GONE);
                relativeProfessional.setVisibility(View.GONE);
            }
        });

        llProfessional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (relativeProfessional.getVisibility() == View.GONE)
                {
                    btn_submit.setVisibility(View.VISIBLE);
                  //  llStudent.setBackgroundColor(R.drawable.bg_orange_lefttoright);
                  //  llProfessional.setBackgroundColor(Color.BLACK);
                    llProfessional.setVisibility(View.GONE);
                    llProfessionalColor.setVisibility(View.VISIBLE);
                    llStudentColor.setVisibility(View.GONE);
                    llStudent.setVisibility(View.VISIBLE);
                  //  text_student.setTextColor(Color.BLACK);
                  //  text_professional.setTextColor(0xFFFFFFFF);
                    relativeStudent.setVisibility(View.GONE);
                    relativeProfessional.setBackground(getResources().getDrawable(R.drawable.btn_border_orange));
                    relativeProfessional.setVisibility(View.VISIBLE);
                    edtUniversity.setText("");
                    edtCourse.setText("");
                    edt_mobilenoStudent.setText("");
                    txt_incorrect_university.setText("");
                    txt_incorrect_university.setVisibility(View.GONE);
                 //   viewUniversity.setBackgroundColor(Color.BLACK);
                    txt_incorrect_course.setText("");
                    txt_incorrect_course.setVisibility(View.GONE);
                   // viewCourse.setBackgroundColor(Color.BLACK);
                    txt_incorrect_email.setText("");
                    txt_incorrect_email.setVisibility(View.GONE);
                    getHeight();
                  //  viewEmailStudent.setBackgroundColor(Color.BLACK);

                }
                else{
                    getHeight();
                }
            }
        });

        llProfessionalColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHeight();
                btn_submit.setVisibility(View.GONE);
                llProfessional.setVisibility(View.VISIBLE);
                llProfessionalColor.setVisibility(View.GONE);
                // llStudent.setBackgroundColor(R.drawable.btn_border_orange);
                // llProfessional.setBackgroundColor(Color.WHITE);
                // text_professional.setTextColor(R.color.text_onboarding);
                relativeProfessional.setVisibility(View.GONE);
                relativeStudent.setVisibility(View.GONE);
            }
        });

//
        edt_mobilenoStudent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    if (Validations.isEmpty(edtUniversity))
                    {
                        txt_incorrect_university.setText(mContext.getResources().getString(R.string.enter_university));
                        txt_incorrect_university.setVisibility(View.VISIBLE);
                        edtUniversity.setBackgroundResource(R.drawable.edittext_red_border);

                    }else
                    {
                        txt_incorrect_university.setText("");
                        txt_incorrect_university.setVisibility(View.GONE);
                        edtUniversity.setBackgroundResource(R.drawable.edittext_orange_border);
                    }

                    if (Validations.isEmpty(edtCourse))
                    {
                        txt_incorrect_course.setText(mContext.getResources().getString(R.string.enter_course));
                        txt_incorrect_course.setVisibility(View.VISIBLE);
                        edtCourse.setBackgroundResource(R.drawable.edittext_red_border);

                    }else
                    {
                        txt_incorrect_course.setText("");
                        txt_incorrect_course.setVisibility(View.GONE);
                        edtCourse.setBackgroundResource(R.drawable.edittext_orange_border);
                    }
                }
            }
        });

        edt_mobilenoProfessional.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    if (Validations.isEmpty(edtCompany))
                    {
                        txt_incorrect_company.setText(mContext.getResources().getString(R.string.enter_company));
                        txt_incorrect_company.setVisibility(View.VISIBLE);
                        edtCompany.setBackgroundResource(R.drawable.edittext_red_border);

                    }else
                    {
                        txt_incorrect_company.setText("");
                        txt_incorrect_company.setVisibility(View.GONE);
                        edtCompany.setBackgroundResource(R.drawable.edittext_orange_border);
                    }

                    if (Validations.isEmpty(edtDesignation))
                    {
                        txt_incorrect_designation.setText(mContext.getResources().getString(R.string.enter_title));
                        txt_incorrect_designation.setVisibility(View.VISIBLE);
                        edtDesignation.setBackgroundResource(R.drawable.edittext_red_border);

                    }else
                    {
                        txt_incorrect_designation.setText("");
                        txt_incorrect_designation.setVisibility(View.GONE);
                        edtDesignation.setBackgroundResource(R.drawable.edittext_orange_border);
                    }
                }
            }
        });

        edt_mobilenoStudent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_mobilenoStudent.getWindowToken(), 0);
                    edt_mobilenoStudent.clearFocus();
                    if(edt_mobilenoStudent.getText().toString().trim().length() > 0)
                    {
                        edt_mobilenoStudent.setBackgroundResource(R.drawable.edittext_orange_border);
                    }
                    handled = true;

                }
                return handled;
            }

        });

        edt_mobilenoProfessional.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_mobilenoProfessional.getWindowToken(), 0);
                    edt_mobilenoProfessional.clearFocus();
                    if(edt_mobilenoProfessional.getText().toString().trim().length() > 0)
                    {
                        edt_mobilenoProfessional.setBackgroundResource(R.drawable.edittext_orange_border);
                    }
                    handled = true;

                }
                return handled;
            }

        });


        edtEmailProf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Validations.isEmpty(edtEmailProf)) {
                    txt_incorrect_emailProf.setText("Please enter email address");
                    txt_incorrect_emailProf.setVisibility(View.VISIBLE);
                //    viewEmailProf.setBackgroundColor(Color.RED);

                } else if (!Validations.isEmpty(edtEmailProf) && !Validations.isValidEmailId(edtEmailProf.getText().toString().replaceAll("\\s+", " ").trim())) {
                    txt_incorrect_emailProf.setText("Please enter valid email id");
                    txt_incorrect_emailProf.setVisibility(View.VISIBLE);
                //    viewEmailProf.setBackgroundColor(Color.RED);
                } else {
                    txt_incorrect_emailProf.setText("");
                    txt_incorrect_emailProf.setVisibility(View.GONE);
               //     viewEmailProf.setBackgroundColor(Color.BLACK);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismissCursor();

                if(edt_mobilenoStudent.getText().toString().trim().length() > 0)
                {
                    edt_mobilenoStudent.setBackgroundResource(R.drawable.edittext_orange_border);
                }
                else if(edt_mobilenoProfessional.getText().toString().trim().length() > 0)
                {
                    edt_mobilenoProfessional.setBackgroundResource(R.drawable.edittext_orange_border);
                }


                if (relativeStudent.getVisibility() == View.VISIBLE) {
                    if (Validations.isEmpty(edtUniversity) || Validations.isEmpty(edtCourse) || Validations.isEmpty(edtEmailStudent)) {
                        if (Validations.isEmpty(edtUniversity)) {
                            txt_incorrect_university.setText("Please enter university name");
                            txt_incorrect_university.setVisibility(View.VISIBLE);
                           // viewUniversity.setBackgroundColor(Color.RED);
                            edtUniversity.setBackgroundResource(R.drawable.edittext_red_border);

                        } else {
                            txt_incorrect_university.setText("");
                            txt_incorrect_university.setVisibility(View.GONE);
                            edtUniversity.setBackgroundResource(R.drawable.edittext_orange_border);
                            // viewUniversity.setBackgroundColor(Color.BLACK);
                        }

                        if (Validations.isEmpty(edtCourse)) {
                            txt_incorrect_course.setText("Please enter course");
                            txt_incorrect_course.setVisibility(View.VISIBLE);
                            edtCourse.setBackgroundResource(R.drawable.edittext_red_border);

                            //  viewCourse.setBackgroundColor(Color.RED);
                        } else {
                            txt_incorrect_course.setText("");
                            txt_incorrect_course.setVisibility(View.GONE);
                            edtCourse.setBackgroundResource(R.drawable.edittext_orange_border);
                            //  viewCourse.setBackgroundColor(Color.BLACK);
                        }

                        if (Validations.isEmpty(edtEmailStudent)) {
                            txt_incorrect_email.setText("Please enter email address");
                            txt_incorrect_email.setVisibility(View.VISIBLE);
                            edtEmailStudent.setBackgroundResource(R.drawable.edittext_red_border);
                            //  viewEmailStudent.setBackgroundColor(Color.RED);

                        } else if (!Validations.isEmpty(edtEmailStudent) && !Validations.isValidEmailId(edtEmailStudent.getText().toString().replaceAll("\\s+", " ").trim())) {
                            txt_incorrect_email.setText("Please enter valid email id");
                            txt_incorrect_email.setVisibility(View.VISIBLE);
                            edtEmailStudent.setBackgroundResource(R.drawable.edittext_red_border);
                            //  viewEmailStudent.setBackgroundColor(Color.RED);
                        } else {
                            txt_incorrect_email.setText("");
                            txt_incorrect_email.setVisibility(View.GONE);
                            edtEmailStudent.setBackgroundResource(R.drawable.edittext_orange_border);
                            // viewEmailStudent.setBackgroundColor(Color.BLACK);
                        }
                    } else {

                        if (!Validations.isValidEmailId(edtEmailStudent.getText().toString().replaceAll("\\s+", " ").trim())) {

                            txt_incorrect_email.setText("Please enter valid email address");
                            txt_incorrect_email.setVisibility(View.VISIBLE);
                            edtEmailStudent.setBackgroundResource(R.drawable.edittext_red_border);

                        } else if ((!edt_mobilenoStudent.getText().toString().trim().equals("")) && edt_mobilenoStudent.getText().toString().length() < 10){

                            Toast.makeText(OnBoardingActivity.this, "Please check your mobile number", Toast.LENGTH_SHORT).show();

                        }else {
                            dismissCursor();

                            if(!edt_mobilenoStudent.getText().toString().trim().equals("") && flag_mobile_verification == false){
                                mobileStudent = edt_mobilenoStudent.getText().toString().replaceAll("\\s+", " ").trim();
                                if(CommonFunctions.chkStatus(mContext)){
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(edtUniversity.getWindowToken(), 0);
                                    imm.hideSoftInputFromWindow(edtCourse.getWindowToken(), 0);
                                    imm.hideSoftInputFromWindow(edtEmailStudent.getWindowToken(), 0);
                                    imm.hideSoftInputFromWindow(edt_mobilenoStudent.getWindowToken(), 0);
                                    edtUniversity.clearFocus();
                                    edtCourse.clearFocus();
                                    edtEmailStudent.clearFocus();
                                    edt_mobilenoStudent.clearFocus();
                                    studentCountryCode = spinnerCountryCodes.getSelectedItem().toString();
                                    CommonFunctions.sDialog(mContext, "Loading");
                                    Otp_Request otp_request = json_otp_request(studentCountryCode, mobileStudent);
                                    req_otp_API(mContext, otp_request, onboarding_activity,"student");
                                }
                                else{

                                    CommonFunctions.displayToast(mContext,getResources().getString(R.string.network_connection));

                                }
                            }else{
                                if (CommonFunctions.chkStatus(mContext)) {

                                    university = edtUniversity.getText().toString().replaceAll("\\s+", " ").trim();
                                    course = edtCourse.getText().toString().replaceAll("\\s+", " ").trim();
                                    emailStudent = edtEmailStudent.getText().toString().replaceAll("\\s+", " ").trim();
                                    mobileStudent = edt_mobilenoStudent.getText().toString().replaceAll("\\s+", " ").trim();
                                    studentCountryCode = spinnerCountryCodes.getSelectedItem().toString();

                                    Intent int_location = new Intent(OnBoardingActivity.this, LocationActivity.class);
                                    int_location.putExtra("user_type", "student");
                                    int_location.putExtra("university_company", university);
                                    int_location.putExtra("course_designation", course);
                                    int_location.putExtra("country_code", studentCountryCode);
                                    int_location.putExtra("mobno", mobileStudent);
                                    startActivity(int_location);
                                    // json_studentinfo(university,course,emailStudent,mobileStudent);
                                } else {
                                    CommonFunctions.displayToast(mContext, getResources().getString(R.string.network_connection));

                                }

                            }

                        }
                    }

                } else {

                    if (Validations.isEmpty(edtCompany) || Validations.isEmpty(edtDesignation) || Validations.isEmpty(edtEmailProf)) {
                        if (Validations.isEmpty(edtCompany)) {
                            txt_incorrect_company.setText("Please enter company name");
                            txt_incorrect_company.setVisibility(View.VISIBLE);
                            edtCompany.setBackgroundResource(R.drawable.edittext_red_border);
                            // viewCompany.setBackgroundColor(Color.RED);
                        } else {
                            txt_incorrect_company.setText("");
                            txt_incorrect_company.setVisibility(View.GONE);
                            edtCompany.setBackgroundResource(R.drawable.edittext_orange_border);
                            // viewCompany.setBackgroundColor(Color.BLACK);
                        }

                        if (Validations.isEmpty(edtDesignation)) {
                            txt_incorrect_designation.setText("Please enter designation");
                            txt_incorrect_designation.setVisibility(View.VISIBLE);
                            edtDesignation.setBackgroundResource(R.drawable.edittext_red_border);
                            // viewDesignation.setBackgroundColor(Color.RED);
                        } else {
                            txt_incorrect_designation.setText("");
                            txt_incorrect_designation.setVisibility(View.GONE);
                            edtDesignation.setBackgroundResource(R.drawable.edittext_orange_border);
                            //  viewDesignation.setBackgroundColor(Color.BLACK);
                        }

                        if (Validations.isEmpty(edtEmailProf)) {
                            txt_incorrect_emailProf.setText("Please enter email address");
                            txt_incorrect_emailProf.setVisibility(View.VISIBLE);
                            edtEmailProf.setBackgroundResource(R.drawable.edittext_red_border);
                            // viewEmailProf.setBackgroundColor(Color.RED);

                        } else if (!Validations.isEmpty(edtEmailProf) && !Validations.isValidEmailId(edtEmailProf.getText().toString().replaceAll("\\s+", " ").trim())) {
                            txt_incorrect_emailProf.setText("Please enter valid email id");
                            txt_incorrect_emailProf.setVisibility(View.VISIBLE);
                            edtEmailProf.setBackgroundResource(R.drawable.edittext_red_border);
                            // viewEmailProf.setBackgroundColor(Color.RED);
                        } else {
                            txt_incorrect_emailProf.setText("");
                            txt_incorrect_emailProf.setVisibility(View.GONE);
                            edtEmailProf.setBackgroundResource(R.drawable.edittext_orange_border);
                            //  viewEmailProf.setBackgroundColor(Color.BLACK);
                        }

                    } else {


                        if (!Validations.isValidEmailId(edtEmailProf.getText().toString().replaceAll("\\s+", " ").trim())) {

                            txt_incorrect_emailProf.setText("Please enter valid email address");
                            txt_incorrect_emailProf.setVisibility(View.VISIBLE);
                            edtEmailProf.setBackgroundResource(R.drawable.edittext_red_border);

                        } else if ((!edt_mobilenoProfessional.getText().toString().trim().equals("")) && edt_mobilenoProfessional.getText().toString().length() < 10) {

                            Toast.makeText(OnBoardingActivity.this, "Please check your mobile number", Toast.LENGTH_SHORT).show();

                        }else {
                            dismissCursor();

                            if(!edt_mobilenoProfessional.getText().toString().trim().equals("") && flag_mobile_verification == false){
                                mobileProf = edt_mobilenoProfessional.getText().toString().trim();

                                    if(CommonFunctions.chkStatus(mContext)) {
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(edtCompany.getWindowToken(), 0);
                                        imm.hideSoftInputFromWindow(edtDesignation.getWindowToken(), 0);
                                        imm.hideSoftInputFromWindow(edtEmailProf.getWindowToken(), 0);
                                        imm.hideSoftInputFromWindow(edt_mobilenoProfessional.getWindowToken(), 0);
                                        edtCompany.clearFocus();
                                        edtDesignation.clearFocus();
                                        edtEmailProf.clearFocus();
                                        edt_mobilenoProfessional.clearFocus();

                                        profCountryCode = spinnerCountryCodes2.getSelectedItem().toString();
                                        CommonFunctions.sDialog(mContext, "Loading");
                                        Otp_Request otp_request = json_otp_request(profCountryCode, mobileProf);
                                        req_otp_API(mContext, otp_request, onboarding_activity, "professional");
                                    }
                                    else{

                                        CommonFunctions.displayToast(mContext,getResources().getString(R.string.network_connection));

                                    }

                            }else{
                                if (CommonFunctions.chkStatus(mContext)) {
                                    Toast.makeText(OnBoardingActivity.this, "Submit", Toast.LENGTH_SHORT).show();
                                    company = edtCompany.getText().toString().replaceAll("\\s+", " ").trim();
                                    designation = edtDesignation.getText().toString().replaceAll("\\s+", " ").trim();
                                    emailProf = edtEmailProf.getText().toString().replaceAll("\\s+", " ").trim();
                                    mobileProf = edt_mobilenoProfessional.getText().toString().replaceAll("\\s+", " ").trim();
                                    profCountryCode = spinnerCountryCodes2.getSelectedItem().toString();

                                    //sat 18feb

                                    Intent int_location = new Intent(OnBoardingActivity.this, LocationActivity.class);
                                    int_location.putExtra("user_type","professional");
                                    int_location.putExtra("university_company",company);
                                    int_location.putExtra("course_designation",designation);
                                    int_location.putExtra("country_code",profCountryCode);
                                    int_location.putExtra("mobno",mobileProf);
                                    startActivity(int_location);


                                } else {
                                    CommonFunctions.displayToast(mContext, getResources().getString(R.string.network_connection));

                                }
                            }


                        }
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

//    public void enableVerifyStudentReady() {
//
//        int len = edt_mobilenoStudent.getText().toString().length();
//        if(len == 10){
//            btn_verify_StudentMobNo.setVisibility(View.VISIBLE);
//
//        }
//    }

//    public void enableVerifyProfReady() {
//
//        int len = edt_mobilenoProfessional.getText().toString().length();
//        if(len == 10) {
//            btn_verify_ProfessionalMobNo.setVisibility(View.VISIBLE);
//        }
//    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void dismissCursor()
    {
        //Meghana
        //Clear Focus from all edit texts
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtUniversity.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edtCourse.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edtEmailStudent.getWindowToken(), 0);
        edtUniversity.clearFocus();
        edtCourse.clearFocus();
        edtEmailStudent.clearFocus();
        txt_incorrect_email.setText("");
        txt_incorrect_email.setVisibility(View.GONE);
        txt_incorrect_university.setText("");
        txt_incorrect_university.setVisibility(View.GONE);
        txt_incorrect_course.setText("");
        txt_incorrect_course.setVisibility(View.GONE);
//        viewUniversity.setBackgroundColor(Color.BLACK);
//        viewCourse.setBackgroundColor(Color.BLACK);
//        viewEmailStudent.setBackgroundColor(Color.BLACK);

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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (Constcore.REQUEST_CODE_ONBOARDING): {
                if(data!= null){
                    flag_mobile_verification = true;
                    llStudent.setClickable(false);
                    llProfessional.setClickable(false);
                    if(data.getExtras().getString("from").equals("student"))
                    {

                        //text_verifiedStudent.setVisibility(View.VISIBLE);
                        if (CommonFunctions.chkStatus(mContext)) {

                            university = edtUniversity.getText().toString().replaceAll("\\s+", " ").trim();
                            course = edtCourse.getText().toString().replaceAll("\\s+", " ").trim();
                            emailStudent = edtEmailStudent.getText().toString().replaceAll("\\s+", " ").trim();
                            mobileStudent = edt_mobilenoStudent.getText().toString().replaceAll("\\s+", " ").trim();
                            studentCountryCode = spinnerCountryCodes.getSelectedItem().toString();

                            Intent int_location = new Intent(OnBoardingActivity.this, LocationActivity.class);
                            int_location.putExtra("user_type", "student");
                            int_location.putExtra("university_company", university);
                            int_location.putExtra("course_designation", course);
                            int_location.putExtra("country_code", studentCountryCode);
                            int_location.putExtra("mobno", mobileStudent);
                            startActivity(int_location);
                            // json_studentinfo(university,course,emailStudent,mobileStudent);
                        } else {
                            CommonFunctions.displayToast(mContext, getResources().getString(R.string.network_connection));

                        }
                       // btn_verify_StudentMobNo.setVisibility(View.GONE);

                    }
                    else if(data.getExtras().getString("from").equals("professional"))
                    {
                       // text_verifiedProf.setVisibility(View.VISIBLE);
                        if (CommonFunctions.chkStatus(mContext)) {
                            Toast.makeText(OnBoardingActivity.this, "Submit", Toast.LENGTH_SHORT).show();
                            company = edtCompany.getText().toString().replaceAll("\\s+", " ").trim();
                            designation = edtDesignation.getText().toString().replaceAll("\\s+", " ").trim();
                            emailProf = edtEmailProf.getText().toString().replaceAll("\\s+", " ").trim();
                            mobileProf = edt_mobilenoProfessional.getText().toString().replaceAll("\\s+", " ").trim();
                            profCountryCode = spinnerCountryCodes2.getSelectedItem().toString();

                            //sat 18feb

                            Intent int_location = new Intent(OnBoardingActivity.this, LocationActivity.class);
                            int_location.putExtra("user_type","professional");
                            int_location.putExtra("university_company",company);
                            int_location.putExtra("course_designation",designation);
                            int_location.putExtra("country_code",profCountryCode);
                            int_location.putExtra("mobno",mobileProf);
                            startActivity(int_location);


                        } else {
                            CommonFunctions.displayToast(mContext, getResources().getString(R.string.network_connection));

                        }
                       // btn_verify_ProfessionalMobNo.setVisibility(View.GONE);

                    }
                }




            }
            break;
        }
    }

    private void validateFieldsStudent()
    {
        if(edtUniversity.getText().toString().replaceAll("\\s+", " ").trim().length()>0 && edtCourse.getText().toString().replaceAll("\\s+", " ").trim().length()>0)
        {
            btn_submit.setBackgroundResource(R.drawable.btn_border_orange);
            btn_submit.setEnabled(true);
            btn_submit.setTextColor(Color.parseColor("#ff704c"));
        }
        else
        {
            btn_submit.setBackgroundResource(R.drawable.btn_border_grey);
            btn_submit.setEnabled(false);
            btn_submit.setTextColor(Color.parseColor("#d6d6d6"));

        }


    }

    private void validateFieldsProf()
    {
        if(edtCompany.getText().toString().replaceAll("\\s+", " ").trim().length()>0 && edtDesignation.getText().toString().replaceAll("\\s+", " ").trim().length()>0)
        {
            btn_submit.setBackgroundResource(R.drawable.btn_border_orange);
            btn_submit.setEnabled(true);
            btn_submit.setTextColor(Color.parseColor("#ff704c"));
        }
        else
        {
            btn_submit.setBackgroundResource(R.drawable.btn_border_grey);
            btn_submit.setEnabled(false);
            btn_submit.setTextColor(Color.parseColor("#d6d6d6"));

        }


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

            }
        });
        return height[0];
    }

}
