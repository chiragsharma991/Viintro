package com.viintro.Viintro.OnBoarding;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Constants.Constcore;
import com.viintro.Viintro.Model.Onboarding_Request;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;
import com.viintro.Viintro.Reusables.TextFont;
import com.viintro.Viintro.Webservices.OnboardingAPI;

import java.util.List;
import java.util.Locale;

import static com.viintro.Viintro.Constants.Constcore.MY_PERMISSIONS_REQUEST_RWFRMCAM;
//FBFNT
public class LocationActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private ImageView find_location;
    private TextView text_selectedlocation, text_location;
    private Button btn_submitLocation;
    private GoogleApiClient mGoogleApiClient;
    private Button btnFind;
    private int PLACE_PICKER_REQUEST = 1;
    Context context;
    Location currentlocation;
    SharedPreferences sharedpreferences;
    Configuration_Parameter m_config;
    LocationManager locationManager;
    String city, state, country, latitude, longitude ,location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
       // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_location);
        context = this;
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        m_config = Configuration_Parameter.getInstance();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //call to googleapiclient to fetch places
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        city = ""; state = ""; country = ""; latitude = ""; longitude = "";
        initialiseUI();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void initialiseUI() {

        find_location = (ImageView) findViewById(R.id.find_location);
        text_selectedlocation = (TextView) findViewById(R.id.text_selectedlocation);
        text_selectedlocation.setTypeface(TextFont.opensans_regular(context));
        btn_submitLocation = (Button) findViewById(R.id.btn_submitLocation);
        text_location = (TextView) findViewById(R.id.text_location);
        text_location.setTypeface(TextFont.opensans_semibold(context));

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
                    if (ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            &&
                            ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                Constcore.MY_PERMISSIONS_ACCESS_CF_LOCATION);
                    } else {
                        getSelfLocation();

                    }
                }




            }
        });

        btn_submitLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!text_selectedlocation.getText().toString().equals("Select location")){
                    if(CommonFunctions.chkStatus(context))
                    {

                        Intent i = getIntent();
                        String usertype = i.getExtras().getString("user_type");
                        String university_company = i.getExtras().getString("university_company");
                        String course_designation = i.getExtras().getString("course_designation");
                        String country_code = i.getExtras().getString("country_code");
                        String mobno = i.getExtras().getString("mobno");

                        CommonFunctions.sDialog(context,"Loading");

                        if(usertype.equals("student"))
                        {
                            Onboarding_Request onboarding_request = new Onboarding_Request();
                            onboarding_request.setClient_id(Constcore.client_Id);
                            onboarding_request.setClient_secret(Constcore.client_Secret);
                            onboarding_request.setUser_type(usertype);
                            onboarding_request.setUniversity(university_company);
                            onboarding_request.setCourse(course_designation);
                            onboarding_request.setCountry_code(country_code);
                            onboarding_request.setMobile_number(mobno);
                            onboarding_request.setCompany("");
                            onboarding_request.setDesignation("");
                            onboarding_request.setCity(city);
                            onboarding_request.setState(state);
                            onboarding_request.setCountry(country);
                            onboarding_request.setLatitude(latitude);
                            onboarding_request.setLongitude(longitude);

                            OnboardingAPI.req_onboarding_API(context, onboarding_request);
                        }
                        else
                        {
                            Onboarding_Request onboarding_request = new Onboarding_Request();
                            onboarding_request.setClient_id(Constcore.client_Id);
                            onboarding_request.setClient_secret(Constcore.client_Secret);
                            onboarding_request.setUser_type(usertype);
                            onboarding_request.setUniversity("");
                            onboarding_request.setCourse("");
                            onboarding_request.setCountry_code(country_code);
                            onboarding_request.setMobile_number(mobno);
                            onboarding_request.setCompany(university_company);
                            onboarding_request.setDesignation(course_designation);
                            onboarding_request.setCity(city);
                            onboarding_request.setState(state);
                            onboarding_request.setCountry(country);
                            onboarding_request.setLatitude(latitude);
                            onboarding_request.setLongitude(longitude);

                            OnboardingAPI.req_onboarding_API(context, onboarding_request);
                        }

                    }
                    else
                    {
                        CommonFunctions.displayToast(context,getResources().getString(R.string.network_connection));
                    }

                }else{


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

    }

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

                    city = addresses.get(0).getLocality();
                    state = addresses.get(0).getAdminArea();
                    country = addresses.get(0).getCountryName();
                    text_selectedlocation.setText(city + "," + state + "," +country);


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
                    Toast.makeText(LocationActivity.this, "Please select high accuracy mode.", Toast.LENGTH_LONG).show();
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

                        city = addresses.get(0).getLocality();
                        state = addresses.get(0).getAdminArea();
                        country = addresses.get(0).getCountryName();
                        text_selectedlocation.setText(city + "," + state + "," +country);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults)
    {

        switch (requestCode)
        {

            case Constcore.MY_PERMISSIONS_ACCESS_CF_LOCATION:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    getSelfLocation();

                }
                else
                {
                    // permission was not granted
                    if (context == null)
                    {
                        return;
                    }
                    if (ActivityCompat.shouldShowRequestPermissionRationale(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))
                    {
                        //showStoragePermissionRationale();

                        new AlertDialog.Builder(LocationActivity.this)
                                .setTitle("Permission Denied")
                                .setMessage(getResources().getString(R.string.message_cf_location_permission))
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // continue with delete
                                        ActivityCompat.requestPermissions(LocationActivity.this,
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                                MY_PERMISSIONS_REQUEST_RWFRMCAM);
                                    }
                                })
                                .setNegativeButton("I'm Sure", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .show();
                        break;

                    }
                    else
                    {

                    }
                    break;

                }
            }
        }
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
            return locationGPS;
        }
        else {
            return locationNet;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                //get data
                Place place = PlaceAutocomplete.getPlace(this, data);
                text_selectedlocation.setText(place.getAddress());

                String address = (String) place.getAddress();
                String[] add = address.split(",");
                city = add[0].trim();
                state = add[1].trim();
                country = add[2].trim();
                LatLng latlong = place.getLatLng();
                latitude = String.valueOf(latlong.latitude);
                longitude = String.valueOf(latlong.longitude);

                Log.e("----"," "+city+" "+state+" "+country+" "+latitude+" "+longitude);


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

}
