package com.example.rouminder.firebase.model;

import com.example.rouminder.firebase.manager.BaseModelManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RepeatPlanModel {
    // 플랜의 id값 (primary key)
    public String id;

    // 해당 플랜 소유자의 uid (foreign key)
    public String uid;

    // 일정 생성 계획
    public ArrayList<Boolean> weekPlan;

    // 플랜으로 생성될 목표명
    public String name;
    // 플랜으로 생성될 목표의 유형 (general, repeat, complex)
    public final String type = "repeat";
    // 플랜으로 생성될 목표 수행 방법 (check, count, location)
    public String method;
    // 플랜으로 생성될 목표의 태그
    public String tag;
    // 플랜으로 생성될 목표의 하이라이트(Color hex code)
    public String highlight;

    // 플랜으로 생성될 목표의 달성 목표 횟수 (check, location은 항상 1)
    public int targetCount;

    /* count용 */
    // 플랜으로 생성될 목표의 단위 (3컵, 2회, ...)
    public String unit;

    /* 플랜으로 생성될 목표의 위도/경도 */
    public Double latitude;
    public Double longitude;

    // 생성일시
    public String createdAt;
    // 수정일시
    public String modifiedAt;

    public RepeatPlanModel(HashMap<String, Object> values) {
        if (values.get("id") == null)
            this.id = BaseModelManager.getRandomId();
        else
            this.id = (String) values.get("id");

        this.uid = BaseModelManager.getInstance().getUid();

        this.createdAt = (String) values.getOrDefault("created_at", BaseModelManager.getTimeStampString());
        this.modifiedAt = (String) values.getOrDefault("modified_at", "");

        this.name = (String) values.get("name");

        this.tag = (String) values.get("tag");
        this.method = (String) values.get("method");
        this.highlight = (String) values.get("highlight");

        this.weekPlan = (ArrayList<Boolean>) values.get("week_plan");

        if (this.method.equals("count")) {
            if (values.get("target_count") instanceof Integer)
                this.targetCount = (Integer) values.get("target_count");
            else
                this.targetCount = ((Long) values.get("target_count")).intValue();
            this.unit = (String) values.get("unit");
        } else {
            this.targetCount = 1;

            if (this.method.equals("location")) {
                this.latitude = (Double) values.get("latitude");
                this.longitude = (Double) values.get("longitude");
            }
        }
    }

    @Override
    public String toString() {
        return "RepeatPlanModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public RepeatPlanModel update(HashMap<String, Object> newValues) {
        String timeStampString = BaseModelManager.getTimeStampString();
        this.modifiedAt = timeStampString;

        this.name = (String) newValues.get("name");

        this.tag = (String) newValues.get("tag");
        this.highlight = (String) newValues.get("highlight");
        this.targetCount = (Integer) newValues.get("target_count");

        this.weekPlan = (ArrayList<Boolean>) newValues.get("week_plan");

        if (method.equals("count"))
            this.unit = (String) newValues.get("unit");
        else if (method.equals("location")) {
            this.latitude = (Double) newValues.get("latitude");
            this.longitude = (Double) newValues.get("longitude");
        }

        return this;
    }

    public HashMap<String, Object> getInfo() {
        HashMap<String, Object> info = new HashMap<>();

        info.put("id", id);
        info.put("uid", uid);
        info.put("created_at", createdAt);
        info.put("modified_at", modifiedAt);
        info.put("name", name);
        info.put("type", type);
        info.put("tag", tag);
        info.put("method", method);
        info.put("highlight", highlight);
        info.put("target_count", targetCount);
        info.put("week_plan", weekPlan);

        if (method.equals("count"))
            info.put("unit", unit);
        else if (method.equals("location")) {
            info.put("latitude", latitude);
            info.put("longitude", longitude);
        }

        return info;
    }
}
