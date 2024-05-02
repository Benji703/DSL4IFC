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
 * @param <Y> The input entity/IFC class for the secondary variable
 * @param <T> The output type of the extractor and the input value for the left value.
 */
public class CompareValueToParameterValueOperation<Y, T> extends ComparisonOperation<Y, T> {
    private String rightVariableName;
	private ParameterValueExtractor<Y, T> rightValueExtractor;
	private T leftValue;


	//
    public CompareValueToParameterValueOperation(T leftValue, String rightVariableName, ParameterValueExtractor<Y, T> rightValueExtractor, ComparisonOperator comparisonOperator) {
		super(comparisonOperator);
		this.leftValue = leftValue;
    	this.rightVariableName = rightVariableName;
		this.rightValueExtractor = rightValueExtractor;
    }


    @Override
    public boolean Evaluate(Y item, VariableStore variables) {
        // Get the field values somehow
        List<Y> secondaryItems = (List<Y>) variables.get(rightVariableName);
        
        // TODO: Should only compute once as the item does not change
        
		return secondaryItems.stream().anyMatch(r -> {
				T secondaryValue = rightValueExtractor.getParameterValue(r);
				
				return Compare(leftValue, secondaryValue);
			});
    }
    
    @Override
    public String toString() {
    	return leftValue + " " + this.comparison + " " + rightValueExtractor.getParameterName();
    }


	@Override
	public String getFilledExpression(Y item) {
		return "\""+leftValue+"\"" + " " + this.comparison + " " + "\""+rightValueExtractor.getParameterValue(item)+"\""+"*";
	}
}