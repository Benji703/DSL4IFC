package lca.epdConnectors.JsonWrappers;

import java.util.ArrayList;
import java.util.List;

public class EpdListJsonObject {
	private int totalCount;
	private ArrayList<EpdMetaDataJsonObject> data;
	
	public EpdListJsonObject(int totalCount, ArrayList<EpdMetaDataJsonObject> epdList) {
		this.setTotalCount(totalCount);
		this.setEpdList(epdList);
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public ArrayList<EpdMetaDataJsonObject> getEpdList() {
		return data;
	}

	public void setEpdList(ArrayList<EpdMetaDataJsonObject> epdList) {
		this.data = epdList;
	}
	
	
}
