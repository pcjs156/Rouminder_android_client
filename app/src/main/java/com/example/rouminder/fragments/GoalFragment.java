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
import com.example.rouminder.adapter.BigGoalAdapter;
import com.example.rouminder.R;
import com.example.rouminder.activities.AddGoalActivity;
import com.example.rouminder.adapter.MiniGoalAdapter;
import com.example.rouminder.data.goalsystem.CheckGoal;
import com.example.rouminder.data.goalsystem.CountGoal;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;
import com.nex3z.togglebuttongroup.button.CircularToggle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class GoalFragment extends Fragment {
    GoalManager goalManager;

    BigGoalAdapter bAdapter;
    MiniGoalAdapter mAdapter;

    RecyclerView recyclerView;
    RecyclerView miniRecyclerView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_goal, container, false);

        goalManager =  ((Application) getActivity().getApplication()).getGoalManager();

        // if goalManager's goals is empty, create goals list
        if (goalManager.goals.size() == 0) initGoalManager();

        ImageView btnAddGoal = (ImageView) rootView.findViewById(R.id.btnAddGoal);

        CircularToggle choiceDay = (CircularToggle) rootView.findViewById(R.id.choiceDay);
        CircularToggle choiceWeek = (CircularToggle) rootView.findViewById(R.id.choiceWeek);
        CircularToggle choiceMonth = (CircularToggle) rootView.findViewById(R.id.choiceMonth);

        LinearLayout weeklyCalendar = (LinearLayout) rootView.findViewById(R.id.weeklyCalendar);
        CardView monthlyCalendar = (CardView) rootView.findViewById(R.id.monthlyCalendar);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.viewGoal);
        miniRecyclerView = (RecyclerView) rootView.findViewById(R.id.lstMiniGoal);

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

        // items 임시 생성 코드
        ArrayList<Goal> items = new ArrayList<>();
        goalManager.goals.forEach((idx, goal)->{
            items.add(goal);
        });

        // items 생성 코드
//        ArrayList<Goal> items = new ArrayList(goalManager.getGoals(LocalDateTime.now(), null, null));

        if (bAdapter == null) setBAdapter(items);
        if (mAdapter == null) setMAdapter(items);

        return rootView;
    }

    void initGoalManager() {
        LocalDateTime from = LocalDateTime.now();

        // 임시 코드 -> open 방식 사용하려면 to 빈 값이 들어가게?
        // 현재는 to 값에 null 못 들어갈 것 같음. 나중에 정하기
        LocalDateTime to = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59));

        // 데이터 생성
        goalManager.addGoal(new CheckGoal(goalManager, 0, "밥 먹기", from, to, 0));
        goalManager.addGoal(new CountGoal(goalManager, 1, "물 마시기", from, to, 1, 5, "회"));
        goalManager.addGoal(new CheckGoal(goalManager, 2, "한강 가기", from, to, 1));
    }

    void setBAdapter(ArrayList<Goal> items) {
        bAdapter = new BigGoalAdapter(goalManager, items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(bAdapter);
    }

    /**
     * set/reset mini goal adapter with goals
     * when click week and monthly calendar, reset mini goal adapter
     *
     * @param items goals to show at mini goal recyclerView
     */
    public void setMAdapter(ArrayList<Goal> items) {
        mAdapter = new MiniGoalAdapter(goalManager, items);
        miniRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        miniRecyclerView.setAdapter(mAdapter);
    }
}