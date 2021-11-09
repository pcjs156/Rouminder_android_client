package com.example.rouminder.firebase;

import androidx.annotation.NonNull;

import com.example.rouminder.firebase.model.ActionModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class ActionManager {
    private static ActionManager instance = new ActionManager();

    private final BaseModelManager baseModelManager = BaseModelManager.getInstance();
    private final DatabaseReference ref;
    private ArrayList<ActionModel> actions;
    private final String uid;

    private ActionManager() {
        FirebaseDatabase db = baseModelManager.db;
        uid = baseModelManager.uid;
        ref = db.getReference("action");
        actions = new ArrayList<>();
    }

    public static ActionManager getInstance() {
        return instance;
    }

    public ActionModel create(String type, String unit) {
        baseModelManager.checkUidInitialized();

        String created_at = baseModelManager.getTimeStampString();

        String randomId = baseModelManager.getRandomId();

        ref.child("data").child(randomId).child("author").setValue(baseModelManager.uid);
        ref.child("data").child(randomId).child("created_at").setValue(created_at);
        ref.child("data").child(randomId).child("type").setValue(type);
        ref.child("data").child(randomId).child("unit").setValue(unit);
        ref.child("data").child(randomId).child("modified_at").setValue("");

        ActionModel newAction = new ActionModel(randomId, uid, created_at, "", type, unit);

        return newAction;
    }

    public void syncActionModels() {
        baseModelManager.checkUidInitialized();

        Query select = ref.child("data");
        select.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, String>> result =
                        (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();

                actions = new ArrayList<>();
                for (String id : result.keySet()) {
                    HashMap<String, String> data = result.get(id);
                    String author = data.get("author");

                    if (author.equals(uid)) {
                        actions.add(new ActionModel(
                                id, uid, data.get("created_at"), data.get("modified_at"),
                                data.get("type"), data.get("unit")));
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

    public ArrayList<ActionModel> getActionModels() {
        return actions;
    }
}
