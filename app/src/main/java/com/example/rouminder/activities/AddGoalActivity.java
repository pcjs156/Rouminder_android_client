package com.example.rouminder.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.rouminder.MainApplication;
import com.example.rouminder.R;
import com.example.rouminder.adapter.SpinnerAdapter;
import com.example.rouminder.data.goalsystem.CheckGoal;
import com.example.rouminder.data.goalsystem.CountGoal;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;
import com.example.rouminder.data.goalsystem.LocationGoal;
import com.example.rouminder.firebase.exception.ModelDoesNotExists;
import com.example.rouminder.firebase.manager.BaseModelManager;
import com.example.rouminder.firebase.manager.GoalModelManager;
import com.example.rouminder.firebase.manager.RepeatPlanModelManager;
import com.example.rouminder.firebase.model.RepeatPlanModel;
import com.example.rouminder.fragments.MapsFragment;
import com.example.rouminder.helpers.RepeatPlanHelper;
import com.google.android.gms.maps.model.LatLng;
import com.example.rouminder.firebase.model.GoalModel;
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;

import android.graphics.Color;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class AddGoalActivity extends AppCompatActivity {
    // self
    private AddGoalActivity self = this;

    // manager
    private GoalModelManager goalModelManager = GoalModelManager.getInstance();
    private RepeatPlanModelManager repeatPlanModelManager = RepeatPlanModelManager.getInstance();

    // uid
    private String uid;

    // UI
    private Spinner highlightsSpinner;
    private SingleSelectToggleGroup typeGroup;
    private SingleSelectToggleGroup methodGroup;
    private MultiSelectToggleGroup weekGroup;

    private EditText goalNameEditText;
    private AutoCompleteTextView editTextTag;
    private EditText editTextTargetCount;
    private EditText editTextUnit;
    private TextView startDate;
    private TextView startTime;
    private TextView endDate;
    private TextView endTime;
    private TextView textViewGoalEndTime;
    private TextView textViewGoalStartTime;
//    private TextView textViewGoalEndDate;
//    private TextView textViewGoalStartDate;


    private MapsFragment mapsFragment;

    // save clicked content
    private int currentType;
    private View clickedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        this.uid = BaseModelManager.getInstance().getUid();

        LinearLayout methodLayout = (LinearLayout) findViewById(R.id.method);
        LinearLayout countLayout = (LinearLayout) findViewById(R.id.count);
        LinearLayout weekdayLayout = (LinearLayout) findViewById(R.id.weekdayLayout);
        LinearLayout dateTimeLayout = (LinearLayout) findViewById(R.id.datetimeLayout);
        RelativeLayout mapLayout = (RelativeLayout) findViewById(R.id.map);

        weekdayLayout.setVisibility(View.GONE);

        LinearLayout dateTime = findViewById(R.id.dateTimeSettingLayout);
        dateTime.setVisibility(View.GONE);

        startDate = findViewById(R.id.startDate);
        startTime = findViewById(R.id.startTime);
        endDate = findViewById(R.id.endDate);
        endTime = findViewById(R.id.endTime);
        goalNameEditText = (EditText) findViewById(R.id.goalNameEditText);
        editTextUnit = (EditText) findViewById(R.id.editTextNumberSigned2);
        editTextTargetCount = (EditText) findViewById(R.id.editTextNumberSigned);

        textViewGoalEndTime = (TextView) findViewById(R.id.textViewGoalEndTime);
        textViewGoalStartTime = (TextView) findViewById(R.id.textViewGoalStartTime);
//        textViewGoalEndDate = (TextView) findViewById(R.id.textViewGoalEndDate);
//        textViewGoalStartDate = (TextView) findViewById(R.id.textViewGoalStartDate);

        textViewGoalEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTimeClicked(view);
            }
        });
        textViewGoalStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTimeClicked(view);
            }
        });
//        textViewGoalEndDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onDateClicked(view);
//            }
//        });
//        textViewGoalStartDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onDateClicked(view);
//            }
//        });

        mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.mapFinder);

        resetDateTime();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar_add_goal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayAdapter tagsAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, goalModelManager.getTags());
        editTextTag = (AutoCompleteTextView) findViewById(R.id.editTextTag);
        editTextTag.setAdapter(tagsAdapter);

        Color[] colors = {
                Color.valueOf(256, 0, 0, 256),
                Color.valueOf(0, 256, 0, 256),
                Color.valueOf(0, 0, 256, 256)};

        SpinnerAdapter highlightsAdapter = new SpinnerAdapter(this, colors);
        highlightsSpinner = (Spinner) findViewById(R.id.spinnerHighlight);
        highlightsSpinner.setAdapter(highlightsAdapter);

        typeGroup = (SingleSelectToggleGroup) findViewById(R.id.groupChoicesTypes);
        methodGroup = (SingleSelectToggleGroup) findViewById(R.id.groupChoicesMethod);
        weekGroup = (MultiSelectToggleGroup) findViewById(R.id.groupChoicesWeekday);
        typeGroup.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                currentType = checkedId;

                if (checkedId == R.id.choiceGeneral) {
                    dateTime.setVisibility(View.GONE);
                    countLayout.setVisibility(View.GONE);
                    mapLayout.setVisibility(View.GONE);
                    //weekGroup.setVisibility(View.GONE);
                    weekdayLayout.setVisibility(View.GONE);
                    dateTimeLayout.setVisibility(View.GONE);

                    methodGroup.clearCheck();

                    methodLayout.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.choiceRepeat) {
                    dateTime.setVisibility(View.GONE);
                    countLayout.setVisibility(View.GONE);
                    mapLayout.setVisibility(View.GONE);

                    methodGroup.clearCheck();
                    weekGroup.clearCheck();

                    methodLayout.setVisibility(View.VISIBLE);
                    weekdayLayout.setVisibility(View.VISIBLE);
                    dateTimeLayout.setVisibility(View.VISIBLE);
                    //weekGroup.setVisibility(View.VISIBLE);

                } else if (checkedId == R.id.choiceComplex) {
                    countLayout.setVisibility(View.GONE);
                    mapLayout.setVisibility(View.GONE);
                    dateTime.setVisibility(View.GONE);

                    methodGroup.clearCheck();

                    methodLayout.setVisibility(View.GONE);
                }
            }
        });
        methodGroup.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                if (checkedId == R.id.choiceCheck) {
                    countLayout.setVisibility(View.GONE);
                    mapLayout.setVisibility(View.GONE);
                } else if (checkedId == R.id.choiceCount) {
                    countLayout.setVisibility(View.VISIBLE);
                    mapLayout.setVisibility(View.GONE);
                } else if (checkedId == R.id.choiceLocation) {
                    countLayout.setVisibility(View.GONE);
                    mapLayout.setVisibility(View.VISIBLE);
                }

                if (currentType == R.id.choiceGeneral) dateTime.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // android.R.id.home : toolbar 의back
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCheckClicked(View view) {
        HashMap<String, Object> values = new HashMap<>();

        String goalName = goalNameEditText.getText().toString();
        if (goalName.isEmpty()) {
            Toast.makeText(self, "목표명이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
            Log.d("FIELD", "name");
            return;
        } else
            values.put("name", goalName);

        Object selectedHighlight = highlightsSpinner.getSelectedItem();
        String highlight = "";
        if (selectedHighlight != null) {
            Color selectedColor = (Color) highlightsSpinner.getSelectedItem();
            highlight = String.format("#%08X", (0xFFFFFFFF & selectedColor.toArgb()));
            values.put("highlight", highlight);
        } else {
            Toast.makeText(self, "하이라이트가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
            Log.d("FIELD", "highlight");
            return;
        }

        String tag = editTextTag.getText().toString();
        values.put("tag", tag);

        String type = "";
        switch (typeGroup.getCheckedId()) {
            case R.id.choiceGeneral:
                type = "general";
                break;
            case R.id.choiceRepeat:
                type = "repeat";
                break;
            case R.id.choiceComplex:
                type = "complex";
                break;
            default:
                Toast.makeText(self, "경고: 유형이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                Log.d("FIELD", "type");
                return;
        }
        if (type.equals("complex")) {
            Toast.makeText(self, "구현되지 않은 유형입니다.", Toast.LENGTH_SHORT).show();
            Log.d("FIELD", "invalid type");
            return;
        } else {
            values.put("type", type);
        }

        String method = "";
        switch (methodGroup.getCheckedId()) {
            case R.id.choiceCheck:
                method = "check";
                break;
            case R.id.choiceCount:
                method = "count";
                break;
            case R.id.choiceLocation:
                method = "location";
                break;
            default:
                Toast.makeText(self, "경고: 수행 방법이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                Log.d("FIELD", "method");
                return;
        }
        values.put("method", method);

        values.put("current", 0);
        switch (method) {
            case "check":
                values.put("target_count", 1);
                break;
            case "location":
                values.put("target_count", 1);
                LatLng selectedLatLng = mapsFragment.getSelectedLatLng();
                if (selectedLatLng == null) {
                    Toast.makeText(self, "위치가 지정되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Double latitude = selectedLatLng.latitude;
                Double longitude = selectedLatLng.longitude;
                values.put("latitude", latitude);
                values.put("longitude", longitude);
                break;
            case "count":
                String unit = editTextUnit.getText().toString();
                if (unit.isEmpty())
                    unit = "회";
                values.put("unit", unit);

                String rawTargetCount = editTextTargetCount.getText().toString();
                if (rawTargetCount == null || rawTargetCount.isEmpty()) {
                    Toast.makeText(self, "목표 count가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Integer targetCount = Integer.parseInt(rawTargetCount);
                values.put("target_count", targetCount);
                break;
        }

        if (type.equals("general")) {
            String startDateString = startDate.getText().toString();
            String endDateString = endDate.getText().toString();
            String startTimeString = startTime.getText().toString();
            String endTimeString = endTime.getText().toString();

            DateTimeFormatter formatter = BaseModelManager.getShortTimeFormatter();
            LocalDateTime start = LocalDateTime.parse(startDateString + " " + startTimeString, formatter);
            LocalDateTime end = LocalDateTime.parse(endDateString + " " + endTimeString, formatter);

            if (start.isAfter(end)) {
                Toast.makeText(self, "시작 일시는 종료 일시 이후일 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            String startDatetime = BaseModelManager.getTimeStampString(start);
            String endDatetime = BaseModelManager.getTimeStampString(end);
            values.put("start_datetime", startDatetime);
            values.put("finish_datetime", endDatetime);

            GoalModel goalModel = goalModelManager.create(values);
            GoalManager goalManager = ((MainApplication) getApplication()).getGoalManager();
            goalManager.addGoal(GoalModelManager.convertGoalModelToGoal(goalManager, goalModel));
        } else if (type.equals("repeat")) {
            Boolean[] _weekPlan = {true, false, true, false, true, false, false};
            ArrayList<Boolean> weekPlan = new ArrayList<>();
            for (Boolean plan : _weekPlan) {
                weekPlan.add(plan);
            }

            values.put("week_plan", weekPlan);
            RepeatPlanModel plan = repeatPlanModelManager.create(values);
            RepeatPlanHelper.generateGoals(((MainApplication)getApplication()).getGoalManager(), plan);
//            goalModelManager.create(plan, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        }

        finish();
    }

    private void resetDateTime() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59));

        resetDate(startDate, start.getYear(), start.getMonthValue(), start.getDayOfMonth());
        resetTime(startTime, start.getHour(), start.getMinute());
        resetDate(endDate, end.getYear(), end.getMonthValue(), end.getDayOfMonth());
        resetTime(endTime, end.getHour(), end.getMinute());
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

    public void onDateClicked(View view) {
        LocalDateTime cur = LocalDateTime.now();
        clickedView = view;

        DatePickerDialog dialog = new DatePickerDialog(this, dateListener,
                cur.getYear(), cur.getMonthValue() - 1, cur.getDayOfMonth());
        dialog.show();
    }

    public void onTimeClicked(View view) {
        LocalDateTime cur = LocalDateTime.now();
        clickedView = view;

        TimePickerDialog dialog = new TimePickerDialog(this, timeListener, cur.getHour(), cur.getMinute(), false);
        dialog.show();
    }

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