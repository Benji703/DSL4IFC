package org.sdu.dsl4ifc.generator.conditional.impls;

import org.sdu.dsl4ifc.generator.conditional.core.BooleanOperation;
import org.sdu.dsl4ifc.generator.conditional.core.Expression;
import org.sdu.dsl4ifc.generator.conditional.core.VariableStore;

public class OrOperation<T> extends BooleanOperation<T> {
    public OrOperation(Expression<T> left, Expression<T> right) {
        super(left, right);
    }

    @Override
    public boolean Evaluate(T item, VariableStore variables) {
        return this.left.Evaluate(item, variables) || this.right.Evaluate(item, variables);
    }
    
    @Override
    public String toString() {
    	return left.toString() + " OR " + right.toString();
    }

	@Override
	public String getFilledExpression(T item) {
		return left.getFilledExpression(item) + " OR " + right.getFilledExpression(item);
	}
}
