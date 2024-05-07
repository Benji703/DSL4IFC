package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dhatim.fastexcel.Worksheet;
import org.sdu.dsl4ifc.generator.IExtractor;
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.table.Cell;
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.table.ColumnHeader;
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.table.Row;
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.table.Table;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;
import org.sdu.dsl4ifc.sustainLang.Reference;

public class TableOutputBlock extends Block<Table> {

	private List<AttributeReference<?>> attributeReferences;
	private Reference reference;

	public TableOutputBlock(List<AttributeReference<?>> attributeReferences, Reference reference) {
		super("Output Table (" + getColumnNamesAsString(attributeReferences) + ")");
		this.attributeReferences = attributeReferences;
		this.reference = reference;
	}

	private static String getColumnNamesAsString(List<AttributeReference<?>> attributeReferences) {
		return String.join(", ", attributeReferences.stream().map(ref -> ref.getDisplayName()).toList());
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
	public Table Calculate() {
		
		var references = findAllBlocks(VariableReferenceBlock.class);
		
		HashMap<String, VariableReferenceBlock<?>> referenceNameToInputBlock = new HashMap<>();
		
		// Determine where variables are from
		// They could implement an interface that is variable
		for (VariableReferenceBlock<?> reference : references) {
			var variableName = reference.getReferenceName();
			referenceNameToInputBlock.put(variableName, reference);
		}
		
		
		var table = new Table(reference.getName());
		this.attributeReferences.forEach(reference -> 
				table.addColumn(reference.getDisplayName(), reference)
			);

		// Get correct inputs
		// Compute variables
		var outputMap = new HashMap<String, List<?>>();
		for (AttributeReference<?> attributeReference : attributeReferences) {
			var referenceName = attributeReference.getReferenceName();
			
			var block = referenceNameToInputBlock.get(referenceName);
			List<?> entities = block.getOutput();
			
			outputMap.put(referenceName, entities);
			
		}
		
		for (var entrySet : outputMap.entrySet()) {
			String referenceName = entrySet.getKey();
			
			entrySet.getValue().forEach(entity -> {
				
				var values = new ArrayList<String>();
				
				for (var columnSource : attributeReferences) {
					if (!columnSource.getReferenceName().equals(referenceName)) {
						values.add("null");
						continue;
					}
					
					IExtractor<Object, String> extractor = (IExtractor<Object, String>) columnSource.getExtractor();
					String parameterValue = extractor.getParameterValue(entity);
					values.add(parameterValue == null ? "null" : parameterValue);
				}
				
				table.addRow(values);
				
			});
		}
	
		return table;
	}

	@Override
	public String generateCacheKey() {
		StringBuilder keyBuilder = new StringBuilder(Name);
		for (AttributeReference<?> ref : attributeReferences) {
            keyBuilder.append(ref.toString()+",");
        }
        for (Block<?> block : Inputs) {
            keyBuilder.append(block.generateCacheKey()+";");
        }
        return keyBuilder.toString();
	}

	@Override
	public void fillTraceInWorksheet(Worksheet worksheet, int startingRow) {
		var table = getOutput();
		
		int currentRow = startingRow;
		
		var headers = table.getHeaders();
		for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++) {
			ColumnHeader header = headers.get(columnIndex);
			
			worksheet.value(currentRow, columnIndex, header.headerText);
			worksheet.style(currentRow, columnIndex).bold().set();
		}
		
		var rows = table.getRows();
		for (Row row : rows) {
			currentRow++;
			
			var cells = row.cells;
			for (int columnIndex = 0; columnIndex < cells.size(); columnIndex++) {
				var cell = cells.get(columnIndex);
				
				worksheet.value(currentRow, columnIndex, cell.value);
			}
		}
	}

}
