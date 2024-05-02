package lca.epdConnectors;

import lca.DomainClasses.EnvProductInfo;
import lca.DomainClasses.Enums.DeclaredUnitEnum;
import lca.Interfaces.IEPDConnector;
import lca.Interfaces.IEnvProductInfo;
import lca.Utilities.ParameterStringBuilder;
import lca.epdConnectors.JsonWrappers.AniesJsonObject;
import lca.epdConnectors.JsonWrappers.EpdListJsonObject;
import lca.epdConnectors.JsonWrappers.EpdMetaDataJsonObject;
import lca.epdConnectors.JsonWrappers.EpdSpecificProductJson;
import lca.epdConnectors.JsonWrappers.Exchange;
import lca.epdConnectors.JsonWrappers.FlowProperty;
import lca.epdConnectors.JsonWrappers.LciaResult;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.DataOutputStream;

public class EcoPlatformConnector implements IEPDConnector {
	private Gson gson;
	private final String totalGwpMethId = "6a37f984-a4b3-458a-a20a-64418c145fa2";
	private final String flowPropUnitId = "93a60a56-a3c8-22da-a746-0800200c9a66";
	private final int unitDataSetId = 1;
	private String bearerToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJCZW5qaTcwMyIsImlzcyI6IkVDT1BPUlRBTCIsImF1ZCI6ImFueSIsInZlciI6IjcuOS40IiwicGVybWlzc2lvbnMiOlsic3RvY2s6cmVhZCxleHBvcnQ6MiIsInN0b2NrOnJlYWQsZXhwb3J0OjEiLCJ1c2VyOnJlYWQsd3JpdGU6NjcxIl0sInJvbGVzIjpbXSwiaWF0IjoxNzExMTAwMjE5LCJleHAiOjE3MTg5ODQyMTksImVtYWlsIjoiYmVhbmQxOUBzdHVkZW50LnNkdS5kayIsInRpdGxlIjoiIiwiZmlyc3ROYW1lIjoiQmVuamFtaW4iLCJsYXN0TmFtZSI6IkFuZGVyc2VuIiwiZ2VuZXJhdGVOZXdUb2tlbnMiOmZhbHNlLCJqb2JQb3NpdGlvbiI6IkthbmRpZGF0c3R1ZGVyZW5kZSIsImFkZHJlc3MiOnsiY2l0eSI6Ik9kZW5zZSIsInppcENvZGUiOiI1MjQwIiwiY291bnRyeSI6IkRLIiwic3RyZWV0IjoiIn0sIm9yZ2FuaXphdGlvbiI6e30sInVzZXJHcm91cHMiOlt7InVzZXJHcm91cE5hbWUiOiJyZWdpc3RlcmVkX3VzZXJzIiwidXNlckdyb3VwT3JnYW5pemF0aW9uTmFtZSI6IkRlZmF1bHQgT3JnYW5pemF0aW9uIn1dLCJhZG1pbmlzdHJhdGVkT3JnYW5pemF0aW9uc05hbWVzIjoiIiwicGhvbmUiOiIiLCJkc3B1cnBvc2UiOiJUaWwgZXQga2FuZGlkYXRwcm9qZWt0IHZlZHLDuHJlbmRlIGVuIElULXBsYXRmb3JtIHRpbCB1ZHJlZ25pbmcgYWYgYmxhLiBMQ0EiLCJzZWN0b3IiOiIiLCJpbnN0aXR1dGlvbiI6IlN5ZGRhbnNrIFVuaXZlcnNpdGV0In0.tidxKWZu6Nz9z4GgSAPRO85dHjaWpIvCxX9bM0lxGSPIDw4SSRgdbLeSGfvck6nG6YqChG_Flr32iQq-QMIGNkzchtxkkWg3uupmtyYhJAQINWwEo5Pjh1LkO5Gh3cMN5LhMoT_qXFZs2B7DsEA6V2RQpMPfKTflm8p47wl9BKU";
	
	public EcoPlatformConnector() {
		gson = new Gson();
	}
	
	@Override
	public IEnvProductInfo GetEPDDataByType(String name) {	
		
		EpdMetaDataJsonObject epdObject = getEpdDatabaseObject(name);
		
		if (epdObject == null || (epdObject.getUri() == null || epdObject.getUri().trim().isEmpty())) {
			return null;
		}
		
		String uri = epdObject.getUri();
		
		EpdSpecificProductJson epdProduct = fetchEpdData(uri);
		if (epdProduct == null) {
			return null;
		}
		
		FlowProperty flowProp = getFlowPropertyWithUnit(epdProduct.getExchanges().getExchange());
		LciaResult lciaResult = getLciaResult(epdProduct.getLciaResults().getLCIAResult());
		
		if (flowProp == null || lciaResult == null) {
			return null;
		}
		
		List<AniesJsonObject> aniesList = lciaResult.getOther().getAnies();
		
		double a = 0.0;
		double c3 = 0.0;
		double c4 = 0.0;
		DeclaredUnitEnum decUnit = ParseToEnum(flowProp.getReferenceUnit());
		
		for (AniesJsonObject anie : aniesList) {
			switch (anie.getModule()) {
			case "A1":
				a += ParseStringToDouble(anie.getValue());
				break;
			case "A2":
				a += ParseStringToDouble(anie.getValue());
				break;
			case "A3":
				a += ParseStringToDouble(anie.getValue());
				break;
			case "A1-A3":
				a = ParseStringToDouble(anie.getValue());
				break;
			case "C3":
				c3 = ParseStringToDouble(anie.getValue());
				break;
			case "C4":
				c4 = ParseStringToDouble(anie.getValue());
				break;

			default:
				break;
			}
		}
		
		
		return new EnvProductInfo(name, a, c3, c4, decUnit, flowProp.getMeanValue());
	}
	
    private DeclaredUnitEnum ParseToEnum(String s) {
        DeclaredUnitEnum declaredEnum;

        try {
            declaredEnum = DeclaredUnitEnum.valueOf(s);
        } catch (NullPointerException e) {
            declaredEnum = null;
        }

        return declaredEnum;
    }
	
	private FlowProperty getFlowPropertyWithUnit(List<Exchange> exchange) {
		Exchange unitExchange = null;
		
		for (Exchange e : exchange) {
			if (e.getDataSetInternalID() == unitDataSetId) {
				unitExchange = e;
			}
		}
		
		if (unitExchange == null) {
			return null;
		}
		
		FlowProperty flowProp = null;
		
		for (FlowProperty f : unitExchange.getFlowProperties()) {
			if (f.getUuid().equals(flowPropUnitId)) {
				flowProp = f;
			}
		}
		
		return flowProp;
	}

	private double ParseStringToDouble(String value) {
		double d = 0.0;
		
		try {
			d = Double.parseDouble(value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		
		return d;
	}

	private EpdMetaDataJsonObject getEpdDatabaseObject(String name) {
        EpdMetaDataJsonObject epdDataJson = null;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("search", "true");
        parameters.put("format", "json");
        parameters.put("distributed", "true");
        parameters.put("virtual", "true");
        parameters.put("metaDataOnly", "false");
        parameters.put("lang", "en");
        parameters.put("name", name);
		
		try {
			String baseUrl = "https://data.eco-platform.org/resource/processes?";
			String reqUrl = baseUrl + ParameterStringBuilder.getParamsString(parameters);
            URL urlObject = new URL(reqUrl);
            HttpURLConnection con = (HttpURLConnection) urlObject.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + bearerToken);

            int responseCode = con.getResponseCode();
            String s = con.getURL().toString();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String sJson = getJsonStringFromStream(con);

                EpdListJsonObject epdList = gson.fromJson(sJson, EpdListJsonObject.class);
                
                if (epdList.getEpdList().size() > 0) {
                    epdDataJson = epdList.getEpdList().get(0);
                }
            	
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return epdDataJson;
		
	}

	private EpdSpecificProductJson fetchEpdData(String url) {
		EpdSpecificProductJson epdSpecific = null;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("format", "json");
        parameters.put("view", "extended");

        try {
            URL urlObject = new URL(url + ParameterStringBuilder.getParamsString(parameters));
            HttpURLConnection con = (HttpURLConnection) urlObject.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + bearerToken);
            
            con.setInstanceFollowRedirects(true);

            int responseCode = con.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String sJson = getJsonStringFromStream(con);

                epdSpecific = gson.fromJson(sJson, EpdSpecificProductJson.class);


            } else {
                System.out.println("Failed to fetch data. Response Code: " + responseCode);
            }
        } catch (IOException  e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
      
        
        return epdSpecific;
    }

	private String getJsonStringFromStream(HttpURLConnection connection) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
		    response.append(inputLine);
		}
		in.close();
		return response.toString();
	}
	
	private LciaResult getLciaResult(List<LciaResult> lciaResultList) {
		LciaResult lciaResult = null; 
        
        for (LciaResult lcR : lciaResultList) {
        	if (lcR.getReferenceToLCIAMethodDataSet().getRefObjectId().equals(totalGwpMethId)) {
        		lciaResult = lcR;
        	}
        }
        
        return lciaResult;
	}

}
