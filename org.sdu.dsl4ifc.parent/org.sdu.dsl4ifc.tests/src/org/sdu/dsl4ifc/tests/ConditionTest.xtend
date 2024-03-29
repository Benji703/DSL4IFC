package org.sdu.dsl4ifc.tests

import java.util.Arrays
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import org.sdu.dsl4ifc.generator.conditional.core.Expression
import org.sdu.dsl4ifc.generator.conditional.impls.AndOperation

import static org.junit.Assert.*
import org.junit.jupiter.api.BeforeAll
import org.sdu.dsl4ifc.generator.conditional.core.VariableStore
import org.sdu.dsl4ifc.generator.conditional.core.StringValue
import org.sdu.dsl4ifc.generator.conditional.impls.ValueEqualsValueOperation
import org.sdu.dsl4ifc.generator.conditional.impls.ValueInStreamOperation

@ExtendWith(InjectionExtension)
@InjectWith(SustainLangInjectorProvider)
class ConditionTest {
	
  @BeforeAll
	def static void setup() {
	}

  @Test
  def testValueClass() {
    val testValue = "test"
    val value = new StringValue(testValue)
    val expectedValues = Arrays.asList(testValue)
    assertEquals(expectedValues, value.getValues().toList())
  }

  @Test
  def testValueEqualsStreamOperationPositive() {
    val valueToCompare = "1"
    val list = Arrays.asList("1", "2", "3")
    val operation = new ValueInStreamOperation(list.stream)
    assertTrue(operation.Evaluate(valueToCompare, null))
  }

  @Test
  def testValueEqualsStreamOperationNegative() {
    val valueToCompare = "4"
    val list = Arrays.asList("1", "2", "3")
    val operation = new ValueInStreamOperation<String>(list.stream)
    assertFalse(operation.Evaluate(valueToCompare, null))
  }

  @Test
  def testValueEqualsValueOperation() {
    val value1 = "test"
    val value2 = "test"
    val operation = new ValueEqualsValueOperation<String, String>(value2)
    assertTrue(operation.Evaluate(value1, null))
  }

  @Test
  def testAndOperationPositive() {
    val mockLeft = new MockExpression(true)
    val mockRight = new MockExpression(true)
    val operation = new AndOperation(mockLeft, mockRight)
    assertTrue(operation.Evaluate("does not matter", null))
  }

  @Test
  def testAndOperationNegativeLeft() {
    val mockLeft = new MockExpression(false)
    val mockRight = new MockExpression(true)
    val operation = new AndOperation(mockLeft, mockRight)
    assertFalse(operation.Evaluate("does not matter", null))
  }

  @Test
  def testAndOperationNegativeRight() {
    val mockLeft = new MockExpression(true)
    val mockRight = new MockExpression(false)
    val operation = new AndOperation(mockLeft, mockRight)
    assertFalse(operation.Evaluate("does not matter", null))
  }
  
}

class MockExpression<T> extends Expression<T> {
  final boolean value

  new(boolean value) {
    this.value = value
  }

  override Evaluate(T item, VariableStore variables)  {
    return value
  }
}
