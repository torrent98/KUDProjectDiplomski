package com.example.kudproject.performance;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class PerformanceDB {

    private DatabaseReference databaseReference;

    public PerformanceDB()
    {
        FirebaseDatabase db =FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Performance.class.getSimpleName());
    }

    public Task<Void> add(Performance performance)
    {
        return databaseReference.push().setValue(performance);
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
