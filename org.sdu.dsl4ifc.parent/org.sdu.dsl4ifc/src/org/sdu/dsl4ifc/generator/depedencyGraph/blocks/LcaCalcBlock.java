package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import org.sdu.dsl4ifc.generator.SustainLangGenerator;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;
import org.sdu.dsl4ifc.sustainLang.EPD;
import org.dhatim.fastexcel.Worksheet;
import org.sdu.dsl4ifc.sustainLang.AreaAuto;
import org.sdu.dsl4ifc.sustainLang.AreaSource;
import org.sdu.dsl4ifc.sustainLang.AreaValue;

import com.apstex.ifc2x3toolbox.ifc2x3.IfcBeam;
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

import lca.DomainClasses.Enums.DeclaredUnitEnum;
import lca.DomainClasses.Enums.EpdType;
import lca.Interfaces.IIfcMaterialCollector;
import lca.Interfaces.IIfcQuantityCollector;

import java.util.stream.Collectors;
import lca.LCA.*;
import lca.ifc.*;
import lca.LCA.materialMapping.WeightedCombinationMapper;

public class LcaCalcBlock extends VariableReferenceBlock<LCAIFCElement> {
	private String sourceVarName;
	private AreaSource area;
	private Double areaValue = null;
	private String referenceName;
	private EpdType epdType;
	private LCA lca;
	
	private boolean autoMapMaterials;
	private Map<String,String> matDefs;
	private WeightedCombinationMapper materialMapper;
	
	public LcaCalcBlock(String sourceVarName, String referenceName, AreaSource area, Map<String,String> matDefs, boolean autoMapMaterials, EPD epdType) {
		super("LCA Calculation (source " + sourceVarName + ")");
		this.sourceVarName = sourceVarName;
		this.referenceName = referenceName;
		this.area = area;
		
		this.matDefs = matDefs;
		this.autoMapMaterials = autoMapMaterials;
		
		this.epdType = translateToJavaEnum(epdType);
		lca = new LCA(this.epdType);
		
		if (autoMapMaterials) {
			this.materialMapper = new WeightedCombinationMapper(lca.getEdpConnetcor());
		}

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
		boolean inputsAreNewer = Inputs.stream().anyMatch(input -> input.GetTimeOfCalculation() > GetTimeOfCalculation());
		
		if (inputsAreNewer) {
			return true;
		}
		
		return false;
	}

	@Override
	public List<LCAIFCElement> Calculate() {
		List<VariableReferenceBlock> references = findAllBlocks(VariableReferenceBlock.class);
		
		if (references.size() > 1 || references.size() == 0) {
			//Be Sad
			return null;
		}
		
	    var sourceVar = (VariableReferenceBlock<?>)references.get(0);
	    
	    List<IfcBuildingElement> ifcElements = (List<IfcBuildingElement>) sourceVar.getOutput();
	    ArrayList<LCAIFCElement> elements = new ArrayList<>();
	    
	    for (IfcBuildingElement element : ifcElements) {
	    	
	    	IIfcQuantityCollector<IfcBuildingElement> quantCol = getQuantityCollector(element);
	    	if (quantCol == null) {
	    		continue;
	    	}
	    	
	    	LcaIfcQuantity quantity = new LcaIfcQuantity();
	    	if (quantCol.isUnitSupported(DeclaredUnitEnum.M3)) {
		    	quantity.setGrossVolume(quantCol.getQuantity(element, DeclaredUnitEnum.M3));
	    	}
	    	if (quantCol.isUnitSupported(DeclaredUnitEnum.M2)) {
		    	quantity.setGrossSideArea(quantCol.getQuantity(element, DeclaredUnitEnum.M2));
	    	}
        
	    	IIfcMaterialCollector<IfcBuildingElement> matCol = getMaterialCollector(element);
	    	String ifcMatName = matCol.getIfcMatName(element);
	    	String epdId = matDefs.get(ifcMatName);
	    	
	    	String elementName = element.getName().getDecodedValue();
	    	
			elements.add(new LCAIFCElement(epdId, elementName, element.getStepLineNumber(), quantity));
	    }

	    Double area = getArea();
        List<LCAIFCElement> lcaElements = lca.calculateLCAByElement(elements, area);
		
		return lcaElements;
	}

	private String getEpdId(String ifcMatName) {
		
		if (ifcMatName == null) {
			return null;
		}
		
		if (autoMapMaterials) {
			
			if (matDefs.containsKey(ifcMatName)) {
				return matDefs.get(ifcMatName);
			}
			
			var epdId = materialMapper.getMostSimilarEpd(ifcMatName).getEpdId();
			matDefs.put(ifcMatName, epdId);
			return epdId;
		}
		
		return matDefs.get(ifcMatName);
	}
	
	private <T extends IfcBuildingElement> IIfcQuantityCollector<T> getQuantityCollector(IfcBuildingElement element) {
		
    	if (element instanceof IfcWall) {
    		return (IIfcQuantityCollector<T>) new IfcWallQuantityCollector();
    	}
    	if (element instanceof IfcBeam) {
    		return (IIfcQuantityCollector<T>) new IfcBeamQuantityCollector();
    	}
    	
    	return null;
	}
	
	private <T extends IfcBuildingElement> IIfcMaterialCollector<T> getMaterialCollector(IfcBuildingElement element) {
		
    	if (element instanceof IfcWall) {
    		return (IIfcMaterialCollector<T>) new IfcWallMaterialCollector();
    	}
    	if (element instanceof IfcBeam) {
    		return (IIfcMaterialCollector<T>) new IfcBeamMaterialCollector();
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

	@Override
	public String generateCacheKey() {
		StringBuilder keyBuilder = new StringBuilder(Name);
		
		keyBuilder.append("source:"+sourceVarName+",");
		keyBuilder.append("reference:"+referenceName+",");
		keyBuilder.append("area:"+getAreaCacheKey()+",");
		keyBuilder.append("materialMapping:"+materialMappingString()+",");
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
	
	private String materialMappingString() {
		
		var builder = new StringBuilder();
		
		if (autoMapMaterials) {
			builder.append("auto,");
		}
		
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
