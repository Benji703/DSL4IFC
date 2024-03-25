package org.sdu.dsl4ifc.generator.conditional.core;

import java.util.stream.Stream;

public class Variable<T> extends Atom<T> {
    // Should support getting fields from a specific variable
    @Override
    public Stream<T> getValues() {
        return null;
    }
}