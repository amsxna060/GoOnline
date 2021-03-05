package com.amansiol.goonline;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.amansiol.goonline.constant.AddressKey;
import com.amansiol.goonline.constant.Constants;
import com.amansiol.goonline.models.CountryCode;
import com.amansiol.goonline.models.Shops;
import com.amansiol.goonline.services.FetchAddressIntentService;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;

public class RegisterShopActivity extends AppCompatActivity {
    FirebaseFirestore db;
    EditText business_name, business_contact;
    TextInputLayout Tpincode, Tlocality, Tsublocality, Tstate, Tcountry;
    Button register;
    String useremail;
    String usernameUid;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    Spinner code;
    String country_code;
    ArrayList<CountryCode> countryCodes;
    private ResultReceiver resultReceiver;
    GeoPoint geoPoint;
    HashMap<String, String> address;
    ProgressDialog pd;
    Shops shop;
    SwitchMaterial verifyswitch;

    void init() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        business_name = findViewById(R.id.business_name);
        business_contact = findViewById(R.id.business_contact);
        Tpincode = findViewById(R.id.TIL_pincode);
        Tlocality = findViewById(R.id.TIL_locality);
        Tsublocality = findViewById(R.id.TIL_sub_locality);
        Tstate = findViewById(R.id.TIL_state);
        Tcountry = findViewById(R.id.TIL_country);
        register = findViewById(R.id.shopreg_btn);
        progressBar = findViewById(R.id.progressBar);
        code = findViewById(R.id.business_contact_code);
        countryCodes = new ArrayList<>();
        countryCodes.add(new CountryCode("Ireland", "+353"));
        countryCodes.add(new CountryCode("India", "+91"));
        countryCodes.add(new CountryCode("USA", "+1"));
        resultReceiver = new AddressResultReceiver(new Handler());
        address = new HashMap<>();
        pd = new ProgressDialog(this);
        shop = new Shops();
        verifyswitch=findViewById(R.id.verifyswitch);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat
                .getColor(getApplicationContext(), R.color.colorPrimary));
        setContentView(R.layout.activity_register_shop);

        init();

        int[] to = {R.id.country_name, R.id.code};
        String[] from = {"countryname", "countrycode"};
        ArrayList<HashMap<String, String>> collection = new ArrayList<>();
        for (int i = 0; i < countryCodes.size(); i++) {
            HashMap<String, String> hashmap = new HashMap<>();
            hashmap.put("countryname", countryCodes.get(i).getCountry_name());
            hashmap.put("countrycode", countryCodes.get(i).getCountry_code());//you have to convert into String.
            collection.add(hashmap);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(),
                collection, R.layout.phonecode, from, to);
        code.setAdapter(simpleAdapter);
        code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getApplicationContext(), countryCodes.get(position).getCountry_code(), Toast.LENGTH_LONG).show();
                country_code = countryCodes.get(position).getCountry_code();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                country_code = countryCodes.get(0).getCountry_code();
            }
        });

        if (mAuth != null) {
            FirebaseUser mUser = mAuth.getCurrentUser();
            if (mUser != null) {
                useremail = mUser.getEmail();
                usernameUid = mUser.getUid();
            }
        }


        register.setOnClickListener(v -> {

            if (business_name.getText().toString().isEmpty()) {
                business_name.setError("Invalid Field");
                business_name.requestFocus();
            } else if (country_code.length() == 2 && business_contact.getText().toString().length() != 10) {
                business_contact.setError("Invalid Field");
                business_contact.requestFocus();
            } else if (country_code.length() == 3 && business_contact.getText().toString().length() != 10) {
                business_contact.setError("Invalid Field");
                business_contact.requestFocus();
            } else if (country_code.length() == 4 && business_contact.getText().toString().length() != 9) {
                business_contact.setError("Invalid Field");
                business_contact.requestFocus();
            } else if (Tpincode.getEditText().getText().toString().length() > 8) {
                Tpincode.setError("Invalid Field");
                Tpincode.requestFocus();
            } else if (Tlocality.getEditText().getText().toString().isEmpty() || !(Tlocality.getEditText().getText().toString().length() < 100)) {
                Tlocality.setError("Invalid Field");
                Tlocality.requestFocus();
            } else if (Tsublocality.getEditText().getText().toString().isEmpty() || !(Tsublocality.getEditText().getText().toString().length() < 100)) {
                Tsublocality.setError("Invalid Field");
                Tsublocality.requestFocus();
            } else if (Tstate.getEditText().getText().toString().isEmpty() || !(Tstate.getEditText().getText().toString().length() < 50)) {
                Tstate.setError("Invalid Field");
                Tstate.requestFocus();
            } else if (Tcountry.getEditText().getText().toString().isEmpty() || !(Tcountry.getEditText().getText().toString().length() < 50)) {
                Tcountry.setError("Invalid Field");
                Tcountry.requestFocus();
            } else {
                if(verifyswitch.isChecked())
                {
                    Intent intent = new Intent(RegisterShopActivity.this, CodeConfirmActivity.class);
                    intent.putExtra("number", country_code + business_contact.getText().toString().trim());
                    intent.putExtra("businessname", business_name.getText().toString().trim());
                    intent.putExtra("sublocality",Tsublocality.getEditText().getText().toString());
                    intent.putExtra("locality", Tlocality.getEditText().getText().toString() );
                    intent.putExtra("statename", Tstate.getEditText().getText().toString());
                    intent.putExtra("countryname",Tcountry.getEditText().getText().toString() );
                    intent.putExtra("postalname",Tpincode.getEditText().getText().toString() );
                    startActivity(intent);
                    finish();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    writeDataToCloudFireStore();
                }
            }
        });

    }
    public void goToLocationFromAddress(String strAddress) {
        //Create coder with Activity context - this
        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            //Get latLng from String
            address = coder.getFromLocationName(strAddress, 5);

            //check for null
            if (address != null) {

                //Lets take first possibility from the all possibilities.
                try {
                    Address location = address.get(0);
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                    FirebaseFirestore
                            .getInstance()
                            .collection("Shops")
                            .document(MainActivity.usernameUid)
                            .update("location", geoPoint);

                    //Animate and Zoon on that map location
                } catch (IndexOutOfBoundsException er) {
                    Toast.makeText(this, "Location isn't available", Toast.LENGTH_SHORT).show();
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeDataToCloudFireStore() {
        HashMap<String, String> addressby_user = new HashMap<>();
        addressby_user.put(AddressKey.SUBLOCALITY,Tsublocality.getEditText().getText().toString());
        addressby_user.put(AddressKey.LOCALITY,Tlocality.getEditText().getText().toString());
        addressby_user.put(AddressKey.STATE_NAME, Tstate.getEditText().getText().toString());
        addressby_user.put(AddressKey.COUNTRY_NAME,Tcountry.getEditText().getText().toString() );
        addressby_user.put(AddressKey.POSTAL_CODE,Tpincode.getEditText().getText().toString() );
        progressBar.setVisibility(View.VISIBLE);
        goToLocationFromAddress(String.format(
                "%s %s %s %s %s"
                , addressby_user.get(AddressKey.SUBLOCALITY)
                , addressby_user.get(AddressKey.LOCALITY)
                , addressby_user.get(AddressKey.STATE_NAME)
                , addressby_user.get(AddressKey.POSTAL_CODE)
                , addressby_user.get(AddressKey.COUNTRY_NAME)
        ));
        shop.setShopname(business_name.getText().toString().trim());
        shop.setPhonenumber(country_code+" " +business_contact.getText().toString());
        shop.setAddress(addressby_user);
        db.collection("Shops").document(usernameUid)
                .set(shop).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(RegisterShopActivity.this, "Register sucessfully", Toast.LENGTH_LONG).show();
                    FirebaseFirestore
                            .getInstance()
                            .collection("Users")
                            .document(MainActivity.usernameUid)
                            .update("haveshop", true);
                    Intent intent = new Intent(RegisterShopActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterShopActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterShopActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
//        Animatoo.animateShrink(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(RegisterShopActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(RegisterShopActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 777);
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return;
        }

        pd.setTitle("Location Access");
        pd.setMessage("Accessing Current Location...");
        pd.show();

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
        LocationServices.getFusedLocationProviderClient(RegisterShopActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(RegisterShopActivity.this)
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
                            pd.dismiss();
                        }
                    }
                }, Looper.getMainLooper());
    }

    private void fetchAddressFromLatLong(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);

    }

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
                Tsublocality.getEditText().setText(address.get(AddressKey.SUBLOCALITY));
                Tpincode.getEditText().setText(address.get(AddressKey.POSTAL_CODE));
                Tlocality.getEditText().setText(address.get(AddressKey.LOCALITY));
                Tstate.getEditText().setText(address.get(AddressKey.STATE_NAME));
                Tcountry.getEditText().setText(address.get(AddressKey.COUNTRY_NAME));
                shop.setAddress(address);
                shop.setLocation(geoPoint);
                shop.setName(MainActivity.susername);
                shop.setEmail(useremail);
                shop.setUid(usernameUid);
                pd.dismiss();

            } else {
                String error = Constants.RESULT_DATA_KEY;
                Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();
            }
            pd.dismiss();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterShopActivity.this);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 777 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(RegisterShopActivity.this, "Please allow location permission", Toast.LENGTH_LONG).show();
            }
        }
    }


}