package org.homeria.webratioassistant.temporal;

import org.eclipse.swt.widgets.Table;

public class ContentUnit {
	private String id;
	private String name;
	private String parentId;
	private String type;
	private String attributes;
	private Table table;

	public ContentUnit(String id, String name, String parentId, String type) {
		this.id = id;
		this.name = name;
		this.parentId = parentId;
		this.type = type;
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getParentId() {
		return this.parentId;
	}

	public String getType() {
		return this.type;
	}

	public String getAttributes() {
		// TODO: obtener los atributos de table
		return this.attributes;
	}

	public void setTable(Table table) {
		this.table = table;
	}
}
