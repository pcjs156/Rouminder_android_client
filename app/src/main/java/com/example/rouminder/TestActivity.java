package com.example.rouminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.rouminder.data.goalsystem.goal.Goal;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {
    // 리사이클러뷰 동작을 시험해볼 액티비티
    // 기능 개발이 끝나면 삭제 요망

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // 데이터 생성
        ArrayList<GoalItem> list = new ArrayList<>();
        list.add(new GoalItem("밥먹기"
                ,"2시간 남음"
                ,"90%"
                ,"~오늘 23:59"
                , true));
        list.add(new GoalItem("숨쉬기"
                ,"3시간 남음"
                ,"80%"
                ,"~오늘 22:59"
                , false));
        list.add(new GoalItem( "잠자기"
                ,"4시간 남음"
                ,"70%"
                ,"~오늘 21:59"
                , false));


        // 어댑터 객체 생성
        GoalAdapter adapter = new GoalAdapter(list);
        RecyclerView recyclerView = findViewById(R.id.viewGoal);
        
        // 어댑터 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);




    }
}