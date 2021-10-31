package com.example.rouminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    GoalFragment goalFragment;
    StatisticsFragment statisticsFragment;
    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);

        goalFragment = new GoalFragment();
        statisticsFragment = new StatisticsFragment();
        profileFragment = new ProfileFragment();

        //첫 화면 띄우기
        getSupportFragmentManager().beginTransaction().add(R.id.mainLayoutContainer, goalFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.main_menu_item_goal:
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