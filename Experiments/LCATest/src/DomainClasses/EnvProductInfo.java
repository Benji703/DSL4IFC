package DomainClasses;

import Interfaces.IEnvProductInfo;

public class EnvProductInfo implements IEnvProductInfo {
    private String name;
    private double a;
    private double c3;
    private double c4;

    public EnvProductInfo(String name, double a, double c3, double c4) {
        this.name = name;
        this.a = a;
        this.c3 = c3;
        this.c4 = c4;
    }


    public Double getC4() {
        return c4;
    }

    public Double getC3() {
        return c3;
    }

    public Double getA() {
        return a;
    }

    public String getName() {
        return name;
    }
}
