package com.example.rouminder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.rouminder.R;
import com.example.rouminder.data.goalsystem.Goal;

import javax.annotation.Nullable;

public class GoalDescribeFragment extends DialogFragment implements View.OnClickListener {
    Goal goal;
    public GoalDescribeFragment(Goal goal) {
        this.goal = goal;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal_describe, container);
        Button buttonConfirm = (Button) v.findViewById(R.id.buttonConfirm);
        TextView textViewName = (TextView) v.findViewById(R.id.textViewName);
        TextView textViewTime = (TextView) v.findViewById(R.id.textViewTime);
        TextView textViewProgress = (TextView) v.findViewById(R.id.textViewProgress);

        String name = "Goal Name : " + goal.getName();
        String time = "Start/End : " + goal.getStartTime().toString() + "/" + goal.getEndTime().toString();
        String progress = "Current/Target : " + goal.currentToString() + "/" + goal.targetToString();

        textViewName.setText(name);
        textViewTime.setText(time);
        textViewProgress.setText(progress);

        buttonConfirm.setOnClickListener(this);
        setCancelable(false);
        return v;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
