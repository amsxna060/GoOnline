package com.amansiol.goonline;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amansiol.goonline.constant.AddressKey;
import com.amansiol.goonline.constant.Constants;
import com.amansiol.goonline.models.User;
import com.amansiol.goonline.services.FetchAddressIntentService;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import static android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
/*
step 1 : we ask to allow  user to access the location to this application.
step 2 : we check gps is enabled or disabled. if disabled we show a dialog box to enable it.
step 3 : then we first get longitude and latitude of current location of user
         with the help of FusedLocationProviderClient.
step 4 : then we start Intent service and get address from service and send it to receiver
step 5 : then in this receiver we upload all the data of user to cloud fire store.

 */

public class LocationAccessActivity extends AppCompatActivity {

    // init all the classes object and views.

    Button allow_loc;
    ProgressBar progressbar;
    TextView latilong;
    TextView addressvalue;
    private ResultReceiver resultReceiver;
    GeoPoint geoPoint;
    User user;
    HashMap<String, String> address;
    FirebaseFirestore firestore;
    CollectionReference usersCollections;
    String snameofuser;
    String useremail;
    String usernameUid;
    FirebaseAuth mAuth;
    String sgenderofuser;
    String userpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat
                .getColor(getApplicationContext(), R.color.colorPrimary));
        setContentView(R.layout.activity_location_access);
        allow_loc=findViewById(R.id.allow_loc);
        progressbar=findViewById(R.id.pb);
        addressvalue=findViewById(R.id.addressvalue);
        latilong=findViewById(R.id.latilong);
        resultReceiver = new AddressResultReceiver(new Handler());
        user = new User();
        address = new HashMap<>();
        firestore = FirebaseFirestore.getInstance();
        usersCollections = firestore.collection("Users");
        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null) {
            FirebaseUser mUser = mAuth.getCurrentUser();
            useremail = mUser.getEmail();
            usernameUid = mUser.getUid();
        }
        snameofuser = getIntent().getStringExtra("username");
        sgenderofuser=getIntent().getStringExtra("usergender");
        userpassword=getIntent().getStringExtra("password");


        allow_loc.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(LocationAccessActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(LocationAccessActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 777);
            } else {
                getCurrentLocation();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(LocationAccessActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(LocationAccessActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 777);
        } else {
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 777 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(LocationAccessActivity.this, "Please allow location permission", Toast.LENGTH_LONG).show();
            }
        }
    }
    /*
    this function is starting point of fetching current
    location of user from where api will fetch longitude
    and latitude of user and we pass longitude and latitude to
    the function 'fetchAddressFromLatLong()' and then start service ,
    fetch address , upload to fire store.
     */
    private void getCurrentLocation() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return;
        }

        progressbar.setVisibility(View.VISIBLE);

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                return;
        }
//        progressbar.setVisibility(View.GONE);
        LocationServices.getFusedLocationProviderClient(LocationAccessActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(LocationAccessActivity.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                            geoPoint = new GeoPoint(latitude, longitude);

                            Location location = new Location("providerNA");
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);
                            fetchAddressFromLatLong(location);

                        } else {
                            progressbar.setVisibility(View.GONE);
                        }
                    }
                }, Looper.getMainLooper());
    }
    /*
    this function will start intent service to fetch
    address and return back to receiver and then upload address
    to cloud fire store.
     */
    private void fetchAddressFromLatLong(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);

    }
    /*
    This is AddressResultReceiver class where we get result
    which we sent from AddressIntentService and
    after getting address and other details of user like name, email etc.
    then save all details to cloud firestore.
     */
    private class AddressResultReceiver extends ResultReceiver {

        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (Constants.SUCCESS_RESULT == resultCode) {
                String add_result = resultData.getString(Constants.RESULT_DATA_KEY);
                String[] add_list = add_result.split("#");
                address.put(AddressKey.SUBLOCALITY, add_list[0] + " ," + add_list[1]);
                address.put(AddressKey.LOCALITY, add_list[2]);
                address.put(AddressKey.STATE_NAME, add_list[3]);
                address.put(AddressKey.COUNTRY_NAME, add_list[4]);
                address.put(AddressKey.POSTAL_CODE, add_list[5]);
                user.setAddress(address);
                user.setLocation(geoPoint);
                user.setName(snameofuser);
                user.setEmail(useremail);
                user.setUid(usernameUid);
                user.setHaveshop(false);
                user.setGender(sgenderofuser);
                user.setVerified(false);
                usersCollections.document(usernameUid).set(user)
                        .addOnSuccessListener(aVoid -> {
                            Intent intent = new Intent(LocationAccessActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG).show());

            } else {
                String error = Constants.RESULT_DATA_KEY;
                Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();
            }
            progressbar.setVisibility(View.GONE);
        }
    }
    /*
    this function we used to show dialog box
    to ask user to enable gps if it is disable
    in user's phone.
    by clicking enable button user will go to
    gps setting activity of phone where user will
    open the gps and return back to this activity then
    fetch all the address then go to mainActivity.
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LocationAccessActivity.this);

        builder.setTitle("Enable GPS")
                .setMessage("Your Gps seems to be disabled, do you want to enable it ? ")
                .setCancelable(false)
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(ACTION_LOCATION_SOURCE_SETTINGS), 405);
//                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 405 && resultCode == RESULT_OK) {
            getCurrentLocation();
        }
    }
}