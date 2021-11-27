package com.example.rouminder.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleObserver;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.rouminder.MainApplication;
import com.example.rouminder.ProgressDialog;
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
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;
import com.nex3z.togglebuttongroup.button.CircularToggle;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class GoalFragment extends Fragment {
    private GoalModelManager goalModelManager;
    private Context context;

    ImageView btnAddGoal;
    SingleSelectToggleGroup domainToggleGroup;

    RecyclerView recyclerView;
    RecyclerView miniRecyclerView;


    static InitGoalsTask task;

    BigGoalAdapter bigGoalAdapter;
    MiniGoalAdapter miniGoalAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(goalModelManager == null) goalModelManager = GoalModelManager.getInstance();
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context instanceof Activity ? context : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_goal, container, false);

        btnAddGoal = (ImageView) rootView.findViewById(R.id.btnAddGoal);

        domainToggleGroup = (SingleSelectToggleGroup) rootView.findViewById(R.id.groupChoices);
        CircularToggle choiceDay = (CircularToggle) rootView.findViewById(R.id.choiceDay);
        CircularToggle choiceWeek = (CircularToggle) rootView.findViewById(R.id.choiceWeek);
        CircularToggle choiceMonth = (CircularToggle) rootView.findViewById(R.id.choiceMonth);

        LinearLayout weeklyCalendar = (LinearLayout) rootView.findViewById(R.id.weeklyCalendar);
        CardView monthlyCalendar = (CardView) rootView.findViewById(R.id.monthlyCalendar);

        bigGoalAdapter = new BigGoalAdapter(((MainApplication)context.getApplicationContext()).getGoalManager(), getCheckedDomain(), getSelectedComparator());
        miniGoalAdapter = new MiniGoalAdapter(((MainApplication)context.getApplicationContext()).getGoalManager(), getCheckedDomain(), getSelectedComparator());

        recyclerView = (RecyclerView) rootView.findViewById(R.id.viewGoal);
        recyclerView.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        recyclerView.setAdapter(bigGoalAdapter);

        miniRecyclerView = (RecyclerView) rootView.findViewById(R.id.lstMiniGoal);
        recyclerView.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        recyclerView.setAdapter(bigGoalAdapter);

        btnAddGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context.getApplicationContext(), "목표 추가 창", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context.getApplicationContext(), AddGoalActivity.class);
                startActivity(intent);
            }
        });

        choiceDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context.getApplicationContext(), "daily calendar", Toast.LENGTH_SHORT).show();

                weeklyCalendar.setVisibility(View.GONE);
                monthlyCalendar.setVisibility(View.GONE);
            }
        });
        choiceWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context.getApplicationContext(), "weekly calendar", Toast.LENGTH_SHORT).show();

                weeklyCalendar.setVisibility(View.VISIBLE);
                monthlyCalendar.setVisibility(View.GONE);
            }
        });
        choiceMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context.getApplicationContext(), "monthly calendar", Toast.LENGTH_SHORT).show();

                weeklyCalendar.setVisibility(View.GONE);
                monthlyCalendar.setVisibility(View.VISIBLE);
            }
        });

        // firebase 연동 데이터 생성
        if (task == null) {
            initFirebaseDate();
        }

        return rootView;
    }

    private GoalManager.Domain getCheckedDomain() {
        int id = domainToggleGroup.getCheckedId();
        GoalManager.Domain domain;
        if(id == R.id.choiceDay) {
            domain = GoalManager.Domain.DAY;
        } else if(id == R.id.choiceWeek){
            domain = GoalManager.Domain.WEEK;
        } else if(id == R.id.choiceMonth) {
            domain = GoalManager.Domain.MONTH;
        } else {
            domain = GoalManager.Domain.ALL;
        }
        return domain;
    }
    // currently returning default comparator; by endtime
    private Comparator<Goal> getSelectedComparator() {
        return new Comparator<Goal>() {
            @Override
            public int compare(Goal g1, Goal g2) {
                return g1.getEndTime().compareTo(g2.getEndTime());
            }
        };
    }

    private void initFirebaseDate() {
        task = new InitGoalsTask();
        task.execute(goalManager);
    }

    class InitGoalsTask extends AsyncTask<GoalManager, Integer ,ArrayList<GoalModel>> {

        @Override
        protected ArrayList<GoalModel> doInBackground(GoalManager... goalManagers) {
            final ProgressDialog[] customProgressDialog = new ProgressDialog[1];

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    customProgressDialog[0] = new ProgressDialog(getActivity());
                    customProgressDialog[0].show();
                    customProgressDialog[0].getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                }
            });

            while (goalModelManager.getIsChanging() == true) {
                // wait for firebase loading
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    customProgressDialog[0].cancel();
                }
            });

            return goalModelManager.get();
        }

        @Override
        protected void onPostExecute(ArrayList<GoalModel> goalModels) {
            super.onPostExecute(goalModels);

            goalModels.forEach(goalModel -> {
                Log.i("test", goalModel.toString());
                goalManager.addGoal(convertGoalModelToGoal(goalModel));
            });

            if (goalManager.getGoal(1) == null) {
                goalManager.addGoal(new CheckGoal(goalManager, -1, "한강 가기",
                        LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).minusMinutes(5),
                        LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(7), 1));
            }
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
}