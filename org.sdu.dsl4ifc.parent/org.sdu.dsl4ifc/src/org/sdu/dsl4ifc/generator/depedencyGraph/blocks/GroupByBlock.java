package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.dhatim.fastexcel.Worksheet;
import org.sdu.dsl4ifc.generator.ParameterValueExtractor;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;
import org.sdu.dsl4ifc.sustainLang.Reference;

public class GroupByBlock<InputType, FieldType> extends VariableReferenceBlock<GroupedRows<InputType>> {
	
	private Reference reference;
	private List<AttributeReference<InputType>> attributeReferences;

	public GroupByBlock(Reference reference, List<AttributeReference<InputType>> attributeReferences)  {
		super("Group (" + reference.getName() + ") By (" + String.join(", ", attributeReferences.stream().map(ref -> ref.getAttributeName()).toList())+")");
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
		
		var<VariableReferenceBlock> references = findAllBlocks(VariableReferenceBlock.class);
		
		if (references.size() > 1) {
			System.out.println("Group by has more than one input...");
		}
		
		VariableReferenceBlock<InputType> reference = references.get(0);
		
		List<InputType> elements = reference.getOutput();
		
		var map = elements.stream().collect(Collectors.toMap(
				p -> {
					List<String> values = attributeReferences.stream().map(t -> t.getExtractor().getParameterValue(p)).toList();
					return String.join(",", values);
				}, 
				t -> { 
					List<String> groupedFieldNames = attributeReferences.stream().map(b -> b.getAttributeName()).toList();
					return new Group<InputType>(groupedFieldNames).add(t); 
				}, 
				(l, u) -> l.merge(u.getElements())
			));
		
		var groupedRows = new ArrayList<GroupedRows<InputType>>();
		for (Entry<String, Group<InputType>> entry : map.entrySet()) {
			Group<InputType> val = entry.getValue();
			groupedRows.add(new GroupedRows<InputType>(val.getElements(), val.getGroupedFieldNames()));
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

	@Override
	public void fillTraceInWorksheet(Worksheet worksheet, int startingRow) {
		int currentRow = startingRow;
		
		// De værdier man grupperer på
		// ID (hvis internalclass -> step number, hvis lcaelement -> stepnumber, hvis lcaresult -> nothing)
		
		var stepNumberExtractor = new ParameterValueExtractor<Object, String>("stepnumber");
		
		worksheet.value(currentRow, 0, "StepNumber");	worksheet.style(currentRow, 0).bold().set();
		for (int currentColumn = 0; currentColumn < attributeReferences.size(); currentColumn++) {
			var ref = attributeReferences.get(currentColumn);
			worksheet.value(currentRow, currentColumn+1, ref.getDisplayName());	worksheet.style(currentRow, currentColumn+1).bold().set();
		}
		currentRow++;

		var groups = getOutput();
		
		for (var group : groups) {
			
			worksheet.value(currentRow, 0, "StepNumber");	worksheet.style(currentRow, 0).bold().italic().set();
			for (int currentColumn = 0; currentColumn < attributeReferences.size(); currentColumn++) {
				var ref = attributeReferences.get(currentColumn);
				String groupValue = ref.getExtractor().getParameterValue(group.elements.get(0));
				worksheet.value(currentRow, currentColumn+1, groupValue);	worksheet.style(currentRow, currentColumn+1).bold().italic().set();
			}
			
			currentRow++;
			
			var rows = group.elements.stream().sorted((o1, o2) -> {
				int stepNumber1 = Integer.parseInt((String)stepNumberExtractor.getParameterValue(o1));
				int stepNumber2 = Integer.parseInt((String)stepNumberExtractor.getParameterValue(o2));
				return stepNumber1 - stepNumber2;
				
			}).toList();
			for (var row : rows) {
				worksheet.value(currentRow, 0, Integer.parseInt(stepNumberExtractor.getParameterValue(row)));
				
				for (int currentColumn = 0; currentColumn < attributeReferences.size(); currentColumn++) {
					var ref = attributeReferences.get(currentColumn);
					worksheet.value(currentRow, currentColumn+1, ref.getExtractor().getParameterValue(row));
				}
				currentRow++;
			}
			
		}
	}

}

class Group<T> {
	
	private List<T> elements = new ArrayList<>();
	private List<String> groupedFieldNames;
	
	public Group(List<String> groupedFieldNames) {
		this.groupedFieldNames = groupedFieldNames;
	}
	
	public Group<T> merge(List<T> elements) {
		this.elements.addAll(elements);
		return this;
	}
	
	public Group<T> add(T element) {
		elements.add(element);
		return this;
	}

	public List<String> getGroupedFieldNames() {
		return groupedFieldNames;
	}

	public List<T> getElements() {
		return elements;
	}
}
