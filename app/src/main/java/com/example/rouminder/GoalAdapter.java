package com.example.rouminder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.example.rouminder.data.goalsystem.CheckGoal;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;

import java.time.LocalDateTime;
import java.util.ArrayList;


public class GoalAdapter extends RecyclerView.Adapter<DefaultGoalHolder> {
//    static ArrayList<GoalItem> list;
    static GoalManager goalManager;

    final int bigGoalRecycler = R.id.viewGoal;
    final int miniGoalRecycler = R.id.lstMiniGoal;

//    GoalAdapter(ArrayList<GoalItem> list) {
//        this.list = list;
//    }

    GoalAdapter(GoalManager goalManager) { super(); this.goalManager = goalManager; }

    @NonNull
    @Override
    public DefaultGoalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        // big goal
        if (parent.getId() == bigGoalRecycler) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_goal, parent, false);
            return new BigGoalHolder(goalManager, view);
        }
        else if (parent.getId() == miniGoalRecycler) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_minigoal, parent, false);
            return new MiniGoalHolder(goalManager, view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull DefaultGoalHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.i("tag", "onBindViewHolder");

        Goal goal = goalManager.getGoal(position);

        if (holder instanceof BigGoalHolder) {
            BigGoalHolder bigGoalHolder = (BigGoalHolder) holder;
            bigGoalHolder.onBind(goal);

            if (goal instanceof CheckGoal) {
                CheckGoal checkGoal = (CheckGoal) goal;

                bigGoalHolder.goalImgCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (position != RecyclerView.NO_POSITION) {
                            if (checkGoal.getChecked())  {
                                bigGoalHolder.goalImgCheckBox.setImageResource(R.drawable.checkbox_off_background);
                                checkGoal.setChecked(false);
                            }
                            else {
                                bigGoalHolder.goalImgCheckBox.setImageResource(R.drawable.checkbox_on_background);
                                checkGoal.setChecked(true);
                            }
                            notifyItemChanged(position);
                        }
                    }
                });
            }
        }
        else if (holder instanceof MiniGoalHolder) {
            MiniGoalHolder miniGoalHolder = (MiniGoalHolder) holder;
            miniGoalHolder.onBind(goal);

            if (goal instanceof CheckGoal) {
                CheckGoal checkGoal = (CheckGoal) goal;

                miniGoalHolder.goalImgCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(position != RecyclerView.NO_POSITION) {
                            if(((CheckGoal) goal).getChecked())  {
                                miniGoalHolder.goalImgCheckBox.setImageResource(R.drawable.checkbox_off_background);
                                ((CheckGoal) goal).setChecked(false);
                            }
                            else {
                                miniGoalHolder.goalImgCheckBox.setImageResource(R.drawable.checkbox_on_background);
                                ((CheckGoal) goal).setChecked(true);
                            }
                            notifyItemChanged(position);
                        }
                    }
                });
            }

        }
    }

    @Override
    public int getItemCount() {
        return goalManager.goals.size();
    }

}
