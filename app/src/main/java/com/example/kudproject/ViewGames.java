package com.example.kudproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class ViewGames extends AppCompatActivity implements GamesSelectorRecyclerViewInterface{

    private ArrayList<Games> games = new ArrayList<>();

    private RecyclerView gamesSelectorRV;
    private GamesSelectorRVAdapter gamesSelectorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_games);

        gamesSelectorRV = findViewById(R.id.rv_games_selector);

//        games.add(new Games(0, "Vlaske igre", "Senior"));
//        games.add(new Games(1, "Meksicke igre", "Senior"));
//        games.add(new Games(2, "Sopske igre", "Junior"));
//        games.add(new Games(3, "Kosovske igre", "Junior"));
//        games.add(new Games(4, "Srpsko kolo", "Amateur"));

        gamesSelectorAdapter = new GamesSelectorRVAdapter(games,this, this);

        gamesSelectorRV.setLayoutManager(new LinearLayoutManager(this));

        gamesSelectorRV.setAdapter(gamesSelectorAdapter);

        gamesSelectorAdapter.notifyDataSetChanged();

    }

    @Override
    public void onItemClick(int position) {

        Games game = games.get(position);

        Intent intent = new Intent(ViewGames.this, AddPerformanceActivity.class);

        intent.putExtra("game", game);

        startActivity(intent);

    }
}