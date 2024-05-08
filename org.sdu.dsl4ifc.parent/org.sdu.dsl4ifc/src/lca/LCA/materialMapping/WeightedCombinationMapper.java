package lca.LCA.materialMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lca.Interfaces.EpdOverview;
import lca.Interfaces.IEPDConnector;

public class WeightedCombinationMapper {

	private static float weightLevenshtein = 0.5f;
	
	private List<EpdOverview> allEpds;

	public WeightedCombinationMapper(IEPDConnector epdConnector) {
		this.allEpds = epdConnector.GetAllEpdNames()
				.stream()
				.filter(overview -> overview.getEpdName() != null)
				.toList();
	}
	
	public EpdSimilarityResult getMostSimilarEpd(String materialName) {
		var lowerCaseMaterialName = materialName.toLowerCase();
		
		var results = allEpds.stream().map(epd -> {
			var primaryScore = combinedSimilarity(lowerCaseMaterialName, epd.getEpdName().toLowerCase());
			var alternativeScore = combinedSimilarity(lowerCaseMaterialName, epd.getEpdName().toLowerCase());
			var score = Math.max(primaryScore, alternativeScore);
			return new EpdSimilarityResult(epd.getEpdName(), epd.getEpdId(), score);
		}).toList();
		
		var sorted = results.stream().sorted((o1, o2) -> Math.round((o2.getSimilarityScore() - o1.getSimilarityScore())*1000)).toList();
		
		EpdSimilarityResult bestResult = sorted.get(0);
		System.out.println(materialName + " --> " + bestResult.getEpdName());
		return bestResult;
	}
	
	public static float combinedSimilarity(String str1, String str2) {
		if (str1 == null || str2 == null) {
			throw new IllegalArgumentException("Strings cannot be null");
		}

		int levenshteinDistance = calculateLevenshteinDistance(str1, str2);	// Lower is better
	    float normalizedLevenshtein = 1 - ((float) levenshteinDistance / Math.max(str1.length(), str2.length())); // Normalize between 0 and 1 and higher is better
	    
	    float jaccardSimilarity = calculateJaccardSimilarity(str1, str2);	// Higher is better [0,1]
	    
	    return (weightLevenshtein * normalizedLevenshtein) + ((1 - weightLevenshtein) * (jaccardSimilarity));
	}

	private static int calculateLevenshteinDistance(String str1, String str2) {
		int[][] dp = new int[str1.length() + 1][str2.length() + 1];

		for (int i = 0; i < str1.length() + 1; i++) {
			dp[i][0] = i;
		}

		for (int j = 0; j < str2.length() + 1; j++) {
			dp[0][j] = j;
		}

		for (int i = 1; i < str1.length() + 1; i++) {
			for (int j = 1; j < str2.length() + 1; j++) {
				if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
					dp[i][j] = dp[i - 1][j - 1];
				} else {
					int insert = dp[i - 1][j] + 1;
					int delete = dp[i][j - 1] + 1;
					int substitute = dp[i - 1][j - 1] + 1;
					dp[i][j] = Math.min(insert, Math.min(delete, substitute));
				}
			}
		}
		return dp[str1.length()][str2.length()];
	}

	private static float calculateJaccardSimilarity(String str1, String str2) {
		Set<String> set1 = new HashSet<>();
		Set<String> set2 = new HashSet<>();

		for (String c : str1.split(" ")) {
			set1.add(c);
		}

		for (String c : str2.split(" ")) {
			set2.add(c);
		}

		Set<String> intersection = new HashSet<>(set1);
		intersection.retainAll(set2);

		int unionSize = set1.size() + set2.size() - intersection.size();
		return (float) intersection.size() / unionSize;
	}
}
