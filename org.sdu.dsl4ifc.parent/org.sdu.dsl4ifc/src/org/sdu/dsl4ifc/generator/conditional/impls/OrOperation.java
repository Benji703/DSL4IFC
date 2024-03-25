package org.sdu.dsl4ifc.generator.conditional.impls;

import org.sdu.dsl4ifc.generator.conditional.core.BooleanOperation;
import org.sdu.dsl4ifc.generator.conditional.core.Expression;

public class OrOperation extends BooleanOperation {
    public OrOperation(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public boolean Evaluate() {
        return this.left.Evaluate() || this.right.Evaluate();
    }
}