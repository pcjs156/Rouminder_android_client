package com.example.rouminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.rouminder.firebase.manager.ActionManager;
import com.example.rouminder.firebase.manager.BaseModelManager;
import com.example.rouminder.firebase.manager.ConditionManager;
import com.example.rouminder.firebase.model.ActionModel;
import com.example.rouminder.firebase.model.ConditionModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

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

        ActionManager aManager = ActionManager.getInstance();
        ConditionManager cManager = ConditionManager.getInstance();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.main_menu_item_goal:
                    ActionModel newAction = aManager.create("ACTION_TYPE_1", "UNIT_1");
                    String actionId = newAction.id;

                    ConditionModel newCondition = cManager.create(actionId, "COND_TYPE_1");
                    String conditionId = newCondition.id;

                    String categoryId = baseModelManager.createCategory("CATEGORY_1");
                    String goalId = baseModelManager.createGoal(conditionId, categoryId, "GOAL_NAME_1");

                    aManager.syncActionModels();
                    ArrayList<ActionModel> actions = aManager.getActionModels();
                    for(ActionModel action: actions) {
                        Log.d("Hello", action.toString(true));
                    }

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