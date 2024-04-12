package org.sdu.dsl4ifc.generator.conditional.impls;

import org.sdu.dsl4ifc.generator.conditional.core.Expression;
import org.sdu.dsl4ifc.generator.conditional.core.VariableStore;

public class TrueValue<T> extends Expression<T> {

	@Override
	public boolean Evaluate(T item, VariableStore variables) {
		return true;
	}
	
	@Override
    public String toString() {
    	return "TRUE";
    }

}
