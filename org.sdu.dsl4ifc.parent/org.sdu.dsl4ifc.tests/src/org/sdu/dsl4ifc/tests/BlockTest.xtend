/*
 * generated by Xtext 2.30.0
 */
package org.sdu.dsl4ifc.tests

import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

@ExtendWith(InjectionExtension)
@InjectWith(SustainLangInjectorProvider)
class BlockTest {
	
	@Test
	def void fileExists() {
		//assertTrue("File does not exist", file.exists)
		//assertTrue("File is an actual file", file.isFile)
	}
	
	
	//@Test
	/*
	def void filterBlockVariableComparisonTest() {
		
		
		val valEq1 = new CompareParameterValueToParameterValueOperation("d", null, null, ComparisonOperator.EQUALS);
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
	* 
	*/
}

/*
 *  
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
* 
*/
