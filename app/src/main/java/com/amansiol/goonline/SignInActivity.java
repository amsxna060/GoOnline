package com.amansiol.goonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    EditText login_email;
    EditText login_pass;
    FirebaseAuth mAuth;
    Button Login_btn;
    ProgressBar progressBar;
    TextView forget_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_sign_in);
        Login_btn=findViewById(R.id.login_btn);
        login_email=findViewById(R.id.login_email);
        login_pass=findViewById(R.id.login_pass);
        progressBar=findViewById(R.id.progressBar);
        forget_pass=findViewById(R.id.forgetpass);
        mAuth=FirebaseAuth.getInstance();
        Login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sEmail=login_email.getText().toString().trim();
                String sPass= login_pass.getText().toString().trim();
                if(!NetworkUtil.isConnected(getApplicationContext())){
                    startActivity(new Intent(getApplicationContext(),NoInternet.class));
                    finish();
                }else
                if(!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()){
                    login_email.setError("incorrect Email");
                    login_email.setFocusable(true);
                }else
                if(sPass.length()<6)
                {
                    login_pass.setError("Password must be more than 6 character");
                    login_pass.setFocusable(true);
                }else {
                    LoginUser(sEmail,sPass);
                }
            }
        });

        forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Patterns.EMAIL_ADDRESS.matcher(login_email.getText().toString()).matches()){
                    login_email.setError("Fill Email Address");
                    login_email.setFocusable(true);
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.sendPasswordResetEmail(login_email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignInActivity.this,"Email sent...",Toast.LENGTH_LONG).show();
//                                Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
//                                startActivity(intent);
                            }else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignInActivity.this,"Email sending Failed...",Toast.LENGTH_LONG).show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setAlpha(0);
                            Toast.makeText(SignInActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();

                        }
                    });
                }
            }
        });

    }
    private void LoginUser(String sEmail, String sPass) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(sEmail, sPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressBar.setVisibility(View.GONE);
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent=new Intent(SignInActivity.this,MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SignInActivity.this, ""+e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
     forget_pass.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             if(!Patterns.EMAIL_ADDRESS.matcher(login_email.getText().toString()).matches()){
                 login_email.setError("Fill Email Address");
                 login_email.setFocusable(true);
             }else {
                 progressBar.setAlpha(1);
                 mAuth.sendPasswordResetEmail(login_email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful()){
                             progressBar.setAlpha(0);
                             Toast.makeText(SignInActivity.this,"Email sent...",Toast.LENGTH_LONG).show();
                         }else {
                             progressBar.setAlpha(0);
                             Toast.makeText(SignInActivity.this,"Email sending Failed...",Toast.LENGTH_LONG).show();
                         }

                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         progressBar.setAlpha(0);
                         Toast.makeText(SignInActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();

                     }
                 });
             }
         }
     });

    }
    public void Goto_Signup(View view) {
        Intent intent=new Intent(this,SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}