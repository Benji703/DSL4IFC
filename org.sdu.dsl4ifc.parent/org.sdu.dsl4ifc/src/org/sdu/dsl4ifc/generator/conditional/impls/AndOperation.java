package org.sdu.dsl4ifc.generator.conditional.impls;

import org.sdu.dsl4ifc.generator.conditional.core.BooleanOperation;
import org.sdu.dsl4ifc.generator.conditional.core.Expression;
import org.sdu.dsl4ifc.generator.conditional.core.VariableStore;

public class AndOperation<T> extends BooleanOperation<T> {
    public AndOperation(Expression<T> left, Expression<T> right) {
        super(left, right);
    }

    @Override
    public boolean Evaluate(T item, VariableStore variables) {
        return this.left.Evaluate(item, variables) && this.right.Evaluate(item, variables);
    }
}