package lca.Interfaces;

import com.apstex.ifc2x3toolbox.ifc2x3.IfcBuildingElement;

public interface IIfcMaterialCollector<T extends IfcBuildingElement> {
	
	public String getIfcMatName(T ifcBuildingElement);
}
