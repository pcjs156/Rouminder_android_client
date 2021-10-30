package com.example.rouminder.data.goalsystem.condition;

import com.example.rouminder.data.goalsystem.action.ActionInstance;
import com.example.rouminder.data.goalsystem.action.BaseDataFormat;

public class ConditionInstance<D extends BaseDataFormat<?, D>> implements Comparable<ConditionInstance<D>> {
    private final Condition<D> condition;
    private final ActionInstance<D> actionInstance;

    public ConditionInstance(Condition<D> condition, ActionInstance<D> actionInstance) {
        this.condition = condition;
        this.actionInstance = actionInstance;
    }

    public Condition<D> getCondition() {
        return condition;
    }

    /**
     * Compare a data with target data to check if accomplished
     *
     * @return true if accomplished.
     */
    public boolean isAccomplished() {
        return condition.isAccomplished(actionInstance.getData());
    }

    @Override
    public int compareTo(ConditionInstance<D> dConditionInstance) {
        return getCondition().compareTo(dConditionInstance.getCondition());
    }
}
