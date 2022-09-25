package com.example.kudproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText userEdit, passwordEdit;
    private Button loginBtn;
    private TextView reg_text,forgotPwd;

    private FirebaseAuth mAuth;

    private ProgressBar loadingPB;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initializing all our variables.
        userEdit = findViewById(R.id.log_user_name_edit);
        passwordEdit = findViewById(R.id.log_password_edit);
        loginBtn = findViewById(R.id.idBtnLogin);
        reg_text = findViewById(R.id.register_text);

        mAuth = FirebaseAuth.getInstance();

        loadingPB = findViewById(R.id.idPBLoading);

        forgotPwd = findViewById(R.id.forgot_pwd);
        forgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "You can reset your password now!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            }
        });


        ImageView imageShowHidePwd = findViewById(R.id.show_hide_password);
        imageShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordEdit.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    passwordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                } else {
                    passwordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        // adding click listener for our new user tv.
        reg_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line opening a login activity.
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        // adding on click listener for our login button.
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // hiding our progress bar.


                // getting data from our edit text on below line.
                String email = userEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                // on below line validating the text input.
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Please enter your e-mail..", Toast.LENGTH_SHORT).show();
                    userEdit.setError("E-mail is required");
                    userEdit.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(LoginActivity.this, "Please re-enter your e-mail..", Toast.LENGTH_SHORT).show();
                    userEdit.setError("E-mail must be valid!");
                    userEdit.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Please enter your password..", Toast.LENGTH_SHORT).show();
                    passwordEdit.setError("Password is required");
                    passwordEdit.requestFocus();
                } else{

                    loadingPB.setVisibility(View.VISIBLE);
                    loginUser(email, password);

                }

            }
        });

    }

    private void loginUser(String email, String password) {
        Toast.makeText(LoginActivity.this, "Login in progress..", Toast.LENGTH_SHORT).show();
        // on below line we are calling a sign in method and passing email and password to it.
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // on below line we are checking if the task is success or not.
                if (task.isSuccessful()) {
                    // on below line we are hiding our progress bar.
                    loadingPB.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Login Successful..", Toast.LENGTH_SHORT).show();
                    // on below line we are opening our mainactivity.
                    Intent i = new Intent(LoginActivity.this, AllMembersActivity.class);
                    startActivity(i);
                    finish();
                } else {

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e){
                        userEdit.setError("User do not exist or is no longer valid. Register again.");
                        userEdit.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        userEdit.setError("Invalid credentials!");
                        userEdit.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    // hiding our progress bar and displaying a toast message.
                    loadingPB.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Please enter valid user credentials..", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // in on start method checking if
        // the user is already sign in.
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // if the user is not null then we are
            // opening a main activity on below line.
            Toast.makeText(LoginActivity.this, "Already logged in..", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(LoginActivity.this, AllMembersActivity.class);
            startActivity(i);
            this.finish();
        }
    }

}