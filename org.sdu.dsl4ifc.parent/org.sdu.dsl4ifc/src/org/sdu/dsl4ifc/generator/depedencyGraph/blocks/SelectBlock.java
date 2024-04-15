package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.table.Table;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;

public class SelectBlock extends Block<Table> {

	private List<AttributeReference<Object, String>> attributeReferences;

	public SelectBlock(String name, List<AttributeReference<Object,String>> attributeReferences) {
		super(name);
		this.attributeReferences = attributeReferences;
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
		
		
		var table = new Table();
		this.attributeReferences.forEach(reference -> 
				table.addColumn(reference.getReferenceName() + "." + reference.getAttributeName(), reference)
			);

		// Get correct inputs
		// Compute variables
		var outputMap = new HashMap<String, List<?>>();
		for (AttributeReference<?, String> attributeReference : attributeReferences) {
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
					
					var extractor = columnSource.getExtractor();
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
		for (AttributeReference<?,?> ref : attributeReferences) {
            keyBuilder.append(ref.getReferenceName()+"."+ref.getAttributeName()+",");
        }
        for (Block<?> block : Inputs) {
            keyBuilder.append(block.generateCacheKey()+";");
        }
        return keyBuilder.toString();
	}

}
