package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.util.ArrayList;
import java.util.List;

import org.dhatim.fastexcel.Worksheet;
import org.sdu.dsl4ifc.generator.ParameterValueExtractor;
import org.sdu.dsl4ifc.generator.SustainLangGenerator;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;
import com.apstex.ifc2x3toolbox.ifc2x3.InternalAccessClass;
import com.apstex.ifc2x3toolbox.ifcmodel.IfcModel;

public class TypeBlock<T extends InternalAccessClass> extends VariableReferenceBlock<InternalAccessClass> {

	private Class<T> clazz;
	
	private String variableName;

	public TypeBlock(String variableName, Class<T> clazz) {
		super("Type ("+variableName+") " + clazz.getSimpleName());
		
		this.variableName = variableName;
		this.clazz = clazz;
	}

	@Override
	public boolean IsOutOfDate() {
		boolean inputsAreNewer = Inputs.stream().anyMatch(input -> input.GetTimeOfCalculation() > GetTimeOfCalculation());
		
		if (inputsAreNewer) {
			return true;
		}
		
		return false;
	}

	@Override
	public List<InternalAccessClass> Calculate() {
		Ifc2x3ParserBlock source = findFirstBlock(Ifc2x3ParserBlock.class);
		IfcModel ifcModel = source.getOutput();
		
		if (!ifcModel.isTypeCacheEnabled())
			ifcModel.setTypeCacheEnabled(true);
		
		SustainLangGenerator.consoleOut.println("Finding type for '" + variableName + "' " + clazz.getName() + "...");
		var collection = ifcModel.getCollection(clazz);
		SustainLangGenerator.consoleOut.println("Got " + collection.size() + " entities");
		return new ArrayList<>(collection);
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

	@Override
	public void fillTraceInWorksheet(Worksheet worksheet, int startingRow) {
		int currentRow = startingRow;
		
		var stepNumberExtractor = new ParameterValueExtractor<>("stepnumber");
		var nameExtractor = new ParameterValueExtractor<>("name");
		var ifcTypeExtractor = new ParameterValueExtractor<>("ifctype");
		
		var input = getOutput();
		var rows = input.stream().sorted((o1, o2) -> {
			
			int parameterValue1 = Integer.parseInt((String) stepNumberExtractor.getParameterValue(o1));
			int parameterValue2 = Integer.parseInt((String) stepNumberExtractor.getParameterValue(o2));
			
			return parameterValue1 - parameterValue2;
		}).toList();
		
		worksheet.value(currentRow, 0, "StepNumber");	worksheet.style(currentRow, 0).bold().set();
		worksheet.value(currentRow, 1, "Name");	worksheet.style(currentRow, 1).bold().set();
		worksheet.value(currentRow, 2, "IfcType");	worksheet.style(currentRow, 2).bold().set();
		
		for (var row : rows) {
			currentRow++;
			
			worksheet.value(currentRow, 0, Integer.parseInt((String) stepNumberExtractor.getParameterValue(row)));
			worksheet.value(currentRow, 1, (String) nameExtractor.getParameterValue(row));
			worksheet.value(currentRow, 2, (String) ifcTypeExtractor.getParameterValue(row));
		}
	}

}
