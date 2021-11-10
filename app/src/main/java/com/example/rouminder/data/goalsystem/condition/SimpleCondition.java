package com.example.rouminder.data.goalsystem.condition;

import androidx.annotation.Nullable;

import com.example.rouminder.data.goalsystem.GoalManager;
import com.example.rouminder.data.goalsystem.action.BaseDataFormat;

public class SimpleCondition<D extends BaseDataFormat<?, D>> extends Condition<D> {
    private final D target;

    public SimpleCondition(@Nullable GoalManager manager, int conditionID, int actionID, D target) {
        super(manager, conditionID, actionID);
        this.target = target;
    }

    @Override
    public boolean isAccomplished(D data) {
        return target.compareTo(data) >= 0;
    }
}
