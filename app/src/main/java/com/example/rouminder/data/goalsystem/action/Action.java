package com.example.rouminder.data.goalsystem.action;

import androidx.annotation.Nullable;

/**
 * A class for Action
 *
 * @param <D> a data format for the action's progress. Provides getter and setter for value, string format.
 */
public abstract class Action<D extends BaseDataFormat<?, D>> implements Comparable<Action<D>> {
    protected static final String TYPE_NAME = "";

    private final ActionManager manager;
    protected D data;
    private int actionID;

    public Action(@Nullable ActionManager manager, int actionID, D data) {
        this.manager = manager;
        this.actionID = actionID;
        this.data = data;
    }

    /**
     * Get a type name of Action.
     *
     * @return type name in a string
     */
    public static String getTypeName() {
        return TYPE_NAME;
    }

    public int getID() {
        return actionID;
    }

    void setID(int actionID) {
        this.actionID = actionID;
    }

    /**
     * Get a data.
     *
     * @return a copy of data
     */
    public D getData() {
        return data;
    }

    /**
     * Set the data
     *
     * @param data a data to be set
     */
    public void setData(D data) {
        this.data.set(data);
    }

    public ActionInstance<D> createInstance() {
        return new ActionInstance<>(manager, -1, getID(), data.another());
    }

    /**
     * Commit new data.
     *
     * @param data a data to be committed.
     */
    public void commit(D data) {
        this.data.set(data);
    }

    @Override
    public int compareTo(Action<D> other) {
        return Integer.compare(getID(), other.getID());
    }
}
