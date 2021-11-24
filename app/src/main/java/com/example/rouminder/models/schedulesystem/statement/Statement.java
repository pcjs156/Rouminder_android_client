package com.example.rouminder.models.schedulesystem.statement;

import com.example.rouminder.models.schedulesystem.unit.Unit;

public abstract class Statement {
    private Unit unit;

    /**
     * Get a statement from an expression.
     *
     * @param expression an String expression.
     * @return a statement object.
     */
    public static Statement fromExpression(String expression) {
        return null;
    }

    /**
     * Get an expression from a statement.
     *
     * @return an String expression.
     */
    public abstract String toExpression();

}
