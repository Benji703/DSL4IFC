package org.sdu.dsl4ifc.generator.conditional.core;

import java.util.stream.Stream;

public interface IAtomicValue<T> {
    public Stream<T> getValues();
}