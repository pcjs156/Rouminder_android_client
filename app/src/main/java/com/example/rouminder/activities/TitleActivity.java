package com.example.rouminder.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.rouminder.R;

public class TitleActivity extends AppCompatActivity {
    LinearLayout titleViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        SharedPreferences prefs = getSharedPreferences("global", MODE_PRIVATE);
        String uid = prefs.getString("uid", null);
        boolean isLoggedBefore = uid != null;

        titleViewContainer = (LinearLayout) findViewById(R.id.titleViewContainer);
        titleViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedBefore) {
                    Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainActivityIntent);
                } else {
                    Intent loginActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginActivityIntent);
                }
            }
        });
    }
}