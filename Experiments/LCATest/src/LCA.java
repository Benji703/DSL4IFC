import java.util.ArrayList;
import java.util.List;

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

    public double CalculateLCAForElement(LCAIFCElement element) {

        return 0.0;
    }

    public ArrayList<LCAIFCElement> CalculateLCAWhole(ArrayList<LCAIFCElement> ifcElements) {

        for (LCAIFCElement element : ifcElements) {
            element.setLcaVal(CalculateLCAForElement(element));
        }

        return ifcElements;
    }
}
