package com.example.rouminder.models.schedulesystem.unit;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleUnit extends Unit {
    private final Domain domain;
    private final String id;

    protected SimpleUnit(String id, Domain domain) {
        super(Stream.of(domain).collect(Collectors.toList()));
        this.domain = domain;
        this.id = id;
    }

    public SimpleUnit(SimpleUnits simpleUnits) {
        this(simpleUnits.id, simpleUnits.domain);
    }

    /**
     * Get an ID for a simple unit.
     * e.g. "minute", "hour", ...
     *
     * @return an ID string.
     */
    public String getId() {
        return id;
    }

    public int get() {
        return getValues().get(0);
    }

    public void set(int value) {
        setValues(value);
    }

    /**
     * Get another unit object with identical domains from a unit.
     *
     * @return another unit with identical domains.
     */
    @Override
    public Unit another() {
        return new SimpleUnit(id, domain);
    }

    /**
     * Get a copy of a unit.
     *
     * @return a copy of a unit.
     */
    @Override
    public Unit copy() {
        SimpleUnit copy = new SimpleUnit(id, domain);
        copy.set(get());
        return copy;
    }
}
