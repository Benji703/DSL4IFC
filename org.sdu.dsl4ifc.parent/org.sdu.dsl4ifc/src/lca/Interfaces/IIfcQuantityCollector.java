package lca.Interfaces;

import com.apstex.ifc2x3toolbox.ifc2x3.IfcBuildingElement;

import lca.DomainClasses.Enums.DeclaredUnitEnum;
import lca.LCA.LcaIfcQuantity;

public interface IIfcQuantityCollector<IfcBuildingElement> {
	
	public Double getQuantity(IfcBuildingElement IfcBuildingElement, DeclaredUnitEnum unit);
}
