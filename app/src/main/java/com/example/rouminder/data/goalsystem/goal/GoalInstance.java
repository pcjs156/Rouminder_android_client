package com.example.rouminder.data.goalsystem.goal;

import androidx.annotation.Nullable;

import com.example.rouminder.data.goalsystem.GoalManager;
import com.example.rouminder.data.goalsystem.action.ActionInstance;
import com.example.rouminder.data.goalsystem.condition.ConditionInstance;

import java.time.LocalDateTime;

public class GoalInstance implements Comparable<GoalInstance> {
    private final GoalManager manager;
    private int goalInstanceID;
    private final int goalID;
    private final int conditionInstanceID;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public GoalInstance(@Nullable GoalManager manager, int goalInstanceID, int goalID, int conditionInstanceID, LocalDateTime from, LocalDateTime to) {
        this.manager = manager;
        this.goalID = goalID;
        this.goalInstanceID = goalInstanceID;
        this.conditionInstanceID = conditionInstanceID;
        this.startTime = from;
        this.endTime = to;
    }

    public int getID() {
        return goalInstanceID;
    }

    void setID(int goalInstanceID) {
        this.goalInstanceID = goalInstanceID;
    }

    /**
     * Check if the goal instance shouldn't have been start.
     *
     * @param now a LocalDateTime object to be tested.
     * @return true if now is before startTime, otherwise false.
     */
    public boolean isBeforeStart(LocalDateTime now) {
        return now.isBefore(startTime);
    }

    /**
     * Check if the goal instance should have been finished.
     *
     * @param now a LocalDateTime object to be tested.
     * @return true if now is after endTime, otherwise false.
     */
    public boolean isAfterEnd(LocalDateTime now) {
        return now.isAfter(endTime);
    }

    /**
     * Check if the goal instance should have been finished.
     *
     * @param now a LocalDateTime object to be tested.
     * @return false if isBeforeStart(now) or isAfterEnd(now), otherwise true.
     */
    public boolean isOnProgress(LocalDateTime now) {
        return !isBeforeStart(now) && !isAfterEnd(now);
    }

    /**
     * Get start time of the goal instance.
     *
     * @return a LocalDateTime.
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Get end time of the goal instance.
     *
     * @return a LocalDateTime.
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }


    public Goal getGoal() {
        return manager == null ? null : manager.getGoal(goalID);
    }

    public ConditionInstance<?> getConditionInstance() {
        return manager == null ? null : manager.getConditionInstance(conditionInstanceID);
    }


    @Override
    public int compareTo(GoalInstance goalInstance) {
        return getGoal().compareTo(goalInstance.getGoal());
    }
}
