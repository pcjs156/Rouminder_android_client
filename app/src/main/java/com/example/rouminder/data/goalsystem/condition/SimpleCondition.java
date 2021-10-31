package com.example.rouminder.data.goalsystem.condition;

import com.example.rouminder.data.goalsystem.action.Action;
import com.example.rouminder.data.goalsystem.action.BaseDataFormat;

public class SimpleCondition<D extends BaseDataFormat<?, D>> extends Condition<D>{
    private final D target;

    public SimpleCondition(int id, Action<D> action, D target) {
        super(id, action);
        this.target = target;
    }

    @Override
    public boolean isAccomplished(D data) {
        return target.compareTo(data) >= 0;
    }
}
