package com.example.kudproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllMembersActivity extends AppCompatActivity implements MembersRecyclerViewInterface {

    //Mapa
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    FloatingActionButton addMemberBtn;

    private RecyclerView memberRV;

    //FirebaseDatabase firebaseDatabase;
    DatabaseReference membersDBRef;
    //private FirebaseAuth mAuth;

    private ProgressBar loadingPB;

    private ArrayList<Member> membersList;
    private MemberRVAdapter memberRVAdapter;

    //private MemberDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_members);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setSelectedItemId(R.id.nav_members);

        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.nav_members:
                        return true;

                    case R.id.nav_map:

                        if(isServiceOK()){
                            init();
                        }
                        return true;

                    case R.id.nav_perf:
                        startActivity(new Intent(getApplicationContext(),Performances.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_settings:
                        startActivity(new Intent(getApplicationContext(),UserSettings.class));
                        overridePendingTransition(0,0);
                        return true;
                }

                return false;
            }

        });

        addMemberBtn = findViewById(R.id.add_member_btn);
        addMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllMembersActivity.this,AddMemberActivity.class);
                startActivity(intent);
            }
        });

        memberRV = findViewById(R.id.rv_members);
        loadingPB = findViewById(R.id.idPBLoading);

        membersList = new ArrayList<>();

//        firebaseDatabase = FirebaseDatabase.getInstance();
//        mAuth = FirebaseAuth.getInstance();

        membersDBRef = FirebaseDatabase.getInstance().getReference("Member");

        // on below line we are getting database reference.
        //databaseReference = firebaseDatabase.getReference("Courses");
        //db = new MemberDB();

        // on below line initializing our adapter class.
        memberRVAdapter = new MemberRVAdapter(membersList, this,this);

        // setting layout manager to recycler view on below line.
        memberRV.setLayoutManager(new LinearLayoutManager(this));

        // setting adapter to recycler view on below line.
        memberRV.setAdapter(memberRVAdapter);

        // on below line calling a method to fetch courses from database.
        //getMembers();

        membersDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                membersList.clear();

                //Toast.makeText(AllMembersActivity.this, "Players are taken form data base.", Toast.LENGTH_SHORT).show();

                for (DataSnapshot data : snapshot.getChildren())
                {

                    //Toast.makeText(AllMembersActivity.this, "Players loading.", Toast.LENGTH_SHORT).show();


                    loadingPB.setVisibility(View.VISIBLE);

                    Member member = data.getValue(Member.class);
                    member.setKey(data.getKey());

                    membersList.add(member);
                    //key = data.getKey();
                }

                loadingPB.setVisibility(View.GONE);
                memberRVAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                memberRVAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);
            }
        });

    }

//    private void getMembers() {
//
//        // on below line clearing our list.
//        membersList.clear();
//
//        db.get().addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot data : snapshot.getChildren())
//                {
//                    Member member = data.getValue(Member.class);
//                    member.setKey(data.getKey());
//
//                    membersList.add(member);
//                    //key = data.getKey();
//                }
//                loadingPB.setVisibility(View.GONE);
//                memberRVAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                memberRVAdapter.notifyDataSetChanged();
//            }
//        });
//
//        // on below line we are calling add child event listener method to read the data.
////        databaseReference.addChildEventListener(new ChildEventListener() {
////            @Override
////            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
////
////                // on below line we are hiding our progress bar.
////                loadingPB.setVisibility(View.GONE);
////
////                // adding snapshot to our array list on below line.
////                membersList.add(snapshot.getValue(Member.class));
////
////                // notifying our adapter that data has changed.
////                memberRVAdapter.notifyDataSetChanged();
////
////            }
////
////            @Override
////            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
////
////                // this method is called when new child is added
////                // we are notifying our adapter and making progress bar
////                // visibility as gone.
////                loadingPB.setVisibility(View.GONE);
////                memberRVAdapter.notifyDataSetChanged();
////
////            }
////
////            @Override
////            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
////
////                // notifying our adapter when child is removed.
////                memberRVAdapter.notifyDataSetChanged();
////                loadingPB.setVisibility(View.GONE);
////
////            }
////
////            @Override
////            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
////
////                // notifying our adapter when child is moved.
////                memberRVAdapter.notifyDataSetChanged();
////                loadingPB.setVisibility(View.GONE);
////
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {
////
////            }
////        });
//
//    }

    @Override
    public void onItemClick(int position) {

        Member member = membersList.get(position);

        Intent intent = new Intent(AllMembersActivity.this, ViewMemberDetails.class);

        intent.putExtra("member",member);

        startActivity(intent);
    }

    private void init(){

        startActivity(new Intent(getApplicationContext(),Navigation.class));
        overridePendingTransition(0,0);

    }

    public boolean isServiceOK() {

        Log.d(TAG, "isServiceOK: checking google service version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(AllMembersActivity.this);

        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isSerciveOK: Google Play Services is working");
            return true;
        } else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServiceOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(AllMembersActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}