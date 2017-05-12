package com.viintro.Viintro.MyProfile;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.EditProfile_Request;
import com.viintro.Viintro.Model.UpdateDisplayPic_Request;
import com.viintro.Viintro.Model.EditProfile_response;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.ViintroApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.viintro.Viintro.Constants.Constcore.MY_PERMISSIONS_ACCESS_CF_LOCATION;
import static com.viintro.Viintro.Reusables.CommonFunctions.getImageUrlfromCloudinary;

public class EditProfileActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private ImageView image_profile, find_location;
    private EditText edt_name, edt_company, edt_designation, edt_university, edt_course;
    //  private Spinner  spinner_date, spinner_year;
    private EditText edt_date, edt_month, edt_year;
    private TextView text_company, text_designation, text_university, text_course, text_selectedlocation;
    private Button btn_save;
    String Profile_city,Profile_state, Profile_country,profile_pic, name,designation_profile, course, university, company;
    private GoogleApiClient mGoogleApiClient;
    Configuration_Parameter m_config;
    Context context;
    SharedPreferences sharedpreferences;
    Location currentlocation;
    LocationManager locationManager;
    String latitude, longitude;
    Uri selectedImage;
    Uri tempUri;
    private int PLACE_PICKER_REQUEST = 3;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA=15;
    public static final int MY_PERMISSIONS_REQUEST_R=30;
    public static final int MY_PERMISSIONS_REQUEST_RWFRMCAM=60;
    private String picturePath;
    String dob_day, dob_month, dob_year;
    private int pYear;
    private int pMonth;
    private int pDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_profile);
        context = this;
        m_config = Configuration_Parameter.getInstance();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        Calendar myCalendar = Calendar.getInstance();
        myCalendar.add(Calendar.YEAR, -100);
        long hundredYearsAgo = myCalendar.getTimeInMillis();

        Calendar c = Calendar.getInstance();
        pYear = c.get(Calendar.YEAR);
        pMonth = c.get(Calendar.MONTH);
        pDay = c.get(Calendar.DAY_OF_MONTH);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(policy);

        //call to googleapiclient to fetch places
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        TextView txt_Header = (TextView) findViewById(R.id.header);
        txt_Header.setText("Edit Profile");
        initialiseUI();
        String student = sharedpreferences.getString(m_config.Profile_user_type,null);
        if(student.equals("student"))
        {
            text_company.setVisibility(View.GONE);
            edt_company.setVisibility(View.GONE);
            text_designation.setVisibility(View.GONE);
            edt_designation.setVisibility(View.GONE);
        }else{
            text_university.setVisibility(View.GONE);
            edt_university.setVisibility(View.GONE);
            text_course.setVisibility(View.GONE);
            edt_course.setVisibility(View.GONE);
        }


    }

    private void initialiseUI() {

        find_location = (ImageView) findViewById(R.id.find_location);
        image_profile = (ImageView) findViewById(R.id.image_profile);
        text_company = (TextView) findViewById(R.id.text_company);
        text_designation = (TextView) findViewById(R.id.text_designation);
        text_university = (TextView) findViewById(R.id.text_university);
        text_course = (TextView) findViewById(R.id.text_course);
        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_name.setEnabled(false);
        edt_company = (EditText) findViewById(R.id.edt_company);
        edt_designation = (EditText) findViewById(R.id.edt_designation);
        edt_university = (EditText) findViewById(R.id.edt_university);
        edt_course = (EditText) findViewById(R.id.edt_course);
        text_selectedlocation = (TextView) findViewById(R.id.text_selectedlocation);
        edt_date = (EditText) findViewById(R.id.edt_date);
        edt_month = (EditText) findViewById(R.id.edt_month);
        edt_year = (EditText) findViewById(R.id.edt_year);
        btn_save = (Button) findViewById(R.id.btn_save);

        Profile_city = sharedpreferences.getString(m_config.Profile_city,null);
        Profile_state = sharedpreferences.getString(m_config.Profile_state,null);
        Profile_country = sharedpreferences.getString(m_config.Profile_country,null);
        latitude = sharedpreferences.getString(m_config.Profile_latitude,null);
        longitude = sharedpreferences.getString(m_config.Profile_longitude,null);
        dob_day = sharedpreferences.getString(m_config.Profile_dob_date,null);
        dob_month = sharedpreferences.getString(m_config.Profile_dob_month,null);
        dob_year = sharedpreferences.getString(m_config.Profile_dob_year,null);

        if(!(Profile_city == null))
        {
            text_selectedlocation.setText(Profile_city+","+Profile_state+","+Profile_country);
        }

        if(!(dob_day == null)){
            edt_date.setText(dob_day);
        }
        if(!(dob_month == null)){
            edt_month.setText(dob_month);
        }
        if(!(dob_year == null)){
            edt_year.setText(dob_year);
        }

        Calendar mcurrentDate=Calendar.getInstance();
        pYear=mcurrentDate.get(Calendar.YEAR);
        pMonth=mcurrentDate.get(Calendar.MONTH);
        pDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

        edt_month.setFocusable(false);
        edt_month.setCursorVisible(false);
        //datepicker dialog
        edt_month.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                switch(arg1.getAction())
                {
                    case MotionEvent.ACTION_DOWN :
                        final DatePickerDialog mDatePicker=new DatePickerDialog(EditProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                                // TODO Auto-generated method stub
                    /*      Your code   to get date and time    */
                                edt_month.setText(selectedmonth+1+"");
                                edt_date.setText(Integer.toString(selectedday));
                                edt_year.setText(Integer.toString(selectedyear));
                                // spinner_date

                            }
                        },pYear, pMonth, pDay);
                        mDatePicker.setTitle("Select date");
                        mDatePicker.show();
                        break;
                    case MotionEvent.ACTION_UP  :
                        break;
                }

                return true;
            }
        });

//        if ((int) Build.VERSION.SDK_INT < 19)
//        {
//            getLatnLong();
//        }
//        else
//        {
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                    &&
//                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                //Permission model implementation
//
//                ActivityCompat.requestPermissions((Activity) context,
//                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
//                        MY_PERMISSIONS_ACCESS_CF_LOCATION);
//            }
//            else
//            {
//                getLatnLong();
//            }
//
//        }

        profile_pic = sharedpreferences.getString(m_config.login_display_pic,null);
        Log.e("profile_pic"," "+profile_pic);

        if(!(profile_pic == null)) {
            Glide.with(context)
                    .load(Uri.parse(profile_pic))
                    .error(R.mipmap.ic_person_black)
                    .placeholder(R.mipmap.ic_person_black)
                    .into(image_profile);
        }

        name = sharedpreferences.getString(m_config.login_fullname,null);
        if(!(name == null)) {
            edt_name.setText(name);
        }

        designation_profile = sharedpreferences.getString(m_config.Designation,null);
        if(!(designation_profile == null)) {
            edt_designation.setText(designation_profile);
        }

        course = sharedpreferences.getString(m_config.Course,null);
        if(!(course == null)) {
            edt_course.setText(course);
        }

        university = sharedpreferences.getString(m_config.University,null);
        if(!(university == null)) {
            edt_university.setText(university);
        }

        company = sharedpreferences.getString(m_config.Company,null);
        if(!(company == null)) {
            edt_company.setText(company);
        }

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();
            }
        });



//        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.months));
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner_month.setAdapter(adapter);
//        spinner_month.setSelection(0);

//        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.date));
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner_date.setAdapter(adapter);
//        spinner_date.setSelection(0);


//        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.year));
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner_year.setAdapter(adapter);
//        spinner_year.setSelection(0);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CommonFunctions.chkStatus(context))
                {
                    CommonFunctions.sDialog(context,"Loading...");

                    String dob_day = edt_date.getText().toString().trim();
                    String dob_month = edt_month.getText().toString();
                    String dob_year = edt_year.getText().toString().trim();

                    String url = null;

                    // Saving video on cloudinary
                    if(picturePath != null)
                    {
                        url = getImageUrlfromCloudinary(context, picturePath);
                        EditProfile_Request editProfile_request = new EditProfile_Request();
                        editProfile_request.setClient_id(Constcore.client_Id);
                        editProfile_request.setClient_secret(Constcore.client_Secret);
                        editProfile_request.setDisplay_pic(url);
                        editProfile_request.setFullname(name);
                        editProfile_request.setDesignation(edt_designation.getText().toString());
                        editProfile_request.setCompany(edt_company.getText().toString());
                        editProfile_request.setCourse(edt_course.getText().toString());
                        editProfile_request.setUniversity(edt_university.getText().toString());
                        editProfile_request.setCity(Profile_city);
                        editProfile_request.setState(Profile_state);
                        editProfile_request.setCountry(Profile_country);
                        editProfile_request.setLatitude(latitude);
                        editProfile_request.setLongitude(longitude);
                        editProfile_request.setDob_day(dob_day);
                        editProfile_request.setDob_month(dob_month);
                        editProfile_request.setDob_year(dob_year);


                        req_edit_profile_API(context, editProfile_request);
                    }

                    else if(sharedpreferences.getString(m_config.login_display_pic,null) != null)
                    {
                        url = sharedpreferences.getString(m_config.login_display_pic,null);
                        EditProfile_Request editProfile_request = new EditProfile_Request();
                        editProfile_request.setClient_id(Constcore.client_Id);
                        editProfile_request.setClient_secret(Constcore.client_Secret);
                        editProfile_request.setDisplay_pic(url);
                        editProfile_request.setFullname(name);
                        editProfile_request.setDesignation(edt_designation.getText().toString());
                        editProfile_request.setCompany(edt_company.getText().toString());
                        editProfile_request.setCourse(edt_course.getText().toString());
                        editProfile_request.setUniversity(edt_university.getText().toString());
                        editProfile_request.setCity(Profile_city);
                        editProfile_request.setState(Profile_state);
                        editProfile_request.setCountry(Profile_country);
                        editProfile_request.setLatitude(latitude);
                        editProfile_request.setLongitude(longitude);
                        editProfile_request.setDob_day(dob_day);
                        editProfile_request.setDob_month(dob_month);
                        editProfile_request.setDob_year(dob_year);


                        req_edit_profile_API(context, editProfile_request);
                    }



                }
                else
                {
                    CommonFunctions.displayToast(context,getResources().getString(R.string.network_connection));
                }
            }
        });

        text_selectedlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call to google places api
                findPlace();
            }
        });

        find_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((int) Build.VERSION.SDK_INT < 23)
                {
                    Log.d("<23","");
                    getSelfLocation();

                }
                else {
                    Log.d(">23","");
                    if (ActivityCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            &&
                            ActivityCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_ACCESS_CF_LOCATION);
                    } else {
                        getSelfLocation();

                    }
                }
            }
        });


    }


    public void selectImage() {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {

                    startCamera();

                }
                else if (options[item].equals("Choose from Gallery"))
                {

                    openGallery();

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void openGallery(){
        if ((int) Build.VERSION.SDK_INT < 23)
        {
            Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        }
        else
        {

            int permissionCheck = ContextCompat.checkSelfPermission(EditProfileActivity.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE);

            Log.e("Read Permission Check"," "+permissionCheck);

            if(permissionCheck==PackageManager.PERMISSION_GRANTED)
            {

                Log.i("Read Permission granted","");
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            }
            else
            {
                Log.i("Read Permission not granted","");
                ActivityCompat.requestPermissions(EditProfileActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_R);

            }
        }
    }

    public void startCamera(){
        if ((int) Build.VERSION.SDK_INT < 23)
        {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 1);
        }
        else
        {
//
            int permissionCheck = ContextCompat.checkSelfPermission(EditProfileActivity.this,
                    android.Manifest.permission.CAMERA);

            Log.e("Camera permission Check"," "+permissionCheck);

            if(permissionCheck == PackageManager.PERMISSION_GRANTED)
            {
                Log.i("Have Camera Permission", "Yes");

                permissionCheck = ContextCompat.checkSelfPermission(EditProfileActivity.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

                //here
                int permissioncheckRead = ContextCompat.checkSelfPermission(EditProfileActivity.this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE);

                Log.i("Read and Write permission check",permissionCheck +"   " +permissioncheckRead );

                if(permissionCheck == PackageManager.PERMISSION_GRANTED && permissioncheckRead == PackageManager.PERMISSION_GRANTED)
                {
                    Log.i("Have Camera, Read and Write Permission","Yes");
                    //Open Camera Here
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                }
                else
                {
                    //Get Permission for read and write
                    Log.i("Camera permission approved ","But No RW permision");
                    Log.i("Ask for camera Read,Write permission","");
                    ActivityCompat.requestPermissions(EditProfileActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_RWFRMCAM);
                }
            }
            else
            {
                Log.e("Dont have camera permission", "Else block");
                ActivityCompat.requestPermissions(EditProfileActivity.this,
                        new String[]{android.Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }

    }

    private void onCaptureImageResult(Intent data)
    {
        if(data != null) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            File destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");
            FileOutputStream fo;
            try {
                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                picturePath = destination.getAbsolutePath();

                fo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            tempUri = getImageUri(getApplicationContext(), thumbnail);

            image_profile.setImageBitmap(thumbnail);
        }
    }


    public void findPlace() {
        try {
            //Autocomplete edittext with the filter(only cities)
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .build();

            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

//    public void getLatnLong(){
//
//        if ((int) Build.VERSION.SDK_INT < 19)
//        {
//            Log.d("<19","");
//            currentlocation = getLastBestLocation();
//
//            if (currentlocation != null)
//            {
//                Log.e("Func  ", currentlocation.getLatitude() + "     " + currentlocation.getLongitude());
//                latitude = String.valueOf(currentlocation.getLatitude());
//                longitude = String.valueOf(currentlocation.getLongitude());
//                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//
//
//            }
//            else
//            {
//                Toast.makeText(context, "Unable to get current location.",Toast.LENGTH_SHORT).show();
//            }
//
//
//        }
//        else
//        {
//            Log.d(">19","");
//            Log.e("--getLocationMode---", " "+getLocationMode());
//
//            if(getLocationMode() == 0 || getLocationMode() == 1 || getLocationMode() == 2)
//            {
//                if(getLocationMode() == 0)
//                {
//
//                }
//                else if(getLocationMode() == 1 || getLocationMode() == 2)
//                {
//                    Toast.makeText(EditProfileActivity.this, "Please select high accuracy mode.", Toast.LENGTH_LONG).show();
//                }
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("Location Services Not Active");
//                builder.setMessage("Please enable Location Services and GPS");
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        // Show location settings when the user acknowledges the alert dialog
//                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        startActivity(intent);
//                        //  startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
//
//
//                    }
//                });
//
//                Dialog alertDialog = builder.create();
//                alertDialog.setCanceledOnTouchOutside(false);
//                alertDialog.show();
//
//            }
//            else
//            {
//
//                currentlocation = getLastBestLocation();
//
//                if(getLocationMode() == 1 && currentlocation == null)
//                {
//                    return;
//                }
//
//                if (currentlocation != null)
//                {
//                    Log.e("Func  ", currentlocation.getLatitude() + "     " + currentlocation.getLongitude());
//                    latitude = String.valueOf(currentlocation.getLatitude());
//                    longitude = String.valueOf(currentlocation.getLongitude());
//                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//
//                }
//                else
//                {
//                    Toast.makeText(context, "Unable to get current location.",Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//        }
//    }


    public void getSelfLocation() {


        if ((int) Build.VERSION.SDK_INT < 19)
        {
            Log.d("<19","");
            currentlocation = getLastBestLocation();

            if (currentlocation != null)
            {
                Log.e("Func  ", currentlocation.getLatitude() + "     " + currentlocation.getLongitude());
                latitude = String.valueOf(currentlocation.getLatitude());
                longitude = String.valueOf(currentlocation.getLongitude());
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());

                try
                {

                    List<Address> addresses = geocoder.getFromLocation(currentlocation.getLatitude(), currentlocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    Log.e("here", " " + addresses);

                    Profile_city = addresses.get(0).getLocality();
                    Profile_state = addresses.get(0).getAdminArea();
                    Profile_country = addresses.get(0).getCountryName();
                    text_selectedlocation.setText(Profile_city + "," + Profile_state + "," +Profile_country);


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(context, "Unable to get current location.",Toast.LENGTH_SHORT).show();

                }
            }
            else
            {
                Toast.makeText(context, "Unable to get current location.",Toast.LENGTH_SHORT).show();
            }


        }
        else
        {
            Log.d(">19","");
            Log.e("--getLocationMode---", " "+getLocationMode());

            if(getLocationMode() == 0 || getLocationMode() == 1 || getLocationMode() == 2)
            {
                if(getLocationMode() == 0)
                {

                }
                else if(getLocationMode() == 1 || getLocationMode() == 2)
                {
                    Toast.makeText(EditProfileActivity.this, "Please select high accuracy mode.", Toast.LENGTH_LONG).show();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Location Services Not Active");
                builder.setMessage("Please enable Location Services and GPS");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Show location settings when the user acknowledges the alert dialog
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        //  startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);


                    }
                });

                Dialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

            }
            else
            {

                currentlocation = getLastBestLocation();
                Log.e("currentlocation"," "+currentlocation);

                if(getLocationMode() == 1 && currentlocation == null)
                {
                    return;
                }

                if (currentlocation != null)
                {
                    Log.e("Func  ", currentlocation.getLatitude() + "     " + currentlocation.getLongitude());
                    latitude = String.valueOf(currentlocation.getLatitude());
                    longitude = String.valueOf(currentlocation.getLongitude());
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    try
                    {

                        List<Address> addresses = geocoder.getFromLocation(currentlocation.getLatitude(), currentlocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        Log.e("here", " " + addresses);

                        Profile_city = addresses.get(0).getLocality();
                        Profile_state = addresses.get(0).getAdminArea();
                        Profile_country = addresses.get(0).getCountryName();
                        text_selectedlocation.setText(Profile_city + "," + Profile_state + "," +Profile_country);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(context, "Unable to get current location.",Toast.LENGTH_SHORT).show();

                    }
                }
                else
                {
                    Toast.makeText(context, "Unable to get current location.",Toast.LENGTH_SHORT).show();
                }

            }

        }


    }

    public int getLocationMode()
    {
        int i = 0;

        try {
            i =  Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return i;
    }

    private Location getLastBestLocation()
    {
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //  return TODO;
        }
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            Log.e("locationGPS"," "+locationGPS);
            return locationGPS;
        }
        else {
            Log.e("locationNet"," "+locationNet);
            return locationNet;
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            onCaptureImageResult(data);
        }
        else if(requestCode == 2)
        {
            if(data != null)
            {
                selectedImage = data.getData();
                String[] filePath1 = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath1, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath1[0]);
                picturePath = c.getString(columnIndex);

                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Glide.with(EditProfileActivity.this)
                        .load(selectedImage)
                        .error(R.mipmap.ic_person_black)
                        .placeholder(R.mipmap.ic_person_black)
                        .fitCenter()
                        .into(image_profile);


            }
        }
        else if(requestCode == PLACE_PICKER_REQUEST)
        {
            if (resultCode == RESULT_OK) {
                //get data
                Place place = PlaceAutocomplete.getPlace(this, data);
                text_selectedlocation.setText(place.getAddress());

                String address = (String) place.getAddress();
                String[] add = address.split(",");
                Profile_city = add[0].trim();
                Profile_state = add[1].trim();
                Profile_country = add[2].trim();
                LatLng latlong = place.getLatLng();
                latitude = String.valueOf(latlong.latitude);
                longitude = String.valueOf(latlong.longitude);

                Log.e("----"," "+Profile_city+" "+Profile_state+" "+Profile_country+" "+latitude+" "+longitude);


                Log.i("auto", "Place: " + place.getAddress());
                Log.i("auto", "Place: " + place.getLatLng());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("auto", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.

            }
        }


    }

//    //api call for otp verification
//    public static void req_update_displaypic_API(Context cont, UpdateDisplayPic_Request updateDisplayPic_request) {
//        final Context context = cont;
//        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
//        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//
//
//        final Gson gson = new Gson();
//        String url = m_config.web_api_link+ "/update/display-picture";
//
//        JSONObject obj = new JSONObject();
//        try
//        {
//            obj.put("client_id", updateDisplayPic_request.getClient_id());
//            obj.put("client_secret", updateDisplayPic_request.getClient_secret());
//            obj.put("display_pic", updateDisplayPic_request.getDisplay_pic());
//
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//        }
//
//        Log.e("-object--"," "+obj);
//
//        // prepare the request
//        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // error response
//                        Log.d("Response", response.toString());
//                        int code = -1;
//                        try {
//                            Log.e("response code"," "+response.getInt("code"));
//                            code = response.getInt("code");
//                            if(code != -1)
//                            {
//                                // CommonFunctions.hDialog();
//                                CommonFunctions.displayToast(context,response.getString("message"));
//                            }
//                            return;
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        EditProfile_response updateDisplayPic_response = gson.fromJson(response.toString(), EditProfile_response.class);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString(m_config.login_display_pic,updateDisplayPic_response.getDisplay_pic());
//                        editor.commit();
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        if(error.networkResponse != null)
//                        {
//                            String networkresponse_data = new String(error.networkResponse.data);
//                            if(networkresponse_data != null)
//                            {
//                                JSONObject network_obj;
//                                try
//                                {
//                                    network_obj = new JSONObject(networkresponse_data);
//                                    CommonFunctions.hDialog();
//                                    if(network_obj.getInt("code") == 12)
//                                    {
//                                        // Alert Dialog for access token expired
//                                        CommonFunctions.alertdialog_token(context,"accesstokenexpired");
//                                    }
//                                    else if(network_obj.getInt("code") == 16)
//                                    {
//                                        // Alert Dialog for refresh token expired
//                                        CommonFunctions.alertdialog_token(context,"refreshtokenexpired");
//                                    }
//                                    else
//                                    {
//                                        CommonFunctions.displayToast(context,network_obj.getString("message"));
//                                    }
//
//                                }
//                                catch (JSONException e)
//                                {
//                                    CommonFunctions.hDialog();
//                                    CommonFunctions.displayToast(context,context.getResources().getString(R.string.Otp_verify_failed));
//                                }
//
//                            }
//                            else
//                            {
//                                CommonFunctions.hDialog();
//                                CommonFunctions.displayToast(context,context.getResources().getString(R.string.Otp_verify_failed));
//                            }
//                        } else
//                        {
//                            CommonFunctions.hDialog();
//                            CommonFunctions.displayToast(context,context.getResources().getString(R.string.Otp_verify_failed));
//                        }
//
//                    }
//                }
//        )
//
//        {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("Content-Type", "application/json");
//                params.put("version","1");
//                params.put("devicetype","android");
//                params.put("timezone",CommonFunctions.getTimeZoneIST());
//                params.put("Authorization","Bearer " + sharedPreferences.getString(m_config.login_access_token,""));
//                params.put("timesstamp",CommonFunctions.getTimeStamp());
//                params.put("os_version",m_config.os_version);
//
//                return params;
//            }
//        };
//
//        // add it to the RequestQueue
//        int socketTimeout = 10000;//60 seconds
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        getRequest.setRetryPolicy(policy);
//
//        // Adding request to request queue
//        ViintroApplication.getInstance().addToRequestQueue(getRequest, "update_display_pic_api");
//
//    }
//


    //api call for edit profile
    public static void req_edit_profile_API(Context cont, final EditProfile_Request editProfile_request) {
        final Context context = cont;
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);


        final Gson gson = new Gson();
        String url = m_config.web_api_link+ "/edit-profile";

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("client_id", editProfile_request.getClient_id());
            obj.put("client_secret", editProfile_request.getClient_secret());
            obj.put("display_pic", editProfile_request.getDisplay_pic());
            obj.put("fullname", editProfile_request.getFullname());
            obj.put("company", editProfile_request.getCompany());
            obj.put("designation", editProfile_request.getDesignation());
            obj.put("university", editProfile_request.getUniversity());
            obj.put("course", editProfile_request.getCourse());
            obj.put("city", editProfile_request.getCity());
            obj.put("state", editProfile_request.getState());
            obj.put("country", editProfile_request.getCountry());
            obj.put("latitude", editProfile_request.getLatitude());
            obj.put("longitude", editProfile_request.getLongitude());
            obj.put("dob_day", editProfile_request.getDob_day());
            obj.put("dob_month", editProfile_request.getDob_month());
            obj.put("dob_year", editProfile_request.getDob_year());

        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        Log.e("-object--"," "+obj);

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // error response
                        Log.d("Response", response.toString());
                        int code = -1;
                        try {
                            Log.e("response code"," "+response.getInt("code"));
                            code = response.getInt("code");
                            if(code != -1)
                            {
                                CommonFunctions.hDialog();
                                CommonFunctions.displayToast(context,response.getString("message"));
                            }
                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            CommonFunctions.hDialog();
                        }
                        try {

                            CommonFunctions.displayToast(context,response.getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        CommonFunctions.hDialog();
                        EditProfile_response editProfile_response = gson.fromJson(response.toString(), EditProfile_response.class);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(m_config.login_display_pic,editProfile_request.getDisplay_pic());
                        editor.putString(m_config.University, editProfile_request.getUniversity());
                        editor.putString(m_config.Course, editProfile_request.getCourse());
                        editor.putString(m_config.Company, editProfile_request.getCompany());
                        editor.putString(m_config.Designation, editProfile_request.getDesignation());
                        editor.putString(m_config.Profile_city, editProfile_request.getCity());
                        editor.putString(m_config.Profile_state, editProfile_request.getState());
                        editor.putString(m_config.Profile_country, editProfile_request.getCountry());
                        editor.commit();
                        CommonFunctions.displayToast(context,editProfile_response.getMessage());
                        Intent resultIntent = new Intent();
                        ((Activity)context).setResult(Activity.RESULT_OK, resultIntent);
                        ((Activity)context).finish();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CommonFunctions.hDialog();

                        if(error.networkResponse != null) {
                            String networkresponse_data = new String(error.networkResponse.data);
                            if (error.networkResponse.statusCode == 500) {
                                CommonFunctions.displayToast(context, context.getResources().getString(R.string.internal_server_error));
                            } else {
                                if (networkresponse_data != null) {
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
                                        CommonFunctions.displayToast(context, context.getResources().getString(R.string.Edit_profile_failed));
                                    }

                                }
                            }
                        }else
                        {
                            CommonFunctions.displayToast(context,context.getResources().getString(R.string.Edit_profile_failed));
                        }
                    }


                }
        )

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("version","1");
                params.put("devicetype","android");
                params.put("timezone",CommonFunctions.getTimeZoneIST());
                params.put("Authorization","Bearer " + sharedPreferences.getString(m_config.login_access_token,""));
                params.put("timesstamp",CommonFunctions.getTimeStamp());
                params.put("os_version",m_config.os_version);

                return params;
            }
        };

        // add it to the RequestQueue
        int socketTimeout = 10000;//60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        getRequest.setRetryPolicy(policy);

        // Adding request to request queue
        ViintroApplication.getInstance().addToRequestQueue(getRequest, "update_display_pic_api");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getSelfLocation();
    }
}
