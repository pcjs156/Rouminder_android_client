package com.example.rouminder;

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

import com.nex3z.togglebuttongroup.button.CircularToggle;

import java.util.ArrayList;

public class GoalFragment extends Fragment {

    ImageView btnAddGoal;

    CircularToggle choiceDay;
    CircularToggle choiceWeek;
    CircularToggle choiceMonth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_goal, container, false);

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
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.viewGoal);

        // 어댑터 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}