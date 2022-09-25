package com.example.kudproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserSettings extends AppCompatActivity {

    //Mapa
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private TextView welcomeText, profileKUDName, kudEmail, kudDate, kudAdress, kudPhone;
    private ProgressBar loadingPB;
    private String name, email, date, adress, phone;
    private ImageView profilePic;

    private Button updateUserData;
    private Button logout;
    private TextView changePwd;
    private TextView changeEmail;

    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setSelectedItemId(R.id.nav_settings);

        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.nav_members:
                        startActivity(new Intent(getApplicationContext(),AllMembersActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_map:

                        if(isServiceOK()){
                            init();
                        }
                        return true;

                    case R.id.nav_perf:
                        startActivity(new Intent(getApplicationContext(),Performances.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_settings:
                        return true;
                }

                return false;
            }

        });

        welcomeText = findViewById(R.id.textview_show_welcome);
        profileKUDName = findViewById(R.id.show_kud_name);
        kudEmail = findViewById(R.id.show_kud_email);
        kudDate = findViewById(R.id.show_kud_doe);
        kudAdress = findViewById(R.id.show_kud_adress);
        kudPhone = findViewById(R.id.show_kud_phone);
        loadingPB = findViewById(R.id.idPBLoading);

        updateUserData = findViewById(R.id.update_user_btn);
        changeEmail = findViewById(R.id.change_user_email_txt);
        changePwd = findViewById(R.id.change_user_password_btn);
        logout = findViewById(R.id.logout_btn);

        profilePic = findViewById(R.id.user_profile_pic);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSettings.this, UploadKUDProfilePic.class);
                startActivity(intent);
            }
        });

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        
        if(firebaseUser == null){
            Toast.makeText(UserSettings.this, "Something went wrong. User details are not avaliable at the moment.", Toast.LENGTH_LONG).show();
        } else {
            loadingPB.setVisibility(View.VISIBLE);
            showUserSettings(firebaseUser);
        }

        updateUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSettings.this, UpdateUserActivity.class);
                startActivity(intent);
            }
        });

        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSettings.this, UpdateEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });

        changePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSettings.this, UpdatePasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authProfile.signOut();
                Toast.makeText(UserSettings.this, "Logged Out.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(UserSettings.this, LoginActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                loadingPB.setVisibility(View.GONE);
                finish();
            }
        });

    }

    private void showUserSettings(FirebaseUser firebaseUser) {

        String userID = firebaseUser.getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails!=null){
                    name = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    date = readUserDetails.kudDate;
                    adress = readUserDetails.kudAdress;
                    phone = readUserDetails.kudPhone;

                    welcomeText.setText("Welcome " + name);
                    profileKUDName.setText(name);
                    kudEmail.setText(email);
                    kudDate.setText(date);
                    kudAdress.setText(adress);
                    kudPhone.setText(phone);

                    Uri uri = firebaseUser.getPhotoUrl();

                    Picasso.with(UserSettings.this).load(uri).into(profilePic);

                }else{
                    Toast.makeText(UserSettings.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }

                loadingPB.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserSettings.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                loadingPB.setVisibility(View.GONE);
            }
        });

    }

    private void init(){

        startActivity(new Intent(getApplicationContext(),Navigation.class));
        overridePendingTransition(0,0);

    }

    public boolean isServiceOK() {

        Log.d(TAG, "isServiceOK: checking google service version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(UserSettings.this);

        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isSerciveOK: Google Play Services is working");
            return true;
        } else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServiceOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(UserSettings.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}