package com.example.rouminder.data.goalsystem;

import android.annotation.SuppressLint;
import android.graphics.Color;

import java.time.LocalDateTime;

public class CountGoal extends Goal {
    private String unit;

    public CountGoal(GoalManager manager, int id, String name, LocalDateTime from, LocalDateTime to, int current, int target, String unit, Color highlight) {
        super(manager, id, name, from, to, current, target, highlight);
        this.unit = unit;
    }

    /**
     * Return if a goal is accomplished or not.
     *
     * @return true if a goal is accomplished, otherwise false.
     */
    @Override
    public boolean isAccomplished() {
        return current >= target;
    }

    /**
     * Get a unit String.
     *
     * @return a unit String.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Set a unit String.
     *
     * @param unit a unit String.
     */
    public void setUnit(String unit) {
        this.unit = unit;
        update();
    }

    /**
     * Get a current count of a CountGoal.
     *
     * @return a current count.
     */
    public int getCount() {
        return getCurrent();
    }

    /**
     * Set a current count of a CurrentGoal.
     *
     * @param count a current count to be set; any value smaller than zero is considered zero.
     */
    public void setCount(int count) {
        setCurrent(Math.max(count, 0));
        update();
    }

    /**
     * Get a goal's target progress in a string form.
     * A unit String is added at the end.
     *
     * @return a String representing its target progress.
     */
    @SuppressLint("DefaultLocale")
    @Override
    public String targetToString() {
        return String.format("%d%s", getTarget(), getUnit());
    }

    /**
     * Get a goal's current progress in a string form.
     * A unit String is added at the end.
     *
     * @return a String representing its current progress.
     */
    @SuppressLint("DefaultLocale")
    @Override
    public String currentToString() {
        return String.format("%d%s", getCurrent(), getUnit());
    }

    /**
     * Get a goal's overall progress in a string form.
     * format: <current> / <target>
     *
     * @return String representing its overall progress.
     */
    @Override
    public String progressToString() {
        return currentToString() + " / " + targetToString();
    }
}
