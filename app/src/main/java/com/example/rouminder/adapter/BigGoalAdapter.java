package com.example.rouminder.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.example.rouminder.firebase.manager.BaseModelManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Comparator;


public class BigGoalAdapter extends BaseGoalAdapter<BigGoalAdapter.ViewHolder> {
    FragmentActivity activity;

    public BigGoalAdapter(FragmentActivity activity, GoalManager goalManager, GoalManager.Domain domain, Comparator<Goal> comparator) {
        super(goalManager, domain, comparator);
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.item_goal, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.onBind(items.get(position));
    }

    public String getProgressString() {
        return Integer.toString(complete) + "/" + Integer.toString(inComplete+complete) + "???";
    }

    public double getProgress() {
        return progress;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CardView goalBox;
        private TextView goalContent;
        private TextView goalRestTime;
        private TextView goalSubText;
        private TextView goalTime;
        private ImageView goalImgCheckBox;
        private CircleProgressBar goalProgressBar;
        private TextView highlight;

        private View bindView;

        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                Bundle data = msg.getData();
                String restDtBody = data.getString("rest_dt_body");
                goalRestTime.setText(restDtBody);

                boolean isExpired = data.getBoolean("is_expired");
                boolean isBefore = data.getBoolean("is_before");
                String type = data.getString("type");

                if ((isExpired || isBefore) && type.equals("check")) {
                    goalImgCheckBox.setImageResource(R.drawable.ic_baseline_block_24);
                    goalImgCheckBox.setClickable(false);
                    goalImgCheckBox.setEnabled(false);
                }
            }
        };

        public ViewHolder(@NonNull View itemView) {
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

        private String getRestTimeString(Goal goal) {
            LocalDateTime now = LocalDateTime.now();
            if(goal.isAfterEnd(now))
                return "??????";

            Duration duration = goal.isBeforeStart(now) ? Duration.between(now, goal.getStartTime()) : Duration.between(now, goal.getEndTime());
            String postfixString = goal.isBeforeStart(now) ? "??????" : "??????";
            String timeString;

            long minutes = duration.toMinutes();
            long hours = duration.toHours();
            long days = duration.toDays();

            if(days != 0) {
                timeString = String.format(Locale.getDefault(), "%d??? %d?????? %d??? ??? ", days, hours % 24, minutes % 60);
            } else if(hours != 0){
                timeString = String.format(Locale.getDefault(), "%d?????? %d??? ??? ", hours % 24, minutes % 60);
            } else if(minutes != 0){
                timeString = String.format(Locale.getDefault(), "%d??? ??? ",  minutes % 60);
            } else {
                timeString = "??? ";
            }

            Log.d("string", timeString + postfixString);

            return timeString + postfixString;
        }

        void onBind(Goal goal) {
            goalContent.setText(goal.getName());
            goalSubText.setText(goal.progressToString());
            goalTime.setText(BaseModelManager.getTimeStampString(goal.getEndTime()));
            highlight.setBackgroundColor(goal.getHighlight().toArgb());

            goalRestTime.setText(getRestTimeString(goal));

            if (goal.getType().equals(Goal.Type.LOCATION.name())) {
                goalProgressBar.setVisibility(View.GONE);
                goalImgCheckBox.setVisibility(View.VISIBLE);
                goalImgCheckBox.setClickable(false);

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

                goalImgCheckBox.setClickable(checkGoal.isOnProgress(LocalDateTime.now()));
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
            } else { // count
                goalImgCheckBox.setVisibility(View.GONE);
                goalProgressBar.setVisibility(View.VISIBLE);
                goalProgressBar.setMax(goal.getTarget());
                goalProgressBar.setProgress(((CountGoal) goal).getCount());
            }

            goalBox.setClickable(!goal.isAfterEnd(LocalDateTime.now()));
            goalBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("TestCode", "FragmentGoalDescribe Start");
                    GoalDescribeFragment goalDescribeFragment = new GoalDescribeFragment(goal);
                    goalDescribeFragment.show(activity.getSupportFragmentManager(), null);
                }
            });
        }
    }
}
