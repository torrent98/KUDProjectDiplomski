package com.example.kudproject.members;

import com.example.kudproject.members.Member;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class MemberDB {

    private DatabaseReference databaseReference;

    public MemberDB()
    {
        FirebaseDatabase db =FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Member.class.getSimpleName());
    }

    public Task<Void> add(Member member)
    {
        return databaseReference.push().setValue(member);
    }

    public Task<Void> update(String key, HashMap<String ,Object> hashMap)
    {
        return databaseReference.child(key).updateChildren(hashMap);
    }

    public Task<Void> remove(String key)
    {
        return databaseReference.child(key).removeValue();
    }

    public Query get(String key)
    {
        if(key == null)
        {
            return databaseReference.orderByKey().limitToFirst(100);
        }
        return databaseReference.orderByKey().startAfter(key).limitToFirst(100);
    }

    public Query get()
    {
        return databaseReference;
    }

}
