package com.example.rouminder.data.goalsystem;

import android.graphics.Color;

import java.time.LocalDateTime;

public class CheckGoal extends Goal {
    private static String CHECKED = "완료";
    private static String UNCHECKED = "미완료";

    public CheckGoal(GoalManager manager, int id, String name, LocalDateTime from, LocalDateTime to,
                     int current, Color highlight, String tag) {
        super(manager, id, name, from, to, current, 1, highlight, tag);
    }

    /**
     * Get a goal's target progress in a string form.
     *
     * @return an empty string since there is no need for target as String.
     */
    @Override
    public String targetToString() {
        return "";
    }

    /**
     * Get a goal's current progress in a string form.
     *
     * @return a String representing its current progress.
     */
    @Override
    public String currentToString() {
        return isAccomplished() ? CHECKED : UNCHECKED;
    }

    /**
     * Get a goal's overall progress in a string form.
     * e.g. "달성", "1/2회"
     *
     * @return String representing its overall progress.
     */
    @Override
    public String progressToString() {
        return currentToString();
    }

    /**
     * Return if a goal is accomplished or not.
     *
     * @return true if a goal is accomplished, otherwise false.
     */
    @Override
    public boolean isAccomplished() {
        return getChecked();
    }

    public boolean getChecked() {
        return current == target;
    }

    public void setChecked(boolean checked) {
        setCurrent(checked ? target : 0);
        update();
    }
}
