import java.io.File;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LCA {
    private LCACalc lcaCalc = new LCACalc();


    public double CalculateLCAForWall(double quantity, double aRef) {

        /*
        for (int i = 0; i < 5; i++) {

        }
        */

        double a = 53.1 * quantity;
        double c3 = 0.965 * quantity;
        double c4 = 0.7 * quantity;

        return lcaCalc.CalculateLCABasic(a,c3,c4,aRef);
    }

    public double CalculateLCAForElement(LCAIFCElement element, double area) {
        JSONParser jsonParser = new JSONParser();
        double aRef = 0;
        double c3Ref = 0;
        double c4Ref= 0;

        try (FileReader reader = new FileReader("src/JSONData/ProductData.json"))
        {
            Object obj = jsonParser.parse(reader);

            JSONObject products = (JSONObject) obj;
            JSONObject product = (JSONObject) products.get(element.getType().toString());

            aRef = Double.parseDouble((String)product.get("a"));
            c3Ref = Double.parseDouble((String)product.get("c3"));
            c4Ref = Double.parseDouble((String)product.get("c4"));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        double a = aRef * element.getQuantity();
        double c3 = c3Ref * element.getQuantity();
        double c4 = c4Ref * element.getQuantity();

        return lcaCalc.CalculateLCABasic(a, c3, c4, area);
    }

    public ArrayList<LCAIFCElement> CalculateLCAWhole(ArrayList<LCAIFCElement> ifcElements, double area) {

        for (LCAIFCElement element : ifcElements) {
            element.setLcaVal(CalculateLCAForElement(element, area));
        }

        return ifcElements;
    }
}
