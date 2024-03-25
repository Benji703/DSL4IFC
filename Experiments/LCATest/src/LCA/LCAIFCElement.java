package LCA;

public class LCAIFCElement {
    private String name;
    private double quantity;
    private double lcaVal;

    public LCAIFCElement(String type, double quantity) {
        this.name = type;
        this.quantity = quantity;
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
