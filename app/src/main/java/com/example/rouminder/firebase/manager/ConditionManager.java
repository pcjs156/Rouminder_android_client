package com.example.rouminder.firebase.manager;

import static com.example.rouminder.firebase.manager.BaseModelManager.checkUidInitialized;

import androidx.annotation.NonNull;

import com.example.rouminder.firebase.model.ConditionModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ConditionManager {
    private static ConditionManager instance = new ConditionManager();

    private final BaseModelManager baseModelManager = BaseModelManager.getInstance();
    private final DatabaseReference ref;
    private ArrayList<ConditionModel> conditions;
    private final String uid;

    private ConditionManager() {
        FirebaseDatabase db = baseModelManager.db;
        uid = baseModelManager.uid;
        ref = db.getReference("condition");
        conditions = new ArrayList<>();
    }

    public static ConditionManager getInstance() {
        return instance;
    }

    public ConditionModel create(String actionId, String condType) {
        baseModelManager.checkUidInitialized();

        String created_at = baseModelManager.getTimeStampString();
        String randomId = baseModelManager.getRandomId();

        ref.child("data").child(randomId).child("author").setValue(uid);
        ref.child("data").child(randomId).child("action").setValue(actionId);
        ref.child("data").child(randomId).child("created_at").setValue(created_at);
        ref.child("data").child(randomId).child("type").setValue(condType);
        ref.child("data").child(randomId).child("modified_at").setValue("");

        ConditionModel newCondition = new ConditionModel(
                randomId, uid, created_at, "", condType
        );

        return newCondition;
    }

    ;

    public void syncConditionModels() {
        checkUidInitialized();

        Query select = ref.child("data");
        select.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, String>> result =
                        (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();

                conditions = new ArrayList<>();
                for (String id : result.keySet()) {
                    HashMap<String, String> data = result.get(id);
                    String author = data.get("author");

                    if (author.equals(uid)) {
                        conditions.add(new ConditionModel(
                                id, uid, data.get("created_at"), data.get("modified_at"),
                                data.get("type")));
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

    public ArrayList<ConditionModel> getActionModels() {
        return conditions;
    }
}
