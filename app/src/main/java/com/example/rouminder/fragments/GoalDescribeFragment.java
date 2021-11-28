package com.example.rouminder.fragments;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.rouminder.MainApplication;
import com.example.rouminder.R;
import com.example.rouminder.data.goalsystem.CheckGoal;
import com.example.rouminder.data.goalsystem.CountGoal;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;

import javax.annotation.Nullable;

public class GoalDescribeFragment extends DialogFragment {
    Goal goal;
    View root;

    public GoalDescribeFragment(Goal goal) {
        this.goal = goal;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_goal_describe, container);

        // radius 조정을 위한 코드
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        ImageView buttonClose = (ImageView) root.findViewById(R.id.buttonClose);
        TextView buttonGoalChange = (TextView) root.findViewById(R.id.buttonGoalChange);
        TextView buttonGoalDelete = (TextView) root.findViewById(R.id.buttonGoalDelete);
        TextView textViewName = (TextView) root.findViewById(R.id.textViewName);
        TextView textViewTime = (TextView) root.findViewById(R.id.textViewTime);
        TextView textViewEndTime = (TextView) root.findViewById(R.id.textViewEndTime);
        TextView textViewProgress = (TextView) root.findViewById(R.id.textViewProgress);

        textViewName.setText(goal.getName());
        textViewTime.setText(goal.getStartTime().toString());
        textViewEndTime.setText(goal.getEndTime().toString());
        textViewProgress.setText(goal.progressToString());

        ImageView neg1 = (ImageView) root.findViewById(R.id.neg1);
        ImageView plus1 = (ImageView) root.findViewById(R.id.plus1);
        TextView location = (TextView) root.findViewById(R.id.location);

        if (goal instanceof CheckGoal) {

        } else if (goal instanceof CountGoal) {
            neg1.setVisibility(View.VISIBLE);
            plus1.setVisibility(View.VISIBLE);
        } else {
            location.setVisibility(View.VISIBLE);
        }

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        buttonGoalChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("GoalDescribeFragment", "Change");
                GoalModifyFragment goalModifyFragment = new GoalModifyFragment(goal);
                goalModifyFragment.show(getActivity().getSupportFragmentManager(), null);
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

        setCancelable(true);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        int width = size.x;
        int height = size.y;

        root.getLayoutParams().width = (int) (width * 0.9);
        root.getLayoutParams().height = (int) (height * 0.8);
    }
}
