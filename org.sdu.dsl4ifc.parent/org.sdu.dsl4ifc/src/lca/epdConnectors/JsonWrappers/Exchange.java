package lca.epdConnectors.JsonWrappers;

import java.util.ArrayList;
import java.util.List;

public class Exchange {
    private int dataSetInternalID;
    private List<FlowProperties> flowProperties;

    public Exchange(int dataSetInternalId, List<FlowProperties> flowProperties) {
    	setDataSetInternalID(dataSetInternalId);
    	setFlowProperties(flowProperties);
	}

    public List<FlowProperties> getFlowProperties() {
        return flowProperties;
    }

    public void setFlowProperties(List<FlowProperties> flowProperties) {
        this.flowProperties = flowProperties;
    }

	public int getDataSetInternalID() {
		return dataSetInternalID;
	}

	public void setDataSetInternalID(int dataSetInternalID) {
		this.dataSetInternalID = dataSetInternalID;
	}
}
