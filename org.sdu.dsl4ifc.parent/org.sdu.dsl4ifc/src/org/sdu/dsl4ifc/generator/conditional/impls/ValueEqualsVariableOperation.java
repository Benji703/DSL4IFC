package org.sdu.dsl4ifc.generator.conditional.impls;

import java.util.List;

import org.sdu.dsl4ifc.generator.ParameterValueExtractor;
import org.sdu.dsl4ifc.generator.conditional.core.ComparisonOperation;
import org.sdu.dsl4ifc.generator.conditional.core.VariableStore;

public class ValueEqualsVariableOperation<U, Y, T> extends ComparisonOperation<U> {
    private String variableName;
	private ParameterValueExtractor<U, T> primaryValueExtractor;
	private ParameterValueExtractor<Y, T> secondaryValueExtractor;


	//
    public ValueEqualsVariableOperation(String variableName, ParameterValueExtractor<U, T> primaryValueExtractor, ParameterValueExtractor<Y, T> secondaryValueExtractor) {
		this.variableName = variableName;
		this.primaryValueExtractor = primaryValueExtractor;
		this.secondaryValueExtractor = secondaryValueExtractor;
    }


    @Override
    public boolean Evaluate(U item, VariableStore variables) {
        // Get the field values somehow
    	T primaryValue = primaryValueExtractor.getParameterValue(item);
        List<Y> secondaryItems = (List<Y>) variables.get(variableName);
        
		return secondaryItems.stream().anyMatch(r -> {
				T secondaryValue = secondaryValueExtractor.getParameterValue(r);
				return secondaryValue.equals(primaryValue);
			});
    }
}