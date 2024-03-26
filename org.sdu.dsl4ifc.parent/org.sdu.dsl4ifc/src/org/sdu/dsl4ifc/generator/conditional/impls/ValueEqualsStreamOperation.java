package org.sdu.dsl4ifc.generator.conditional.impls;

import java.util.stream.Stream;

import org.sdu.dsl4ifc.generator.conditional.core.ComparisonOperation;

public class ValueEqualsStreamOperation<T,U> extends ComparisonOperation<U> {
    // T is the type of the actual value to compare. Not the ifc object. The same with U
    public ValueEqualsStreamOperation(Stream<U> right) {
        this.right = right;
    }

    private final Stream<U> right;

    @Override
    public boolean Evaluate(U item) {
        // Get the field values somehow
        return right.anyMatch(r -> r.equals(item));
    }
}