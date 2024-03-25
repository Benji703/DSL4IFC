package org.sdu.dsl4ifc.generator.conditional.core;

public abstract class BooleanOperation extends Expression {
    public BooleanOperation(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    public Expression left, right;
}