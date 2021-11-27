package com.example.rouminder.data.goalsystem;

import androidx.annotation.Nullable;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GoalManager {
    private static final Period timeToExpire = Period.ofMonths(1);
    private static int MAX_ID = 0;
    public final HashMap<Integer, Goal> goals;
    private final TreeSet<Goal> earlyStartingGoals;
    private final TreeSet<Goal> earlyEndingGoals;
    private final TreeSet<Goal> ongoingGoals;

    private final List<OnGoalChangeListener> onGoalChangeListeners;

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
        ongoingGoals = new TreeSet<>(new Comparator<Goal>() {
            @Override
            public int compare(Goal g1, Goal g2) {
                return g1.getStartTime().compareTo(g2.getStartTime());
            }
        });
        onGoalChangeListeners = new ArrayList<>();
    }

    /**
     * Add a goal to the manager.
     *
     * @param goal a goal to be added.
     * @return the id of the goal if succeed, otherwise -1.
     */
    public int addGoal(Goal goal) {
        if (goal.getId() == -1) {
            goal.setId(++GoalManager.MAX_ID);
        }

        goals.put(goal.getId(), goal);
        earlyStartingGoals.add(goal);
        earlyEndingGoals.add(goal);

        onGoalChangeListeners.forEach(listener -> listener.onGoalAdd(goal.getId()));
        return goal.getId();
    }

    /**
     * Get a goal from the manager
     *
     * @param id an identifier of a goal to be found.
     * @return a goal object if found, otherwise null.
     */
    public Goal getGoal(int id) {
        return goals.get(id);
    }

    void updateGoal(int id) {
        onGoalChangeListeners.forEach(listener -> {
            listener.onGoalUpdate(id);
        });
    }

    void updateGoalTime(int id, Runnable function) {
        Goal goal = getGoal(id);
        earlyStartingGoals.remove(goal);
        earlyEndingGoals.remove(goal);
        ongoingGoals.remove(goal);

        function.run();

        earlyStartingGoals.add(goal);
        earlyEndingGoals.add(goal);
        renewGoals(LocalDateTime.now());
        updateGoal(id);
    }

    /**
     * Remove a goal from the manager
     *
     * @param id an identifier of a goal to be removed.
     * @return true if successfully remove the goal, otherwise false.
     */
    public boolean removeGoal(int id) {
        boolean result;
        if (goals.get(id) != null) {

            Goal goal = goals.remove(id);
            earlyStartingGoals.remove(goal);
            earlyEndingGoals.remove(goal);
            result = true;
        } else {
            result = false;
        }

        onGoalChangeListeners.forEach(listener -> {
            listener.onGoalRemove(id);
        });
        return result;
    }

    /**
     * Get all GoalInstances.
     *
     * @return a list of goals.
     */
    public List<Goal> getGoals() {
        return new ArrayList<>(earlyStartingGoals);
    }

    /**
     * Get GoalInstances that matches domain and state.
     *
     * @param now    a LocalDateTime object of now.
     * @param domain a Domain enum that is used to check if instance is the same in specific domain.
     * @param status a current state that goal should have.
     * @return a List of goals that matches criteria.
     */
    public List<Goal> getGoals(LocalDateTime now, @Nullable Domain domain, @Nullable Status status) {
        if (domain == null)
            domain = Domain.ALL;

        if (status == null)
            status = Status.ALL;

        DomainFilter domainFilter = new DomainFilter(now, domain);
        StatusFilter statusFilter = new StatusFilter(now, status);
        List<Goal> domainFiltered;

        if (domain == Domain.ALL) {
            domainFiltered = new ArrayList<>(earlyStartingGoals);
        } else {


            Goal dummy = domainFilter.getDummy();
            Goal bottom = earlyEndingGoals.floor(dummy);
            Goal top = earlyStartingGoals.ceiling(dummy);
            SortedSet<Goal> setFromBottom = earlyEndingGoals.tailSet(bottom);
            SortedSet<Goal> setFromTop = earlyStartingGoals.headSet(top);
            domainFiltered = setFromBottom.stream()
                    .filter(setFromTop::contains)
                    .collect(Collectors.toList());
        }

        List<Goal> statusFiltered = domainFiltered.stream().filter(statusFilter).collect(Collectors.toList());
        return statusFiltered;
    }

    /**
     * Renew goal statuses by a given time.
     *
     * @return true if any goal has changed.
     */
    public boolean renewGoals(LocalDateTime now) {
        boolean result = false;

        // add to ongoing goals
        {
            Goal start = ongoingGoals.isEmpty() ? earlyStartingGoals.first() : earlyStartingGoals.floor(ongoingGoals.last());
            Goal end = earlyStartingGoals.ceiling(new Goal(now, now));
            Set<Goal> goalsToBeAdded = earlyStartingGoals.tailSet(start).headSet(end).stream().filter(g -> g.isOnProgress(now)).collect(Collectors.toSet());
            ongoingGoals.addAll(goalsToBeAdded);
            result = goalsToBeAdded.isEmpty();
        }

        // remove from ongoing goals
        ongoingGoals.removeIf(g -> g.isAfterEnd(now));

        return result;
    }

    /**
     * Set an event listener for changes of each goal;
     * like add, update, change.
     *
     * @param listener a listener to be set.
     */
    public void setOnGoalChangeListener(OnGoalChangeListener listener) {
        onGoalChangeListeners.add(listener);
    }

    /**
     * Remove an event listener for changes of each goal from the list
     *
     * @param listener a listener to be removed.
     */
    public void removeOnGoalChangeListener(OnGoalChangeListener listener) {
        onGoalChangeListeners.remove(listener);
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

        public static boolean test(LocalDateTime now, Domain domain, Goal goal) {
            return new DomainFilter(now, domain).test(goal);
        }

        /**
         * Create a dummy goal for effective search.
         *
         * @return a goal with given range
         */
        public Goal getDummy() {
            return new Goal(end, start);
        }

        @Override
        public boolean test(Goal goal) {
            return test(goal, start, end);
        }

        public boolean test(Goal goal, LocalDateTime start, LocalDateTime end) {
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

        public static boolean test(LocalDateTime now, Status status, Goal goal) {
            return new StatusFilter(now, status).test(goal);
        }

        @Override
        public boolean test(Goal goal) {
            return test(goal, now);
        }

        public boolean test(Goal goal, LocalDateTime now) {
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

    /**
     * An event listener for changes of each goal
     */
    public abstract class OnGoalChangeListener {
        public abstract void onGoalAdd(int id);

        public abstract void onGoalUpdate(int id);

        public abstract void onGoalRemove(int id);

        public void remove() {
            removeOnGoalChangeListener(this);
        }
    }

    /**
     * An event listener for changes of each goal for matching criteria.
     */
    public abstract class OnGoalWithCriteriaChangeListener extends OnGoalChangeListener{
        private final Domain domain;
        private final Status status;
        private LocalDateTime previous;

        public OnGoalWithCriteriaChangeListener(Domain domain, Status status) {
            this.domain = domain;
            this.status = status;
        }

        @Override
        public void onGoalAdd(int id) {
            LocalDateTime current = LocalDateTime.now();
            if(current.getDayOfMonth() != previous.getDayOfMonth())
                onDomainChanged();
            previous = current;
            if (DomainFilter.test(LocalDateTime.now(), domain, getGoal(id))
                    && StatusFilter.test(LocalDateTime.now(), status, getGoal(id)))
                onGoalWithCriteriaAdd(id);
        }

        @Override
        public void onGoalUpdate(int id) {
            LocalDateTime current = LocalDateTime.now();
            if(current.getDayOfMonth() != previous.getDayOfMonth())
                onDomainChanged();
            previous = current;
            if (DomainFilter.test(LocalDateTime.now(), domain, getGoal(id))
                    && StatusFilter.test(LocalDateTime.now(), status, getGoal(id)))
                onGoalWithCriteriaUpdate(id);
        }

        @Override
        public void onGoalRemove(int id) {
            LocalDateTime current = LocalDateTime.now();
            if(current.getDayOfMonth() != previous.getDayOfMonth())
                onDomainChanged();
            previous = current;
            if (DomainFilter.test(LocalDateTime.now(), domain, getGoal(id))
                    && StatusFilter.test(LocalDateTime.now(), status, getGoal(id)))
                onGoalWithCriteriaRemove(id);
        }


        public abstract void onGoalWithCriteriaAdd(int id);

        public abstract void onGoalWithCriteriaUpdate(int id);

        public abstract void onGoalWithCriteriaRemove(int id);

        public abstract void onDomainChanged();
    }
}
