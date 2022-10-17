package com.example.kudproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText userEdit, passwordEdit, confirmEdit, kudNameEdit, kudDateEdit, kudAdressEdit, kudPhoneEdit;
    private Button register;
    private TextView loginText;
    private ProgressBar loadingPB;

    private DatePickerDialog picker;
    private FirebaseAuth mAuth;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userEdit = findViewById(R.id.reg_user_name_edit);
        passwordEdit = findViewById(R.id.reg_password_edit);
        confirmEdit = findViewById(R.id.reg_confirm_password_edit);
        kudNameEdit = findViewById(R.id.reg_kud_name_edit);
        kudDateEdit = findViewById(R.id.reg_kud_date_edit);
        kudAdressEdit = findViewById(R.id.reg_kud_adress_edit);
        kudPhoneEdit = findViewById(R.id.reg_phone_number_edit);

        kudDateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int dan = calendar.get(Calendar.DAY_OF_MONTH);
                int mesec = calendar.get(Calendar.MONTH);
                int godina = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        kudDateEdit.setText(dayOfMonth + "/" + (month + 1)  +"/" + year);
                    }
                }, godina,mesec,dan);
                picker.show();
            }
        });

        register = findViewById(R.id.idBtnRegister);
        loadingPB = findViewById(R.id.idPBLoading);
        loginText = findViewById(R.id.login_text);

        mAuth = FirebaseAuth.getInstance();

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hiding our progress bar.
                loadingPB.setVisibility(View.VISIBLE);

                // getting data from our edit text.
                String userName = userEdit.getText().toString();
                String pwd = passwordEdit.getText().toString();
                String cnfPwd = confirmEdit.getText().toString();
                String kudName = kudNameEdit.getText().toString();
                String kudDate = kudDateEdit.getText().toString();
                String kudAdress = kudAdressEdit.getText().toString();
                String kudPhone = kudPhoneEdit.getText().toString();

                userName.trim();
                pwd.trim();
                kudName.trim();
                kudAdress.trim();
                kudPhone.trim();

                String cutKudName = kudName.substring(0,1).toUpperCase() + kudName.substring(1);

                //----------------VALIDATION-----------------//

                // checking if the password and confirm password is equal or not.
                if (!pwd.equals(cnfPwd)) {
                    Toast.makeText(RegisterActivity.this, "Please check both having same password..", Toast.LENGTH_SHORT).show();
                    confirmEdit.requestFocus();
                    passwordEdit.clearComposingText();
                    confirmEdit.clearComposingText();

                } else if (TextUtils.isEmpty(pwd) && TextUtils.isEmpty(cnfPwd)) {

                    // checking if the text fields are empty or not.
                    Toast.makeText(RegisterActivity.this, "Please enter your credentials..", Toast.LENGTH_SHORT).show();

                    passwordEdit.setError("Password is required");
                    passwordEdit.requestFocus();

                    confirmEdit.setError("It is required to confirm password");
                    confirmEdit.requestFocus();

                } else if (pwd.length() < 6) {

                    // checking if the text fields are empty or not.
                    Toast.makeText(RegisterActivity.this, "Password needs to be at least 6 digits", Toast.LENGTH_SHORT).show();
                    passwordEdit.setError("Password is week");
                    passwordEdit.requestFocus();

                    confirmEdit.setError("It is required to confirm password");
                    confirmEdit.requestFocus();

                } else if(TextUtils.isEmpty(userName)){
                    Toast.makeText(RegisterActivity.this, "Please enter your e-mail..", Toast.LENGTH_SHORT).show();
                    kudNameEdit.setError("E-mail is required");
                    kudNameEdit.requestFocus();

                } else if(!Patterns.EMAIL_ADDRESS.matcher(userName).matches()){
                    Toast.makeText(RegisterActivity.this, "Please re-enter your e-mail..", Toast.LENGTH_SHORT).show();
                    userEdit.setError("Valid e-mail is required");
                    userEdit.requestFocus();

                } else if(TextUtils.isEmpty(cutKudName)){
                    Toast.makeText(RegisterActivity.this, "Please enter KUD name..", Toast.LENGTH_SHORT).show();
                    kudNameEdit.setError("KUD name is required");
                    kudNameEdit.requestFocus();

                }  else if(TextUtils.isEmpty(kudDate)){
                    Toast.makeText(RegisterActivity.this, "Please enter establishment date..", Toast.LENGTH_SHORT).show();
                    kudDateEdit.setError("KUD establishment date is required");
                    kudDateEdit.requestFocus();

                } else if(TextUtils.isEmpty(kudAdress)){
                    Toast.makeText(RegisterActivity.this, "Please enter KUD adress..", Toast.LENGTH_SHORT).show();
                    kudAdressEdit.setError("KUD adress is required");
                    kudAdressEdit.requestFocus();

                } else if(TextUtils.isEmpty(kudPhone)){
                    Toast.makeText(RegisterActivity.this, "Please re-enter offical KUD phone number..", Toast.LENGTH_SHORT).show();
                    kudPhoneEdit.setError("KUD phone number should be 10 digits at max.");
                    kudPhoneEdit.requestFocus();

                } else if(kudPhone.length() > 10){
                    Toast.makeText(RegisterActivity.this, "Please enter offical KUD phone number..", Toast.LENGTH_SHORT).show();
                    kudPhoneEdit.setError("KUD phone number is required");
                    kudPhoneEdit.requestFocus();

                }else {

                    registerUser(cutKudName, kudDate, kudAdress, kudPhone, userName, pwd);

                }
            }
        });

    }

    private void registerUser(String kudName, String kudDate, String kudAdress, String kudPhone, String userName, String pwd) {

        //----------------DATABASE AUTHENTICATION-----------------//

        // on below line we are creating a new user by passing email and password.
        mAuth.createUserWithEmailAndPassword(userName, pwd).addOnCompleteListener(RegisterActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // on below line we are checking if the task is success or not.
                if (task.isSuccessful()) {

                    // in on success method we are hiding our progress bar and opening a login activity.

                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(kudName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    //Enter User Data to Database
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(kudDate, kudAdress, kudPhone);

                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                firebaseUser.sendEmailVerification();

                                Toast.makeText(RegisterActivity.this, "User Registered Successfully..", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                loadingPB.setVisibility(View.GONE);
                                finish();

                            }else{

                                Toast.makeText(RegisterActivity.this, "User Registered FAILED..", Toast.LENGTH_SHORT).show();

                            }
                            loadingPB.setVisibility(View.GONE);

                        }
                    });


                } else {

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){
                        passwordEdit.setError("Your password is to week. Use a mix of alphabet, numbers and other character signs");
                        passwordEdit.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        passwordEdit.setError("Your email is invalid. Please insert valid e-mail.");
                        userEdit.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        userEdit.setError("User is already registered with this e-mail. Use another e-mail.");
                        userEdit.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                    }

                    // in else condition we are displaying a failure toast message.
                    loadingPB.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Fail to register user..", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}