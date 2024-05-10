package lca.ifc;

import com.apstex.ifc2x3toolbox.ifc2x3.IfcBuildingElement;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcMaterialLayerSetUsage;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelAssociates;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelAssociatesMaterial;
import com.apstex.step.core.SET;

import lca.Interfaces.IIfcMaterialCollector;

public class IfcWallMaterialCollector implements IIfcMaterialCollector {

	@Override
	public String getIfcMatName(IfcBuildingElement element) {
		
		SET<IfcRelAssociates> associations = element.getHasAssociations_Inverse();
    	if (associations == null)
    		return null;
		
		return getIfcMatName(associations);
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

}
