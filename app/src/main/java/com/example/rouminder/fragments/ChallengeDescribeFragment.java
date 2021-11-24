package com.example.rouminder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.rouminder.ChallengeItem;
import com.example.rouminder.R;
import com.example.rouminder.data.goalsystem.Goal;

import javax.annotation.Nullable;

public class ChallengeDescribeFragment extends DialogFragment implements View.OnClickListener {
    ChallengeItem challengeItem;

    public ChallengeDescribeFragment(ChallengeItem challengeItem) {
        this.challengeItem = challengeItem;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_challenge_describe, container);
        Button buttonConfirm = (Button) v.findViewById(R.id.buttonConfirm2);
        TextView textViewChallengeName = (TextView) v.findViewById(R.id.textViewChallengeName);

        String name = "Goal Name : " + challengeItem.getChallengeName();

        textViewChallengeName.setText(name);

        buttonConfirm.setOnClickListener(this);
        setCancelable(false);
        return v;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
