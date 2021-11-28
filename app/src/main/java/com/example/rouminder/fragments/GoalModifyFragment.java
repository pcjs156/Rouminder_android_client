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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.rouminder.R;
import com.example.rouminder.adapter.SpinnerAdapter;
import com.example.rouminder.data.goalsystem.CountGoal;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.LocationGoal;
import com.example.rouminder.firebase.manager.BaseModelManager;
import com.example.rouminder.firebase.manager.GoalModelManager;
import com.google.android.gms.maps.model.LatLng;

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

        LinearLayout repeatLayout = (LinearLayout) view.findViewById(R.id.repeatLayout);
        LinearLayout countLayout = (LinearLayout) view.findViewById(R.id.count);
        RelativeLayout mapLayout = (RelativeLayout) view.findViewById(R.id.map);
        LinearLayout timeLayout = (LinearLayout) view.findViewById(R.id.timeLayout);

//        MapsFragment mapsFragment = (MapsFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.modifyMapFinder);
        // 위 코드가 오류가 나서 어떻게든 돌리기 위해 static 멤버를 선언했습니다..
        MapsFragment.selectedLatLng = null;

        TextView buttonGoalChange = (TextView) view.findViewById(R.id.buttonGoalModify); // 수정 버튼
        ImageView buttonGoalCancel = (ImageView) view.findViewById(R.id.buttonClose); // 취소 버튼

        TextView textViewStartTimeFront = (TextView) view.findViewById(R.id.textViewStartTimeFront); // 시작 시간, 년월일
        TextView textViewStartTimeBack = (TextView) view.findViewById(R.id.textViewStartTimeBack); // 시작 시간, 시분
        TextView textViewEndTimeFront = (TextView) view.findViewById(R.id.textViewEndTimeFront); // 마감 시간, 년월일
        TextView textViewEndTimeBack = (TextView) view.findViewById(R.id.textViewEndTimeBack); // 마감 시간, 시분
        EditText editTextGoalName = view.findViewById(R.id.editTextGoalName); // 골 이름
        EditText countNumber = (EditText) view.findViewById(R.id.countNumber);
        EditText countUnit = (EditText) view.findViewById(R.id.countUnit);

        // set tag adapter and color adapter
        ArrayAdapter tagsAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, GoalModelManager.getInstance().getTags());
        AutoCompleteTextView editTextTag = (AutoCompleteTextView) view.findViewById(R.id.editTextTag);
        editTextTag.setAdapter(tagsAdapter);

        Color[] colors = {
                Color.valueOf(256, 0, 0, 256),
                Color.valueOf(0, 256, 0, 256),
                Color.valueOf(0, 0, 256, 256)};

        SpinnerAdapter highlightsAdapter = new SpinnerAdapter(context, colors);
        Spinner highlightsSpinner = (Spinner) view.findViewById(R.id.spinnerHighlight);
        highlightsSpinner.setAdapter(highlightsAdapter);

        // set Text
        editTextGoalName.setText(goal.getName());
        editTextTag.setText(goal.getTag());
        Log.i("goal", "highlight position : " + highlightsAdapter.findItemPosition(goal.getHighlight()));
        highlightsSpinner.setSelection(highlightsAdapter.findItemPosition(goal.getHighlight()));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        if (true) { // if goal general
            repeatLayout.setVisibility(View.GONE);
            textViewStartTimeFront.setText(goal.getStartTime().format(dateFormatter));
            textViewStartTimeBack.setText(goal.getStartTime().format(timeFormatter));
            textViewEndTimeFront.setText(goal.getEndTime().format(dateFormatter));
            textViewEndTimeBack.setText(goal.getEndTime().format(timeFormatter));
        } else { // if goal repeat
            timeLayout.setVisibility(View.GONE);
        }

        if (goal.getType().equals(Goal.Type.COUNT.name())) {
            mapLayout.setVisibility(View.GONE);
            countNumber.setText(""+((CountGoal) goal).getCount());
            countUnit.setText(((CountGoal) goal).getUnit());
        } else if (goal.getType().equals(Goal.Type.LOCATION.name())) {
            countLayout.setVisibility(View.GONE);
        } else { // check goal
            mapLayout.setVisibility(View.GONE);
            countLayout.setVisibility(View.GONE);
        }

        // button click 구현
        buttonGoalChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("GoalModifyFragment", "Modify Start");

                // modify
                goal.setName(editTextGoalName.getText().toString());
                goal.setTag(editTextTag.getText().toString());
                goal.setHighlight((Color) highlightsSpinner.getSelectedItem());

                if (true) { // if goal general
                    String startDate = textViewStartTimeFront.getText().toString();
                    String startTime = textViewStartTimeBack.getText().toString();
                    String endDate = textViewEndTimeFront.getText().toString();
                    String endTime = textViewEndTimeBack.getText().toString();

                    DateTimeFormatter formatter = BaseModelManager.getShortTimeFormatter();
                    LocalDateTime start = LocalDateTime.parse(startDate+ " " + startTime, formatter);
                    LocalDateTime end = LocalDateTime.parse(endDate + " " + endTime, formatter);

                    // update
                    goal.setStartTime(start);
                    goal.setEndTime(end);
                } else { // if goal repeat
                    timeLayout.setVisibility(View.GONE);
                }

                if (goal.getType().equals(Goal.Type.COUNT.name())) {
                    if (countNumber.getText().toString().contains("-") |
                            countNumber.getText().toString().contains(",") |
                            countNumber.getText().toString().contains(".")) {
                        Toast.makeText(context, "숫자에는 특수문자가 들어갈 수 없습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        goal.setTarget(Integer.parseInt(countNumber.getText().toString()));
                        ((CountGoal) goal).setUnit(countUnit.getText().toString());
                    }
                } else if (goal.getType().equals(Goal.Type.LOCATION.name())) {
                    Log.i("test", "location equals");
                    LatLng location = MapsFragment.getSelectedLatLng();

                    if (location != null) {
                        Log.i("test", "location latitude : " + location.latitude);
                        Log.i("test", "location longitude : " + location.longitude);
                        ((LocationGoal) goal).setLat(location.latitude);
                        ((LocationGoal) goal).setLng(location.longitude);
                    }
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
