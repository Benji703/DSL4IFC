package org.sdu.dsl4ifc.generator.depedencyGraph.blocks.table;

import java.util.ArrayList;
import java.util.List;

import org.sdu.dsl4ifc.generator.depedencyGraph.blocks.AttributeReference;

public class Table {
	private List<Row> rows = new ArrayList<>();
	private List<ColumnHeader> headers = new ArrayList<>();
	private String title;
	
	public Table(String title) {
		this.title = title;
	}
	
	public void addRow(List<String> values) {
		if (values.size() != headers.size()) {
			throw new IllegalArgumentException("Expected " + headers.size() + " values but got " + values.size());
		}
		
		Row newRow = new Row(headers.size());
		for (int i = 0; i < values.size(); i++) {
			newRow.setValue(i, values.get(i));
		}
		
		rows.add(newRow);
	}
	
	public void addColumn(String headerText, AttributeReference<?> reference) {
		headers.add(new ColumnHeader(headerText, reference));
	}
	
	public void setCellValue(int rowIndex, int columnIndex, String value) {
		rows.get(rowIndex).setValue(columnIndex, value);
	}
	
	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("\n");
	    sb.append("Table: ");
	    sb.append(title);
	    sb.append("\n");
	    
	    // Calculate column widths
	    int[] columnWidths = new int[headers.size()];
	    for (int i = 0; i < columnWidths.length; i++) {
	        // Accommodate header text and cell values
	    	final int i1 = i;
	        int maxLength = Math.max(headers.get(i).headerText.length(),
	                rows.stream().mapToInt(row -> {
	                	Cell cell = row.cells.get(i1);
						return cell.value.length();
	                }).max().orElse(0));
	        columnWidths[i] = maxLength + 4; // Add padding
	    }

	    // Header row
	    sb.append(buildRowSeparator(columnWidths));
	    if (!headers.isEmpty()) { // Check if there are any headers
	        sb.append("|");
	    }
	    for (int i = 0; i < headers.size(); i++) {
	        sb.append(" ").append(pad(headers.get(i).headerText, columnWidths[i])).append(" |");
	    }
	    sb.append("\n");
	    sb.append(buildRowSeparator(columnWidths));

	    // Data rows
	    for (Row row : rows) {
	        sb.append("|");
	        for (int i = 0; i < row.cells.size(); i++) {
	            sb.append(" ").append(pad(row.cells.get(i).value, columnWidths[i])).append(" |");
	        }
	        sb.append("\n");
	    }

	    // Bottom border
	    sb.append(buildRowSeparator(columnWidths));

	    return sb.toString();
	}

	private String buildRowSeparator(int[] columnWidths) {
	    StringBuilder sb = new StringBuilder();
	    sb.append("+");
	    for (int width : columnWidths) {
	        sb.append("-".repeat(width + 2)).append("+"); // Add padding for pipes
	    }
	    return sb.append("\n").toString();
	}

	private String pad(String text, int width) {
	    return text.length() >= width ? text : text + " ".repeat(width - text.length()); 
	}

}