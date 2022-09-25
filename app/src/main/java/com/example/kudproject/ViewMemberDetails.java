package com.example.kudproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ViewMemberDetails extends AppCompatActivity {

    String name, lastname, adress, rank;

    TextView punoIme, adresa, rangic;
    FloatingActionButton editMemberBtn;

    Member memberView;

    RadioButton memberRankUpdate;
    RadioGroup ranks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_member_details);

        memberView = getIntent().getParcelableExtra("member");

        punoIme = findViewById(R.id.imeIPrezime);
        adresa = findViewById(R.id.adresaIgraca);
        rangic = findViewById(R.id.rankIgraca);
        editMemberBtn = findViewById(R.id.editMemberButton);


        String ime_i_prezime = memberView.getIme() + " " + memberView.getPrezime();

        punoIme.setText(ime_i_prezime);
        adresa.setText(memberView.getAdresa());
        rangic.setText(memberView.getRank());

        editMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ViewMemberDetails.this, EditMember.class);

                intent.putExtra("memberEdit",memberView);

                startActivity(intent);

            }
        });

    }

}