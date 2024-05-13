package lca.epdConnectors.JsonWrappers;

public class LciaResult {
    private LciaMethRefJson referenceToLCIAMethodDataSet;
    private OtherJsonObject other;

    public void setReferenceToLCIAMethodDataSet(LciaMethRefJson referenceToLCIAMethodDataSet) {
        this.referenceToLCIAMethodDataSet = referenceToLCIAMethodDataSet;
    }
    
    public LciaMethRefJson getReferenceToLCIAMethodDataSet() {
        return referenceToLCIAMethodDataSet;
    }

    public OtherJsonObject getOther() {
        return other;
    }

    public void setOther(OtherJsonObject other) {
        this.other = other;
    }
}
