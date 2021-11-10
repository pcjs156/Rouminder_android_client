package com.example.rouminder.data.goalsystem.condition;

import androidx.annotation.Nullable;

import com.example.rouminder.data.goalsystem.GoalManager;
import com.example.rouminder.data.goalsystem.action.Action;
import com.example.rouminder.data.goalsystem.action.BaseDataFormat;


public abstract class Condition<D extends BaseDataFormat<?, D>> implements Comparable<Condition<D>> {
    private final GoalManager manager;
    private final int actionID;
    private int conditionID;


    public Condition(@Nullable GoalManager manager, int conditionID, int actionID) {
        this.manager = manager;
        this.conditionID = conditionID;
        this.actionID = actionID;
    }

    public int getID() {
        return conditionID;
    }

    void setID(int conditionID) {
        this.conditionID = conditionID;
    }

    @SuppressWarnings("unchecked")
    public Action<D> getAction() {
        return manager == null ? null : (Action<D>) manager.getActionManager().getAction(actionID);
    }

    public ConditionInstance<D> createInstance() {
        ConditionInstance<D> conditionInstance = new ConditionInstance<D>(manager,
                -1,
                getID(),
                getAction().createInstance().getID());

        if (manager == null) {
            return conditionInstance;
        } else if (manager.addConditionInstance(conditionInstance)) {
            return conditionInstance;
        } else {
            return null;
        }
    }

    public abstract boolean isAccomplished(D data);

    @Override
    public int compareTo(Condition<D> condition) {
        return Integer.compare(getID(), condition.getID());
    }
}
