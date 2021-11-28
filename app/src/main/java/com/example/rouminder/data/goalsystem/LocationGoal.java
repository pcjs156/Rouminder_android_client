package com.example.rouminder.data.goalsystem;

import android.graphics.Color;

import java.time.LocalDateTime;

public class LocationGoal extends Goal{
    /**
     * A constructor for a Goal.
     *
     * @param id      an identifier.
     * @param name    a name of the goal.
     * @param from    when the goal should start; closed start point of the range.
     * @param to      when the goal should end; open end point of the range.
     * @param current a current progress of the goal.
     * @param target  a target progress of the goal.
     */
    public LocationGoal(GoalManager gm, int id, String name, LocalDateTime from, LocalDateTime to, int current, int target, Color highlight) {
        super(gm, id, name, from, to, current, target, highlight);
    }

    /*
        implementations
     */
}
