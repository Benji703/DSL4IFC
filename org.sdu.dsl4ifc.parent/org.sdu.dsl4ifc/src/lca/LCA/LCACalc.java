package lca.LCA;

public class LCACalc {


    public double CalculateLCABasic(Double a123, Double c3, Double c4) {
    	var a123Val = (a123 == null) ? 0 : a123;
    	var c3Val = (c3 == null) ? 0 : c3;
    	var c4Val = (c4 == null) ? 0 : c4;
    	
        return (a123Val + c3Val + c4Val);
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
