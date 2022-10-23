package com.example.kudproject.user;

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

import com.example.kudproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateEmailActivity extends AppCompatActivity {

    private EditText editEnterNewEmail, editEnterPwd;
    private Button btnUpdateEmail;
    private ProgressBar loadingPB;
    private String userOldEmail, userNewEmail, userPwd;
    private TextView oldEmail;

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        editEnterNewEmail = findViewById(R.id.update_email_new_enter);
        editEnterPwd = findViewById(R.id.update_email_enter_password);
        btnUpdateEmail = findViewById(R.id.update_user_email_btn);
        loadingPB = findViewById(R.id.idPBLoading);
        oldEmail = findViewById(R.id.update_email_old_text);

        btnUpdateEmail.setEnabled(false);
        editEnterNewEmail.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        userOldEmail = firebaseUser.getEmail();
        oldEmail.setText(userOldEmail);

        if(firebaseUser.equals("")){
            Toast.makeText(UpdateEmailActivity.this, "Something went wrong. User details not available.", Toast.LENGTH_SHORT).show();
        } else{
            reAuthenticateUser(firebaseUser);
        }

    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {

        Button btnAuthenticate = findViewById(R.id.authenticate_user_btn);

        btnAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userPwd = editEnterPwd.getText().toString();

                if (TextUtils.isEmpty(userPwd)) {
                    Toast.makeText(UpdateEmailActivity.this, "Password is needed to continue", Toast.LENGTH_SHORT).show();
                    editEnterPwd.setError("Please enter your password for authentication");
                    editEnterPwd.requestFocus();

                }else{

                    loadingPB.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail,userPwd);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                loadingPB.setVisibility(View.GONE);
                                Toast.makeText(UpdateEmailActivity.this, "Password authenticated. You can proceed.", Toast.LENGTH_SHORT).show();

                                editEnterNewEmail.setEnabled(true);
                                btnUpdateEmail.setEnabled(true);

                                editEnterPwd.setEnabled(false);
                                btnAuthenticate.setEnabled(false);

                                btnUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmailActivity.this,R.color.orange_nav));

                                btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        userNewEmail = editEnterNewEmail.getText().toString();

                                        if (TextUtils.isEmpty(userNewEmail)) {
                                            Toast.makeText(UpdateEmailActivity.this, "New e-mail is required", Toast.LENGTH_SHORT).show();
                                            editEnterNewEmail.setError("Please enter new e-mail");
                                            editEnterNewEmail.requestFocus();

                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
                                            Toast.makeText(UpdateEmailActivity.this, "Please valid e-mail", Toast.LENGTH_SHORT).show();
                                            editEnterNewEmail.setError("Valid e-mail is required");
                                            editEnterNewEmail.requestFocus();

                                        } else if (userOldEmail.matches(userNewEmail)) {
                                            Toast.makeText(UpdateEmailActivity.this, "New e-mail cant be same as old e-mail", Toast.LENGTH_SHORT).show();
                                            editEnterNewEmail.setError("Enter new e-mail");
                                            editEnterNewEmail.requestFocus();
                                        } else {
                                            loadingPB.setVisibility(View.VISIBLE);
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });

                            }else {

                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }

                        }
                    });

                }

            }
        });

    }

    private void updateEmail(FirebaseUser firebaseUser) {

        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    firebaseUser.sendEmailVerification();

                    Toast.makeText(UpdateEmailActivity.this, "E-mail is successfully updated. Please verify it.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateEmailActivity.this, UserSettings.class);
                    startActivity(intent);
                    finish();

                }else {
                    try {
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                loadingPB.setVisibility(View.GONE);
            }
        });

    }
}