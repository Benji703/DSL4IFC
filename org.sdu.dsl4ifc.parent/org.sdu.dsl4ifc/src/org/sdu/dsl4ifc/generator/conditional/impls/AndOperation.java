package org.sdu.dsl4ifc.generator.conditional.impls;

import org.sdu.dsl4ifc.generator.conditional.core.BooleanOperation;
import org.sdu.dsl4ifc.generator.conditional.core.Expression;

public class AndOperation extends BooleanOperation {
    public AndOperation(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public boolean Evaluate() {
        return this.left.Evaluate() && this.right.Evaluate();
    }
}