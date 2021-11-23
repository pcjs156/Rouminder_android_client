package com.example.rouminder.models.schedulesystem.unit;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleUnit extends Unit{

    private final String id;

    protected SimpleUnit(String id, Domain domain) {
        super(Stream.of(domain).collect(Collectors.toList()));
        this.id = id;
    }

    SimpleUnit(SimpleUnits simpleUnits) {
        this(simpleUnits.id, simpleUnits.domain);
    }

    public String getId() {
        return id;
    }
}
