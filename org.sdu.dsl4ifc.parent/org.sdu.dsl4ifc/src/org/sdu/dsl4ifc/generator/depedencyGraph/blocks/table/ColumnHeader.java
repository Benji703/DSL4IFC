package org.sdu.dsl4ifc.generator.depedencyGraph.blocks.table;

import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.AttributeReference;

public class ColumnHeader {
	String headerText;
	AttributeReference<?,String> attributeReference;
	
	public ColumnHeader(String headerText, AttributeReference<?,String> reference) {
		this.headerText = headerText;
		this.attributeReference = reference;
	}
}