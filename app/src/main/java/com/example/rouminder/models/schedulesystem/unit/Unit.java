package com.example.rouminder.models.schedulesystem.unit;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

public abstract class Unit {
    protected final TreeSet<Domain> domains;
    protected final Map<Domain, Integer> values;

    protected Unit(List<Domain> domains) {
        this.domains = new TreeSet<>(domains);
        this.values = new HashMap<>();
    }

    public static int compareDomains(Unit lhs, Unit rhs) {
        int compare;
        Iterator<Domain> it1 = lhs.domains.iterator(), it2 = rhs.domains.iterator();
        while(it1.hasNext() && it2.hasNext()) {
            compare = it1.next().compareTo(it2.next());
            if(compare != 0)
                return compare;
        }
        if(it1.hasNext()) {
            return 1;
        } else if(it2.hasNext()) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * Check if a unit is compatible with another unit.
     *
     * @param unit a unit to check compatibility.
     * @return true if a unit is compatible with another, otherwise false.
     */
    public boolean isCompatible(Unit unit) {
        return (Domain.orBitmask(domains.toArray(new Domain[0]))
                & Domain.orBitmask(unit.domains.toArray(new Domain[0]))) == Domain.MIN_BITMASK;
    }

    /**
     * Get values of a unit at each domain.
     *
     * @return a list of values in their domain's ascending order.
     */
    public List<Integer> getValues() {
        List<Integer> values = new ArrayList<>();
        for (Domain domain : domains) {
            values.add(this.values.get(domain));
        }
        return values;
    }

    /**
     * Set values of a unit at each domain.
     *
     * @param values a number of values in their domain's ascending order.
     *               set null to skip the value in specific position.
     */
    public void setValues(Integer... values) {
        Iterator<Domain> it = domains.iterator();
        for (Integer value : values) {
            if (!it.hasNext())
                break;
            if (value != null) {
                this.values.put(it.next(), value);
            }
        }
    }

    /**
     * Apply a unit to the LocalDateTime object. Only should the part matched with unit's domain be replaced.
     *
     * @param time a LocalDateTime object to be replaced.
     * @return a new LocalDateTime object with the unit applied.
     */
    public LocalDateTime apply(LocalDateTime time) {
        LocalDateTime temp = time;
        for (Domain domain : domains) {
            temp = domain.apply(temp, values.getOrDefault(domain, 0));
        }
        return temp;
    }

    /**
     * Get another unit object with identical domains from a unit.
     *
     * @return another unit with identical domains.
     */
    public abstract Unit another();

    /**
     * Get a copy of a unit.
     *
     * @return a copy of a unit.
     */
    public abstract Unit copy();

    /**
     * A domain, or primary time unit like minute, hour, etc., compromising time units.
     * Each domain has its own bit for checking conflicts of multiple units.
     */
    public enum Domain implements Comparable<Domain> {
        MINUTE("minute", 0x0001, 0, 60),
        HOUR("hour", 0x0002, 0, 24),
        DAY("day", 0x0004, 0, 7),
        WEEK("week", 0x0008, 0, 4),
        MONTH("month", 0x0010, 0, 12);

        private static final int MAX_BITMASK;
        private static final int MIN_BITMASK = 0x0000;
        private static final DayOfWeek startDayOfWeek = DayOfWeek.MONDAY;

        static {
            MAX_BITMASK = 2 * values()[values().length - 1].bitmask - 1;
        }

        private final String id;
        private final int bitmask;
        /**
         * range of unit per domain as integer.
         * the range is half-open range; [start, end)
         * -1 for undefined boundary
         */
        private final int start;
        private final int end;

        Domain(String id, int bitmask) {
            this(id, bitmask, -1, -1);
        }

        Domain(String id, int bitmask, int start, int end) {
            this.id = id;
            this.bitmask = bitmask;
            this.start = start;
            this.end = end;
        }

        /**
         * Calculate a final product of bit-or operation of multiple domains.
         *
         * @param domains a List of domains to calculate.
         * @return a result bitmask.
         */
        public static int orBitmask(Domain... domains) {
            int bitmask = MIN_BITMASK;
            for (Domain domain : domains) {
                bitmask |= domain.getBitmask();
            }
            return bitmask;
        }

        /**
         * Calculate a final product of bit-and operation of multiple domains.
         *
         * @param domains a List of domains to calculate.
         * @return a result bitmask.
         */
        public static int andBitmask(Domain... domains) {
            int bitmask = MAX_BITMASK;
            for (Domain domain : domains) {
                bitmask &= domain.getBitmask();
            }
            return bitmask;
        }

        /**
         * Get domains from bitmask.
         *
         * @param bitmask a bitmask.
         * @return an list of Domain.
         */
        public static List<Domain> getDomainsFromBitmask(int bitmask) {
            int normalized = bitmask & MAX_BITMASK;
            List<Domain> domains = new ArrayList<>();
            for (Domain domain : Domain.values()) {
                if ((normalized & domain.getBitmask()) != 0)
                    domains.add(domain);
            }
            return domains;
        }

        /**
         * Get Domains within Range.
         * Order is determined by definition order.
         *
         * @param from a starting Domain.
         * @param to   an ending Domain.
         * @return a list of Domain.
         */
        public static List<Domain> getDomainsFromRange(Domain from, Domain to) {
            List<Domain> domains = new ArrayList<>();
            for (Domain domain : Domain.values()) {
                if (from.getBitmask() <= domain.getBitmask() && domain.getBitmask() <= to.getBitmask())
                    domains.add(domain);
            }

            return domains;
        }

        /**
         * Get a start value, or possible smallest value of the domain.
         *
         * @return a start value.
         */
        public int getStart() {
            return start;
        }

        /**
         * Get an end value, or possible largest value of the domain.
         * It returns -1 if not defined.
         *
         * @return an end value, or -1 if end value is not defined.
         */
        public int getEnd() {
            return end;
        }

        /**
         * Overwrite a value at a domain to the LocalDateTime object.
         *
         * @param time  a LocalDateTime object to be replaced.
         * @param value a value used to overwrite at the domain.
         * @return a new LocalDateTime object with its value overwritten at the domain, or return null if value is out-of-bound.
         */
        public LocalDateTime apply(LocalDateTime time, int value) {
            LocalDateTime newTime;
            switch (this) {
                case MINUTE:
                    newTime = time.truncatedTo(ChronoUnit.MINUTES)
                            .plusMinutes(value);
                    break;
                case HOUR:
                    newTime = time.truncatedTo(ChronoUnit.HOURS)
                            .plusHours(value)
                            .plusMinutes(time.getMinute());
                    break;
                case DAY:
                    newTime = time.with(TemporalAdjusters.previousOrSame(startDayOfWeek))
                            .plusDays(value)
                            .plusHours(time.getHour())
                            .plusMinutes(time.getMinute());
                    break;
                case WEEK:
                    newTime = time.with(TemporalAdjusters.firstDayOfMonth())
                            .with(TemporalAdjusters.nextOrSame(startDayOfWeek))
                            .with(TemporalAdjusters.nextOrSame(time.getDayOfWeek()))
                            .plusHours(time.getHour())
                            .plusMinutes(time.getMinute());
                    break;
                case MONTH:
                    newTime = time.plusYears(value == 12 ? 1 : 0).withMonth((value + 1) % 12)
                            .with(TemporalAdjusters.firstDayOfMonth())
                            .with(TemporalAdjusters.nextOrSame(startDayOfWeek))
                            .plusWeeks(time.get(WeekFields.of(Locale.getDefault()).weekOfMonth()) - 1)
                            .with(TemporalAdjusters.nextOrSame(time.getDayOfWeek()))
                            .plusHours(time.getHour())
                            .plusMinutes(time.getMinute());
                    break;
                default:
                    newTime = time;
            }

            return newTime;
        }

        /**
         * Get an identifier of each domain.
         *
         * @return an identifier String.
         */
        public String getId() {
            return id;
        }

        /**
         * Get a bitmask of a domain.
         *
         * @return a bitmask.
         */
        public int getBitmask() {
            return bitmask;
        }
    }
}
