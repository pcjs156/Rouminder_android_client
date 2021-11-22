package com.example.rouminder;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    RecyclerView goldListContents;
    RecyclerView silverListContents;
    RecyclerView bronzeListContents;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_profile, container, false);

        Button goldMedalList = root.findViewById(R.id.btnGoldMedalList);
        Button silverMedalList = root.findViewById(R.id.btnSilverMedalList);
        Button bronzeMedalList = root.findViewById(R.id.btnBronzeMedalList);

        goldListContents = root.findViewById(R.id.goldListContents);
        silverListContents = root.findViewById(R.id.silverListContents);
        bronzeListContents = root.findViewById(R.id.bronzeListContents);

        goldMedalList.setOnClickListener(this);
        silverMedalList.setOnClickListener(this);
        bronzeMedalList.setOnClickListener(this);

        // 데이터 생성
        ArrayList<ChallengeItem> testList = new ArrayList<>();
        testList.add(new ChallengeItem("도전과제1"));
        testList.add(new ChallengeItem("도전과제2"));
        testList.add(new ChallengeItem( "도전과제3"));

        // 어댑터 객체 생성
        ChallengeAdapter goldAdapter = new ChallengeAdapter(testList);
        ChallengeAdapter silverAdapter = new ChallengeAdapter(testList);
        ChallengeAdapter bronzeAdapter = new ChallengeAdapter(testList);

        // 어댑터 설정
        goldListContents.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        goldListContents.setAdapter(goldAdapter);
        silverListContents.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        silverListContents.setAdapter(silverAdapter);
        bronzeListContents.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        bronzeListContents.setAdapter(bronzeAdapter);

        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGoldMedalList:
                Log.i("tag", "gold button click");
                if (goldListContents.getVisibility() == View.VISIBLE) goldListContents.setVisibility(View.GONE);
                else goldListContents.setVisibility(View.VISIBLE);
                break;
            case R.id.btnSilverMedalList:
                Log.i("tag", "silver button click");
                if (silverListContents.getVisibility() == View.VISIBLE) silverListContents.setVisibility(View.GONE);
                else silverListContents.setVisibility(View.VISIBLE);
                break;
            case R.id.btnBronzeMedalList:
                Log.i("tag", "bronze button click");
                if (bronzeListContents.getVisibility() == View.VISIBLE) bronzeListContents.setVisibility(View.GONE);
                else bronzeListContents.setVisibility(View.VISIBLE);
                break;
        }
    }
}