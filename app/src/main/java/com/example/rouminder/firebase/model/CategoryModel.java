package com.example.rouminder.firebase.model;

public class CategoryModel {
    public final String id;
    public final String uid;
    public final String created_at;
    public final String modified_at;
    public final String name;

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
}
