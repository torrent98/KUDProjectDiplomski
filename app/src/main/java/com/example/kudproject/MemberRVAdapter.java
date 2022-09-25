package com.example.kudproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MemberRVAdapter extends RecyclerView.Adapter<MemberRVAdapter.ViewHolder> {

    private final MembersRecyclerViewInterface membersRecyclerViewInterface;
    private ArrayList<Member> memberArrayList;
    private Context context;
    int lastPos = -1;

    // creating a constructor.
    public MemberRVAdapter(ArrayList<Member> memberArrayList, Context context, MembersRecyclerViewInterface membersRecyclerViewInterface) {
        this.memberArrayList = memberArrayList;
        this.context = context;
        this.membersRecyclerViewInterface = membersRecyclerViewInterface;
    }

    @NonNull
    @Override
    public MemberRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // inflating our layout file on below line.
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.member_item_rv, parent, false);

        return new MemberRVAdapter.ViewHolder(view,membersRecyclerViewInterface);
        //return new ViewHolder(view,membersRecyclerViewInterface);
    }


    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull MemberRVAdapter.ViewHolder holder, int position) {
        // setting data to our recycler view item on below line.
        Member member = memberArrayList.get(position);

        holder.ime.setText(member.getIme());
        holder.prezime.setText(member.getPrezime());
        holder.adresa.setText(member.getAdresa());
        holder.rank.setText("Rank:" + member.getRank());

        // adding animation to recycler view item on below line.
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
        return memberArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variable for our text view on below line.
        private TextView ime,prezime,adresa,rank;

        public ViewHolder(@NonNull View itemView, MembersRecyclerViewInterface membersRecyclerViewInterface) {
            super(itemView);
            // initializing all our variables on below line.
            ime = itemView.findViewById(R.id.ime_clana);
            prezime = itemView.findViewById(R.id.prezime_clana);
            adresa = itemView.findViewById(R.id.adresa_clana);
            rank = itemView.findViewById(R.id.rank_clana);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(membersRecyclerViewInterface!=null){

                        int pos = getAdapterPosition();

                        if(pos!=RecyclerView.NO_POSITION){

                            membersRecyclerViewInterface.onItemClick(pos);

                        }

                    }
                }
            });

        }
    }

}
