package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;

import com.apstex.ifc2x3toolbox.ifc2x3.IfcElementQuantity;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcQuantityVolume;
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

public class LcaBlock extends Block<LCAResult> {
	private String sourceVarName;
	private String summaryVariableName;
	private String entitiesVariableName;
	
	
	public LcaBlock(String name, String sourceVarName, String summaryVariableName, String entitiesVariableName) {
		super(name);
		this.sourceVarName = sourceVarName;
		this.summaryVariableName = summaryVariableName;
		this.entitiesVariableName = entitiesVariableName;
	}

	@Override
	public boolean IsOutOfDate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LCAResult Calculate() {
		//sources.get(0).getOutput;
		
		List<VariableReferenceBlock> references = findAllBlocks(VariableReferenceBlock.class);
		
		if (references.size() > 1 || references.size() == 0) {
			//Be Sad
			return null;
		}
		System.out.println("LCA Babeh");
		
	    var sourceVar = (VariableReferenceBlock<?>)references.get(0);
	    
	    List<IfcWall> ifcElements = (List<IfcWall>)sourceVar.getOutput();
	    ArrayList<LCAIFCElement> elements = new ArrayList<>();
	    
	    for (IfcWall iWall : ifcElements) {
	    	var invSet = iWall.getIsDefinedBy_Inverse();
	    	double volume = 0;
	    	
	    	for (IfcRelDefines iRel : invSet) {
	    		
	    		if (iRel.getClass() != IfcRelDefinesByProperties.class) {
	    			continue;
	    		}
	    		
	    		var iRelProp = (IfcRelDefinesByProperties)iRel;
	    		
	    		if (iRelProp.getRelatingPropertyDefinition().getClass() != IfcElementQuantity.class) {
	    			continue;
	    		}
	    		
	    		IfcElementQuantity elementQuant = (IfcElementQuantity)iRelProp.getRelatingPropertyDefinition();
	    		
	    		volume = GetQuanityVolume(elementQuant);
	    	}
	    	
	    	elements.add(new LCAIFCElement("Letbeton vægelement, 150 mm tyk væg, 10% udsparinger",volume));
	    }
	    
		
		LCA lca = new LCA();

        LCAResult lcaResult = lca.CalculateLCAWhole(elements, 200, 180, 1000);
		
		return lcaResult;
	}

	private double GetQuanityVolume(IfcElementQuantity elementQuant) {
		
		for (IfcPhysicalQuantity q : elementQuant.getQuantities()) {
			if (q.getClass() == IfcQuantityVolume.class) {
				return ((IfcQuantityVolume)q).getVolumeValue().getValue();
			}
		}
		
		return 0.0;
	}

	@Override
	public String generateCacheKey() {
		StringBuilder keyBuilder = new StringBuilder(Name);
		
		keyBuilder.append(sourceVarName+",");
		
        for (Block<?> block : Inputs) {
            keyBuilder.append(block.generateCacheKey()+";");
        }
        return keyBuilder.toString();
	}

	public String getEntitiesVariableName() {
		return entitiesVariableName;
	}

	public String getSummaryVariableName() {
		return summaryVariableName;
	}

}
