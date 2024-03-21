public class LCACalc {


    public double CalculateLCAFull(double a123, double b4, double b6, double c3, double c4, double dMat, double dOp, double aRef, double aHeat) {
        double lcaBasic = CalculateLCABasic2(a123, b4, b6, c3, c4, aRef, aHeat);
        double lcaDmodule = CalculateLCADModule(dMat, dOp, aRef, aHeat);
        double co2Eq = lcaBasic + lcaDmodule;
        return  co2Eq;
    }

    public double CalculateLCADModule(double dMat, double dOp, double aRef, double aHeat) {
        return (dMat / (aRef * 50)) + (dOp / (aHeat * 50));
    }

    public double CalculateLCABasic2(double a123, double b4, double b6, double c3, double c4, double aRef, double aHeat) {
        return (a123 + b4 + c3 + c4) / (aRef*50) + (b6 / (aHeat*50));
    }

    public double CalculateLCABasic(double a123, double c3, double c4, double aRef) {
        return (a123 + c3 + c4) / (aRef*50);
    }

    public double CalculateLCAOperational(double b6, double aHeat) {
        return (b6 / (aHeat * 50));
    }


}
