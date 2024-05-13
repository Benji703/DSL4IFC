package org.sdu.dsl4ifc.generator.depedencyGraph.blocks.table;

import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.AttributeReference;

public class ColumnHeader {
	public String headerText;
	public AttributeReference<?> attributeReference;
	
	public ColumnHeader(String headerText, AttributeReference<?> reference) {
		this.headerText = headerText;
		this.attributeReference = reference;
	}
}