package com.amansiol.goonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amansiol.goonline.constant.AddressKey;
import com.amansiol.goonline.models.Shops;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CodeConfirmActivity extends AppCompatActivity {


    private String phoneNumber;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private Shops shop;
    FirebaseFirestore db;
    private String usernameUid;
    Button verifyButton;
    EditText codeText;
    ProgressBar progressBar;
    ProgressBar codeBar;
    String code;
    PhoneAuthProvider.ForceResendingToken mToken;
    GeoPoint geoPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_confirm);
        shop = new Shops();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        verifyButton=findViewById(R.id.verify);
        codeText = findViewById(R.id.code);
        progressBar = findViewById(R.id.progressBar);
        codeBar =findViewById(R.id.codeBar);


        if (mAuth != null) {
            FirebaseUser mUser = mAuth.getCurrentUser();
            if (mUser != null) {
                usernameUid = mUser.getUid();
            }
        }
        codeBar.setVisibility(View.VISIBLE);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull @NotNull PhoneAuthCredential phoneAuthCredential) {
                Log.d("CodeConfirmActivity","veriCompleted"+phoneAuthCredential.getSmsCode());
                codeText.setText(phoneAuthCredential.getSmsCode());
                codeBar.setVisibility(View.GONE);
                writeDataToCloudFireStore();
            }

            @Override
            public void onVerificationFailed(@NonNull @NotNull FirebaseException e) {
                Log.d("CodeConfirm","failed"+e.getMessage());
                codeBar.setVisibility(View.GONE);
                Toast.makeText(CodeConfirmActivity.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    Log.d("CodeConfirm","invalid creditail"+e.getMessage());
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Log.d("CodeConfirm","too many request"+e.getMessage());
                }
            }

            @Override
            public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.d("CodeConfirm","code sent"+s);
                Toast.makeText(CodeConfirmActivity.this,s,Toast.LENGTH_LONG).show();
                codeBar.setVisibility(View.GONE);
                code = s;
                mToken=forceResendingToken;
                verifyButton.setOnClickListener(v -> {
                    if(codeText.getText().toString().equals(code))
                    {
                        writeDataToCloudFireStore();
                    }
                    else {
                        Toast.makeText(CodeConfirmActivity.this, "Invalid Code", Toast.LENGTH_LONG).show();
                    }
                });

            }
        };

        phoneNumber = getIntent().getStringExtra("number");
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)        // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
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

                    geoPoint = new GeoPoint(latitude, longitude);
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
        addressby_user.put(AddressKey.SUBLOCALITY, getIntent().getStringExtra("sublocality"));
        addressby_user.put(AddressKey.LOCALITY,getIntent().getStringExtra("locality") );
        addressby_user.put(AddressKey.STATE_NAME, getIntent().getStringExtra("statename"));
        addressby_user.put(AddressKey.COUNTRY_NAME, getIntent().getStringExtra("countryname"));
        addressby_user.put(AddressKey.POSTAL_CODE, getIntent().getStringExtra("postalname"));
        progressBar.setVisibility(View.VISIBLE);
        goToLocationFromAddress(String.format(
                "%s %s %s %s %s"
                , addressby_user.get(AddressKey.SUBLOCALITY)
                , addressby_user.get(AddressKey.LOCALITY)
                , addressby_user.get(AddressKey.STATE_NAME)
                , addressby_user.get(AddressKey.POSTAL_CODE)
                , addressby_user.get(AddressKey.COUNTRY_NAME)
        ));
        shop.setShopname(getIntent().getStringExtra("businessname"));
        shop.setPhonenumber(getIntent().getStringExtra("number"));
        shop.setAddress(addressby_user);
        shop.setName(MainActivity.susername);
        shop.setUid(MainActivity.usernameUid);
        shop.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        shop.setLocation(geoPoint);
        db.collection("Shops").document(usernameUid)
                .set(shop).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(CodeConfirmActivity.this, "Register sucessfully", Toast.LENGTH_LONG).show();
                    FirebaseFirestore
                            .getInstance()
                            .collection("Users")
                            .document(MainActivity.usernameUid)
                            .update("haveshop", true);
                    Intent intent = new Intent(CodeConfirmActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CodeConfirmActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CodeConfirmActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }
}