/*
 * generated by Xtext 2.30.0
 */
package org.sdu.dsl4ifc.generator

import com.apstex.ifc2x3toolbox.ifc2x3.IfcDoor
import com.apstex.ifc2x3toolbox.ifc2x3.IfcRoot
import com.apstex.ifc2x3toolbox.ifc2x3.IfcWall
import java.util.ArrayList
import java.util.List
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
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.FilterBlock
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.Ifc2x3ParserBlock
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.SourceBlock
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.TypeBlock
import org.sdu.dsl4ifc.sustainLang.Attribute
import org.sdu.dsl4ifc.sustainLang.BooleanExpression
import org.sdu.dsl4ifc.sustainLang.ComparisonExpression
import org.sdu.dsl4ifc.sustainLang.ComparisonOperator
import org.sdu.dsl4ifc.sustainLang.IfcType
import org.sdu.dsl4ifc.sustainLang.Reference
import org.sdu.dsl4ifc.sustainLang.SourceCommand
import org.sdu.dsl4ifc.sustainLang.Statement
import org.sdu.dsl4ifc.sustainLang.Value
import org.sdu.dsl4ifc.sustainLang.FilterCommand
import org.sdu.dsl4ifc.generator.conditional.impls.TrueValue

/**
 * Generates code from your model files on save.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
class SustainLangGenerator extends AbstractGenerator {
	
	public static MessageConsoleStream consoleOut = null;

	override void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {
		
		val myConsole = findConsole("SusLang");
		consoleOut = myConsole.newMessageStream();
		consoleOut.println("SusLang Console");
		
		val statements = resource.allContents.filter(Statement);
		
		statements.forEach[execute(resource)]
		
		//test(sourceCommand, resource)
	}
		
	def execute(Statement statement, Resource resource) {
		// TODO: This is wrong: Start from the bottom/end and build up using each blocks identities
		
		val source = statement.source
		val sourceBlock = new SourceBlock("Source", source.path, resource)
		val parserBlock = new Ifc2x3ParserBlock("Parser 2x3")
		parserBlock.AddInput(sourceBlock)
		
		val from = statement.from
		val typeBlocks = new ArrayList<TypeBlock<?>>;
		for (reference : from.types) {
			val typeBlock = new TypeBlock('''Type «reference.name»''', reference.name, reference.ifcType.toIfcType);
			typeBlock.AddInput(parserBlock)
			typeBlocks.add(typeBlock)
		}
		
		val filterCommands = statement.filters
		var filters = List.of()
		if (!filterCommands.empty) {
			filters = filterCommands.map[filter | filter.createFilterBlock(typeBlocks)]
			filters.forEach[f | {
				consoleOut.println('''Filter result: «f.variableName»''')
				f.output.forEach[e | consoleOut.println(e.toString)]
			}]
		}
		
		val dos = statement.^do
		
		val transforms = statement.transforms
	}
		
	def FilterBlock<?> createFilterBlock(FilterCommand filterCommand, List<TypeBlock<?>> typeBlocks) {
		val referenceName = filterCommand.reference.name
		val filterBlock = new FilterBlock('''Filter «referenceName»''', referenceName, filterCommand.toExpression(filterCommand.reference))
		// TODO: Actually only add if it depends on it (but this should be solved by building the graph from the bottom up)
		typeBlocks.forEach[block | filterBlock.AddInput(block)]
		return filterBlock
	}
		
	def Expression<?> toExpression(FilterCommand command, Reference variableReference) {
		val expression = command.condition.toBlockExpression(variableReference);
		return expression
		// Do this
	}
		
	def dispatch Expression<?> toBlockExpression(org.sdu.dsl4ifc.sustainLang.Expression expression, Reference variableReference) {
		throw new Exception("Cannot convert this expression to block expression: " + expression.class.name)
	}

	def dispatch Expression<?> toBlockExpression(BooleanExpression expression, Reference variableReference) {
		
		switch (expression.operator) {
			case AND:
				return new AndOperation(expression.left.toBlockExpression(variableReference), expression.right.toBlockExpression(variableReference))
			case OR:
				return new OrOperation(expression.left.toBlockExpression(variableReference), expression.right.toBlockExpression(variableReference))
			default: throw new Exception("BooleanExpression operator has not been implemented")
		}
	}
	
	// Could be an "exists" as well
	val defaultValue = new TrueValue()
	
	def dispatch Expression<?> toBlockExpression(ComparisonExpression expression, Reference variableReference) {
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
			default: {
				
			}
		}
	}
	
	protected def void test(SourceCommand sourceCommand, Resource resource) {
		val sourceBlock = new SourceBlock("Source", sourceCommand.path, resource)
		val parserBlock = new Ifc2x3ParserBlock("Parser 2x3")
		val wallTypeBlock = new TypeBlock("Type1", "w", IfcWall);
		val doorTypeBlock = new TypeBlock("Type2", "d", IfcRoot);
		
		parserBlock.AddInput(sourceBlock)
		wallTypeBlock.AddInput(parserBlock)
		doorTypeBlock.AddInput(parserBlock)
		
		filterBlockVariableComparisonTest(wallTypeBlock, doorTypeBlock)
	}
	
	def void filterBlockVariableComparisonTest(TypeBlock<?> wallTypeBlock, TypeBlock<?> doorTypeBlock) {
		val extr1 = new ParameterValueExtractor<IfcWall, String>("name");
		
		val valEq1 = new CompareParameterValueToParameterValueOperation("d", extr1, extr1, ComparisonOperator.EQUALS);
		val filterBlock = new FilterBlock<IfcWall>("F1", "w", valEq1);
		
		filterBlock.AddInput(wallTypeBlock);
		filterBlock.AddInput(doorTypeBlock);
		
		val output = filterBlock.output.toList
		if (output.empty) {
			consoleOut.println("No objects returned")
		} else {
			output.forEach[w | consoleOut.println(w.name.decodedValue)]
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
	
	
}
