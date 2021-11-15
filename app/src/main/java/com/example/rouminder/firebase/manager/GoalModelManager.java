package com.example.rouminder.firebase.manager;

import androidx.annotation.NonNull;

import com.example.rouminder.firebase.model.GoalModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class GoalModelManager {
    private static GoalModelManager instance = new GoalModelManager();

    private final BaseModelManager baseModelManager = BaseModelManager.getInstance();
    private final DatabaseReference ref;
    private ArrayList<GoalModel> goals;
    private final String uid;

    private GoalModelManager() {
        FirebaseDatabase db = baseModelManager.db;
        uid = baseModelManager.uid;
        ref = db.getReference("goal");
        goals = new ArrayList<>();
    }

    public static GoalModelManager getInstance() {
        return instance;
    }

    public GoalModel create(String categoryId, String goalName) {
        baseModelManager.checkUidInitialized();

        String created_at = baseModelManager.getTimeStampString();
        String randomId = baseModelManager.getRandomId();

        ref.child("data").child(randomId).child("author").setValue(uid);
        ref.child("data").child(randomId).child("created_at").setValue(created_at);
        ref.child("data").child(randomId).child("modified_at").setValue("");
        ref.child("data").child(randomId).child("category").setValue(categoryId);
        ref.child("data").child(randomId).child("name").setValue(goalName);

        GoalModel newGoal = new GoalModel(randomId, uid, created_at,
                "", categoryId, goalName);

        return newGoal;
    }

    public void syncGoalModels() {
        baseModelManager.checkUidInitialized();

        Query select = ref.child("data");
        select.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, String>> result =
                        (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();

                goals = new ArrayList<>();
                for (String id : result.keySet()) {
                    HashMap<String, String> data = result.get(id);
                    String author = data.get("author");

                    if (author.equals(uid)) {
                        goals.add(new GoalModel(
                                id, uid, data.get("created_at"), data.get("modified_at"),
                                data.get("category"), data.get("name")));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("HELLO : Failed to read value");
                databaseError.toException();
            }
        });
    }

    public ArrayList<GoalModel> getGoalModels() {
        return goals;
    }
}
