package lca.Interfaces;

import com.apstex.ifc2x3toolbox.ifc2x3.IfcBuildingElement;

public interface IIfcQuantityCollector<IfcBuildingElement> {
	
	public Double getVolume(IfcBuildingElement IfcBuildingElement);
	
	public Double getArea(IfcBuildingElement IfcBuildingElement);
}
