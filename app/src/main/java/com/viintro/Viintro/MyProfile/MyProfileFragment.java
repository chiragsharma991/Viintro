package com.viintro.Viintro.MyProfile;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Landing.SampleVideoFullScreen;
import com.viintro.Viintro.Model.Colleges;
import com.viintro.Viintro.Model.Companies;
import com.viintro.Viintro.Model.Designations;
import com.viintro.Viintro.Model.MyProfile_Response;
import com.viintro.Viintro.Model.Others;
import com.viintro.Viintro.Model.Qualifications;
import com.viintro.Viintro.Model.Schools;
import com.viintro.Viintro.Model.Skills;
import com.viintro.Viintro.Model.Videos;
import com.viintro.Viintro.PublicProfile.PublicProfileActivity;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Reusables.TextFont;
import com.viintro.Viintro.VideoRecord.VideoRecordingActivity;
import com.viintro.Viintro.ViintroApplication;
import com.viintro.Viintro.Webservices.AccessTokenAPI;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import static com.viintro.Viintro.Constants.Configuration_Parameter.intro_video_myprofile;
import static com.viintro.Viintro.Constants.Configuration_Parameter.profile_colleges_list;
import static com.viintro.Viintro.Constants.Configuration_Parameter.profile_companies_list;
import static com.viintro.Viintro.Constants.Configuration_Parameter.profile_designations_list;
import static com.viintro.Viintro.Constants.Configuration_Parameter.profile_others_list;
import static com.viintro.Viintro.Constants.Configuration_Parameter.profile_qualifications_list;
import static com.viintro.Viintro.Constants.Configuration_Parameter.profile_schools_list;
import static com.viintro.Viintro.Constants.Configuration_Parameter.profile_skills_list;
import static com.viintro.Viintro.Constants.Configuration_Parameter.profile_video_list;
import static com.viintro.Viintro.HomeFeed.HomeActivity.img_settings;
import static com.viintro.Viintro.HomeFeed.HomeActivity.rel_search;

/**
 * Created by rkanawade on 23/02/17.
 */

public class MyProfileFragment extends Fragment
{

    private Button btn_add_contactdetails, btn_add_colleges, btn_add_qualification, btn_add_school, btn_add_skills, btn_other, btn_add_designation, btn_add_company, btn_upload_videos;
    private TextView text_profile, text_website, text_colleges, text_qualification, text_schools, text_skills, text_others, text_designation, text_company, text_ProfileName, text_profile_designation, text_profile_company, text_profile_location, text_phone;
    private TextView text_profile_views, text_profile_university, text_profile_course, txt_followers_count, text_strength, text_profile_video, text_who_viewed_profile, text_live_videos, text_live_videos_profile;
    private ImageView img_edit_introvideo, img_editContacts, img_editColleges, img_editQualification, img_editSchools, img_editSkills, img_editOthers, img_editDesignation, img_editCompany, img_profile, img_editProfile;
    private RelativeLayout relative_contact_details, relative_colleges_details, relative_qualification_details, relative_schools_details, relative_skills_details;
    private RelativeLayout relative_others_details, relative_designation_details, relative_company_details;
    private SeekBar seekBar;
    private LinearLayout llPhone, llWebsite, llProfile;
    RecyclerView mRecyclerView;
    public static ProfileVideosAdapter profile_videos_adapter;
    RecyclerView.LayoutManager mLayoutManager;
    Configuration_Parameter m_config;
    SharedPreferences sharedpreferences;
    RelativeLayout rlTitle;
    public static TextView txt_following_count;
    public static TextView text_profile_strength;
    public static  ImageView img_intro_video_thumbnail;
    public static  Context contextmyprofile;
    View v;
    Context context;


    public static MyProfileFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        MyProfileFragment myProfileFragment = new MyProfileFragment();
        myProfileFragment.setArguments(args);
        return myProfileFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rel_search.setVisibility(View.VISIBLE);
        img_settings.setVisibility(View.VISIBLE);
        context = getContext();
        contextmyprofile = getContext();
        return inflater.inflate(R.layout.my_profile_fragment, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        m_config = Configuration_Parameter.getInstance();
        v = getView();

        if (profile_video_list == null) {
            profile_video_list = new ArrayList();

        }
        if (intro_video_myprofile == null) {
            intro_video_myprofile = new Videos();
        }

        getHeight(v);

        CommonFunctions.sDialog(context, "Loading data..");

        if (CommonFunctions.chkStatus(context))
        {
            req_myprofile_API(context);
        }
        else
        {
            CommonFunctions.displayToast(context,getResources().getString(R.string.network_connection));
            CommonFunctions.hDialog();
            return;
        }

        initialiseUI();

    }

    private void initialiseUI()
    {
        btn_add_contactdetails = (Button) v.findViewById(R.id.btn_add_contactdetails);
        // btn_add_colleges = (Button) v.findViewById(R.id.btn_add_colleges);
        btn_add_qualification = (Button) v.findViewById(R.id.btn_add_qualification);
        btn_add_school = (Button) v.findViewById(R.id.btn_add_school);
        btn_add_skills = (Button) v.findViewById(R.id.btn_add_skills);
        btn_other = (Button) v.findViewById(R.id.btn_other);
        btn_add_designation = (Button) v.findViewById(R.id.btn_add_designation);
        btn_add_company = (Button) v.findViewById(R.id.btn_add_company);
        txt_following_count =  (TextView) v.findViewById(R.id.txt_following_count);
        txt_following_count.setTypeface(TextFont.opensans_bold(context));
        txt_followers_count =  (TextView) v.findViewById(R.id.txt_followers_count);
        txt_followers_count.setTypeface(TextFont.opensans_bold(context));
        btn_upload_videos = (Button) v.findViewById(R.id.btn_upload_videos);
        text_profile = (TextView) v.findViewById(R.id.text_profile);
        text_website = (TextView) v.findViewById(R.id.text_website);
        text_strength = (TextView) v.findViewById(R.id.text_strength);
        text_strength.setTypeface(TextFont.opensans_semibold(context));
        text_profile_video = (TextView) v.findViewById(R.id.text_profile_video);
        text_profile_video.setTypeface(TextFont.opensans_bold(context));
        // text_colleges = (TextView) v.findViewById(R.id.text_colleges);
        text_qualification = (TextView) v.findViewById(R.id.text_qualification);
        text_schools = (TextView) v.findViewById(R.id.text_schools);
        text_skills = (TextView) v.findViewById(R.id.text_skills);
        text_others = (TextView) v.findViewById(R.id.text_others);
        text_designation = (TextView) v.findViewById(R.id.text_designation);
        text_company = (TextView) v.findViewById(R.id.text_company);
        text_ProfileName = (TextView) v.findViewById(R.id.text_ProfileName);
        text_ProfileName.setTypeface(TextFont.opensans_bold(context));
        text_profile_designation = (TextView) v.findViewById(R.id.text_profile_designation);
        text_profile_designation.setTypeface(TextFont.opensans_regular(context));
        text_profile_company = (TextView) v.findViewById(R.id.text_profile_company);
        text_profile_company.setTypeface(TextFont.opensans_regular(context));
        text_profile_location = (TextView) v.findViewById(R.id.text_profile_location);
        text_profile_location.setTypeface(TextFont.opensans_regular(context));

        text_profile_views = (TextView) v.findViewById(R.id.text_profile_views);
        text_profile_views.setTypeface(TextFont.opensans_bold(context));
        text_live_videos = (TextView) v.findViewById(R.id.text_live_videos);
        text_live_videos.setTypeface(TextFont.opensans_bold(context));
        text_who_viewed_profile = (TextView) v.findViewById(R.id.text_who_viewed_profile);
        text_who_viewed_profile.setTypeface(TextFont.opensans_regular(context));
        text_live_videos_profile = (TextView) v.findViewById(R.id.text_live_videos_profile);
        text_live_videos_profile.setTypeface(TextFont.opensans_regular(context));
        text_profile_strength = (TextView) v.findViewById(R.id.text_profile_strength);
        text_profile_strength.setTypeface(TextFont.opensans_bold(context));
        text_phone = (TextView) v.findViewById(R.id.text_phone);
        // text_profile_university = (TextView) v.findViewById(R.id.text_profile_university);
        // text_profile_course = (TextView) v.findViewById(R.id.text_profile_course);
        img_edit_introvideo = (ImageView) v.findViewById(R.id.img_edit_introvideo);
        img_editContacts = (ImageView) v.findViewById(R.id.img_editContacts);
        //  img_editColleges = (ImageView) v.findViewById(R.id.img_editColleges);
        img_editQualification = (ImageView) v.findViewById(R.id.img_editQualification);
        img_editSchools = (ImageView) v.findViewById(R.id.img_editSchools);
        img_editSkills = (ImageView) v.findViewById(R.id.img_editSkills);
        img_editOthers = (ImageView) v.findViewById(R.id.img_editOthers);
        img_editDesignation = (ImageView) v.findViewById(R.id.img_editDesignation);
        img_editCompany = (ImageView) v.findViewById(R.id.img_editCompany);
        img_intro_video_thumbnail = (ImageView) v.findViewById(R.id.intro_video_thumbnail);
        img_profile = (ImageView) v.findViewById(R.id.img_profile);
        img_editProfile = (ImageView) v.findViewById(R.id.img_editProfile);
//        relative_contact_details = (RelativeLayout) v.findViewById(R.id.relative_contact_details);
//        //relative_colleges_details = (RelativeLayout) v.findViewById(R.id.relative_colleges_details);
//        relative_qualification_details = (RelativeLayout) v.findViewById(R.id.relative_qualification_details);
//        relative_schools_details = (RelativeLayout) v.findViewById(R.id.relative_schools_details);
//        relative_skills_details = (RelativeLayout) v.findViewById(R.id.relative_skills_details);
//        relative_others_details = (RelativeLayout) v.findViewById(R.id.relative_others_details);
//        relative_designation_details = (RelativeLayout) v.findViewById(R.id.relative_designation_details);
//        relative_company_details = (RelativeLayout) v.findViewById(R.id.relative_company_details);
//        rlTitle = (RelativeLayout) v.findViewById(R.id.rlTitle);
        seekBar = (SeekBar) v.findViewById(R.id.seekBar);
//        llPhone = (LinearLayout) v.findViewById(R.id.llPhone);
//        llProfile = (LinearLayout) v.findViewById(R.id.llProfile);
//        llWebsite = (LinearLayout) v.findViewById(R.id.llWebsite);
//
//        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
//        mRecyclerView.setHasFixedSize(true);
//
//        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        getDataInList();

//        if(profile_video_list.size() == 3)
//        {
//            btn_upload_videos.setEnabled(false);
//        }
//        else
//        {
//            btn_upload_videos.setEnabled(true);
//        }
//
        img_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settings);

            }
        });

        String name = sharedpreferences.getString(m_config.login_fullname,null);
        String designation_profile = sharedpreferences.getString(m_config.Designation,null);
        String company = sharedpreferences.getString(m_config.Company,null);
        String university = sharedpreferences.getString(m_config.University,null);
        String course = sharedpreferences.getString(m_config.Course,null);
        String profilecount = sharedpreferences.getString(m_config.Profile_view_count,null);
        String followingcount = sharedpreferences.getString(m_config.Profile_following_count,null);
        String followerscount = sharedpreferences.getString(m_config.Profile_followers_count,null);
        String livevidoescount = sharedpreferences.getString(m_config.Profile_live_videos,null);
//        String profile_mobno = sharedpreferences.getString(m_config.Mobile_Number,null);
//        String profile_url = sharedpreferences.getString(m_config.profile_url,null);
//        String profile_website = sharedpreferences.getString(m_config.profile_website,null);
        String thumbnail = intro_video_myprofile.getThumbnail();
        String display_pic = sharedpreferences.getString(m_config.login_display_pic,null);

        int profile_strength = sharedpreferences.getInt(m_config.Profile_Strength,0);

        if(!(profile_strength == 0)){
            text_profile_strength.setText(profile_strength+"%");
            // seekBar.setMax(100);
            seekBar.setProgress(profile_strength);
        }

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        String usertype = sharedpreferences.getString(m_config.Profile_user_type,null);
//        if(usertype != null) {
//            //if company data is present in profile response, set that data in respective relative layout
//            if (profile_companies_list == null || profile_companies_list.size() == 0) {
//
//                    btn_add_company.setVisibility(View.VISIBLE);
//                    relative_company_details.setVisibility(View.GONE);
//
//
//            }
//            else
//            {
//                    text_company.setText("");
//                    btn_add_company.setVisibility(View.GONE);
//                    relative_company_details.setVisibility(View.VISIBLE);
//
//                    String com = "";
//
//                    for (int i = 0; i < profile_companies_list.size(); i++) {
//
//                        if (i == profile_companies_list.size() - 1) {
//                            String val = profile_companies_list.get(i).toString().trim().substring(0, 1).toUpperCase() + profile_companies_list.get(i).toString().trim().substring(1);
//                            Log.e("val==", " " + val);
//                            com += val;
//
//                        } else {
//
//                            String val = profile_companies_list.get(i).toString().trim().substring(0, 1).toUpperCase() + profile_companies_list.get(i).toString().trim().substring(1);
//                            Log.e("val=---=", " " + val);
//                            com += val + ", ";
//
//                        }
//
//                        text_company.setText(com.replaceAll("\\[", "").replaceAll("\\]", ""));
//
//                    }
//
//            }
//
//            //if designation data is present in profile response, set that data in respective relative layout
//            if (profile_designations_list == null || profile_designations_list.size() == 0)
//            {
//
//                    btn_add_designation.setVisibility(View.VISIBLE);
//                    relative_designation_details.setVisibility(View.GONE);
//
//            }
//            else
//            {
//
//                    text_designation.setText("");
//                    btn_add_designation.setVisibility(View.GONE);
//                    relative_designation_details.setVisibility(View.VISIBLE);
//
//                    String design = "";
//
//                    for (int i = 0; i < profile_designations_list.size(); i++) {
//
//                        if (i == profile_designations_list.size() - 1) {
//                            String val = profile_designations_list.get(i).toString().trim().substring(0, 1).toUpperCase() + profile_designations_list.get(i).toString().trim().substring(1);
//                            Log.e("val==", " " + val);
//                            design += val;
//
//                        } else {
//
//                            String val = profile_designations_list.get(i).toString().trim().substring(0, 1).toUpperCase() + profile_designations_list.get(i).toString().trim().substring(1);
//                            Log.e("val=---=", " " + val);
//                            design += val + ", ";
//
//                        }
//
//                        text_designation.setText(design.replaceAll("\\[", "").replaceAll("\\]", ""));
//
//                    }
//            }
//
//           // }
//        }

        if(!(name == null)) {
            text_ProfileName.setText(name);
        }

        if(usertype != null){

            if(usertype.equals("student")){
                if(!(university == null)) {

                    text_profile_company.setText(university);
                }

                if(!(course == null)) {
                    text_profile_designation.setText(course);
                }
            }else{
                if(!(company == null)) {

                    text_profile_company.setText(company);
                }

                if(!(designation_profile == null)) {

                    text_profile_designation.setText(designation_profile);
                }
            }

        }


        if(!(profilecount == null))
        {
            text_profile_views.setText(profilecount);
        }

        if(!(followingcount == null))
        {
            txt_following_count.setText(followingcount);
        }

        if(!(followerscount == null))
        {
            txt_followers_count.setText(followerscount);
        }

        if(!(livevidoescount == null))
        {
            text_live_videos.setText(livevidoescount);
        }

        //contact details data
//        if(profile_url == null && profile_mobno == null && profile_website == null)
//        {
//            Log.d("==","");
//            btn_add_contactdetails.setVisibility(View.VISIBLE);
//        }
//        else
//        {
//            Log.d("==","else");
//
//            rlTitle.setVisibility(View.VISIBLE);
//            if(profile_url != null)
//            {
//                Log.d("==","llProfile");
//                llProfile.setVisibility(View.VISIBLE);
//                text_profile.setText(profile_url);
//            }else{
//                llProfile.setVisibility(View.GONE);
//
//            }
//
//            if(profile_mobno == null || profile_mobno.equals(""))
//            {
//                llPhone.setVisibility(View.GONE);
//
//            }else{
//                llPhone.setVisibility(View.VISIBLE);
//                text_phone.setText(profile_mobno);
//
//            }
//
//            if(profile_website != null)
//            {
//                Log.d("==","llWebsite");
//                llWebsite.setVisibility(View.VISIBLE);
//                text_website.setText(profile_website);
//            }else{
//                llWebsite.setVisibility(View.GONE);
//
//            }
//            btn_add_contactdetails.setVisibility(View.GONE);
//        }

        String Profile_city = sharedpreferences.getString(m_config.Profile_city,null);
        String Profile_state = sharedpreferences.getString(m_config.Profile_state,null);
        String Profile_country = sharedpreferences.getString(m_config.Profile_country,null);
        if(!(Profile_city == null)){
            text_profile_location.setText(Profile_city+","+Profile_state+","+Profile_country);
        }


        if(!(display_pic == null)) {
            Glide.with(context)
                    .load(Uri.parse(display_pic))
                    .into(img_profile);
        }

        if(!(thumbnail == null)) {
            Log.e("thumbnail"," "+thumbnail);
            Glide.with(context)
                    .load(Uri.parse(thumbnail))
                    .into(img_intro_video_thumbnail);
        }

        img_intro_video_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(intro_video_myprofile.getVideo_mp4() != null)
                {
                    Intent int_sample_videos = new Intent(context, SampleVideoFullScreen.class);
                    int_sample_videos.putExtra("Current url",intro_video_myprofile.getVideo_mp4());
                    startActivity(int_sample_videos);
                }

            }
        });

        txt_following_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        txt_followers_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        img_edit_introvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CommonFunctions.chkStatus(context))
                {
                    if(CommonFunctions.checkCameraHardware(context))
                    {
                        Intent int_video_record = new Intent(context, VideoRecordingActivity.class);
                        int_video_record.putExtra("from", "edit_intro_video");
                        context.startActivity(int_video_record);
                    }
                }
                else
                {
                    CommonFunctions.displayToast(context,context.getResources().getString(R.string.network_connection));
                }
            }
        });

//        btn_add_contactdetails.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent int_add_contact = new Intent(context, AddContactDetailsActivity.class);
//                startActivityForResult(int_add_contact, Constcore.REQUEST_CODE_ADDCONTACTDETAILS);
//            }
//        });
//
//        img_editContacts.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent int_edit_contact = new Intent(context, AddContactDetailsActivity.class);
//                startActivityForResult(int_edit_contact, Constcore.REQUEST_CODE_ADDCONTACTDETAILS);
//            }
//        });
//
//        btn_add_qualification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent int_add_qualification = new Intent(context, AddQualificationActivity.class);
//                startActivityForResult(int_add_qualification, Constcore.REQUEST_CODE_ADDQUALIFICATION);
//            }
//        });
//
//        img_editQualification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String qual = text_qualification.getText().toString().trim();
//                Intent int_edit_qualification = new Intent(context, AddQualificationActivity.class);
//                int_edit_qualification.putExtra("qual",qual);
//                startActivityForResult(int_edit_qualification, Constcore.REQUEST_CODE_ADDQUALIFICATION);
//            }
//        });
//
//        btn_add_school.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent int_add_schools = new Intent(context, AddSchoolsActivity.class);
//                startActivityForResult(int_add_schools, Constcore.REQUEST_CODE_ADDSCHOOLS);
//            }
//        });
//
//        img_editSchools.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String sch = text_schools.getText().toString().trim();
//                Intent int_edit_schools = new Intent(context, AddSchoolsActivity.class);
//                int_edit_schools.putExtra("sch",sch);
//                startActivityForResult(int_edit_schools, Constcore.REQUEST_CODE_ADDSCHOOLS);
//            }
//        });
//
//        btn_add_skills.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent int_add_skills = new Intent(context, AddSkillsActivity.class);
//                startActivityForResult(int_add_skills, Constcore.REQUEST_CODE_ADDSKILLS);
//            }
//        });
//
//        img_editSkills.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String skl = text_skills.getText().toString().trim();
//                Intent int_edit_skills = new Intent(context, AddSkillsActivity.class);
//                int_edit_skills.putExtra("skl",skl);
//                startActivityForResult(int_edit_skills, Constcore.REQUEST_CODE_ADDSKILLS);
//            }
//        });
//
//        btn_other.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent int_add_others = new Intent(context, AddOthersActivity.class);
//                startActivityForResult(int_add_others, Constcore.REQUEST_CODE_ADDOTHERS);
//            }
//        });
//
//        img_editOthers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String othr = text_others.getText().toString().trim();
//                Intent int_edit_others = new Intent(context, AddOthersActivity.class);
//                int_edit_others.putExtra("others",othr);
//                startActivityForResult(int_edit_others, Constcore.REQUEST_CODE_ADDOTHERS);
//            }
//        });
//
//        btn_add_designation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent int_add_designation = new Intent(context, AddDesignationActivity.class);
//                startActivityForResult(int_add_designation, Constcore.REQUEST_CODE_ADDDESIGNATION);
//            }
//        });
//
//        img_editDesignation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String dsgn = text_designation.getText().toString().trim();
//                Intent int_edit_designation = new Intent(context, AddDesignationActivity.class);
//                int_edit_designation.putExtra("dsgn",dsgn);
//                startActivityForResult(int_edit_designation, Constcore.REQUEST_CODE_ADDDESIGNATION);
//            }
//        });
//
//        btn_add_company.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent int_add_company = new Intent(context, AddCompanyActivity.class);
//                startActivityForResult(int_add_company, Constcore.REQUEST_CODE_ADDCOMPANY);
//            }
//        });
//
//        img_editCompany.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String cmpny = text_company.getText().toString().trim();
//                Intent int_edit_company = new Intent(context, AddCompanyActivity.class);
//                int_edit_company.putExtra("cmpny",cmpny);
//                startActivityForResult(int_edit_company, Constcore.REQUEST_CODE_ADDCOMPANY);
//            }
//        });
//
        img_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit_profile = new Intent(context, EditProfileActivity.class);
                startActivityForResult(edit_profile, Constcore.REQUEST_CODE_EDITPROFILE);
            }
        });

        btn_upload_videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(context, PublicProfileActivity.class);
//                startActivity(i);

                if(CommonFunctions.checkCameraHardware(context))
                {
                    Intent int_video_record = new Intent(context, VideoRecordingActivity.class);
                    int_video_record.putExtra("from","profile");
                    //context.startActivity(int_video_record);
                    startActivityForResult(int_video_record, Constcore.REQUEST_CODE_PROFILE_VIDEO);

                }

            }
        });

//        LinearLayout lin_live_videos = (LinearLayout) v.findViewById(R.id.lin_live_videos);
//        lin_live_videos.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent int_landing = new Intent(context, LiveVideosActivity.class);
//                startActivity(int_landing);
//            }
//        });
//
//        LinearLayout lin_profile_views = (LinearLayout) v.findViewById(R.id.lin_profile_views);
//        lin_profile_views.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(context, WhoViewedProfileActivity.class);
//                startActivity(i);
//
//            }
//        });
//
//        TextView txt_followers_count = (TextView) v.findViewById(R.id.txt_followers_count);
//        txt_followers_count.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(context, MyFollowersActivity.class);
//                startActivity(i);
//            }
//        });
//
//        TextView txt_following_count = (TextView) v.findViewById(R.id.txt_following_count);
//        txt_following_count.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(context, MyFollowingsActivity.class);
//                startActivity(i);
//            }
//        });
//
//        //if qualification data is present in profile response, set that data in respective relative layout
//        if(profile_qualifications_list == null || profile_qualifications_list.size() == 0)
//        {
//            btn_add_qualification.setVisibility(View.VISIBLE);
//            relative_qualification_details.setVisibility(View.GONE);
//
//        }
//        else
//        {
//            Log.e("profile_qualifications_list",""+profile_qualifications_list);
//            text_qualification.setText("");
//            btn_add_qualification.setVisibility(View.GONE);
//            relative_qualification_details.setVisibility(View.VISIBLE);
//
//
//            String qual = "";
//
//            for(int i=0; i<profile_qualifications_list.size(); i++)
//            {
//
//                if(i == profile_qualifications_list.size()-1)
//                {
//                    String val = profile_qualifications_list.get(i).toString().trim().substring(0,1).toUpperCase() + profile_qualifications_list.get(i).toString().trim().substring(1);
//                    qual += val;
//
//                }
//                else
//                {
//
//                    String val = profile_qualifications_list.get(i).toString().trim().substring(0,1).toUpperCase() + profile_qualifications_list.get(i).toString().trim().substring(1);
//                    qual += val+", ";
//
//                }
//
//                text_qualification.setText(qual.replaceAll("\\[", "").replaceAll("\\]",""));
//
//            }
//
//        }
//
//        //if schools data is present in profile response, set that data in respective relative layout
//        if(profile_schools_list == null || profile_schools_list.size() == 0)
//        {
//            btn_add_school.setVisibility(View.VISIBLE);
//            relative_schools_details.setVisibility(View.GONE);
//
//        }
//        else
//        {
//            text_schools.setText("");
//            btn_add_school.setVisibility(View.GONE);
//            relative_schools_details.setVisibility(View.VISIBLE);
//
//            String sch = "";
//
//            for(int i=0; i<profile_schools_list.size(); i++)
//            {
//
//                if(i == profile_schools_list.size()-1)
//                {
//                    String val = profile_schools_list.get(i).toString().trim().substring(0,1).toUpperCase() + profile_schools_list.get(i).toString().trim().substring(1);
//                    sch += val;
//
//                }
//                else
//                {
//
//                    String val = profile_schools_list.get(i).toString().trim().substring(0,1).toUpperCase() + profile_schools_list.get(i).toString().trim().substring(1);
//                    sch += val+", ";
//
//                }
//
//                text_schools.setText(sch.replaceAll("\\[", "").replaceAll("\\]",""));
//
//            }
//        }
//
//        //if company skills is present in profile response, set that data in respective relative layout
//        if(profile_skills_list == null || profile_skills_list.size() == 0)
//        {
//            btn_add_skills.setVisibility(View.VISIBLE);
//            relative_skills_details.setVisibility(View.GONE);
//
//        }
//        else {
//
//            text_skills.setText("");
//            btn_add_skills.setVisibility(View.GONE);
//            relative_skills_details.setVisibility(View.VISIBLE);
//
//            String skl = "";
//
//            for (int i = 0; i < profile_skills_list.size(); i++) {
//
//                if (i == profile_skills_list.size() - 1) {
//                    String val = profile_skills_list.get(i).toString().trim().substring(0,1).toUpperCase() + profile_skills_list.get(i).toString().trim().substring(1);
//                    skl += val;
//
//                } else {
//
//                    String val = profile_skills_list.get(i).toString().trim().substring(0, 1).toUpperCase() + profile_skills_list.get(i).toString().trim().substring(1);
//                    skl += val + ", ";
//
//                }
//
//                text_skills.setText(skl.replaceAll("\\[", "").replaceAll("\\]", ""));
//
//            }
//        }
//
//
//        //if others is present in profile response, set that data in respective relative layout
//        if(profile_others_list == null || profile_others_list.size() == 0)
//        {
//            btn_other.setVisibility(View.VISIBLE);
//            relative_others_details.setVisibility(View.GONE);
//
//        }
//        else {
//
//            text_others.setText("");
//            btn_other.setVisibility(View.GONE);
//            relative_others_details.setVisibility(View.VISIBLE);
//
//            String othr = "";
//
//            for (int i = 0; i < profile_others_list.size(); i++) {
//
//                if (i == profile_others_list.size() - 1) {
//                    String val = profile_others_list.get(i).toString().trim().substring(0,1).toUpperCase() + profile_others_list.get(i).toString().trim().substring(1);
//                    othr += val;
//
//                } else {
//
//                    String val = profile_others_list.get(i).toString().trim().substring(0, 1).toUpperCase() + profile_others_list.get(i).toString().trim().substring(1);
//                    othr += val + ", ";
//
//                }
//
//                text_others.setText(othr.replaceAll("\\[", "").replaceAll("\\]", ""));
//
//            }
//        }
    }

    private void getDataInList()
    {
        profile_videos_adapter =  new ProfileVideosAdapter(getActivity(), profile_video_list, img_intro_video_thumbnail, btn_upload_videos);
        mRecyclerView.setAdapter(profile_videos_adapter);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (Constcore.REQUEST_CODE_ADDCOMPANY):
                //update company data when intent to myprofile activity
                if(data!= null){
                    String company = data.getStringExtra("company");
                    if(!(company.equals(""))) {
                        btn_add_company.setVisibility(View.GONE);
                        relative_company_details.setVisibility(View.VISIBLE);
                        text_company.setText(company);
                    }else{
                        btn_add_company.setVisibility(View.VISIBLE);
                        relative_company_details.setVisibility(View.GONE);
                    }
                }
                break;


            case (Constcore.REQUEST_CODE_ADDCONTACTDETAILS):
                //update contact details data when intent to myprofile activity
                if(data!= null)
                {
                    String profile = data.getStringExtra("profile");
                    String website = data.getStringExtra("website");
                    String mobno = data.getStringExtra("mobno");
                    if(!((profile.equals("")) || (website.equals("")) || (mobno.equals(""))))
                    {

                        llPhone.setVisibility(View.VISIBLE);
                        llWebsite.setVisibility(View.VISIBLE);
                        llProfile.setVisibility(View.VISIBLE);
                        text_profile.setText(profile);
                        text_website.setText(website);
                        text_phone.setText(mobno);
                    }
//                    else{
//                           btn_add_contactdetails.setVisibility(View.GONE);
//                           relative_contact_details.setVisibility(View.VISIBLE);
//                           text_profile.setText(profile);
//                           text_website.setText(website);
//                           text_phone.setText(mobno);
//                       }

//                    }else{
//                        btn_add_contactdetails.setVisibility(View.VISIBLE);
//                        relative_contact_details.setVisibility(View.GONE);
                    // }

                }
                break;


            case (Constcore.REQUEST_CODE_ADDDESIGNATION):
                //update designation data when intent to myprofile activity
                if(data!= null){
                    String designation = data.getStringExtra("designation");
                    if(!(designation.equals(""))) {
                        btn_add_designation.setVisibility(View.GONE);
                        relative_designation_details.setVisibility(View.VISIBLE);
                        text_designation.setText(designation);
                    }else{
                        btn_add_designation.setVisibility(View.VISIBLE);
                        relative_designation_details.setVisibility(View.GONE);
                    }
                }
                break;


            case (Constcore.REQUEST_CODE_ADDOTHERS):
                //update other data when intent to myprofile activity
                if(data!= null){
                    String others = data.getStringExtra("others");
                    if(!(others.equals(""))) {
                        btn_other.setVisibility(View.GONE);
                        relative_others_details.setVisibility(View.VISIBLE);
                        text_others.setText(others);
                    }else{
                        btn_other.setVisibility(View.VISIBLE);
                        relative_others_details.setVisibility(View.GONE);
                    }
                }
                break;


            case (Constcore.REQUEST_CODE_ADDQUALIFICATION):
                //update qualification data when intent to myprofile activity
                if(data!= null){
                    String qualification = data.getStringExtra("qualification");
                    if(!(qualification.equals(""))) {
                        btn_add_qualification.setVisibility(View.GONE);
                        relative_qualification_details.setVisibility(View.VISIBLE);
                        text_qualification.setText(qualification);
                    }else{
                        btn_add_qualification.setVisibility(View.VISIBLE);
                        relative_qualification_details.setVisibility(View.GONE);
                    }
                }
                break;


            case (Constcore.REQUEST_CODE_ADDSCHOOLS):
                //update schools data when intent to myprofile activity
                if(data!= null){
                    String schools = data.getStringExtra("schools");
                    if(!(schools.equals(""))) {
                        btn_add_school.setVisibility(View.GONE);
                        relative_schools_details.setVisibility(View.VISIBLE);
                        text_schools.setText(schools);
                    }else{
                        btn_add_school.setVisibility(View.VISIBLE);
                        relative_schools_details.setVisibility(View.GONE);
                    }
                }
                break;


            case (Constcore.REQUEST_CODE_ADDSKILLS):
                //update skills data when intent to myprofile activity
                if(data!= null){
                    String skills = data.getStringExtra("skills");
                    if(!(skills.equals(""))) {
                        btn_add_skills.setVisibility(View.GONE);
                        relative_skills_details.setVisibility(View.VISIBLE);
                        text_skills.setText(skills);
                    }else{
                        btn_add_skills.setVisibility(View.VISIBLE);
                        relative_skills_details.setVisibility(View.GONE);
                    }
                }
                break;

            case (Constcore.REQUEST_CODE_EDITPROFILE):
                //update profile data when intent to myprofile activity
                if(data!= null)
                {
                    String image = sharedpreferences.getString(m_config.login_display_pic,"");

                    if(!(image == null)) {
                        // Log.e("thumbnail"," "+thumbnail);
                        Glide.with(context)
                                .load(image)
                                .fitCenter()
                                .into(img_profile);
                    }

                    String usertype = sharedpreferences.getString(m_config.Profile_user_type,null);
                    if(usertype.equals("student"))
                    {
                        text_profile_company.setText(sharedpreferences.getString(m_config.University, null));
                        text_profile_designation.setText(sharedpreferences.getString(m_config.Course, null));
                    }
                    else if(usertype.equals("professional"))
                    {
                        text_profile_company.setText(sharedpreferences.getString(m_config.Company, null));
                        text_profile_designation.setText(sharedpreferences.getString(m_config.Designation, null));
                    }

                    text_profile_location.setText(sharedpreferences.getString(m_config.Profile_city, null)+", "+sharedpreferences.getString(m_config.Profile_state, null)+", "+sharedpreferences.getString(m_config.Profile_country, null));

                }

                break;

            case (Constcore.REQUEST_CODE_PROFILE_VIDEO):
                // update profile videos when intent to myprofile activity
                if(profile_video_list.size() == 3)
                {
                    btn_upload_videos.setEnabled(false);
                }
                else
                {
                    btn_upload_videos.setEnabled(true);
                }
                profile_videos_adapter.notifyDataSetChanged();

                break;



        }
    }


    // API Call for My profile
    public void req_myprofile_API(final Context cont)
    {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String bearertoken = sharedPreferences.getString(m_config.login_access_token,"");


        final Gson gson = new Gson();
        String url = m_config.web_api_link + "/my-profile";
        Log.d("url", url.toString());
        // prepare the Request
        JsonObjectRequest profilerequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        int code = -1;
                        try {
                            Log.e("response code", " " + response.getInt("code"));
                            code = response.getInt("code");
                            if (code != -1) {
                                CommonFunctions.hDialog();
                                CommonFunctions.displayToast(context, response.getString("message"));
                            }
                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        txt_following_count.setText("Followings 0");
                        profile_video_list = new ArrayList<>();
                        intro_video_myprofile = new Videos();
                        profile_companies_list = new ArrayList<>();
                        profile_designations_list = new ArrayList<>();
                        profile_qualifications_list = new ArrayList<>();
                        profile_schools_list = new ArrayList<>();
                        profile_skills_list = new ArrayList<>();
                        profile_others_list = new ArrayList<>();

                        MyProfile_Response myProfile_response = gson.fromJson(response.toString(), MyProfile_Response.class);
                        ArrayList<Videos> videos = myProfile_response.getVideos();
                        ArrayList<Companies> companies = myProfile_response.getCompanies();
                        ArrayList<Designations> designations = myProfile_response.getDesignations();
                        ArrayList<Qualifications> qualifications = myProfile_response.getQualifications();
                        ArrayList<Schools> schools = myProfile_response.getSchools();
                        ArrayList<Skills> skills = myProfile_response.getSkills();
                        ArrayList<Others> others = myProfile_response.getOthers();

                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString(m_config.login_fullname, myProfile_response.getFullname());
                        editor.putString(m_config.Profile_user_type, myProfile_response.getUser_type());
                        editor.putString(m_config.profile_url, myProfile_response.getProfile_url());
                        editor.putString(m_config.profile_website, myProfile_response.getWebsite());
                        editor.putString(m_config.Country_code, myProfile_response.getCountry_code());
                        editor.putString(m_config.Mobile_Number, myProfile_response.getMobile_number());
                        editor.putInt(m_config.Profile_Strength, myProfile_response.getProfile_strength());
                        editor.putString(m_config.University, myProfile_response.getUniversity());
                        editor.putString(m_config.Course, myProfile_response.getCourse());
                        editor.putString(m_config.Company, myProfile_response.getCompany());
                        editor.putString(m_config.Designation, myProfile_response.getDesignation());
                        editor.putString(m_config.Profile_city, myProfile_response.getCity());
                        editor.putString(m_config.Profile_state, myProfile_response.getState());
                        editor.putString(m_config.Profile_country, myProfile_response.getCountry());
                        editor.putString(m_config.Profile_latitude, myProfile_response.getLatitude());
                        editor.putString(m_config.Profile_longitude, myProfile_response.getLongitude());
                        editor.putString(m_config.Profile_dob_date, myProfile_response.getDob_day());
                        editor.putString(m_config.Profile_dob_month, myProfile_response.getDob_month());
                        editor.putString(m_config.Profile_dob_year, myProfile_response.getDob_year());
                        editor.putString(m_config.Profile_live_videos, String.valueOf(myProfile_response.getLive_video()));
                        editor.putString(m_config.Profile_connection_count, String.valueOf(myProfile_response.getConnection_count()));
                        editor.putString(m_config.Profile_view_count, String.valueOf(myProfile_response.getProfile_view_count()));
                        editor.putString(m_config.Profile_following_count, String.valueOf(myProfile_response.getFollowing_count()));
                        editor.putString(m_config.Profile_followers_count, String.valueOf(myProfile_response.getFollowers_count()));
                        editor.putString(m_config.login_display_pic, myProfile_response.getDisplay_pic());

                        if(videos != null)
                        {
                            for(int i = 0; i < videos.size(); i++)
                            {

                                if(videos.get(i).getIntro() == true)
                                {
//                                    editor.putString(m_config.Profile_Intro_video, videos.get(i).getVideo_mp4());
//                                    editor.putString(m_config.Thumbnail_profile, videos.get(i).getThumbnail());
                                    intro_video_myprofile = videos.get(i);
                                    Log.e("intro_video_myprofile"," "+intro_video_myprofile.toString());
                                }
                                else
                                {
                                    profile_video_list.add(videos.get(i));
                                }
                            }


                        }





                        if(companies != null){

                            for(int i = 0; i < companies.size(); i++)
                            {

                                profile_companies_list.add(companies.get(i).getCompany());

                            }
                        }

                        if(designations != null){

                            for(int i = 0; i < designations.size(); i++)
                            {

                                profile_designations_list.add(designations.get(i).getDesignation());

                            }
                        }

                        if(qualifications != null){
                            for(int i = 0; i < qualifications.size(); i++)
                            {

                                profile_qualifications_list.add(qualifications.get(i).getQualification());

                            }
                        }

                        if(schools != null){
                            for(int i = 0; i < schools.size(); i++)
                            {
                                Log.d("school not null im api","");
                                profile_schools_list.add(schools.get(i).getSchool());

                            }
                        }

                        if(skills != null){

                            for(int i = 0; i < skills.size(); i++)
                            {
                                Log.d("school not null im api","");
                                profile_skills_list.add(skills.get(i).getSkill());

                            }
                        }

                        if(others != null)
                        {

                            for(int i = 0; i < others.size(); i++)
                            {
                                profile_others_list.add(others.get(i).getOther());

                            }
                        }

                        CommonFunctions.hDialog();

                        editor.putBoolean("my_profile_flag",true);
                        editor.commit();
                        initialiseUI();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        CommonFunctions.hDialog();
                        if (error.networkResponse != null) {
                            String networkresponse_data = new String(error.networkResponse.data);
                            if(error.networkResponse.statusCode == 500)
                            {
                                CommonFunctions.displayToast(context,context.getResources().getString(R.string.internal_server_error));
                            }
                            else
                            {
                                if (networkresponse_data != null)
                                {
                                    JSONObject network_obj;
                                    try {
                                        network_obj = new JSONObject(networkresponse_data);

                                        if (network_obj.getInt("code") == 12) {
                                            // Alert Dialog for access token expired
                                            CommonFunctions.alertdialog_token(context, "accesstokenexpired");
                                        } else if (network_obj.getInt("code") == 16) {
                                            // Alert Dialog for refresh token expired
                                            CommonFunctions.alertdialog_token(context, "refreshtokenexpired");
                                        } else {
                                            CommonFunctions.displayToast(context, network_obj.getString("message"));
                                        }
                                    } catch (JSONException e)
                                    {

                                    }

                                }
                            }
                        }



                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("version", Constcore.version);
                params.put("devicetype", "android");
                params.put("timezone:IST", CommonFunctions.getTimeZoneIST());
                params.put("Authorization","Bearer "+bearertoken);
                params.put("timesstamp",CommonFunctions.getTimeStamp());
                params.put("os_version",m_config.os_version);
                return params;
            }
        };

        // add it to the RequestQueue
        int socketTimeout = 10000;//60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        profilerequest.setRetryPolicy(policy);

        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(profilerequest, "my_profile_api");

    }

    public void updatefollowingcount(Context context, String chk_follow_unfollow)
    {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String count = sharedpreferences.getString(m_config.Profile_following_count,"0");
        int follow_count =  Integer.parseInt(count);
        if(chk_follow_unfollow.equals("unfollow"))
        {
            follow_count  = Integer.parseInt(count) - 1;
        }
        else if(chk_follow_unfollow.equals("follow"))
        {
            follow_count  = Integer.parseInt(count) + 1;
        }

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(m_config.Profile_following_count, String.valueOf(follow_count));
        editor.commit();

        txt_following_count.setText("Followings "+sharedpreferences.getString(m_config.Profile_following_count,"0"));
    }

    public void editIntroVideo()
    {
        Log.e("thumbnail"," "+intro_video_myprofile.getThumbnail());
        Glide.with(contextmyprofile)
                .load(Uri.parse(intro_video_myprofile.getThumbnail()))
                .into(img_intro_video_thumbnail);
    }

    public int getHeight(final View v) {
        final int[] height = new int[1];

        final RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.rel_white_background);
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
                RelativeLayout rel_orange_background = (RelativeLayout) v.findViewById(R.id.rel_orange_background);
                rel_orange_background.getLayoutParams().height = height[0];
                rel_orange_background.requestLayout();

            }
        });
        return height[0];
    }



}

