/*
 * generated by Xtext 2.30.0
 */
package org.sdu.dsl4ifc.generator

import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.AbstractGenerator
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xtext.generator.IGeneratorContext
import com.apstex.ifc2x3toolbox.ifcmodel.IfcModel;
import com.apstex.ifc2x3toolbox.ifc2x3.IfcWall

/**
 * Generates code from your model files on save.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
class SustainLangGenerator extends AbstractGenerator {

	override void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {
		var ifcModel = new IfcModel();
		
		var walls = ifcModel.getCollection(IfcWall)
		walls.forEach[wall | System.out.println(wall)]
		
//		fsa.generateFile('greetings.txt', 'People to greet: ' + 
//			resource.allContents
//				.filter(Greeting)
//				.map[name]
//				.join(', '))
	}
}
