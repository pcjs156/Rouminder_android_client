package com.example.rouminder.data.goalsystem;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;

/**
 * A class for Goal.
 */
public class Goal implements Comparable<Goal> {
    private int id;
    private final GoalManager manager;
    protected int current = 0;
    protected int target = 0;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String name;
    private Color highlight;
    private Drawable icon;


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
    public Goal(GoalManager manager, int id, String name, LocalDateTime from, LocalDateTime to, int current, int target) {
        this.manager = manager;
        this.id = id;
        this.name = name;
        this.startTime = from;
        this.endTime = to;
        this.current = current;
        this.target = target;
    }

    /**
     * A constructor for a goal of temporary use in querying.
     *
     * @param from when the goal should start; closed start point of the range.
     * @param to   when the goal should end; open end point of the range.
     */
    Goal(LocalDateTime from, LocalDateTime to) {
        this.manager = null;
        this.id = -1;
        this.name = null;
        this.startTime = from;
        this.endTime = to;
    }

    protected void update() {
        if (manager != null)
            manager.updateGoal(getId());
    }

    /**
     * Get an identifier of a goal.
     *
     * @return an identifier.
     */
    public int getId() {
        return id;
    }

    /**
     * Set an identifier of a goal.
     * @param id an identifier
     */
    void setId(int id) {this.id = id;}

    /**
     * Get a name of a goal.
     *
     * @return a name of a goal in a String.
     */
    public String getName() {
        return name;
    }

    /**
     * Set a name of a goal.
     *
     * @param name a name to be set.
     */
    public void setName(String name) {
        update();
        this.name = name;
    }

    /**
     * Get a start time of a goal.
     *
     * @return a start time.
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Set start time of a goal.
     *
     * @param startTime a time to be set.
     */
    public void setStartTime(LocalDateTime startTime) {
        Runnable r = () -> {this.startTime = startTime;};
        if(manager != null)
            manager.updateGoalTime(getId(), r);
        else
            r.run();
    }

    /**
     * Get an end time of a goal.
     *
     * @return an end time.
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Set an end time of a goal.
     *
     * @param endTime a time to be set.
     */
    public void setEndTime(LocalDateTime endTime) {
        Runnable r = () -> {this.endTime = endTime;};
        if(manager != null)
            manager.updateGoalTime(getId(), r);
        else
            r.run();
    }

    /**
     * Check if the goal instance shouldn't have been start.
     *
     * @param now a LocalDateTime object to be tested.
     * @return true if now is before startTime, otherwise false.
     */
    public boolean isBeforeStart(LocalDateTime now) {
        return now.isBefore(startTime);
    }

    /**
     * Check if the goal instance should have been finished.
     *
     * @param now a LocalDateTime object to be tested.
     * @return true if now is after endTime, otherwise false.
     */
    public boolean isAfterEnd(LocalDateTime now) {
        return now.isAfter(endTime);
    }

    /**
     * Check if the goal instance should have been finished.
     *
     * @param now a LocalDateTime object to be tested.
     * @return false if isBeforeStart(now) or isAfterEnd(now), otherwise true.
     */
    public boolean isOnProgress(LocalDateTime now) {
        return !isBeforeStart(now) && !isAfterEnd(now);
    }

    /**
     * Get an icon of the goal.
     *
     * @return a Drawable object.
     */
    public Drawable getIcon() {
        return icon;
    }

    /**
     * Set an icon for the goal.
     *
     * @param icon a Drawable object to be set.
     */
    public void setIcon(Drawable icon) {
        this.icon = icon;
        update();
    }

    /**
     * Get a highlight color of the goal.
     *
     * @return a Color object.
     */
    public Color getHighlight() {
        return highlight;
    }

    /**
     * Set a highlight color for the goal.
     *
     * @param highlight a Color object to be set.
     */
    public void setHighlight(Color highlight) {
        this.highlight = highlight;
        update();
    }

    /**
     * Get a goal's target progress in a number.
     *
     * @return an integer representing target progress which is larger than or equal to zero.
     */
    public int getTarget() {
        return target;
    }

    public int getCurrent() {
        return current;
    }

    protected void setCurrent(int current) {
        this.current = current;
        update();
    }

    /**
     * Get a goal's target progress in a string form.
     *
     * @return a String representing its target progress.
     */
    public String targetToString() {
        return null;
    }

    /**
     * Get a goal's current progress in a string form.
     *
     * @return a String representing its current progress.
     */
    public String currentToString() {
        return null;
    }

    /**
     * Get a goal's overall progress in a string form.
     * e.g. "달성", "1/2회"
     *
     * @return String representing its overall progress.
     */
    public String progressToString() {
        return null;
    }

    /**
     * Return if a goal is accomplished or not.
     *
     * @return true if a goal is accomplished, otherwise false.
     */
    public boolean isAccomplished() {
        return false;
    }

    /**
     * Get a String representing the type of a goal.
     *
     * @return a name of the associated enum
     */
    public String getType() {
        return Goal.Type.fromClass(getClass()).name();
    }

    @Override
    public int compareTo(Goal goal) {
        return Integer.compare(getId(), goal.getId());
    }

    public enum Type {
        COUNT(CountGoal.class),
        CHECK(CheckGoal.class),
        LOCATION(LocationGoal.class);

        private Class<? extends Goal> clazz;

        Type(Class<? extends Goal> clazz) {
            this.clazz = clazz;
        }

        public static Type fromClass(Class<? extends Goal> clazz) {
            return Arrays.stream(Type.values()).filter(type -> type.clazz == clazz).findFirst().orElse(null);
        }

        public Class<? extends Goal> toClass() {
            return clazz;
        }
    }
}
