package org.sdu.dsl4ifc.generator.conditional.core;

import org.dhatim.fastexcel.Worksheet;

public interface ITrace {
	public void fillTraceInWorksheet(Worksheet worksheet, int startingRow);
}
