package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import org.sdu.dsl4ifc.generator.SustainLangGenerator;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;
import org.sdu.dsl4ifc.sustainLang.EPD;
import org.dhatim.fastexcel.Worksheet;
import org.sdu.dsl4ifc.generator.SustainLangGenerator;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;
import org.sdu.dsl4ifc.sustainLang.AreaAuto;
import org.sdu.dsl4ifc.sustainLang.AreaSource;
import org.sdu.dsl4ifc.sustainLang.AreaValue;

import com.apstex.ifc2x3toolbox.ifc2x3.IfcBuilding;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcBuildingElement;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcElementQuantity;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcQuantityVolume;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelAssociates;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelAssociatesMaterial;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcPhysicalQuantity;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcQuantityArea;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelDefines;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelDefinesByProperties;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcWall;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcMaterialLayerSetUsage;
import com.apstex.step.core.SET;



import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lca.DomainClasses.Enums.EpdType;
import lca.Interfaces.IIfcQuantityCollector;

import java.util.stream.Collectors;
import lca.LCA.*;
import lca.ifc.IfcWallQuantityCollector;

public class LcaCalcBlock extends VariableReferenceBlock<LCAIFCElement> {
	private String sourceVarName;
	private AreaSource area;
	private Double areaValue = null;
	private Map<String,String> matDefs;
	private String referenceName;
	private EpdType epdType;
	private LCA lca;
	
	public LcaCalcBlock(String sourceVarName, String referenceName, AreaSource area, Map<String,String> matDefs, EPD epdType) {
		super("LCA Calculation (source " + sourceVarName + ")");
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
		return false;
		
	}

	@Override
	public List<LCAIFCElement> Calculate() {
		IIfcQuantityCollector<IfcWall> wallQuantCollector;
		
		List<VariableReferenceBlock> references = findAllBlocks(VariableReferenceBlock.class);
		
		if (references.size() > 1 || references.size() == 0) {
			//Be Sad
			return null;
		}
		
	    var sourceVar = (VariableReferenceBlock<?>)references.get(0);
	    
	    List<IfcBuildingElement> ifcElements = (List<IfcBuildingElement>) sourceVar.getOutput();
	    ArrayList<LCAIFCElement> elements = new ArrayList<>();
	    
	    for (IfcBuildingElement element : ifcElements) {

	    	
	    	LcaIfcQuantity quantity = new LcaIfcQuantity();
	    	
	    	var invSet = element.getIsDefinedBy_Inverse();
	    	
	    	LcaIfcQuantity quantity = getIfcQuantity(invSet);
	    	
	    	SET<IfcRelAssociates> associations = element.getHasAssociations_Inverse();
	    	if (associations == null)
	    		continue;
	    	
			String ifcMatName = getIfcMatName(associations);
	    	String epdId = matDefs.get(ifcMatName);
	    	
	    	String elementName = element.getName().getDecodedValue();
	    	
			elements.add(new LCAIFCElement(epdId, elementName, element.getStepLineNumber(), quantity));
	    }

	    Double area = getArea();
        List<LCAIFCElement> lcaElements = lca.calculateLCAByElement(elements, area);
		
		return lcaElements;
	}
	
	private <T extends IfcBuildingElement> IIfcQuantityCollector<T> getQuantityCollector(IfcBuildingElement element) {
		
    	if (element instanceof IfcWall) {
    		return (IIfcQuantityCollector<T>) new IfcWallQuantityCollector();
    	}
    	
    	return null;
	}
	
	public Double getArea() {
		
		if (areaValue != null) {
			return areaValue;
		}
		
		if (area instanceof AreaValue) {
			areaValue = ((AreaValue) area).getArea();
		}
		if (area instanceof AreaAuto) {
			var parserBlock = findFirstBlock(Ifc2x3ParserBlock.class);
			var ifcModel = parserBlock.getOutput();
			
			var ifcBuldings = ifcModel.getCollection(IfcBuilding.class);
			
			areaValue = getSomeFloorAreaSum(ifcBuldings);
			
			if (areaValue == null) {
				SustainLangGenerator.consoleOut.println("WARNING: Could not find an area value. The LCA results using the area will be null.");
			}
		}
		
		return areaValue;
	}

	private Double getSomeFloorAreaSum(Collection<IfcBuilding> ifcBuldings) {
		Double area = ifcBuldings.stream().collect(Collectors.summingDouble(building -> {
				SET<IfcRelDefines> isDefinedBy = building.getIsDefinedBy_Inverse();
				
				for (IfcRelDefines iRel : isDefinedBy) {
					
					if (!(iRel instanceof IfcRelDefinesByProperties)) {
						continue;
					}
					
					var iRelProp = (IfcRelDefinesByProperties) iRel;
					
					if (!(iRelProp.getRelatingPropertyDefinition() instanceof IfcElementQuantity)) {
						continue;
					}
					
					IfcElementQuantity elementQuant = (IfcElementQuantity) iRelProp.getRelatingPropertyDefinition();
					
					for (IfcPhysicalQuantity quantity : elementQuant.getQuantities()) {
						if (quantity instanceof IfcQuantityArea && quantity.getName().getDecodedValue().equals("NetFloorArea")) {
							return ((IfcQuantityArea) quantity).getAreaValue().getValue();
						}
						if (quantity instanceof IfcQuantityArea && quantity.getName().getDecodedValue().equals("GrossFloorArea")) {
							SustainLangGenerator.consoleOut.println("WARNING: Using gross area as area value in LCA calculation!");
							return ((IfcQuantityArea) quantity).getAreaValue().getValue();
						}
						
					}
				}
				
				return 0;
			}));
		
		return area == 0 ? null : area;
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
		if (invSet == null) {
			return quantity;
		}
		
		
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
		keyBuilder.append("area:"+getAreaCacheKey()+",");
		keyBuilder.append("matdefs:"+matDefsToString()+",");
		keyBuilder.append("epdType:"+epdType+",");
		
        for (Block<?> block : Inputs) {
            keyBuilder.append(block.generateCacheKey()+";");
        }
        return keyBuilder.toString();
	}
	
	private String getAreaCacheKey() {
		if (area instanceof AreaValue) {
			return ((AreaValue) area).getArea()+"";
		}
		if (area instanceof AreaAuto) {
			return "AUTO";
		}
		return null;
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
		worksheet.value(currentRow, 1, "IFC Step Number");
		worksheet.value(currentRow, 2, "A");
		worksheet.value(currentRow, 3, "C3");
		worksheet.value(currentRow, 4, "C4");
		worksheet.value(currentRow, 5, "D");
		worksheet.value(currentRow, 6, "Quantity");
		worksheet.value(currentRow, 7, "EPD ID");
		worksheet.value(currentRow, 8, "EDP Name");
		worksheet.value(currentRow, 9, "Result");
		
		for (LCAIFCElement element : elements) {
			currentRow++;
			
			// IfcName, Step number, A, C3, C4, D, Quantity, EPD ID, EPD Name, Result
			worksheet.value(currentRow, 0, element.getIfcName());
			worksheet.value(currentRow, 1, element.getIfcStepNumber());
			worksheet.value(currentRow, 2, element.getAResult());
			worksheet.value(currentRow, 3, element.getC3Result());
			worksheet.value(currentRow, 4, element.getC4Result());
			worksheet.value(currentRow, 5, element.getdResult());
			worksheet.value(currentRow, 6, element.getQuantity().getGrossSideArea());	// TODO: Choose the quantity that is used in the calculation
			worksheet.value(currentRow, 7, element.getEpdId());
			worksheet.value(currentRow, 8, element.getEpdName());
			worksheet.value(currentRow, 9, element.getLcaVal());
			
		}
		
	}
	
}
