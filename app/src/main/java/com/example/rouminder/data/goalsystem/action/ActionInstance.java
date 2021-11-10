package com.example.rouminder.data.goalsystem.action;

import androidx.annotation.Nullable;

public class ActionInstance<D extends BaseDataFormat<?, D>> implements Comparable<ActionInstance<D>> {
    private final ActionManager manager;
    private final int actionInstanceID;
    private final int actionID;
    private final D data;

    public ActionInstance(@Nullable ActionManager manager, int actionInstanceID, int actionID, D data) {
        this.manager = manager;
        this.actionInstanceID = actionInstanceID;
        this.actionID = actionID;
        this.data = data;
    }

    public int getID() {
        return actionID;
    }

    @SuppressWarnings("unchecked")
    public Action<D> getAction() {
        return manager == null ? null : (Action<D>) manager.getAction(actionID);
    }

    /**
     * Get a copy of the data.
     *
     * @return an new Data object or null if failed to create.
     */
    public D getData() {
        return data.copy();
    }

    /**
     * Set data's value.
     *
     * @param data a data to be assigned.
     */
    public void setData(D data) {
        this.data.set(data);
    }


    @Override
    public int compareTo(ActionInstance<D> other) {
        return getAction().compareTo(other.getAction());
    }
}
