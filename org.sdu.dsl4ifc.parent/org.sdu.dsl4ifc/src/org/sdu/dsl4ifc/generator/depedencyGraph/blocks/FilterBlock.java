package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.util.List;
import java.util.stream.Stream;

import org.sdu.dsl4ifc.generator.conditional.core.Expression;
import org.sdu.dsl4ifc.generator.conditional.core.VariableStore;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.IVariableReference;

public class FilterBlock<T> extends VariableReferenceBlock<T> implements IVariableReference  {

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
		var sources = findAllBlocks(TypeBlock.class);
		
		List<T> toBeFiltered = null;
		
		for (TypeBlock<?> typeBlock : sources) {
			var variableName = typeBlock.getReferenceName();
			
			if (this.variableName == variableName) {	// Is the primary variable
				toBeFiltered = ((Stream<T>) typeBlock.getOutput()).toList();
				continue;
			}
			
			variables.put(variableName, ((Stream<Object>) typeBlock.getOutput()).toList());
		}
		
		var result = toBeFiltered.stream().filter(i -> expression.Evaluate(i, variables));
		
		var list = result.toList();
		
		return list.stream();
	}

	@Override
	public String getReferenceName() {
		return variableName;
	}
}
