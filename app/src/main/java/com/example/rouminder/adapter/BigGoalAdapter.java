package com.example.rouminder.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


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

        Timer timer = new Timer();

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
            goalSubText.setText(goal.progressToString());
            goalTime.setText(BaseModelManager.getTimeStampString(goal.getEndTime()));
            highlight.setBackgroundColor(goal.getHighlight().toArgb());

            TimerTask task = new TimerTask() {
                private String getRestTimeString(LocalDateTime now, LocalDateTime start, LocalDateTime end) {
                    String body = "";

                    Duration duration;
                    Long durationSeconds;
                    long days, hours, minutes, seconds;

                    if (now.isAfter(end)) {
                        body = "만료";
                    } else if (now.isBefore(start)) {
                        duration = Duration.between(now, start);

                        durationSeconds = duration.getSeconds();

                        hours = durationSeconds / (60 * 60);
                        days = hours / 24;
                        hours -= 24 * days;

                        minutes = ((durationSeconds % (60 * 60)) / 60);
                        seconds = durationSeconds % 60;

                        if (days != 0)
                            body += (days + "일 ");
                        if (hours != 0)
                            body += (hours + "시간 ");
                        if (minutes != 0)
                            body += (minutes + "분 ");

                        if (days == 0 && hours == 0 && minutes == 0 && seconds > 0)
                            body = (durationSeconds % 60) + "초 후 시작";
                        else {
                            if (days == 0 && hours == 0 && minutes == 0)
                                body = "만료";
                            else
                                body += "후 시작";
                        }
                    } else {
                        duration = Duration.between(now, end);

                        durationSeconds = duration.getSeconds();

                        hours = durationSeconds / (60 * 60);
                        days = hours / 24;
                        hours -= 24 * days;

                        minutes = ((durationSeconds % (60 * 60)) / 60);
                        seconds = durationSeconds % 60;

                        if (days != 0)
                            body += (days + "일 ");
                        if (hours != 0)
                            body += (hours + "시간 ");
                        if (minutes != 0)
                            body += (minutes + "분 ");

                        if (days == 0 && hours == 0 && minutes == 0 && seconds > 0)
                            body = (durationSeconds % 60) + "초 후 종료";
                        else {
                            if (days == 0 && hours == 0 && minutes == 0)
                                body = "만료";
                            else
                                body += "후 종료";
                        }
                    }

                    return body;
                }

                @Override
                public void run() {
                    Message msg = handler.obtainMessage();
                    Bundle data = new Bundle();

                    if (goal instanceof CheckGoal)
                        data.putString("type", "check");
                    else if (goal instanceof CountGoal)
                        data.putString("type", "count");
                    else
                        data.putString("type", "location");

                    LocalDateTime start = goal.getStartTime();
                    LocalDateTime end = goal.getEndTime();
                    LocalDateTime now = LocalDateTime.now();
                    String restDtBody = getRestTimeString(now, start, end);
                    data.putString("rest_dt_body", restDtBody);
                    data.putBoolean("is_expired", now.isAfter(end));
                    data.putBoolean("is_before", now.isBefore(start));

                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            };
            timer.schedule(task, 0, 500);

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
                    goalDescribeFragment.show(activity.getSupportFragmentManager(), null);
                }
            });
        }
    }
}
