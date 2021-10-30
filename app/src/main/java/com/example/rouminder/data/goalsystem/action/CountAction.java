package com.example.rouminder.data.goalsystem.action;

public class CountAction extends Action<IntegerDataFormat> {
    protected static final String TYPE_NAME = "count";

    /**
     * Create Action with id and data.
     *
     * @param id   a unique integer id
     * @param data a data in DataFormat
     */
    public CountAction(int id, IntegerDataFormat data) {
        super(id, data);
    }
}
