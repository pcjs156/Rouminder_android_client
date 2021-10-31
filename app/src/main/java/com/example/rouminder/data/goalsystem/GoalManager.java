package com.example.rouminder.data.goalsystem;

import com.example.rouminder.data.goalsystem.action.Action;
import com.example.rouminder.data.goalsystem.action.ActionInstance;
import com.example.rouminder.data.goalsystem.condition.Condition;
import com.example.rouminder.data.goalsystem.condition.ConditionInstance;
import com.example.rouminder.data.goalsystem.goal.Goal;
import com.example.rouminder.data.goalsystem.goal.GoalInstance;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class GoalManager {
    private final Set<Goal> goals;
    private final TreeSet<GoalInstance> earlyStartingActionInstanceSet;
    private final TreeSet<GoalInstance> earlyEndingGoalInstanceSet;
    private final Set<Action<?>> actions;
    private final Set<ActionInstance<?>> actionInstances;
    private final Set<Condition<?>> conditions;
    private final Set<ConditionInstance<?>> conditionInstances;

    public GoalManager() {
        goals = new HashSet<>();
        earlyStartingActionInstanceSet = new TreeSet<>(new Comparator<GoalInstance>() {
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
        actions = new HashSet<>();
        actionInstances = new HashSet<>();
        conditions = new HashSet<>();
        conditionInstances = new HashSet<>();
    }
}
