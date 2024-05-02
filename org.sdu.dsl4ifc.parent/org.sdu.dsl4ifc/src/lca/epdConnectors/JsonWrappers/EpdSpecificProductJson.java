package lca.epdConnectors.JsonWrappers;

public class EpdSpecificProductJson {
	private LciaResults lciaResults;
	
	public EpdSpecificProductJson(LciaResults lciaResults) {
		this.setLciaResults(lciaResults);
	}

	public LciaResults getLciaResults() {
		return lciaResults;
	}

	public void setLciaResults(LciaResults lciaResults) {
		this.lciaResults = lciaResults;
	}
}
