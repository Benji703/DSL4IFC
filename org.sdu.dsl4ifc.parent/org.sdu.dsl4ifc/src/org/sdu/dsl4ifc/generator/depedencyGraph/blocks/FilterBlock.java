package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.sdu.dsl4ifc.generator.conditional.core.Expression;
import org.sdu.dsl4ifc.generator.conditional.core.VariableStore;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;

public class FilterBlock<T> extends Block<Stream<T>> {

	private Expression<T> expression;
	private VariableStore variables = new VariableStore();
	private String variableName;

	// TODO: How do we represent the boolean condition with objects?
	public FilterBlock(String name, String variableName, Expression<T> expression) {
		super(name);
		this.variableName = variableName;
		this.expression = expression;
	}

	@Override
	public boolean IsOutOfDate() {
		// Check a checksum of the file? Or the change date and length?
		return false;
	}

	@Override
	public Stream<T> Calculate() {
		// TODO: Get all type blocks (you can combine variable references)
		var sources = findAllBlocks(TypeBlock.class);
		
		Stream<T> toBeFiltered = null;
		
		for (TypeBlock<?> typeBlock : sources) {
			var variableName = typeBlock.getVariableName();
			
			if (this.variableName == variableName) {	// Is the primary variable
				toBeFiltered = (Stream<T>) typeBlock.getOutput();
				continue;
			}
			
			variables.put(variableName, (List<Object>) typeBlock.getOutput().toList());
		}
		
		var result = toBeFiltered.filter(i -> expression.Evaluate(i, variables));
		
		return result;
	}
}
