package com.example.rouminder.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.example.rouminder.MainApplication;
import com.example.rouminder.adapter.BigGoalAdapter;
import com.example.rouminder.R;
import com.example.rouminder.activities.AddGoalActivity;
import com.example.rouminder.adapter.MiniGoalAdapter;
import com.example.rouminder.data.goalsystem.CheckGoal;
import com.example.rouminder.data.goalsystem.CountGoal;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;
import com.example.rouminder.data.goalsystem.LocationGoal;
import com.example.rouminder.firebase.manager.GoalModelManager;
import com.example.rouminder.firebase.model.GoalModel;
import com.nex3z.togglebuttongroup.button.CircularToggle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class GoalFragment extends Fragment {
    GoalManager goalManager;
    GoalModelManager goalModelManager;

    BigGoalAdapter bAdapter;
    MiniGoalAdapter mAdapter;

    ImageView btnAddGoal;

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

        goalModelManager = GoalModelManager.getInstance();
        goalManager = ((MainApplication) getActivity().getApplication()).getGoalManager();

        btnAddGoal = (ImageView) rootView.findViewById(R.id.btnAddGoal);

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

        // firebase 연동 데이터 생성
        initFirebaseDate();

        // items 임시 생성 코드
        ArrayList<Goal> items = new ArrayList<>();
        goalManager.goals.forEach((idx, goal)->{
            Log.i("test", goal.toString());
            items.add(goal);
        });

        // items 생성 코드
//        ArrayList<Goal> items = new ArrayList(goalManager.getGoals(LocalDateTime.now(), null, null));

        if (bAdapter == null) setBAdapter(items);
        if (mAdapter == null) setMAdapter(items);

        return rootView;
    }

    private void initFirebaseDate() {
        InitGoalsTask task = new InitGoalsTask();
        task.execute(goalManager);
    }

    class InitGoalsTask extends AsyncTask<GoalManager, Integer ,ArrayList<GoalModel>> {

        @Override
        protected ArrayList<GoalModel> doInBackground(GoalManager... goalManagers) {
            try {
                while (goalModelManager.getIsChanging() == true) {
                    Log.i("test", "sleep");
                    Thread.sleep(1000);
                    // 시간 되면 sleep 대신 로딩 중 표시 띄우고 싶음
                }
            } catch (InterruptedException e) {

            }
            return goalModelManager.get();
        }

        @Override
        protected void onPostExecute(ArrayList<GoalModel> goalModels) {
            super.onPostExecute(goalModels);

            goalModels.forEach(goalModel -> {
                Log.i("test", goalModel.toString());
                goalManager.addGoal(convertGoalModelToGoal(goalModel));
            });
        }
    }

    private Goal convertGoalModelToGoal(GoalModel goalModel) {
        Goal goal;

        HashMap<String, Object> info = goalModel.getInfo();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("20yy.MM.dd/HH:mm:ss");

        if (info.get("method").equals("check")) {
            goal = new CheckGoal(goalManager,
                    Integer.parseInt(info.get("id").toString()),
                    info.get("name").toString(),
                    LocalDateTime.parse(info.get("start_datetime").toString(), formatter),
                    LocalDateTime.parse(info.get("finish_datetime").toString(), formatter),
                    Integer.parseInt(info.get("current").toString()));
        } else if (info.get("method").equals("count")) {
            goal = new CountGoal(goalManager,
                    Integer.parseInt(info.get("id").toString()),
                    info.get("name").toString(),
                    LocalDateTime.parse(info.get("start_datetime").toString(), formatter),
                    LocalDateTime.parse(info.get("finish_datetime").toString(), formatter),
                    Integer.parseInt(info.get("current").toString()),
                    Integer.parseInt(info.get("target_count").toString()),
                    info.get("unit").toString());
        } else {
            goal = new LocationGoal(goalManager,
                    Integer.parseInt(info.get("id").toString()),
                    info.get("name").toString(),
                    LocalDateTime.parse(info.get("start_datetime").toString(), formatter),
                    LocalDateTime.parse(info.get("finish_datetime").toString(), formatter),
                    Integer.parseInt(info.get("current").toString()),
                    Integer.parseInt(info.get("target_count").toString()));
        }

        return goal;
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