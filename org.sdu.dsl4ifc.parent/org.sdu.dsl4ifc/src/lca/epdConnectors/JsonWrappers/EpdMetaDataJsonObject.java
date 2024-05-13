package lca.epdConnectors.JsonWrappers;

public class EpdMetaDataJsonObject {
	private String name;
	private String uuid;
	private String uri;
	
	public EpdMetaDataJsonObject(String name, String uuid, String uri) {
		this.setName(name);
		this.setUuid(uuid);
		this.setUri(uri);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
