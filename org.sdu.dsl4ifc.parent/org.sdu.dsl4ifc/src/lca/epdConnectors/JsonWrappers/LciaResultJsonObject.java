package lca.epdConnectors.JsonWrappers;

public class LciaResultJsonObject {
    private LciaMethRefJsonObject referenceToLCIAMethodDataSet;
    private OtherJsonObject other;

    public LciaMethRefJsonObject getReferenceToLCIAMethodDataSet() {
        return referenceToLCIAMethodDataSet;
    }

    public void setReferenceToLCIAMethodDataSet(LciaMethRefJsonObject referenceToLCIAMethodDataSet) {
        this.referenceToLCIAMethodDataSet = referenceToLCIAMethodDataSet;
    }

    public OtherJsonObject getOther() {
        return other;
    }

    public void setOther(OtherJsonObject other) {
        this.other = other;
    }
}
