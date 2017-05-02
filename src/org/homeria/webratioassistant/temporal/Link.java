package org.homeria.webratioassistant.temporal;

public class Link {
	private String type;
	private String sourceId;
	private String destinyId;

	public Link(String sourceId, String destinyId, String type) {
		this.sourceId = sourceId;
		this.destinyId = destinyId;
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	public String getIdSource() {
		return this.sourceId;
	}

	public String getIdDestiny() {
		return this.destinyId;
	}

}
