package com.amansiol.goonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class ChangePassword extends AppCompatActivity {

    TextInputLayout currentPass,newPass,confirmPass;
    Button changeBtn;
    String currentpassStr;
    String temppassStr="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat
                .getColor(getApplicationContext(), R.color.colorPrimary));
        setContentView(R.layout.activity_change_password);
        currentPass=findViewById(R.id.current_password);
        newPass=findViewById(R.id.new_password);
        confirmPass=findViewById(R.id.confirm_password);
        changeBtn=findViewById(R.id.change_password);

        newPass.getEditText().addTextChangedListener(new TextWatcher() {
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



        confirmPass.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(confirmPass.getEditText().getText().toString().equals(newPass.getEditText().getText().toString()))
                {
                    confirmPass.setHelperText("Password Match");
                }
                else {
                    confirmPass.setHelperText("Mismatched");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (!verifyPassword(newPass.getEditText().getText().toString()))
                {
                    newPass.setError("Make Strong Password");
                    newPass.requestFocus();
                }
                else if (!confirmPass.getEditText().getText().toString().equals(newPass.getEditText().getText().toString()))
                {
                    confirmPass.setError("Password Mismatch");
                    confirmPass.requestFocus();
                }
                else
                {
                    String newpassword = newPass.getEditText().getText().toString();
                    FirebaseAuth mAuth=FirebaseAuth.getInstance();
                    final FirebaseUser user = mAuth.getCurrentUser();
                    user.updatePassword(newpassword)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ChangePassword.this, "Password has been updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ChangePassword.this,SettingActivity.class));
                                finish();
                            }
                        })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(ChangePassword.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    });

                }
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
            newPass.setHelperText("*Field is Mandontary");
            newPass.setHelperTextColor(ColorStateList.valueOf(Color.parseColor("#505050")));
            return  false;
        }else if(!UPPER_CASE_MISSING.matcher(password).matches()){
            newPass.setHelperText("*Password Should Contain One Upper case Character");
            newPass.setHelperTextColor(ColorStateList.valueOf(Color.parseColor("#505050")));
            return  false;
        }else if(!NUMBER_MISSING.matcher(password).matches()){
            newPass.setHelperText("*Password Should Contain One  Numberical Digit");
            newPass.setHelperTextColor(ColorStateList.valueOf(Color.parseColor("#505050")));
            return  false;
        }else if(!LOWERCASE_MISSING.matcher(password).matches()){
            newPass.setHelperText("*Password Should Contain One Lower Case Character");
            newPass.setHelperTextColor(ColorStateList.valueOf(Color.parseColor("#505050")));
            return  false;
        }else if(!SYMBOL_MISSING.matcher(password).matches()){
            newPass.setHelperText("*Password Should Contain One Symbol (@#$%^&+=)");
            newPass.setHelperTextColor(ColorStateList.valueOf(Color.parseColor("#505050")));
            return  false;
        }else if(password.length()<8){
            newPass.setHelperText("*Password Should Contain 8 Characters");
            newPass.setHelperTextColor(ColorStateList.valueOf(Color.parseColor("#505050")));
            return  false;
        }
        else if(PERFECT_MATCH.matcher(password).matches()){
            newPass.setHelperText("Perfect Password");
            newPass.setHelperTextColor(ColorStateList.valueOf(Color.parseColor("#23FF19")));
            return true;
        }
        return false;
    }
}