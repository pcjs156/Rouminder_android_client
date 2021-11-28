package com.example.rouminder.firebase.manager;

import static com.example.rouminder.firebase.manager.BaseModelManager.checkUidInitialized;

import androidx.annotation.NonNull;

import com.example.rouminder.firebase.model.GoalModel;

import android.util.Log;
import android.util.Pair;
import android.widget.ArrayAdapter;

import com.example.rouminder.firebase.exception.ModelDoesNotExists;
import com.example.rouminder.firebase.model.RepeatPlanModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.time.LocalDateTime;
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

    public GoalModel create(RepeatPlanModel plan, LocalDateTime startDatetime, LocalDateTime finishDatetime) {
        checkUidInitialized();

        HashMap<String, Object> values = plan.getInfo();

        String createdAt = baseModelManager.getTimeStampString();

        String randomId = baseModelManager.getRandomId();

        values.put("id", randomId);
        values.put("uid", BaseModelManager.uid);
        values.put("created_at", createdAt);
        values.put("modified_at", "");

        values.put("plan", plan.id);
        values.put("current", 0);

        values.put("start_datetime", BaseModelManager.getTimeStampString(startDatetime));
        values.put("finish_datetime", BaseModelManager.getTimeStampString(finishDatetime));

        ref.child("data").child(randomId).setValue(values);

        GoalModel newGoal = new GoalModel(values);
        goals.add(newGoal);

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

        if (id.length() != 9) {
            id = String.format("%09d", Integer.parseInt(id));
        }

        GoalModel targetGoal = null;
        int targetIdx = -1;
        for (int i = 0; i < goals.size(); i++) {
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

        if (id.length() != 9) {
            id = String.format("%09d", Integer.parseInt(id));
        }

        Log.i("model", "delete " + id);
        GoalModel targetGoal = null;
        int targetIdx = -1;

        for (int i = 0; i < goals.size(); i++) {
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

    public double getEntireAchievementRate() {
        int entireGoalCnt = goals.size();
        int achievedGoalCnt = 0;
        for (GoalModel goalModel : goals) {
            if (goalModel.isAchieved())
                achievedGoalCnt++;
        }

        if (getIsChanging() == true)
            return -1;
        if (entireGoalCnt == 0)
            return 0;
        else
            return (achievedGoalCnt / ((double) entireGoalCnt)) * 100;
    }

    public ArrayList<Pair<String, HashMap<String, Object>>> getStatInfos() {
        final String etcTag = "기타";

        HashMap<String, Integer> achievementCntPerTags = new HashMap<>();
        achievementCntPerTags.put(etcTag, 0);

        HashMap<String, Integer> entireGoalCntPerTags = new HashMap<>();
        entireGoalCntPerTags.put(etcTag, 0);

        LocalDateTime now = LocalDateTime.now();

        for (GoalModel goalModel : goals) {
            LocalDateTime finishDatetime = null;
            try {
                finishDatetime = BaseModelManager.parseTimeStampString(goalModel.finishDatetime);
            } catch (ParseException e) {
                System.out.println("Parse Error 발생!!!!!1");
                e.printStackTrace();
                return null;
            }

            if (finishDatetime.isAfter(now)) {
                System.out.println("FINISH DATETIME : " + finishDatetime);
                continue;
            }

            String tag = goalModel.tag;
            if (goalModel.tag == null || goalModel.tag.isEmpty())
                tag = etcTag;

            if (entireGoalCntPerTags.keySet().contains(tag)) {
                entireGoalCntPerTags.put(tag, entireGoalCntPerTags.get(tag) + 1);
            } else {
                entireGoalCntPerTags.put(tag, 1);
            }

            if (!achievementCntPerTags.keySet().contains(tag))
                achievementCntPerTags.put(tag, 0);

            if (goalModel.isAchieved())
                achievementCntPerTags.put(tag, achievementCntPerTags.get(tag) + 1);
        }

        ArrayList<Pair<String, HashMap<String, Object>>> achievementRates = new ArrayList<>();
        for (String tag : entireGoalCntPerTags.keySet()) {
            int achievedGoalCnt = achievementCntPerTags.get(tag);
            int entireGoalCnt = entireGoalCntPerTags.get(tag);

            if (entireGoalCnt == 0)
                continue;

            double achievementRate = (achievedGoalCnt / ((double) entireGoalCnt)) * 100;

            HashMap<String, Object> statInfo = new HashMap<>();
            statInfo.put("achievementRate", achievementRate);
            statInfo.put("totalGoalCnt", entireGoalCnt);
            achievementRates.add(new Pair<>(tag, statInfo));
        }

        return achievementRates;
    }
}
