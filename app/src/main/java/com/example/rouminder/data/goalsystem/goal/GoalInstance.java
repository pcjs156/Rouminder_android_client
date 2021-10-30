package com.example.rouminder.data.goalsystem.goal;

import com.example.rouminder.data.goalsystem.action.ActionInstance;
import com.example.rouminder.data.goalsystem.condition.ConditionInstance;

import java.time.LocalDateTime;

public class GoalInstance implements Comparable<GoalInstance>{
    private final Goal goal;
    private final ConditionInstance<?> conditionInstance;
    private final ActionInstance<?> actionInstance;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public GoalInstance(Goal goal, ConditionInstance<?> conditionInstance, ActionInstance<?> actionInstance, LocalDateTime from, LocalDateTime to) {
        this.goal = goal;
        this.conditionInstance = conditionInstance;
        this.actionInstance = actionInstance;
        this.startTime = from;
        this.endTime = to;
    }

    /**
     * Check if the goal instance shouldn't have been start.
     * @param now a LocalDateTime object to be tested.
     * @return true if now is before startTime, otherwise false.
     */
    public boolean isBeforeStart(LocalDateTime now) {
        return now.isBefore(startTime);
    }

    /**
     * Check if the goal instance should have been finished.
     * @param now a LocalDateTime object to be tested.
     * @return true if now is after endTime, otherwise false.
     */
    public boolean isAfterEnd(LocalDateTime now) {
        return now.isAfter(endTime);
    }

    /**
     * Check if the goal instance should have been finished.
     * @param now a LocalDateTime object to be tested.
     * @return false if isBeforeStart(now) or isAfterEnd(now), otherwise true.
     */
    public boolean isOnProgress(LocalDateTime now) {
        return !isBeforeStart(now) && !isAfterEnd(now);
    }

    /**
     * Get start time of the goal instance.
     * @return a LocalDateTime.
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Get end time of the goal instance.
     * @return a LocalDateTime.
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }


    public Goal getGoal() {
        return goal;
    }

    public ConditionInstance<?> getConditionInstance() {
        return conditionInstance;
    }

    public ActionInstance<?> getActionInstance() {
        return actionInstance;
    }



    @Override
    public int compareTo(GoalInstance goalInstance) {
        return getGoal().compareTo(goalInstance.getGoal());
    }
}
