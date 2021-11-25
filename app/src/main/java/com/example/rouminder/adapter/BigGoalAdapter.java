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
import androidx.recyclerview.widget.RecyclerView;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.example.rouminder.R;
import com.example.rouminder.data.goalsystem.CheckGoal;
import com.example.rouminder.data.goalsystem.CountGoal;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;
import com.example.rouminder.data.goalsystem.LocationGoal;

import java.util.ArrayList;
import java.util.List;


public class BigGoalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Goal> items;

    public BigGoalAdapter(GoalManager goalManager, List<Goal> items) {
        super();
        this.items = items;

        goalManager.setOnGoalChangeListener(new GoalManager.OnGoalChangeListener() {
            @Override
            public void onGoalAdd(int id) {
                int position = getItemPosition(id);
                if (position  == -1) {
                    addGoal(goalManager.getGoal(id));
                    notifyItemInserted(position);
                }
            }

            @Override
            public void onGoalUpdate(int id) {
                int position = getItemPosition(id);
                if (position != -1) {
                    notifyItemChanged(position);
                }
            }

            @Override
            public void onGoalRemove(int id) {
                int position = getItemPosition(id);
                if (position != -1) {
                    removeGoal(goalManager.getGoal(id));
                    notifyItemRemoved(position);
                }
            }
        });
    }

    private int getItemPosition(Goal goal) {
        for (int pos = 0 ; pos < items.size(); pos++) {
            if (items.get(pos).getId() == goal.getId()) return pos;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.item_goal, parent, false);
        return new BigGoalHolder(view);
    }

    public void onBindViewHolder(@NonNull BigGoalHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.onBind(items.get(position));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        onBindViewHolder((BigGoalHolder) holder, position);
    }

    private int addGoal(Goal goal) {
        items.add(goal);
    }

    private int removeGoal(Goal goal) {
        int position = items.indexOf(goal);
        items.remove(goal);
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class BigGoalHolder extends RecyclerView.ViewHolder {
        private TextView goalContent;
        private TextView goalRestTime;
        private TextView goalSubText;
        private TextView goalTime;
        private ImageView goalImgCheckBox;
        private CircleProgressBar goalProgressBar;

        public BigGoalHolder(@NonNull View itemView) {
            super(itemView);

            goalContent = itemView.findViewById(R.id.goalContent);
            goalRestTime = itemView.findViewById(R.id.goalRestTime);
            goalSubText = itemView.findViewById(R.id.goalSubText);
            goalTime = itemView.findViewById(R.id.goalTime);
            goalProgressBar = itemView.findViewById(R.id.progressBar);
            goalImgCheckBox = itemView.findViewById(R.id.goalImgCheckBox);
        }

        void onBind(Goal goal) {
            goalContent.setText(goal.getName());
            goalRestTime.setText("rest Time 들어갈 자리");
            goalSubText.setText(goal.progressToString());
            goalTime.setText(goal.getEndTime().toString());

            if (goal instanceof LocationGoal) {
                goalProgressBar.setVisibility(View.GONE);
            } else if (goal instanceof CheckGoal) {
                goalProgressBar.setVisibility(View.GONE);

                CheckGoal checkGoal = (CheckGoal) goal;

                if (checkGoal.getChecked())
                    goalImgCheckBox.setImageResource(R.drawable.checkbox_on_background);
                else goalImgCheckBox.setImageResource(R.drawable.checkbox_off_background);

                goalImgCheckBox.setClickable(true);
                goalImgCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checkGoal.getChecked()) {
                            goalImgCheckBox.setImageResource(R.drawable.checkbox_off_background);
                            checkGoal.setChecked(false);
                        } else {
                            goalImgCheckBox.setImageResource(R.drawable.checkbox_on_background);
                            checkGoal.setChecked(true);
                        }
                    }
                });
            } else {
                goalImgCheckBox.setVisibility(View.GONE);
                goalProgressBar.setMax(goal.getTarget());
                goalProgressBar.setProgress(((CountGoal) goal).getCount());
            }
        }
    }
}
