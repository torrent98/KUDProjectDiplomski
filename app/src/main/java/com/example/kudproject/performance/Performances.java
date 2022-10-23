package com.example.kudproject.performance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kudproject.maps.Navigation;
import com.example.kudproject.R;
import com.example.kudproject.members.AllMembersActivity;
import com.example.kudproject.user.UserSettings;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Performances extends AppCompatActivity implements PerformancesRecyclerViewInterface {

    //Mapa
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    FloatingActionButton addPerformanceBtn;

    private RecyclerView perfRV;

    //FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //private FirebaseAuth mAuth;

    private ProgressBar loadingPB;

    private ArrayList<Performance> perfList;
    private PerformanceRVAdapter performanceRVAdapter;

//    private Performance performanceMain;

    //private PerformanceDB db;

    private SearchView searchPerfBar;

    private Button filterBtn_Amateur;
    private Button filterBtn_Junior;
    private Button filterBtn_Senior;
    private Button filterBtn_Clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performances);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setSelectedItemId(R.id.nav_perf);

        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.nav_members:
                        startActivity(new Intent(getApplicationContext(), AllMembersActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_map:

                        if(isServiceOK()){
                            init();
                        }
                        return true;

                    case R.id.nav_perf:
                        return true;

                    case R.id.nav_settings:
                        startActivity(new Intent(getApplicationContext(), UserSettings.class));
                        overridePendingTransition(0,0);
                        return true;
                }

                return false;
            }

        });

        addPerformanceBtn = findViewById(R.id.add_performance_btn);
        addPerformanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Performances.this, AddPerformanceActivity.class);
                startActivity(intent);
            }
        });

        searchPerfBar = findViewById(R.id.searchPerformance);
        searchPerfBar.clearFocus();

        searchPerfBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchPerfList(newText);
                return true;
            }
        });

        filterBtn_Amateur = findViewById(R.id.filterBtn_amateur_perf);
        filterBtn_Junior = findViewById(R.id.filterBtn_junior_perf);
        filterBtn_Senior = findViewById(R.id.filterBtn_senior_perf);
        filterBtn_Clear = findViewById(R.id.filterBtn_clear_perf);

        filterBtn_Amateur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amateur = "Amateur";

                filterAmateurPerformances(amateur);
            }
        });

        filterBtn_Junior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String junior = "Junior";

                filterJuniorsPerformances(junior);
            }
        });

        filterBtn_Senior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String senior = "Senior";

                filterSeniorsPerformances(senior);
            }
        });

        filterBtn_Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearPerformanceList();

            }
        });

        perfRV = findViewById(R.id.rv_performances);
        loadingPB = findViewById(R.id.idPBLoading);

        perfList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Performance");
        // on below line we are getting database reference.
        //databaseReference = firebaseDatabase.getReference("Courses");
        //db = new PerformanceDB();

        // on below line initializing our adapter class.
        performanceRVAdapter = new PerformanceRVAdapter(perfList,this,this);

        // setting layout manager to recycler view on below line.
        perfRV.setLayoutManager(new LinearLayoutManager(this));

        // setting adapter to recycler view on below line.
        perfRV.setAdapter(performanceRVAdapter);

        getAllPerformances();

//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                perfList.clear();
//
//                for (DataSnapshot data : snapshot.getChildren())
//                {
//
//                    loadingPB.setVisibility(View.VISIBLE);
//
//                    Performance performance = data.getValue(Performance.class);
//                    performance.setKey(data.getKey());
//
//                    perfList.add(performance);
//                    //key = data.getKey();
//                }
//                loadingPB.setVisibility(View.GONE);
//                performanceRVAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                performanceRVAdapter.notifyDataSetChanged();
//            }
//        });

        // on below line calling a method to fetch courses from database.
        //getPerformances();

    }

    private void getAllPerformances() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                perfList.clear();

                for (DataSnapshot data : snapshot.getChildren())
                {

                    loadingPB.setVisibility(View.VISIBLE);

                    Performance performance = data.getValue(Performance.class);
                    performance.setKey(data.getKey());

                    perfList.add(performance);
                    //key = data.getKey();
                }
                loadingPB.setVisibility(View.GONE);
                performanceRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                performanceRVAdapter.notifyDataSetChanged();
            }
        });

    }

    private void searchPerfList(String newText) {

        ArrayList<Performance> newPerfList = new ArrayList<>();

        for (Performance performance : perfList){
            if(performance.getNaslov().toLowerCase().contains(newText.toLowerCase()) || performance.getLokacija().toLowerCase().contains(newText.toLowerCase())){
                newPerfList.add(performance);
            }
        }

        perfList.clear();

        for (Performance performance : newPerfList){
            perfList.add(performance);
        }

        if(newPerfList.isEmpty()){
            //Toast.makeText(this, "No data found.", Toast.LENGTH_SHORT).show();
        }else{
            performanceRVAdapter.setFilteredList(perfList,this,this);
        }

    }

    private void clearPerformanceList() {

        filterBtn_Amateur.setVisibility(View.VISIBLE);
        filterBtn_Junior.setVisibility(View.VISIBLE);
        filterBtn_Senior.setVisibility(View.VISIBLE);

        getAllPerformances();

    }

    private void filterSeniorsPerformances(String senior) {

        filterBtn_Amateur.setVisibility(View.GONE);
        filterBtn_Junior.setVisibility(View.GONE);

        ArrayList<Performance> seniorPerf = new ArrayList<>();

        for (Performance performance : perfList){
            seniorPerf.add(performance);
        }

        perfList.clear();

        for (Performance performance : seniorPerf){
            if(performance.getPotrebanRank().toLowerCase().contains(senior.toLowerCase())){
                perfList.add(performance);
            }
        }

        if(perfList.isEmpty()){
            Toast.makeText(this, "No Senior Performance found.", Toast.LENGTH_SHORT).show();
        }else{
            performanceRVAdapter.setFilteredList(perfList,this,this);
        }

    }

    private void filterJuniorsPerformances(String junior) {

        filterBtn_Amateur.setVisibility(View.GONE);
        filterBtn_Senior.setVisibility(View.GONE);

        ArrayList<Performance> juniorPerf = new ArrayList<>();

        for (Performance performance : perfList){
            juniorPerf.add(performance);
        }

        perfList.clear();

        for (Performance performance : juniorPerf){
            if(performance.getPotrebanRank().toLowerCase().contains(junior.toLowerCase())){
                perfList.add(performance);
            }
        }

        if(perfList.isEmpty()){
            Toast.makeText(this, "No Junior Performance found.", Toast.LENGTH_SHORT).show();
        }else{
            performanceRVAdapter.setFilteredList(perfList,this,this);
        }

    }

    private void filterAmateurPerformances(String amateur) {

        filterBtn_Junior.setVisibility(View.GONE);
        filterBtn_Senior.setVisibility(View.GONE);

        ArrayList<Performance> amateurPerf = new ArrayList<>();

        for (Performance performance : perfList){
            amateurPerf.add(performance);
        }

        perfList.clear();

        for (Performance performance : amateurPerf){
            if(performance.getPotrebanRank().toLowerCase().contains(amateur.toLowerCase())){
                perfList.add(performance);
            }
        }

        if(perfList.isEmpty()){
            Toast.makeText(this, "No Amateur Performance found.", Toast.LENGTH_SHORT).show();
        }else{
            performanceRVAdapter.setFilteredList(perfList,this,this);
        }

    }

//    private void getPerformances() {
//
//        perfList.clear();
//
//        db.get().addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot data : snapshot.getChildren())
//                {
//                    Performance performance = data.getValue(Performance.class);
//                    performance.setKey(data.getKey());
//
//                    perfList.add(performance);
//                    //key = data.getKey();
//                }
//                loadingPB.setVisibility(View.GONE);
//                performanceRVAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                performanceRVAdapter.notifyDataSetChanged();
//            }
//        });
//
//
//    }

    @Override
    public void onItemClick(int position) {

        Performance performance = perfList.get(position);

        Intent intent = new Intent(Performances.this, ViewPerformanceDetails.class);

        intent.putExtra("key",performance.getKey());
        intent.putExtra("naslov",performance.getNaslov());
        intent.putExtra("datumOdrzavanja",performance.getDatumOdrzavanja());
        intent.putExtra("lokacija",performance.getLokacija());
        intent.putExtra("potrebanRank",performance.getPotrebanRank());
        intent.putParcelableArrayListExtra(ViewPerformanceDetails.EXTRA_DATA, performance.getGamesList());
        intent.putParcelableArrayListExtra(ViewPerformanceDetails.EXTRA_PLAYER, performance.getMemberList());

        startActivity(intent);

    }

    private void init(){

        startActivity(new Intent(getApplicationContext(), Navigation.class));
        overridePendingTransition(0,0);

    }

    public boolean isServiceOK() {

        Log.d(TAG, "isServiceOK: checking google service version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Performances.this);

        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isSerciveOK: Google Play Services is working");
            return true;
        } else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServiceOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Performances.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}