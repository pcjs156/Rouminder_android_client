package com.example.rouminder.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.rouminder.Application;
import com.example.rouminder.adapter.GoalAdapter;
import com.example.rouminder.R;
import com.example.rouminder.activities.AddGoalActivity;
import com.example.rouminder.data.goalsystem.CheckGoal;
import com.example.rouminder.data.goalsystem.CountGoal;
import com.example.rouminder.data.goalsystem.GoalManager;
import com.example.rouminder.helpers.GoalNotificationHelper;
import com.nex3z.togglebuttongroup.button.CircularToggle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class GoalFragment extends Fragment {

    GoalNotificationHelper notificationHelper;
    GoalManager goalManager;

    ImageView btnAddGoal;

    CircularToggle choiceDay;
    CircularToggle choiceWeek;
    CircularToggle choiceMonth;

    static GoalAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_goal, container, false);

        goalManager = ((Application) getActivity().getApplication()).getGoalManager();

        btnAddGoal = (ImageView) rootView.findViewById(R.id.btnAddGoal);

        choiceDay = (CircularToggle) rootView.findViewById(R.id.choiceDay);
        choiceWeek = (CircularToggle) rootView.findViewById(R.id.choiceWeek);
        choiceMonth = (CircularToggle) rootView.findViewById(R.id.choiceMonth);

        LinearLayout weeklyCalendar = (LinearLayout) rootView.findViewById(R.id.weeklyCalendar);
        CardView monthlyCalendar = (CardView) rootView.findViewById(R.id.monthlyCalendar);

        btnAddGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "목표 추가 창", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity().getApplicationContext(), AddGoalActivity.class);
                startActivity(intent);
            }
        });

        choiceDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "daily calendar", Toast.LENGTH_SHORT).show();

                weeklyCalendar.setVisibility(View.GONE);
                monthlyCalendar.setVisibility(View.GONE);
            }
        });

        choiceWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "weekly calendar", Toast.LENGTH_SHORT).show();

                weeklyCalendar.setVisibility(View.VISIBLE);
                monthlyCalendar.setVisibility(View.GONE);
            }
        });

        choiceMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "monthly calendar", Toast.LENGTH_SHORT).show();

                weeklyCalendar.setVisibility(View.GONE);
                monthlyCalendar.setVisibility(View.VISIBLE);
            }
        });

        // 만약 adapter init 안 했을 경우 생성
        if (adapter == null) {
            createGoalAdapter();
        }

        // Recycler View 설정
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.viewGoal);
        RecyclerView miniRecyclerView = (RecyclerView) rootView.findViewById(R.id.lstMiniGoal);

        // 어댑터 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(adapter);
        miniRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        miniRecyclerView.setAdapter(adapter);

        // 알람 초기화
        initGoalAlarms();

        return rootView;
    }

    void createGoalAdapter() {
//        from, to는 모두 현재 시간과, 오늘 23:59으로 통일시킵니다.
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59));

        // alarm test를 위한 시간 설정
        LocalDateTime test = LocalDateTime.now();
        test = test.plusMinutes(5);

        // 데이터 생성
        goalManager.addGoal(new CheckGoal(goalManager, 0, "test", from, test, 0));
        goalManager.addGoal(new CheckGoal(goalManager, 1, "밥 먹기", from, to, 0));
        goalManager.addGoal(new CountGoal(goalManager, 2, "물 마시기", from, to, 1, 5, "회"));
        goalManager.addGoal(new CheckGoal(goalManager, 3, "한강 가기", from, to, 1));

        // 어댑터 객체 생성
        adapter = new GoalAdapter(goalManager);
    }

    void initGoalAlarms() {
        notificationHelper = new GoalNotificationHelper(getActivity().getApplicationContext());

        goalManager.goals.forEach((id, goal)-> {
            notificationHelper.registerGoal(goal);
        });
    }
}