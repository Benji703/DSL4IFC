package LCA;

import java.util.ArrayList;

import EPDConnectors.BR18Connector;
import Interfaces.IEPDConnector;
import Interfaces.IEnvProductInfo;

public class LCA {
    private LCACalc lcaCalc;
    private IEPDConnector edpConnetcor;


    public LCA(){
        lcaCalc = new LCACalc();
        edpConnetcor = new BR18Connector();
    }


    public double CalculateLCAForWall(double quantity, double aRef) {

        double a = 53.1 * quantity;
        double c3 = 0.965 * quantity;
        double c4 = 0.7 * quantity;

        return lcaCalc.CalculateLCABasic(a,c3,c4,aRef);
    }

    public double CalculateLCAForElement(LCAIFCElement element, double area) {
        double aRef = 0;
        double c3Ref = 0;
        double c4Ref= 0;

        IEnvProductInfo envProductInfo = edpConnetcor.GetEPDDataByType(element.getName());

        double a = envProductInfo.getA() * element.getQuantity();
        double c3 = envProductInfo.getC3() * element.getQuantity();
        double c4 = envProductInfo.getC4() * element.getQuantity();

        return lcaCalc.CalculateLCABasic(a, c3, c4, area);
    }

    public LCAResult CalculateLCAWhole(ArrayList<LCAIFCElement> ifcElements, double area, double areaHeated, double b6) {

        for (LCAIFCElement element : ifcElements) {
            element.setLcaVal(CalculateLCAForElement(element, area));
        }

        double baseResult = ifcElements.stream().mapToDouble(LCAIFCElement::getLcaVal).sum();
        double opResult = lcaCalc.CalculateLCAOperational(b6, areaHeated);
        double result = baseResult + opResult;

        return new LCAResult(result,ifcElements);
    }
}
