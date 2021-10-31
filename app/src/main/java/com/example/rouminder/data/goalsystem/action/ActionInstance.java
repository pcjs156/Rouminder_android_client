package com.example.rouminder.data.goalsystem.action;

public class ActionInstance<D extends BaseDataFormat<?, D>> implements Comparable<ActionInstance<D>>{
    private final Action<D> action;
    private final D data;

    public ActionInstance(Action<D> action, D data) {
        this.action = action;
        this.data = data;
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

    /**
     * Get actual Action.
     *
     * @return a Action object.
     */
    public Action<D> getAction() {
        return action;
    }

    @Override
    public int compareTo(ActionInstance<D> dActionInstance) {
        return getAction().compareTo(dActionInstance.getAction());
    }
}
