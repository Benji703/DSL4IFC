package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import org.sdu.dsl4ifc.generator.ParameterValueExtractor;

public class AttributeReference<T, U> {
	
	private final String referenceName;
	private final ParameterValueExtractor<T, U> extractor;
	private String attributeName;

	public AttributeReference(String referenceName, String attributeName, ParameterValueExtractor<T, U> extractor) {
		this.referenceName = referenceName;
		this.attributeName = attributeName;
		this.extractor = extractor;
	}

	public String getReferenceName() {
		return referenceName;
	}
	
	public String getAttributeName() {
		return attributeName;
	}

	public ParameterValueExtractor<T, U> getExtractor() {
		return extractor;
	}
	
	
}