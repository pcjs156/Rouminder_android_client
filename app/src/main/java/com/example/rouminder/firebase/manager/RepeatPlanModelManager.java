package com.example.rouminder.firebase.manager;

import static com.example.rouminder.firebase.manager.BaseModelManager.checkUidInitialized;

import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.rouminder.firebase.exception.ModelDoesNotExists;
import com.example.rouminder.firebase.model.RepeatPlanModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class RepeatPlanModelManager {
    private static RepeatPlanModelManager instance = new RepeatPlanModelManager();

    private final BaseModelManager baseModelManager = BaseModelManager.getInstance();
    private final DatabaseReference ref;
    public final DatabaseReference dataRef;
    public ArrayList<RepeatPlanModel> plans = new ArrayList<>();

    private final HashSet<ArrayAdapter> notifyAdapters = new HashSet<>();

    private boolean isChanging;

    private RepeatPlanModelManager() {
        isChanging = true;

        FirebaseDatabase db = baseModelManager.db;
        ref = db.getReference("repeat_plan");
        dataRef = ref.child("data");

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isChanging = true;

                HashMap<String, HashMap<String, String>> result =
                        (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();

                plans.clear();
                if (result != null && !result.isEmpty()) {
                    for (String id : result.keySet()) {
                        HashMap<String, String> goal = result.get(id);
                        String author = goal.get("uid");
                        HashMap<String, Object> _goal = (HashMap<String, Object>) goal.clone();
                        if (author.equals(BaseModelManager.uid)) {
                            plans.add(new RepeatPlanModel(_goal));
                        }
                    }
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

    public static RepeatPlanModelManager getInstance() {
        return instance;
    }

    public void notifyToAdapters() {
        for (ArrayAdapter adapter : notifyAdapters) {
            adapter.notifyDataSetChanged();
        }
    }

    public RepeatPlanModel create(HashMap<String, Object> values) {
        checkUidInitialized();

        String createdAt = baseModelManager.getTimeStampString();

        String randomId = baseModelManager.getRandomId();

        values.put("id", randomId);
        values.put("uid", BaseModelManager.uid);
        values.put("created_at", createdAt);
        values.put("modified_at", "");

        ref.child("data").child(randomId).setValue(values);

        RepeatPlanModel newGoal = new RepeatPlanModel(values);

        return newGoal;
    }

    public ArrayList<RepeatPlanModel> get() {
        checkUidInitialized();
        return plans;
    }

    public RepeatPlanModel get(String id) throws ModelDoesNotExists {
        checkUidInitialized();

        RepeatPlanModel ret = null;
        for (RepeatPlanModel model : plans) {
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

    public RepeatPlanModel update(String id, HashMap<String, Object> newValues) throws ModelDoesNotExists {
        checkUidInitialized();

        RepeatPlanModel targetRepeatPlanModel = null;
        int targetIdx = -1;
        for (int i = 0; i < plans.size(); i++) {
            RepeatPlanModel model = plans.get(i);
            if (model.id.equals(id)) {
                targetRepeatPlanModel = model;
                targetIdx = i;
                break;
            }
        }

        if (targetRepeatPlanModel == null)
            throw new ModelDoesNotExists();
        else {
            newValues.put("modified_at", BaseModelManager.getTimeStampString());
            dataRef.child(id).updateChildren(newValues);

            RepeatPlanModel updatedModel = targetRepeatPlanModel.update(newValues);
            plans.set(targetIdx, updatedModel);

            notifyToAdapters();
            return updatedModel;
        }
    }

    public void delete(String id) throws ModelDoesNotExists {
        checkUidInitialized();

        RepeatPlanModel targetRepeatPlanModel = null;
        int targetIdx = -1;
        for (RepeatPlanModel repeatPlanModel : plans) {
            if (repeatPlanModel.id.equals(id)) {
                targetRepeatPlanModel = repeatPlanModel;
                break;
            }
        }
        for (int i = 0; i < plans.size(); i++) {
            RepeatPlanModel repeatPlanModel = plans.get(i);
            if (repeatPlanModel.id.equals(id)) {
                targetRepeatPlanModel = repeatPlanModel;
                targetIdx = i;
                break;
            }
        }

        if (targetRepeatPlanModel == null)
            throw new ModelDoesNotExists();
        else {
            dataRef.child(id).removeValue();
            plans.remove(targetIdx);
            notifyToAdapters();
        }
    }
}
