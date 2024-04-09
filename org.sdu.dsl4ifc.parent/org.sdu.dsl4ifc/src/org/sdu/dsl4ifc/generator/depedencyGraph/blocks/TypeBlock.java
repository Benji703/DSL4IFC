package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.sdu.dsl4ifc.generator.SustainLangGenerator;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.IVariableReference;

import com.apstex.ifc2x3toolbox.ifc2x3.IfcRoot;
import com.apstex.ifc2x3toolbox.ifcmodel.IfcModel;

public class TypeBlock<T extends IfcRoot> extends VariableReferenceBlock<IfcRoot> implements IVariableReference  {

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
	public Stream<IfcRoot> Calculate() {
		Ifc2x3ParserBlock source = findFirstBlock(Ifc2x3ParserBlock.class);
		IfcModel ifcModel = source.getOutput();
		
		if (!ifcModel.isTypeCacheEnabled())
			ifcModel.setTypeCacheEnabled(true);
		
		SustainLangGenerator.consoleOut.println("Finding type for '" + variableName + "' " + clazz.getName() + "...");
		var collection = ifcModel.getCollection(clazz);
		SustainLangGenerator.consoleOut.println("Got " + collection.size() + " entities");
		return (Stream<IfcRoot>) new ArrayList<>(collection).stream();
	}

	@Override
	public String getReferenceName() {
		return variableName;
	}
	
	@Override
	public String generateCacheKey() {
		StringBuilder keyBuilder = new StringBuilder(Name);
		
		keyBuilder.append(variableName+",");
		keyBuilder.append(clazz.toString()+",");
		
        for (Block<?> block : Inputs) {
            keyBuilder.append(block.generateCacheKey()+";");
        }
        return keyBuilder.toString();
	}

}
