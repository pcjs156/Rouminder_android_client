package com.example.rouminder.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.rouminder.MainApplication;
import com.example.rouminder.adapter.BigGoalAdapter;
import com.example.rouminder.R;
import com.example.rouminder.activities.AddGoalActivity;
import com.example.rouminder.adapter.MiniGoalAdapter;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;
import com.nex3z.togglebuttongroup.button.CircularToggle;

import java.util.ArrayList;
import java.util.List;

public class GoalFragment extends Fragment {
    GoalManager goalManager;
    RecyclerView miniRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_goal, container, false);

        goalManager = ((MainApplication) getActivity().getApplication()).getGoalManager();

        ImageView btnAddGoal = (ImageView) rootView.findViewById(R.id.btnAddGoal);

        CircularToggle choiceDay = (CircularToggle) rootView.findViewById(R.id.choiceDay);
        CircularToggle choiceWeek = (CircularToggle) rootView.findViewById(R.id.choiceWeek);
        CircularToggle choiceMonth = (CircularToggle) rootView.findViewById(R.id.choiceMonth);

        LinearLayout weeklyCalendar = (LinearLayout) rootView.findViewById(R.id.weeklyCalendar);
        CardView monthlyCalendar = (CardView) rootView.findViewById(R.id.monthlyCalendar);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.viewGoal);
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
                weeklyCalendar.setVisibility(View.GONE);
                monthlyCalendar.setVisibility(View.GONE);
            }
        });
        choiceWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weeklyCalendar.setVisibility(View.VISIBLE);
                monthlyCalendar.setVisibility(View.GONE);
            }
        });
        choiceMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weeklyCalendar.setVisibility(View.GONE);
                monthlyCalendar.setVisibility(View.VISIBLE);
            }
        });

        // adapter 설정
        BigGoalAdapter bAdapter = new BigGoalAdapter(getActivity(), goalManager, goalManager.getGoals());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(bAdapter);

        setMAdapter(goalManager.getGoals());

        return rootView;
    }

    /**
     * set/reset mini goal adapter with goals
     * when click week and monthly calendar, reset mini goal adapter
     *
     * @param items goals to show at mini goal recyclerView
     */
    public void setMAdapter(List<Goal> items) {
        MiniGoalAdapter mAdapter = new MiniGoalAdapter(getActivity(), goalManager, items);
        miniRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        miniRecyclerView.setAdapter(mAdapter);
    }
}