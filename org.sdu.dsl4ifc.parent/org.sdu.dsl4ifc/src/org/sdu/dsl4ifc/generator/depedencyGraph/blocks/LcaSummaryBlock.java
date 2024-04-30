package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;

import java.util.List;
import lca.LCA.*;

public class LcaSummaryBlock extends VariableReferenceBlock<LCAResult> {
	private String sourceVarName;
	private double heatedArea;
	private double b6;
	private String referenceName;
	
	public LcaSummaryBlock(String name, String sourceVarName, String referenceName, double heatedArea, double b6) {
		super(name);
		this.sourceVarName = sourceVarName;
		this.referenceName = referenceName;
		this.heatedArea = heatedArea;
		this.b6 = b6;
	}

	@Override
	public boolean IsOutOfDate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<LCAResult> Calculate() {
		
		var lcaCalcBlock = findFirstBlock(LcaCalcBlock.class);
		var lcaElements = lcaCalcBlock.getOutput();
	    
		LCA lca = new LCA();

        LCAResult lcaResult = lca.CalculateLCAWhole(lcaElements, heatedArea, b6);
		
		return List.of(lcaResult);
	}

	@Override
	public String generateCacheKey() {
		StringBuilder keyBuilder = new StringBuilder(Name);
		
		keyBuilder.append(sourceVarName+",");
		keyBuilder.append("heatedArea:"+heatedArea+",");
		keyBuilder.append("b6:"+b6+",");
		
        for (Block<?> block : Inputs) {
            keyBuilder.append(block.generateCacheKey()+";");
        }
        return keyBuilder.toString();
	}

	@Override
	public String getReferenceName() {
		return referenceName;
	}
}
