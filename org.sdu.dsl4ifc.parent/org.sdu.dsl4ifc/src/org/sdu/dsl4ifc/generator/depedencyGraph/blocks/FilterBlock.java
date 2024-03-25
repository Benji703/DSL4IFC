package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.util.stream.Stream;

import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;

public class FilterBlock<T> extends Block<Stream<T>> {

	// TODO: How do we represent the boolean condition with objects?
	public FilterBlock(String name) {
		super(name);
	}

	@Override
	public boolean IsOutOfDate() {
		// Check a checksum of the file? Or the change date and length?
		return false;
	}

	@Override
	public Stream<T> Calculate() {
		// TODO: Get all type blocks (you can combine variable references)
		TypeBlock<T> source = findBlock(TypeBlock.class);
		source.getVariableName()								
		Stream<T> types = source.getOutput();
		
		types.filter(null)
		
		return types;
	}
}
