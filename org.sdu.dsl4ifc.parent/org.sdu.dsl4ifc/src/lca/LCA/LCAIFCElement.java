package lca.LCA;

import java.util.HashMap;
import java.util.Map;

public class LCAIFCElement {
	private String ifcName;
	private String epdName;
    private String epdId;
    private LcaIfcQuantity quantity;
    private int lifeTime;
    private Double aResult;
    private Double c3Result;
    private Double c4Result;
    private Double dResult;
    private Double lcaVal;
    private int ifcStepNumber;


    public LCAIFCElement(String epdId, String ifcName, int ifcStepNumber, LcaIfcQuantity quantity) {
        this.epdId = epdId;
        this.ifcName = ifcName;
        this.quantity = quantity;
        this.ifcStepNumber = ifcStepNumber;
    }

    public Double getAResult() {
        return aResult;
    }

    public void setAResult(Double aResult) {
        this.aResult = aResult;
    }

    public Double getC3Result() {
        return c3Result;
    }

    public void setC3Result(Double c3Result) {
        this.c3Result = c3Result;
    }

    public Double getC4Result() {
        return c4Result;
    }

    public void setC4Result(Double c4Result) {
        this.c4Result = c4Result;
    }

    public Double getLcaVal() {
        return lcaVal;
    }

    public void setLcaVal(Double lcaVal) {
        this.lcaVal = lcaVal;
    }

    public String getEpdId() {
        return epdId;
    }

    public LcaIfcQuantity getQuantity() {
        return quantity;
    }

    public int getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(int lifeTime) {
        this.lifeTime = lifeTime;
    }


    public Map<String, Double> getResultMap() {
        Map<String, Double> map = new HashMap<>();
        map.put("A1-A3",aResult);
        map.put("C3",c3Result);
        map.put("C4",c4Result);

        return map;
    }

    @Override
    public String toString() {
        return "LCAIFCElement{" +
                "name='" + epdId + '\'' +
                ", quantity=" + quantity +
                ", aResult=" + aResult +
                ", c3Result=" + c3Result +
                ", c4Result=" + c4Result +
                ", lcaVal=" + lcaVal +
                '}';
    }

    public Double getdResult() {
        return dResult;
    }

    public void setdResult(Double dResult) {
        this.dResult = dResult;
    }

	public String getIfcName() {
		return ifcName;
	}

	public void setIfcName(String ifcName) {
		this.ifcName = ifcName;
	}

	public String getEpdName() {
		return epdName;
	}

	public void setEpdName(String epdName) {
		this.epdName = epdName;
	}

	public int getIfcStepNumber() {
		return ifcStepNumber;
	}

	public void setIfcStepNumber(int ifcStepNumber) {
		this.ifcStepNumber = ifcStepNumber;
	}
	
}
