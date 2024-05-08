package lca.ifc;

import com.apstex.ifc2x3toolbox.ifc2x3.IfcElementQuantity;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcPhysicalQuantity;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcQuantityArea;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcQuantityVolume;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelDefines;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelDefinesByProperties;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcWall;
import com.apstex.step.core.SET;

import lca.DomainClasses.Enums.DeclaredUnitEnum;
import lca.Interfaces.IIfcQuantityCollector;
import lca.LCA.LcaIfcQuantity;

public class IfcWallQuantityCollector implements IIfcQuantityCollector<IfcWall> {

	@Override
	public Double getQuantity(IfcWall ifcWall, DeclaredUnitEnum unit) {
		Double quantity = null;
		var invSet = ifcWall.getIsDefinedBy_Inverse();
		if (invSet == null) {
			return null;
		}
		
		IfcElementQuantity elementQuant = getElementQuant(invSet);
		if (elementQuant == null) {
			return null;
		}
		
		switch (unit) {
		case M3:
			quantity = getGrossVolume(elementQuant);
			break;
		case M2:
			quantity = getGrossArea(elementQuant);
			break;
		default:
			break;
		}
		
		return quantity;
	}

	private IfcElementQuantity getElementQuant(SET<IfcRelDefines> invSet) {
		IfcElementQuantity elementQuant = null;
		
		for (IfcRelDefines iRel : invSet) {
			
			if (!(iRel instanceof IfcRelDefinesByProperties)) {
				continue;
			}
			
			var iRelProp = (IfcRelDefinesByProperties)iRel;
			
			if (!(iRelProp.getRelatingPropertyDefinition() instanceof IfcElementQuantity)) {
				continue;
			}
			
			elementQuant = (IfcElementQuantity)iRelProp.getRelatingPropertyDefinition();
		}
		return elementQuant;
	}
	
	private Double getGrossVolume(IfcElementQuantity elementQuant) {
		double grossVolume = 0;
		
		for (IfcPhysicalQuantity q : elementQuant.getQuantities()) {
			if (q instanceof IfcQuantityVolume && q.getName().getDecodedValue().equals("GrossVolume")) {
				grossVolume = ((IfcQuantityVolume)q).getVolumeValue().getValue();
			}
		}
		
		return grossVolume;
	}
	
	private Double getGrossArea(IfcElementQuantity elementQuant) {
		double grossArea = 0;
		
		for (IfcPhysicalQuantity q : elementQuant.getQuantities()) {
			if (q instanceof IfcQuantityArea && q.getName().getDecodedValue().equals("GrossArea")) {
				grossArea = ((IfcQuantityArea)q).getAreaValue().getValue();
			}
		}
		return grossArea;
	}



}
