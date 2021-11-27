package com.example.rouminder.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.example.rouminder.R;
import com.example.rouminder.adapter.StatPerTagsAdapter;
import com.example.rouminder.firebase.manager.GoalModelManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatisticsFragment extends Fragment {
    private ViewGroup root;
    public RecyclerView statPerTagsView;
    private GoalModelManager goalModelManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        goalModelManager = GoalModelManager.getInstance();

        // Inflate the layout for this fragment
        root = (ViewGroup) inflater.inflate(R.layout.fragment_statistics, container, false);
        statPerTagsView = root.findViewById(R.id.cardPerTags);
        statPerTagsView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        statPerTagsView.setAdapter(new StatPerTagsAdapter(goalModelManager.getStatInfos()));

        initCircularChart();
        initBarChart();

        return root;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        statPerTagsView.setAdapter(new StatPerTagsAdapter(goalModelManager.getStatInfos()));
    }

    private void initCircularChart() {
        CircleProgressBar circleBar = root.findViewById(R.id.progressBar);
        circleBar.setMax(100);

        // 평균 달성률 설정(%)
        circleBar.setProgress(60);
    }

    private void initBarChart() {
        List<BarEntry> entryList = new ArrayList<>();

        // 그래프의 x축, y축 설정
        // 이 때 x축은 month(or week), y는 목표 달성률이 될 예정입니다.
        entryList.add(new BarEntry(1f, 0f));
        entryList.add(new BarEntry(2f, 5f));
        entryList.add(new BarEntry(3f, 1f));
        entryList.add(new BarEntry(4f, 2f));
        entryList.add(new BarEntry(5f, 3f));

        BarDataSet barDataSet = new BarDataSet(entryList, "MyBarDataSet");

        BarData barData = new BarData(barDataSet);

        BarChart barChart = (BarChart) root.findViewById(R.id.bar_chart);
        barChart.setData(barData);

        // 그래프 클릭 이벤트 비활성화
        barChart.setTouchEnabled(false);

        // 범례 삭제
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);

        // x축 범위 지정
        barChart.getXAxis().setAxisMinimum(0f);
        barChart.getXAxis().setAxisMaximum(6f);
        barChart.getXAxis().setGranularity(1f);

        // y축 범위 지정
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisRight().setAxisMinimum(0f);

        // y축 label 비활성화
        barChart.getAxisLeft().setDrawLabels(false);
        barChart.getAxisRight().setDrawLabels(false);

        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);

        // grid 미설정
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);

        // x축 값 위치 설정
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            // 1~5 범위에 x 값 입력
            List<String> labels = new ArrayList<>(Arrays.asList(new String[]{"", "", "", "", "", "", ""}));

            @Override
            public String getFormattedValue(float value) {
                return labels.get((int) value);
            }
        });

        barChart.invalidate();
    }
}