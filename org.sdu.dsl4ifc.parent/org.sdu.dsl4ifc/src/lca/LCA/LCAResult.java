package lca.LCA;

import java.util.ArrayList;
import java.util.List;

public class LCAResult {
    private double lcaResult;
    private List<LCAIFCElement> elements;

    public LCAResult(double lcaResult) {
        this.lcaResult = lcaResult;
        elements = new ArrayList<>();
    }

    public LCAResult(double lcaResult, List<LCAIFCElement> elements) {
        this.lcaResult = lcaResult;
        this.elements = elements;
    }

    public List<LCAIFCElement> getElements() {
        return elements;
    }

    public void setElements(List<LCAIFCElement> elements) {
        this.elements = elements;
    }

    public double getLcaResult() {
        return lcaResult;
    }

    public void setLcaResult(double lcaResult) {
        this.lcaResult = lcaResult;
    }
}
