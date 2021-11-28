package com.example.rouminder.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import com.example.rouminder.MainApplication;
import com.example.rouminder.R;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;
import com.example.rouminder.firebase.manager.BaseModelManager;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nullable;

public class GoalModifyFragment extends DialogFragment {
    private Goal goal;
    private View view;
    private Context context;

    private TextView clickedView;

    public GoalModifyFragment(Goal goal) {
        this.goal = goal;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_goal_modify, container);

        context = getContext();

        // radius 조정을 위한 코드
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        TextView buttonGoalChange = (TextView) view.findViewById(R.id.buttonGoalModify); // 수정 버튼
        ImageView buttonGoalCancel = (ImageView) view.findViewById(R.id.buttonClose); // 취소 버튼

        TextView textViewStartTimeFront = (TextView) view.findViewById(R.id.textViewStartTimeFront); // 시작 시간, 년월일
        TextView textViewStartTimeBack = (TextView) view.findViewById(R.id.textViewStartTimeBack); // 시작 시간, 시분
        TextView textViewEndTimeFront = (TextView) view.findViewById(R.id.textViewEndTimeFront); // 마감 시간, 년월일
        TextView textViewEndTimeBack = (TextView) view.findViewById(R.id.textViewEndTimeBack); // 마감 시간, 시분
        EditText editTextGoalName = view.findViewById(R.id.editTextGoalName); // 골 이름

        editTextGoalName.setText(goal.getName());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        textViewStartTimeFront.setText(goal.getStartTime().format(dateFormatter));
        textViewStartTimeBack.setText(goal.getStartTime().format(timeFormatter));
        textViewEndTimeFront.setText(goal.getEndTime().format(dateFormatter));
        textViewEndTimeBack.setText(goal.getEndTime().format(timeFormatter));

        buttonGoalChange.setOnClickListener(new View.OnClickListener() { // 수정 버튼 클릭 리스너
                @Override
                public void onClick(View view) {
                    Log.d("GoalModifyFragment", "Modify Start");
                    try {
                        String startDate = textViewStartTimeFront.getText().toString();
                        String startTime = textViewStartTimeBack.getText().toString();
                        String endDate = textViewEndTimeFront.getText().toString();
                        String endTime = textViewEndTimeBack.getText().toString();

                        DateTimeFormatter formatter = BaseModelManager.getShortTimeFormatter();
                        LocalDateTime start = LocalDateTime.parse(startDate+ " " + startTime, formatter);
                        LocalDateTime end = LocalDateTime.parse(endDate + " " + endTime, formatter);

                        GoalManager goalManager = ((MainApplication)getActivity().getApplication()).getGoalManager();
                        goalManager.getGoal(goal.getId()).setName(editTextGoalName.getText().toString());
                        goalManager.getGoal(goal.getId()).setStartTime(start);
                        goalManager.getGoal(goal.getId()).setEndTime(end);

                    } catch(Exception e) {
                        Log.d("GoalModifyFragment", "Modify Error");
                        e.printStackTrace();
                    }
                    dismiss();
                }
            });
        buttonGoalCancel.setOnClickListener(new View.OnClickListener() { // 취소 버튼 클릭 리스너
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        textViewStartTimeFront.setOnClickListener(onDateClicked);
        textViewStartTimeBack.setOnClickListener(onTimeClicked);
        textViewEndTimeFront.setOnClickListener(onDateClicked);
        textViewEndTimeBack.setOnClickListener(onTimeClicked);

        setCancelable(false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        int width = size.x;
        int height = size.y;

        view.getLayoutParams().width = (int) (width * 0.9);
        view.getLayoutParams().height = (int) (height * 0.8);
    }

    private void resetDate(TextView view, int year, int month, int day) {
        LocalDateTime ret = LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.now());
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String strDate = ret.format(dateFormat);

        view.setText(strDate);
    }

    private void resetTime(TextView view, int hour, int minute) {
        LocalDateTime ret = LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute));
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        String strDate = ret.format(timeFormat);

        view.setText(strDate);
    }

    private View.OnClickListener onDateClicked = new View.OnClickListener() { // 시작 시간, 년월일
        @Override
        public void onClick(View view) {
            clickedView = (TextView) view;

            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            LocalDateTime ret = LocalDateTime.of(LocalDate.parse(clickedView.getText().toString(), format), LocalTime.now());

            DatePickerDialog dialog = new DatePickerDialog(context, dateListener,
                    ret.getYear(), ret.getMonthValue() - 1, ret.getDayOfMonth());
            dialog.show();
        }
    };

    private View.OnClickListener onTimeClicked = new View.OnClickListener() { // 마감 시간, 시분
        @Override
        public void onClick(View view) {
            clickedView = (TextView) view;

            DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");
            LocalDateTime ret = LocalDateTime.of(LocalDate.now(), LocalTime.parse(clickedView.getText().toString(), format));

            TimePickerDialog dialog = new TimePickerDialog(context, timeListener, ret.getHour(), ret.getMinute(), false);
            dialog.show();
        }
    };

    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            resetDate(clickedView, year, monthOfYear + 1, dayOfMonth);
        }
    };

    private TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            resetTime(clickedView, hour, minute);
        }
    };

}
