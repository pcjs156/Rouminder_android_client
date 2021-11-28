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

import com.example.rouminder.MainApplication;
import com.example.rouminder.R;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;

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
        Button buttonGoalCancel = (Button) view.findViewById(R.id.buttonGoalCancel); // 취소 버튼
        Button buttonGoalChange = (Button) view.findViewById(R.id.buttonGoalModify); // 수정 버튼
        TextView textViewStartTimeFront = (TextView) view.findViewById(R.id.textViewStartTimeFront); // 시작 시간, 년월일
        TextView textViewStartTimeBack = (TextView) view.findViewById(R.id.textViewStartTimeBack); // 시작 시간, 시분
        TextView textViewEndTimeFront = (TextView) view.findViewById(R.id.textViewEndTimeFront); // 마감 시간, 년월일
        TextView textViewEndTimeBack = (TextView) view.findViewById(R.id.textViewEndTimeBack); // 마감 시간, 시분
        EditText editTextGoalName = view.findViewById(R.id.editTextGoalName); // 골 이름

        Context context = getContext();
        LocalDateTime startTime = goal.getStartTime();
        LocalDateTime endTime = goal.getEndTime();

        editTextGoalName.setText(goal.getName());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

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
                    Log.d("GoalModifyFragment", "Modify Start");
                    try {
                        //Log.d("GoalModifyFragment",textViewStartTimeFront.getText().toString());
                        //Log.d("GoalModifyFragment",textViewStartTimeBack.getText().toString());
                        //Log.d("GoalModifyFragment",textViewEndTimeFront.getText().toString());
                        //Log.d("GoalModifyFragment",textViewEndTimeBack.getText().toString());

                        int[] startDate = new int[3];
                        String temp = textViewStartTimeFront.getText().toString();
                        String[] startDateString = temp.split("\\.");
                        for(int i=0; i < 3; i++)
                            startDate[i] = Integer.parseInt(startDateString[i]);
                        int[] startTime = new int[2];
                        String[] startTimeString = textViewStartTimeBack.getText().toString().split(":");
                        for(int i=0; i < 2; i++)
                            startTime[i] = Integer.parseInt(startTimeString[i]);

                        int[] endDate = new int[3];
                        String[] endDateString = textViewEndTimeFront.getText().toString().split("\\.");
                        for(int i=0; i < 3; i++)
                            endDate[i] = Integer.parseInt(endDateString[i]);
                        int[] endTime = new int[2];
                        String[] endTimeString = textViewEndTimeBack.getText().toString().split(":");
                        for(int i=0; i < 2; i++)
                            endTime[i] = Integer.parseInt(endTimeString[i]);

                        LocalDateTime newStartTime = LocalDateTime.of(startDate[0], startDate[1], startDate[2], startTime[0], startDate[1]);
                        LocalDateTime newEndTime = LocalDateTime.of(endDate[0], endDate[1],endDate[2],endTime[0], endTime[1]);

                        GoalManager goalManager = ((MainApplication)getActivity().getApplication()).getGoalManager();
                        goalManager.getGoal(goal.getId()).setName(editTextGoalName.getText().toString());
                        goalManager.getGoal(goal.getId()).setStartTime(newStartTime);
                        goalManager.getGoal(goal.getId()).setEndTime(newEndTime);

                    } catch(Exception e) {
                        Log.d("GoalModifyFragment", "Modify Error");
                        e.printStackTrace();
                    }
                    dismiss();
                }
            });

        textViewStartTimeFront.setOnClickListener(new View.OnClickListener() { // 시작 시간, 년월일
                @Override
                public void onClick(View view) {
                    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() { // 년월일 받는 리스너
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                            resetDate(textViewStartTimeFront,view, year, monthOfYear + 1, dayOfMonth);
                        }
                    };
                        DatePickerDialog dialog = new DatePickerDialog(context, dateListener, startTime.getYear(), startTime.getMonthValue() - 1, startTime.getDayOfMonth());
                        dialog.show();
                }
            });

        textViewStartTimeBack.setOnClickListener(new View.OnClickListener() { // 시작 시간, 시분
                @Override
                public void onClick(View view) {
                    TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() { // 시분 받는 리스너
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                            resetTime(textViewStartTimeBack,view, hour, minute);
                        }
                    };

                    TimePickerDialog dialog = new TimePickerDialog(context, timeListener, startTime.getHour(), startTime.getMinute(), false);
                    dialog.show();
                }
            });

        textViewEndTimeFront.setOnClickListener(new View.OnClickListener() { // 마감 시간, 년월일
                @Override
                public void onClick(View view) {
                    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() { // 년월일 받는 리스너
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                            resetDate(textViewEndTimeFront,view, year, monthOfYear + 1, dayOfMonth);
                        }
                    };
                    DatePickerDialog dialog = new DatePickerDialog(context, dateListener, endTime.getYear(), endTime.getMonthValue() - 1, endTime.getDayOfMonth());
                    dialog.show();
                }
            });

        textViewEndTimeBack.setOnClickListener(new View.OnClickListener() { // 마감 시간, 시분
                @Override
                public void onClick(View view) {
                    TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() { // 시분 받는 리스너
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                            resetTime(textViewEndTimeBack,view, hour, minute);
                        }
                    };

                    TimePickerDialog dialog = new TimePickerDialog(context, timeListener, endTime.getHour(), endTime.getMinute(), false);
                    dialog.show();
                }
            });

        setCancelable(false);
        return view;
    }

    private void resetDate(TextView editText, View view, int year, int month, int day) {
        LocalDateTime ret = LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.now());
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String strDate = ret.format(dateFormat);

        editText = (TextView) view;
        editText.setText(strDate);
    }

    private void resetTime(TextView editText, View view, int hour, int minute) {
        LocalDateTime ret = LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute));
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        String strDate = ret.format(timeFormat);

        editText = (TextView) view;
        editText.setText(strDate);
    }

}
