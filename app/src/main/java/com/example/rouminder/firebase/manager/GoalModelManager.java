package com.example.rouminder.firebase.manager;

import static com.example.rouminder.firebase.manager.BaseModelManager.checkUidInitialized;

import androidx.annotation.NonNull;

import com.example.rouminder.firebase.model.GoalModel;

import android.widget.ArrayAdapter;

import com.example.rouminder.firebase.exception.ModelDoesNotExists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;


public class GoalModelManager {
    private static GoalModelManager instance = new GoalModelManager();

    private final BaseModelManager baseModelManager = BaseModelManager.getInstance();
    private final DatabaseReference ref;
    public final DatabaseReference dataRef;
    public ArrayList<GoalModel> goals = new ArrayList<>();
    private ArrayList<String> tags = new ArrayList<>();

    private final HashSet<ArrayAdapter> notifyAdapters = new HashSet<>();

    private boolean isChanging;

    private GoalModelManager() {
        isChanging = true;

        FirebaseDatabase db = baseModelManager.db;
        ref = db.getReference("goal");
        dataRef = ref.child("data");

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isChanging = true;

                HashMap<String, HashMap<String, String>> result =
                        (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();

                goals.clear();
                tags.clear();
                if (result != null && !result.isEmpty()) {
                    for (String id : result.keySet()) {
                        HashMap<String, String> goal = result.get(id);
                        String author = goal.get("uid");
                        HashMap<String, Object> _goal = (HashMap<String, Object>) goal.clone();
                        if (author.equals(BaseModelManager.uid)) {
                            goals.add(new GoalModel(_goal));

                            String tag = goal.get("tag");
                            if (tag != null && !tags.contains(tag)) {
                                tags.add(tag);
                            }
                        }
                    }

                    if (!tags.isEmpty()) {
                        tags.sort(new Comparator<String>() {
                            @Override
                            public int compare(String s, String t1) {
                                return s.compareTo(t1);
                            }
                        });
                    }

                    notifyToAdapters();
                }

                isChanging = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("REALTIME_DB: 목표 연동 실패");
            }
        });
    }

    public boolean getIsChanging() {
        return isChanging;
    }

    public static GoalModelManager getInstance() {
        return instance;
    }

    public boolean addNotifyAdapter(ArrayAdapter newAdapter) {
        if (notifyAdapters.contains(newAdapter))
            return false;
        else {
            notifyAdapters.add(newAdapter);
            return true;
        }
    }

    public void notifyToAdapters() {
        for (ArrayAdapter adapter : notifyAdapters) {
            adapter.notifyDataSetChanged();
        }
    }

    public GoalModel create(HashMap<String, Object> values) {
        checkUidInitialized();

        String createdAt = baseModelManager.getTimeStampString();

        String randomId = baseModelManager.getRandomId();

        values.put("id", randomId);
        values.put("uid", BaseModelManager.uid);
        values.put("created_at", createdAt);
        values.put("modified_at", "");

        ref.child("data").child(randomId).setValue(values);

        GoalModel newGoal = new GoalModel(values);

        return newGoal;
    }

    public ArrayList<GoalModel> get() {
        checkUidInitialized();
        return goals;
    }

    public GoalModel get(String id) throws ModelDoesNotExists {
        checkUidInitialized();

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

    public ArrayList<String> getTags() {
        checkUidInitialized();
        return tags;
    }

    public GoalModel update(String id, HashMap<String, Object> newValues) throws ModelDoesNotExists {
        checkUidInitialized();

        GoalModel targetGoal = null;
        int targetIdx = -1;
        for(int i=0; i<goals.size(); i++) {
            GoalModel model = goals.get(i);
            if (model.id.equals(id)) {
                targetGoal = model;
                targetIdx = i;
                break;
            }
        }

        if (targetGoal == null)
            throw new ModelDoesNotExists();
        else {
            newValues.put("modified_at", BaseModelManager.getTimeStampString());
            dataRef.child(id).updateChildren(newValues);

            GoalModel updatedModel = targetGoal.update(newValues);
            goals.set(targetIdx, updatedModel);

            notifyToAdapters();
            return updatedModel;
        }
    }

    public void delete(String id) throws ModelDoesNotExists {
        checkUidInitialized();

        GoalModel targetGoal = null;
        int targetIdx = -1;
        for (GoalModel model : goals) {
            if (model.id.equals(id)) {
                targetGoal = model;
                break;
            }
        }
        for(int i=0; i<goals.size(); i++) {
            GoalModel model = goals.get(i);
            if (model.id.equals(id)) {
                targetGoal = model;
                targetIdx = i;
                break;
            }
        }

        if (targetGoal == null)
            throw new ModelDoesNotExists();
        else {
            dataRef.child(id).removeValue();
            goals.remove(targetIdx);
            notifyToAdapters();
        }
    }
}
