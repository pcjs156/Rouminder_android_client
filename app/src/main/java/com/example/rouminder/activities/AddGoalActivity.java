package com.example.rouminder.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
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
import com.example.rouminder.firebase.manager.BaseModelManager;
import com.example.rouminder.firebase.manager.GoalModelManager;
import com.example.rouminder.firebase.manager.RepeatPlanModelManager;
import com.example.rouminder.firebase.model.RepeatPlanModel;
import com.example.rouminder.fragments.MapsFragment;
import com.google.android.gms.maps.model.LatLng;
import com.example.rouminder.firebase.model.GoalModel;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;

import android.graphics.Color;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class AddGoalActivity extends AppCompatActivity {
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    public Spinner highlightsSpinner;
    public SingleSelectToggleGroup typeGroup;
    public SingleSelectToggleGroup methodGroup;

    private EditText goalNameEditText;
    private MultiAutoCompleteTextView editTextTag;
    private EditText editTextTargetCount;
    private EditText editTextUnit;
    private TextView startDate;
    private TextView startTime;
    private TextView endDate;
    private TextView endTime;

    private MapsFragment mapsFragment;

    public static int currentType;
    public static View clickedDate;

    public GoalModelManager goalModelManager = GoalModelManager.getInstance();
    public RepeatPlanModelManager repeatPlanModelManager = RepeatPlanModelManager.getInstance();
    GoalManager goalManager;

    public String uid;

    public AddGoalActivity self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        this.uid = BaseModelManager.getInstance().getUid();

        LinearLayout methodLayout = (LinearLayout) findViewById(R.id.method);
        LinearLayout countLayout = (LinearLayout) findViewById(R.id.count);
        RelativeLayout mapLayout = (RelativeLayout) findViewById(R.id.map);

        LinearLayout dateTime = findViewById(R.id.dateTimeSettingLayout);
        dateTime.setVisibility(View.GONE);

        startDate = findViewById(R.id.startDate);
        startTime = findViewById(R.id.startTime);
        endDate = findViewById(R.id.endDate);
        endTime = findViewById(R.id.endTime);
        goalNameEditText = (EditText) findViewById(R.id.goalNameEditText);
        editTextUnit = (EditText) findViewById(R.id.editTextNumberSigned2);
        editTextTargetCount = (EditText) findViewById(R.id.editTextNumberSigned);

        mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.mapFinder);

        resetDateTime();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar_add_goal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayAdapter tagsAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, goalModelManager.getTags());
        editTextTag = (MultiAutoCompleteTextView) findViewById(R.id.editTextTag);
        MultiAutoCompleteTextView.CommaTokenizer token = new MultiAutoCompleteTextView.CommaTokenizer();
        editTextTag.setTokenizer(token);
        editTextTag.setAdapter(tagsAdapter);

//        int colors[] = {R.drawable.red, R.drawable.blue, R.drawable.green};
        Color[] colors = {
                Color.valueOf(256, 0, 0, 256),
                Color.valueOf(0, 256, 0, 256),
                Color.valueOf(0, 0, 256, 256)};

        SpinnerAdapter highlightsAdapter = new SpinnerAdapter(this, colors);
        highlightsSpinner = (Spinner) findViewById(R.id.spinnerHighlight);
        highlightsSpinner.setAdapter(highlightsAdapter);

        typeGroup = (SingleSelectToggleGroup) findViewById(R.id.groupChoicesTypes);
        methodGroup = (SingleSelectToggleGroup) findViewById(R.id.groupChoicesMethod);
        typeGroup.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                currentType = checkedId;

                if (checkedId == R.id.choiceGeneral) {
                    dateTime.setVisibility(View.GONE);
                    countLayout.setVisibility(View.GONE);
                    mapLayout.setVisibility(View.GONE);

                    methodGroup.clearCheck();

                    methodLayout.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.choiceRepeat) {
                    dateTime.setVisibility(View.GONE);
                    countLayout.setVisibility(View.GONE);
                    mapLayout.setVisibility(View.GONE);

                    methodGroup.clearCheck();

                    methodLayout.setVisibility(View.VISIBLE);
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

//        add goal 완료 버튼은 showCustom을 사용하여 나중에 추가할 계획
//        getSupportActionBar().setDisplayShowCustomEnabled();
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

            StringTokenizer startDateTokenizer = new StringTokenizer(startDateString, ".");
            int startYear = Integer.parseInt(startDateTokenizer.nextToken().toString());
            int startMonth = Integer.parseInt(startDateTokenizer.nextToken().toString());
            int startDay = Integer.parseInt(startDateTokenizer.nextToken().toString());

            StringTokenizer endDateTokenizer = new StringTokenizer(endDateString, ".");
            int endYear = Integer.parseInt(endDateTokenizer.nextToken().toString());
            int endMonth = Integer.parseInt(endDateTokenizer.nextToken().toString());
            int endDay = Integer.parseInt(endDateTokenizer.nextToken().toString());

            StringTokenizer startTimeTokenizer = new StringTokenizer(startTimeString, ":");
            int startHour = Integer.parseInt(startTimeTokenizer.nextToken().toString());
            int startMinute = Integer.parseInt(startTimeTokenizer.nextToken().toString());

            StringTokenizer endTimeTokenizer = new StringTokenizer(endTimeString, ":");
            int endHour = Integer.parseInt(endTimeTokenizer.nextToken().toString());
            int endMinute = Integer.parseInt(endTimeTokenizer.nextToken().toString());

            LocalDateTime start = LocalDateTime.of(startYear, startMonth, startDay, startHour, startMinute);
            LocalDateTime end = LocalDateTime.of(endYear, endMonth, endDay, endHour, endMinute);

            if (start.isAfter(end)) {
                Toast.makeText(self, "시작 일시는 종료 일시 이후일 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
//        else if (start.isBefore(LocalDateTime.now())) {
//            Toast.makeText(self, "시작 일시는 현시점 이전일 수 없습니다.", Toast.LENGTH_SHORT).show();
//            return;
//        }

            String startDatetime = BaseModelManager.getTimeStampString(start);
            String endDatetime = BaseModelManager.getTimeStampString(end);
            values.put("start_datetime", startDatetime);
            values.put("finish_datetime", endDatetime);

            GoalModel goalModel = goalModelManager.create(values);
            goalManager = ((MainApplication) getApplication()).getGoalManager();
            goalManager.addGoal(convertGoalModelToGoal(goalModel));
        } else if (type.equals("repeat")) {
            Boolean[] _weekPlan = {true, false, true, false, true, false, false};
            ArrayList<Boolean> weekPlan = new ArrayList<>();
            for (Boolean plan : _weekPlan) {
                weekPlan.add(plan);
            }

            values.put("week_plan", weekPlan);
            RepeatPlanModel plan = repeatPlanModelManager.create(values);
            goalModelManager.create(plan);
        }

        finish();
    }

    private Goal convertGoalModelToGoal(GoalModel goalModel) {
        Goal goal;

        HashMap<String, Object> info = goalModel.getInfo();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("20yy.MM.dd HH:mm:ss");

        Log.i("test", info.get("method").toString());

        if ((info.get("method").toString()).equals("check")) {
            Log.i("test", "id" + info.get("id").toString());
            goal = new CheckGoal(goalManager,
                    Integer.parseInt(info.get("id").toString()),
                    info.get("name").toString(),
                    LocalDateTime.parse(info.get("start_datetime").toString(), formatter),
                    LocalDateTime.parse(info.get("finish_datetime").toString(), formatter),
                    Integer.parseInt(info.get("current").toString()));
        } else if (info.get("method").toString().equals("count")) {
            goal = new CountGoal(goalManager,
                    Integer.parseInt(info.get("id").toString()),
                    info.get("name").toString(),
                    LocalDateTime.parse(info.get("start_datetime").toString(), formatter),
                    LocalDateTime.parse(info.get("finish_datetime").toString(), formatter),
                    Integer.parseInt(info.get("current").toString()),
                    Integer.parseInt(info.get("target_count").toString()),
                    info.get("unit").toString());
        } else {
            goal = new LocationGoal(goalManager,
                    Integer.parseInt(info.get("id").toString()),
                    info.get("name").toString(),
                    LocalDateTime.parse(info.get("start_datetime").toString(), formatter),
                    LocalDateTime.parse(info.get("finish_datetime").toString(), formatter),
                    Integer.parseInt(info.get("current").toString()),
                    Integer.parseInt(info.get("target_count").toString()));
        }

        return goal;
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
        clickedDate = view;

        DatePickerDialog dialog = new DatePickerDialog(this, dateListener,
                cur.getYear(), cur.getMonthValue() - 1, cur.getDayOfMonth());
        dialog.show();
    }

    public void onTimeClicked(View view) {
        LocalDateTime cur = LocalDateTime.now();
        clickedDate = view;

        TimePickerDialog dialog = new TimePickerDialog(this, timeListener, cur.getHour(), cur.getMinute(), false);
        dialog.show();
    }

    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            resetDate(clickedDate, year, monthOfYear + 1, dayOfMonth);
        }
    };

    private TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            resetTime(clickedDate, hour, minute);
        }
    };


}