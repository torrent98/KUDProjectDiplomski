package com.example.kudproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ViewPerformanceDetails extends AppCompatActivity implements GamesSelectorRecyclerViewInterface, MemberOnClickRecyclerViewInterface {

    public static final String EXTRA_DATA = "EXTRA_DATA";
    public static final String EXTRA_PLAYER = "EXTRA_PLAYER";

    String kljuc, naslov, lokacija, datumOD, rank;

    TextView titleShow, locationShow, dateShow, rankShow;
    FloatingActionButton editPerformanceButton;

    Performance performanceView;

    //------GAMES------//

    private RecyclerView gamesRV;
    private ArrayList<Games> games = new ArrayList<>();
    private GamesRVAdapter gamesRVAdapter;

    //------GAMES------//

    //------PLAYERS------//

    private RecyclerView playersRV;
    private ArrayList<Member> players = new ArrayList<>();
    private MemberSelectorRVAdapter memberSelectorRVAdapter;

    //------PLAYERS------//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_performance_details);

        performanceView = new Performance();

        performanceView.setKey(getIntent().getStringExtra("key"));
        performanceView.setNaslov(getIntent().getStringExtra("naslov"));
        performanceView.setDatumOdrzavanja(getIntent().getStringExtra("datumOdrzavanja"));
        performanceView.setLokacija(getIntent().getStringExtra("lokacija"));
        performanceView.setPotrebanRank(getIntent().getStringExtra("potrebanRank"));
        performanceView.setGamesList(getIntent().getParcelableArrayListExtra(EXTRA_DATA));
        performanceView.setMemberList(getIntent().getParcelableArrayListExtra(EXTRA_PLAYER));

        //performanceView = getIntent().getParcelableExtra("performance");

        titleShow = findViewById(R.id.title_perf);
        dateShow = findViewById(R.id.prikazi_datumOD);
        locationShow = findViewById(R.id.prikazi_lokaciju);
        rankShow = findViewById(R.id.rank_izabran);

        editPerformanceButton = findViewById(R.id.editPerformanceBtn);

        titleShow.setText(performanceView.getNaslov());
        dateShow.setText(performanceView.getDatumOdrzavanja());
        locationShow.setText(performanceView.getLokacija());
        rankShow.setText(performanceView.getPotrebanRank());

        //------GAMES------//

        gamesRV = findViewById(R.id.rv_games_view);

        gamesRVAdapter = new GamesRVAdapter(performanceView.getGamesList(), this, this);
        gamesRV.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));

        gamesRV.setAdapter(gamesRVAdapter);

        //------GAMES------//

        //------PLAYERS------//

        playersRV = findViewById(R.id.rv_players_view);

        memberSelectorRVAdapter = new MemberSelectorRVAdapter(performanceView.getMemberList(), this,this);
        playersRV.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));

        playersRV.setAdapter(memberSelectorRVAdapter);

        //------PLAYERS------//

        editPerformanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPerformanceDetails.this, EditPerformance.class);

                intent.putExtra("id",performanceView.getKey());
                intent.putExtra("title",performanceView.getNaslov());
                intent.putExtra("date",performanceView.getDatumOdrzavanja());
                intent.putExtra("location",performanceView.getLokacija());
                intent.putExtra("rang",performanceView.getPotrebanRank());
                intent.putParcelableArrayListExtra(EditPerformance.EDIT_GAME, performanceView.getGamesList());
                intent.putParcelableArrayListExtra(EditPerformance.EDIT_PLAYER, performanceView.getMemberList());

                startActivity(intent);
            }
        });

    }

    @Override
    public void onItemClick(int position) {

        Games viewGame = performanceView.getGamesList().get(position);

        Toast.makeText(this, "You clicked on: " + performanceView.getGamesList().get(position).getGameName() , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMemberClick(int position) {
        Toast.makeText(this, "List of Players...", Toast.LENGTH_SHORT).show();
    }
}