package org.sdu.dsl4ifc.generator;

public interface IExtractor<T, U> {

	U getParameterValue(T item);

}