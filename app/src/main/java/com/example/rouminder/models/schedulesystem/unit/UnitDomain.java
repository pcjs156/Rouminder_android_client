package com.example.rouminder.models.schedulesystem.unit;

import java.util.ArrayList;
import java.util.List;

public enum UnitDomain {
    MINUTE("minute", 0x0001, 0, 60),
    HOUR("hour", 0x0002, 0, 24),
    DAY("day", 0x0004, 0, 7),
    WEEK("week", 0x0008, 0, 4),
    MONTH("month", 0x0016, 0, 12),
    YEAR("year", 0x0032);

    private final String name;
    private final int bitmask;

    /**
     * range of unit per domain as integer.
     * the range is half-open range; [start, end)
     * -1 for undefined boundary
     */
    private final int start;
    private final int end;

    UnitDomain(String name, int bitmask) {
        this(name, bitmask, -1, -1);
    }

    UnitDomain(String name, int bitmask, int start, int end) {
        this.name = name;
        this.bitmask = bitmask;
        this.start = start;
        this.end = end;
    }

    public String getName() {
        return name;
    }

    public int getBitmask() {
        return bitmask;
    }

    public static int orBitmask(List<UnitDomain> domains) {
        int bitmask = 0x0000;
        for(UnitDomain domain:domains) {
            bitmask |= domain.getBitmask();
        }
        return bitmask;
    }

    public static int andBitmask(List<UnitDomain> domains) {
        int bitmask = 0xffff;
        for(UnitDomain domain:domains) {
            bitmask &= domain.getBitmask();
        }
        return bitmask;
    }

    public static UnitDomain[] getDomainsFromBitmask(int bitmask) {
        List<UnitDomain> domains = new ArrayList<>();
        for(UnitDomain domain: UnitDomain.values()) {
            if((bitmask & domain.getBitmask()) != 0)
                domains.add(domain);
        }
        return (UnitDomain[]) domains.toArray();
    }
}
