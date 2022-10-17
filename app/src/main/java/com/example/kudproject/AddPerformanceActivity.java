package com.example.kudproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class AddPerformanceActivity extends AppCompatActivity implements GamesSelectorRecyclerViewInterface, MemberOnClickRecyclerViewInterface {

    private EditText titleEdit, locationEdit, dateEdit;
    private Button submitButton, btnReset, btnResetPlayers, filterGamesAndPlayers;

    private RadioButton playersRank;
    private RadioGroup ranks;

    private DatePickerDialog picker;

    private ProgressBar loadingPB;
    private int performanceID;

//    private PerformanceDB db;

    DatabaseReference performanceDBref;

    //GAMES

    private RecyclerView gamesRV;
    private ArrayList<Games> games = new ArrayList<>();
    private ArrayList<Games> gamesFIlter = new ArrayList<>();
    private GamesRVAdapter gamesRVAdapter;

//    private ArrayList<Integer> itemsKey = new ArrayList<>();
//    private boolean[] selectedGame;
//    private String[] gameTitles = {"Sopske igre", "Vranjske igre", "Vlaske igre", "Vojvodjanske igre", "Srpsko kolo"};
//
//    private TextView cmbGames;

    private Performance performanceMain = new Performance();

    //GAMES

    //----------PLAYERS-----------//

    DatabaseReference membersDBRef;

    private RecyclerView playersRV;
    private ArrayList<Member> players = new ArrayList<>();
    private MemberSelectorRVAdapter memberSelectorRVAdapter;

    //----------PLAYERS-----------//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_performance);

        titleEdit = findViewById(R.id.performance_title_edit);
        dateEdit = findViewById(R.id.performance_date_edit);
        locationEdit = findViewById(R.id.performance_location_edit);
        ranks = findViewById(R.id.perf_rank_radio_buttons);

        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int dan = calendar.get(Calendar.DAY_OF_MONTH);
                int mesec = calendar.get(Calendar.MONTH);
                int godina = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(AddPerformanceActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateEdit.setText(dayOfMonth + "/" + (month + 1)  +"/" + year);
                    }
                }, godina,mesec,dan);
                picker.show();
            }
        });

//        int rankRadioID = ranks.getCheckedRadioButtonId();
//        playersRank = findViewById(rankRadioID);

        loadingPB = findViewById(R.id.idPBLoading);

        performanceDBref = FirebaseDatabase.getInstance().getReference().child("Performance");
        membersDBRef = FirebaseDatabase.getInstance().getReference("Member");

        filterGamesAndPlayers = findViewById(R.id.filter_games_and_players);

        //----------------------------GAMES------------------------------//

        gamesRV = findViewById(R.id.rv_games);

        gamesRVAdapter = new GamesRVAdapter(games, this, this);
        gamesRV.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        gamesRV.setAdapter(gamesRVAdapter);

        //----------------------------GAMES------------------------------//

        //----------------------------PLAYERS-----------------------------//

        playersRV = findViewById(R.id.rv_players);

        memberSelectorRVAdapter = new MemberSelectorRVAdapter(players, this, this);
        playersRV.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        playersRV.setAdapter(memberSelectorRVAdapter);

        //----------------------------PLAYERS-----------------------------//

        filterGamesAndPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int rankRadioID = ranks.getCheckedRadioButtonId();
                playersRank = findViewById(rankRadioID);

                    String rankFilter = playersRank.getText().toString();

                    if(rankFilter.equals("Amateur")){

                        games.clear();

                        addAmateurGames();
                        getAmateurPlayers();

                        gamesRVAdapter.notifyDataSetChanged();
                        memberSelectorRVAdapter.notifyDataSetChanged();


                    } else if(rankFilter.equals("Junior")) {

                        games.clear();

                        addJuniorGames();
                        getJuniorPlayers();

                        gamesRVAdapter.notifyDataSetChanged();
                        memberSelectorRVAdapter.notifyDataSetChanged();

                    } else {

                        games.clear();

                        addSeniorGames();
                        getSeniorPlayers();

                        gamesRVAdapter.notifyDataSetChanged();
                        memberSelectorRVAdapter.notifyDataSetChanged();

                    }

                }
        });

        //addGames();

        btnReset = findViewById(R.id.reset_games);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String filterReset = playersRank.getText().toString();

                resetList(filterReset);

            }
        });


        btnResetPlayers = findViewById(R.id.reset_players);

        btnResetPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String filterReset = playersRank.getText().toString();
                resetPlayersList(filterReset);

            }
        });

        //----------------------------PLAYERS-----------------------------//

        submitButton = findViewById(R.id.submit_performance_btn);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadingPB.setVisibility(View.VISIBLE);
//
//                // getting data from our edit text.
//                String title = titleEdit.getText().toString();
//                String date = dateEdit.getText().toString();
//                String location = locationEdit.getText().toString();
//                String rank = playersRank.getText().toString();
//
////                courseID = courseName;
//
//                PerformanceDB db = new PerformanceDB();
//
//                // on below line we are passing all data to our modal class.
//                Performance performance = new Performance(title, date, location, rank);
//
//                // on below line we are calling a add value event
//                // to pass data to firebase database.
//
//                db.add(performance).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//
//                        // displaying a toast message.
//                        Toast.makeText(AddPerformanceActivity.this, "Performance Submited..", Toast.LENGTH_SHORT).show();
//
//                        // starting a main activity.
//                        startActivity(new Intent(AddPerformanceActivity.this, Performances.class));
//
//                        loadingPB.setVisibility(View.GONE);
//                    }
//
//                });

                String title = titleEdit.getText().toString();
                String date = dateEdit.getText().toString();
                String location = locationEdit.getText().toString();
                String rank = playersRank.getText().toString();

                title.trim();
                location.trim();

                String titleFinal = title.substring(0,1).toUpperCase() + title.substring(1);

                //----------------VALIDATION-----------------//


                //----------------VALIDATION-----------------//

                loadingPB.setVisibility(View.VISIBLE);


                insertPerformance(titleFinal, date, location, rank, games, players);

            }
        });
    }

    private void resetPlayersList(String filterReset) {

        players.clear();

        if(filterReset.equals("Amateur")){
            getAmateurPlayers();
        } else if(filterReset.equals("Junior")){
            getJuniorPlayers();
        } else {
            getSeniorPlayers();
        }

        //getPlayers();

        memberSelectorRVAdapter.notifyDataSetChanged();

        Toast.makeText(AddPerformanceActivity.this, "List reseted.", Toast.LENGTH_SHORT).show();

    }

    private void getAmateurPlayers() {

        membersDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                players.clear();

                for (DataSnapshot data : snapshot.getChildren())
                {

                    Member member = data.getValue(Member.class);
                    member.setKey(data.getKey());

                    if(member.getRank().equals("Amateur")){
                        players.add(member);
                        //key = data.getKey();
                    }

                }

                memberSelectorRVAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                memberSelectorRVAdapter.notifyDataSetChanged();
            }
        });

    }

    private void getJuniorPlayers() {

        membersDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                players.clear();



                for (DataSnapshot data : snapshot.getChildren())
                {

                    Member member = data.getValue(Member.class);

                    member.setKey(data.getKey());

                    if(member.getRank().equals("Junior")){
                        players.add(member);
                        //key = data.getKey();
                    }

                }
                memberSelectorRVAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                memberSelectorRVAdapter.notifyDataSetChanged();
            }
        });

    }

    private void getSeniorPlayers() {

        membersDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                players.clear();



                for (DataSnapshot data : snapshot.getChildren())
                {

                    Member member = data.getValue(Member.class);

                    member.setKey(data.getKey());

                    if(member.getRank().equals("Senior")){
                        players.add(member);
                        //key = data.getKey();
                    }

                }
                memberSelectorRVAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                memberSelectorRVAdapter.notifyDataSetChanged();
            }
        });

    }

    private void resetList(String filterReset) {

        games.clear();

        if(filterReset.equals("Amateur")){
            addAmateurGames();
        } else if(filterReset.equals("Junior")){
            addJuniorGames();
        } else {
            addSeniorGames();
        }

//        addGames();

        gamesRVAdapter.notifyDataSetChanged();

        Toast.makeText(AddPerformanceActivity.this, "List reseted.", Toast.LENGTH_SHORT).show();

    }

    private void addAmateurGames() {
//        games.add(new Games(0, "Vlaske igre", "Senior"));
//        games.add(new Games(1, "Meksicke igre", "Senior"));
//        games.add(new Games(2, "Sopske igre", "Junior"));
//        games.add(new Games(3, "Kosovske igre", "Junior"));
        games.add(new Games(4, "Srpsko kolo", "Amateur"));
    }

    private void addJuniorGames() {
//        games.add(new Games(0, "Vlaske igre", "Senior"));
//        games.add(new Games(1, "Meksicke igre", "Senior"));
        games.add(new Games(2, "Sopske igre", "Junior"));
        games.add(new Games(3, "Kosovske igre", "Junior"));
        //games.add(new Games(4, "Srpsko kolo", "Amateur"));
    }

    private void addSeniorGames() {
        games.add(new Games(0, "Vlaske igre", "Senior"));
        games.add(new Games(1, "Meksicke igre", "Senior"));
//        games.add(new Games(2, "Sopske igre", "Junior"));
//        games.add(new Games(3, "Kosovske igre", "Junior"));
        //games.add(new Games(4, "Srpsko kolo", "Amateur"));
    }

    private void insertPerformance(String title, String date, String location, String rank, ArrayList<Games> games, ArrayList<Member> players) {

        //db = new PerformanceDB();

        String key = performanceDBref.push().getKey();

        Performance performance = new Performance(key, title, date, location, rank, games, players);

        assert key != null;

        performanceDBref.child(key).setValue(performance);

//        performanceDBref.push().setValue(performance);

        Toast.makeText(AddPerformanceActivity.this, "Performance Added." + playersRank.getText(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(AddPerformanceActivity.this, Performances.class);
        startActivity(intent);

        loadingPB.setVisibility(View.GONE);

//        db.add(performance).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                Toast.makeText(AddPerformanceActivity.this, "Performance Added." + playersRank.getText(), Toast.LENGTH_SHORT).show();
//
//                Intent intent = new Intent(AddPerformanceActivity.this, Performances.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//
//                loadingPB.setVisibility(View.GONE);
//
//                finish();
//            }
//        });

    }

    public void checkButton(View v){
        int rankRadioID = ranks.getCheckedRadioButtonId();

        playersRank = findViewById(rankRadioID);
        Toast.makeText(this, "Selected Rank is:" + playersRank.getText(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onItemClick(int position) {

        games.remove(position);
        gamesRVAdapter.notifyItemRemoved(position);

    }

    @Override
    public void onMemberClick(int position) {
        players.remove(position);
        memberSelectorRVAdapter.notifyItemRemoved(position);
    }
}