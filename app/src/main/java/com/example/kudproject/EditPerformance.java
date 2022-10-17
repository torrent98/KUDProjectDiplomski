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

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EditPerformance extends AppCompatActivity implements GamesSelectorRecyclerViewInterface, MemberOnClickRecyclerViewInterface {

    public static final String EDIT_GAME = "EDIT_GAME";
    public static final String EDIT_PLAYER = "EDIT_PLAYER";

    String rankPomocni;

    private EditText titleUpdateEdit, dODUpdateEdit, locationUpdateEdit;
    private String title, date, location, rank;
    private Button updateBtn, resetGamesBtn, resetPlayersBtn, filterGamesAndPlayersEditBtn;
    private String datePomocni;

    private RadioButton playersRankUpdate;
    private RadioGroup ranksUpdate;

    private DatePickerDialog picker;

    private ProgressBar loadingPB;
    private int performanceID;

    DatabaseReference performanceDBref;
    DatabaseReference membersDBRef;

    private Performance performanceUpdate = new Performance();

    //------GAMES------//

    private RecyclerView gamesEditRV;
    private ArrayList<Games> gamesEdit = new ArrayList<>();
    private GamesRVAdapter gamesEditRVAdapter;

    //----------PLAYERS-----------//

    private RecyclerView playersEditRV;
    private ArrayList<Member> playersEdit = new ArrayList<>();
    private MemberSelectorRVAdapter memberEditSelectorRVAdapter;

    //----------PLAYERS-----------//

//    private ArrayList<Integer> itemsKey = new ArrayList<>();
//    private boolean[] selectedGame;
//    private String[] gameTitles = {"Sopske igre", "Vranjske igre", "Vlaske igre", "Vojvodjanske igre", "Srpsko kolo"};
//    private String[] gameTitlesPom = new String[5];
//
//    private TextView cmbGamesUpdate;

    //------GAMES------//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_performance);

        performanceUpdate.setKey(getIntent().getStringExtra("id"));
        performanceUpdate.setNaslov(getIntent().getStringExtra("title"));
        performanceUpdate.setDatumOdrzavanja(getIntent().getStringExtra("date"));
        performanceUpdate.setLokacija(getIntent().getStringExtra("location"));
        performanceUpdate.setPotrebanRank(getIntent().getStringExtra("rang"));

        gamesEdit = getIntent().getParcelableArrayListExtra(EDIT_GAME);
        playersEdit = getIntent().getParcelableArrayListExtra(EDIT_PLAYER);

//        performanceUpdate.setGamesList(getIntent().getParcelableArrayListExtra(EDIT_GAME));
//        performanceUpdate.setMemberList(getIntent().getParcelableArrayListExtra(EDIT_PLAYER));

        performanceUpdate.setGamesList(gamesEdit);
        performanceUpdate.setMemberList(playersEdit);

        //performanceUpdate = getIntent().getParcelableExtra("performanceEdit");

        titleUpdateEdit = findViewById(R.id.performance_title_edit_update);
        dODUpdateEdit = findViewById(R.id.performance_date_edit_update);
        locationUpdateEdit = findViewById(R.id.performance_location_edit_update);

//        gamesRV = findViewById(R.id.rv_games_edit);
//        playersRV = findViewById(R.id.rv_players_edit);

        resetGamesBtn = findViewById(R.id.reset_games_edit);
        resetPlayersBtn = findViewById(R.id.reset_players_edit);

        ranksUpdate = findViewById(R.id.perf_rank_radio_buttons_update);
//        int rankRadioID = ranksUpdate.getCheckedRadioButtonId();
//        playersRankUpdate = findViewById(rankRadioID);

        //cmbGamesUpdate = findViewById(R.id.cmb_games_update);

        loadingPB = findViewById(R.id.idPBLoading);
        updateBtn = findViewById(R.id.update_performance_btn);

        if (performanceUpdate.getMemberList() != null) {
            // on below line we are setting data to our edit text from our modal class.
            titleUpdateEdit.setText(performanceUpdate.getNaslov());
            dODUpdateEdit.setText(performanceUpdate.getDatumOdrzavanja());
            locationUpdateEdit.setText(performanceUpdate.getLokacija());

            if(performanceUpdate.getPotrebanRank().equals("Amateur")){
                playersRankUpdate = findViewById(R.id.amateur_perf_radio_update);
            } else if(performanceUpdate.getPotrebanRank().equals("Junior")){
                playersRankUpdate = findViewById(R.id.junior_perf_radio_update);
            } else{
                playersRankUpdate = findViewById(R.id.senior_perf_radio_update);
            }

            playersRankUpdate.setChecked(true);

            //-----GAMES-----//

            gamesEditRV = findViewById(R.id.rv_games_edit);

            gamesEditRVAdapter = new GamesRVAdapter(performanceUpdate.getGamesList(), this, this);
            gamesEditRV.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

            gamesEditRV.setAdapter(gamesEditRVAdapter);

            //-----PLAYERS-----//

            playersEditRV = findViewById(R.id.rv_players_edit);

            memberEditSelectorRVAdapter = new MemberSelectorRVAdapter(performanceUpdate.getMemberList(), this,this);
            playersEditRV.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

            playersEditRV.setAdapter(memberEditSelectorRVAdapter);

            //------GAMES------//

//            for(int i = 0; i < performanceUpdate.getGamesList().size(); i++){
//                games.add(performanceUpdate.getGamesList().get(i));
//            }
//
//            for(int l = 0; l <games.size(); l++){
//
//                String pomocni = games.get(l).getGameName();
//                gameTitlesPom[l]=pomocni;
//
//            }
//
//            for(int j = 0; j<games.size(); j++){
//                itemsKey.add(games.get(j).getGameID());
//            }

//            selectedGame = new boolean[games.size()];
//
//            for(int i = 0; i < games.size(); i++){
//
//                if(games.get(i).isSelected){
//                    selectedGame[i] = true;
//                    String pomocni = games.get(i).getGameName();
//                    gameTitlesPom[i]=pomocni;
//                    itemsKey.add(games.get(i).getGameID());
//
//                }
//
//            }
//
//            StringBuilder stringBuilder = new StringBuilder();
//
//            for(int j = 0; j < itemsKey.size(); j++){
//                stringBuilder.append(gameTitlesPom[itemsKey.get(j)]);
//
//                games.get(j).setGameID(itemsKey.get(j));
//                games.get(j).setGameName(gameTitlesPom[itemsKey.get(j)]);
//                games.get(j).setSelected(true);
//
//                if(j != itemsKey.size() - 1){
//                    stringBuilder.append(", ");
//                }
//
//            }
//
//            cmbGamesUpdate.setText(stringBuilder.toString());

            //------GAMES------//

        }else{
            Toast.makeText(EditPerformance.this, "Something went wrong..", Toast.LENGTH_SHORT).show();
        }

        filterGamesAndPlayersEditBtn = findViewById(R.id.filter_games_and_players_edit);

        filterGamesAndPlayersEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int rankRadioID = ranksUpdate.getCheckedRadioButtonId();
                playersRankUpdate = findViewById(rankRadioID);

                String rankFilterEdit = playersRankUpdate.getText().toString();

                if(rankFilterEdit.equals("Amateur")){

                    gamesEdit.clear();
                    playersEdit.clear();

                    addAmateurGames();
                    getAmateurPlayers();

                    gamesEditRVAdapter.notifyDataSetChanged();
                    memberEditSelectorRVAdapter.notifyDataSetChanged();


                } else if(rankFilterEdit.equals("Junior")) {

                    gamesEdit.clear();
                    playersEdit.clear();

                    addJuniorGames();
                    getJuniorPlayers();

                    gamesEditRVAdapter.notifyDataSetChanged();
                    memberEditSelectorRVAdapter.notifyDataSetChanged();

                } else {

                    gamesEdit.clear();
                    playersEdit.clear();

                    addSeniorGames();
                    getSeniorPlayers();

                    gamesEditRVAdapter.notifyDataSetChanged();
                    memberEditSelectorRVAdapter.notifyDataSetChanged();

                }

            }
        });

        resetGamesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rank = playersRankUpdate.getText().toString();

                resetList(rank);

            }
        });

        resetPlayersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rank = playersRankUpdate.getText().toString();

                resetPlayersList(rank);

            }
        });

        datePomocni = dODUpdateEdit.getText().toString();

        dODUpdateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textSADoE[] = datePomocni.split("/");

                int dan = Integer.parseInt(textSADoE[0]);
                int mesec = Integer.parseInt(textSADoE[1]) - 1;
                int godina = Integer.parseInt(textSADoE[2]);

                DatePickerDialog picker;

                picker = new DatePickerDialog(EditPerformance.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dODUpdateEdit.setText(dayOfMonth + "/" + (month + 1)  +"/" + year);
                    }
                }, godina,mesec,dan);
                picker.show();
            }
        });

//        cmbGamesUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(EditPerformance.this);
//                builder.setTitle("Choose Game");
//                builder.setCancelable(false);
//
//                builder.setMultiChoiceItems(gameTitles, selectedGame, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                        if (isChecked) {
//                            itemsKey.add(which);
//                        } else {
//                            itemsKey.remove(which);
//                        }
//                    }
//                });
//
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        StringBuilder stringBuilder = new StringBuilder();
//
//                        games.clear();
//
//                        for(int j = 0; j < itemsKey.size(); j++){
//                            stringBuilder.append(gameTitles[itemsKey.get(j)]);
//
//                            games.get(j).setGameID(j);
//                            games.get(j).setGameName(gameTitles[itemsKey.get(j)]);
//                            games.get(j).setSelected(true);
//
//                            if(j != itemsKey.size() - 1){
//                                stringBuilder.append(", ");
//                            }
//
//                        }
//
//                        cmbGamesUpdate.setText(stringBuilder.toString());
//
//                    }
//                });
//
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//
//                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        for(int j=0; j < selectedGame.length; j++){
//                            selectedGame[j] = false;
//
//                            itemsKey.clear();
//
//                            cmbGamesUpdate.setText("");
//
//                        }
//
//                    }
//                });
//
//                builder.show();
//
//            }
//        });

        // on below line we are initialing our database reference and we are adding a child as our course id.
        performanceDBref = FirebaseDatabase.getInstance().getReference("Performance").child(performanceUpdate.getKey());
        membersDBRef = FirebaseDatabase.getInstance().getReference("Member");

        // on below line we are adding click listener for our add course button.
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // on below line we are making our progress bar as visible.
                loadingPB.setVisibility(View.VISIBLE);

//                int rankRadioID = ranksUpdate.getCheckedRadioButtonId();
//                playersRankUpdate = findViewById(rankRadioID);

                // on below line we are getting data from our edit text.
                title = titleUpdateEdit.getText().toString();
                date = dODUpdateEdit.getText().toString();
                location = locationUpdateEdit.getText().toString();
                rank = playersRankUpdate.getText().toString();

                title.trim();
                location.trim();

                String titleFinal = title.substring(0,1).toUpperCase() + title.substring(1);

                updateData(performanceUpdate.getKey(), titleFinal, date, location, rank, performanceUpdate.getGamesList(), performanceUpdate.getMemberList());

                // on below line we are creating a map for
                // passing a data using key and value pair.
//                Map<String, Object> map = new HashMap<>();
//                map.put("title", title);
//                map.put("date", date);
//                map.put("location", location);
//                map.put("rank", rank);

                // on below line we are calling a database reference on
                // add value event listener and on data change method
//                performanceDBref.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                        // making progress bar visibility as gone.
//                        loadingPB.setVisibility(View.GONE);
//
//
//                        // adding a map to our database.
//                        performanceDBref.updateChildren(map);
//
//                        // on below line we are displaying a toast message.
//                        Toast.makeText(EditPerformance.this, "Performance Updated..", Toast.LENGTH_SHORT).show();
//
//                        // opening a new activity after updating our coarse.
//                        startActivity(new Intent(EditPerformance.this, Performances.class));
//                        finish();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                        // displaying a failure message on toast.
//                        Toast.makeText(EditPerformance.this, "Fail to update performance..", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
            }
        });
    }

    private void resetPlayersList(String rank) {

        playersEdit.clear();

        if(rank.equals("Amateur")){
            getAmateurPlayers();
        } else if(rank.equals("Junior")){
            getJuniorPlayers();
        } else {
            getSeniorPlayers();
        }

        //getPlayers();

        memberEditSelectorRVAdapter.notifyDataSetChanged();

        Toast.makeText(EditPerformance.this, "List reseted.", Toast.LENGTH_SHORT).show();

    }

    private void resetList(String rank) {

        gamesEdit.clear();

        if(rank.equals("Amateur")){
            addAmateurGames();
        } else if(rank.equals("Junior")){
            addJuniorGames();
        } else {
            addSeniorGames();
        }

        //addGames();

        gamesEditRVAdapter.notifyDataSetChanged();

        Toast.makeText(EditPerformance.this, "List reseted.", Toast.LENGTH_SHORT).show();

    }

    private void addAmateurGames() {
//        games.add(new Games(0, "Vlaske igre", "Senior"));
//        games.add(new Games(1, "Meksicke igre", "Senior"));
//        games.add(new Games(2, "Sopske igre", "Junior"));
//        games.add(new Games(3, "Kosovske igre", "Junior"));
        gamesEdit.add(new Games(4, "Srpsko kolo", "Amateur"));
    }

    private void addJuniorGames() {
//        games.add(new Games(0, "Vlaske igre", "Senior"));
//        games.add(new Games(1, "Meksicke igre", "Senior"));
        gamesEdit.add(new Games(2, "Sopske igre", "Junior"));
        gamesEdit.add(new Games(3, "Kosovske igre", "Junior"));
        //games.add(new Games(4, "Srpsko kolo", "Amateur"));
    }

    private void addSeniorGames() {
        gamesEdit.add(new Games(0, "Vlaske igre", "Senior"));
        gamesEdit.add(new Games(1, "Meksicke igre", "Senior"));
//        games.add(new Games(2, "Sopske igre", "Junior"));
//        games.add(new Games(3, "Kosovske igre", "Junior"));
        //games.add(new Games(4, "Srpsko kolo", "Amateur"));
    }

    private void getAmateurPlayers() {

        membersDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                playersEdit.clear();

                for (DataSnapshot data : snapshot.getChildren())
                {

                    Member member = data.getValue(Member.class);
                    member.setKey(data.getKey());

                    if(member.getRank().equals("Amateur")){
                        playersEdit.add(member);
                        //key = data.getKey();
                    }

                }

                memberEditSelectorRVAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                memberEditSelectorRVAdapter.notifyDataSetChanged();
            }
        });

    }

    private void getJuniorPlayers() {

        membersDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                playersEdit.clear();



                for (DataSnapshot data : snapshot.getChildren())
                {

                    Member member = data.getValue(Member.class);

                    member.setKey(data.getKey());

                    if(member.getRank().equals("Junior")){
                        playersEdit.add(member);
                        //key = data.getKey();
                    }

                }
                memberEditSelectorRVAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                memberEditSelectorRVAdapter.notifyDataSetChanged();
            }
        });

    }

    private void getSeniorPlayers() {

        membersDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                playersEdit.clear();



                for (DataSnapshot data : snapshot.getChildren())
                {

                    Member member = data.getValue(Member.class);

                    member.setKey(data.getKey());

                    if(member.getRank().equals("Senior")){
                        playersEdit.add(member);
                        //key = data.getKey();
                    }

                }
                memberEditSelectorRVAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                memberEditSelectorRVAdapter.notifyDataSetChanged();
            }
        });

    }

    private void updateData(String key, String title, String date, String location, String rank, ArrayList<Games> games, ArrayList<Member> players) {

        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Performance").child(key);
        Performance editPerformance = new Performance(key, title, date, location, rank, games, players);
        dataRef.setValue(editPerformance);

        Toast.makeText(EditPerformance.this, "Performance Updated..", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(EditPerformance.this, Performances.class));
        finish();

        loadingPB.setVisibility(View.GONE);

    }

    public void checkButton(View view) {

        int rankRadioID = ranksUpdate.getCheckedRadioButtonId();

        playersRankUpdate = findViewById(rankRadioID);
        Toast.makeText(this, "Selected Rank is:" + playersRankUpdate.getText(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onItemClick(int position) {
        gamesEdit.remove(position);
        gamesEditRVAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onMemberClick(int position) {
        playersEdit.remove(position);
        memberEditSelectorRVAdapter.notifyItemRemoved(position);
    }

}