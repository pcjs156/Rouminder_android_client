package com.example.rouminder.firebase.model;

public class GoalModel {
    public final String id;
    public final String uid;
    public final String created_at;
    public final String modified_at;
    public final String condition;
    public final String category;
    public final String name;

    public GoalModel(String id, String uid, String created_at, String modified_at,
                     String condition, String category, String name) {
        this.id = id;
        this.uid = uid;
        this.created_at = created_at;
        this.modified_at = modified_at;
        this.condition = condition;
        this.category = category;
        this.name = name;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean verbose) {
        String ret = String.format("[%s] name: '%s' / condition '%s' / category '%s'",
                                    id, name, condition, category);
        if (verbose) {
            ret += String.format(" / created on '%s' / modified on '%s' / by '%s'",
                    created_at, modified_at, uid);
            return ret;
        } else {
            return ret;
        }
    }
}
