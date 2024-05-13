package org.sdu.dsl4ifc.generator;

import java.util.Comparator;
import java.util.stream.Collectors;

import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.GroupedRows;
import org.sdu.dsl4ifc.sustainLang.AggregateFunction;

public class AggregateValueExtractor<InputType> implements IExtractor<GroupedRows<InputType>, String>{

	private String parameterName;
	private IExtractor<InputType, String> parameterValueExtractor;
	private AggregateFunction function;
	private MultiTypeComparator comparator;
	public String getParameterName() {
		return parameterName;
	}
	
	public AggregateValueExtractor(String parameterName, AggregateFunction function) {
		this.parameterValueExtractor = new ParameterValueExtractor<InputType, String>(parameterName);
		this.function = function;
		this.parameterName = parameterName.toLowerCase();
		
		this.comparator = new MultiTypeComparator();
		
	}
	
	public String getParameterValue(GroupedRows<InputType> item) {
		
		var elements = item.elements.stream();
		
		switch (function) {
		case COUNT:
			return (elements.count()+"");
			
		case SUM:
			return elements
					.collect(Collectors.summarizingDouble(value -> Double.parseDouble((String) parameterValueExtractor.getParameterValue(value))))
					.getSum()+"";

		case AVERAGE:
			return elements.collect(Collectors.averagingDouble(value -> Double.parseDouble((String) parameterValueExtractor.getParameterValue(value))))+"";
			
		case MAXIMUM:
			return elements.map(t -> parameterValueExtractor.getParameterValue(t)).collect(Collectors.maxBy((o1, o2) -> this.comparator.compare(o1, o2))).get();
		
		case MINIMUM:
			return elements.map(t -> parameterValueExtractor.getParameterValue(t)).collect(Collectors.minBy((o1, o2) -> this.comparator.compare(o1, o2))).get();
			
		default:
			SustainLangGenerator.consoleOut.println("This function is not implemented: " + function);
			break;
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return parameterName + ", fun: " + function;
	}
}

class MultiTypeComparator implements Comparator<Object> {

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 instanceof String && o2 instanceof String) {
            return ((String) o1).compareTo((String) o2);
        } else if (o1 instanceof Integer && o2 instanceof Integer) {
            return ((Integer) o1).compareTo((Integer) o2);
        } else if (o1 instanceof Double && o2 instanceof Double) {
            return ((Double) o1).compareTo((Double) o2);
        } else {
            // Convert to string and compare as a fallback if types are mixed
            String str1 = o1.toString();
            String str2 = o2.toString();
            return str1.compareTo(str2);
        }
    }
}

