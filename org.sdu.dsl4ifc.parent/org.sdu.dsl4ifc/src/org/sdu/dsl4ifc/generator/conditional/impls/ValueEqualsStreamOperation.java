package org.sdu.dsl4ifc.generator.conditional.impls;

import java.util.List;
import java.util.stream.Stream;

import org.sdu.dsl4ifc.generator.conditional.core.ComparisonOperation;
import org.sdu.dsl4ifc.generator.conditional.core.VariableStore;

public class ValueEqualsStreamOperation<T,U> extends ComparisonOperation<U> {
    // T is the type of the actual value to compare. Not the ifc object. The same with U
    public ValueEqualsStreamOperation(Stream<U> right) {
        this.right = right.toList();
    }

    private final List<U> right;

    @Override
    public boolean Evaluate(U item, VariableStore variables) {
        // Get the field values somehow
        return right.stream().anyMatch(r -> r.equals(item));
    }
}