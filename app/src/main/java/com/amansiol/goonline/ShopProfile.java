package com.amansiol.goonline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amansiol.goonline.constant.AddressKey;
import com.amansiol.goonline.models.Shops;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ShopProfile extends AppCompatActivity {

    EditText editname;
    ImageView name_toggle, save_name;
    TextView add_value, name_value, phonenumber;
    ProgressBar progressBar;
    Shops tempUser;

    void initView() {
        editname = findViewById(R.id.editname);
        name_toggle = findViewById(R.id.name_toggle);
        add_value = findViewById(R.id.add_value);
        name_value = findViewById(R.id.name_value);
        phonenumber = findViewById(R.id.email_value);
        save_name = findViewById(R.id.save_name);
        progressBar = findViewById(R.id.loading);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat
                .getColor(getApplicationContext(), R.color.colorPrimary));
        setContentView(R.layout.activity_shop_profile);
        initView();
        getAllDetails();

        name_toggle.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            name_value.setVisibility(View.GONE);
            name_toggle.setVisibility(View.GONE);
            editname.setVisibility(View.VISIBLE);
            save_name.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            editname.requestFocus();
        });

        save_name.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);

            /*
            Here i m updating name on cloud firestore
             */
            if (!editname.getText().toString().isEmpty()) {
                FirebaseFirestore
                        .getInstance()
                        .collection("Shops")
                        .document(MainActivity.usernameUid)
                        .update("shopname", editname.getText().toString());
                //for refresh details again
                getAllDetails();
            }
            progressBar.setVisibility(View.GONE);
            name_value.setVisibility(View.VISIBLE);
            name_toggle.setVisibility(View.VISIBLE);
            editname.setVisibility(View.GONE);
            save_name.setVisibility(View.GONE);
        });
    }

    /*
This Function is used for inflate data into views
*/
    void inflateValue(Shops shop) {
        name_value.setText(shop.getShopname());
        phonenumber.setText(shop.getPhonenumber());
        HashMap<String, String> addressmap;
        addressmap = shop.getAddress();
        add_value.setText(String.format(
                "%s\n%s\n%s ,%s\n%s ,%s "
                , shop.getName()
                , addressmap.get(AddressKey.SUBLOCALITY)
                , addressmap.get(AddressKey.LOCALITY)
                , addressmap.get(AddressKey.POSTAL_CODE)
                , addressmap.get(AddressKey.STATE_NAME)
                , addressmap.get(AddressKey.COUNTRY_NAME)
        ));

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
                                Toast.makeText(ShopProfile.this, "No person Exist", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }

                        }
                ).addOnFailureListener(e -> {
            Toast.makeText(ShopProfile.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        });
    }
}