package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.util.List;
import java.util.stream.Stream;

import org.dhatim.fastexcel.Worksheet;
import org.sdu.dsl4ifc.generator.ParameterValueExtractor;
import org.sdu.dsl4ifc.generator.conditional.core.Expression;
import org.sdu.dsl4ifc.generator.conditional.core.VariableStore;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;
import com.apstex.ifc2x3toolbox.ifc2x3.InternalAccessClass;

public class FilterBlock extends VariableReferenceBlock<InternalAccessClass> {

	private Expression<InternalAccessClass> expression;
	private VariableStore variables = new VariableStore();
	private String variableName;

	// TODO: How do we represent the boolean condition with objects?
	public FilterBlock(String variableName, Expression<InternalAccessClass> expression) {
		super("Filter (" + variableName + ")");
		this.variableName = variableName;
		this.expression = expression;
	}

	@Override
	public boolean IsOutOfDate() {
		// Check a checksum of the file? Or the change date and length?
		return false;
	}

	@Override
	public List<InternalAccessClass> Calculate() {
		
		List<InternalAccessClass> toBeFiltered = findInputToFilter();
		var result = filter(toBeFiltered).toList();
		
		return result;
	}

	private Stream<InternalAccessClass> filter(List<InternalAccessClass> toBeFiltered) {
		return toBeFiltered.stream().filter(i -> expression.Evaluate(i, variables));
	}

	private List<InternalAccessClass> findInputToFilter() {
		var sources = findAllBlocks(TypeBlock.class);
		
		List<InternalAccessClass> toBeFiltered = null;
		
		for (TypeBlock<?> typeBlock : sources) {
			var variableName = typeBlock.getReferenceName();
			
			if (this.variableName.equals(variableName)) {	// Is the primary variable
				toBeFiltered = (List<InternalAccessClass>) typeBlock.getOutput();
				continue;
			}
			
			variables.put(variableName, (List<InternalAccessClass>) typeBlock.getOutput());
		}
		return toBeFiltered;
	}

	@Override
	public String getReferenceName() {
		return variableName;
	}
	
	@Override
	public String generateCacheKey() {
		StringBuilder keyBuilder = new StringBuilder(Name);
		
		keyBuilder.append(variableName+",");
		keyBuilder.append(expression.toString()+",");
		
        for (Block<?> block : Inputs) {
            keyBuilder.append(block.generateCacheKey()+";");
        }
        return keyBuilder.toString();
	}

	@Override
	public void fillTraceInWorksheet(Worksheet worksheet, int startingRow) {
		
		// All inputs and the expression shown with filled in values and the resulting value
		// stepnumber, expression filled in, filter result
		
		int currentRow = startingRow;
		
		var stepNumberExtractor = new ParameterValueExtractor<>("stepnumber");
		
		var input = findInputToFilter();
		var rows = input.stream().map(i -> {
			
			var stepNumber = (String) stepNumberExtractor.getParameterValue(i);
			var filledExpression = expression.getFilledExpression(i);
			var result = expression.Evaluate(i, variables);
			
			return new FilterTraceRow(Integer.parseInt(stepNumber), filledExpression, result);
		}).sorted((o1, o2) -> o1.stepNumber - o2.stepNumber).toList();
		
		worksheet.value(currentRow, 0, "StepNumber");	worksheet.style(currentRow, 0).bold().set();
		worksheet.value(currentRow, 1, "Expression (* = primary variable)");	worksheet.style(currentRow, 1).bold().wrapText(false).set();
		worksheet.value(currentRow, 2, "Inclusion");	worksheet.style(currentRow, 2).bold().set();
		
		for (var row : rows) {
			currentRow++;
			
			worksheet.value(currentRow, 0, row.stepNumber);
			worksheet.value(currentRow, 1, row.filledExpression);
			worksheet.value(currentRow, 2, row.included);
		}
		
	}
}

class FilterTraceRow {
	int stepNumber;
	String filledExpression;
	boolean included;
	
	public FilterTraceRow(int stepNumber, String filledExpression, boolean included) {
		super();
		this.stepNumber = stepNumber;
		this.filledExpression = filledExpression;
		this.included = included;
	}
	
	
}
