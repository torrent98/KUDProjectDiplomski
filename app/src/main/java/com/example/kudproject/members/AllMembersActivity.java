package com.example.kudproject.members;

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
import com.example.kudproject.performance.Performances;
import com.example.kudproject.R;
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

public class AllMembersActivity extends AppCompatActivity implements MembersRecyclerViewInterface {

    //Mapa
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    FloatingActionButton addMemberBtn;
    FloatingActionButton sortMembersBtn;

    private RecyclerView memberRV;

    //FirebaseDatabase firebaseDatabase;
    DatabaseReference membersDBRef;
    //private FirebaseAuth mAuth;

    private ProgressBar loadingPB;

    private ArrayList<Member> membersList;
    private MemberRVAdapter memberRVAdapter;

    //private MemberDB db;

    //Search

    private SearchView searchMemberBar;

    private Button filterBtn_Amateur;
    private Button filterBtn_Junior;
    private Button filterBtn_Senior;
    private Button filterBtn_Clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_members);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setSelectedItemId(R.id.nav_members);

        //Search views
        searchMemberBar = findViewById(R.id.searchMember);
        searchMemberBar.clearFocus();

        searchMemberBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchMemberList(newText);
                return true;
            }
        });

        filterBtn_Amateur = findViewById(R.id.filterBtn_amateur);
        filterBtn_Junior = findViewById(R.id.filterBtn_junior);
        filterBtn_Senior = findViewById(R.id.filterBtn_senior);
        filterBtn_Clear = findViewById(R.id.filterBtn_clear);

        filterBtn_Amateur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amateur = "Amateur";

                filterAmateurs(amateur);
            }
        });

        filterBtn_Junior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String junior = "Junior";

                filterJuniors(junior);
            }
        });

        filterBtn_Senior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String senior = "Senior";

                filterSeniors(senior);
            }
        });

        filterBtn_Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearMemberList();

            }
        });

        sortMembersBtn = findViewById(R.id.sort_members_btn);

        sortMembersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sortMembers();

            }
        });

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
                        startActivity(new Intent(getApplicationContext(), Performances.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_settings:
                        startActivity(new Intent(getApplicationContext(), UserSettings.class));
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
                Intent intent = new Intent(AllMembersActivity.this, AddMemberActivity.class);
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

        getAllMembers();

//        membersDBRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                membersList.clear();
//
//                //Toast.makeText(AllMembersActivity.this, "Players are taken form data base.", Toast.LENGTH_SHORT).show();
//
//                for (DataSnapshot data : snapshot.getChildren())
//                {
//
//                    //Toast.makeText(AllMembersActivity.this, "Players loading.", Toast.LENGTH_SHORT).show();
//
//
//                    loadingPB.setVisibility(View.VISIBLE);
//
//                    Member member = data.getValue(Member.class);
//                    member.setKey(data.getKey());
//
//                    membersList.add(member);
//                    //key = data.getKey();
//                }
//
//                loadingPB.setVisibility(View.GONE);
//                memberRVAdapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                memberRVAdapter.notifyDataSetChanged();
//                loadingPB.setVisibility(View.GONE);
//            }
//        });

    }

    private void getAllMembers() {

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

    private void sortMembers() {

        int brojac=0;

        ArrayList<Member> sortedMembersList = new ArrayList<>();

        for (Member member : membersList){

            sortedMembersList.add(member);

        }

        membersList.clear();

        for (Member member : sortedMembersList){

            if(brojac == 0){
                if(member.getRank().equals("Senior")){
                    membersList.add(member);
                }
            }

        }

        brojac = 1;

        for (Member member : sortedMembersList){

            if(brojac == 1){
                if(member.getRank().equals("Junior")){
                    membersList.add(member);
                }
            }

        }

        brojac = 2;

        for (Member member : sortedMembersList){

            if(brojac == 2){
                if(member.getRank().equals("Amateur")){
                    membersList.add(member);
                }
            }

        }

        memberRVAdapter.setFilteredList(membersList,this,this);

    }

    private void clearMemberList() {

        filterBtn_Amateur.setVisibility(View.VISIBLE);
        filterBtn_Junior.setVisibility(View.VISIBLE);
        filterBtn_Senior.setVisibility(View.VISIBLE);

        getAllMembers();

    }

    private void filterSeniors(String senior) {

        filterBtn_Amateur.setVisibility(View.GONE);
        filterBtn_Junior.setVisibility(View.GONE);

        ArrayList<Member> seniorMembers = new ArrayList<>();
        ArrayList<Member> supportList = new ArrayList<>();

        for (Member member : membersList){
            supportList.add(member);
        }

        membersList.clear();

        for (Member member : supportList){
            if(member.getRank().toLowerCase().contains(senior.toLowerCase())){
                membersList.add(member);
            }
        }

        if(membersList.isEmpty()){
            Toast.makeText(this, "No Seniors found.", Toast.LENGTH_SHORT).show();
        }else{
            memberRVAdapter.setFilteredList(membersList,this,this);
        }

    }

    private void filterJuniors(String junior) {

        filterBtn_Amateur.setVisibility(View.GONE);
        filterBtn_Senior.setVisibility(View.GONE);

        ArrayList<Member> juniorMembers = new ArrayList<>();
        ArrayList<Member> supportList = new ArrayList<>();

        for (Member member : membersList){
            supportList.add(member);
        }

        membersList.clear();

        for (Member member : supportList){
            if(member.getRank().toLowerCase().contains(junior.toLowerCase())){
                membersList.add(member);
            }
        }

        if(membersList.isEmpty()){
            Toast.makeText(this, "No Juniors found.", Toast.LENGTH_SHORT).show();
        }else{
            memberRVAdapter.setFilteredList(membersList,this,this);
        }

    }

    private void filterAmateurs(String amateur) {

        filterBtn_Junior.setVisibility(View.GONE);
        filterBtn_Senior.setVisibility(View.GONE);

        ArrayList<Member> amateurMembers = new ArrayList<>();
        ArrayList<Member> supportList = new ArrayList<>();

        for (Member member : membersList){
            supportList.add(member);
        }

        membersList.clear();

        for (Member member : supportList){
            if(member.getRank().toLowerCase().contains(amateur.toLowerCase())){
                membersList.add(member);
            }
        }

        if(membersList.isEmpty()){
            Toast.makeText(this, "No Amateurs found.", Toast.LENGTH_SHORT).show();
        }else{
            memberRVAdapter.setFilteredList(membersList,this,this);
        }

    }

    private void searchMemberList(String newText) {

        ArrayList<Member> newMemberList = new ArrayList<>();

        for (Member member : membersList){
            if(member.getIme().toLowerCase().contains(newText.toLowerCase()) || member.getPrezime().toLowerCase().contains(newText.toLowerCase())){
                newMemberList.add(member);
            }
        }

        membersList.clear();

        for (Member member : newMemberList){
            membersList.add(member);
        }

        if(membersList.isEmpty()){
            //Toast.makeText(this, "No data found.", Toast.LENGTH_SHORT).show();
        }else{
            memberRVAdapter.setFilteredList(membersList,this,this);
        }

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

        startActivity(new Intent(getApplicationContext(), Navigation.class));
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