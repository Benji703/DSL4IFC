import com.apstex.ifc2x3toolbox.ifcmodel.IfcModel;

import java.io.File;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {

        LCA lca = new LCA();

        double result = lca.CalculateLCAForWall(500,500);

        System.out.println("LCA for wall = " + result + " kg CO2-equivalents/m2/year");
    }
}