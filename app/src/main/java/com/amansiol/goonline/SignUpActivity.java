package com.amansiol.goonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText email,password,name;
    ProgressBar progressBar;
    TextInputLayout passwordLayout;
    AutoCompleteTextView gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        getWindow().setStatusBarColor(Color.parseColor("#9F26DE"));
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        email=findViewById(R.id.register_email);
        password=findViewById(R.id.register_pass);
        name=findViewById(R.id.register_name);
        progressBar=findViewById(R.id.progressBar);
        passwordLayout=findViewById(R.id.TIL_pass);
        gender=findViewById(R.id.gender);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,new String[]{"Male","Female"});
        gender.setAdapter(genderAdapter);
        passwordLayout.setHelperText("*Field is Mandontary");
        passwordLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                verifyPassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void Goto_SignIn(View view) {
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }

    public void Sign_up_for_free(View view) {
        String s_email= email.getText().toString();
        String s_password= password.getText().toString();
        String s_name= name.getText().toString();
        if(!NetworkUtil.isConnected(getApplicationContext())){
            startActivity(new Intent(getApplicationContext(),NoInternet.class));
            finish();
        }else
        if(s_name.isEmpty()){
            name.setError("Please Fill Name!");
            name.requestFocus();
        }else
        if(!s_email.contains("@gmail.com")){
            email.setError("Invalid Email");
            email.requestFocus();
        }else if(verifyPassword(s_password)){
            registerUser(s_email,s_password,s_name);
        }else {
            password.setError("Invalid Error");
        }
    }

    private void registerUser(String s_email, String s_password, final String s_name) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(s_email, s_password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            progressBar.setVisibility(View.GONE);
                            Intent intent=new Intent(SignUpActivity.this,LocationAccessActivity.class);
                            intent.putExtra("username",s_name);
                            intent.putExtra("usergender",gender.getText().toString());
                            intent.putExtra("password",s_password);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }
    private boolean verifyPassword(String password){
        final Pattern PERFECT_MATCH
                = Pattern.compile(
                          "^"
                        + "(?=.*[0-9])"
                        + "(?=.*[a-z])"
                        + "(?=.*[A-Z])"
                        + "(?=.*[a-zA-Z])"
                        + "(?=.*[@#$%^&+=])"
                        + "(?=\\S+$)"
                        + ".{8,16}"
                        + "$"
        );
        final Pattern NUMBER_MISSING
                = Pattern.compile("(?=.*[0-9])" + ".{0,}");
        final Pattern UPPER_CASE_MISSING
                = Pattern.compile("(?=.*[A-Z])" + ".{0,}");
        final Pattern SYMBOL_MISSING
                = Pattern.compile("(?=.*[@#$%^&+=])" + ".{0,}");
        final Pattern LOWERCASE_MISSING
                = Pattern.compile("(?=.*[a-z])" + ".{0,}");

        if(password.isEmpty()){
            passwordLayout.setHelperText("*Field is Mandontary");
            passwordLayout.setHelperTextColor(ColorStateList.valueOf(Color.parseColor("#505050")));
            return  false;
        }else if(!UPPER_CASE_MISSING.matcher(password).matches()){
            passwordLayout.setHelperText("*Password Should Contain One Upper case Character");
            passwordLayout.setHelperTextColor(ColorStateList.valueOf(Color.parseColor("#505050")));
            return  false;
        }else if(!NUMBER_MISSING.matcher(password).matches()){
            passwordLayout.setHelperText("*Password Should Contain One  Numberical Digit");
            passwordLayout.setHelperTextColor(ColorStateList.valueOf(Color.parseColor("#505050")));
            return  false;
        }else if(!LOWERCASE_MISSING.matcher(password).matches()){
            passwordLayout.setHelperText("*Password Should Contain One Lower Case Character");
            passwordLayout.setHelperTextColor(ColorStateList.valueOf(Color.parseColor("#505050")));
            return  false;
        }else if(!SYMBOL_MISSING.matcher(password).matches()){
            passwordLayout.setHelperText("*Password Should Contain One Symbol (@#$%^&+=)");
            passwordLayout.setHelperTextColor(ColorStateList.valueOf(Color.parseColor("#505050")));
            return  false;
        }else if(password.length()<8){
            passwordLayout.setHelperText("*Password Should Contain 8 Characters");
            passwordLayout.setHelperTextColor(ColorStateList.valueOf(Color.parseColor("#505050")));
            return  false;
        }
        else if(PERFECT_MATCH.matcher(password).matches()){
            passwordLayout.setHelperText("Perfect Password");
            passwordLayout.setHelperTextColor(ColorStateList.valueOf(Color.parseColor("#23FF19")));
            return true;
        }
        return false;
    }
}