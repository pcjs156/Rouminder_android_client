package com.example.rouminder.data.goalsystem.goal;

import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.example.rouminder.data.goalsystem.action.BaseDataFormat;
import com.example.rouminder.data.goalsystem.condition.Condition;
import com.example.rouminder.data.goalsystem.action.Action;

import java.time.LocalDateTime;

/**
 * Abstract class for Goal, which is responsible for content part of 'Goal Feature'.
 */
public abstract class Goal implements Comparable<Goal>{
    private final int id;
    private final Condition<?> condition;
    private final Action<? extends BaseDataFormat<?, ?>> action;
    private String name;


    public Goal(int id, String name, Condition<?> condition, Action<?> action) {
        this.id = id;
        this.name = name;
        this.condition = condition;
        this.action = action;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Condition<?> getCondition() {
        return condition;
    }

    public GoalInstance createInstance(LocalDateTime from, LocalDateTime to) {
        return new GoalInstance(this, condition.createInstance(), action.createInstance(), from, to);
    }

    @Override
    public int compareTo(Goal goal) {
        return Integer.compare(getId(), goal.getId());
    }
}
