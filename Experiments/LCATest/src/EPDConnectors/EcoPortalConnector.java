package EPDConnectors;

import DomainClasses.EnvProductInfo;
import DomainClasses.Enums.IFCTypeEnum;
import Interfaces.IEPDConnector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EcoPortalConnector implements IEPDConnector {

    private final String epdUrl = "";

    @Override
    public EnvProductInfo GetEPDDataByType(IFCTypeEnum type) {
        double aRef = 0;
        double c3Ref = 0;
        double c4Ref = 0;

        /*
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("src/JSONData/ProductData.json"))
        {
            Object obj = jsonParser.parse(reader);

            JSONObject products = (JSONObject) obj;
            JSONObject product = (JSONObject) products.get(type.toString());

            aRef = Double.parseDouble((String)product.get("a"));
            c3Ref = Double.parseDouble((String)product.get("c3"));
            c4Ref = Double.parseDouble((String)product.get("c4"));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }*/

        JSONObject edpData = FetchEDPData("https://epdnorway.lca-data.com/resource/processes/58476c51-01e2-4866-bd27-d665341f09fe?format=json");

        JSONArray moduleArray = (JSONArray) ( (JSONObject) ( (JSONObject) ((JSONArray) ((JSONObject) edpData.get("exchanges")).get("exchange")).get(1)).get("other")).get("anies");
        System.out.println(moduleArray.toString());

        return new EnvProductInfo(type.toString(), aRef, c3Ref, c4Ref);
    }

    private JSONObject FetchEDPData(String url) {
        String bearerToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJCZW5qaTcwMyIsImlzcyI6IkVDT1BPUlRBTCIsImF1ZCI6ImFueSIsInZlciI6IjcuOS40IiwicGVybWlzc2lvbnMiOlsic3RvY2s6cmVhZCxleHBvcnQ6MiIsInN0b2NrOnJlYWQsZXhwb3J0OjEiLCJ1c2VyOnJlYWQsd3JpdGU6NjcxIl0sInJvbGVzIjpbXSwiaWF0IjoxNzExMTAwMjE5LCJleHAiOjE3MTg5ODQyMTksImVtYWlsIjoiYmVhbmQxOUBzdHVkZW50LnNkdS5kayIsInRpdGxlIjoiIiwiZmlyc3ROYW1lIjoiQmVuamFtaW4iLCJsYXN0TmFtZSI6IkFuZGVyc2VuIiwiZ2VuZXJhdGVOZXdUb2tlbnMiOmZhbHNlLCJqb2JQb3NpdGlvbiI6IkthbmRpZGF0c3R1ZGVyZW5kZSIsImFkZHJlc3MiOnsiY2l0eSI6Ik9kZW5zZSIsInppcENvZGUiOiI1MjQwIiwiY291bnRyeSI6IkRLIiwic3RyZWV0IjoiIn0sIm9yZ2FuaXphdGlvbiI6e30sInVzZXJHcm91cHMiOlt7InVzZXJHcm91cE5hbWUiOiJyZWdpc3RlcmVkX3VzZXJzIiwidXNlckdyb3VwT3JnYW5pemF0aW9uTmFtZSI6IkRlZmF1bHQgT3JnYW5pemF0aW9uIn1dLCJhZG1pbmlzdHJhdGVkT3JnYW5pemF0aW9uc05hbWVzIjoiIiwicGhvbmUiOiIiLCJkc3B1cnBvc2UiOiJUaWwgZXQga2FuZGlkYXRwcm9qZWt0IHZlZHLDuHJlbmRlIGVuIElULXBsYXRmb3JtIHRpbCB1ZHJlZ25pbmcgYWYgYmxhLiBMQ0EiLCJzZWN0b3IiOiIiLCJpbnN0aXR1dGlvbiI6IlN5ZGRhbnNrIFVuaXZlcnNpdGV0In0.tidxKWZu6Nz9z4GgSAPRO85dHjaWpIvCxX9bM0lxGSPIDw4SSRgdbLeSGfvck6nG6YqChG_Flr32iQq-QMIGNkzchtxkkWg3uupmtyYhJAQINWwEo5Pjh1LkO5Gh3cMN5LhMoT_qXFZs2B7DsEA6V2RQpMPfKTflm8p47wl9BKU";

        JSONParser jsonParser = new JSONParser();
        JSONObject epdDataJson = new JSONObject();

        try {
            URL urlObject = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + bearerToken);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Object obj = jsonParser.parse(response.toString());

                epdDataJson = (JSONObject) obj;

                //System.out.println(response.toString());

            } else {
                System.out.println("Failed to fetch data. Response Code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return epdDataJson;
    }
}
