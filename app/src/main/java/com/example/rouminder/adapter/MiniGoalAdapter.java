package com.example.rouminder.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.util.Comparator;

public class MiniGoalAdapter extends BaseGoalAdapter {


    public MiniGoalAdapter(GoalManager goalManager, GoalManager.Domain domain, Comparator<Goal> comparator) {
        super(goalManager, domain, comparator);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.item_minigoal, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.onBind(items.get(position));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        onBindViewHolder((ViewHolder) holder, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView goalContent;
        TextView goalSubText;
        ImageView goalImgCheckBox;
        CircleProgressBar goalProgressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

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
        }
    }
}
