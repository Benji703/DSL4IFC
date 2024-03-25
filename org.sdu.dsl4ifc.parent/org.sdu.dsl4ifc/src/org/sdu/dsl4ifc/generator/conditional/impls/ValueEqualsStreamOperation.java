package org.sdu.dsl4ifc.generator.conditional.impls;

import java.util.stream.Stream;

import org.sdu.dsl4ifc.generator.conditional.core.ComparisonOperation;

public class ValueEqualsStreamOperation<T,U> extends ComparisonOperation {
    // T is the type of the actual value to compare. Not the ifc object. The same with U
    public ValueEqualsStreamOperation(T left, Stream<U> right) {
        this.left = left;
        this.right = right;
    }

    private final T left;
    private final Stream<U> right;

    @Override
    public boolean Evaluate() {
        // Get the field values somehow
        return right.anyMatch(r -> r.equals(left));
    }
}