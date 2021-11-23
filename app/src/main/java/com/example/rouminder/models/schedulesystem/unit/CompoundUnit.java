package com.example.rouminder.models.schedulesystem.unit;


public class CompoundUnit extends Unit{
    public CompoundUnit(Domain from, Domain to) {
        super(Domain.getDomainsFromRange(from, to));
    }
}
