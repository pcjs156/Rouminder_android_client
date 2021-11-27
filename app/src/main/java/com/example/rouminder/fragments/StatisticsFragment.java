package com.example.rouminder.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rouminder.R;
import com.example.rouminder.firebase.manager.GoalModelManager;

public class StatisticsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        GoalModelManager gManager = GoalModelManager.getInstance();

        Toast.makeText(getActivity().getApplicationContext(), "rate: " + gManager.getEntireAchievementRate(), Toast.LENGTH_SHORT).show();
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }
}