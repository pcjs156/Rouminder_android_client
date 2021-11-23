package com.example.rouminder.models.schedulesystem.unit;

public enum SimpleUnits {
    MINUTE("minute", Unit.Domain.MINUTE),
    HOUR("hour", Unit.Domain.HOUR),
    DAY("day", Unit.Domain.DAY),
    WEEK("week", Unit.Domain.WEEK),
    MONTH("month", Unit.Domain.MONTH);

    public final String id;
    public final Unit.Domain domain;

    SimpleUnits(String id, Unit.Domain domain) {
        this.id = id;
        this.domain = domain;
    }
}
