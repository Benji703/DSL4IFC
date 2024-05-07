package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import org.sdu.dsl4ifc.generator.SustainLangGenerator;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;
import org.sdu.dsl4ifc.sustainLang.EPD;

import com.apstex.ifc2x3toolbox.ifc2x3.IfcBuildingElement;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcElementQuantity;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcLabel;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcQuantityVolume;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelAssociates;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelAssociatesMaterial;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcPhysicalQuantity;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcQuantityArea;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelDefines;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelDefinesByProperties;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcMaterialLayerSetUsage;
import com.apstex.step.core.SET;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lca.DomainClasses.Enums.EpdType;
import lca.LCA.*;

public class LcaCalcBlock extends VariableReferenceBlock<LCAIFCElement> {
	private String sourceVarName;
	private double area;
	private Map<String,String> matDefs;
	private String referenceName;
	private EpdType epdType;
	private LCA lca;
	
	public LcaCalcBlock(String name, String sourceVarName, String referenceName, double area, Map<String,String> matDefs, EPD epdType) {
		super(name);
		this.sourceVarName = sourceVarName;
		this.referenceName = referenceName;
		this.area = area;
		this.matDefs = matDefs;
		this.epdType = translateToJavaEnum(epdType);
		
		lca = new LCA(this.epdType);
	}

	private EpdType translateToJavaEnum(EPD xEpdType) {
		if (xEpdType == EPD.ECO) {
			return EpdType.EcoPlatform;
		} else if (xEpdType == EPD.BR18) {
			return EpdType.BR18;
		}
		
		SustainLangGenerator.consoleOut.println("Could not recognize EPD type. Using BR18 as default instead.");
		return EpdType.BR18;
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
	    
	    List<IfcBuildingElement> ifcElements = (List<IfcBuildingElement>) sourceVar.getOutput();
	    ArrayList<LCAIFCElement> elements = new ArrayList<>();
	    
	    for (IfcBuildingElement element : ifcElements) {
	    	var invSet = element.getIsDefinedBy_Inverse();
	    	
	    	LcaIfcQuantity quantity = getIfcQuantity(invSet);
	    	
	    	SET<IfcRelAssociates> associations = element.getHasAssociations_Inverse();
	    	if (associations == null)
	    		continue;
	    	
			String ifcMatName = getIfcMatName(associations);
	    	String epdId = matDefs.get(ifcMatName);
	    	
	    	String elementName = element.getName().getDecodedValue();
	    	
			elements.add(new LCAIFCElement(epdId, elementName, quantity));
	    }

        List<LCAIFCElement> lcaElements = lca.calculateLCAByElement(elements, area);
		
		return lcaElements;
	}

	private String getIfcMatName(SET<IfcRelAssociates> matSet) {
		
		for (IfcRelAssociates relAss : matSet) {
			if (!(relAss instanceof IfcRelAssociatesMaterial)) {
				continue;
			}
			
			var relAssMat = (IfcRelAssociatesMaterial)relAss;
			
			if (!(relAssMat.getRelatingMaterial() instanceof IfcMaterialLayerSetUsage)) {
				continue;
			}
			
			var relMat = ((IfcMaterialLayerSetUsage)relAssMat.getRelatingMaterial()).getForLayerSet();
			
			var ifcMatLayer = relMat.getMaterialLayers().get(0);
	
			return ifcMatLayer.getMaterial().getName().getValue();
		}
		
		return null;
	}

	private LcaIfcQuantity getIfcQuantity(SET<IfcRelDefines> invSet) {
		LcaIfcQuantity quantity = new LcaIfcQuantity();
		
		for (IfcRelDefines iRel : invSet) {
			
			if (!(iRel instanceof IfcRelDefinesByProperties)) {
				continue;
			}
			
			var iRelProp = (IfcRelDefinesByProperties)iRel;
			
			if (!(iRelProp.getRelatingPropertyDefinition() instanceof IfcElementQuantity)) {
				continue;
			}
			
			IfcElementQuantity elementQuant = (IfcElementQuantity)iRelProp.getRelatingPropertyDefinition();
			
			quantity = GetQuantity(elementQuant);
		}
		return quantity;
	}

	private LcaIfcQuantity GetQuantity(IfcElementQuantity elementQuant) {
		double grossVolume = 0;
		double grossSideArea = 0;
		
		for (IfcPhysicalQuantity q : elementQuant.getQuantities()) {
			if (q instanceof IfcQuantityVolume && q.getName().getDecodedValue().equals("GrossVolume")) {
				grossVolume = ((IfcQuantityVolume)q).getVolumeValue().getValue();
			}
			if (q instanceof IfcQuantityArea && q.getName().getDecodedValue().equals("GrossSideArea")) {
				grossSideArea = ((IfcQuantityArea)q).getAreaValue().getValue();
			}
		}
		
		return new LcaIfcQuantity(grossSideArea, grossVolume);
	}


	@Override
	public String generateCacheKey() {
		StringBuilder keyBuilder = new StringBuilder(Name);
		
		keyBuilder.append("source:"+sourceVarName+",");
		keyBuilder.append("reference:"+referenceName+",");
		keyBuilder.append("area:"+area+",");
		keyBuilder.append("matdefs:"+matDefsToString()+",");
		keyBuilder.append("epdType:"+epdType+",");
		
        for (Block<?> block : Inputs) {
            keyBuilder.append(block.generateCacheKey()+";");
        }
        return keyBuilder.toString();
	}

	@Override
	public String getReferenceName() {
		return referenceName;
	}
	
	private String matDefsToString() {
		var builder = new StringBuilder();
		
		for (var entry : matDefs.entrySet()) {
			builder.append(entry.getKey()+ " -> " + entry.getValue()+ ",");
		}
		
		return builder.toString();
	}
}
