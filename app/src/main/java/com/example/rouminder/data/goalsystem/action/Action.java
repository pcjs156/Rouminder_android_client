package com.example.rouminder.data.goalsystem.action;

/**
 * A class for Action
 *
 * @param <D> a data format for the action's progress. Provides getter and setter for value, string format.
 */
public abstract class Action<D extends BaseDataFormat<?, D>> implements Comparable<Action<D>>{
    protected static final String TYPE_NAME = "";

    private final int id;
    protected D data;

    /**
     * Create Action with id and data.
     *
     * @param id   a unique integer id
     * @param data a data in DataFormat
     */
    public Action(int id, D data) {
        this.id = id;
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

    public int getId() {
        return id;
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
        return new ActionInstance<>(this, data.another());
    }

    /**
     * Commit new data.
     * @param data a data to be committed.
     */
    public void commit(D data) {
        this.data.set(data);
    }

    @Override
    public int compareTo(Action<D> dAction) {
        return Integer.compare(getId(), dAction.getId());
    }
}
