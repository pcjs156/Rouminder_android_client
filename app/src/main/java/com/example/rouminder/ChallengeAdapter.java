package com.example.rouminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class ChallengeItem {
    private String challengeName;

    public ChallengeItem(String challengeName) {
        this.challengeName = challengeName;
    }

    String getChallengeName() { return challengeName; }
    void setChallengeName(String challengeName) {this.challengeName = challengeName;}
}

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeHolder> {
    static ArrayList<ChallengeItem> list;

    ChallengeAdapter(ArrayList<ChallengeItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ChallengeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_challenge, parent, false);
        return new ChallengeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeHolder holder, int position) {
        ChallengeItem goalItem = list.get(position);
        holder.challengeName.setText(goalItem.getChallengeName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class ChallengeHolder extends RecyclerView.ViewHolder{
    CardView challengeContent;
    TextView challengeName;

    public ChallengeHolder(@NonNull View itemView) {
        super(itemView);
        challengeContent = itemView.findViewById(R.id.medalChallenge);
        challengeName = itemView.findViewById(R.id.medalChallengeText);
        challengeContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 클릭 시 해당 도전과제 설명 창 띄우기
            }
        });
    }
}