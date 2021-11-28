package com.example.rouminder.data.goalsystem;

import android.util.Log;

import androidx.annotation.Nullable;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GoalManager {
    private static final Period timeToExpire = Period.ofMonths(1);
    private static int MAX_ID = 0;
    public final HashMap<Integer, Goal> goals;
    private final TreeMap<LocalDateTime, Set<Goal>> earlyStartingGoals;
    private final TreeMap<LocalDateTime, Set<Goal>> earlyEndingGoals;
    private final TreeMap<LocalDateTime, Set<Goal>> ongoingGoals;

    private final List<OnGoalChangeListener> onGoalChangeListeners;

    public GoalManager() {
        goals = new HashMap<>();
        earlyStartingGoals = new TreeMap<>();
        earlyEndingGoals = new TreeMap<>();
        ongoingGoals = new TreeMap<>();
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

        if(earlyStartingGoals.containsKey(goal.getStartTime()))
            earlyStartingGoals.get(goal.getStartTime()).add(goal);
        else
            earlyStartingGoals.put(goal.getStartTime(), new HashSet<>(Collections.singletonList(goal)));
        if(earlyEndingGoals.containsKey(goal.getEndTime()))
            earlyEndingGoals.get(goal.getEndTime()).add(goal);
        else
            earlyEndingGoals.put(goal.getStartTime(), new HashSet<>(Collections.singletonList(goal)));

        onGoalChangeListeners.forEach(listener -> listener.onGoalAdd(goal.getId()));
        renewGoals(LocalDateTime.now());
        Log.d("after_add", goals.size() + " " + earlyStartingGoals.values().size() + " " + earlyEndingGoals.values().size());
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
        for(OnGoalChangeListener listener: onGoalChangeListeners) {
            listener.onGoalUpdate(id);
        }
//        onGoalChangeListeners.forEach(listener -> {
//            listener.onGoalUpdate(id);
//        });
    }

    void updateGoalTime(int id, Runnable function) {
        Goal goal = getGoal(id);
        if(earlyStartingGoals.containsKey(goal.getStartTime())) {
            Set<Goal> set = earlyStartingGoals.get(goal.getStartTime());
            set.remove(goal);
            if(set.isEmpty())
                earlyStartingGoals.remove(goal.getStartTime(), set);
        }
        if(earlyEndingGoals.containsKey(goal.getEndTime())) {
            Set<Goal> set = earlyEndingGoals.get(goal.getEndTime());
            set.remove(goal);
            if(set.isEmpty())
                earlyEndingGoals.remove(goal.getEndTime(), set);
        }
        if(ongoingGoals.containsKey(goal.getStartTime())) {
            Set<Goal> set = ongoingGoals.get(goal.getStartTime());
            set.remove(goal);
            if(set.isEmpty())
                ongoingGoals.remove(goal.getStartTime(), set);
        }

        function.run();

        if(earlyStartingGoals.containsKey(goal.getStartTime()))
            earlyStartingGoals.get(goal.getStartTime()).add(goal);
        else
            earlyStartingGoals.put(goal.getStartTime(), new HashSet<>(Collections.singletonList(goal)));
        if(earlyEndingGoals.containsKey(goal.getEndTime()))
            earlyEndingGoals.get(goal.getEndTime()).add(goal);
        else
            earlyEndingGoals.put(goal.getStartTime(), new HashSet<>(Collections.singletonList(goal)));
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
        onGoalChangeListeners.forEach(listener -> {
            listener.onGoalRemove(id);
        });
        boolean result;
        if (goals.get(id) != null) {

            Goal goal = goals.remove(id);
            if(earlyStartingGoals.containsKey(goal.getStartTime())) {
                Set<Goal> set = earlyStartingGoals.get(goal.getStartTime());
                set.remove(goal);
                if(set.isEmpty())
                    earlyStartingGoals.remove(goal.getStartTime(), set);
            }
            if(earlyEndingGoals.containsKey(goal.getEndTime())) {
                Set<Goal> set = earlyEndingGoals.get(goal.getEndTime());
                set.remove(goal);
                if(set.isEmpty())
                    earlyEndingGoals.remove(goal.getEndTime(), set);
            }
            result = true;
        } else {
            result = false;
        }

        renewGoals(LocalDateTime.now());
        return result;
    }

    /**
     * Get all goals.
     *
     * @return a list of goals.
     */
    public List<Goal> getGoals() {
        return new ArrayList<>(goals.values());
    }

    /**
     * Get goals matching domain and state.
     *
     * @param now    a LocalDateTime object of now.
     * @param domain a Domain enum that is used to check if instance is the same in specific domain.
     * @param status a current state that goal should have.
     * @return a list of goals that matches criteria.
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
            domainFiltered = new ArrayList<>(goals.values());
        } else {
//            domainFiltered = earlyStartingGoals.stream().filter(domainFilter).collect(Collectors.toList());
            Goal dummy = domainFilter.getDummy();
            LocalDateTime bottom = earlyEndingGoals.floorKey(dummy.getEndTime());
            LocalDateTime top = earlyStartingGoals.ceilingKey(dummy.getStartTime());
//            SortedSet<Goal> setFromBottom = (bottom == null) ? new TreeSet<>() : earlyEndingGoals.tailSet(bottom);
//            SortedSet<Goal> setFromTop = (top == null) ? new TreeSet<>() : earlyStartingGoals.headSet(top);
//            SortedSet<Goal> setFromBottom = bottom == null ? earlyEndingGoals : earlyEndingGoals.tailSet(bottom);
//            SortedSet<Goal> setFromTop = top == null ? earlyStartingGoals : earlyStartingGoals.headSet(top);
//            domainFiltered = setFromBottom.stream()
//                    .filter(setFromTop::contains)
//                    .collect(Collectors.toList());
            SortedMap<LocalDateTime, Set<Goal>> mapFromBottom = bottom == null ? earlyEndingGoals : earlyEndingGoals.tailMap(bottom, true);
            SortedMap<LocalDateTime, Set<Goal>> mapFromTop = top == null ? earlyStartingGoals : earlyStartingGoals.headMap(top, true);
            Set<Goal> setFromBottom = mapFromBottom.values().stream().reduce(new HashSet<>(), (total, set) -> {
                total.addAll(set);
                return total;
            });
            Set<Goal> setFromTop = mapFromTop.values().stream().reduce(new HashSet<>(), (total, set) -> {
                total.addAll(set);
                return total;
            });
            domainFiltered = setFromBottom.stream()
                    .filter(setFromTop::contains)
                    .filter(domainFilter)
                    .collect(Collectors.toList());
        }
//        domainFiltered = goals.values().stream().filter(domainFilter).collect(Collectors.toList());

        List<Goal> statusFiltered = domainFiltered.stream().filter(statusFilter).collect(Collectors.toList());

        Log.d("filter_result", goals.size() + " " + earlyStartingGoals.values().size() + " " + earlyEndingGoals.values().size()
                + " " + domainFiltered.size() + " " + statusFiltered.size());
        return statusFiltered;
    }

    /**
     * Get ongoing goals.
     *
     * @return a list of ongoing goals.
     */
    public List<Goal> getOngoingGoals() {
        return new ArrayList<>(ongoingGoals.values().stream().reduce(new HashSet<>(), (total, set) -> {
            total.addAll(set);
            return total;
        }));
    }

    /**
     * Renew goal statuses by a given time.
     *
     * @return true if any goal has changed.
     */
    public boolean renewGoals(LocalDateTime now) {
        boolean result = false;


        TreeMap<LocalDateTime, Set<Goal>> newOngoingGoals = new TreeMap<>();
        getGoals(now, null, Status.ONGOING).forEach(g -> {
            if(newOngoingGoals.containsKey(g.getStartTime()))
                newOngoingGoals.get(g.getStartTime()).add(g);
            else
                newOngoingGoals.put(g.getStartTime(), new HashSet<>(Collections.singletonList(g)));
        });

        if(!newOngoingGoals.equals(ongoingGoals)) {
            ongoingGoals.clear();

            ongoingGoals.putAll(newOngoingGoals);
            result = true;
        }

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
                    start = now.truncatedTo(ChronoUnit.DAYS).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                    end = start.plusDays(7);
                    break;
                case MONTH:
                    start = now.minusMonths(1)
                            .with(TemporalAdjusters.firstDayOfNextMonth())
                            .truncatedTo(ChronoUnit.DAYS);
                    end = start.plusMonths(1);
                    break;
                case ALL:
                default:
                    start = null;
                    end = null;
                    break;
            }
            if(start != null && end != null) {
                Log.d("domainfilter", start.toString() + " " + end.toString());
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
            if(previous != null && current.getDayOfMonth() != previous.getDayOfMonth())
                onDomainChanged();
            previous = current;
            if (DomainFilter.test(LocalDateTime.now(), domain, getGoal(id))
                    && StatusFilter.test(LocalDateTime.now(), status, getGoal(id)))
                onGoalWithCriteriaAdd(id);
        }

        @Override
        public void onGoalUpdate(int id) {
            LocalDateTime current = LocalDateTime.now();
            if(previous != null && current.getDayOfMonth() != previous.getDayOfMonth())
                onDomainChanged();
            previous = current;
            if (DomainFilter.test(LocalDateTime.now(), domain, getGoal(id))
                    && StatusFilter.test(LocalDateTime.now(), status, getGoal(id)))
                onGoalWithCriteriaUpdate(id);
        }

        @Override
        public void onGoalRemove(int id) {
            LocalDateTime current = LocalDateTime.now();
            if(previous != null && current.getDayOfMonth() != previous.getDayOfMonth())
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
