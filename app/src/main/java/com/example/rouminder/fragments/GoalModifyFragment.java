package com.example.rouminder.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import com.example.rouminder.R;
import com.example.rouminder.data.goalsystem.Goal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nullable;

public class GoalModifyFragment extends DialogFragment {
    Goal goal;
    public GoalModifyFragment(Goal goal) {this.goal = goal;}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal_modify, container);
        Button buttonGoalCancel = view.findViewById(R.id.buttonGoalCancel); // 취소 버튼
        Button buttonGoalChange = view.findViewById(R.id.buttonGoalModify); // 수정 버튼
        TextView textViewStartTimeFront = view.findViewById(R.id.textViewStartTimeFront); // 시작 시간, 년월일
        TextView textViewStartTimeBack = view.findViewById(R.id.textViewStartTimeBack); // 시작 시간, 시분
        TextView textViewEndTimeFront = view.findViewById(R.id.textViewEndTimeFront); // 마감 시간, 년월일
        TextView textViewEndTimeBack = view.findViewById(R.id.textViewEndTimeBack); // 마감 시간, 시분
        EditText editTextGoalName = view.findViewById(R.id.editTextGoalName); // 골 이름

        Context context = getContext();
        LocalDateTime startTime = goal.getStartTime();
        LocalDateTime endTime = goal.getEndTime();

        editTextGoalName.setText(goal.getName());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm");

        textViewStartTimeFront.setText(goal.getStartTime().format(dateFormatter));
        textViewStartTimeBack.setText(goal.getStartTime().format(timeFormatter));

        textViewEndTimeFront.setText(goal.getEndTime().format(dateFormatter));
        textViewEndTimeBack.setText(goal.getEndTime().format(timeFormatter));

            buttonGoalCancel.setOnClickListener(new View.OnClickListener() { // 취소 버튼 클릭 리스너
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

            buttonGoalChange.setOnClickListener(new View.OnClickListener() { // 수정 버튼 클릭 리스너
                @Override
                public void onClick(View view) {
                    // GoalManager를 이용하여 데이터 수정하도록 만들기.
                    dismiss();
                }
            });

            DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() { // 년월일 받는 리스너
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    resetDate(view, year, monthOfYear + 1, dayOfMonth);
                }
            };

            TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() { // 시분 받는 리스너
                @Override
                public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                    resetTime(view, hour, minute);
                }
            };

            textViewStartTimeFront.setOnClickListener(new View.OnClickListener() { // 시작 시간, 년월일
                @Override
                public void onClick(View view) {
                        DatePickerDialog dialog = new DatePickerDialog(context, dateListener, startTime.getYear(), startTime.getMonthValue() - 1, startTime.getDayOfMonth());
                        dialog.show();
                }
            });

            textViewStartTimeBack.setOnClickListener(new View.OnClickListener() { // 시작 시간, 시분
                @Override
                public void onClick(View view) {
                    TimePickerDialog dialog = new TimePickerDialog(context, timeListener, startTime.getHour(), startTime.getMinute(), false);
                    dialog.show();
                }
            });

            textViewEndTimeFront.setOnClickListener(new View.OnClickListener() { // 마감 시간, 년월일
                @Override
                public void onClick(View view) {
                    DatePickerDialog dialog = new DatePickerDialog(context, dateListener, endTime.getYear(), endTime.getMonthValue() - 1, endTime.getDayOfMonth());
                    dialog.show();
                }
            });

            textViewEndTimeBack.setOnClickListener(new View.OnClickListener() { // 마감 시간, 시분
                @Override
                public void onClick(View view) {
                    TimePickerDialog dialog = new TimePickerDialog(context, timeListener, endTime.getHour(), endTime.getMinute(), false);
                    dialog.show();
                }
            });

        setCancelable(false);
        return view;
    }

    private void resetDate(View view, int year, int month, int day) {
        LocalDateTime ret = LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.now());
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String strDate = ret.format(dateFormat);

        TextView editText = (TextView) view;
        editText.setText(strDate);
    }

    private void resetTime(View view, int hour, int minute) {
        LocalDateTime ret = LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute));
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        String strDate = ret.format(timeFormat);

        TextView editText = (TextView) view;
        editText.setText(strDate);
    }

}
