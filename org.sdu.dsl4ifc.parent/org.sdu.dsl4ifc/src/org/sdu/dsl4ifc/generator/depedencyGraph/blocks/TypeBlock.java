package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.util.stream.Stream;

import org.sdu.dsl4ifc.generator.SustainLangGenerator;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;

import com.apstex.ifc2x3toolbox.ifcmodel.IfcModel;

public class TypeBlock<T> extends Block<Stream<T>> {

	private Class<T> clazz;

	public TypeBlock(String name, Class<T> clazz) {
		super(name);
		this.clazz = clazz;
		
	}

	@Override
	public boolean IsOutOfDate() {
		return false;
	}

	@Override
	public Stream<T> Calculate() {
		Ifc2x3ParserBlock source = findBlock(Ifc2x3ParserBlock.class);
		IfcModel ifcModel = source.getOutput();
		
		if (!ifcModel.isTypeCacheEnabled())
			ifcModel.setTypeCacheEnabled(true);
		
		SustainLangGenerator.consoleOut.println("Finding type " + clazz.getName() + "...");
		var collection = ifcModel.getCollection(clazz);
		SustainLangGenerator.consoleOut.println("Got " + collection.size() + " entities");
		return collection.stream();
	}

}
