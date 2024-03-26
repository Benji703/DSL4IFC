package LCA;

import java.util.ArrayList;

public class LCAResult {
    private double lcaResult;
    private ArrayList<LCAIFCElement> elements;

    public LCAResult(double lcaResult) {
        this.lcaResult = lcaResult;
        elements = new ArrayList<>();
    }

    public LCAResult(double lcaResult, ArrayList<LCAIFCElement> elements) {
        this.lcaResult = lcaResult;
        this.elements = elements;
    }

    public ArrayList<LCAIFCElement> getElements() {
        return elements;
    }

    public void setElements(ArrayList<LCAIFCElement> elements) {
        this.elements = elements;
    }

    public double getLcaResult() {
        return lcaResult;
    }

    public void setLcaResult(double lcaResult) {
        this.lcaResult = lcaResult;
    }
}
