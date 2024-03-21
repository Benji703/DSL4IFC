public class LCAIFCElement {
    private IFCType type;
    private double quantity;
    private double lcaVal;

    public LCAIFCElement(IFCType type, double quantity) {
        this.type = type;
        this.quantity = quantity;
    }


    public double getLcaVal() {
        return lcaVal;
    }

    public void setLcaVal(double lcaVal) {
        this.lcaVal = lcaVal;
    }
}
