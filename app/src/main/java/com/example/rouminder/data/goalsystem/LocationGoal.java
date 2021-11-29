package com.example.rouminder.data.goalsystem;

import android.annotation.SuppressLint;
import android.graphics.Color;

import java.time.LocalDateTime;

public class LocationGoal extends Goal {
    private static String CHECKED = "완료";
    private static String UNCHECKED = "미완료";

    private double lat;
    private double lng;

    /**
     * A constructor for a Goal.
     *
     * @param id      an identifier.
     * @param name    a name of the goal.
     * @param from    when the goal should start; closed start point of the range.
     * @param to      when the goal should end; open end point of the range.
     * @param current a current progress of the goal.
     * @param target  a target progress of the goal.
     */
    public LocationGoal(GoalManager gm, int id, String name, LocalDateTime from, LocalDateTime to,
                        int current, int target, double lat, double lng, Color highlight, String tag) {
        super(gm, id, name, from, to, current, target, highlight, tag);
        this.lat = lat;
        this.lng = lng;
    }

    /**
     * Get a latitude.
     *
     * @return a unit String.
     */
    public double getLat() {
        return lat;
    }

    /**
     * Set a latitude double.
     *
     * @param lat a latitude double.
     */
    public void setLat(double lat) {
        this.lat = lat;
        update();
    }

    /**
     * Get a longitude.
     *
     * @return a unit String.
     */
    public double getLng() {
        return lng;
    }

    /**
     * Set a longitude double.
     *
     * @param lng a longitude double.
     */
    public void setLng(double lng) {
        this.lng = lng;
        update();
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
