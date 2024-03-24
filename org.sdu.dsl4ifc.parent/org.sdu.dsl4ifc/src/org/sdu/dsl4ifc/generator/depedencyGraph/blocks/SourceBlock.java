package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.sdu.dsl4ifc.generator.SustainLangGenerator;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;

public class SourceBlock extends Block<File> {

	private String path;
	private Resource resource;

	public SourceBlock(String name, String path, Resource resource) {
		super(name);
		this.path = path;
		this.resource = resource;
	}

	@Override
	public boolean IsOutOfDate() {
		// Check a checksum of the file? Or the change date and length?
		return false;
	}

	@Override
	public File Calculate() {
	
		var absolutePath = isRelativePath() ? getAbsolutePathFromRelativePath(path) : path;
		
		SustainLangGenerator.consoleOut.println("Found file: " + absolutePath);
		
		File file = new File(absolutePath);
		return file;
	}

	private boolean isRelativePath() {
		return path.startsWith(".");
	}

	private String getAbsolutePathFromRelativePath(String relativePath) {
		var uri = resource.getURI();
		var file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(uri.toPlatformString(true)));
        var path = new Path(file.getLocation().toOSString());
        var ifcPath = path.uptoSegment(path.segmentCount()-1).append(relativePath).toOSString();
        
        return ifcPath;
	}
}
