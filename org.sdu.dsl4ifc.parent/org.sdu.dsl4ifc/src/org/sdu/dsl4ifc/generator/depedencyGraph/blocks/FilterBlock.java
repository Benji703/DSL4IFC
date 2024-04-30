package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.util.List;

import org.sdu.dsl4ifc.generator.conditional.core.Expression;
import org.sdu.dsl4ifc.generator.conditional.core.VariableStore;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.IVariableReference;

import com.apstex.ifc2x3toolbox.ifc2x3.IfcRoot;
import com.apstex.ifc2x3toolbox.ifc2x3.InternalAccessClass;

public class FilterBlock extends VariableReferenceBlock<InternalAccessClass> {

	private Expression<InternalAccessClass> expression;
	private VariableStore variables = new VariableStore();
	private String variableName;

	// TODO: How do we represent the boolean condition with objects?
	public FilterBlock(String name, String variableName, Expression<InternalAccessClass> expression) {
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
	public List<InternalAccessClass> Calculate() {
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
		
		var result = toBeFiltered.stream().filter(i -> expression.Evaluate(i, variables));
		
		var list = result.toList();
		
		return list;
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
