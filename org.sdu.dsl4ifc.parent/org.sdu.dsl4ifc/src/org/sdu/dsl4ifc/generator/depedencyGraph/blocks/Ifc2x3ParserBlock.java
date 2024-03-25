package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.io.File;

import org.sdu.dsl4ifc.generator.SustainLangGenerator;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;

import com.apstex.ifc2x3toolbox.ifcmodel.IfcModel;

public class Ifc2x3ParserBlock extends Block<IfcModel> {

	public Ifc2x3ParserBlock(String name) {
		super(name);
	}

	@Override
	public boolean IsOutOfDate() {
		return false;
	}

	@Override
	public IfcModel Calculate() {
		SourceBlock source = findFirstBlock(SourceBlock.class);
		File file = source.getOutput();
		
		var ifcModel = new IfcModel();
        try {
        	SustainLangGenerator.consoleOut.println("Parsing file: " + file.getAbsolutePath() + "...");
        	SustainLangGenerator.consoleOut.println("Parsing done!");
			ifcModel.readStepFile(file);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
        ifcModel.setTypeCacheEnabled(true);
        
		return ifcModel;
	}

}
