package com.example.rouminder.data.goalsystem.action;

import androidx.annotation.NonNull;

/**
 * Base abstract class for DataFormat
 *
 * @param <T> value's internal type
 * @param <S> self
 */
public abstract class BaseDataFormat<T, S extends BaseDataFormat<T, S>>
        implements Comparable<S> {

    protected static final String TYPE_NAME = "";

    protected final String unit;
    protected T value;


    protected BaseDataFormat(String unit) {
        this.unit = unit;
    }

    protected BaseDataFormat(String unit, T value) {
        this.unit = unit;
        this.value = value;
    }

    private static String getTypeName() {
        return TYPE_NAME;
    }

    /**
     * Get a unit of data.
     *
     * @return a String
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Get a value of data.
     *
     * @return a value of data in primary type.
     */
    public T get() {
        return value;
    }

    /**
     * Set a value of data from primary type.
     *
     * @param value a value to be set.
     */
    public void set(T value) {
        this.value = value;
    }

    /**
     * Set a value of data from another data.
     *
     * @param data a data to be set.
     */
    public abstract void set(S data);

    /**
     * Get a copy of data.
     *
     * @return a new data.
     */
    public abstract S copy();

    /**
     * Get another default-value data.
     *
     * @return a
     */
    public abstract S another();

    /**
     * Add another value to data.
     *
     * @param data a data to be added.
     */
    public abstract S add(S data);


    /**
     * Subtract the value to a given value.
     *
     * @param data a value to subtract
     * @return a result of subtraction
     */
    public abstract S sub(S data);

    /**
     * Extract numerical summarization from the value
     */
    public abstract int numerize();

    /**
     * Compare source to another data.
     *
     * @param data a data to compare with.
     * @return compareTo result of two value
     */
    @Override
    public int compareTo(S data) {
        return sub(data).numerize();
    }

    /**
     * Wrap in string with unit prefix.
     *
     * @return a wrapped string.
     */
    @NonNull
    @Override
    public String toString() {
        return value.toString() + unit;
    }
}