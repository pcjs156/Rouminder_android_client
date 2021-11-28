package com.example.rouminder.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
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
    View root;

    public ChallengeDescribeFragment(ChallengeItem challengeItem) {
        this.challengeItem = challengeItem;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_challenge_describe, container);
        Button buttonConfirm = (Button) root.findViewById(R.id.buttonConfirm2);
        TextView textViewChallengeName = (TextView) root.findViewById(R.id.textViewChallengeName);

        String name = "Goal Name : " + challengeItem.getChallengeName();

        textViewChallengeName.setText(name);

        buttonConfirm.setOnClickListener(this);
        setCancelable(false);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        int width = size.x;
        int height = size.y;

        root.getLayoutParams().width = (int) (width * 0.9);
        root.getLayoutParams().height = (int) (height * 0.8);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
