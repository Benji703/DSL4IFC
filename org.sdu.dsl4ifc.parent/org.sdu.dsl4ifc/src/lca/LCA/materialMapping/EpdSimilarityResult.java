package lca.LCA.materialMapping;

public class EpdSimilarityResult {
	
	private String epdName;
	private String epdId;
	private float similarityScore;

	public EpdSimilarityResult(String epdName, String epdId, float similarityScore) {
		this.epdName = epdName;
		this.epdId = epdId;
		this.similarityScore = similarityScore;
		
	}

	public String getEpdName() {
		return epdName;
	}

	public String getEpdId() {
		return epdId;
	}

	public float getSimilarityScore() {
		return similarityScore;
	}
	
}