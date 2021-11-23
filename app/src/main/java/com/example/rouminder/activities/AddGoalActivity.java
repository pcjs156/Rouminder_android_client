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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.rouminder.R;
import com.example.rouminder.adapter.SpinnerAdapter;
import com.example.rouminder.firebase.manager.BaseModelManager;
import com.example.rouminder.firebase.manager.GoalModelManager;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;

import android.graphics.Color;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.StringTokenizer;

public class AddGoalActivity extends AppCompatActivity {
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    public Spinner highlightsSpinner;
    public SingleSelectToggleGroup typeGroup;
    public SingleSelectToggleGroup methodGroup;

    private EditText goalNameEditText;
    private MultiAutoCompleteTextView editTextTag;
    private TextView startDate;
    private TextView startTime;
    private TextView endDate;
    private TextView endTime;

    public static int currentType;
    public static View clickedDate;

    public GoalModelManager goalModelManager = GoalModelManager.getInstance();

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
                Color.valueOf(0, 256,0,256),
                Color.valueOf(0, 0,256,256)};

        SpinnerAdapter highlightsAdapter = new SpinnerAdapter(this, colors);
        highlightsSpinner = (Spinner) findViewById(R.id.spinnerHighlight);
        highlightsSpinner.setAdapter(highlightsAdapter);

        // highlightsAdapter.getDropDownView()

        // 현재 오류나서 잠시 주석처리
//        LabelToggle choiceGeneral = (LabelToggle) findViewById(R.id.choiceGeneral);
//        choiceGeneral.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ArrayList<String> categoryIds = new ArrayList<>();
//                for (CategoryModel model : categoryModels) {
//                    categoryIds.add(model.id);
//                }
//
//                Collections.shuffle(categoryIds);
//                if (!categoryIds.isEmpty()) {
//                    String pickedId = categoryIds.get(0);
//                    HashMap<String, Object> newData = new HashMap<>();
//                    newData.put("name", "newName");
//                    try {
//                        categoryModelManager.update(pickedId, newData);
//                    } catch (ModelDoesNotExists e) {
//                        e.printStackTrace();
//                        Toast.makeText(getApplicationContext(), "해당 모델이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
//
//        LabelToggle choiceRepeat = (LabelToggle) findViewById(R.id.choiceRepeat);
//        choiceRepeat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ArrayList<String> categoryIds = new ArrayList<>();
//                for (CategoryModel model : categoryModels) {
//                    categoryIds.add(model.id);
//                }
//
//                Collections.shuffle(categoryIds);
//                if (!categoryIds.isEmpty()) {
//                    String pickedId = categoryIds.get(0);
//                    try {
//                        categoryModelManager.delete(pickedId);
//                    } catch (ModelDoesNotExists e) {
//                        e.printStackTrace();
//                        Toast.makeText(getApplicationContext(), "해당 모델이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });

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
        }
        else {
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
        if (!type.equals("general")) {
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
                method = "choice";
                break;
            case R.id.choiceLocation:
                method = "location";
                break;
            default:
                Toast.makeText(self, "경고: 수행 방법이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                Log.d("FIELD", "method");
                return;
        }
        if (!method.equals("check")) {
            Toast.makeText(self, "구현되지 않은 수행 방법입니다.", Toast.LENGTH_SHORT).show();
            Log.d("FIELD", "invalid method");
            return;
        } else {
            values.put("method", method);
        }

        values.put("current", 0);

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

        Date start = new Date(startYear, startMonth, startDay, startHour, startMinute);
        Date end = new Date(endYear, endMonth, endDay, endHour, endMinute);

        String startDatetime = BaseModelManager.getTimeStampString(start);
        String endDatetime = BaseModelManager.getTimeStampString(end);
        values.put("start_datetime", startDatetime);
        values.put("finish_datetime", endDatetime);

        Log.d("DATETIME", startDatetime);

        goalModelManager.create(values);

        finish();
    }

    private void getGoalInfo() {
        typeGroup.getCheckedId();
        methodGroup.getCheckedId();
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