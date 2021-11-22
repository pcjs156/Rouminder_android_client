package com.example.rouminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.rouminder.firebase.Manager;
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

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.main_menu_item_goal:
                    Manager manager = Manager.getInstance();
                    manager.createAction(uid, "TYPE1", "unit1");
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