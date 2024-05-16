package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.util.Collection;
import java.util.stream.Collectors;

import org.sdu.dsl4ifc.generator.SustainLangGenerator;

import com.apstex.ifc2x3toolbox.ifc2x3.IfcBuilding;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcElementQuantity;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcPhysicalQuantity;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcQuantityArea;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelDefines;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelDefinesByProperties;
import com.apstex.step.core.SET;

public class AreaExtractor {
	
	public static Double getSomeFloorAreaSum(Collection<IfcBuilding> ifcBuldings) {
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
}
