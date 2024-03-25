import DomainClasses.BR18ProductDeclaration;
import DomainClasses.Enums.IFCTypeEnum;
import EPDConnectors.BR18Connector;
import LCA.LCA;
import LCA.LCAIFCElement;
import LCA.LCAResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {

        /*LCA lca = new LCA();

        LCAIFCElement wall1 = new LCAIFCElement(IFCTypeEnum.wall, 200);
        LCAIFCElement wall2 = new LCAIFCElement(IFCTypeEnum.wall, 200);
        LCAIFCElement wall3 = new LCAIFCElement(IFCTypeEnum.wall, 200);
        LCAIFCElement wall4 = new LCAIFCElement(IFCTypeEnum.wall, 200);

        LCAIFCElement floor = new LCAIFCElement(IFCTypeEnum.floor, 1000);

        ArrayList<LCAIFCElement> elements = new ArrayList<>();
        elements.add(wall1);
        elements.add(wall2);
        elements.add(wall3);
        elements.add(wall4);
        elements.add(floor);

        LCAResult lcaResult = lca.CalculateLCAWhole(elements, 200, 180, 1000);

        System.out.println("LCA.LCA for building = " + lcaResult.getLcaResult() + " kg CO2-equivalents/m2/year");
        */

        BR18Connector br18 = new BR18Connector();

        Map<String, List<String>> list = br18.GetEPDDataByType(IFCTypeEnum.wall);
    }

}