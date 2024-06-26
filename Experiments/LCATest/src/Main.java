import LCA.LCA;
import LCA.LCAIFCElement;
import LCA.LCAResult;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {

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

        LCAIFCElement br18 = lcaResult.getElements().stream().filter(e -> e.getName().equals(floorS)).findAny().orElse(null);

        System.out.println(br18);

        System.out.println("LCA.LCA for building = " + lcaResult.getLcaResult() + " kg CO2-equivalents/m2/year");


        lcaResult.getElements().forEach(e -> {
            Map<String, Double> map = e.getResultMap();
            System.out.println("{ Name: " + e.getName() + " With Quantity: " + e.getQuantity());
            System.out.println("    LCA for A1-A3 + C3 & C4: " + e.getLcaVal());

            for (Map.Entry<String, Double> set : map.entrySet()) {
                if (set.getValue() == null) {
                    System.out.println("   " + set.getKey() + " equals null");
                }
            }
            System.out.println("}");
        });
    }

}