package com.example.rouminder.firebase.model;

public class ActionModel {
    public final String id;
    public final String uid;
    public final String created_at;
    public final String modified_at;
    public final String type;
    public final String unit;

    public ActionModel(String id, String uid, String created_at, String modified_at, String type, String unit) {
        this.id = id;
        this.uid = uid;
        this.created_at = created_at;
        this.modified_at = modified_at;
        this.type = type;
        this.unit = unit;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean verbose) {
        String ret = String.format("[%s] type: '%s' / unit: '%s'",
                id, type, unit);
        if (verbose) {
            ret += String.format(" / created on '%s' / modified on '%s' / by '%s'",
                    created_at, modified_at, uid);
            return ret;
        } else {
            return ret;
        }
    }
}
