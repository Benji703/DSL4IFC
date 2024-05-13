package lca.Interfaces;

public class EpdOverview {
	
	private String epdName;
	private String alternativeEpdName;
	private String epdId;
	
	public EpdOverview(String epdName, String epdId, String alternativeEpdName) {
		this.epdName = epdName;
		this.epdId = epdId;
		this.alternativeEpdName = alternativeEpdName;
	}

	public String getEpdName() {
		return epdName;
	}

	public String getEpdId() {
		return epdId;
	}

	public String getAlternativeEpdName() {
		return alternativeEpdName;
	}
	
	
	
}