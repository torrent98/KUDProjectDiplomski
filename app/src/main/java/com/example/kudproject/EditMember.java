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

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditMember extends AppCompatActivity {

    private RadioButton memberRankUpdate;
    private RadioGroup ranks;

    private ProgressBar loadingPB;
    Button updateBtn;

    private String imeZaUpdate, prezimeZaUpdate, adresaZaUpdate;
    private EditText memberNameUpdateEdit, memberLastNameUpdateEdit, memberAdressUpdateEdit;
    String rankPomocni;
    Member memberEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_member);

        memberEdit = getIntent().getParcelableExtra("memberEdit");

        //firebaseDatabase = FirebaseDatabase.getInstance();

        memberNameUpdateEdit = findViewById(R.id.member_name_update_edit);
        memberLastNameUpdateEdit = findViewById(R.id.member_lastName_update_edit);
        memberAdressUpdateEdit = findViewById(R.id.member_adress_update_edit);

        ranks = findViewById(R.id.rank_radio_buttons_update);
//        int rankRadioID = ranks.getCheckedRadioButtonId();
//        memberRankUpdate = findViewById(rankRadioID);

        loadingPB = findViewById(R.id.idPBLoading);
        updateBtn = findViewById(R.id.idBtnEditMember);

//        updateBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                MemberDB db = new MemberDB();
//
//                Member member_edit = new Member(memberNameUpdateEdit.getText().toString(), memberLastNameUpdateEdit.getText().toString(), memberAdressUpdateEdit.getText().toString(),memberRankUpdate.getText().toString());
//
//                HashMap<String, Object> hashMap = new HashMap<>();
//                hashMap.put("ime", memberNameUpdateEdit.getText().toString());
//                hashMap.put("prezime", memberLastNameUpdateEdit.getText().toString());
//                hashMap.put("adresa", memberAdressUpdateEdit.getText().toString());
//                hashMap.put("rank", memberRankUpdate.getText().toString());
//                db.update(member_edit.getKey(), hashMap).addOnSuccessListener(suc ->
//                {
//                    Toast.makeText(EditMember.this, "Member Updated..", Toast.LENGTH_SHORT).show();
//
////                    Toast.makeText(this, "Member is updated", Toast.LENGTH_SHORT).show();
//                    finish();
//                }).addOnFailureListener(er ->
//                {
//                    Toast.makeText(EditMember.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
//
//                });
//
//            }
//        });

        if (memberEdit != null) {
            // on below line we are setting data to our edit text from our modal class.
            memberNameUpdateEdit.setText(memberEdit.getIme());
            memberLastNameUpdateEdit.setText(memberEdit.getPrezime());
            memberAdressUpdateEdit.setText(memberEdit.getAdresa());

            if(memberEdit.getRank().equals("Amateur")){
                memberRankUpdate = findViewById(R.id.amateur_radio_update);
            } else if(memberEdit.getRank().equals("Junior")){
                memberRankUpdate = findViewById(R.id.junior_radio_update);
            } else{
                memberRankUpdate = findViewById(R.id.senior_radio_update);
            }

            memberRankUpdate.setChecked(true);

        }else{
            Toast.makeText(EditMember.this, "Something went wrong..", Toast.LENGTH_SHORT).show();
        }

        // on below line we are initialing our database reference and we are adding a child as our course id.
        //databaseReference = firebaseDatabase.getReference("Member").child(memberEdit.getKey());

        // on below line we are adding click listener for our add course button.
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // on below line we are making our progress bar as visible.
                loadingPB.setVisibility(View.VISIBLE);

                int rankRadioID = ranks.getCheckedRadioButtonId();
                memberRankUpdate = findViewById(rankRadioID);

                // on below line we are getting data from our edit text.
                String ime = memberNameUpdateEdit.getText().toString();
                String prezime = memberLastNameUpdateEdit.getText().toString();
                String adresa = memberAdressUpdateEdit.getText().toString();
                String rank = memberRankUpdate.getText().toString();

                updateData(memberEdit.getKey(), ime, prezime, adresa, rank);

                // on below line we are creating a map for
                // passing a data using key and value pair.
//                Map<String, Object> map = new HashMap<>();
//                map.put("ime", ime);
//                map.put("prezime", prezime);
//                map.put("adresa", adresa);
//                map.put("rank", rank);
//
//                // on below line we are calling a database reference on
//                // add value event listener and on data change method
//                databaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                        // making progress bar visibility as gone.
//                        loadingPB.setVisibility(View.GONE);
//
//                        // adding a map to our database.
//                        databaseReference.updateChildren(map);
//
//                        // on below line we are displaying a toast message.
//                        Toast.makeText(EditMember.this, "Member Updated..", Toast.LENGTH_SHORT).show();
//
//                        // opening a new activity after updating our coarse.
//                        startActivity(new Intent(EditMember.this, AllMembersActivity.class));
//                        finish();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                        // displaying a failure message on toast.
//                        Toast.makeText(EditMember.this, "Fail to update member..", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
            }
        });

    }

    private void updateData(String key, String ime, String prezime, String emailAdresa, String rang){

        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Member").child(key);
        Member editMember = new Member(key, ime, prezime, emailAdresa, rang);
        dataRef.setValue(editMember);

        Toast.makeText(EditMember.this, "Member Updated..", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(EditMember.this, AllMembersActivity.class));
        finish();

        loadingPB.setVisibility(View.GONE);

    }

    public void checkButton(View view) {

        int rankRadioID = ranks.getCheckedRadioButtonId();

        memberRankUpdate = findViewById(rankRadioID);
        Toast.makeText(this, "Selected Rank is:" + memberRankUpdate.getText(), Toast.LENGTH_SHORT).show();

    }
}