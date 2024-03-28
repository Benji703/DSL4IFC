package org.sdu.dsl4ifc.generator.conditional.core;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class StringValue extends Atom<String> {  // String until now
    private final List<String> values;

    // Be a list of one value
    public StringValue(String value) {
        this.values = Collections.singletonList(value);
    }

    @Override
    public Stream<String> getValues() {
        return this.values.stream();
    }
}