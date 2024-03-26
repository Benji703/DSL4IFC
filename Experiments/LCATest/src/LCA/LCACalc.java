package LCA;

public class LCACalc {


    public double CalculateLCABasic(double a123, double c3, double c4, double aRef) {
        return (a123 + c3 + c4) / (aRef*50);
    }

    public double CalculateLCAOperational(double b6, double aHeat) {
        return (b6 / (aHeat * 50));
    }


}
