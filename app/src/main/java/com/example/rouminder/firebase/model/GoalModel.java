package com.example.rouminder.firebase.model;

import com.example.rouminder.firebase.manager.BaseModelManager;

import java.util.HashMap;

public class GoalModel {
    // 목표의 id값 (primary key)
    public final String id;

    // 해당 목표의 소유자의 uid (foreign key)
    public final String uid;

    // 목표명
    public String name;
    // 목표의 유형
    public final String type;
    // 목표의 달성 현황 (boolean type의 경우 0, 1로 false, true를 표기)
    public int current;
    // 태그
    public String tag;
    // 하이라이트(Color hex code)
    public String highlight;

    // 목표 시작 일시
    public String startDatetime;
    // 목표 종료 일시
    public String finishDatetime;

    // 생성일시
    public final String createdAt;
    // 수정일시
    public String modifiedAt;

    public GoalModel(String id, String uid, String createdAt, String modifiedAt,
                     String name, String type, int current, String tag, String highlight,
                     String startDatetime, String finishDatetime) {
        this.id = id;
        this.uid = uid;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.name = name;
        this.type = type;
        this.current = current;
        this.tag = tag;
        this.highlight = highlight;
        this.startDatetime = startDatetime;
        this.finishDatetime = finishDatetime;
    }

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

        Object currentObj = values.get("current");
        if (currentObj != null && currentObj instanceof String)
            this.current = Integer.parseInt((String) currentObj);
        else if (currentObj != null && currentObj instanceof Integer)
            this.current = (Integer) currentObj;

        this.tag = (String) values.get("tag");
        this.highlight = (String) values.get("highlight");

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
        info.put("highlight", highlight);
        info.put("start_datetime", startDatetime);
        info.put("finish_datetime", finishDatetime);

        return info;
    }
}
