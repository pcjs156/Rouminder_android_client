package com.example.rouminder.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.rouminder.fragments.GoalDescribeFragment;
import com.example.rouminder.fragments.GoalFragment;
import com.example.rouminder.fragments.ProfileFragment;
import com.example.rouminder.R;
import com.example.rouminder.fragments.StatisticsFragment;
import com.example.rouminder.firebase.manager.BaseModelManager;
import com.example.rouminder.firebase.manager.CategoryModelManager;
import com.example.rouminder.firebase.manager.GoalModelManager;
import com.example.rouminder.firebase.model.CategoryModel;
import com.example.rouminder.firebase.model.GoalModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
        CategoryModelManager ctManager = CategoryModelManager.getInstance();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.main_menu_item_goal:
                    CategoryModel newCategory = ctManager.create("CATEGORY_1");
                    String categoryId = newCategory.id;

                    GoalModel newGoal = gManager.create(categoryId, "GOAL_NAME_1", "GOAL_TYPE_1",
                            0, "2021.11.15/16:45:25", "2021.11.16/16:45:25");
                    String goalId = newGoal.id;

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