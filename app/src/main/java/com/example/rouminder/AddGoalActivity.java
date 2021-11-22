package com.example.rouminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.rouminder.firebase.exception.ModelDoesNotExists;
import com.example.rouminder.firebase.manager.BaseModelManager;
import com.example.rouminder.firebase.manager.GoalModelManager;
import com.example.rouminder.firebase.model.GoalModel;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;
import com.nex3z.togglebuttongroup.button.LabelToggle;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class AddGoalActivity extends AppCompatActivity {
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    public EditText goalName;
    public Spinner tagSpinner;
    public Spinner highlightsSpinner;
    public SingleSelectToggleGroup typeGroup;
    public SingleSelectToggleGroup methodGroup;

    public GoalModelManager goalModelManager = GoalModelManager.getInstance();
    public ArrayList<GoalModel> goalModels = goalModelManager.get();

    public String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        this.uid = BaseModelManager.getInstance().getUid();

        LinearLayout methodLayout = (LinearLayout) findViewById(R.id.method);
        LinearLayout countLayout = (LinearLayout) findViewById(R.id.count);
        RelativeLayout mapLayout = (RelativeLayout) findViewById(R.id.map);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar_add_goal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<String> tags = goalModelManager.getTags();
        ArrayAdapter tagAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tags);

        String[] highlights = {"하이라이트1", "하이라이트2", "하이라이트3", "하이라이트4", "하이라이트5"};
        ArrayAdapter highlightsAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, highlights);

        highlightsSpinner = (Spinner) findViewById(R.id.spinnerHighlight);
        tagSpinner = (Spinner) findViewById(R.id.spinnerTag);

        highlightsSpinner.setAdapter(highlightsAdapter);
        tagSpinner.setAdapter(tagAdapter);

        LabelToggle choiceGeneral = (LabelToggle) findViewById(R.id.choiceGeneral);
        choiceGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> goalIds = new ArrayList<>();
                Log.e("LEN:", "size " + goalModels.size());
                for (GoalModel model : goalModels) {
                    goalIds.add(model.id);
                    Log.e("ID:", model.id);
                }

                Collections.shuffle(goalIds);
                if (!goalIds.isEmpty()) {
                    String pickedId = goalIds.get(0);
                    Log.e("PICKED:", pickedId);
                    GoalModel targetGoal = null;
                    for(GoalModel model: goalModels) {
                        if (model.id.equals(pickedId)) {
                            targetGoal = model;
                            break;
                        }
                    }

                    HashMap<String, Object> newData = targetGoal.getInfo();
                    newData.put("name", "newName");
                    for(String key: newData.keySet()) {
                        Log.e("TEST:", key + " / " + newData.get(key).toString());
                    }
                    try {
                        Log.e("PICKED:", pickedId);
                        goalModelManager.update(pickedId, newData);
                    } catch (ModelDoesNotExists e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "해당 모델이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        LabelToggle choiceRepeat = (LabelToggle) findViewById(R.id.choiceRepeat);
        choiceRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> categoryIds = new ArrayList<>();
                for (GoalModel model : goalModels) {
                    categoryIds.add(model.id);
                }

                Collections.shuffle(categoryIds);
                if (!categoryIds.isEmpty()) {
                    String pickedId = categoryIds.get(0);
                    try {
                        goalModelManager.delete(pickedId);
                    } catch (ModelDoesNotExists e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "해당 모델이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

//        나중에 Spinner.getSelectedItem()으로 선택 값 가져오기

        typeGroup = (SingleSelectToggleGroup) findViewById(R.id.groupChoicesTypes);
        methodGroup = (SingleSelectToggleGroup) findViewById(R.id.groupChoicesMethod);
        typeGroup.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                if (checkedId == R.id.choiceGeneral) {
                    methodLayout.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.choiceRepeat) {
                    methodLayout.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.choiceComplex) {
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
}