package LCA;

import DomainClasses.Enums.IFCTypeEnum;

public class LCAIFCElement {
    private String type;
    private double quantity;
    private double lcaVal;

    public LCAIFCElement(String type, double quantity) {
        this.type = type;
        this.quantity = quantity;
    }


    public double getLcaVal() {
        return lcaVal;
    }

    public void setLcaVal(double lcaVal) {
        this.lcaVal = lcaVal;
    }

    public String getType() {
        return type;
    }

    public double getQuantity() {
        return quantity;
    }
}
