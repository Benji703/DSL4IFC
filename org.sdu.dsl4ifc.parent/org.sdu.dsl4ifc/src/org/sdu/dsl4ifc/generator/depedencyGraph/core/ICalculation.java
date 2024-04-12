package org.sdu.dsl4ifc.generator.depedencyGraph.core;

public interface ICalculation<T> {
    /**
     * How the block is calculated
     * @return The result of the calculation
     * @throws Exception 
     */
    T Calculate();
    
    long GetTimeOfCalculation();
}
