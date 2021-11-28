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
import androidx.cardview.widget.CardView;
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

import java.util.ArrayList;
import java.util.List;

public class MiniGoalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Goal> items;
    FragmentActivity activity;

    public MiniGoalAdapter(FragmentActivity activity, GoalManager goalManager, List<Goal> items) {
        super();
        this.items = items;
        this.activity = activity;

        goalManager.setOnGoalChangeListener(goalManager.new OnGoalChangeListener() {
            @Override
            public void onGoalAdd(int id) {
                int position = getItemPosition(id);
                if (position == -1) {
                    position = addGoal(goalManager.getGoal(id));
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
                    position = removeGoal(items.get(position));
                    notifyItemRemoved(position);
                }
            }
        });
    }

    private int getItemPosition(int id) {
        Goal goal = items.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
        return (goal == null) ? -1 : items.indexOf(goal);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.item_minigoal, parent, false);
        return new MiniGoalHolder(view);
    }

    public void onBindViewHolder(@NonNull MiniGoalHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.onBind(items.get(position));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        onBindViewHolder((MiniGoalHolder) holder, position);
    }

    private int addGoal(Goal goal) {
        items.add(goal);
        return items.indexOf(goal);
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

    class MiniGoalHolder extends RecyclerView.ViewHolder {
        ConstraintLayout goalBox;
        TextView goalContent;
        TextView goalSubText;
        ImageView goalImgCheckBox;
        CircleProgressBar goalProgressBar;

        public MiniGoalHolder(@NonNull View itemView) {
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
                        if (((CheckGoal) goal).getChecked()) {
                            goalImgCheckBox.setImageResource(R.drawable.checkbox_off_background);
                            ((CheckGoal) goal).setChecked(false);
                        } else {
                            goalImgCheckBox.setImageResource(R.drawable.checkbox_on_background);
                            ((CheckGoal) goal).setChecked(true);
                        }
                    }
                });
            } else {
                goalImgCheckBox.setVisibility(View.GONE);
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
