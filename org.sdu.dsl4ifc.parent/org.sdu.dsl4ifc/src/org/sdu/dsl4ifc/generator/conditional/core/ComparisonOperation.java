package org.sdu.dsl4ifc.generator.conditional.core;

import org.sdu.dsl4ifc.sustainLang.ComparisonOperator;

public abstract class ComparisonOperation<T, U> extends Expression<T> {
	protected ComparisonOperator comparison;
	
	public ComparisonOperation(ComparisonOperator comparison) {
		this.comparison = comparison;
	}
	
	protected boolean Compare(U left, U right) {
		switch (comparison) {
			case EQUALS: {
				if (left == null) {
					return left == right;
				}
				
				return left.equals(right);
			}
			
			case DIFFERENT: {
				if (left == null) {
					return left != right;
				}
				return !left.equals(right);
			}
			
			default: {
				throw new IllegalArgumentException("Unexpected value: " + comparison);
			}
		}
	}
}