import com.apstex.ifc2x3toolbox.ifcmodel.IfcModel;

import java.io.File;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {

        LCA lca = new LCA();

        LCAIFCElement wall1 = new LCAIFCElement(IFCType.wall, 200);
        LCAIFCElement wall2 = new LCAIFCElement(IFCType.wall, 200);
        LCAIFCElement wall3 = new LCAIFCElement(IFCType.wall, 200);
        LCAIFCElement wall4 = new LCAIFCElement(IFCType.wall, 200);

        LCAIFCElement floor = new LCAIFCElement(IFCType.floor, 1000);

        ArrayList<LCAIFCElement> elements = new ArrayList<>();
        elements.add(wall1);
        elements.add(wall2);
        elements.add(wall3);
        elements.add(wall4);
        elements.add(floor);

        ArrayList<LCAIFCElement> elementResults = lca.CalculateLCAWhole(elements, 1000);

        double result = elementResults.stream().mapToDouble(LCAIFCElement::getLcaVal).sum();

        System.out.println("LCA for building = " + result + " kg CO2-equivalents/m2/year");
    }
}