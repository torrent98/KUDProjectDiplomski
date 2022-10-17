package com.example.kudproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        memberRank = findViewById(rankRadioID);

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

                String cutMemberName = memberName.substring(0,1).toUpperCase() + memberName.substring(1);
                String cutMemberLastName = memberLastName.substring(0,1).toUpperCase() + memberLastName.substring(1);

                //prvi nacin

                insertMember(cutMemberName, cutMemberLastName, memberAdress, rank);

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