package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import org.dhatim.fastexcel.Worksheet;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lca.LCA.*;

public class LcaCalcBlock extends VariableReferenceBlock<LCAIFCElement> {
	private String sourceVarName;
	private double area;
	private Map<String,String> matDefs;
	private String referenceName;
	private LCA lca = new LCA();
	
	public LcaCalcBlock(String name, String sourceVarName, String referenceName, double area, Map<String,String> matDefs) {
		super(name);
		this.sourceVarName = sourceVarName;
		this.referenceName = referenceName;
		this.area = area;
		this.matDefs = matDefs;
	}

	@Override
	public boolean IsOutOfDate() {
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

	@Override
	public void fillTraceInWorksheet(Worksheet worksheet, int startingRow) {
		
		int currentRow = startingRow;
		var elements = getOutput();
		
		worksheet.value(currentRow, 0, "IFC Name");
		worksheet.value(currentRow, 1, "A");
		worksheet.value(currentRow, 2, "C3");
		worksheet.value(currentRow, 3, "C4");
		worksheet.value(currentRow, 4, "D");
		worksheet.value(currentRow, 5, "Quantity");
		worksheet.value(currentRow, 6, "EPD ID");
		worksheet.value(currentRow, 7, "EDP Name");
		worksheet.value(currentRow, 8, "Result");
		
		for (LCAIFCElement element : elements) {
			currentRow++;
			
			// IfcName, A, C3, C4, D, Quantity, EPD ID, EPD Name, Result
			worksheet.value(currentRow, 0, element.getIfcName());
			worksheet.value(currentRow, 1, element.getAResult());
			worksheet.value(currentRow, 2, element.getC3Result());
			worksheet.value(currentRow, 3, element.getC4Result());
			worksheet.value(currentRow, 4, element.getdResult());
			worksheet.value(currentRow, 5, element.getQuantity().getGrossSideArea());	// TODO: Choose the quantity that is used in the calculation
			worksheet.value(currentRow, 6, element.getEpdId());
			worksheet.value(currentRow, 7, element.getEpdName());
			worksheet.value(currentRow, 8, element.getLcaVal());
			
		}
		
	}
}
