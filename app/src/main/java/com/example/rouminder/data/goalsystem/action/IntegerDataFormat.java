package com.example.rouminder.data.goalsystem.action;

import android.os.Parcel;

/**
 * Base DataFormat for Integer type value
 */
public class IntegerDataFormat extends BaseDataFormat<Integer, IntegerDataFormat> {
    static final String TYPE_NAME = "integer";

    protected Integer value = 0;

    public IntegerDataFormat(String unit) {
        super(unit);
    }

    public IntegerDataFormat(String unit, int value) {
        super(unit, (Integer) value);
    }

    public IntegerDataFormat(IntegerDataFormat data) {
        this(data.unit, data.value);
    }

    /**
     * Set a value of data from primary type.
     *
     * @param value a value to be set.
     */
    @Override
    public void set(Integer value) {
        super.set(Math.max(value, 0));
    }

    /**
     * Set a value of data from another data.
     *
     * @param data a data to be set.
     */
    public void set(IntegerDataFormat data) {
        set(data.value);
    }

    /**
     * Get a copy of data.
     *
     * @return a new data.
     */
    @Override
    public IntegerDataFormat copy() {
        return new IntegerDataFormat(this);
    }

    /**
     * Get another default-value data.
     *
     * @return a
     */
    @Override
    public IntegerDataFormat another() {
        return new IntegerDataFormat(unit);
    }

    /**
     * Add another value to data.
     *
     * @param data a data to be added.
     */
    @Override
    public IntegerDataFormat add(IntegerDataFormat data) {
        return new IntegerDataFormat(this.unit, this.value + data.value);
    }

    /**
     * Subtract the value to a given value.
     *
     * @param data a value to subtract
     * @return a result of subtraction
     */
    @Override
    public IntegerDataFormat sub(IntegerDataFormat data) {
        return new IntegerDataFormat(this.unit, this.value - data.value);
    }

    /**
     * Extract numerical summarization from the value
     *
     * @return an integer.
     */
    @Override
    public int numerize() {
        return 0;
    }

}
