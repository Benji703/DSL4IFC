package lca.ifc;

import com.apstex.ifc2x3toolbox.ifc2x3.IfcBeam;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcMaterial;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcMaterialLayerSetUsage;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelAssociates;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRelAssociatesMaterial;
import com.apstex.step.core.SET;

import lca.Interfaces.IIfcMaterialCollector;

public class IfcBeamMaterialCollector implements IIfcMaterialCollector<IfcBeam> {

	@Override
	public String getIfcMatName(IfcBeam element) {
		
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
			
			if (!(relAssMat.getRelatingMaterial() instanceof IfcMaterial)) {
				continue;
			}
			
			String matName = ((IfcMaterial)relAssMat.getRelatingMaterial()).getName().getDecodedValue();
			
	
			return matName;
		}
		
		return null;
	}
	
}
