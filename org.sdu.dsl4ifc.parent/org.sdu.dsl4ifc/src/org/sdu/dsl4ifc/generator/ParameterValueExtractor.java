package org.sdu.dsl4ifc.generator;

import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.GroupedRows;

import com.apstex.ifc2x3toolbox.ifc2x3.IfcLabel;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcMaterial;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRoot;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcWall;
import com.apstex.step.core.ClassInterface;

import lca.LCA.LCAIFCElement;
import lca.LCA.LCAResult;

// Make one for each ifcType
public class ParameterValueExtractor<T, U> implements IExtractor<T, U> { 

	private String parameterName;
	public String getParameterName() {
		return parameterName;
	}

	// T : Input type (IfcWall fx), U : Output parameter value type (String fx)
	
	public ParameterValueExtractor(String parameterName) {
		this.parameterName = parameterName.toLowerCase();
		
	}
	
	@Override
	public U getParameterValue(T item) {
		
		if (item instanceof IfcWall asIfcWall) {
			
			switch (parameterName) {
			default:
				break;
			}
			
		}
		if (item instanceof ClassInterface asClassInterface) {
			switch (parameterName) {
			case "stepnumber":
				return (U) (""+asClassInterface.getStepLineNumber());
			case "ifctype":
				return (U) (asClassInterface.getClass().getSimpleName());
			default:
				break;
			}
		}
		if (item instanceof IfcRoot asIfcRoot) {
			switch (parameterName) {
			case "name":
				IfcLabel name = asIfcRoot.getName();
				return name == null ? null : (U) name.getDecodedValue();
			default:
				break;
			}
		}
		
		if (item instanceof IfcMaterial asIfcMaterial) {
			switch (parameterName) {
			case "name":
				IfcLabel name = asIfcMaterial.getName();
				return name == null ? null : (U) name.getDecodedValue();

			case "stepnumber":
				return (U) (""+asIfcMaterial.getStepLineNumber());
			default:
				break;
			}
		}
		
		if (item instanceof LCAResult asLcaResult) {
			switch (parameterName) {
			case "result":
				return (U) (asLcaResult.getLcaResult()+" kg. CO₂ ævk. / m² / år");

			default:
				break;
			}
		}
		
		if (item instanceof LCAIFCElement asLcaElement) {
			switch (parameterName) {
			case "ifcname":
				return (U) asLcaElement.getIfcName();
			case "epdid":
				return (U) asLcaElement.getEpdId();
			case "a":
				return (U) (asLcaElement.getAResult() + " kg. CO₂ ævk. / m² / år");
			case "c3":
				return (U) (asLcaElement.getC3Result() + " kg. CO₂ ævk. / m² / år");
			case "c4":
				return (U) (asLcaElement.getC4Result() + " kg. CO₂ ævk. / m² / år");
			case "d":
				return (U) (asLcaElement.getdResult() + " kg. CO₂ ævk. / m² / år");
			case "result":
				return (U) (asLcaElement.getLcaVal() + " kg. CO₂ ævk. / m² / år");
			case "quantity":
				return (U) (asLcaElement.getQuantity() + " m³");
			case "lifetime":
				return (U) (asLcaElement.getLifeTime() + " year(s)");

			default:
				break;
			}
		}
		
		if (item instanceof GroupedRows<?> asGroupedRow) {
			
			if (!asGroupedRow.groupedFields.contains(parameterName)) {
				return null;
			}
			
			Object firstObject = asGroupedRow.elements.get(0);
			return getParameterValue((T) firstObject);
		}
		
		return null;
		
	}
	
	@Override
	public String toString() {
		return parameterName;
	}
}
