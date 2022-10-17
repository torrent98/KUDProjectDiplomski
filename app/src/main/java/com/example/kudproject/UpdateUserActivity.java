package com.example.kudproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class UpdateUserActivity extends AppCompatActivity {

    private EditText kudNameUpdate, kudDOEUpdate, kudAdressUpdate, kudPhoneUpdate;
    private ProgressBar loadingPB;
    private Button updateUserBtn;
    private String name, date, adress, phone;

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        kudNameUpdate = findViewById(R.id.name_update_edit);
        kudDOEUpdate = findViewById(R.id.doe_update_edit);
        kudAdressUpdate = findViewById(R.id.adress_update_edit);
        kudPhoneUpdate = findViewById(R.id.phone_update_edit);
        loadingPB = findViewById(R.id.idPBLoading);

        updateUserBtn = findViewById(R.id.update_profile_btn);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        showUserData(firebaseUser);

        kudDOEUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textSADoE[] = date.split("/");

                int dan = Integer.parseInt(textSADoE[0]);
                int mesec = Integer.parseInt(textSADoE[1]) - 1;
                int godina = Integer.parseInt(textSADoE[2]);

                DatePickerDialog picker;

                picker = new DatePickerDialog(UpdateUserActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        kudDOEUpdate.setText(dayOfMonth + "/" + (month + 1)  +"/" + year);
                    }
                }, godina,mesec,dan);
                picker.show();
            }
        });

        updateUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateProfile(firebaseUser);

            }
        });

    }

    private void showUserData(FirebaseUser firebaseUser) {

        String userID = firebaseUser.getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails!=null){
                    name = firebaseUser.getDisplayName();
                    date = readUserDetails.kudDate;
                    adress = readUserDetails.kudAdress;
                    phone = readUserDetails.kudPhone;

                    kudNameUpdate.setText(name);
                    kudDOEUpdate.setText(date);
                    kudAdressUpdate.setText(adress);
                    kudPhoneUpdate.setText(phone);

                }else{
                    Toast.makeText(UpdateUserActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }

                loadingPB.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateUserActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                loadingPB.setVisibility(View.GONE);
            }
        });

    }

    private void updateProfile(FirebaseUser firebaseUser) {

        if(TextUtils.isEmpty(name)){
            Toast.makeText(UpdateUserActivity.this, "Please enter KUD name..", Toast.LENGTH_SHORT).show();
            kudNameUpdate.setError("KUD name is required");
            kudNameUpdate.requestFocus();

        }  else if(TextUtils.isEmpty(date)){
            Toast.makeText(UpdateUserActivity.this, "Please enter establishment date..", Toast.LENGTH_SHORT).show();
            kudDOEUpdate.setError("KUD establishment date is required");
            kudDOEUpdate.requestFocus();

        } else if(TextUtils.isEmpty(adress)){
            Toast.makeText(UpdateUserActivity.this, "Please enter KUD adress..", Toast.LENGTH_SHORT).show();
            kudAdressUpdate.setError("KUD adress is required");
            kudAdressUpdate.requestFocus();

        } else if(TextUtils.isEmpty(phone)){
            Toast.makeText(UpdateUserActivity.this, "Please re-enter offical KUD phone number..", Toast.LENGTH_SHORT).show();
            kudPhoneUpdate.setError("KUD phone number should be 10 digits at max.");
            kudPhoneUpdate.requestFocus();

        } else if(phone.length() > 10){
            Toast.makeText(UpdateUserActivity.this, "Please enter offical KUD phone number..", Toast.LENGTH_SHORT).show();
            kudPhoneUpdate.setError("KUD phone number is required");
            kudPhoneUpdate.requestFocus();

        }else {

            name = kudNameUpdate.getText().toString();
            date = kudDOEUpdate.getText().toString();
            adress = kudAdressUpdate.getText().toString();
            phone = kudPhoneUpdate.getText().toString();

            name.trim();
            adress.trim();
            phone.trim();

            String nameUserFinal = name.substring(0,1).toUpperCase() + name.substring(1);

            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(date,adress,phone);

            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

            String userID = firebaseUser.getUid();

            loadingPB.setVisibility(View.VISIBLE);

            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(nameUserFinal).build();

                        firebaseUser.updateProfile(profileUpdates);

                        Toast.makeText(UpdateUserActivity.this, "Update successful.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(UpdateUserActivity.this, UserSettings.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }else {

                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(UpdateUserActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                    loadingPB.setVisibility(View.GONE);
                }
            });

        }

    }
}