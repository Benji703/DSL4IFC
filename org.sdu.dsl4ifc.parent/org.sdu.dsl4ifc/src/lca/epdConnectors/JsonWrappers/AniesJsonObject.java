package lca.epdConnectors.JsonWrappers;

public class AniesJsonObject {
    private String value;
    private String module;
    
    public AniesJsonObject(String value, String module) {
		this.value = value;
		this.module = module;
	}

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}
