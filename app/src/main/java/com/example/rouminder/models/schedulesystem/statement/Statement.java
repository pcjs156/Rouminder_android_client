package com.example.rouminder.models.schedulesystem.statement;

import com.example.rouminder.models.schedulesystem.unit.Unit;

public abstract class Statement<U extends Unit> {
    public abstract String toExpression();
}
