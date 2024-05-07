package lca.epdConnectors.JsonWrappers;

public class EpdSpecificProductJson {
	private LciaResults LCIAResults;
	private Exchanges exchanges;
	
	public EpdSpecificProductJson(LciaResults lciaResults, Exchanges exchanges) {
		this.setLciaResults(lciaResults);
		this.setExchanges(exchanges);
	}

	public LciaResults getLciaResults() {
		return LCIAResults;
	}

	public void setLciaResults(LciaResults lciaResults) {
		this.LCIAResults = lciaResults;
	}

	public Exchanges getExchanges() {
		return exchanges;
	}

	public void setExchanges(Exchanges exchanges) {
		this.exchanges = exchanges;
	}
}
