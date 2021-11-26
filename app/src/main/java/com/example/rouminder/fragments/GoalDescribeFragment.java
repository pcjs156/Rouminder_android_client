package com.example.rouminder.fragments;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.rouminder.MainApplication;
import com.example.rouminder.R;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;

import javax.annotation.Nullable;

public class GoalDescribeFragment extends DialogFragment{
    Goal goal;

    public GoalDescribeFragment(Goal goal) {
        this.goal = goal;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal_describe, container);
        Button buttonGoalChange = (Button) v.findViewById(R.id.buttonGoalChange);
        Button buttonGoalDelete = (Button) v.findViewById(R.id.buttonGoalDelete);
        TextView textViewName = (TextView) v.findViewById(R.id.textViewName);
        TextView textViewTime = (TextView) v.findViewById(R.id.textViewTime);
        TextView textViewProgress = (TextView) v.findViewById(R.id.textViewProgress);

        String name = "Goal Name : " + goal.getName();
        String time = "Start/End : " + goal.getStartTime().toString() + "/" + goal.getEndTime().toString();
        String progress = "Current/Target : " + goal.currentToString() + "/" + goal.targetToString();

        textViewName.setText(name);
        textViewTime.setText(time);
        textViewProgress.setText(progress);

        buttonGoalChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("GoalDescribeFragment", "Change");
                dismiss();
            }
        });

        buttonGoalDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("GoalDescribeFragment", "Delete Button Clicked");
                GoalManager goalManager = ((MainApplication) getActivity().getApplication()).getGoalManager();
                int id = goal.getId();
                Log.d("GoalDescribeFragment", Integer.toString(id));
                goalManager.removeGoal(id);
                Log.d("GoalDescribeFragment", goalManager.getGoals().toString());
                dismiss();
            }
        });
        setCancelable(false);
        return v;
    }
}
