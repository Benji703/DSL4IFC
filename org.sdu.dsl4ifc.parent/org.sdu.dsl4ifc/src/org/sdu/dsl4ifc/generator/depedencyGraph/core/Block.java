package org.sdu.dsl4ifc.generator.depedencyGraph.core;

import java.util.ArrayList;
import java.util.List;

public abstract class Block<T> implements IOutOfDate, ICalculation<T>, ICacheKeyGenerator {
	
    public String Name;
    private T output;
    private long timeOfCalculation = 0;
    
    public T getOutput() {
        Invoke();
        return output;
    }
    
    public List<Block<?>> Inputs = new ArrayList<>();

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

    public void Recalculate() {
        System.out.println("Calculating " + Name);
        timeOfCalculation = System.currentTimeMillis();
        output = Calculate();
    }

    public <U> U findFirstBlock(Class<U> blockType) {
        for (Block<?> block : Inputs) {
            if (blockType.isInstance(block)) {
                return blockType.cast(block);
            }
        }
        return null; // or throw an exception if the block is not found
    }
    
    public <U> List<U> findAllBlocks(Class<U> blockType) {
    	var returnList = new ArrayList<U>();
    	
        for (Block<?> block : Inputs) {
            if (blockType.isInstance(block)) {
            	returnList.add(blockType.cast(block));
            }
        }
        return returnList;
    }
    
    @Override
	public long GetTimeOfCalculation() {
		return timeOfCalculation;
    }
}

