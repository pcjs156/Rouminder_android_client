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

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.rouminder.MainApplication;
import com.example.rouminder.R;
import com.example.rouminder.data.goalsystem.Goal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nullable;

public class GoalModifyFragment extends DialogFragment {
    int goalID;
    public GoalModifyFragment(Goal goal) { this.goalID = goal.getId(); }
    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal_modify, container);
        EditText editTextGoalName = v.findViewById(R.id.editTextGoalName);
        Button buttonConfirm = v.findViewById(R.id.buttonConfirm);
        Button buttonCancel = v.findViewById(R.id.buttonCancel);
        TextView textViewGoalStartTimeData = v.findViewById(R.id.textViewGoalStartTimeDataFront);
        TextView textViewGoalEndTimeData = v.findViewById(R.id.textViewGoalEndTimeDataFront);

        Goal goal = ((MainApplication)getActivity().getApplication()).getGoalManager().getGoal(goalID);
        editTextGoalName.setText(goal.getName());
        textViewGoalStartTimeData.setText(goal.getStartTime().toString());
        textViewGoalEndTimeData.setText(goal.getEndTime().toString());

        Context context = getContext();

        textViewGoalStartTimeData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("GoalModifyFragment", "StartTime Clicked");
                LocalDateTime cur = LocalDateTime.now();
                DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        resetDate(view, year, monthOfYear + 1, dayOfMonth);
                    }
                };
                TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        resetTime(view, hour, minute);
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog(context, dateListener,
                        cur.getYear(), cur.getMonthValue() - 1, cur.getDayOfMonth());
                dialog.show();
            }
        });

        textViewGoalEndTimeData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("GoalModifyFragment", "EndTime Clicked");
                LocalDateTime cur = goal.getEndTime();
                DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        resetDate(view, year, monthOfYear + 1, dayOfMonth);
                    }
                };
                TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        resetTime(view, hour, minute);
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog(context, dateListener,
                        cur.getYear(), cur.getMonthValue() - 1, cur.getDayOfMonth());
                dialog.show();
            }
        });


        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return v;
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
