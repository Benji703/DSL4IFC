package org.sdu.dsl4ifc.generator.depedencyGraph.blocks;

import org.sdu.dsl4ifc.generator.depedencyGraph.core.Block;
import java.util.ArrayList;
import java.util.Map;
import lca.LCA.*;

public class LcaBlock extends Block<LCAResult> {

	public LcaBlock(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean IsOutOfDate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LCAResult Calculate() {
		
		//var sources = findAllBlocks(FilterBlock.class);
		//sources.get(0).getOutput;
		
		LCA lca = new LCA();

        String concrete = "Letbeton vægelement, 150 mm tyk væg, 10% udsparinger";
        String floorS = "Celleglas-isolering 115 kg/m³";

        LCAIFCElement wall1 = new LCAIFCElement(concrete, 200);
        wall1.setLifeTime(12);
        LCAIFCElement wall2 = new LCAIFCElement(concrete, 200);
        wall2.setLifeTime(70);
        LCAIFCElement wall3 = new LCAIFCElement(concrete, 200);
        LCAIFCElement wall4 = new LCAIFCElement(concrete, 200);

        LCAIFCElement floor = new LCAIFCElement(floorS, 1000);

        ArrayList<LCAIFCElement> elements = new ArrayList<>();
        elements.add(wall1);
        elements.add(wall2);
        elements.add(wall3);
        elements.add(wall4);
        elements.add(floor);

        LCAResult lcaResult = lca.CalculateLCAWhole(elements, 200, 180, 1000);
		
		return lcaResult;
	}

}
