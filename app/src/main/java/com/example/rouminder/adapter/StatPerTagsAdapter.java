package com.example.rouminder.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rouminder.R;

import java.util.ArrayList;
import java.util.HashMap;

public class StatPerTagsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Pair<String, HashMap<String, Object>>> statInfoList;

    public StatPerTagsAdapter(ArrayList<Pair<String, HashMap<String, Object>>> statInfoList) {
        super();
        this.statInfoList = statInfoList;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        onBindViewHolder((StatPerTagHolder) holder, position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_stat_per_tag, parent, false);
        return new StatPerTagHolder(view);
    }


    public void onBindViewHolder(@NonNull StatPerTagHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.onBind(statInfoList.get(position));
    }

    @Override
    public int getItemCount() {
        return statInfoList.size();
    }
}

class StatPerTagHolder extends RecyclerView.ViewHolder {
    private TextView tagName;
    private TextView achievementRate;
    private TextView totalGoalCnt;

    public StatPerTagHolder(@NonNull View itemView) {
        super(itemView);

        tagName = itemView.findViewById(R.id.tagName);
        achievementRate = itemView.findViewById(R.id.achievementRate);
        totalGoalCnt = itemView.findViewById(R.id.totalGoalCnt);
    }

    void onBind(Pair<String, HashMap<String, Object>> info) {
        tagName.setText(info.first);
        achievementRate.setText(((Double) info.second.get("achievementRate")).toString() + "%");
        totalGoalCnt.setText(((Integer) info.second.get("totalGoalCnt")).toString() + "개");
    }
}