package lca.LCA;

import java.util.List;

public class LcaDResult {
	private Double area;
	private Double heatedArea;
	private Double dOp;
	private Double dResult;
	private Double wholeResult;
	private List<LCAIFCElement> elements;
	
	public LcaDResult(Double dResult, Double dOp, List<LCAIFCElement> elements, Double area, Double heatedArea) {
		this.dResult = dResult;
		this.dOp = dOp;
		this.elements = elements;
		this.setArea(area);
		this.setHeatedArea(heatedArea);
	}

	public Double getdOp() {
		return dOp;
	}

	public Double getWholeResult() {
		return wholeResult;
	}

	public void setWholeResult(Double wholeResult) {
		this.wholeResult = wholeResult;
	}

	public Double getdResult() {
		return dResult;
	}

	public void setdResult(Double dResult) {
		this.dResult = dResult;
	}

	public List<LCAIFCElement> getElements() {
		return elements;
	}

	public Double getHeatedArea() {
		return heatedArea;
	}

	public void setHeatedArea(Double heatedArea) {
		this.heatedArea = heatedArea;
	}

	public Double getArea() {
		return area;
	}

	public void setArea(Double area) {
		this.area = area;
	}
	
	
}
