package com.example.rouminder.data.goalsystem;

import androidx.annotation.Nullable;

import com.example.rouminder.data.goalsystem.action.Action;
import com.example.rouminder.data.goalsystem.action.ActionInstance;
import com.example.rouminder.data.goalsystem.condition.Condition;
import com.example.rouminder.data.goalsystem.condition.ConditionInstance;
import com.example.rouminder.data.goalsystem.goal.Goal;
import com.example.rouminder.data.goalsystem.goal.GoalInstance;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GoalManager {
    private final Set<Goal> goals;
    private final TreeSet<GoalInstance> earlyStartingGoalInstanceSet;
    private final TreeSet<GoalInstance> earlyEndingGoalInstanceSet;
    private final TreeSet<GoalInstance> ongoingGoalInstanceSet;
    private final Set<Action<?>> actions;
    private final Set<ActionInstance<?>> actionInstances;
    private final Set<Condition<?>> conditions;
    private final Set<ConditionInstance<?>> conditionInstances;
    private final Period timeUntilGoalInstanceArchived;

    public GoalManager() {
        goals = new HashSet<>();
        earlyStartingGoalInstanceSet = new TreeSet<>(new Comparator<GoalInstance>() {
            @Override
            public int compare(GoalInstance g1, GoalInstance g2) {
                return g1.getStartTime().compareTo(g2.getStartTime());
            }
        });
        earlyEndingGoalInstanceSet = new TreeSet<>(new Comparator<GoalInstance>() {
            @Override
            public int compare(GoalInstance g1, GoalInstance g2) {
                return g1.getEndTime().compareTo(g2.getEndTime());
            }
        });
        ongoingGoalInstanceSet = new TreeSet<>();
        actions = new HashSet<>();
        actionInstances = new HashSet<>();
        conditions = new HashSet<>();
        conditionInstances = new HashSet<>();
        timeUntilGoalInstanceArchived = Period.ofMonths(1);
    }

    /**
     * Add a Goal to the manager.
     *
     * @param goal to be added to manager.
     * @return true if successfully added, otherwise false.
     */
    public boolean addGoal(Goal goal) {
        return false;
    }

    /**
     * Add an Action to the manager.
     *
     * @param action to be added to manager.
     * @return true if successfully added, otherwise false.
     */
    public boolean addAction(Action<?> action) {
        return false;
    }

    /**
     * Add a Condition to the manager.
     *
     * @param condition to be added to manager.
     * @return true if successfully added, otherwise false.
     */
    public boolean addCondition(Condition<?> condition) {
        return false;
    }

    /**
     * Add a GoalInstance to the manager.
     *
     * @param goalInstance to be added to manager.
     * @return true if successfully added, otherwise false.
     */
    public boolean addGoalInstance(GoalInstance goalInstance) {
        return false;
    }

    /**
     * Add an ActionInstance to the manager.
     *
     * @param actionInstance to be added to manager.
     * @return true if successfully added, otherwise false.
     */
    public boolean addActionInstance(ActionInstance<?> actionInstance) {
        return false;
    }

    /**
     * Add a ConditionInstance to the manager
     *
     * @param conditionInstance to be added to manger.
     * @return true if successfully added, otherwise false.
     */
    public boolean addConditionInstance(ConditionInstance<?> conditionInstance) {
        return false;
    }


    /**
     * Get a Goal with specified id.
     * @param id of a Goal to be found.
     * @return a Goal object if found, otherwise null.
     */
    public Goal getGoal(int id) {
        return null;
    }

    /**
     * Get a Action with specified id.
     * @param id of a Action to be found.
     * @return a Action object if found, otherwise null.
     */
    public Action<?> getAction(int id) {
        return null;
    }

    /**
     * Get a Condition with specified id.
     * @param id of a Condition to be found.
     * @return a Condition object if found, otherwise null.
     */
    public Condition<?> getCondition(int id) {
        return null;
    }
    /**
     * Get a GoalInstance with specified id.
     * @param id of a GoalInstance to be found.
     * @return a GoalInstance object if found, otherwise null.
     */
    public GoalInstance getGoalInstance(int id) {
        return null;
    }

    /**
     * Get GoalInstances that matches domain and state.
     *
     * @param now    a LocalDateTime object of now.
     * @param domain a Domain enum that is used to check if instance is the same in specific domain.
     * @param status a current state that GoalInstance should have.
     * @return a List of GoalInstances that matches criteria.
     */
    public List<GoalInstance> getGoalInstances(LocalDateTime now, @Nullable Domain domain, @Nullable Status status) {
        if (domain == null)
            domain = Domain.ALL;

        if (status == null)
            status = Status.ALL;

        DomainFilter domainFilter = new DomainFilter(now, domain);
        StatusFilter statusFilter = new StatusFilter(now, status);

        GoalInstance dummy = domainFilter.getDummy();
        GoalInstance bottom = earlyEndingGoalInstanceSet.floor(dummy);
        GoalInstance top = earlyStartingGoalInstanceSet.ceiling(dummy);
        SortedSet<GoalInstance> setFromBottom = earlyEndingGoalInstanceSet.tailSet(bottom);
        SortedSet<GoalInstance> setFromTop = earlyStartingGoalInstanceSet.headSet(top);
        List<GoalInstance> domainFiltered = setFromBottom.stream()
                .filter(val -> !setFromTop.contains(val))
                .collect(Collectors.toList());

        List<GoalInstance> statusFiltered = domainFiltered.stream().filter(statusFilter).collect(Collectors.toList());
        return statusFiltered;
    }

    /**
     * Get ongoing GoalInstances.
     *
     * @return a list of ongoing GoalInstance objects.
     */
    public List<GoalInstance> getOngoingGoalInstances() {
        return new ArrayList<>(ongoingGoalInstanceSet);
    }

    /**
     * Get a ActionInstance with specified id.
     * @param id of a ActionInstance to be found.
     * @return a ActionInstance object if found, otherwise null.
     */
    public ActionInstance<?> getActionInstance(int id) {
        return null;
    }
    /**
     * Get a ConditionInstance with specified id.
     * @param id of a ConditionInstance to be found.
     * @return a ConditionInstance object if found, otherwise null.
     */
    public ConditionInstance<?> getConditionInstance(int id) {
        return null;
    }

    /**
     * Remove a Goal from manager.
     *
     * @param goal to be removed from manager.
     * @return true if successfully removed(found), otherwise false.
     */
    public boolean removeGoal(Goal goal) {
        return false;
    }

    /**
     * Remove a Goal from manager.
     *
     * @param id of the Goal to be removed from manager.
     * @return true if successfully removed(found), otherwise false.
     */
    public boolean removeGoal(int id) {
        return false;
    }

    /**
     * Remove an Action from manager.
     *
     * @param action to be removed from manager.
     * @return true if successfully removed(found), otherwise false.
     */
    public boolean removeAction(Action<?> action) {
        return false;
    }

    /**
     * Remove an Action from manager.
     *
     * @param id of the Action to be removed from manager.
     * @return true if successfully removed(found), otherwise false.
     */
    public boolean removeAction(int id) {
        return false;
    }

    /**
     * Remove a Condition from manager.
     *
     * @param condition to be removed from manager.
     * @return true if successfully removed(found), otherwise false.
     */
    public boolean removeCondition(Condition<?> condition) {
        return false;
    }

    /**
     * Remove a Condition from manager.
     *
     * @param id to be removed from manager.
     * @return true if successfully removed(found), otherwise false.
     */
    public boolean removeCondition(int id) {
        return false;
    }

    /**
     * Remove a GoalInstance from manager.
     *
     * @param goalInstance to be removed from manager.
     * @return true if successfully removed(found), otherwise false.
     */
    public boolean removeGoalInstance(GoalInstance goalInstance) {
        return false;
    }

    /**
     * Remove a GoalInstance from manager.
     *
     * @param id to be removed from manager.
     * @return true if successfully removed(found), otherwise false.
     */
    public boolean removeGoalInstance(int id) {
        return false;
    }

    /**
     * Remove an ActionInstance from manager.
     *
     * @param actionInstance to be removed from manager.
     * @return true if successfully removed(found), otherwise false.
     */
    public boolean removeActionInstance(ActionInstance<?> actionInstance) {
        return false;
    }

    /**
     * Remove an ActionInstance from manager.
     *
     * @param id to be removed from manager.
     * @return true if successfully removed(found), otherwise false.
     */
    public boolean removeActionInstance(int id) {
        return false;
    }

    /**
     * Remove a ConditionInstance from manager.
     *
     * @param id to be removed from manager.
     * @return true if successfully removed(found), otherwise false.
     */
    public boolean removeConditionInstance(int id) {
        return false;
    }

    /**
     * Remove a ConditionInstance from manager.
     *
     * @param conditionInstance to be removed from manager.
     * @return true if successfully removed(found), otherwise false.
     */
    public boolean removeConditionInstance(ConditionInstance<?> conditionInstance) {
        return false;
    }



    /**
     * Update GoalInstances by LocalDateTime.
     *
     * @param now a LocalDateTime to be used in updating
     * @return true if any change occured.
     */
    public boolean updateGoalInstances(LocalDateTime now) {
        return updateOngoingGoalInstances(now)
                || expireOngoingGoalInstances(now)
                || archiveGoalInstances(now);
    }

    private boolean updateOngoingGoalInstances(LocalDateTime now) {
        return false;
    }

    private boolean expireOngoingGoalInstances(LocalDateTime now) {
        return false;
    }

    private boolean archiveGoalInstances(LocalDateTime now) {
        return false;
    }

    public enum Domain {
        ALL,
        DAY,
        WEEK,
        MONTH;
    }

    public enum Status {
        ALL,
        BEFORE,
        ONGOING,
        AFTER
    }

    private static class DomainFilter implements Predicate<GoalInstance> {
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

        public GoalInstance getDummy() {
            return new GoalInstance(null, null, null, end, start);
        }

        @Override
        public boolean test(GoalInstance goalInstance) {
            return (start == null && end == null)
                    || (goalInstance.getEndTime().isAfter(start) && goalInstance.getStartTime().isBefore(end))
                    ;
        }
    }

    private static class StatusFilter implements Predicate<GoalInstance> {
        private final Status status;
        private final LocalDateTime now;

        public StatusFilter(LocalDateTime now, Status status) {
            this.status = status;
            this.now = now;
        }

        @Override
        public boolean test(GoalInstance goalInstance) {
            switch (status) {
                case BEFORE:
                    return goalInstance.isBeforeStart(now);
                case ONGOING:
                    return goalInstance.isOnProgress(now);
                case AFTER:
                    return goalInstance.isAfterEnd(now);
                case ALL:
                default:
                    return true;
            }
        }
    }
}
