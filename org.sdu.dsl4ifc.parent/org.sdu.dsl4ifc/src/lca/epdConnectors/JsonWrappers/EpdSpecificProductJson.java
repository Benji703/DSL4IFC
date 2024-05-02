package lca.epdConnectors.JsonWrappers;

public class EpdSpecificProductJson {
	private LciaResults lciaResults;
	private Exchanges exchanges;
	
	public EpdSpecificProductJson(LciaResults lciaResults, Exchanges exchanges) {
		this.setLciaResults(lciaResults);
		this.setExchanges(exchanges);
	}

	public LciaResults getLciaResults() {
		return lciaResults;
	}

	public void setLciaResults(LciaResults lciaResults) {
		this.lciaResults = lciaResults;
	}

	public Exchanges getExchanges() {
		return exchanges;
	}

	public void setExchanges(Exchanges exchanges) {
		this.exchanges = exchanges;
	}
}
