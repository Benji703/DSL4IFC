package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.sdu.dsl4ifc.generator.ParameterValueExtractor;
import org.sdu.dsl4ifc.generator.SustainLangGenerator;
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.table.Table;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.IVariableReference;
import org.sdu.dsl4ifc.sustainLang.Reference;

public class GroupByBlock<InputType, FieldType> extends VariableReferenceBlock<GroupedRows<InputType>> {
	
	private Reference reference;
	private List<AttributeReference<InputType>> attributeReferences;

	public GroupByBlock(String name, Reference reference, List<AttributeReference<InputType>> attributeReferences)  {
		super(name);
		this.reference = reference;
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
	public List<GroupedRows<InputType>> Calculate() {
		
		var references = findAllBlocks(VariableReferenceBlock.class);
		
		if (references.size() > 1) {
			System.out.println("Group by has more than one input...");
		}
		
		VariableReferenceBlock<InputType> reference = references.get(0);
		
		List<InputType> elements = reference.getOutput();
		
		var map = elements.stream().collect(Collectors.groupingBy(p -> {
			List<String> values = attributeReferences.stream().map(t -> t.getExtractor().getParameterValue(p)).toList();
			return String.join(",", values);
		}));
		
		var groupedRows = new ArrayList<GroupedRows<InputType>>();
		for (Entry<String, List<InputType>> entry : map.entrySet()) {
			List<InputType> val = entry.getValue();
			groupedRows.add(new GroupedRows<>(val));
		}
		
		return groupedRows;
	}

	@Override
	public String generateCacheKey() {
		StringBuilder keyBuilder = new StringBuilder(Name);
		keyBuilder.append("reference:" + reference.getName() + ",");
		for (AttributeReference<?> ref : attributeReferences) {
            keyBuilder.append(ref.getReferenceName()+"."+ref.getAttributeName()+",");
        }
        for (Block<?> block : Inputs) {
            keyBuilder.append(block.generateCacheKey()+";");
        }
        return keyBuilder.toString();
	}

	@Override
	public String getReferenceName() {
		return reference.getName();
	}

}
