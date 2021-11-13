package com.example.rouminder.data.goalsystem;

import androidx.annotation.Nullable;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GoalManager {
    private static final Period timeToExpire = Period.ofMonths(1);
    private final HashMap<Integer, Goal> goals;
    private final TreeSet<Goal> earlyStartingGoals;
    private final TreeSet<Goal> earlyEndingGoals;
    private final Vector<Goal> pendingGoals;
    private final Set<Goal> ongoingGoals;
    private final Set<Goal> endedGoals;

    public GoalManager() {
        Set<Integer> a = new HashSet<>();
        Goal goal;
        goals = new HashMap<>();
        earlyStartingGoals = new TreeSet<>(new Comparator<Goal>() {
            @Override
            public int compare(Goal g1, Goal g2) {
                return g1.getStartTime().compareTo(g2.getStartTime());
            }
        });
        earlyEndingGoals = new TreeSet<>(new Comparator<Goal>() {
            @Override
            public int compare(Goal g1, Goal g2) {
                return g1.getEndTime().compareTo(g2.getEndTime());
            }
        });
        pendingGoals = new Vector<>();
        ongoingGoals = new HashSet<>();
        endedGoals = new HashSet<>();
    }

    /**
     * Add a goal to the manager.
     *
     * @param goal a goal to be added.
     * @return the id of the goal if succeed, otherwise -1.
     */
    public int addGoal(Goal goal) {
        return -1;
    }

    /**
     * Get a goal from the manager
     *
     * @param id an identifier of a goal to be found.
     * @return a goal object if found, otherwise null.
     */
    public Goal getGoal(int id) {
        return goals.getOrDefault(id, null);
    }

    /**
     * Remove a goal from the manager
     *
     * @param id an identifier of a goal to be removed.
     * @return true if successfully remove the goal, otherwise false.
     */
    public boolean removeGoal(int id) {
        return false;
    }

    /**
     * Get GoalInstances that matches domain and state.
     *
     * @param now    a LocalDateTime object of now.
     * @param domain a Domain enum that is used to check if instance is the same in specific domain.
     * @param status a current state that GoalInstance should have.
     * @return a List of GoalInstances that matches criteria.
     */
    public List<Goal> getGoals(LocalDateTime now, @Nullable Domain domain, @Nullable Status status) {
        if (domain == null)
            domain = Domain.ALL;

        if (status == null)
            status = Status.ALL;

        DomainFilter domainFilter = new DomainFilter(now, domain);
        StatusFilter statusFilter = new StatusFilter(now, status);

        Goal dummy = domainFilter.getDummy();
        Goal bottom = earlyEndingGoals.floor(dummy);
        Goal top = earlyStartingGoals.ceiling(dummy);
        SortedSet<Goal> setFromBottom = earlyEndingGoals.tailSet(bottom);
        SortedSet<Goal> setFromTop = earlyStartingGoals.headSet(top);
        List<Goal> domainFiltered = setFromBottom.stream()
                .filter(val -> setFromTop.contains(val))
                .collect(Collectors.toList());

        List<Goal> statusFiltered = domainFiltered.stream().filter(statusFilter).collect(Collectors.toList());
        return statusFiltered;
    }

    /**
     * A enum for namespacing the domain in time, such as day, month, etc.
     */
    public enum Domain {
        ALL,
        DAY,
        WEEK,
        MONTH
    }

    /**
     * A enum for namespacing the status of a goal, such as 'Before start'.
     */
    public enum Status {
        ALL,
        BEFORE,
        ONGOING,
        AFTER
    }

    private static class DomainFilter implements Predicate<Goal> {
        private final LocalDateTime start;
        private final LocalDateTime end;

        public DomainFilter(LocalDateTime now, Domain domain) {
            switch (domain) {
                case DAY:
                    start = now.truncatedTo(ChronoUnit.DAYS);
                    end = start.plusDays(1);
                    break;
                case WEEK:
                    start = now.truncatedTo(ChronoUnit.DAYS).with(DayOfWeek.MONDAY);
                    end = start.plusDays(7);
                    break;
                case MONTH:
                    start = now.truncatedTo(ChronoUnit.MONTHS);
                    end = start.plusMonths(1);
                    break;
                case ALL:
                default:
                    start = null;
                    end = null;
                    break;
            }
        }

        /**
         * Create a dummy goal for effective search.
         * @return a goal with given range
         */
        public Goal getDummy() {
            return new Goal(end, start);
        }

        @Override
        public boolean test(Goal goal) {
            return (start == null && end == null)
                    || (goal.getEndTime().isAfter(start) && goal.getStartTime().isBefore(end))
                    ;
        }
    }

    private static class StatusFilter implements Predicate<Goal> {
        private final Status status;
        private final LocalDateTime now;

        public StatusFilter(LocalDateTime now, Status status) {
            this.status = status;
            this.now = now;
        }

        @Override
        public boolean test(Goal goal) {
            switch (status) {
                case BEFORE:
                    return goal.isBeforeStart(now);
                case ONGOING:
                    return goal.isOnProgress(now);
                case AFTER:
                    return goal.isAfterEnd(now);
                case ALL:
                default:
                    return true;
            }
        }
    }
}
