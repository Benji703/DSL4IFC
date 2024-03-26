package org.sdu.dsl4ifc.generator.conditional.impls;

import org.sdu.dsl4ifc.generator.conditional.core.ComparisonOperation;
import org.sdu.dsl4ifc.generator.conditional.core.VariableStore;

public class ValueEqualsVariableOperation<U> extends ComparisonOperation<U> {
    private String variableName;


	// T is the type of the actual value to compare. Not the ifc object. The same with U
    public ValueEqualsVariableOperation(String variableName) {
		this.variableName = variableName;
    }


    @Override
    public boolean Evaluate(U item, VariableStore variables) {
        // Get the field values somehow
        return variables.get(variableName).stream().anyMatch(r -> r.equals(item));
    }
}