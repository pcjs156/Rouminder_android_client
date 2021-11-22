package com.example.rouminder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;

import java.time.LocalDateTime;
import java.util.Calendar;

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
                alarmTest();

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

    private void alarmTest() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE); // 알람 매니저 초기화
        GoalManager goalManager = new GoalManager();

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0); // 인덴트 생성

        // 현재 시간 받아오기
        LocalDateTime end = LocalDateTime.now();
        // 현재 시간에서 5분 20초 추가
        end = end.plusMinutes(5);
        end = end.plusSeconds(10);

        // 목표 설정
        Goal goal = new Goal(goalManager,0,"응애",LocalDateTime.now(), end,10,100);
        goalManager.addGoal(goal);

        // 목표에서 마감 시간 받아옴.
        LocalDateTime end2 = goal.getEndTime();
        // 목표에서 5분 차감,
        end2 = end2.minusMinutes(5);
        // 캘린더 객체에 목표 시간 적용
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, end2.getYear());
        calendar.set(Calendar.MONTH, end2.getMonthValue() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, end2.getDayOfMonth() - 1);
        calendar.set(Calendar.HOUR, end2.getHour());
        calendar.set(Calendar.MINUTE, end2.getMinute());
        calendar.set(Calendar.SECOND, end2.getSecond());

        Long aa = calendar.getTimeInMillis();
        Log.i("time", Long.toString(aa));

        // 5분 차감된 시간으로 브로드캐스트를 울리도록 알아서 설정.
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() , pendingIntent); // 해당 시간으로 알림

    }
}