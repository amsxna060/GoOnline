package com.amansiol.goonline;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.amansiol.goonline.constant.AddressKey;
import com.amansiol.goonline.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;
/*
This is MyAccount Fragment Class in Which we
fetch data regarding current user and populate
it into views like name ,email , current address.
And also we can Edit name and Edit Address of User
 */

public class MyAccountFragment extends Fragment {

    //view declaration

    TextInputLayout editpincode, editlocality, editsublocality, editstate, editcountry;
    EditText editname;
    Button change_address;
    ImageView name_toggle, address_toggle, save_name;
    RelativeLayout edit_address_layout;
    TextView add_value, name_value, email_value, unverified;
    ProgressBar progressBar;
    User tempUser;
    HashMap<String, String> edit_add_map;

    /*
  this function is for initialize all the views and objects
   */
    void initView(View view) {
        editname = view.findViewById(R.id.editname);
        editpincode = view.findViewById(R.id.TIL_pincode);
        edit_address_layout = view.findViewById(R.id.edit_address_layout);
        editlocality = view.findViewById(R.id.TIL_locality);
        editsublocality = view.findViewById(R.id.TIL_sub_locality);
        editstate = view.findViewById(R.id.TIL_state);
        editcountry = view.findViewById(R.id.TIL_country);
        change_address = view.findViewById(R.id.change_address);
        name_toggle = view.findViewById(R.id.name_toggle);
        address_toggle = view.findViewById(R.id.address_toggle);
        add_value = view.findViewById(R.id.add_value);
        name_value = view.findViewById(R.id.name_value);
        email_value = view.findViewById(R.id.email_value);
        unverified = view.findViewById(R.id.unverified);
        save_name = view.findViewById(R.id.save_name);
        progressBar = view.findViewById(R.id.loading);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        // init views
        initView(view);
        // get All value of user from cloud fire store
        // and inflating value to views in name field or address etc.
        getAllDetails();

        /*
        Now i m defining all the
        click listener events
        on all necessary views.
         */
        name_toggle.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            name_value.setVisibility(View.GONE);
            name_toggle.setVisibility(View.GONE);
            editname.setVisibility(View.VISIBLE);
            save_name.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            editname.requestFocus();
        });

        address_toggle.setOnClickListener(v -> {
            edit_address_layout.setVisibility(View.VISIBLE);
            address_toggle.setVisibility(View.GONE);
            add_value.setVisibility(View.GONE);
        });

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
                    .collection("Users")
                    .document(MainActivity.usernameUid)
                    .update("address", edit_add_map);
            //for refresh details again
            getAllDetails();

            edit_address_layout.setVisibility(View.GONE);
            address_toggle.setVisibility(View.VISIBLE);
            add_value.setVisibility(View.VISIBLE);
        });

        save_name.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);

            /*
            Here i m updating name on cloud firestore
             */
            if (!editname.getText().toString().isEmpty()) {
                FirebaseFirestore
                        .getInstance()
                        .collection("Users")
                        .document(MainActivity.usernameUid)
                        .update("name", editname.getText().toString());
                //for refresh details again
                getAllDetails();
            }
            progressBar.setVisibility(View.GONE);
            name_value.setVisibility(View.VISIBLE);
            name_toggle.setVisibility(View.VISIBLE);
            editname.setVisibility(View.GONE);
            save_name.setVisibility(View.GONE);
        });

        return view;
    }

    /*
    This Function is used for inflate data into views
 */
    void inflateValue(User user) {
        name_value.setText(user.getName());
        email_value.setText(user.getEmail());
        HashMap<String, String> addressmap;
        addressmap = user.getAddress();
        edit_add_map = new HashMap<>();
        edit_add_map.putAll(addressmap);
        add_value.setText(String.format(
                "%s\n%s\n%s ,%s\n%s ,%s "
                , user.getName()
                , addressmap.get(AddressKey.SUBLOCALITY)
                , addressmap.get(AddressKey.LOCALITY)
                , addressmap.get(AddressKey.POSTAL_CODE)
                , addressmap.get(AddressKey.STATE_NAME)
                , addressmap.get(AddressKey.COUNTRY_NAME)
        ));

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
                .collection("Users")
                .document(MainActivity.usernameUid)
                .get()
                .addOnSuccessListener(
                        document -> {
                            if (document.exists()) {
                                tempUser = document.toObject(User.class);
                                if (tempUser != null) {
                                    inflateValue(tempUser);
                                    progressBar.setVisibility(View.GONE);
                                }

                            } else {
                                Toast.makeText(getContext(), "No person Exist", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }

                        }
                ).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        });
    }

    private void checkUserLoginState() {
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){


        }else
        {
            startActivity(new Intent(getActivity(),SignInActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        checkUserLoginState();
        FirebaseUser muser=FirebaseAuth.getInstance().getCurrentUser();
        if(muser!=null){
            if(muser.isEmailVerified()){
                HashMap<String, Object> results = new HashMap<>();
                results.put("isverified","Verified");
                FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(MainActivity.usernameUid)
                        .update(results)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                unverified.setText("Verified");
                                unverified.setTextColor(Color.parseColor("#42E500"));
                                unverified.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_check_circle_24,0);
                            }
                        });
            }else
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Verification");
                builder.setMessage("Please Verify your account with google.");
                builder.setPositiveButton("Go", (dialog, which) -> {
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.sendEmailVerification()
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    Toast.makeText(getActivity(),"Verification email sent to"+user.getEmail(),Toast.LENGTH_LONG).show();
                                    AlertDialog.Builder builder1=new AlertDialog.Builder(getActivity());
                                    builder1.setMessage("Verification email sent to "+user.getEmail()+" Go to gmail and click on link..");
                                    builder1.setCancelable(false);
                                    builder1.setPositiveButton("OK", (dialog1, which1) -> {
                                        FirebaseAuth.getInstance().signOut();
                                        checkUserLoginState();
                                    });
                                    builder1.create().show();
                                }else {
                                    Toast.makeText(getActivity(),"Email Verification failed..",Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(e -> Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_LONG).show());
                });
                builder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }

        }

    }

}

//7223940924