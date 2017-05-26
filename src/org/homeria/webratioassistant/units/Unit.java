package org.homeria.webratioassistant.units;

import org.eclipse.swt.graphics.Point;

import com.webratio.ide.model.IEntity;

public abstract class Unit extends WebRatioElement {
	protected IEntity entity;
	protected Point position;

	public Unit(String id, String name, String x, String y, IEntity entity) {
		super(id, name);
		this.entity = entity;
		this.position = new Point(Integer.valueOf(x), Integer.valueOf(y));
	}
}