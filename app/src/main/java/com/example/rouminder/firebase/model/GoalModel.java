package com.example.rouminder.firebase.model;

import com.example.rouminder.firebase.manager.BaseModelManager;

import java.util.Date;

public class GoalModel {
    // 목표의 id값 (primary key)
    public final String id;

    // 해당 목표의 소유자의 uid (foreign key)
    public final String uid;
    // 소속된 카테고리의 id
    public final String category;

    // 목표명
    public final String name;
    // 목표의 유형
    public final String type;
    // 목표의 달성 현황 (boolean type의 경우 0, 1로 false, true를 표기)
    public final int current;

    // 목표 시작 일시
    public String startDatetime;
    // 목표 종료 일시
    public String finishDatetime;

    // 생성일시
    public final String created_at;
    // 수정일시
    public final String modified_at;

    public GoalModel(String id, String uid, String created_at, String modified_at,
                     String category, String name, String type, int current,
                     String startDatetime, String finishDatetime) {
        this.id = id;
        this.uid = uid;
        this.created_at = created_at;
        this.modified_at = modified_at;
        this.category = category;
        this.name = name;
        this.type = type;
        this.current = current;
        this.startDatetime = startDatetime;
        this.finishDatetime = finishDatetime;
    }

    public GoalModel(String id, String uid, String created_at, String modified_at,
                     String category, String name, String type, int current,
                     Date startDatetime, Date finishDatetime) {
        this(id, uid, created_at, modified_at, category, name, type, current, "", "");

        String startDTString = BaseModelManager.getTimeStampString(startDatetime);
        this.startDatetime = startDTString;
        String finishDTString = BaseModelManager.getTimeStampString(finishDatetime);
        this.finishDatetime = finishDTString;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean verbose) {
        String ret = String.format("[%s] name: '%s' / category '%s'",
                id, name, category);
        if (verbose) {
            ret += String.format(" / created on '%s' / modified on '%s' / by '%s'",
                    created_at, modified_at, uid);
            return ret;
        } else {
            return ret;
        }
    }
}
