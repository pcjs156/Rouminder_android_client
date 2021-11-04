package com.example.rouminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;



public class GoalAdapter extends RecyclerView.Adapter<Holder> {
    ArrayList<GoalItem> list;

    GoalAdapter(ArrayList<GoalItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_goal, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        GoalItem goalItem = list.get(position);
        holder.goalContent.setText(goalItem.getGoalContent());
        holder.goalRestTime.setText(goalItem.getGoalRestTime());
        holder.goalCount.setText(goalItem.getGoalCount());
        holder.goalTime.setText(goalItem.getGoalTime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}

class Holder extends RecyclerView.ViewHolder {
    TextView goalContent;
    TextView goalRestTime;
    TextView goalCount;
    TextView goalTime;

    public Holder(@NonNull View itemView) {
        super(itemView);
        goalContent = itemView.findViewById(R.id.goalContent);
        goalRestTime = itemView.findViewById(R.id.goalRestTime);
        goalCount = itemView.findViewById(R.id.goalCount);
        goalTime = itemView.findViewById(R.id.goalTime);
    }
}


