package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.util.List;

public class GroupedRows<T> {
	public List<T> elements;
	public List<String> groupedFields;
	
	public GroupedRows(List<T> elements, List<String> groupedFields) {
		this.elements = elements;
		this.groupedFields = groupedFields.stream().map(t -> t.toLowerCase()).toList();
	}
}
