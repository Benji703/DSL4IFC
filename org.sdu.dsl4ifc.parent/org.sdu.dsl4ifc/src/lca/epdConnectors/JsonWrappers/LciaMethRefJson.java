package lca.epdConnectors.JsonWrappers;

public class LciaMethRefJson {
    private String type;
    private String refObjectId;
    private String version;
    
    public LciaMethRefJson(String type, String refObjectId, String version) {
    	this.type = type;
    	this.refObjectId = refObjectId;
    	this.version = version;
	}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRefObjectId() {
        return refObjectId;
    }

    public void setRefObjectId(String refObjectId) {
        this.refObjectId = refObjectId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
