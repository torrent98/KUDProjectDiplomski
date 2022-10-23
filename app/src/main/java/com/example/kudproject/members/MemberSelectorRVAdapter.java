package com.example.kudproject.members;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kudproject.R;

import java.util.ArrayList;

public class MemberSelectorRVAdapter extends RecyclerView.Adapter<MemberSelectorRVAdapter.ViewHolder> {

    private final MemberOnClickRecyclerViewInterface memberOnClickRecyclerViewInterface;
    private ArrayList<Member> memberArrayList;
    private Context context;
    int lastPos = -1;

    public MemberSelectorRVAdapter(ArrayList<Member> memberArrayList, Context context, MemberOnClickRecyclerViewInterface memberOnClickRecyclerViewInterface) {
        this.memberArrayList = memberArrayList;
        this.context = context;
        this.memberOnClickRecyclerViewInterface = memberOnClickRecyclerViewInterface;
    }

    @NonNull
    @Override
    public MemberSelectorRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.member_item_rv_selector, parent, false);

        return new MemberSelectorRVAdapter.ViewHolder(view, memberOnClickRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberSelectorRVAdapter.ViewHolder holder, int position) {

        Member member = memberArrayList.get(position);

        holder.naslovIgraca.setText(member.getIme() + " " + member.getPrezime());

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

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView naslovIgraca;
        private Button removePlayer;

        public ViewHolder(@NonNull View itemView, MemberOnClickRecyclerViewInterface memberOnClickRecyclerViewInterface) {
            super(itemView);

            naslovIgraca = itemView.findViewById(R.id.naslovIgrac);
            //removePlayer = itemView.findViewById(R.id.remove_player_btn);

            itemView.findViewById(R.id.remove_player_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(memberOnClickRecyclerViewInterface!=null){

                        int pos = getAdapterPosition();

                        if(pos!=RecyclerView.NO_POSITION){

                            memberOnClickRecyclerViewInterface.onMemberClick(pos);

                        }

                    }
                }
            });

        }
    }
}
