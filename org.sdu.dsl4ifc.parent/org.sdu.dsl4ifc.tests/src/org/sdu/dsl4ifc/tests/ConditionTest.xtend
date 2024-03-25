package org.sdu.dsl4ifc.tests

import java.util.Arrays
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import org.sdu.dsl4ifc.generator.conditional.core.Expression
import org.sdu.dsl4ifc.generator.conditional.core.Value
import org.sdu.dsl4ifc.generator.conditional.impls.AndOperation
import org.sdu.dsl4ifc.generator.conditional.impls.ValueEqualsStreamOperation
import org.sdu.dsl4ifc.generator.conditional.impls.ValueEqualsValueOperation

import static org.junit.Assert.*

@ExtendWith(InjectionExtension)
@InjectWith(SustainLangInjectorProvider)
class ConditionTest {

  @Test
  def testValueClass() {
    val testValue = "test"
    val value = new Value(testValue)
    val expectedValues = Arrays.asList(testValue)
    assertEquals(expectedValues, value.getValues().toList())
  }

  @Test
  def testValueEqualsStreamOperationPositive() {
    val valueToCompare = "1"
    val list = Arrays.asList("1", "2", "3")
    val operation = new ValueEqualsStreamOperation(valueToCompare, list.stream)
    assertTrue(operation.Evaluate())
  }

  @Test
  def testValueEqualsStreamOperationNegative() {
    val valueToCompare = "4"
    val list = Arrays.asList("1", "2", "3")
    val operation = new ValueEqualsStreamOperation<String, String>(valueToCompare, list.stream)
    assertFalse(operation.Evaluate())
  }

  @Test
  def testValueEqualsValueOperation() {
    val value1 = "test"
    val value2 = "test"
    val operation = new ValueEqualsValueOperation<String>(value1, value2)
    assertTrue(operation.Evaluate())
  }

  @Test
  def testAndOperationPositive() {
    val mockLeft = new MockExpression(true)
    val mockRight = new MockExpression(true)
    val operation = new AndOperation(mockLeft, mockRight)
    assertTrue(operation.Evaluate())
  }

  @Test
  def testAndOperationNegativeLeft() {
    val mockLeft = new MockExpression(false)
    val mockRight = new MockExpression(true)
    val operation = new AndOperation(mockLeft, mockRight)
    assertFalse(operation.Evaluate())
  }

  @Test
  def testAndOperationNegativeRight() {
    val mockLeft = new MockExpression(true)
    val mockRight = new MockExpression(false)
    val operation = new AndOperation(mockLeft, mockRight)
    assertFalse(operation.Evaluate())
  }
}

class MockExpression extends Expression {
  final boolean value

  new(boolean value) {
    this.value = value
  }

  override Evaluate()  {
    return value
  }
}
