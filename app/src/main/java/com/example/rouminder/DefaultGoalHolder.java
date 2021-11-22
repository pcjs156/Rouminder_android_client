package com.example.rouminder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.example.rouminder.data.goalsystem.CheckGoal;
import com.example.rouminder.data.goalsystem.CountGoal;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;

public abstract class DefaultGoalHolder extends RecyclerView.ViewHolder {
    public DefaultGoalHolder(@NonNull View itemView) {
        super(itemView);
    }
}

class BigGoalHolder extends DefaultGoalHolder {
    TextView goalContent;
    TextView goalRestTime;
    TextView goalSubText;
    TextView goalTime;
    public ImageView goalImgCheckBox;
    CircleProgressBar goalProgressBar;

    public BigGoalHolder(GoalManager goalManager, @NonNull View itemView) {
        super(itemView);

        goalContent = itemView.findViewById(R.id.goalContent);
        goalRestTime = itemView.findViewById(R.id.goalRestTime);
        goalSubText = itemView.findViewById(R.id.goalSubText);
        goalTime = itemView.findViewById(R.id.goalTime);
        goalProgressBar = itemView.findViewById(R.id.progressBar);
        goalImgCheckBox = itemView.findViewById(R.id.goalImgCheckBox);

        int pos = getAdapterPosition();
        Goal goal = goalManager.getGoal(pos);

        if (goal instanceof CheckGoal){
            goalImgCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(pos != RecyclerView.NO_POSITION) {
                        if(((CheckGoal) goal).getChecked())  {
                            goalImgCheckBox.setImageResource(R.drawable.checkbox_off_background);
                            ((CheckGoal) goal).setChecked(false);
                        }
                        else {
                            goalImgCheckBox.setImageResource(R.drawable.checkbox_on_background);
                            ((CheckGoal) goal).setChecked(true);
                        }
                    }
                }
            });
        }

    }

    public void onBind(Goal goalItem) {
        goalContent.setText(goalItem.getName());
        goalRestTime.setText("rest Time 들어갈 자리");
        goalSubText.setText(goalItem.progressToString());
        goalTime.setText(goalItem.getEndTime().toString());

        if (goalItem instanceof CountGoal) {
            goalImgCheckBox.setVisibility(View.GONE);
            goalProgressBar.setMax(goalItem.getTarget());
            goalProgressBar.setProgress(((CountGoal) goalItem).getCount());
        }
        else {
            goalProgressBar.setVisibility(View.GONE);
            goalImgCheckBox.setImageResource(
                    (((CheckGoal) goalItem).getChecked()
                            ? R.drawable.checkbox_on_background
                            : R.drawable.checkbox_off_background));
        }
    }
}

class MiniGoalHolder extends DefaultGoalHolder {
    TextView goalContent;
    TextView goalSubText;
    public ImageView goalImgCheckBox;
    CircleProgressBar goalProgressBar;

    public MiniGoalHolder(GoalManager goalManager, @NonNull View itemView) {
        super(itemView);

        goalContent = itemView.findViewById(R.id.miniGoalContent);
        goalSubText = itemView.findViewById(R.id.miniGoalSubText);
        goalProgressBar = itemView.findViewById(R.id.miniGoalProgressBar);
        goalImgCheckBox = itemView.findViewById(R.id.miniGoalImgCheckBox);

        int pos = getAdapterPosition();
        Goal goal = goalManager.getGoal(pos);

        if (goal instanceof CheckGoal){
            goalImgCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(pos != RecyclerView.NO_POSITION) {
                        if(((CheckGoal) goal).getChecked())  {
                            goalImgCheckBox.setImageResource(R.drawable.checkbox_off_background);
                            ((CheckGoal) goal).setChecked(false);
                        }
                        else {
                            goalImgCheckBox.setImageResource(R.drawable.checkbox_on_background);
                            ((CheckGoal) goal).setChecked(true);
                        }
                    }
                }
            });
        }
    }

    public void onBind(Goal goalItem) {
        goalContent.setText(goalItem.getName());
        goalSubText.setText(goalItem.progressToString());

        if (goalItem instanceof CountGoal) {
            goalImgCheckBox.setVisibility(View.GONE);
            goalProgressBar.setMax(goalItem.getTarget());
            goalProgressBar.setProgress(((CountGoal) goalItem).getCount());
        }
        else {
            goalProgressBar.setVisibility(View.GONE);
            goalImgCheckBox.setImageResource(
                    (((CheckGoal) goalItem).getChecked()
                            ? R.drawable.checkbox_on_background
                            : R.drawable.checkbox_off_background));
        }
    }
}
