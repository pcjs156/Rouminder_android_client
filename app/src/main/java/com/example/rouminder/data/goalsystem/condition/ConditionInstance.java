package com.example.rouminder.data.goalsystem.condition;

import androidx.annotation.Nullable;

import com.example.rouminder.data.goalsystem.GoalManager;
import com.example.rouminder.data.goalsystem.action.ActionInstance;
import com.example.rouminder.data.goalsystem.action.BaseDataFormat;

public class ConditionInstance<D extends BaseDataFormat<?, D>> implements Comparable<ConditionInstance<D>> {
    private final GoalManager manager;
    private final int conditionID;
    private final int actionInstanceID;
    private int conditionInstanceID;

    public ConditionInstance(@Nullable GoalManager manager, int conditionInstanceID, int conditionID, int actionInstanceID) {
        this.manager = manager;
        this.conditionInstanceID = conditionInstanceID;
        this.conditionID = conditionID;
        this.actionInstanceID = actionInstanceID;
    }

    public int getID() {
        return conditionInstanceID;
    }

    void setID(int conditionInstanceID) {
        this.conditionInstanceID = conditionInstanceID;
    }

    @SuppressWarnings("unchecked")
    public Condition<D> getCondition() {
        return manager == null ? null : (Condition<D>) manager.getCondition(conditionID);
    }

    @SuppressWarnings("unchecked")
    public ActionInstance<D> getActionInstance() {
        return manager == null ? null : (ActionInstance<D>) manager.getActionManager().getActionInstance(actionInstanceID);
    }

    /**
     * Compare a data with target data to check if accomplished
     *
     * @return true if accomplished.
     */
    public boolean isAccomplished() {
        return getCondition().isAccomplished(getActionInstance().getData());
    }

    @Override
    public int compareTo(ConditionInstance<D> dConditionInstance) {
        return getCondition().compareTo(dConditionInstance.getCondition());
    }
}
