package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.sdu.dsl4ifc.generator.SustainLangGenerator;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.IVariableReference;

import com.apstex.ifc2x3toolbox.ifcmodel.IfcModel;

public class TypeBlock<T> extends VariableReferenceBlock<T> implements IVariableReference  {

	private Class<T> clazz;
	
	private String variableName;

	public TypeBlock(String name, String variableName, Class<T> clazz) {
		super(name);
		
		this.variableName = variableName;
		this.clazz = clazz;
	}

	@Override
	public boolean IsOutOfDate() {
		return false;
	}

	@Override
	public Stream<T> Calculate() {
		Ifc2x3ParserBlock source = findFirstBlock(Ifc2x3ParserBlock.class);
		IfcModel ifcModel = source.getOutput();
		
		if (!ifcModel.isTypeCacheEnabled())
			ifcModel.setTypeCacheEnabled(true);
		
		SustainLangGenerator.consoleOut.println("Finding type for '" + variableName + "' " + clazz.getName() + "...");
		var collection = ifcModel.getCollection(clazz);
		SustainLangGenerator.consoleOut.println("Got " + collection.size() + " entities");
		return new ArrayList<>(collection).stream();
	}

	@Override
	public String getReferenceName() {
		return variableName;
	}

}
