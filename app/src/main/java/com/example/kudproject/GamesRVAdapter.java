package com.example.kudproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GamesRVAdapter extends RecyclerView.Adapter<GamesRVAdapter.ViewHolder> {

    private final GamesSelectorRecyclerViewInterface gamesSelectorRecyclerViewInterface;
    private ArrayList<Games> gamesArrayList;
    private Context context;
    int lastPos = -1;

    public GamesRVAdapter(ArrayList<Games> gamesArrayList, Context context, GamesSelectorRecyclerViewInterface gamesSelectorRecyclerViewInterface) {
        this.gamesArrayList = gamesArrayList;
        this.context = context;
        this.gamesSelectorRecyclerViewInterface = gamesSelectorRecyclerViewInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // inflating our layout file on below line.
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.game_item_rv, parent, false);

        return new GamesRVAdapter.ViewHolder(view, gamesSelectorRecyclerViewInterface);
        //return new ViewHolder(view,membersRecyclerViewInterface);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // setting data to our recycler view item on below line.
        Games game = gamesArrayList.get(position);

        holder.naslovIgre.setText(game.getGameName());

        setAnimation(holder.itemView, position);

    }

    private void setAnimation(View itemView, int position) {
        if (position > lastPos) {
            // on below line we are setting animation.
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.setAnimation(animation);
            lastPos = position;
        }
    }

    @Override
    public int getItemCount() {
        return gamesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView naslovIgre;

        public ViewHolder(@NonNull View itemView, GamesSelectorRecyclerViewInterface gamesSelectorRecyclerViewInterface) {
            super(itemView);

            naslovIgre = itemView.findViewById(R.id.naslovIgrice);

            itemView.findViewById(R.id.remove_game_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(gamesSelectorRecyclerViewInterface!=null){

                        int pos = getAdapterPosition();

                        if(pos!=RecyclerView.NO_POSITION){

                            gamesSelectorRecyclerViewInterface.onItemClick(pos);

                        }

                    }
                }
            });
        }
    }

}
