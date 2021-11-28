package com.example.rouminder.helpers;

import android.util.Pair;

import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;
import com.example.rouminder.firebase.exception.ModelDoesNotExists;
import com.example.rouminder.firebase.manager.GoalModelManager;
import com.example.rouminder.firebase.manager.RepeatPlanModelManager;
import com.example.rouminder.firebase.model.GoalModel;
import com.example.rouminder.firebase.model.RepeatPlanModel;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class RepeatPlanHelper {
    private RepeatPlanHelper() {}

    private static boolean doesGoalHaveMatchingPlan(Goal goal, RepeatPlanModel planModel) {
        GoalModel goalModel = null;
        try {
            goalModel = GoalModelManager.getInstance().get(String.valueOf(goal.getId()));
        } catch (ModelDoesNotExists e) {
            e.printStackTrace();
        }
        return goalModel != null && goalModel.plan.equals(planModel.id);
    }

    public static void generateGoals(GoalManager goalManager, RepeatPlanModel model) {
        GoalModelManager goalModelManager = GoalModelManager.getInstance();
        // get last ending goal with given plan
        LocalDateTime from = goalManager.getGoals().stream()
                .filter(g-> doesGoalHaveMatchingPlan(g, model))
                .map(Goal::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());
        LocalDateTime to = LocalDateTime.now().plusMonths(1);

        List<Pair<LocalDateTime, LocalDateTime>> periods = new ArrayList<>();

        // get a list of possible periods
        // DayOfWeek: Monday(1) ~ Sunday(7)
        // dayOfWeek: Sunday(0) ~ Saturday(6)
        for(LocalDateTime current = from.truncatedTo(ChronoUnit.DAYS); current.isBefore(to); current = current.plusDays(1)) {
            int dayOfWeek = current.getDayOfWeek().getValue() % 7;
            if(model.weekPlan.get(dayOfWeek))
                periods.add(new Pair<>(current, current.plusDays(1)));
        }

        List<Goal> goal = new ArrayList<>();
        periods.stream().map(p -> goalModelManager.create(model, p.first, p.second))
                .forEach(m -> goalManager.addGoal(GoalModelManager.convertGoalModelToGoal(goalManager, m)));
    }

    public static void removeAssociatedGoals(GoalManager goalManager, RepeatPlanModel model) {
        GoalModelManager goalModelManager = GoalModelManager.getInstance();
        goalManager.getGoals(LocalDateTime.now(), GoalManager.Domain.ALL, GoalManager.Status.BEFORE).stream()
                .filter(g -> doesGoalHaveMatchingPlan(g, model))
                .forEach(g -> goalManager.removeGoal(g.getId()));
        try {
            RepeatPlanModelManager.getInstance().delete(model.id);
        } catch(ModelDoesNotExists e) {
            e.printStackTrace();
        }
    }
}
