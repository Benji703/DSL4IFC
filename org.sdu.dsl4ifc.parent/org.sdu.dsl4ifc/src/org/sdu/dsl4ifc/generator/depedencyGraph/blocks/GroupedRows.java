package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.util.List;

public class GroupedRows<T> {
	public List<T> elements;
	
	public GroupedRows(List<T> elements) {
		this.elements = elements;
	}
}
