package com.example.rouminder.models.schedulesystem.unit;


public class CompoundUnit extends Unit{
    private final Domain from;
    private final Domain to;

    public CompoundUnit(Domain from, Domain to) {
        super(Domain.getDomainsFromRange(from, to));
        this.from = from;
        this.to = to;
    }

    /**
     * Get another unit object with identical domains from a unit.
     *
     * @return another unit with identical domains.
     */
    @Override
    public Unit another() {
        return new CompoundUnit(from, to);
    }

    /**
     * Get a copy of a unit.
     *
     * @return a copy of a unit.
     */
    @Override
    public Unit copy() {
        CompoundUnit copy = new CompoundUnit(from, to);
        copy.setValues(getValues().toArray(new Integer[0]));
        return copy;
    }
}
