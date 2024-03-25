package org.sdu.dsl4ifc.generator.conditional.impls;

import org.sdu.dsl4ifc.generator.conditional.core.ComparisonOperation;

public class ValueEqualsValueOperation<T> extends ComparisonOperation {
    // T is the type of the actual value to compare. Not the ifc object. The same with U
    public ValueEqualsValueOperation(T left, T right) {
        this.left = left;
        this.right = right;
    }

    private final T left;
    private final T right;

    @Override
    public boolean Evaluate() {
        // Get the field values somehow
        return left.equals(right);
    }
}