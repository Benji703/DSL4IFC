package org.sdu.dsl4ifc.generator.depedencyGraph.core;

import java.util.ArrayList;
import java.util.List;

public abstract class Block<T> implements IOutOfDate, IRecalculation, ICalculation<T> {
    public String Name;
    private T output;
    public T getOutput() {
        Invoke();
        return output;
    }
    public final List<Block<?>> Inputs = new ArrayList<>();

    public Block(String name) {
        Name = name;
    }

    public void AddInput(Block<?> block) {
        Inputs.add(block);
    }

    public void Invoke() {
        System.out.println("Invoking " + Name);
        for (var input : Inputs) {
            input.Invoke();
        }

        if (output == null || IsOutOfDate())
            Recalculate();
    }

    @Override
    public void Recalculate() {
        System.out.println("Calculating " + Name);
        output = Calculate();
    }

    public <U> U findBlock(Class<U> blockType) {
        for (Block<?> block : Inputs) {
            if (blockType.isInstance(block)) {
                return blockType.cast(block);
            }
        }
        return null; // or throw an exception if the block is not found
    }
}

