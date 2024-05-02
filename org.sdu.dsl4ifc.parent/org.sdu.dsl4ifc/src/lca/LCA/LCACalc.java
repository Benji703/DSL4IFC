package lca.LCA;

public class LCACalc {


    public double CalculateLCABasic(double a123, double c3, double c4) {
        return (a123 + c3 + c4);
    }

    public double CalculateLCAOperational(double b6, double aHeat) {
        return (b6 / (aHeat * 50));
    }

    public double CalculateLCAModuleDBasic(double dMat, double aRef) {
        return (dMat /(aRef * 50));
    }

    public double CalculateLCAModuleDOperational(double dOp, double aHeat) {
        return (dOp /(aHeat * 50));
    }
    
    public double CalculateBuildingLca(double lca, double aRef) {
    	return lca / (aRef*50);
    }
}
