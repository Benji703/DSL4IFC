package org.sdu.dsl4ifc.generator.conditional.impls;

import java.util.List;
import java.util.stream.Stream;

import org.sdu.dsl4ifc.generator.conditional.core.ComparisonOperation;
import org.sdu.dsl4ifc.generator.conditional.core.VariableStore;

/**
 * 
 * @author andreasedalpedersen
 *
 * @param <T> The type of the input to evalute the expression on
 */
public class ValueInStreamOperation<T> extends ComparisonOperation<T> {
    // T is the type of the actual value to compare. Not the ifc object. The same with U
    public ValueInStreamOperation(Stream<T> right) {
        this.right = right.toList();
    }

    private final List<T> right;

    @Override
    public boolean Evaluate(T item, VariableStore variables) {
        // Get the field values somehow
        return right.stream().anyMatch(r -> r.equals(item));
    }
}