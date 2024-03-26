package org.sdu.dsl4ifc.generator.conditional.impls;

import org.sdu.dsl4ifc.generator.conditional.core.ComparisonOperation;

public class ValueEqualsValueOperation<T, U> extends ComparisonOperation<U> {
    // T is the type of the actual value to compare. Not the ifc object. The same with U
    public ValueEqualsValueOperation(T right) {
        this.right = right;
    }

    private final T right;

    @Override
    public boolean Evaluate(U item) {
        // Get the field values somehow
        return item.equals(right);
    }
}
