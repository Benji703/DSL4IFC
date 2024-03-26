/*
 * generated by Xtext 2.30.0
 */
package org.sdu.dsl4ifc.tests

import java.io.File
import java.util.Collection
import java.util.stream.Stream
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import org.sdu.dsl4ifc.generator.conditional.impls.ValueEqualsValueOperation
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.FilterBlock
import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.TypeBlock

import static extension org.junit.jupiter.api.Assertions.assertEquals
import org.sdu.dsl4ifc.generator.conditional.impls.ValueEqualsStreamOperation
import java.util.List
import static org.junit.Assert.assertArrayEquals
import org.sdu.dsl4ifc.generator.conditional.impls.ValueEqualsVariableOperation

@ExtendWith(InjectionExtension)
@InjectWith(SustainLangInjectorProvider)
class BlockTest {

	static File file;

	@BeforeAll
	def static void setUpBeforeClass() throws Exception {
		file = new File("/Users/andreasedalpedersen/SDU-local/Speciale/dsl4ifc/DSL4IFC/org.sdu.dsl4ifc.parent/org.sdu.dsl4ifc.tests/src/org/sdu/dsl4ifc/tests/Duplex_A_20110907.ifc")
		
	}
	
	@Test
	def void fileExists() {
		//assertTrue("File does not exist", file.exists)
		//assertTrue("File is an actual file", file.isFile)
	}
	
	@Test
	def void parseTest() {
		
	}
	
	@Test
	def void filterBlockSingleValueComparisonTest() {
		
		val valEq1 = new ValueEqualsValueOperation("1");
		
		val filterBlock = new FilterBlock<String>("F1", "w", valEq1);
		
		val list = #["1", "2", "3"];
		val mockTypeBlock = new MockTypeBlock("T1", "w", String, list);
		filterBlock.AddInput(mockTypeBlock);
		
		val output = filterBlock.output.toList
		output.size.assertEquals(1, "Should only hold one number")
		output.head.assertEquals("1", "Object should be '1'")
	}
	
	@Test
	def void filterBlockMutipleValueComparisonTest() {
		val compList = #["2", "3"];
		val valEq1 = new ValueEqualsStreamOperation(compList.stream);
		
		val filterBlock = new FilterBlock<String>("F1", "w", valEq1);
		
		val list = #["1", "2", "3"];
		val mockTypeBlock = new MockTypeBlock("T1", "w", String, list);
		filterBlock.AddInput(mockTypeBlock);
		
		val output = filterBlock.output.toList
		assertEquals(2, output.size, "Should only hold two numbers")
		assertArrayEquals("Object should be '1' and '2'", List.of("2", "3").toArray(), output.toArray())
	}
	
	@Test
	def void filterBlockVariableComparisonTest() {
		val valEq1 = new ValueEqualsVariableOperation("d");
		val filterBlock = new FilterBlock<String>("F1", "w", valEq1);
		
		val list1 = #["1", "2", "3"];
		val mockTypeBlock1 = new MockTypeBlock("T1", "w", String, list1);
		filterBlock.AddInput(mockTypeBlock1);
		
		val list2 = #["2", "3"];
		val mockTypeBlock2 = new MockTypeBlock("T2", "d", String, list2);
		filterBlock.AddInput(mockTypeBlock2);
		
		val output = filterBlock.output.toList
		assertEquals(2, output.size, "Should only hold two numbers")
		assertArrayEquals("Object should be '1' and '2'", List.of("2", "3").toArray(), output.toArray())
	}
}

class MockTypeBlock<T> extends TypeBlock<T> {
	
	Collection<T> values;
	
	new(String name, String variableName, Class<T> clazz, Collection<T> values) {
		super(name, variableName, clazz)
		this.values = values;
	}
	
	
	override Stream<T> Calculate() {
		return values.stream
	}
	
}