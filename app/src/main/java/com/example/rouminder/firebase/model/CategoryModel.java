package com.example.rouminder.firebase.model;

import com.example.rouminder.firebase.manager.BaseModelManager;

import java.util.HashMap;

public class CategoryModel {
    public final String id;
    public final String uid;
    public final String created_at;
    public String modified_at;
    public String name;

    public CategoryModel(String id, String uid, String created_at, String modified_at, String name) {
        this.id = id;
        this.uid = uid;
        this.created_at = created_at;
        this.modified_at = modified_at;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public CategoryModel update(HashMap<String, Object> newValues) {
        String timeStampString = BaseModelManager.getTimeStampString();
        this.modified_at = timeStampString;

        this.name = (String) newValues.get("name");
        return this;
    }
}
