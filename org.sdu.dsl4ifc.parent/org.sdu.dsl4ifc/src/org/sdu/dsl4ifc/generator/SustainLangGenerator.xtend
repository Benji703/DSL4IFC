/*
 * generated by Xtext 2.30.0
 */
package org.sdu.dsl4ifc.generator

import com.apstex.ifc2x3toolbox.ifc2x3.IfcDoor
import com.apstex.ifc2x3toolbox.ifc2x3.IfcMaterial
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRoot
import com.apstex.ifc2x3toolbox.ifc2x3.IfcWall
import com.apstex.ifc2x3toolbox.ifc2x3.InternalAccessClass
import java.util.ArrayList
import java.util.Collection
import java.util.HashSet
import java.util.LinkedHashMap
import java.util.List
import java.util.Map
import java.util.function.Function
import lca.LCA.LCAResult
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
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.Ifc2x3ParserBlock
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.LcaCalcBlock
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.SelectBlock
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.TypeBlock
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block
import org.sdu.dsl4ifc.sustainLang.Attribute
import org.sdu.dsl4ifc.sustainLang.BooleanExpression
import org.sdu.dsl4ifc.sustainLang.Calculation
import org.sdu.dsl4ifc.sustainLang.ComparisonExpression
import org.sdu.dsl4ifc.sustainLang.FilterCommand
import org.sdu.dsl4ifc.sustainLang.IfcType
import org.sdu.dsl4ifc.sustainLang.Reference
import org.sdu.dsl4ifc.sustainLang.SelectCommand
import org.sdu.dsl4ifc.sustainLang.SourceCommand
import org.sdu.dsl4ifc.sustainLang.Statement
import org.sdu.dsl4ifc.sustainLang.Value
import org.sdu.dsl4ifc.sustainLang.FilterCommand
import org.sdu.dsl4ifc.generator.conditional.impls.TrueValue
import lca.LCA.LCAResult
import java.util.Map
import org.sdu.dsl4ifc.sustainLang.LcaCalculation
import org.sdu.dsl4ifc.sustainLang.Calculation
import org.sdu.dsl4ifc.sustainLang.impl.LcaCalculationImpl
import org.sdu.dsl4ifc.sustainLang.SourceCommand
import org.sdu.dsl4ifc.sustainLang.impl.LcaParamsImpl
import org.sdu.dsl4ifc.sustainLang.LcaParams
import org.sdu.dsl4ifc.sustainLang.MatDef
import java.util.HashMap
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.LcaCalcBlock

import org.sdu.dsl4ifc.sustainLang.impl.LcaCalculationImpl


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
				val timeStart = System.currentTimeMillis()
				System.out.println("[CREATING GRAPH]")
				val graph = constructGraph(statement, resource)
				System.out.println("[EXECUTING]")
				val output = graph.output
				consoleOut.println(output.toString)
				val timeMsg = '''Done [«System.currentTimeMillis-timeStart» ms]'''
				consoleOut.println(timeMsg)
				System.out.println(timeMsg)
			}]
	}
		
	def Block<?> constructGraph(Statement statement, Resource resource) {
		
		val select = statement.select
		val selectBlock = select.createBlock(statement, resource)
		
		val filterBlock = statement.filters.head.createBlock(statement,resource);
		
		val dos = statement.^do
		val calcs = dos.calculation
		
	    for (Calculation cal : calcs) {
	    	consoleOut.println(cal.class.toString())
			if (cal instanceof LcaCalculation) {
				val lcaPar = cal.lcaParams;
				val matDefs = cal.matDefs
				
			 	var matDefMap = new HashMap<String,String>
				
				for (MatDef matDef : matDefs) {
					matDefMap.put(matDef.ifcMat,matDef.epdMatId);
				}
				

				val lcaBlock = new LcaCalcBlock("LcaBlock",lcaPar.sourceVar.toString(),lcaPar.area,matDefMap);

				lcaBlock.AddInput(filterBlock);
				val lcaResult = lcaBlock.Calculate();
				
			}
	    }
		
		val transforms = statement.transforms
    
		val checkedBlock = searchAndReplaceNodes(selectBlock)
		
		// TODO: Run through all nodes and run this on the block getOldBlockIfExists
			// If the block was replaces then don't go further down that branch as it will be the old ones either way
		
		return checkedBlock
	}
	
	def Block<?> searchAndReplaceNodes(Block<?> block) {
		
		if (catalog.blockExists(block)) {
			consoleOut.println("Reusing old block: " + block.generateCacheKey)
			var oldBlock = catalog.getBlock(block)
			catalog.registerBlock(oldBlock)
			
			return oldBlock
		}
		
		block.Inputs = new ArrayList(block.Inputs.map[b | searchAndReplaceNodes(b)])
		
		return block
	}
	
	def printLcaResult(LCAResult lcaResult) {
		consoleOut.println("LCA.LCA for building = " + lcaResult.getLcaResult() + " kg CO2-equivalents/m2/year");
		
		lcaResult.elements.forEach(e | {
			var map = e.resultMap;
			consoleOut.println("{ Name: " + e.getEpdId() + " With Quantity: " + e.getQuantity() + " and lifetime: " + e.getLifeTime());
			consoleOut.println("    LCA for A1-A3 + C3 & C4: " + e.getLcaVal());
			
			map.forEach(k,v | {
				if (v === null) {
					consoleOut.println("   " + k + " equals null");
				}
			})
			consoleOut.println("}");
		})
	}
	
	def dispatch Block<?> createBlock(SelectCommand select, Statement statement, Resource resource) {
		val selectBlock = new SelectBlock("Select", select.toAttributeReferences)
		
		// Create necesarry inputs		
		for (attribute : select.selects.distinctBy[s | s.reference.name]) {
			addInputs(statement, attribute, resource, selectBlock)
		}
		
		return catalog.ensureExistingIsUsed(selectBlock)
	}
	
	protected def void addInputs(Statement statement, Attribute attribute, Resource resource, SelectBlock selectBlock) {
		
		val filtersForAttribute = statement.filters.filter[filter | attribute.reference.name === filter.reference.name]
		if (!filtersForAttribute.isEmpty) { // References a filter
			val filter = filtersForAttribute.head
			val filterBlock = filter.createBlock(statement, resource)
			selectBlock.AddInput(catalog.ensureExistingIsUsed(filterBlock))
			return
		}
		
		val typesForAttribute = statement.from.types.filter[type | attribute.reference.name === type.name]
		if (!typesForAttribute.isEmpty) { // References a from
			val type = typesForAttribute.head
			val typeBlock = type.createBlock(statement, resource)
			selectBlock.AddInput(catalog.ensureExistingIsUsed(typeBlock))
		}
	}
	
	def dispatch Block<?> createBlock(FilterCommand filter, Statement statement, Resource resource) {
		val filterBlock = new FilterBlock('''Filter: «filter.reference.name»''', filter.reference.name, filter.toExpression)
		
		// Create necesarry inputs
		val typeBlock = filter.reference.createBlock(statement, resource)
		filterBlock.AddInput(typeBlock)
		
		val references = new HashSet()
		filter.condition.addAllVariableReferences(references)
		
		references.filter[ref | ref.name !== filter.reference.name].forEach[reference | {
			val block = reference.createBlock(statement, resource)
			filterBlock.AddInput(block)
		}]
		
		return catalog.ensureExistingIsUsed(filterBlock)
	}
	
	def dispatch Block<?> createBlock(Reference reference, Statement statement, Resource resource) {
		val typeBlock = new TypeBlock('''Type: "«reference.name»" «reference.ifcType»''', reference.name, reference.ifcType.toIfcType)
		
		// Create necesarry inputs
		val parserBlock = statement.source.createBlock(statement, resource)
		typeBlock.AddInput(parserBlock)
		
		return catalog.ensureExistingIsUsed(typeBlock)
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
	 
	def List<AttributeReference<Object, String>> toAttributeReferences(SelectCommand command) {
		command.selects.map[attribute | {
			new AttributeReference(attribute.reference.name, attribute.attribute, new ParameterValueExtractor(attribute.attribute))
		}]
	}
	
	protected def void addSelectBlockInputs(Statement statement, Attribute attribute, Resource resource, SelectBlock selectBlock) {
		
		val filtersForAttribute = statement.filters.filter[filter | attribute.reference.name === filter.reference.name]
		if (!filtersForAttribute.isEmpty) { // References a filter
			val filter = filtersForAttribute.head
			val filterBlock = filter.createBlock(statement, resource)
			selectBlock.AddInput(filterBlock)
			return
		}
		
		val typesForAttribute = statement.from.types.filter[type | attribute.reference.name === type.name]
		if (!typesForAttribute.isEmpty) { // References a from
			val type = typesForAttribute.head
			val typeBlock = type.createBlock(statement, resource)
			selectBlock.AddInput(typeBlock)
		}
	}
		
	def Expression<InternalAccessClass> toExpression(FilterCommand command) {
		val expression = command.condition.toBlockExpression(command.reference);
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
		return new ParameterValueExtractor(attribute.attribute)
	}
	
	def toIfcType(IfcType type) {
		switch (type) {
			case IFC_WALL: {
				return IfcWall
			}
			case IFC_DOOR: {
				return IfcDoor
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
	
	def static <T, K> List<T> distinctBy(List<T> list, Function<T, K> keyExtractor) {
        val Map<K, T> map = new LinkedHashMap();
        for (T element : list) {
            map.putIfAbsent(keyExtractor.apply(element), element);
        }
        return new ArrayList(map.values());
    }
	
}
