package lca.LCA;

public class LcaIfcQuantity {
	
	private Double grossSideArea;
	private Double grossVolume;
	
	public LcaIfcQuantity() {
	}
	
	public LcaIfcQuantity(Double grossSideArea, Double grossVolume) {
		this.grossSideArea = grossSideArea;
		this.grossVolume = grossVolume;
	}

	public Double getGrossSideArea() {
		return grossSideArea;
	}

	public void setGrossSideArea(Double grossSideArea) {
		this.grossSideArea = grossSideArea;
	}

	public Double getGrossVolume() {
		return grossVolume;
	}

	public void setGrossVolume(Double grossVolume) {
		this.grossVolume = grossVolume;
	}
}
