package com.example.rouminder.firebase.manager;

import static com.example.rouminder.firebase.manager.BaseModelManager.checkUidInitialized;

import androidx.annotation.NonNull;

import com.example.rouminder.firebase.model.CategoryModel;
import com.example.rouminder.firebase.model.GoalModel;
import com.example.rouminder.firebase.model.ModelDoesNotExists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;


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

    public GoalModel create(String categoryId, String goalName, String goalType,
                            int current, Date startDatetime, Date finishDatetime) {
        String startDTString = BaseModelManager.getTimeStampString(startDatetime);
        String finishDTString = BaseModelManager.getTimeStampString(finishDatetime);

        GoalModel newGoal = create(categoryId, goalName, goalType, current, startDTString, finishDTString);
        return newGoal;
    }

    public GoalModel create(String categoryId, String goalName, String goalType,
                            int current, String startDTString, String finishDTString) {
        checkUidInitialized();

        String created_at = baseModelManager.getTimeStampString();
        String randomId = baseModelManager.getRandomId();

        ref.child("data").child(randomId).child("author").setValue(uid);
        ref.child("data").child(randomId).child("created_at").setValue(created_at);
        ref.child("data").child(randomId).child("modified_at").setValue("");
        ref.child("data").child(randomId).child("category").setValue(categoryId);
        ref.child("data").child(randomId).child("name").setValue(goalName);
        ref.child("data").child(randomId).child("type").setValue(goalType);
        ref.child("data").child(randomId).child("current").setValue(current);

        ref.child("data").child(randomId).child("start_datetime").setValue(startDTString);
        ref.child("data").child(randomId).child("finish_datetime").setValue(finishDTString);


        GoalModel newGoal = new GoalModel(randomId, uid, created_at,
                "", categoryId, goalName, goalType, current, startDTString, finishDTString);

        sync();

        return newGoal;
    }

    public void sync() {
        checkUidInitialized();

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
                                data.get("category"), data.get("name"), data.get("type"), Integer.parseInt(data.get("current")),
                                data.get("start_datetime"), data.get("finish_datetime")));
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

    public ArrayList<GoalModel> get() {
        checkUidInitialized();
        sync();
        return goals;
    }

    public GoalModel get(String id) throws ModelDoesNotExists {
        checkUidInitialized();
        sync();

        GoalModel ret = null;
        for (GoalModel model : goals) {
            if (model.id.equals(id)) {
                ret = model;
                break;
            }
        }

        if (ret == null)
            throw new ModelDoesNotExists();
        else
            return ret;
    }
}
