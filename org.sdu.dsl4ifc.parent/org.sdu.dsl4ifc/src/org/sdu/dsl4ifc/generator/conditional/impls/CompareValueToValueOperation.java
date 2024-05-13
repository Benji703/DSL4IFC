package org.sdu.dsl4ifc.generator.conditional.impls;

import org.sdu.dsl4ifc.generator.conditional.core.ComparisonOperation;
import org.sdu.dsl4ifc.generator.conditional.core.VariableStore;
import org.sdu.dsl4ifc.sustainLang.ComparisonOperator;

/**
 * This is two value comparison. Fx "a" = "b", 1 = 1, or "a" = 2.
 * Cannot be used for IFC objects. Use EntityValue instead.
 * @author andreasedalpedersen
 *
 * @param <T>
 * @param <U>
 */
public class CompareValueToValueOperation<T, U> extends ComparisonOperation<U, T> {

	// T is the type of the actual value to compare. Not the ifc object. The same with U
    public CompareValueToValueOperation(T left, T right, ComparisonOperator comparisonOperator) {
    	super(comparisonOperator);
		this.left = left;
        this.right = right;
    }

    private final T left;
    private final T right;

    @Override
    public boolean Evaluate(U item, VariableStore variables) {
        // Get the field values somehow
        return left.equals(right);
    }
    
    @Override
    public String toString() {
    	return left + " " + this.comparison + " " + right;
    }

	@Override
	public String getFilledExpression(U item) {
		return "\""+left+"\"" + " " + this.comparison + " " + "\""+right+"\"";
	}
}
