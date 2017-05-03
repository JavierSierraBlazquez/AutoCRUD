package org.homeria.webratioassistant.temporal;

public class Link {
	private String name;
	private String type;
	private String sourceId;
	private String destinyId;

	public Link(String name, String type, String sourceId, String destinyId) {
		this.name = name;
		this.type = type;
		this.sourceId = sourceId;
		this.destinyId = destinyId;
	}

	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}

	public String getSourceId() {
		return this.sourceId;
	}

	public String getDestinyId() {
		return this.destinyId;
	}

}
