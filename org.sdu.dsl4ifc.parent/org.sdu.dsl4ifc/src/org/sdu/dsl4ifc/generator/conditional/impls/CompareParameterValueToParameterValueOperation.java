package org.sdu.dsl4ifc.generator.conditional.impls;

import java.util.List;

import org.sdu.dsl4ifc.generator.ParameterValueExtractor;
import org.sdu.dsl4ifc.generator.conditional.core.ComparisonOperation;
import org.sdu.dsl4ifc.generator.conditional.core.VariableStore;
import org.sdu.dsl4ifc.sustainLang.ComparisonOperator;

/**
 * Sets up a equality comparison where values coming from the primary value extractor performed on primary entities are compared with values
 * coming from the secondary value extractor run on all secondary values.
 * 
 * This compares to "doors.name = windows.name" where doors is the primary variable, windows is the secondary variable and the name value is found through the extractors.
 * 
 * @author andreasedalpedersen
 *
 * @param <U> The input entity/IFC class for the primary variable
 * @param <Y> The input entity/IFC class for the secondary variable
 * @param <T> The output type of the extractors. Must be the same for both extractors.
 */
public class CompareParameterValueToParameterValueOperation<U, Y, T> extends ComparisonOperation<U, T> {
    private String rightVariableName;
	private ParameterValueExtractor<U, T> primaryValueExtractor;
	private ParameterValueExtractor<Y, T> secondaryValueExtractor;


	//
    public CompareParameterValueToParameterValueOperation(String rightVariableName, ParameterValueExtractor<U, T> primaryValueExtractor, ParameterValueExtractor<Y, T> secondaryValueExtractor, ComparisonOperator comparisonOperator) {
		super(comparisonOperator);
    	this.rightVariableName = rightVariableName;
		this.primaryValueExtractor = primaryValueExtractor;
		this.secondaryValueExtractor = secondaryValueExtractor;
    }


    @Override
    public boolean Evaluate(U item, VariableStore variables) {
        // Get the field values somehow
    	T primaryValue = primaryValueExtractor.getParameterValue(item);
        List<Y> secondaryItems = (List<Y>) variables.get(rightVariableName);
        
		return secondaryItems.stream().anyMatch(r -> {
				T secondaryValue = secondaryValueExtractor.getParameterValue(r);
				
				return Compare(primaryValue, secondaryValue);
			});
    }
    
    @Override
    public String toString() {
    	return primaryValueExtractor.getParameterName() + " " + this.comparison +" " + secondaryValueExtractor.getParameterName();
    }


	@Override
	public String getFilledExpression(U item) {
		return "\""+primaryValueExtractor.getParameterValue(item)+"\""+ "*" + " " + this.comparison +" " + secondaryValueExtractor.getParameterName();
	}
}