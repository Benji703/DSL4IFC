package DomainClasses;

public class EnvProductInfo {
    private double name;
    private double a;
    private double c3;
    private double c4;

    public EnvProductInfo(double name, double a, double c3, double c4) {
        this.name = name;
        this.a = a;
        this.c3 = c3;
        this.c4 = c4;
    }


    public double getC4() {
        return c4;
    }

    public double getC3() {
        return c3;
    }

    public double getA() {
        return a;
    }

    public double getName() {
        return name;
    }
}
