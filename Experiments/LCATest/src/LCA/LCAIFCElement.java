package LCA;

public class LCAIFCElement {
    private String name;
    private double quantity;
    private Double aResult;
    private Double c3Result;
    private Double c4Result;
    private double lcaVal;


    public LCAIFCElement(String type, double quantity) {
        this.name = type;
        this.quantity = quantity;
    }

    public Double getaResult() {
        return aResult;
    }

    public void setaResult(Double aResult) {
        this.aResult = aResult;
    }

    public Double getC3Result() {
        return c3Result;
    }

    public void setC3Result(Double c3Result) {
        this.c3Result = c3Result;
    }

    public Double getC4Result() {
        return c4Result;
    }

    public void setC4Result(Double c4Result) {
        this.c4Result = c4Result;
    }

    public double getLcaVal() {
        return lcaVal;
    }

    public void setLcaVal(double lcaVal) {
        this.lcaVal = lcaVal;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }
}
