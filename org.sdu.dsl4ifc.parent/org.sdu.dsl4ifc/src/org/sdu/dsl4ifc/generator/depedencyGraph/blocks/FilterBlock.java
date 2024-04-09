package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.util.List;
import java.util.stream.Stream;

import org.sdu.dsl4ifc.generator.conditional.core.Expression;
import org.sdu.dsl4ifc.generator.conditional.core.VariableStore;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.IVariableReference;

import com.apstex.ifc2x3toolbox.ifc2x3.IfcRoot;

public class FilterBlock extends VariableReferenceBlock<IfcRoot> implements IVariableReference  {

	private Expression<IfcRoot> expression;
	private VariableStore variables = new VariableStore();
	private String variableName;

	// TODO: How do we represent the boolean condition with objects?
	public FilterBlock(String name, String variableName, Expression<IfcRoot> expression) {
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
	public Stream<IfcRoot> Calculate() {
		var sources = findAllBlocks(TypeBlock.class);
		
		List<IfcRoot> toBeFiltered = null;
		
		for (TypeBlock<?> typeBlock : sources) {
			var variableName = typeBlock.getReferenceName();
			
			if (this.variableName == variableName) {	// Is the primary variable
				toBeFiltered = ((Stream<IfcRoot>) typeBlock.getOutput()).toList();
				continue;
			}
			
			variables.put(variableName, (List<IfcRoot>) typeBlock.getOutput().toList());
		}
		
		var result = toBeFiltered.stream().filter(i -> expression.Evaluate(i, variables));
		
		var list = result.toList();
		
		return list.stream();
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
}
