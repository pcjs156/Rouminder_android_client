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

import java.util.List;


public class BigGoalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Goal> items;
    FragmentActivity activity;

    public BigGoalAdapter(FragmentActivity activity, GoalManager goalManager, List<Goal> items) {
        super();
        this.items = items;
        this.activity = activity;

        goalManager.setOnGoalChangeListener(goalManager.new OnGoalChangeListener() {
            @Override
            public void onGoalAdd(int id) {
                int position = getItemPosition(id);
                if (position  == -1) {
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

    class BigGoalHolder extends RecyclerView.ViewHolder {
        private CardView goalBox;
        private TextView goalContent;
        private TextView goalRestTime;
        private TextView goalSubText;
        private TextView goalTime;
        private ImageView goalImgCheckBox;
        private CircleProgressBar goalProgressBar;
        private TextView highlight;

        public BigGoalHolder(@NonNull View itemView) {
            super(itemView);

            goalBox = itemView.findViewById(R.id.goalCardView);
            goalContent = itemView.findViewById(R.id.goalContent);
            goalRestTime = itemView.findViewById(R.id.goalRestTime);
            goalSubText = itemView.findViewById(R.id.goalSubText);
            goalTime = itemView.findViewById(R.id.goalTime);
            goalProgressBar = itemView.findViewById(R.id.progressBar);
            goalImgCheckBox = itemView.findViewById(R.id.goalImgCheckBox);
            highlight = itemView.findViewById(R.id.highlight);
        }

        void onBind(Goal goal) {
            goalContent.setText(goal.getName());
            goalRestTime.setText("rest Time 들어갈 자리");
            goalSubText.setText(goal.progressToString());
            goalTime.setText(goal.getEndTime().toString());
            highlight.setBackgroundColor(goal.getHighlight().toArgb());

            if (goal instanceof LocationGoal) {
                goalProgressBar.setVisibility(View.GONE);

                if (((LocationGoal) goal).getChecked())
                    goalImgCheckBox.setImageResource(R.drawable.checkbox_on_background);
                else goalImgCheckBox.setImageResource(R.drawable.checkbox_off_background);

                goalImgCheckBox.setClickable(false);
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