/*
 * generated by Xtext 2.30.0
 */
package org.sdu.dsl4ifc.generator

import com.apstex.ifc2x3toolbox.ifc2x3.IfcBuildingElement
import com.apstex.ifc2x3toolbox.ifc2x3.IfcDoor
import com.apstex.ifc2x3toolbox.ifc2x3.IfcMaterial
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRoot
import com.apstex.ifc2x3toolbox.ifc2x3.IfcSlab
import com.apstex.ifc2x3toolbox.ifc2x3.IfcWall
import com.apstex.ifc2x3toolbox.ifc2x3.InternalAccessClass
import java.util.ArrayList
import java.util.Collection
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedHashMap
import java.util.List
import java.util.Map
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.ui.console.ConsolePlugin
import org.eclipse.ui.console.MessageConsole
import org.eclipse.ui.console.MessageConsoleStream
import org.eclipse.xtext.generator.AbstractGenerator
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xtext.generator.IGeneratorContext
import org.sdu.dsl4ifc.generator.conditional.core.Expression
import org.sdu.dsl4ifc.generator.conditional.impls.AndOperation
import org.sdu.dsl4ifc.generator.conditional.impls.CompareParameterValueToParameterValueOperation
import org.sdu.dsl4ifc.generator.conditional.impls.CompareParameterValueToValueOperation
import org.sdu.dsl4ifc.generator.conditional.impls.CompareValueToParameterValueOperation
import org.sdu.dsl4ifc.generator.conditional.impls.CompareValueToValueOperation
import org.sdu.dsl4ifc.generator.conditional.impls.OrOperation
import org.sdu.dsl4ifc.generator.conditional.impls.TrueValue
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.AttributeReference
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.FilterBlock
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.GroupByBlock
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.Ifc2x3ParserBlock
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.LcaCalcBlock
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.LcaSummaryBlock
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.TableOutputBlock
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.TypeBlock
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block
import org.sdu.dsl4ifc.sustainLang.Attribute
import org.sdu.dsl4ifc.sustainLang.BooleanExpression
import org.sdu.dsl4ifc.sustainLang.ComparisonExpression
import org.sdu.dsl4ifc.sustainLang.Field
import org.sdu.dsl4ifc.sustainLang.FilterCommand
import org.sdu.dsl4ifc.sustainLang.Function
import org.sdu.dsl4ifc.sustainLang.IfcType
import org.sdu.dsl4ifc.sustainLang.LcaCalculation
import org.sdu.dsl4ifc.sustainLang.MatDef
import org.sdu.dsl4ifc.sustainLang.OutputCommand
import org.sdu.dsl4ifc.sustainLang.Reference
import org.sdu.dsl4ifc.sustainLang.SourceCommand
import org.sdu.dsl4ifc.sustainLang.Statement
import org.sdu.dsl4ifc.sustainLang.TransformationCommand
import org.sdu.dsl4ifc.sustainLang.Value
import org.sdu.dsl4ifc.sustainLang.OutputArgument

class SustainLangGenerator extends AbstractGenerator {
	
	public static MessageConsoleStream consoleOut = null;
	FilterBlockCatalog catalog = new FilterBlockCatalog;

	override void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {
		
		val myConsole = findConsole("SusLang");
		consoleOut = myConsole.newMessageStream();
		consoleOut.println("SusLang Console");
		
		val statements = resource.allContents.filter(Statement);
		
		catalog.setupNewRun
		
		statements.forEach[statement | {
			
				try {
					val timeStart = System.currentTimeMillis()
					System.out.println("[CREATING GRAPH]")
					val graphs = constructGraph(statement, resource)
					System.out.println("[EXECUTING]")
					val outputs = graphs.map[graph | graph.output]
					outputs.forEach[output | consoleOut.println(output.toString)]
					val timeMsg = '''Done [«System.currentTimeMillis-timeStart» ms]'''
					consoleOut.println(timeMsg)
					System.out.println(timeMsg)
				} catch (Exception e) {
					consoleOut.println("Error during the execution of the query!")
					throw e;
				}
				
			}]
	}
		
	def List<Block<?>> constructGraph(Statement statement, Resource resource) {
		
		val outputs = statement.outputs
		
		return outputs.map[output | {
			val tableOutputBlock = output.createBlock(statement, resource)
			val checkedBlock = searchAndReplaceNodes(tableOutputBlock)
			return checkedBlock
		}]
	}
	
	def Block<?> searchAndReplaceNodes(Block<?> block) {
		
		if (catalog.blockExists(block)) {
			var oldBlock = catalog.getBlock(block)
			catalog.registerBlock(oldBlock)
			
			return oldBlock
		}
		
		block.Inputs = new ArrayList(block.Inputs.map[b | searchAndReplaceNodes(b)])
		
		return block
	}
	
	def dispatch Block<?> createBlock(OutputCommand output, Statement statement, Resource resource) {
		val tableOutputBlock = new TableOutputBlock("Output Table", output.toAttributeReferences, output.reference)
		
		// Create necesarry inputs		
		addInputsToTableOutput(statement, output.reference, resource, tableOutputBlock)
		
		return catalog.ensureExistingIsUsed(tableOutputBlock)
	}
	
	protected def void addInputsToTableOutput(Statement statement, Reference reference, Resource resource, TableOutputBlock tableOutputBlock) {
		
		// Transformations
		val transformationsForAttribute = statement.transforms.filter[transformation | reference.name === transformation.reference.name]
		if (!transformationsForAttribute.isEmpty) { // References a transformation
		
			val transform = transformationsForAttribute.head
			
			val transformBlock = catalog.ensureExistingIsUsed(transform.createBlock(statement, resource))
			tableOutputBlock.AddInput(transformBlock)
			return
		}
		
		// Or add other inputs
		addFilterOrTypeInput(statement, reference, resource, tableOutputBlock)
	}
	
	private def void addFilterOrTypeInput(Statement statement, Reference reference, Resource resource, Block<?> block) {
		// Filters
		val filtersForAttribute = statement.filters.filter[filter | reference.name === filter.type.name]
		if (!filtersForAttribute.isEmpty) { // References a filter
		
			val filter = filtersForAttribute.head
			if (filter.condition === null) {
				val type = filtersForAttribute.head.type
				val typeBlock = catalog.ensureExistingIsUsed(type.createBlock(statement, resource))
				block.AddInput(typeBlock)
				return
			}
			
			val filterBlock = catalog.ensureExistingIsUsed(filter.createBlock(statement, resource))
			block.AddInput(filterBlock)
			return
		}
		
		// Calculations
		val lcaCalcsForAttribute = statement.^do.calculation.filter(LcaCalculation).filter[lca | 
				lca.lcaEntitiesReference === null ? false : reference.name === lca.lcaEntitiesReference.name
			]
		if (!lcaCalcsForAttribute.isEmpty) { // References a lca calculation
			val calc = lcaCalcsForAttribute.head
			val calcBlock = calc.createLcaCalculationBlock(statement, resource)
			block.AddInput(catalog.ensureExistingIsUsed(calcBlock))
			return
		}
		val lcaSummariesForAttribute = statement.^do.calculation.filter(LcaCalculation).filter[lca | reference.name === lca.summaryReference.name]
		if (!lcaSummariesForAttribute.isEmpty) { // References a lca calculation
			val calc = lcaSummariesForAttribute.head
			val calcBlock = calc.createLcaSummaryBlock(statement, resource)
			block.AddInput(catalog.ensureExistingIsUsed(calcBlock))
			return
		}
	}
	
	def dispatch Block<?> createBlock(FilterCommand filter, Statement statement, Resource resource) {
		val filterBlock = new FilterBlock('''Filter: «filter.type.name»''', filter.type.name, filter.toExpression)
		
		// Create necesarry inputs
		val typeBlock = filter.type.createBlock(statement, resource)
		filterBlock.AddInput(typeBlock)
		
		val references = new HashSet()
		filter.condition.addAllVariableReferences(references)
		
		references.filter[ref | ref.name !== filter.type.name].forEach[reference | {
			val block = reference.createBlock(statement, resource)
			filterBlock.AddInput(block)
		}]
		
		return catalog.ensureExistingIsUsed(filterBlock)
	}
	
	def dispatch Block<?> createBlock(TransformationCommand transformation, Statement statement, Resource resource) {
		val name = '''Group «transformation.reference.name» By («FOR attribute : transformation.attributes SEPARATOR ', '»«attribute»«ENDFOR»)'''
		val transformBlock = new GroupByBlock(name, transformation.reference, transformation.toAttributeReferences);
		
		// Create necesarry inputs
		addFilterOrTypeInput(statement, transformation.reference, resource, transformBlock)
		
		return transformBlock;
	}
	
	def dispatch Block<?> createBlock(Reference reference, Statement statement, Resource resource) {
		val typeBlock = new TypeBlock('''Type: "«reference.name»" «reference.ifcType»''', reference.name, reference.ifcType.toIfcType)
		
		// Create necesarry inputs
		val parserBlock = statement.source.createBlock(statement, resource)
		typeBlock.AddInput(parserBlock)
		
		return catalog.ensureExistingIsUsed(typeBlock)
	}
	
	def Block<?> createLcaSummaryBlock(LcaCalculation cal, Statement statement, Resource resource) {
		
		val lcaPar = cal.lcaParams;
		
		val lcaSummaryBlock = new LcaSummaryBlock('''LCA Summary (source: «cal.source.name»)''', cal.source.name, cal.summaryReference.name, lcaPar.areaHeat, lcaPar.b6);
		
		// Create necesarry inputs
		val lcaCalcBlock = cal.createLcaCalculationBlock(statement, resource)
		
		lcaSummaryBlock.AddInput(lcaCalcBlock)
		
		return catalog.ensureExistingIsUsed(lcaSummaryBlock)
	}
	
	def Block<?> createLcaCalculationBlock(LcaCalculation cal, Statement statement, Resource resource) {
		
		val lcaPar = cal.lcaParams;
		val matDefs = cal.matDefs
		
	 	var matDefMap = new HashMap<String,String>
		
		for (MatDef matDef : matDefs) {
			matDefMap.put(matDef.ifcMat,matDef.epdMatId);
		}
		
		val referenceName = cal.lcaEntitiesReference === null ? "lcacalcblockentities" : cal.lcaEntitiesReference.name
		val lcaCalcBlock = new LcaCalcBlock('''LCA Calculation (source: «cal.source.name»)''', cal.source.name, referenceName, lcaPar.area, matDefMap);
		
		// Create necesarry inputs
		// Can be types or filters
		val inputBlock = createInputFromReference(statement, resource, cal.source)
		lcaCalcBlock.AddInput(catalog.ensureExistingIsUsed(inputBlock))
		
		return catalog.ensureExistingIsUsed(lcaCalcBlock)
	}
	
	def Block<?> createInputFromReference(Statement statement, Resource resource, Reference reference) {
		val filtersForAttribute = statement.filters.filter[filter | reference.name === filter.type.name]
		if (!filtersForAttribute.isEmpty) { // References a 'filter'
			val filter = filtersForAttribute.head
		
			if (filter.condition === null) {
				val type = filtersForAttribute.head.type
				return catalog.ensureExistingIsUsed(type.createBlock(statement, resource))
				
			}
			
			return catalog.ensureExistingIsUsed(filter.createBlock(statement, resource))
		}
	}
	
	def dispatch Block<?> createBlock(SourceCommand source, Statement statement, Resource resource) {
		val parserBlock = new Ifc2x3ParserBlock("IFC Parser 2x3", source.path, resource)
		
		return catalog.ensureExistingIsUsed(parserBlock)
	}
	
	def void addAllVariableReferences(org.sdu.dsl4ifc.sustainLang.Expression expression, Collection<Reference> references) {
		
		switch (expression) {
			BooleanExpression: {
				expression.left.addAllVariableReferences(references)
				expression.right.addAllVariableReferences(references)
			}
			ComparisonExpression: {
				expression.left.addAllVariableReferences(references)
				expression.right.addAllVariableReferences(references)
			}
			Attribute: {
				references.add(expression.reference)
			}
		}
	}
	 
	def List<AttributeReference<?>> toAttributeReferences(OutputCommand command) {
		command.columns.map[outputParameter | {
			switch (outputParameter) {
				Field:
					new AttributeReference(command.reference.name, outputParameter.fieldName, new ParameterValueExtractor(outputParameter.fieldName), outputParameter.toDisplayName)
				Function: {
					val aggregateValueExtractor = new AggregateValueExtractor<Object>(outputParameter.field.fieldName, outputParameter.functionType)
					return new AttributeReference(command.reference.name, outputParameter.field.fieldName, aggregateValueExtractor, outputParameter.toDisplayName)
				}
			}
			
		}]
	}
	
	def List<AttributeReference<?>> toAttributeReferences(TransformationCommand command) {
		command.attributes.map[field |
			new AttributeReference(command.reference.name, field.fieldName, new ParameterValueExtractor(field.fieldName), field.toDisplayName)
		]
	}
	
	def String toDisplayName(OutputArgument outputArgument) {
		switch (outputArgument) {
			Field:
				'''«outputArgument.fieldName»'''
			Function: {
				'''«outputArgument.functionType»(«outputArgument.field.fieldName»)'''
			}
			default:
				throw new Exception("Output argument not set")
		}
	}
		
	def Expression<InternalAccessClass> toExpression(FilterCommand command) {
		val expression = command.condition.toBlockExpression(command.type);
		return expression
	}
		
	def dispatch Expression<InternalAccessClass> toBlockExpression(org.sdu.dsl4ifc.sustainLang.Expression expression, Reference variableReference) {
		throw new Exception("Cannot convert this expression to block expression: " + expression.class.name)
	}

	def dispatch Expression<InternalAccessClass> toBlockExpression(BooleanExpression expression, Reference variableReference) {
		
		switch (expression.operator) {
			case AND:
				return new AndOperation(expression.left.toBlockExpression(variableReference), expression.right.toBlockExpression(variableReference))
			case OR:
				return new OrOperation(expression.left.toBlockExpression(variableReference), expression.right.toBlockExpression(variableReference))
			default: throw new Exception("BooleanExpression operator has not been implemented")
		}
	}
	
	// Could be an "exists" as well
	val defaultValue = new TrueValue<InternalAccessClass>()
	
	def dispatch Expression<InternalAccessClass> toBlockExpression(ComparisonExpression expression, Reference variableReference) {
		var left = expression.left
		var right = expression.right
		
		// Ensure active variable is on the left
		if (right instanceof Attribute) {
			
			if (right.reference.name === variableReference.name) {	// Rotate if primary is on the right side
				val oldRight = right
				right = left
				left = oldRight
			}
		}
		
		
		if (left instanceof Value && right instanceof Value) {				// 1 = 1
			val rightValue = right as Value
			val leftValue = left as Value
			
			return new CompareValueToValueOperation(leftValue.stringValue, rightValue.stringValue, expression.operator)
		}
		else if (left instanceof Attribute && right instanceof Value) {		// p.name = "name"	// p is primary
			val leftAttribute = left as Attribute
			val rightValue = right as Value
			
			// Should always be true
			if (leftAttribute.reference.name !== variableReference.name) {
				return defaultValue;
			}
			
			return new CompareParameterValueToValueOperation(rightValue.stringValue, leftAttribute.toExtractor, expression.operator)
		}
		else if (left instanceof Value && right instanceof Attribute) {		// "name" = s.name	// s is secondary
			val rightAttribute = right as Attribute
			val leftValue = left as Value
			
			// Should always be true
			if (rightAttribute.reference.name !== variableReference.name) {
				return defaultValue;
			}
			
			return new CompareValueToParameterValueOperation(leftValue.stringValue, rightAttribute.reference.name, rightAttribute.toExtractor, expression.operator)
		}
		else if (left instanceof Attribute && right instanceof Attribute) {	// p.name = s.name
			val leftAttribute = left as Attribute
			val rightAttribute = right as Attribute
			
			if (leftAttribute.reference.name !== variableReference.name && rightAttribute.reference.name !== variableReference.name) {
				return defaultValue;
			}
			
			val rightVariableName = rightAttribute.reference.name
			return new CompareParameterValueToParameterValueOperation(rightVariableName, leftAttribute.toExtractor, rightAttribute.toExtractor, expression.operator)
		}
	}
		
	def ParameterValueExtractor<?, ?> toExtractor(Attribute attribute) {
		return new ParameterValueExtractor(attribute.field.fieldName)
	}
	
	def toIfcType(IfcType type) {
		switch (type) {
			case IFC_WALL: {
				return IfcWall
			}
			case IFC_DOOR: {
				return IfcDoor
			}
			case IFC_BUILDING_ELEMENT: {
				return IfcBuildingElement
			}
			case IFC_SLAB: {
				return IfcSlab
			}
			case IFC_ROOT: {
				return IfcRoot
			}
			case IFC_MATERIAL: {
				return IfcMaterial
			}
			default: {
				
			}
		}
	}
	
	def MessageConsole findConsole(String name) {
	    val plugin = ConsolePlugin.getDefault()
	    val conMan = plugin.getConsoleManager()
	    val existing = conMan.getConsoles()
	    for (console : existing) {
	        if (name.equals(console.getName())) {
	            return console as MessageConsole
	        }
	    }
	    // no console found, so create a new one
	    val myConsole = new MessageConsole(name, null)
	    val consoles = #[myConsole];
	    conMan.addConsoles(consoles);
	    return myConsole;
	}
	
	def static <T, K> List<T> distinctBy(List<T> list, java.util.function.Function<T, K> keyExtractor) {
        val Map<K, T> map = new LinkedHashMap();
        for (T element : list) {
            map.putIfAbsent(keyExtractor.apply(element), element);
        }
        return new ArrayList(map.values());
    }
	
}
