package com.example.rouminder.fragments;

import static com.example.rouminder.data.goalsystem.GoalManager.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rouminder.MainApplication;
import com.example.rouminder.R;
import com.example.rouminder.activities.AddGoalActivity;
import com.example.rouminder.adapter.BigGoalAdapter;
import com.example.rouminder.adapter.MiniGoalAdapter;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;
import com.example.rouminder.firebase.manager.GoalModelManager;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;
import com.nex3z.togglebuttongroup.button.CircularToggle;

import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class GoalFragment extends Fragment {
    GoalManager goalManager;
    private Context context;

    public static GoalFragment me;

    SingleSelectToggleGroup domainToggleGroup;

    BigGoalAdapter bigGoalAdapter;
    MiniGoalAdapter miniGoalAdapter;

    TextView progress;
    TextView progressStr;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        goalManager = ((MainApplication) getActivity().getApplication()).getGoalManager();

        me = this;

        ImageView btnAddGoal = (ImageView) rootView.findViewById(R.id.btnAddGoal);

        progress = (TextView) rootView.findViewById(R.id.entireGoalProgress);
        progressStr = (TextView) rootView.findViewById(R.id.entireGoalProgressStr);

        domainToggleGroup = (SingleSelectToggleGroup) rootView.findViewById(R.id.groupChoices);
        CircularToggle choiceDay = (CircularToggle) rootView.findViewById(R.id.choiceDay);
        CircularToggle choiceWeek = (CircularToggle) rootView.findViewById(R.id.choiceWeek);
        CircularToggle choiceMonth = (CircularToggle) rootView.findViewById(R.id.choiceMonth);

//        LinearLayout weeklyCalendar = (LinearLayout) rootView.findViewById(R.id.weeklyCalendar);
//        CardView monthlyCalendar = (CardView) rootView.findViewById(R.id.monthlyCalendar);

        bigGoalAdapter = new BigGoalAdapter(getActivity(), ((MainApplication) context.getApplicationContext()).getGoalManager(), getCheckedDomain(), getSelectedComparator());
        miniGoalAdapter = new MiniGoalAdapter(getActivity(), ((MainApplication) context.getApplicationContext()).getGoalManager(), getCheckedDomain(), getSelectedComparator());

        setProgress();

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.viewGoal);
        recyclerView.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        recyclerView.setAdapter(bigGoalAdapter);


        RecyclerView miniRecyclerView = (RecyclerView) rootView.findViewById(R.id.lstMiniGoal);
        miniRecyclerView.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        miniRecyclerView.setAdapter(miniGoalAdapter);

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

//                weeklyCalendar.setVisibility(View.GONE);
//                monthlyCalendar.setVisibility(View.GONE);

                bigGoalAdapter.setDomain(Domain.DAY);
                miniGoalAdapter.setDomain(Domain.DAY);
            }
        });
        choiceWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context.getApplicationContext(), "weekly calendar", Toast.LENGTH_SHORT).show();

//                weeklyCalendar.setVisibility(View.VISIBLE);
//                monthlyCalendar.setVisibility(View.GONE);

                bigGoalAdapter.setDomain(Domain.WEEK);
                miniGoalAdapter.setDomain(Domain.WEEK);
            }
        });
        choiceMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context.getApplicationContext(), "monthly calendar", Toast.LENGTH_SHORT).show();

//                weeklyCalendar.setVisibility(View.GONE);
//                monthlyCalendar.setVisibility(View.VISIBLE);

                bigGoalAdapter.setDomain(Domain.MONTH);
                miniGoalAdapter.setDomain(Domain.MONTH);
            }
        });

        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                Log.d("test", "aaa");
                bigGoalAdapter.notifyDataSetChanged();
                return false;
            }
        });

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };
        new Timer().schedule(task, 0, 30 * 1000);

        return rootView;
    }

    private Domain getCheckedDomain() {
        int id = domainToggleGroup.getCheckedId();
        Domain domain;
        if(id == R.id.choiceDay) {
            domain = Domain.DAY;
        } else if(id == R.id.choiceWeek){
            domain = Domain.WEEK;
        } else if(id == R.id.choiceMonth) {
            domain = Domain.MONTH;
        } else {
            domain = Domain.DAY;
        }
        return domain;
    }

    // currently returning default comparator; by end time
    private Comparator<Goal> getSelectedComparator() {
        return new Comparator<Goal>() {
            @Override
            public int compare(Goal g1, Goal g2) {
                return g1.getEndTime().compareTo(g2.getEndTime());
            }
        };
    }

    public void setProgress() {
        progressStr.setText(bigGoalAdapter.getProgressString());
        progress.setText(Double.toString(bigGoalAdapter.getProgress())+"%");
    }
}