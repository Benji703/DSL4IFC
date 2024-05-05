package lca.LCA;

import java.util.ArrayList;
import java.util.List;

import org.sdu.dsl4ifc.generator.SustainLangGenerator;

import lca.epdConnectors.BR18Connector;
import lca.Interfaces.IEPDConnector;
import lca.Interfaces.IEnvProductInfo;

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

    public Double CalculateLCAForElement(LCAIFCElement element, Double area) {
    	if (area == null) {
			return null;
		}

        IEnvProductInfo envProductInfo = edpConnetcor.GetEPDDataByType(element.getEpdId());
        
        if (envProductInfo == null) {
        	return null;
        }
        
        element.setEpdName(envProductInfo.getName());

        CalculateLcaByQuantity(envProductInfo, element);

        double aRes = TranslateNullToZero(element.getAResult());
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
    
    private void CalculateLcaByQuantity(IEnvProductInfo envInfo, LCAIFCElement element) {
    	double quantity = 0;
    	
    	switch (envInfo.getDeclaredUnit()) {
		case M3:
			quantity = element.getQuantity().getGrossVolume();
			break;
		case M2:
			quantity = element.getQuantity().getGrossSideArea();
			break;
		default:
			//Skriv warning: SustainLangGenerator.consoleOut
			SustainLangGenerator.consoleOut.println("Unit: " + envInfo.getDeclaredUnit() + " from material with ID: " + element.getEpdId() + " not supported");
			break;
		}
    	
		element.setAResult(MultiplyWithQuantities(envInfo.getA(),envInfo.getDeclaredFactor(),envInfo.getMassFactor(),quantity,element.getLifeTime()));
		element.setC3Result(MultiplyWithQuantities(envInfo.getC3(),envInfo.getDeclaredFactor(),envInfo.getMassFactor(),quantity,element.getLifeTime()));
		element.setC4Result(MultiplyWithQuantities(envInfo.getC4(),envInfo.getDeclaredFactor(),envInfo.getMassFactor(),quantity,element.getLifeTime()));
    }

    private Double MultiplyWithQuantities(Double envInfo, Double declaredFactor, Double massFactor, Double quant, int lifeTime) {
        if (envInfo == null) {
            return null;
        }

        int yearFactor = 1;
        if (lifeTime >= 1) {
            yearFactor = (int) Math.ceil((50.0 / lifeTime));
        }
        
        return (envInfo/declaredFactor) * massFactor * quant * yearFactor;
    }
    
    public List<LCAIFCElement> calculateLCAByElement(List<LCAIFCElement> ifcElements, Double area) {
    	ArrayList<LCAIFCElement> ifcElementResults = new ArrayList<LCAIFCElement>(); 
    	
        for (LCAIFCElement element : ifcElements) {
            element.setLcaVal(CalculateLCAForElement(element, area));
            ifcElementResults.add(element);
        }
        
        return ifcElementResults;
    }

    public LCAResult CalculateLCAWhole(List<LCAIFCElement> ifcElements, Double areaHeated, double b6, Double area) {

        Double result = getResult(ifcElements, areaHeated, b6, area);

        return new LCAResult(result,ifcElements, area, areaHeated);
    }


	private Double getResult(List<LCAIFCElement> ifcElements, Double areaHeated, double b6, Double area) {
		if (area == null) {
			return null;
		}
		
		double baseResult = ifcElements.stream().filter(t -> t.getLcaVal() != null).mapToDouble(LCAIFCElement::getLcaVal).sum();
        double baseWithArea = lcaCalc.CalculateBuildingLca(baseResult, area);
        double opResult = lcaCalc.CalculateLCAOperational(b6, areaHeated);
        double result = baseWithArea + opResult;
		return result;
	}
}
