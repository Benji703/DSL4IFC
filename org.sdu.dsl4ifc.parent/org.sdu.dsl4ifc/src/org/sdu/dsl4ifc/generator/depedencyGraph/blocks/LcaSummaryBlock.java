package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;

import com.apstex.ifc2x3toolbox.ifc2x3.IfcElementQuantity;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcQuantityVolume;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelAssociates;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelAssociatesMaterial;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcPhysicalQuantity;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelDefines;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRoot;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcWall;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelDefinesByProperties;
import com.apstex.step.core.SET;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lca.LCA.*;

public class LcaSummaryBlock extends VariableReferenceBlock<LCAResult> {
	private String sourceVarName;
	private double heatedArea;
	private double b6;
	
	public LcaSummaryBlock(String name, String sourceVarName, double heatedArea, double b6) {
		super(name);
		this.sourceVarName = sourceVarName;
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
		return sourceVarName;
	}
}
