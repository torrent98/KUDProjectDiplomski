package com.example.kudproject.members;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.kudproject.R;
import com.example.kudproject.user.LoginActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddMemberActivity extends AppCompatActivity {

    private Button submitButton;
    private EditText memberNameEdit, memberLastNameEdit, memberAdressEdit;

    private RadioButton memberRank;
    private RadioGroup ranks;

    private ProgressBar loadingPB;
    private int memberID;

    DatabaseReference memberDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        memberNameEdit = findViewById(R.id.member_name);
        memberLastNameEdit = findViewById(R.id.member_surname);
        memberAdressEdit = findViewById(R.id.member_email);
        ranks = findViewById(R.id.rank_radio_buttons);

        int rankRadioID = ranks.getCheckedRadioButtonId();
        memberRank = findViewById(R.id.amateur_radio);

        memberRank.setChecked(true);

        loadingPB = findViewById(R.id.idPBLoading);

        memberDB = FirebaseDatabase.getInstance().getReference().child("Member");

        submitButton = findViewById(R.id.idBtnAddMember);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPB.setVisibility(View.VISIBLE);

                // getting data from our edit text.
                String memberName = memberNameEdit.getText().toString();
                String memberLastName = memberLastNameEdit.getText().toString();
                String memberAdress = memberAdressEdit.getText().toString();
                String rank = memberRank.getText().toString();

                memberName.trim();
                memberLastName.trim();
                memberAdress.trim();

                String cutMemberName;

                String cutMemberLastName;
                //String cutMemberLastName = memberLastName.substring(0,1).toUpperCase() + memberLastName.substring(1);

                //prvi nacin

                // on below line validating the text input.
                if (TextUtils.isEmpty(memberName)) {
                    Toast.makeText(AddMemberActivity.this, "Please enter member's name..", Toast.LENGTH_SHORT).show();
                    memberNameEdit.setError("Member name is required");
                    memberNameEdit.requestFocus();
                } else if (TextUtils.isEmpty(memberLastName)) {
                    Toast.makeText(AddMemberActivity.this, "Please enter member's last name..", Toast.LENGTH_SHORT).show();
                    memberLastNameEdit.setError("Member last name is required");
                    memberLastNameEdit.requestFocus();
                } else if (TextUtils.isEmpty(memberAdress)) {
                    Toast.makeText(AddMemberActivity.this, "Please enter member's last name..", Toast.LENGTH_SHORT).show();
                    memberAdressEdit.setError("Member e-mail adress is required");
                    memberAdressEdit.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(memberAdress).matches()) {
                    Toast.makeText(AddMemberActivity.this, "Please re-enter your e-mail..", Toast.LENGTH_SHORT).show();
                    memberAdressEdit.setError("E-mail must be valid!");
                    memberAdressEdit.requestFocus();
                } else if ( rank.equals(null) && TextUtils.isEmpty(rank)) {
                    Toast.makeText(AddMemberActivity.this, "Please choose member's rank..", Toast.LENGTH_SHORT).show();
                    memberRank.setError("Member rank is required");
                    memberRank.requestFocus();
                } else {

                    cutMemberName = memberName.substring(0,1).toUpperCase() + memberName.substring(1);
                    cutMemberLastName = memberLastName.substring(0,1).toUpperCase() + memberLastName.substring(1);

                    loadingPB.setVisibility(View.VISIBLE);
                    insertMember(cutMemberName, cutMemberLastName, memberAdress, rank);

                }

                //insertMember(cutMemberName, cutMemberLastName, memberAdress, rank);

//                courseID = courseName;

                //MemberDB db = new MemberDB();

                // on below line we are passing all data to our modal class.
                //Member member = new Member(memberName, memberLastName, memberAdress, rank);

                // on below line we are calling a add value event
                // to pass data to firebase database.

//                db.add(member).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//
//                        // displaying a toast message.
//                        Toast.makeText(AddMemberActivity.this, "Member Added..", Toast.LENGTH_SHORT).show();
//
//                        // starting a main activity.
//                        startActivity(new Intent(AddMemberActivity.this, AllMembersActivity.class));
//
//                        loadingPB.setVisibility(View.GONE);
//                    }
//
//                });

            }
        });

    }

    private void insertMember(String memberName, String memberLastName, String memberAdress, String rank) {

        String key = memberDB.push().getKey();

        Member member = new Member(key, memberName, memberLastName, memberAdress, rank);

        assert key != null;

        memberDB.child(key).setValue(member);

        Toast.makeText(AddMemberActivity.this, "Member Added..", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(AddMemberActivity.this, AllMembersActivity.class));

        loadingPB.setVisibility(View.GONE);

    }

    public void checkButton(View v){
        int rankRadioID = ranks.getCheckedRadioButtonId();

        memberRank = findViewById(rankRadioID);
        Toast.makeText(this, "Selected Rank is:" + memberRank.getText(), Toast.LENGTH_SHORT).show();
    }

}