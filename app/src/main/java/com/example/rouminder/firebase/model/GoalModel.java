package com.example.rouminder.firebase.model;

import com.example.rouminder.firebase.manager.BaseModelManager;

import java.util.HashMap;
import java.util.Date;

public class GoalModel {
    // 목표의 id값 (primary key)
    public String id;

    // 해당 목표의 소유자의 uid (foreign key)
    public String uid;

    // (type이 repeat인 경우)
    // 해당 목표를 생성한 plan의 id
    public String plan;

    // 목표명
    public String name;
    // 목표의 유형 (general, repeat, complex)
    public String type;
    // 목표 수행 방법 (check, count, location)
    public String method;
    // 목표의 달성 현황 (boolean type의 경우 0, 1로 false, true를 표기)
    public int current;
    // 태그
    public String tag;
    // 하이라이트(Color hex code)
    public String highlight;

    // 목표 count (check, location은 항상 1)
    public int targetCount;

    /* count용 */
    // 단위 (3컵, 2회, ...)
    public String unit;

    /* 위도/경도 */
    public Double latitude;
    public Double longitude;

    // 목표 시작 일시
    public String startDatetime;
    // 목표 종료 일시
    public String finishDatetime;

    // 생성일시
    public String createdAt;
    // 수정일시
    public String modifiedAt;

    public GoalModel(HashMap<String, Object> values) {
        if (values.get("id") == null)
            this.id = BaseModelManager.getRandomId();
        else
            this.id = (String) values.get("id");

        this.uid = BaseModelManager.getInstance().getUid();

        this.createdAt = (String) values.getOrDefault("created_at", BaseModelManager.getTimeStampString());
        this.modifiedAt = (String) values.getOrDefault("modified_at", "");

        this.name = (String) values.get("name");
        this.type = (String) values.get("type");

        if (this.type.equals("repeat"))
            this.plan = (String) values.get("plan");

        Object currentObj = values.get("current");
        if (currentObj != null && currentObj instanceof String)
            this.current = Integer.parseInt((String) currentObj);
        else if (currentObj != null && currentObj instanceof Integer)
            this.current = (Integer) currentObj;
        else if (currentObj != null && currentObj instanceof Long)
            this.current = ((Long) currentObj).intValue();

        this.tag = (String) values.get("tag");
        this.method = (String) values.get("method");
        this.highlight = (String) values.get("highlight");

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

        this.startDatetime = (String) values.get("start_datetime");
        this.finishDatetime = (String) values.get("finish_datetime");
    }

    @Override
    public String toString() {
        return "GoalModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public GoalModel update(HashMap<String, Object> newValues) {
        String timeStampString = BaseModelManager.getTimeStampString();
        this.modifiedAt = timeStampString;

        this.name = (String) newValues.get("name");

        Object currentObj = newValues.get("current");
        if (currentObj != null && currentObj instanceof String)
            this.current = Integer.parseInt((String) currentObj);
        else if (currentObj != null && currentObj instanceof Integer)
            this.current = (Integer) currentObj;

        this.tag = (String) newValues.get("tag");
        this.highlight = (String) newValues.get("highlight");
        this.targetCount = (Integer) newValues.get("target_count");

        if (method.equals("count"))
            this.unit = (String) newValues.get("unit");
        else if (method.equals("location")) {
            this.latitude = (Double) newValues.get("latitude");
            this.longitude = (Double) newValues.get("longitude");
        }

        this.startDatetime = (String) newValues.get("start_datetime");
        this.finishDatetime = (String) newValues.get("finish_datetime");

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
        info.put("current", current);
        info.put("tag", tag);
        info.put("method", method);
        info.put("highlight", highlight);
        info.put("target_count", targetCount);

        if (method.equals("repeat"))
            info.put("plan", plan);

        if (method.equals("count"))
            info.put("unit", unit);
        else if (method.equals("location")) {
            info.put("latitude", latitude);
            info.put("longitude", longitude);
        }

        info.put("start_datetime", startDatetime);
        info.put("finish_datetime", finishDatetime);

        return info;
    }

    public boolean isAchieved() {
        if (method.equals("check") || method.equals("location"))
            return this.current == 1;
        else if (method.equals("count"))
            return this.current == this.targetCount;
        else
            return false;
    }
}
