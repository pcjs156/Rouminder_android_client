package com.example.rouminder.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.rouminder.firebase.manager.BaseModelManager;
import com.example.rouminder.firebase.manager.GoalModelManager;
import com.example.rouminder.firebase.model.GoalModel;
import com.example.rouminder.fragments.GoalFragment;
import com.example.rouminder.fragments.ProfileFragment;
import com.example.rouminder.R;
import com.example.rouminder.fragments.StatisticsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    String uid;

    BottomNavigationView bottomNavigationView;

    GoalFragment goalFragment;
    StatisticsFragment statisticsFragment;
    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("global", MODE_PRIVATE);
        uid = prefs.getString("uid", null);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);

        goalFragment = new GoalFragment();
        statisticsFragment = new StatisticsFragment();
        profileFragment = new ProfileFragment();

        //첫 화면 띄우기
        getSupportFragmentManager().beginTransaction().add(R.id.mainLayoutContainer, goalFragment).commit();

        BaseModelManager.setUid(uid);
        BaseModelManager baseModelManager = BaseModelManager.getInstance();

        GoalModelManager gManager = GoalModelManager.getInstance();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.main_menu_item_goal:
                    // 이거 test code인가?
                    HashMap<String, Object> values = new HashMap<>();
                    values.put("name", "GOAL_NAME_1");
                    values.put("type", "GOAL_TYPE_1");
                    values.put("current", 0);
                    values.put("tag", "GOAL_TAG_1");
                    values.put("method", "METHOD_1");
                    values.put("highlight", "COLOR_1");
                    values.put("start_datetime", "2021.11.15/16:45:25");
                    values.put("finish_datetime", "2021.11.16/16:45:25");
                    GoalModel newGoal = gManager.create(values);

                    getSupportFragmentManager().beginTransaction().replace(R.id.mainLayoutContainer, goalFragment).commit();
                    break;
                case R.id.main_menu_item_statistics:
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainLayoutContainer, statisticsFragment).commit();
                    break;
                case R.id.main_menu_item_profile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainLayoutContainer, profileFragment).commit();
                    break;
                default:
                    return false;
            }

            return true;
        });
    }

}