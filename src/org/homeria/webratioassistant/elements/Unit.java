package org.homeria.webratioassistant.elements;

import com.webratio.ide.model.IEntity;

public abstract class Unit extends WebRatioElement {
	protected IEntity entity;

	public Unit(String id, String name, String x, String y, IEntity entity) {
		super(id, name, x, y);
		this.entity = entity;
	}
}
