package org.sdu.dsl4ifc.generator.conditional.impls;

import org.sdu.dsl4ifc.generator.conditional.core.BooleanOperation;
import org.sdu.dsl4ifc.generator.conditional.core.Expression;

public class OrOperation<T> extends BooleanOperation<T> {
    public OrOperation(Expression<T> left, Expression<T> right) {
        super(left, right);
    }

    @Override
    public boolean Evaluate(T item) {
        return this.left.Evaluate(item) || this.right.Evaluate(item);
    }
}
