package lca.LCA;

import java.util.ArrayList;
import java.util.List;

public class LCAResult {
	private Double area;
	private Double heatedArea;
    private Double lcaResult;
    private Double dResult;
    private Double dSubtracted;
    private List<LCAIFCElement> elements;

    public LCAResult(double lcaResult, Double area, Double heatedArea) {
        this.lcaResult = lcaResult;
		this.area = area;
		this.heatedArea = heatedArea;
        elements = new ArrayList<>();
    }

    public LCAResult(Double lcaResult, List<LCAIFCElement> elements, Double area, Double heatedArea) {
        this.lcaResult = lcaResult;
        this.elements = elements;

		this.area = area;
		this.heatedArea = heatedArea;
    }

    public List<LCAIFCElement> getElements() {
        return elements;
    }

    public void setElements(List<LCAIFCElement> elements) {
        this.elements = elements;
    }

    public Double getLcaResult() {
        return lcaResult;
    }

    public void setLcaResult(double lcaResult) {
        this.lcaResult = lcaResult;
    }

	public Double getArea() {
		return area;
	}

	public void setArea(Double area) {
		this.area = area;
	}

	public Double getHeatedArea() {
		return heatedArea;
	}

	public void setHeatedArea(Double heatedArea) {
		this.heatedArea = heatedArea;
	}

	public Double getdResult() {
		return dResult;
	}

	public void setdResult(Double dResult) {
		this.dResult = dResult;
	}

	public Double getdSubtracted() {
		return dSubtracted;
	}

	public void setdSubtracted(Double dSubtracted) {
		this.dSubtracted = dSubtracted;
	}
    
    
}
