package lca.LCA;

public class LcaIfcQuantity {
	
	private Double grossSideArea;
	private Double volume;
	
	public LcaIfcQuantity() {
	}
	
	public LcaIfcQuantity(Double grossSideArea, Double grossVolume) {
		this.grossSideArea = grossSideArea;
		this.volume = grossVolume;
	}

	public Double getGrossSideArea() {
		return grossSideArea;
	}

	public void setGrossSideArea(Double grossSideArea) {
		this.grossSideArea = grossSideArea;
	}

	public Double getGrossVolume() {
		return volume;
	}

	public void setGrossVolume(Double grossVolume) {
		this.volume = grossVolume;
	}
}
