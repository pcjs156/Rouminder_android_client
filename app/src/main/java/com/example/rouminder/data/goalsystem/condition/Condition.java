package com.example.rouminder.data.goalsystem.condition;

import com.example.rouminder.data.goalsystem.action.Action;
import com.example.rouminder.data.goalsystem.action.BaseDataFormat;


public abstract class Condition<D extends BaseDataFormat<?, D>> implements Comparable<Condition<D>>{
    protected final int id;
    private final Action<D> action;


    public Condition(int id, Action<D> action) {
        this.id = id;
        this.action = action;
    }

    public int getId() {
        return id;
    }

    public ConditionInstance<D> createInstance() {
        return new ConditionInstance<>(this, action.createInstance());
    }

    public abstract boolean isAccomplished(D data);

    @Override
    public int compareTo(Condition<D> condition) {
        return Integer.compare(getId(), condition.getId());
    }
}
