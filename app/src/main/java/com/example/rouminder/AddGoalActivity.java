package com.example.rouminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.rouminder.firebase.exception.ModelDoesNotExists;
import com.example.rouminder.firebase.manager.BaseModelManager;
import com.example.rouminder.firebase.manager.CategoryModelManager;
import com.example.rouminder.firebase.model.CategoryModel;
import com.example.rouminder.firebase.manager.BaseModelManager;
import com.example.rouminder.firebase.manager.CategoryModelManager;
import com.example.rouminder.firebase.model.CategoryModel;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;
import com.nex3z.togglebuttongroup.button.LabelToggle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.graphics.Color;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import android.graphics.Color;

public class AddGoalActivity extends AppCompatActivity {
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    public Spinner highlightsSpinner;
    public SingleSelectToggleGroup typeGroup;
    public SingleSelectToggleGroup methodGroup;
    public Color selectedColor;

    public CategoryModelManager categoryModelManager = CategoryModelManager.getInstance();
    public ArrayList<CategoryModel> categoryModels = categoryModelManager.getCategories();

    private TextView startDate;
    private TextView startTime;
    private TextView endDate;
    private TextView endTime;

    public static int currentType;
    public static View clickedDate;

    public String uid;

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

        resetDateTime();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar_add_goal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 현재 firebase 오류나서 tag 임의 생성
        categoryModelManager.categories = new ArrayList<CategoryModel>();
        categoryModelManager.create("tag1");
        categoryModelManager.create("tag2");
        categoryModelManager.create("tag3");

        categoryModels = categoryModelManager.getCategories();

        ArrayAdapter tagsAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categoryModels);
        MultiAutoCompleteTextView editTextTag = (MultiAutoCompleteTextView) findViewById(R.id.editTextTag);
        MultiAutoCompleteTextView.CommaTokenizer token = new MultiAutoCompleteTextView.CommaTokenizer();
        editTextTag.setTokenizer(token);
        editTextTag.setAdapter(tagsAdapter);

        int colors[] = {R.drawable.red, R.drawable.blue, R.drawable.green};
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

//        나중에 Spinner.getSelectedItem()으로 선택 값 가져오기
//        각 색깔에 대응하여 Color 인스턴스 설정, Goal 생성할 때 그냥 setHighlight 해주면 될듯.
/*
        highlightsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getItemAtPosition(i);
                switch (selectedItem) {
                    case "빨강색":
                        Log.e("하이라이트아이템", "#ff0000");
                        selectedColor = Color.valueOf(0xffff0000);
                        break;
                    case "초록색":
                        Log.e("하이라이트아이템", "#00ff00");
                        selectedColor = Color.valueOf(0xff00ff00);
                        break;
                    case "파란색":
                        Log.e("하이라이트아이템", "#0000ff");
                        selectedColor = Color.valueOf(0xff0000ff);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.e("하이라이트아이템", "선택지 없음.");
                selectedColor = null;
            }
        });
 */

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
                } else if (checkedId == R.id.choiceRepeat) {
                    dateTime.setVisibility(View.GONE);
                    countLayout.setVisibility(View.GONE);
                    mapLayout.setVisibility(View.GONE);

                    methodGroup.clearCheck();

                    methodLayout.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.choiceComplex) {
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

                    if (currentType == R.id.choiceGeneral) dateTime.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.choiceCount) {
                    countLayout.setVisibility(View.VISIBLE);
                    mapLayout.setVisibility(View.GONE);
                } else if (checkedId == R.id.choiceLocation) {

                    if (currentType == R.id.choiceGeneral) dateTime.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.choiceLocation) {
                    countLayout.setVisibility(View.GONE);
                    mapLayout.setVisibility(View.VISIBLE);

                    if (currentType == R.id.choiceGeneral) dateTime.setVisibility(View.VISIBLE);
                }
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
                cur.getYear(), cur.getMonthValue()-1, cur.getDayOfMonth());
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
            resetDate(clickedDate, year, monthOfYear+1, dayOfMonth);
        }
    };

    private TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            resetTime(clickedDate, hour, minute);
        }
    };


}