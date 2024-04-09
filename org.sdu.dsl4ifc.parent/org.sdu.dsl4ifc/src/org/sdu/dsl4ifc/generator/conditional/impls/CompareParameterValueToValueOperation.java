package org.sdu.dsl4ifc.generator.conditional.impls;

import org.sdu.dsl4ifc.generator.ParameterValueExtractor;
import org.sdu.dsl4ifc.generator.conditional.core.ComparisonOperation;
import org.sdu.dsl4ifc.generator.conditional.core.VariableStore;
import org.sdu.dsl4ifc.sustainLang.ComparisonOperator;

/**
 * Sets up a equality comparison where values coming from the primary value extractor performed on primary entities are compared with one value.
 * 
 * This compares to 'doors.name = "Door 1" ' where doors is the primary variable, "Door 1" is the name to compare with.
 * 
 * @author andreasedalpedersen
 *
 * @param <U> The input entity/IFC class for the primary variable
 * @param <Y> The input entity/IFC class for the secondary variable
 * @param <T> The output type of the extractors. Must be the same for both extractors.
 */
public class CompareParameterValueToValueOperation<U, Y, T> extends ComparisonOperation<U, T> {
    private T rightValue;
	private ParameterValueExtractor<U, T> leftValueExtractor;


	public CompareParameterValueToValueOperation(T rightValue, ParameterValueExtractor<U, T> leftValueExtractor, ComparisonOperator operator) {
		super(operator);
    	this.rightValue = rightValue;
		this.leftValueExtractor = leftValueExtractor; 
    }


    @Override
    public boolean Evaluate(U item, VariableStore variables) {
        // Get the field values somehow
    	T primaryValue = leftValueExtractor.getParameterValue(item);
    	return Compare(primaryValue, rightValue);
    }

    @Override
    public String toString() {
    	return leftValueExtractor.getParameterName() + " " + this.comparison +" " + rightValue;
    }
	
}