package com.example.rouminder;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nex3z.togglebuttongroup.button.CircularToggle;

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
                Toast.makeText(getActivity().getApplicationContext(), "로그인", Toast.LENGTH_SHORT).show();

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

        return rootView;
    }
}