package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import java.util.List;
import org.dhatim.fastexcel.Worksheet;
import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;
import org.sdu.dsl4ifc.sustainLang.AreaAuto;
import org.sdu.dsl4ifc.sustainLang.AreaSource;
import org.sdu.dsl4ifc.sustainLang.AreaValue;

import lca.LCA.LCA;
import lca.LCA.LCAResult;

public class LcaSummaryBlock extends VariableReferenceBlock<LCAResult> {
	private String sourceVarName;
	private double heatedArea;
	private AreaSource area;
	private double b6;
	private String referenceName;
	private Double dOp;
	
	public LcaSummaryBlock(String sourceVarName, String referenceName, double heatedArea, double b6, AreaSource area, Double dOp) {
		super("LCA Summary (source " + sourceVarName + ")");
		this.sourceVarName = sourceVarName;
		this.referenceName = referenceName;
		this.heatedArea = heatedArea;
		this.b6 = b6;
		this.area = area;
		this.dOp = dOp;
	}

	@Override
	public boolean IsOutOfDate() {
		boolean inputsAreNewer = Inputs.stream().anyMatch(input -> input.GetTimeOfCalculation() > GetTimeOfCalculation());
		
		if (inputsAreNewer) {
			return true;
		}
		
		return false;
	}

	@Override
	public List<LCAResult> Calculate() {
		
		var lcaCalcBlock = findFirstBlock(LcaCalcBlock.class);
		var lcaElements = lcaCalcBlock.getOutput();
	    
		LCA lca = new LCA();

		Double area = lcaCalcBlock.getArea();

        LCAResult lcaResult = lca.CalculateLCAWhole(lcaElements, heatedArea, b6, area);
        if (dOp != null) {
        	lcaResult.setdResult(lca.CalculateLcaForDModule(lcaElements, heatedArea, dOp, area));
        	lcaResult.setdSubtracted(lcaResult.getLcaResult()+lcaResult.getdResult());
        }
        
		
		return List.of(lcaResult);
	}


	@Override
	public String generateCacheKey() {
		StringBuilder keyBuilder = new StringBuilder(Name);
		
		keyBuilder.append("source:"+sourceVarName+",");
		keyBuilder.append("reference:"+referenceName+",");
		keyBuilder.append("area:"+getAreaCacheKey()+",");
		keyBuilder.append("heatedArea:"+heatedArea+",");
		keyBuilder.append("b6:"+b6+",");
		keyBuilder.append("dop:"+dOp+",");
		
        for (Block<?> block : Inputs) {
            keyBuilder.append(block.generateCacheKey()+";");
        }
        return keyBuilder.toString();
	}

	private String getAreaCacheKey() {
		if (area instanceof AreaValue) {
			return ((AreaValue) area).getArea()+"";
		}
		if (area instanceof AreaAuto) {
			return "AUTO";
		}
		return null;
	}

	@Override
	public String getReferenceName() {
		return referenceName;
	}

	@Override
	public void fillTraceInWorksheet(Worksheet worksheet, int startingRow) {
		int currentRow = startingRow;
		worksheet.value(currentRow, 0, "LCA Result");	worksheet.style(currentRow, 0).bold().set();
		worksheet.value(currentRow, 1, "Area");			worksheet.style(currentRow, 1).bold().set();
		worksheet.value(currentRow, 2, "Heated Area");	worksheet.style(currentRow, 2).bold().set();
		worksheet.value(currentRow, 3, "B6");			worksheet.style(currentRow, 3).bold().set();
		
		var summary = getOutput();
		
		for (LCAResult lcaResult : summary) {
			currentRow++;
			
			worksheet.value(currentRow, 0, lcaResult.getLcaResult());
			worksheet.value(currentRow, 1, lcaResult.getArea());
			worksheet.value(currentRow, 2, lcaResult.getHeatedArea());
			worksheet.value(currentRow, 3, b6);
		}
	}
}
