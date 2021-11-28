package com.example.rouminder.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;

import com.example.rouminder.MainApplication;
import com.example.rouminder.ProgressDialog;
import com.example.rouminder.data.goalsystem.CheckGoal;
import com.example.rouminder.data.goalsystem.CountGoal;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;
import com.example.rouminder.data.goalsystem.LocationGoal;
import com.example.rouminder.firebase.exception.ModelDoesNotExists;
import com.example.rouminder.firebase.manager.BaseModelManager;
import com.example.rouminder.firebase.manager.GoalModelManager;
import com.example.rouminder.firebase.manager.RepeatPlanModelManager;
import com.example.rouminder.firebase.model.GoalModel;
import com.example.rouminder.fragments.GoalFragment;
import com.example.rouminder.fragments.ProfileFragment;
import com.example.rouminder.R;
import com.example.rouminder.fragments.StatisticsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    GoalManager goalManager;
    GoalModelManager goalModelManager;
    RepeatPlanModelManager repeatPlanModelManager;

    GoalFragment goalFragment;
    StatisticsFragment statisticsFragment;
    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("global", MODE_PRIVATE);
        String uid = prefs.getString("uid", null);
        BaseModelManager.setUid(uid);

        goalModelManager = GoalModelManager.getInstance();
        repeatPlanModelManager = RepeatPlanModelManager.getInstance();
        goalManager = ((MainApplication) getApplication()).getGoalManager();

        // connect goalModel Manager and goal manager
        goalManager.setOnGoalChangeListener(goalManager.new OnGoalChangeListener(){
            @Override
            public void onGoalAdd(int id) {
                // firebase create를 먼저 해야지 unique id를 가져올 수 있으므로
                // 여기는 비움.
            }

            @Override
            public void onGoalUpdate(int id) {
                Goal goal = goalManager.getGoal(id);
                // test code 미리 return
                if (goal.getId() < 1000) {
                    return;
                }

                try {
                    goalModelManager.update(Integer.toString(id), convertGoalToHashMap(goal));
                } catch (ModelDoesNotExists e) {
                    Log.d("model", "goal id, " + id + " does not exist");
                }
            }

            @Override
            public void onGoalRemove(int id) {
                // test code 미리 return
                if (id < 1000) {
                    return;
                }

                try {
                    goalModelManager.delete(Integer.toString(id));
                } catch (ModelDoesNotExists e) {
                    e.printStackTrace();
                    Log.d("model", "goal id, " + id + " does not exist");
                }
            }
        });

        // firebase 연동 데이터 생성
        initFirebaseDate();

        // bottom navigation setting
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);

        FragmentManager fm = getSupportFragmentManager();
        goalFragment = new GoalFragment();
        statisticsFragment = new StatisticsFragment();
        profileFragment = new ProfileFragment();

        // 위치 권한 설정 메시지
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
            }
        }

        //첫 화면 띄우기
        fm.beginTransaction().add(R.id.mainLayoutContainer, goalFragment, "goal").commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.main_menu_item_goal:
                    // show
                    fm.beginTransaction().show(fm.findFragmentByTag("goal")).commit();

                    // hide others
                    if (fm.findFragmentByTag("statistics") != null)
                        fm.beginTransaction().hide(fm.findFragmentByTag("statistics")).commit();
                    if (fm.findFragmentByTag("profile") != null)
                        fm.beginTransaction().hide(fm.findFragmentByTag("profile")).commit();

                    break;
                case R.id.main_menu_item_statistics:
                    // show
                    if (fm.findFragmentByTag("statistics") != null)
                        fm.beginTransaction().show(fm.findFragmentByTag("statistics")).commit();
                    else
                        fm.beginTransaction().add(R.id.mainLayoutContainer, statisticsFragment, "statistics").commit();

                    // hide others
                    if (fm.findFragmentByTag("goal") != null)
                        fm.beginTransaction().hide(fm.findFragmentByTag("goal")).commit();
                    if (fm.findFragmentByTag("profile") != null)
                        fm.beginTransaction().hide(fm.findFragmentByTag("profile")).commit();

                    break;
                case R.id.main_menu_item_profile:
                    // show
                    if (fm.findFragmentByTag("profile") != null)
                        fm.beginTransaction().show(fm.findFragmentByTag("profile")).commit();
                    else
                        fm.beginTransaction().add(R.id.mainLayoutContainer, profileFragment, "profile").commit();

                    // hide others
                    if (fm.findFragmentByTag("goal") != null)
                        fm.beginTransaction().hide(fm.findFragmentByTag("goal")).commit();
                    if (fm.findFragmentByTag("statistics") != null)
                        fm.beginTransaction().hide(fm.findFragmentByTag("statistics")).commit();

                    break;
                default:
                    return false;
            }

            return true;
        });
    }

    private void initFirebaseDate() {
        InitGoalsTask task = new InitGoalsTask();
        task.execute(goalManager);
    }

    class InitGoalsTask extends AsyncTask<GoalManager, Integer ,ArrayList<GoalModel>> {
        @Override
        protected ArrayList<GoalModel> doInBackground(GoalManager... goalManagers) {
            final ProgressDialog[] customProgressDialog = new ProgressDialog[1];

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    customProgressDialog[0] = new ProgressDialog(MainActivity.this);
                    customProgressDialog[0].show();
                    customProgressDialog[0].getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                }
            });

            while (goalModelManager.getIsChanging() == true) {
                // wait for firebase loading
            }

            while (repeatPlanModelManager.getIsChanging() == true) {
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    customProgressDialog[0].cancel();
                }
            });

            return goalModelManager.get();
        }

        @Override
        protected void onPostExecute(ArrayList<GoalModel> goalModels) {
            super.onPostExecute(goalModels);

            goalModels.forEach(goalModel -> {
                Log.i("test", goalModel.toString());
                goalManager.addGoal(convertGoalModelToGoal(goalModel));
            });

//            if (goalManager.getGoal(1) == null) {
//                goalManager.addGoal(new CheckGoal(goalManager, -1, "한강 가기",
//                        LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).minusMinutes(5),
//                        LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(7), 1,
//                        Color.valueOf(Color.parseColor("#ffff0000"))));
//            }
        }
    }

    private Goal convertGoalModelToGoal(GoalModel goalModel) {
        Goal goal;

        HashMap<String, Object> info = goalModel.getInfo();

        DateTimeFormatter formatter = BaseModelManager.getLongTimeFormatter();

        if (info.get("method").equals("check")) {
            goal = new CheckGoal(goalManager,
                    Integer.parseInt(info.get("id").toString()),
                    info.get("name").toString(),
                    LocalDateTime.parse(info.get("start_datetime").toString(), formatter),
                    LocalDateTime.parse(info.get("finish_datetime").toString(), formatter),
                    Integer.parseInt(info.get("current").toString()),
                    Color.valueOf(Color.parseColor(info.get("highlight").toString())));
        } else if (info.get("method").equals("count")) {
            goal = new CountGoal(goalManager,
                    Integer.parseInt(info.get("id").toString()),
                    info.get("name").toString(),
                    LocalDateTime.parse(info.get("start_datetime").toString(), formatter),
                    LocalDateTime.parse(info.get("finish_datetime").toString(), formatter),
                    Integer.parseInt(info.get("current").toString()),
                    Integer.parseInt(info.get("target_count").toString()),
                    info.get("unit").toString(),
                    Color.valueOf(Color.parseColor(info.get("highlight").toString())));
        } else {
            goal = new LocationGoal(goalManager,
                    Integer.parseInt(info.get("id").toString()),
                    info.get("name").toString(),
                    LocalDateTime.parse(info.get("start_datetime").toString(), formatter),
                    LocalDateTime.parse(info.get("finish_datetime").toString(), formatter),
                    Integer.parseInt(info.get("current").toString()),
                    Integer.parseInt(info.get("target_count").toString()),
                    Double.parseDouble(info.get("latitude").toString()),
                    Double.parseDouble(info.get("longitude").toString()),
                    Color.valueOf(Color.parseColor(info.get("highlight").toString())));
        }

        return goal;
    }

    private HashMap<String, Object> convertGoalToHashMap(Goal goal) {
        HashMap<String, Object> values = new HashMap<>();

        DateTimeFormatter formatter = BaseModelManager.getLongTimeFormatter();

        values.put("id", Integer.toString(goal.getId()));
        values.put("name", goal.getName());
        values.put("type", goal.getType());
        values.put("current", goal.getCurrent());
//        values.put("tag", goal.getTag());
        if (goal instanceof CheckGoal) {
            values.put("method", "check");
        } else if (goal instanceof CountGoal) {
            values.put("method", "count");
        } else {
            values.put("method", "location");
        }
        Color color = goal.getHighlight();
        if (color == null) color = Color.valueOf(126,0, 0, 126);
        values.put("highlight", String.format("#%08X", color.toArgb()));
        values.put("target_count", goal.getTarget());
        values.put("start_datetime", goal.getStartTime().format(formatter));
        values.put("finish_datetime", goal.getEndTime().format(formatter));

        return values;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.findFragmentByTag("goal") != null){
            fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag("goal")).commit();
        }
        if(fragmentManager.findFragmentByTag("statistics") != null){
            fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag("statistics")).commit();
        }
        if(fragmentManager.findFragmentByTag("profile") != null) {
            fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag("profile")).commit();
        }
    }
}