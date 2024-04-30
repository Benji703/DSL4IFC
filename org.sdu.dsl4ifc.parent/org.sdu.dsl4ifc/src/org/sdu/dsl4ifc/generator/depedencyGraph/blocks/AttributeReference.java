package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import org.sdu.dsl4ifc.generator.IExtractor;
import org.sdu.dsl4ifc.generator.ParameterValueExtractor;

public class AttributeReference<T> {
	
	private final String referenceName;
	private final IExtractor<T, String> extractor;
	private String attributeName;

	public AttributeReference(String referenceName, String attributeName, IExtractor<T, String> extractor) {
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

	public IExtractor<T, String> getExtractor() {
		return extractor;
	}
	
	@Override
	public String toString() {
		return referenceName + ": " + extractor.toString();
	}
}