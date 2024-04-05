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

        IEnvProductInfo envProductInfo = edpConnetcor.GetEPDDataByType(element.getName());

        element.setaResult(MultiplyWithQuanitities(envProductInfo.getA(),element));
        element.setC3Result(MultiplyWithQuanitities(envProductInfo.getC3(),element));
        element.setC4Result(MultiplyWithQuanitities(envProductInfo.getC4(),element));

        double aRes = TranslateNullToZero(element.getaResult());
        double c3Res = TranslateNullToZero(element.getC3Result());
        double c4Res = TranslateNullToZero(element.getC4Result());

        return lcaCalc.CalculateLCABasic(aRes, c3Res, c4Res, area);
    }

    private double TranslateNullToZero(Double d) {
        if (d == null) {
            return 0;
        }

        return d;
    }

    private Double MultiplyWithQuanitities(Double envInfo, LCAIFCElement element) {
        if (envInfo == null) {
            return null;
        }

        int yearFactor = 1;
        if (element.getLifeTime() >= 1) {
            yearFactor = Math.ceilDiv(50, element.getLifeTime());
        }

        return envInfo * element.getQuantity() * yearFactor;
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
