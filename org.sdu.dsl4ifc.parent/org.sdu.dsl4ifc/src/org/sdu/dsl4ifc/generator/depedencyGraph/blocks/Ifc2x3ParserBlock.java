package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.io.File;

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

	public Ifc2x3ParserBlock(String name, String path, Resource resource) {
		super(name);
		this.path = path;
		this.resource = resource;
	}

	@Override
	public boolean IsOutOfDate() {
		return false;
	}

	@Override
	public IfcModel Calculate() {
		
		var absolutePath = isRelativePath() ? getAbsolutePathFromRelativePath(path) : path;
		
		SustainLangGenerator.consoleOut.println("Found file: " + absolutePath);
		
		File file = new File(absolutePath);
		
		var ifcModel = new IfcModel();
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
		
		var absolutePath = isRelativePath() ? getAbsolutePathFromRelativePath(path) : path;
		keyBuilder.append(absolutePath);
		
        return keyBuilder.toString();
	}

	@Override
	public void fillTraceInWorksheet(Worksheet worksheet, int startingRow) {
		// TODO Auto-generated method stub
		
	}

}
