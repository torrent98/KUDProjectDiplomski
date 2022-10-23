package com.example.kudproject.performance;

import android.annotation.SuppressLint;
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

public class PerformanceRVAdapter extends RecyclerView.Adapter<PerformanceRVAdapter.ViewHolder> {

    private PerformancesRecyclerViewInterface performancesRecyclerViewInterface;
    private ArrayList<Performance> performanceArrayList;
    private Context context;
    int lastPos = -1;

    // creating a constructor.
    public PerformanceRVAdapter(ArrayList<Performance> performanceArrayList, Context context, PerformancesRecyclerViewInterface performancesRecyclerInterface) {
        this.performanceArrayList = performanceArrayList;
        this.context = context;
        this.performancesRecyclerViewInterface = performancesRecyclerInterface;
    }

    public void setFilteredList(ArrayList<Performance> performanceArrayList, Context context, PerformancesRecyclerViewInterface performancesRecyclerInterface){

        this.performanceArrayList = performanceArrayList;
        this.context = context;
        this.performancesRecyclerViewInterface = performancesRecyclerInterface;

        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public PerformanceRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflating our layout file on below line.
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.performance_item_rv, parent, false);

        return new PerformanceRVAdapter.ViewHolder(view,performancesRecyclerViewInterface);
        //return new ViewHolder(view,membersRecyclerViewInterface);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull PerformanceRVAdapter.ViewHolder holder, int position) {

        // setting data to our recycler view item on below line.
        Performance performance = performanceArrayList.get(position);

        holder.naslov.setText(performance.getNaslov());
        holder.datumOD.setText("Datum:" + performance.getDatumOdrzavanja());
        holder.lokacija.setText("Lokacija:" + performance.getLokacija());

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
        return performanceArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variable for our text view on below line.
        private TextView naslov,lokacija,datumOD;

        public ViewHolder(@NonNull View itemView, PerformancesRecyclerViewInterface performancesRecyclerInterface) {
            super(itemView);
            // initializing all our variables on below line.
            naslov = itemView.findViewById(R.id.naslov_nastupa);
            datumOD = itemView.findViewById(R.id.datum_odrzavanja);
            lokacija = itemView.findViewById(R.id.lokacija_nastupa);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(performancesRecyclerInterface!=null){

                        int pos = getAdapterPosition();

                        if(pos!=RecyclerView.NO_POSITION){

                            performancesRecyclerInterface.onItemClick(pos);

                        }

                    }
                }
            });

        }
    }

}
