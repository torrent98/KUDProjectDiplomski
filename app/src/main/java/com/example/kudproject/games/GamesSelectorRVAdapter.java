package com.example.kudproject.games;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kudproject.R;

import java.util.ArrayList;

public class GamesSelectorRVAdapter extends RecyclerView.Adapter<GamesSelectorRVAdapter.ViewHolder>{

    private final GamesSelectorRecyclerViewInterface gamesSelectorRecyclerViewInterface;
    private ArrayList<Games> gamesArrayList;
    private Context context;
    int lastPos = -1;

    public GamesSelectorRVAdapter(ArrayList<Games> gamesArrayList, Context context, GamesSelectorRecyclerViewInterface gamesSelectorRecyclerViewInterface) {
        this.gamesArrayList = gamesArrayList;
        this.context = context;
        this.gamesSelectorRecyclerViewInterface = gamesSelectorRecyclerViewInterface;
    }

    @NonNull
    @Override
    public GamesSelectorRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // inflating our layout file on below line.
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.game_selector_item, parent, false);

        return new GamesSelectorRVAdapter.ViewHolder(view, gamesSelectorRecyclerViewInterface);
        //return new ViewHolder(view,membersRecyclerViewInterface);

    }

    @Override
    public void onBindViewHolder(@NonNull GamesSelectorRVAdapter.ViewHolder holder, int position) {

        // setting data to our recycler view item on below line.
        Games game = gamesArrayList.get(position);

        holder.naslovIgre.setText(game.getGameName());

        // adding animation to recycler view item on below line.
        setAnimationSlide(holder.itemView, position);

    }

    private void setAnimationSlide(View itemView, int position) {
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
