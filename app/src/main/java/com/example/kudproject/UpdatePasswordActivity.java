package com.example.kudproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePasswordActivity extends AppCompatActivity {

    private EditText editEnterNewPwd, confirmEnterNewPwd, editEnterPwd;
    private Button btnUpdatePwd, btnAuthenticate;
    private ProgressBar loadingPB;
    private String userConfirmPwd, userNewPwd, userPwd;

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        editEnterNewPwd = findViewById(R.id.update_pwd_new_enter);
        editEnterPwd = findViewById(R.id.update_pwd_enter_password);
        confirmEnterNewPwd = findViewById(R.id.update_pwd_new_enter_confirm);
        btnUpdatePwd = findViewById(R.id.update_user_pwd_btn);
        btnAuthenticate = findViewById(R.id.authenticate_user_btn);
        loadingPB = findViewById(R.id.idPBLoading);

        btnUpdatePwd.setEnabled(false);
        editEnterNewPwd.setEnabled(false);
        confirmEnterNewPwd.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();


        if(firebaseUser.equals("")){
            Toast.makeText(UpdatePasswordActivity.this, "Something went wrong. User details not available.", Toast.LENGTH_SHORT).show();
        } else{
            reAuthenticateUser(firebaseUser);
        }

    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {

        btnAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userPwd = editEnterPwd.getText().toString();

                if (TextUtils.isEmpty(userPwd)) {
                    Toast.makeText(UpdatePasswordActivity.this, "Password is needed to continue", Toast.LENGTH_SHORT).show();
                    editEnterPwd.setError("Please enter your current password for authentication");
                    editEnterPwd.requestFocus();

                }else{

                    loadingPB.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),userPwd);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                loadingPB.setVisibility(View.GONE);
                                Toast.makeText(UpdatePasswordActivity.this, "Password authenticated. You can proceed.", Toast.LENGTH_SHORT).show();

                                editEnterNewPwd.setEnabled(true);
                                btnUpdatePwd.setEnabled(true);
                                confirmEnterNewPwd.setEnabled(true);

                                editEnterPwd.setEnabled(false);
                                btnAuthenticate.setEnabled(false);

                                btnUpdatePwd.setBackgroundTintList(ContextCompat.getColorStateList(UpdatePasswordActivity.this,R.color.orange_nav));

                                btnUpdatePwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changePassword(firebaseUser);
                                    }
                                });

                            }else {

                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(UpdatePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                            loadingPB.setVisibility(View.GONE);

                        }
                    });

                }

            }
        });

    }

    private void changePassword(FirebaseUser firebaseUser) {

        userConfirmPwd = confirmEnterNewPwd.getText().toString();
        userNewPwd = editEnterNewPwd.getText().toString();

        if (TextUtils.isEmpty(userNewPwd)) {
            Toast.makeText(UpdatePasswordActivity.this, "New password is required", Toast.LENGTH_SHORT).show();
            editEnterNewPwd.setError("Please enter new password");
            editEnterNewPwd.requestFocus();

        } else if (TextUtils.isEmpty(userConfirmPwd)) {
            Toast.makeText(UpdatePasswordActivity.this, "Please confirm your new password", Toast.LENGTH_SHORT).show();
            confirmEnterNewPwd.setError("Confirm new password");
            confirmEnterNewPwd.requestFocus();

        } else if (!userNewPwd.matches(userConfirmPwd)) {
            Toast.makeText(UpdatePasswordActivity.this, "Passwords must match", Toast.LENGTH_SHORT).show();
            confirmEnterNewPwd.setError("Please check your password confirmation");
            confirmEnterNewPwd.requestFocus();

        } else if (!userConfirmPwd.matches(userNewPwd)) {
            Toast.makeText(UpdatePasswordActivity.this, "Passwords must match", Toast.LENGTH_SHORT).show();
            editEnterNewPwd.setError("Please check your entered password");
            editEnterNewPwd.requestFocus();

        } else {
            loadingPB.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userNewPwd).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){

                        Toast.makeText(UpdatePasswordActivity.this, "Password is successfully updated.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdatePasswordActivity.this, UserSettings.class);
                        startActivity(intent);
                        finish();

                    }else {
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(UpdatePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    loadingPB.setVisibility(View.GONE);
                }
            });

        }

    }
}