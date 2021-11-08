package com.example.rouminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;

public class AddGoalActivity extends AppCompatActivity {
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    public EditText goalName;
    public Spinner categoriesSpinner;
    public Spinner highlightsSpinner;
    public SingleSelectToggleGroup typeGroup;
    public SingleSelectToggleGroup methodGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        LinearLayout methodLayout = (LinearLayout) findViewById(R.id.method);
        LinearLayout countLayout = (LinearLayout) findViewById(R.id.count);
        RelativeLayout mapLayout = (RelativeLayout) findViewById(R.id.map);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar_add_goal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String[] categories = { "카테고리1", "카테고리2", "카테고리3" };
        String[] highlights = { "하이라이트1", "하이라이트2", "하이라이트3", "하이라이트4", "하이라이트5" };
        ArrayAdapter categoriesAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories);
        ArrayAdapter highlightsAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, highlights);

        categoriesSpinner = (Spinner) findViewById(R.id.spinnerCategory);
        highlightsSpinner = (Spinner) findViewById(R.id.spinnerHighlight);

        categoriesSpinner.setAdapter(categoriesAdapter);
        highlightsSpinner.setAdapter(highlightsAdapter);

//        나중에 Spinner.getSelectedItem()으로 선택 값 가져오기

        typeGroup = (SingleSelectToggleGroup) findViewById(R.id.groupChoicesTypes);
        methodGroup = (SingleSelectToggleGroup) findViewById(R.id.groupChoicesMethod);
        typeGroup.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                if (checkedId == R.id.choiceGeneral) {
                    methodLayout.setVisibility(View.VISIBLE);
                }
                else if (checkedId == R.id.choiceRepeat) {
                    methodLayout.setVisibility(View.VISIBLE);
                }
                else if (checkedId == R.id.choiceComplex) {
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
                }
                else if (checkedId == R.id.choiceCount) {
                    countLayout.setVisibility(View.VISIBLE);
                    mapLayout.setVisibility(View.GONE);
                }
                else if (checkedId == R.id.choiceLocation) {
                    countLayout.setVisibility(View.GONE);
                    mapLayout.setVisibility(View.VISIBLE);
                }
            }
        });

//        add goal 완료 버튼은 showCustom을 사용하여 나중에 추가할 계획
//        getSupportActionBar().setDisplayShowCustomEnabled();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
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
}