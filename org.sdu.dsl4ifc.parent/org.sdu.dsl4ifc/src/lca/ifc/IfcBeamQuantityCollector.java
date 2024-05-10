package lca.ifc;

import com.apstex.ifc2x3toolbox.ifc2x3.IfcBeam;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcElementQuantity;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcPhysicalQuantity;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcQuantityArea;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcQuantityVolume;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelDefines;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelDefinesByProperties;
import com.apstex.step.core.SET;

import lca.DomainClasses.Enums.DeclaredUnitEnum;
import lca.Interfaces.IIfcQuantityCollector;

public class IfcBeamQuantityCollector implements IIfcQuantityCollector<IfcBeam> {

	@Override
	public Double getQuantity(IfcBeam element, DeclaredUnitEnum unit) {
		Double quantity = null;
		var invSet = element.getIsDefinedBy_Inverse();
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
		default:
			break;
		}
		
		return quantity;
	}

	@Override
	public boolean isUnitSupported(DeclaredUnitEnum unit) {
		switch (unit) {
		case M3:
			return true;
		default:
			return false;
		}
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
		double volume = 0;
		
		for (IfcPhysicalQuantity q : elementQuant.getQuantities()) {
			if (q instanceof IfcQuantityVolume && q.getName().getDecodedValue().equals("GrossVolume")) {
				volume = ((IfcQuantityVolume)q).getVolumeValue().getValue();
				break;
			}
			if (q instanceof IfcQuantityVolume && q.getName().getDecodedValue().equals("NetVolume")) {
				volume = ((IfcQuantityVolume)q).getVolumeValue().getValue();
			}
		}
		
		return volume;
	}

}
