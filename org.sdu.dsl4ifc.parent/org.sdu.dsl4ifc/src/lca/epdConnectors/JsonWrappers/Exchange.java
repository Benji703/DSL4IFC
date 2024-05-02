package lca.epdConnectors.JsonWrappers;

import java.util.ArrayList;
import java.util.List;

public class Exchange {
    private int dataSetInternalID;
    private List<FlowProperties> flowproperties;

    public Exchange(int dataSetInternalId, List<FlowProperties> flowProperties) {
    	setDataSetInternalID(dataSetInternalId);
    	setFlowproperties(flowProperties);
	}

    public List<FlowProperties> getFlowproperties() {
        return flowproperties;
    }

    public void setFlowproperties(List<FlowProperties> flowProperties) {
        this.flowproperties = flowProperties;
    }

	public int getDataSetInternalID() {
		return dataSetInternalID;
	}

	public void setDataSetInternalID(int dataSetInternalID) {
		this.dataSetInternalID = dataSetInternalID;
	}
}
