package com.amansiol.goonline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amansiol.goonline.constant.AddressKey;
import com.amansiol.goonline.models.Shops;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ChangeAddressShop extends AppCompatActivity {

    TextInputLayout editpincode, editlocality, editsublocality, editstate, editcountry;
    Button change_address;
    ProgressBar progressBar;
    Shops tempUser;
    HashMap<String, String> edit_add_map;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat
                .getColor(getApplicationContext(), R.color.colorPrimary));
        setContentView(R.layout.activity_change_address_shop);
        init();
        getAllDetails();

        change_address.setOnClickListener(v -> {


            if (Objects.requireNonNull(editsublocality
                    .getEditText())
                    .getText()
                    .toString()
                    != null) {
                edit_add_map.put(AddressKey.SUBLOCALITY,
                        editsublocality.getEditText()
                                .getText().toString()
                                .trim());
            }
            if (editlocality
                    .getEditText()
                    .getText()
                    .toString()
                    != null) {
                edit_add_map
                        .put(AddressKey.LOCALITY,
                                editlocality
                                        .getEditText()
                                        .getText()
                                        .toString()
                                        .trim());
            }
            if (editcountry
                    .getEditText()
                    .getText()
                    .toString()
                    != null) {
                edit_add_map
                        .put(AddressKey.COUNTRY_NAME,
                                editcountry
                                        .getEditText()
                                        .getText()
                                        .toString()
                                        .trim());
            }
            if (editstate
                    .getEditText()
                    .getText()
                    .toString()
                    != null) {
                edit_add_map
                        .put(AddressKey.STATE_NAME,
                                editstate
                                        .getEditText()
                                        .getText()
                                        .toString()
                                        .trim());

            }
            if (editpincode
                    .getEditText()
                    .getText()
                    .toString()
                    != null) {
                if (editpincode
                        .getEditText()
                        .getText()
                        .toString()
                        .length() > 7) {
                    editpincode.setError("Pincode can't be greater than 7");
                    editpincode.requestFocus();
                    return;
                } else {
                    edit_add_map
                            .put(AddressKey.POSTAL_CODE,
                                    editpincode
                                            .getEditText()
                                            .getText()
                                            .toString()
                                            .trim());
                }
            }
            FirebaseFirestore
                    .getInstance()
                    .collection("Shops")
                    .document(MainActivity.usernameUid)
                    .update("address", edit_add_map);

            goToLocationFromAddress(String.format(
                    "%s %s %s %s %s"
                    , edit_add_map.get(AddressKey.SUBLOCALITY)
                    , edit_add_map.get(AddressKey.LOCALITY)
                    , edit_add_map.get(AddressKey.STATE_NAME)
                    , edit_add_map.get(AddressKey.POSTAL_CODE)
                    , edit_add_map.get(AddressKey.COUNTRY_NAME)
            ));
            //for refresh details again
            startActivity(new Intent(ChangeAddressShop.this,DashBoard.class));
            finish();
        });

    }

    private void init() {
        editpincode = findViewById(R.id.TIL_pincode);
        editlocality = findViewById(R.id.TIL_locality);
        editsublocality = findViewById(R.id.TIL_sub_locality);
        editstate = findViewById(R.id.TIL_state);
        editcountry = findViewById(R.id.TIL_country);
        change_address = findViewById(R.id.change_address);
        progressBar = findViewById(R.id.loading);
    }
    void inflateValue(Shops user) {
        HashMap<String, String> addressmap;
        addressmap = user.getAddress();
        edit_add_map = new HashMap<>();
        edit_add_map.putAll(addressmap);
        // helper text is like a hint but show below the box.
        editcountry.setHelperText(addressmap.get(AddressKey.COUNTRY_NAME));
        editpincode.setHelperText(addressmap.get(AddressKey.POSTAL_CODE));
        editstate.setHelperText(addressmap.get(AddressKey.STATE_NAME));
        editlocality.setHelperText(addressmap.get(AddressKey.LOCALITY));
        editsublocality.setHelperText(addressmap.get(AddressKey.SUBLOCALITY));
    }
    // this function is used for fetching details from cloud firestore.
    void getAllDetails() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore
                .getInstance()
                .collection("Shops")
                .document(MainActivity.usernameUid)
                .get()
                .addOnSuccessListener(
                        document -> {
                            if (document.exists()) {
                                tempUser = document.toObject(Shops.class);
                                if (tempUser != null) {
                                    inflateValue(tempUser);
                                    progressBar.setVisibility(View.GONE);
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "No person Exist", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }

                        }
                ).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
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
                    double latitude=location.getLatitude();
                    double longitude =location.getLongitude();
                    GeoPoint geoPoint = new GeoPoint(latitude,longitude);
                    FirebaseFirestore
                            .getInstance()
                            .collection("Shops")
                            .document(MainActivity.usernameUid)
                            .update("location",geoPoint);

                    //Animate and Zoon on that map location
                } catch (IndexOutOfBoundsException er) {
                    Toast.makeText(this, "Location isn't available", Toast.LENGTH_SHORT).show();
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}