package com.example.kudproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText editTextEmailToReset;
    private Button resetPwd;
    private ProgressBar loadingPB;
    private FirebaseAuth authProfile;
    private static final String TAG = "ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editTextEmailToReset = findViewById(R.id.edit_text_pwd_reset_email);
        resetPwd = findViewById(R.id.btn_reset_pwd);
        loadingPB = findViewById(R.id.idPBLoading);

        resetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmailToReset.toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your e-mail..", Toast.LENGTH_SHORT).show();
                    editTextEmailToReset.setError("E-mail is required");
                    editTextEmailToReset.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please re-enter your e-mail..", Toast.LENGTH_SHORT).show();
                    editTextEmailToReset.setError("E-mail must be valid!");
                    editTextEmailToReset.requestFocus();
                } else {
                    loadingPB.setVisibility(View.VISIBLE);
                    resetPassword(email);

                }
            }
        });

    }

    private void resetPassword(String email) {

        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this, "Please check your e-mail for password reset link", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                } else {

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e){
                        editTextEmailToReset.setError("User do not exist or is no longer valid. Register again.");
                        editTextEmailToReset.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
                loadingPB.setVisibility(View.GONE);
            }
        });

    }
}