/*
 * generated by Xtext 2.30.0
 */
package org.sdu.dsl4ifc.tests

import com.google.inject.Inject
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.util.ArrayList
import java.util.HashMap
import java.util.Map
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.resource.XtextResourceSet
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.util.StringInputStream
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import org.sdu.dsl4ifc.generator.IfcFileInformation
import org.sdu.dsl4ifc.generator.SustainLangGenerator

@ExtendWith(InjectionExtension)
@InjectWith(SustainLangInjectorProvider)
class PerformanceTest {
	
	@Test
	def void fileExists() {
		//assertTrue("File does not exist", file.exists)
		//assertTrue("File is an actual file", file.isFile)
	}
	
	@Inject
	SustainLangGenerator generator
	
	PrintStream log
	String csvPath = "./ifc-files-data.csv";
	
	Map<String, IfcFileInformation> fileInformation = new HashMap
	
	final int reps = 10;
	
	@Test
	def void test() {
		
		var ifcFolderPath = "/Users/andreasedalpedersen/SDU-local/Speciale/Evaluation/IFC-files";
        var folderFile = new File(ifcFolderPath);
        var ifcFiles = folderFile.listFiles().filter[file | file.name.endsWith(".ifc")];
		
		log = new PrintStream(new FileOutputStream(csvPath));
		try {
	        
	        log.println("File Name, File Size [byte], File Entities Count, File Relations Count, File Properties Count, IFC Source Program, Cold Response Time [ms], Warm Response Time [ms], Cold Query ID, Warm Query ID, Test Name, IfcType (Data Volume), Warm vs. Cold Response Time Diff. [ms]");
			
			for (file : ifcFiles) {
				val path = file.absolutePath
				println(path)
				
				val info = generator.getIfcFileInformation(path)
				fileInformation.put(path, info)
				
				for (var i = 0; i < reps; i++) {
					runTests(path)
				}
			}
		
		} finally {
			log.close
		}
		
	}
	
	protected def void runTests(String path) {
		runTest(path, TestEnum.IdentifierNameChanged)
		runTest(path, TestEnum.AddedToSelect)
		runTest(path, TestEnum.FilterExpressionChanged)
		runTest(path, TestEnum.LcaChanged)
		runTest(path, TestEnum.FilterAdded)
		runTest(path, TestEnum.FilterRemoved)
		runTest(path, TestEnum.LcaAdded)
		runTest(path, TestEnum.LcaRemoved)
	}
	
	def runTest(String ifcPath, TestEnum test) {
		
		println('''Running Test [«test»]''')
		
		switch (test) {
			case IdentifierNameChanged: {
				runAllVolumeQueries(ifcPath, QueryEnum.Identifier1, QueryEnum.Identifier2, "Identifier Name Changed")
			}
			case AddedToSelect: {
				runAllVolumeQueries(ifcPath, QueryEnum.SelectOneProperty, QueryEnum.SelectTwoProperties, "Property Added to Select")
			}
			case FilterExpressionChanged: {
				runAllVolumeQueries(ifcPath, QueryEnum.Filter1, QueryEnum.Filter2, "Filter Expression Changed")
			}
			case LcaChanged: {
				runAllVolumeQueries(ifcPath, QueryEnum.LCA1, QueryEnum.LCA2, "LCA Changed")
			}
			case FilterAdded: {
				runAllVolumeQueries(ifcPath, QueryEnum.NoFilter, QueryEnum.Filter1, "Filter Added")
			}
			case FilterRemoved: {
				runAllVolumeQueries(ifcPath, QueryEnum.Filter1, QueryEnum.NoFilter, "Filter Removed")
			}
			case LcaAdded: {
				runAllVolumeQueries(ifcPath, QueryEnum.NoLCA, QueryEnum.LCA1, "LCA Added")
			}
			case LcaRemoved: {
				runAllVolumeQueries(ifcPath, QueryEnum.LCA1, QueryEnum.NoLCA, "LCA Removed")
			}
		
			default: {
				throw new Exception("Test is not found...")
			}
		}
	}
	
	String[] ifcTypes = #[
		"IfcMaterial", 
		"IfcWall", 
		"IfcRoot"
	];
	
	def runAllVolumeQueries(String ifcPath, QueryEnum coldQuery, QueryEnum warmQuery, String testName) {
		
		for (ifcType : ifcTypes) {
			generator.clearCache
			runWarmAndColdQuery(ifcPath, coldQuery, warmQuery, testName, ifcType)
		}
	}
	
	def runWarmAndColdQuery(String ifcPath, QueryEnum coldQuery, QueryEnum warmQuery, String testName, String ifcType) {
		// Set up a resource set
		val resourceSet = new XtextResourceSet();
		
		val coldResource = createResource(ifcPath, coldQuery, resourceSet, ifcType);
		val warmResource = createResource(ifcPath, warmQuery, resourceSet, ifcType);
		
		// Cold
		var time = System.currentTimeMillis
		generator.runTest(coldResource);
		val coldResponseTime = System.currentTimeMillis - time
		println('''Cold response time: «coldResponseTime» ms.''')
		
		// Warm
		time = System.currentTimeMillis
		generator.runTest(warmResource);
		val warmResponseTime = System.currentTimeMillis - time
		println('''Warm response time: «warmResponseTime» ms.''')
		
		appendResult(coldResponseTime, warmResponseTime, ifcPath, coldQuery, warmQuery, testName, ifcType)
	}
	
	def appendResult(long coldResponseTime, long warmResponseTime, String ifcPath, QueryEnum coldQuery, QueryEnum warmQuery, String testName, String ifcType) {
		val info = fileInformation.get(ifcPath)
		
		// 0. file name
		// 1. file size [byte]
		// 2. file entities count
		// 3. file relations count
		// 4. file properties count
		// 5. ifc source program
		// 6. cold response time [ms]
		// 7. warm response time [ms]
		// 8. cold query id
		// 9. warm query id
		// 10. test name
		// 11. IfcType (Data Volume)
		// 12. warm vs. cold response time diff.
		
        val cells = new ArrayList<String>;
		
		// 0: File name
        cells.add(info.fileName);

        // 1: File size (byte)
        cells.add(info.fileSize+"");

        // 2: Entities count
        cells.add(info.entitiesCount+"");

        // 3: Relations count
        cells.add(info.relationsCount+"");

        // 4: Properties count
        cells.add(info.propertiesCount+"");

        // 5: Source program
        cells.add(info.sourceProgram);
        
        // 6: Cold Response time
        cells.add(coldResponseTime+"");
        
        // 7: Warm Response time
        cells.add(warmResponseTime+"");
        
        // 8: Cold Query id
        cells.add(coldQuery+"");
        
        // 9: Warm Query id
        cells.add(warmQuery+"");
        
        // 10: Test Name
        cells.add(testName);
        
        // 11: IfcType (Data Volume)
        cells.add(ifcType);
        
        // 12: warm vs. cold response time diff. [ms]
        cells.add(warmResponseTime - coldResponseTime + "");
        
        val row = String.join(", ", cells);
        log.println(row)
	}
	
	def Resource createResource(String ifcPath, QueryEnum queryEnum, XtextResourceSet resourceSet, String ifcType) {
		val resource = resourceSet.createResource(URI.createURI('''«queryEnum»_«ifcPath».slang'''));
		
		// Add content to the resource
		val query = getQuery(ifcPath, queryEnum, ifcType);
		println('''Got query: '«query»''')
				
		resource.load(new StringInputStream(query), null);
		
		return resource
	}
	
	def String getQuery(String ifcPath, QueryEnum queryEnum, String ifcType) {
		
		switch (queryEnum) {
			case Identifier1: {
				return '''
				SOURCE MODEL "«ifcPath»"
				SELECT x.stepnumber, x.name
				FROM «ifcType» x
				FILTER x WHERE x.name <> ""
				'''
			}
			case Identifier2: {
				return '''
				SOURCE MODEL "«ifcPath»"
				SELECT y.stepnumber, y.name
				FROM «ifcType» y
				FILTER y WHERE x.name <> ""
				'''
			}
			case SelectOneProperty: {
				return '''
				SOURCE MODEL "«ifcPath»"
				SELECT x.stepnumber
				FROM «ifcType» x
				'''
			}
			case SelectTwoProperties: {
				return '''
				SOURCE MODEL "«ifcPath»"
				SELECT x.stepnumber, x.name
				FROM «ifcType» x
				'''
			}
			case Filter1: {
				return '''
				SOURCE MODEL "«ifcPath»"
				SELECT x.stepnumber, x.name
				FROM «ifcType» x
				FILTER x WHERE x.name <> ""
				'''
			}
			case Filter2: {
				return '''
				SOURCE MODEL "«ifcPath»"
				SELECT x.stepnumber, x.name
				FROM «ifcType» x
				FILTER x WHERE x.name = "ifc-name"
				'''
			}
			case LCA1: {
				return '''
				SOURCE MODEL "«ifcPath»"
				SELECT elements.result
				FROM «ifcType» x
				DO LCA(
					AREA 5
					AREAHEAT 5
					B6 10
					SOURCE x
					MATDEF (
						"Letklinkerblok, Massiv" -> "​​#B1339"
					) 
				) sum, elements
				'''
			}
			case LCA2: {
				return '''
				SOURCE MODEL "«ifcPath»"
				SELECT elements.result
				FROM «ifcType» x
				DO LCA(
					AREA 500
					AREAHEAT 500
					B6 100
					SOURCE x
					MATDEF (
						"Concrete, Cast In Situ" -> "#G0242"
					) 
				) sum, elements
				'''
			}
			case NoFilter: {
				return '''
				SOURCE MODEL "«ifcPath»"
				SELECT x.stepnumber, x.name
				FROM «ifcType» x
				'''
			}
			case NoLCA: {
				return '''
				SOURCE MODEL "«ifcPath»"
				SELECT x.stepnumber
				FROM «ifcType» x
				'''
			}
			
			default: throw new Exception("Query not found")
		}
	}
}

enum QueryEnum {
	Identifier1,
	Identifier2,
	SelectOneProperty,
	SelectTwoProperties,
	Filter1,
	Filter2,
	LCA1,
	LCA2,
	NoFilter,
	NoLCA
}

enum TestEnum {
	IdentifierNameChanged,
	AddedToSelect,
	FilterExpressionChanged,
	LcaChanged,
	FilterAdded,
	FilterRemoved,
	LcaAdded,
	LcaRemoved
}