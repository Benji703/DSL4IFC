package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;

import com.apstex.ifc2x3toolbox.ifc2x3.IfcElementQuantity;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcMaterialLayerSet;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcMaterialSelect;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcQuantityVolume;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelAssociates;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelAssociatesMaterial;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcPhysicalQuantity;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelDefines;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRoot;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcWall;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelDefinesByProperties;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcMaterialLayerSetUsage;
import com.apstex.step.core.SET;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lca.LCA.*;

public class LcaCalcBlock extends Block<List<LCAIFCElement>> {
	private String sourceVarName;
	private String path;
	private int area;
	private Map<String,String> matDefs;
	private LCA lca = new LCA();
	
	public LcaCalcBlock(String name, String sourceVarName, int area, Map<String,String> matDefs) {
		super(name);
		this.sourceVarName = sourceVarName;
		this.area = area;
		this.matDefs = matDefs;
	}

	@Override
	public boolean IsOutOfDate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<LCAIFCElement> Calculate() {
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
	    	
	    	volume = getIfcVolume(invSet);
	    	
	    	String matId = getIfcMatId(iWall.getHasAssociations_Inverse());
	    	
	    	elements.add(new LCAIFCElement(matId,volume));
	    }

        List<LCAIFCElement> lcaElements = lca.calculateLCAByElement(elements, area);
		
		return lcaElements;
	}

	private String getIfcMatId(SET<IfcRelAssociates> matSet) {
		
		for (IfcRelAssociates relAss : matSet) {
			if (relAss.getClass() != IfcRelAssociatesMaterial.class) {
				continue;
			}
			
			var relAssMat = (IfcRelAssociatesMaterial)relAss;
			
			if (!(relAssMat.getRelatingMaterial() instanceof IfcMaterialLayerSetUsage)) {
				continue;
			}
			
			var relMat = ((IfcMaterialLayerSetUsage)relAssMat.getRelatingMaterial()).getForLayerSet();
			
			var ifcMatLayer = relMat.getMaterialLayers().get(0);
			
			var s = ifcMatLayer.getMaterial().getName().getValue();
			return matDefs.get(s);
		}
		
		return null;
	}

	private double getIfcVolume(SET<IfcRelDefines> invSet) {
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
		return volume;
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
}
