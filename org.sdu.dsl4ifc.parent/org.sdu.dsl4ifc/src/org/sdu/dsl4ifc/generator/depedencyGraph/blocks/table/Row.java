package org.sdu.dsl4ifc.generator.depedencyGraph.blocks.table;

import java.util.ArrayList;
import java.util.List;

public class Row {
	public List<Cell> cells;
	
	Row(int size) {
		cells = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			cells.add(new Cell(""));
		}
	}
	
	public void setValue(int columnIndex, String value) {
		cells.set(columnIndex, new Cell(value));
	}
}