package com.example.rouminder.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.rouminder.MainApplication;
import com.example.rouminder.R;
import com.example.rouminder.data.goalsystem.Goal;

import org.w3c.dom.Text;

import javax.annotation.Nullable;

public class GoalModifyFragment extends DialogFragment {
    int goalID;
    public GoalModifyFragment(Goal goal) { this.goalID = goal.getId(); }
    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal_modify, container);
        EditText editTextGoalName = v.findViewById(R.id.editTextGoalName);
        Button buttonConfirm = v.findViewById(R.id.buttonConfirm);
        Button buttonCancel = v.findViewById(R.id.buttonCancel);
        TextView textViewGoalStartTimeData = v.findViewById(R.id.textViewGoalStartTimeData);
        TextView textViewGoalEndTimeData = v.findViewById(R.id.textViewGoalEndTimeData);

        Goal goal = ((MainApplication)getActivity().getApplication()).getGoalManager().getGoal(goalID);
        editTextGoalName.setText(goal.getName());
        textViewGoalStartTimeData.setText(goal.getStartTime().toString());
        textViewGoalEndTimeData.setText(goal.getEndTime().toString());

        textViewGoalStartTimeData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("GoalModifyFragment", "StartTime Clicked");
            }
        });

        textViewGoalEndTimeData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("GoalModifyFragment", "EndTime Clicked");
            }
        });


        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return v;
    }
}
