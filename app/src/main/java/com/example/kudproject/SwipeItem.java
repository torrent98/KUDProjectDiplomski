package com.example.kudproject;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeItem extends ItemTouchHelper.SimpleCallback {

    GamesRVAdapter gamesRVAdapter;

    SwipeItem(GamesRVAdapter gamesRVAdapter){
        super(0,ItemTouchHelper.UP | ItemTouchHelper.DOWN);
        this.gamesRVAdapter = gamesRVAdapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        //this.gamesRVAdapter.deleteItem(position);
    }
}
