package lca.LCA;

import java.util.ArrayList;
import java.util.List;

import org.sdu.dsl4ifc.generator.SustainLangGenerator;

import lca.epdConnectors.BR18Connector;
import lca.epdConnectors.EcoPlatformConnector;
import lca.DomainClasses.Enums.EpdType;
import lca.Interfaces.IEPDConnector;
import lca.Interfaces.IEnvProductInfo;

public class LCA {
    private LCACalc lcaCalc;
    private IEPDConnector edpConnetcor;


    public LCA(EpdType epdType){
        lcaCalc = new LCACalc();
        
        switch (epdType) {
		case EcoPlatform:
	        edpConnetcor = new EcoPlatformConnector();
			break;
		case BR18:
	        edpConnetcor = new BR18Connector();
			break;
		default:
			edpConnetcor = new BR18Connector();
			break;
		}

    }
    
    public LCA(){
        lcaCalc = new LCACalc();
        this.edpConnetcor = new BR18Connector();
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

        if (!CalculateLcaByQuantity(envProductInfo, element)) {
        	return null;
        }

        Double aRes = element.getAResult();
        Double c3Res = element.getC3Result();
        Double c4Res = element.getC4Result();

        return lcaCalc.CalculateLCABasic(aRes, c3Res, c4Res);
    }
    
    /**
     * 
     * @param envInfo
     * @param element
     * @return true if a supported unit is provided, false if an unsupported unit is provided 
     */
    private boolean CalculateLcaByQuantity(IEnvProductInfo envInfo, LCAIFCElement element) {
    	Double quantity = null;
    	
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
			return false;
		}
    	
		element.setAResult(MultiplyWithQuantities(envInfo.getA(),envInfo.getDeclaredFactor(),envInfo.getMassFactor(),quantity,element.getLifeTime()));
		element.setC3Result(MultiplyWithQuantities(envInfo.getC3(),envInfo.getDeclaredFactor(),envInfo.getMassFactor(),quantity,element.getLifeTime()));
		element.setC4Result(MultiplyWithQuantities(envInfo.getC4(),envInfo.getDeclaredFactor(),envInfo.getMassFactor(),quantity,element.getLifeTime()));
		return true;
    }

    private Double MultiplyWithQuantities(Double envInfo, Double declaredFactor, Double massFactor, Double quant, int lifeTime) {
        if (envInfo == null || quant == null) {
            return null;
        }

        int yearFactor = 1;
        if (lifeTime >= 1) {
            yearFactor = (int) Math.ceil((50.0 / lifeTime));
        }
        
        return (envInfo/declaredFactor) * quant * yearFactor;
    }
    
    public List<LCAIFCElement> calculateLCAByElement(List<LCAIFCElement> ifcElements, Double area) {
    	ArrayList<LCAIFCElement> ifcElementResults = new ArrayList<LCAIFCElement>(); 
    	
        for (LCAIFCElement element : ifcElements) {
            Double lcaValue = CalculateLCAForElement(element, area);
			element.setLcaVal(lcaValue);
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
	
    public Double CalculateLcaForDModule(List<LCAIFCElement> ifcElements, Double areaHeated, double dOp, Double area) {

        return getDResult(ifcElements, areaHeated, dOp, area);
    }
    
	private Double getDResult(List<LCAIFCElement> ifcElements, Double areaHeated, double dOp, Double area) {
		if (area == null) {
			return null;
		}
		
		double baseDResult = ifcElements.stream().filter(t -> t.getLcaVal() != null).mapToDouble(LCAIFCElement::getDResult).sum();
        double baseDWithArea = lcaCalc.CalculateLCAModuleDBasic(baseDResult, area);
        double opDResult = lcaCalc.CalculateLCAModuleDOperational(dOp, areaHeated);
        double dResult = baseDWithArea + opDResult;
		return dResult;
	}


	public IEPDConnector getEdpConnetcor() {
		return edpConnetcor;
	}
	
}
