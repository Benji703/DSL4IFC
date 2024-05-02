package lca.epdConnectors.JsonWrappers;

public class FlowProperties {
	private double meanValue;
    private String referenceUnit;
    private String uuid;
    
    public FlowProperties(double meanValue, String referenceUnit, String uuid) {
		this.meanValue = meanValue;
		this.referenceUnit = referenceUnit;
		this.uuid = uuid;
	}

    public double getMeanValue() {
        return meanValue;
    }

    public void setMeanValue(double meanValue) {
        this.meanValue = meanValue;
    }

    public String getReferenceUnit() {
        return referenceUnit;
    }

    public void setReferenceUnit(String referenceUnit) {
        this.referenceUnit = referenceUnit;
    }

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
