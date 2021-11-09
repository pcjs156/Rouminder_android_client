package com.example.rouminder.firebase.model;

public class ConditionModel {
    public final String id;
    public final String uid;
    public final String created_at;
    public final String modified_at;
    public final String type;

    public ConditionModel(String id, String uid, String created_at, String modified_at, String type) {
        this.id = id;
        this.uid = uid;
        this.created_at = created_at;
        this.modified_at = modified_at;
        this.type = type;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean verbose) {
        String ret = String.format("[%s] type: '%s'", id, type);
        if (verbose) {
            ret += String.format(" / created on '%s' / modified on '%s' / by '%s'",
                    created_at, modified_at, uid);
            return ret;
        } else {
            return ret;
        }
    }
}
