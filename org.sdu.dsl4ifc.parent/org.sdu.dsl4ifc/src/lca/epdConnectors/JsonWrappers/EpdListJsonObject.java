package lca.epdConnectors.JsonWrappers;

import java.util.List;

public class EpdListJsonObject {
	private int totalCount;
	private List<EpdMetaDataJsonObject> epdList;
	
	public EpdListJsonObject(int totalCount, List<EpdMetaDataJsonObject> epdList) {
		this.setTotalCount(totalCount);
		this.setEpdList(epdList);
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<EpdMetaDataJsonObject> getEpdList() {
		return epdList;
	}

	public void setEpdList(List<EpdMetaDataJsonObject> epdList) {
		this.epdList = epdList;
	}
	
	
}
