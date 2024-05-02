package lca.epdConnectors.JsonWrappers;

import java.util.List;

public class Exchange {
    private List<FlowProperty> flowProperties;
    private int dataSetInternalID;

    public List<FlowProperty> getFlowProperties() {
        return flowProperties;
    }

    public void setFlowProperties(List<FlowProperty> flowProperties) {
        this.flowProperties = flowProperties;
    }

	public int getDataSetInternalID() {
		return dataSetInternalID;
	}

	public void setDataSetInternalID(int dataSetInternalID) {
		this.dataSetInternalID = dataSetInternalID;
	}
}
