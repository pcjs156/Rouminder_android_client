package com.example.rouminder.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.example.rouminder.R;
import com.example.rouminder.data.goalsystem.CheckGoal;
import com.example.rouminder.data.goalsystem.CountGoal;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;
import com.example.rouminder.data.goalsystem.LocationGoal;
import com.example.rouminder.fragments.GoalDescribeFragment;

import java.util.Comparator;

public class MiniGoalAdapter extends BaseGoalAdapter<MiniGoalAdapter.ViewHolder> {
    FragmentActivity activity;

    public MiniGoalAdapter(FragmentActivity activity, GoalManager goalManager, GoalManager.Domain domain, Comparator<Goal> comparator) {
        super(goalManager, domain, comparator);
        this.activity = activity;
    }

    @NonNull
    @Override
    public MiniGoalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.item_minigoal, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.onBind(items.get(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout goalBox;
        TextView goalContent;
        TextView goalSubText;
        ImageView goalImgCheckBox;
        CircleProgressBar goalProgressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            goalBox = itemView.findViewById(R.id.mini_goal);
            goalContent = itemView.findViewById(R.id.miniGoalContent);
            goalSubText = itemView.findViewById(R.id.miniGoalSubText);
            goalProgressBar = itemView.findViewById(R.id.miniGoalProgressBar);
            goalImgCheckBox = itemView.findViewById(R.id.miniGoalImgCheckBox);
        }

        void onBind(Goal goal) {
            goalContent.setText(goal.getName());
            goalSubText.setText(goal.progressToString());

            if (goal.getType().equals(Goal.Type.LOCATION.name())) {
                goalProgressBar.setVisibility(View.GONE);
                goalImgCheckBox.setVisibility(View.VISIBLE);

                if (((LocationGoal) goal).getChecked())
                    goalImgCheckBox.setImageResource(R.drawable.checkbox_on_background);
                else goalImgCheckBox.setImageResource(R.drawable.checkbox_off_background);

            } else if (goal.getType().equals(Goal.Type.CHECK.name())) {
                goalProgressBar.setVisibility(View.GONE);
                goalImgCheckBox.setVisibility(View.VISIBLE);

                CheckGoal checkGoal = (CheckGoal) goal;

                if (checkGoal.getChecked())
                    goalImgCheckBox.setImageResource(R.drawable.checkbox_on_background);
                else goalImgCheckBox.setImageResource(R.drawable.checkbox_off_background);

                goalImgCheckBox.setClickable(true);
                goalImgCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (((CheckGoal) goal).getChecked()) {
                            goalImgCheckBox.setImageResource(R.drawable.checkbox_off_background);
                            ((CheckGoal) goal).setChecked(false);
                        } else {
                            goalImgCheckBox.setImageResource(R.drawable.checkbox_on_background);
                            ((CheckGoal) goal).setChecked(true);
                        }
                    }
                });
                goalBox.setClickable(true);
                goalBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("TestCode", "FragmentGoalDescribe Start");
                        GoalDescribeFragment goalDescribeFragment = new GoalDescribeFragment(goal);
                        goalDescribeFragment.show(activity.getSupportFragmentManager(),null);
                    }
                });
            } else {
                goalImgCheckBox.setVisibility(View.GONE);
                goalProgressBar.setVisibility(View.VISIBLE);
                goalProgressBar.setMax(goal.getTarget());
                goalProgressBar.setProgress(((CountGoal) goal).getCount());
            }

            goalBox.setClickable(true);
            goalBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("TestCode", "FragmentGoalDescribe Start");
                    GoalDescribeFragment goalDescribeFragment = new GoalDescribeFragment(goal);
                    goalDescribeFragment.show(activity.getSupportFragmentManager(),null);
                }
            });
        }
    }
}
