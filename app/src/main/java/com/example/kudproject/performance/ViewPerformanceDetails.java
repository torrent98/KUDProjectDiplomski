package com.example.kudproject.performance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kudproject.R;
import com.example.kudproject.games.Games;
import com.example.kudproject.games.GamesRVAdapter;
import com.example.kudproject.games.GamesSelectorRecyclerViewInterface;
import com.example.kudproject.members.Member;
import com.example.kudproject.members.MemberOnClickRecyclerViewInterface;
import com.example.kudproject.members.MemberSelectorRVAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ViewPerformanceDetails extends AppCompatActivity implements GamesSelectorRecyclerViewInterface, MemberOnClickRecyclerViewInterface {

    public static final String EXTRA_DATA = "EXTRA_DATA";
    public static final String EXTRA_PLAYER = "EXTRA_PLAYER";

    String kljuc, naslov, lokacija, datumOD, rank;

    TextView titleShow, locationShow, dateShow, rankShow;
    FloatingActionButton editPerformanceButton;

    FloatingActionButton sendEmailBtn;

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

        //--------SEND_EMAIL--------//

        sendEmailBtn = findViewById(R.id.sendEmailButton);

        sendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naslov = titleShow.getText().toString();
                lokacija = locationShow.getText().toString();
                datumOD = dateShow.getText().toString();

                String[] adreseIgraca = new String[performanceView.getMemberList().size()];

                ArrayList<String> naziviIgara = new ArrayList<>();

                for(Member member:performanceView.getMemberList()){
                    players.add(member);
                }

                //IZMENE------------------------

                for(int i = 0; i < performanceView.getMemberList().size(); i++){
                    adreseIgraca[i] = performanceView.getMemberList().get(i).getAdresa();
                }

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, adreseIgraca);
                intent.putExtra(Intent.EXTRA_SUBJECT, naslov + " / Lokacija:" + lokacija);
                intent.putExtra(Intent.EXTRA_TEXT, "Datum:"+datumOD);

                if(intent.resolveActivity(getPackageManager()) != null){

                    startActivity(intent);

                }else{

                    Toast.makeText(ViewPerformanceDetails.this, "Something went wrong...", Toast.LENGTH_SHORT).show();

                }

                //IZMENE------------------------

//                int brojacZaIgre =0;
//
//                Intent intent = new Intent(Intent.ACTION_SEND);
//
//                if(!players.isEmpty()){
//
//                    for (Member member:players){
//
//                        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + member.getAdresa()));
//
////                        intent.putExtra(Intent.EXTRA_EMAIL, member.getAdresa() + " ");
//                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{member.getAdresa()});
//                        intent.putExtra(Intent.EXTRA_SUBJECT, naslov + " / Lokacija:" + lokacija);
//                        intent.putExtra(Intent.EXTRA_TEXT, "Datum:"+datumOD);
//
//                        if(brojacZaIgre == 0){
//
//                            for(Games games:performanceView.getGamesList()){
//                                naziviIgara.add(games.getGameName());
//
//                                intent.putExtra(Intent.EXTRA_TEXT, new String[]{games.getGameName() + "/"});
//
//                            }
////                            if(!naziviIgara.isEmpty()){
////                                //intent.putExtra(Intent.EXTRA_TEXT, igreSviNazivi);
////                            }else {
////                                Toast.makeText(ViewPerformanceDetails.this, "No Games. Something went wrong...", Toast.LENGTH_SHORT).show();
////                            }
//                            brojacZaIgre = 1;
//
//                        }
//
//                        intent.setType("message/rfc822");
//                        startActivity(Intent.createChooser(intent,"Choose Mail App"));
//
//                    }
//
////                    intent.setType("message/rfc822");
////                    startActivity(Intent.createChooser(intent,"Choose Mail App"));
//
//                    Toast.makeText(ViewPerformanceDetails.this, "E-mail sent successfully!", Toast.LENGTH_SHORT).show();
//
//                }else{
//                    Toast.makeText(ViewPerformanceDetails.this, "No Players. Something went wrong...", Toast.LENGTH_SHORT).show();
//                }

            }
        });

        //--------SEND_EMAIL--------//

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