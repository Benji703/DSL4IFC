package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.util.List;

import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.IVariableReference;

public abstract class VariableReferenceBlock<T> extends Block<List<T>> implements IVariableReference {

	public VariableReferenceBlock(String name) {
		super(name);
	}

}
