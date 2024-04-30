package lca.epdConnectors;

import lca.Interfaces.IEPDConnector;
import lca.Interfaces.IEnvProductInfo;
import lca.epdConnectors.JsonWrappers.EpdListJsonObject;
import lca.epdConnectors.JsonWrappers.EpdMetaDataJsonObject;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EcoPlatformConnector implements IEPDConnector {
	Gson gson = new Gson();
	String bearerToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJCZW5qaTcwMyIsImlzcyI6IkVDT1BPUlRBTCIsImF1ZCI6ImFueSIsInZlciI6IjcuOS40IiwicGVybWlzc2lvbnMiOlsic3RvY2s6cmVhZCxleHBvcnQ6MiIsInN0b2NrOnJlYWQsZXhwb3J0OjEiLCJ1c2VyOnJlYWQsd3JpdGU6NjcxIl0sInJvbGVzIjpbXSwiaWF0IjoxNzExMTAwMjE5LCJleHAiOjE3MTg5ODQyMTksImVtYWlsIjoiYmVhbmQxOUBzdHVkZW50LnNkdS5kayIsInRpdGxlIjoiIiwiZmlyc3ROYW1lIjoiQmVuamFtaW4iLCJsYXN0TmFtZSI6IkFuZGVyc2VuIiwiZ2VuZXJhdGVOZXdUb2tlbnMiOmZhbHNlLCJqb2JQb3NpdGlvbiI6IkthbmRpZGF0c3R1ZGVyZW5kZSIsImFkZHJlc3MiOnsiY2l0eSI6Ik9kZW5zZSIsInppcENvZGUiOiI1MjQwIiwiY291bnRyeSI6IkRLIiwic3RyZWV0IjoiIn0sIm9yZ2FuaXphdGlvbiI6e30sInVzZXJHcm91cHMiOlt7InVzZXJHcm91cE5hbWUiOiJyZWdpc3RlcmVkX3VzZXJzIiwidXNlckdyb3VwT3JnYW5pemF0aW9uTmFtZSI6IkRlZmF1bHQgT3JnYW5pemF0aW9uIn1dLCJhZG1pbmlzdHJhdGVkT3JnYW5pemF0aW9uc05hbWVzIjoiIiwicGhvbmUiOiIiLCJkc3B1cnBvc2UiOiJUaWwgZXQga2FuZGlkYXRwcm9qZWt0IHZlZHLDuHJlbmRlIGVuIElULXBsYXRmb3JtIHRpbCB1ZHJlZ25pbmcgYWYgYmxhLiBMQ0EiLCJzZWN0b3IiOiIiLCJpbnN0aXR1dGlvbiI6IlN5ZGRhbnNrIFVuaXZlcnNpdGV0In0.tidxKWZu6Nz9z4GgSAPRO85dHjaWpIvCxX9bM0lxGSPIDw4SSRgdbLeSGfvck6nG6YqChG_Flr32iQq-QMIGNkzchtxkkWg3uupmtyYhJAQINWwEo5Pjh1LkO5Gh3cMN5LhMoT_qXFZs2B7DsEA6V2RQpMPfKTflm8p47wl9BKU";
	
	@Override
	public IEnvProductInfo GetEPDDataByType(String name) {
		// TODO Auto-generated method stub
		
		
		EpdMetaDataJsonObject epdObject = getEpdDatabaseObject(name);
		return null;
	}
	
	private EpdMetaDataJsonObject getEpdDatabaseObject(String name) {
        EpdMetaDataJsonObject epdDataJson = null;
		
		try {
            URL urlObject = new URL("https://data.eco-platform.org/resource/processes?search=true&format=JSON&distributed=true&virtual=true&metaDataOnly=false&lang=en&name=" + name);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + bearerToken);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String sJson = getJsonStringFromStream(connection);

                EpdListJsonObject epdList = gson.fromJson(sJson, EpdListJsonObject.class);
                
                if (epdList.getEpdList().size() > 0) {
                    epdDataJson = epdList.getEpdList().get(0);
                }
            	
            }
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return epdDataJson;
		
	}

	private EpdMetaDataJsonObject fetchEDPData(String url) {
        EpdMetaDataJsonObject epdDataJson = null;

        try {
            URL urlObject = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + bearerToken);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String sJson = getJsonStringFromStream(connection);

                EpdListJsonObject epdList = gson.fromJson(sJson, EpdListJsonObject.class);
                
                if (epdList.getEpdList().size() > 0) {
                    epdDataJson = epdList.getEpdList().get(0);
                }

                //System.out.println(response.toString());

            } else {
                System.out.println("Failed to fetch data. Response Code: " + responseCode);
            }
        } catch (IOException  e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        
        return epdDataJson;
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

}
