package org.sdu.dsl4ifc.generator.conditional.core;

public abstract class BooleanOperation<T> extends Expression<T> {
    public BooleanOperation(Expression<T> left, Expression<T> right) {
        this.left = left;
        this.right = right;
    }

    public Expression<T> left, right;
}