package EPDConnectors;

import DomainClasses.EnvProductInfo;
import DomainClasses.IFCType;
import Interfaces.IEPDConnector;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class EcoPortalConnector implements IEPDConnector {

    private final String epdUrl = "";

    @Override
    public EnvProductInfo GetEPDDataByType(IFCType type) {
        double aRef = 0;
        double c3Ref = 0;
        double c4Ref = 0;


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
        }

        return new EnvProductInfo(type.toString(), aRef, c3Ref, c4Ref);
    }





}
