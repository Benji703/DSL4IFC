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
	private String path;
	
	public LcaBlock(String name, String sourceVarName, String path) {
		super(name);
		this.sourceVarName = sourceVarName;
		this.path = path;
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
	    
	    List<IfcWall> ifcElements = (List<IfcWall>)sourceVar.getOutput().toList();
	    
	    for (IfcWall iWall : ifcElements) {
	    	var invSet = iWall.getIsDefinedBy_Inverse();
	    	
	    	for (IfcRelDefines iRel : invSet) {
	    		
	    		if (iRel.getClass() != IfcRelDefinesByProperties.class) {
	    			continue;
	    		}
	    		
	    		var iRelProp = (IfcRelDefinesByProperties)iRel;
	    		
	    		if (iRelProp.getRelatingPropertyDefinition().getClass() != IfcElementQuantity.class) {
	    			continue;
	    		}
	    		
	    		IfcElementQuantity elementQuant = (IfcElementQuantity)iRelProp.getRelatingPropertyDefinition();
	    		
	    		double volume = GetQuanityVolume(elementQuant);
	    	}
	    }
	    
		
		LCA lca = new LCA();

        String concrete = "Letbeton vægelement, 150 mm tyk væg, 10% udsparinger";
        String floorS = "Celleglas-isolering 115 kg/m³";

        LCAIFCElement wall1 = new LCAIFCElement(concrete, 200);
        wall1.setLifeTime(12);
        LCAIFCElement wall2 = new LCAIFCElement(concrete, 200);
        wall2.setLifeTime(70);
        LCAIFCElement wall3 = new LCAIFCElement(concrete, 200);
        LCAIFCElement wall4 = new LCAIFCElement(concrete, 200);

        LCAIFCElement floor = new LCAIFCElement(floorS, 1000);

        ArrayList<LCAIFCElement> elements = new ArrayList<>();
        elements.add(wall1);
        elements.add(wall2);
        elements.add(wall3);
        elements.add(wall4);
        elements.add(floor);

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

}
