package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.dhatim.fastexcel.Worksheet;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.sdu.dsl4ifc.generator.SustainLangGenerator;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;

import com.apstex.ifc2x3toolbox.ifcmodel.IfcModel;

public class Ifc2x3ParserBlock extends Block<IfcModel> {

	private String path;
	private Resource resource;

	public Ifc2x3ParserBlock(String path, Resource resource) {
		super("IFC2x3 Parser (" + getFileName(path) + ")");
		this.path = path;
		this.resource = resource;
	}

	private static String getFileName(String path) {
		Path p = new Path(path);
		return p.segments()[p.segmentCount()-1];
	}

	@Override
	public boolean IsOutOfDate() {
		return false;
	}

	@Override
	public IfcModel Calculate() {
		
		var absolutePath = getAbsoluteFilePath();
		
		SustainLangGenerator.consoleOut.println("Found file: " + absolutePath);
		
		File file = new File(absolutePath);
		
		var ifcModel = new IfcModel();
		
		ifcModel.addStepParserProgressListener(listener -> {
			var state = listener.getCurrentState();
			try {
				printLoadingBar(state, 40, listener.getMessage());
			} catch (IOException e) {
				// Nothing
			}
		});
		
        try {
        	SustainLangGenerator.consoleOut.println("Parsing file: " + file.getAbsolutePath() + "...");
			ifcModel.readStepFile(file);
			SustainLangGenerator.consoleOut.println("Parsing done!");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
        ifcModel.setTypeCacheEnabled(true);
        
		return ifcModel;
	}
	
	private boolean isRelativePath() {
		return !path.startsWith("/");
	}

	private String getAbsolutePathFromRelativePath(String relativePath) {
		var uri = resource.getURI();
		var file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(uri.toPlatformString(true)));
        var path = new Path(file.getLocation().toOSString());
        var ifcPath = path.uptoSegment(path.segmentCount()-1).append(relativePath).toOSString();
        
        return ifcPath;
	}
	
	@Override
	public String generateCacheKey() {
		StringBuilder keyBuilder = new StringBuilder(Name);
		
		var absolutePath = getAbsoluteFilePath();
		keyBuilder.append(absolutePath);
		
        return keyBuilder.toString();
	}

	private String getAbsoluteFilePath() {
		return isRelativePath() ? getAbsolutePathFromRelativePath(path) : path;
	}

	@Override
	public void fillTraceInWorksheet(Worksheet worksheet, int startingRow) {
		int currentRow = startingRow;
		
		// Absolute file path
		String absoluteFilePath = getAbsoluteFilePath();
		worksheet.value(currentRow, 0, "IFC File Path:"); worksheet.style(currentRow, 0).bold().set(); 
		worksheet.value(currentRow, 1, absoluteFilePath); 
		currentRow++;
		
		// File size
		File file = new File(absoluteFilePath);
		long fileSizeBytes = file.length();
		String formatedFileSize = fileSizeBytes / 1000 / 1000 + " MB";
		worksheet.value(currentRow, 0, "IFC File Size:"); worksheet.style(currentRow, 0).bold().set(); 
		worksheet.value(currentRow, 1, formatedFileSize); 
		currentRow++;
		
		// Last edited
		long lastModifiedMilis = file.lastModified();
		Date lastModifiedDate = new Date(lastModifiedMilis); 
		worksheet.value(currentRow, 0, "Last Modified:"); worksheet.style(currentRow, 0).bold().set(); 
		worksheet.value(currentRow, 1, lastModifiedDate); worksheet.style(currentRow, 1).format("dd/MM/yyyy HH:mm:ss").set();
		currentRow++;
		
		// IFC schema
		var ifcModel = getOutput();
		String schema = ifcModel.getFile_SchemaString();
		worksheet.value(currentRow, 0, "IFC Schema:"); worksheet.style(currentRow, 0).bold().set(); 
		worksheet.value(currentRow, 1, schema); 
		currentRow++;
		
		worksheet.width(1, 30);
	}

	public static void printLoadingBar(int percentage, int barLength, String message) throws IOException {
		var console = SustainLangGenerator.consoleOut;
		
        // Ensure percentage is between 0 and 100
        if (percentage < 0) {
            percentage = 0;
        } else if (percentage > 100) {
            percentage = 100;
        }

        // Calculate the number of filled parts in the bar
        
        
        boolean isDone = message.trim().equals("");
		if (isDone) {
        	percentage = 100;
		}
        
        int filledLength = (int) (barLength * (percentage / 100.0));

        // Create the loading bar string
        StringBuilder bar = new StringBuilder();
        bar.append('[');
        for (int i = 0; i < barLength; i++) {
            if (i < filledLength) {
                bar.append('#'); // Filled part
            } else {
                bar.append('-'); // Empty part
            }
        }
        bar.append(']');

        // Clear the console line before printing (return carriage and flush)
        console.print("\r"); // Return carriage
        console.flush();

        // Print the loading bar with percentage
        console.print(bar.toString() + " " + String.format("%d%%", percentage) + " " + message);

        // If we've reached 100%, print a newline to end the output properly
        if (isDone) {
        	console.println(); // Move to a new line
        }
    }
}
