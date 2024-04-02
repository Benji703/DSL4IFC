package org.sdu.dsl4ifc.generator;

import com.apstex.ifc2x3toolbox.ifc2x3.IfcLabel;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRoot;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcWall;

// Make one for each ifcType
public class ParameterValueExtractor<T, U> {

	private String parameterName;

	// T : Input type (IfcWall fx), U : Output parameter value type (String fx)
	
	public ParameterValueExtractor(String parameterName) {
		this.parameterName = parameterName.toLowerCase();
		
	}
	
	public U getParameterValue(T item) {
		
		if (item instanceof IfcWall asIfcWall) {
			
			switch (parameterName) {
			default:
				break;
			}
			
		}
		if (item instanceof IfcRoot asIfcRoot) {
			switch (parameterName) {
			case "name":
				IfcLabel name = asIfcRoot.getName();
				return name == null ? null : (U) name.getDecodedValue();

			case "stepnumber":
				return (U) (""+asIfcRoot.getStepLineNumber());
			default:
				break;
			}
		}
		return null;
		
	}

	ParameterValueExtractor() {
	  throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}
}
